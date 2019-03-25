package com.houbank.utils;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Poi {
    private static Sheet sheet;    //表格类实例
    static Object[][] result;
    private static String filePath = "D:/workspace/hb-autotest/src/main/resources/LoginTest.xlsx";//其中path为文件存放路径

    //读取excel文件，创建表格实例
    public static Object[][] loadExcel() {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(new File(filePath));
            Workbook workBook = WorkbookFactory.create(inStream);

            sheet = workBook.getSheetAt(0);
            result = init();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //获取单元格的值
    private static String getCellValue(Cell cell) {
        String cellValue = "";
        DataFormatter formatter = new DataFormatter();
        if (cell != null) {
            //判断单元格数据的类型，不同类型调用不同的方法
            switch (cell.getCellTypeEnum()) {
                //数值类型
                case NUMERIC:
                    //进一步判断 ，单元格格式是日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = formatter.formatCellValue(cell);
                    } else {
                        //数值
                        double value = cell.getNumericCellValue();
                        int intValue = (int) value;
                        cellValue = value - intValue == 0 ? String.valueOf(intValue) : String.valueOf(value);
                    }
                    break;
                case STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                //判断单元格是公式格式，需要做一种特殊处理来得到相应的值
                case FORMULA: {
                    try {
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException e) {
                        cellValue = String.valueOf(cell.getRichStringCellValue());
                    }

                }
                break;
                case BLANK:
                    cellValue = "";
                    break;
                case ERROR:
                    cellValue = "";
                    break;
                default:
                    cellValue = cell.toString().trim();
                    break;
            }
        }
        return cellValue.trim();
    }

    private static Object[][] init() {
        int rowCount = 0;
        if (sheet != null)
            rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();//Eecel文件的行号和列号都是从0开始的，相减得出列数
        List<Object[]> records = new ArrayList<Object[]>();
        for (int i = 0; i < rowCount + 1; i++) {
            Row row = null;
            if (sheet != null) {
                row = sheet.getRow(i+1);//使用getRow方法获取行对象，跳过列头
            }
            if (row == null) {
                continue;   //略过空行
            }
            String fields[] = new String[row.getLastCellNum()]; //声明一个数组，存储Excel文件每行的N个数据
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                //获取单元格的值
                fields[j] = getCellValue(cell);
            }
            //将得到的值放入链表中
            records.add(fields) ;
        }
        result = new Object[records.size()][]; //定义函数返回值，即Object[][]
        for (int i=0; i<records.size();i++) {                //将存储测试数据的List转换为一个人Object的二维数组
            result[i] = records.get(i);                    //设置二维数组每行的值，每行是一个Object对象
        }
        return result;
    }

}
