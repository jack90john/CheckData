package com.qy.checkData;

import org.apache.poi.hssf.usermodel.*;

import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by John on 2016/7/7.
 */

class ExportExcel {
    static void export(List<CheckedData> list){
        // 第一步，创建一个workbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("对码表");
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 25000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 25000);
        // 第三步，在sheet中添加表头第0行
        HSSFRow row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("当地编码");
        cell.setCellStyle(style);
        cell = row.createCell(1);
        cell.setCellValue("当地名称");
        cell.setCellStyle(style);
        cell = row.createCell(2);
        cell.setCellValue("标准编码");
        cell.setCellStyle(style);
        cell = row.createCell(3);
        cell.setCellValue("标准名称");
        cell.setCellStyle(style);


        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow(i + 1);
            CheckedData checkData = list.get(i);
            // 第四步，创建单元格，并设置值
            row.createCell(0).setCellValue(checkData.getLocalCode());
            row.createCell(1).setCellValue(checkData.getLocalName());
            row.createCell(2).setCellValue(checkData.getStandardCode());
            row.createCell(3).setCellValue(checkData.getStandardName());
        }
        // 第六步，将文件存到指定位置
        try {
            FileOutputStream exportFile = new FileOutputStream("C:/Users/John/Desktop/checkData.xls");
            wb.write(exportFile);
            exportFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
