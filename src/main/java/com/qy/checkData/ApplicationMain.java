package com.qy.checkData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2016/7/7.
 */
public class ApplicationMain {


    static ResultSet ret = null;

    public static void main(String[] args) {
        String sql = "select * from jiangsu";//SQL语句
        List<ReturnData> localList = query(sql);

        String  baseSql = "select * from service";
        List<ReturnData> baseList = query(baseSql);

        List<CheckedData> checkedList = new ArrayList<>();

        for (int i = 0; i < localList.size(); i++){
            String localName = localList.get(i).getName();
            for(int j = 0; j<baseList.size(); j++){
                if(localName != null && localName.equals(baseList.get(j).getName())){
                    CheckedData checkedData = new CheckedData();
                    checkedData.setLocalCode(localList.get(i).getCode());
                    checkedData.setLocalName(localName);
                    checkedData.setStandardCode(baseList.get(j).getCode());
                    checkedData.setStandardName(baseList.get(j).getName());
                    checkedList.add(checkedData);
                    break;
                }
            }
        }

        ExportExcel.export(checkedList);
    }

    private static List<ReturnData> query(String sql){
        DBHelper dbl = new DBHelper(sql);//创建DBHelper对象
        List<ReturnData> returnList = new ArrayList<>();
        try {
            ret = dbl.pst.executeQuery();//执行语句，得到结果集
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
}
