package com.qy.checkData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2016/7/7.
 *
 * @version 1.0
 */
public class ApplicationMain {

    private static List<CheckedData> checkedList = new ArrayList<>();
    private static List<CheckedData> uncheckedList = new ArrayList<>();
    private static List<CheckedData> needAddList = new ArrayList<>();

    public static void main(String[] args) {
        String sql = "select * from henan";//SQL语句
        List<ReturnData> localList = query(sql);

        String baseSql = "select * from service";
        List<ReturnData> baseList = query(baseSql);

        ApplicationMain.compareWithName(localList, baseList);

        ExportExcel.export(checkedList, 1);
        ExportExcel.export(uncheckedList, 2);
        ExportExcel.export(needAddList, 3);
    }

    private static List<ReturnData> query(String sql) {
        DBHelper dbl = new DBHelper(sql);//创建DBHelper对象
        List<ReturnData> returnList = new ArrayList<>();
        try {
            ResultSet ret = dbl.pst.executeQuery();
            while (ret.next()) {
                ReturnData returnData = new ReturnData();
                returnData.setCode(ret.getString(1));
                returnData.setName(ret.getString(2));
                returnList.add(returnData);
            }//显示数据
            ret.close();
            dbl.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnList;
    }

    private static void addToList(ReturnData aLocalList, ReturnData aBaseList, List<CheckedData> list) {
        CheckedData checkedData = new CheckedData();
        checkedData.setLocalCode(aLocalList.getCode());
        checkedData.setLocalName(aLocalList.getName());
        if (aBaseList != null){
            checkedData.setStandardCode(aBaseList.getCode());
            checkedData.setStandardName(aBaseList.getName());
        }
        list.add(checkedData);
    }

    private static void compareWithName(List<ReturnData> localList, List<ReturnData> baseList) {
        List<ReturnData> waitForCodeList = new ArrayList<>();
        //遍历每一个本地名称
        for (ReturnData aLocalList : localList) {
            String localName = aLocalList.getName();
            String localCode = aLocalList.getCode();
            boolean isPass = false;
            //对每一个本地名称与标准名称做对比。
            for (ReturnData aBaseList : baseList) {
                isPass = false;
                String baseName = aBaseList.getName();
                String baseCode = aBaseList.getCode();
                //先按名称匹配
                if (localName != null && localName.equals(baseName)) {
                    //名称匹配上之后匹配服务代码
                    if (localCode != null) {
//                        服务代码大于6位数的匹配前六位
                        if (localCode.length() > 6 && baseCode.length() > 6) {
                            if (localCode.substring(0, 6).equals(baseCode.substring(0, 6))) {
                                addToList(aLocalList, aBaseList, checkedList);
                                isPass = true;
                                break;
                            }
                        } else {
                            //服务代码不满6位的完全全匹配
                            if (localCode.equals(baseCode)) {
                                addToList(aLocalList, aBaseList, checkedList);
                                isPass = true;
                                break;
                            }
                        }
                    }
                    //如果姓名相同，编码前六位未匹配上的加入未通过List输出
                    addToList(aLocalList, aBaseList, uncheckedList);
                    isPass = true;
                }
            }
            if (isPass == false) {
                //名字匹配失败，加入待代码匹配List
                waitForCodeList.add(aLocalList);
            }
        }
        //名字匹配完成，进行代码匹配
        ApplicationMain.compareWithCode(waitForCodeList, baseList);
    }

    private static void compareWithCode(List<ReturnData> waitForCode, List<ReturnData> baseList) {
        //遍历待匹配代码列表
        for (ReturnData aWaitForCode : waitForCode) {
            String localName = aWaitForCode.getName();
            String localCode = aWaitForCode.getCode();
            //对待匹配代码列表中的每一个代码与标准代码做比较
            boolean isPass = false;
            int z = 0;
            boolean flag = false;
            for(int i=0; i<baseList.size()-1; i++){
                String baseName = baseList.get(i).getName();
                String baseCode = baseList.get(i).getCode();
                //如果代码相同则比较名称相似度，如果像是度大于0.75则通过，否则人工干预。
                if (localCode != null && localCode.equals(baseCode)) {
                    z = i; //记录代码相同位置
                    flag = true; //是否有代码匹配标志
                    if (DataCompare.sim(localName, baseName) > 0.75) {
                        addToList(aWaitForCode, baseList.get(i), checkedList);
                        isPass = true;
                        break;
                    }
                }
            }
            if(!isPass && flag){
                //加入未通过匹配列表
                addToList(aWaitForCode, baseList.get(z), uncheckedList);
            }else if(!isPass && !flag){
                addToList(aWaitForCode,null,needAddList);
            }
//            for (ReturnData aBaseList : baseList) {
//                String baseName = aBaseList.getName();
//                String baseCode = aBaseList.getCode();
//                //如果代码相同则比较名称相似度，如果像是度大于0.75则通过，否则人工干预。
//                if (localCode != null && localCode.equals(baseCode)) {
//                    if (DataCompare.sim(localName, baseName) > 0.75) {
//                        addToList(aWaitForCode, aBaseList, checkedList);
//                        isPass = true;
//                        break;
//                    }
//                }
//            }
//            if(!isPass){
//                //加入未通过匹配列表
//                addToList(aWaitForCode, aBaseList, uncheckedList);
//            }
        }
    }
}
