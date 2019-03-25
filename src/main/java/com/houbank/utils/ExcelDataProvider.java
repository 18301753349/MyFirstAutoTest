package com.houbank.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.function.Consumer;

@Log4j2
public class ExcelDataProvider implements Iterator<Object[]> {

	private Workbook book = null;
	private Sheet sheet = null;
	private int rowCount = 0;
	private int curRowNo = 0;
	private int columnNum = 0;
	private String[] columnnName;
	private List<Object> datalist = new ArrayList<Object>();
	private Object[] data = null;

	public ExcelDataProvider(String excelFile, String sheetName,
							 String groupName) {
		try {
			log.info("File Path:" + excelFile);
			log.info("sheetname:" + sheetName + "  groupName:" + groupName);
			//
			File excelDir = new File(excelFile);
			FileInputStream is = new FileInputStream(excelDir);
			this.book = WorkbookFactory.create(is);

			this.sheet = book.getSheet(sheetName);
			if (this.sheet != null) {
				Row c = sheet.getRow(0);
				this.columnNum = c.getPhysicalNumberOfCells();
				columnnName = new String[c.getPhysicalNumberOfCells()];
				for (int i = 0; i < c.getPhysicalNumberOfCells(); i++) {
					columnnName[i] = c.getCell(i).getStringCellValue();
				}
				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					//
					Row tmpRow = sheet.getRow(i);
					Map<String, String> rowData = new HashMap<String, String>();
					rowData = GetRowData(tmpRow);
					if (groupName.trim().length() > 0) {
						if (rowData.containsKey("GroupName")
								&& rowData.get("GroupName").equals(groupName)) {
							datalist.add(rowData);
							this.rowCount++;

						}
					} else {
						datalist.add(rowData);
						this.rowCount++;

					}

				}
				data = datalist.toArray();
			} else {
				log.info("sheetName:\"" + sheetName);
			}

		} catch (Exception e) {
			log.error("解析excel失败", e);
		}

	}

	private Map<String, String> GetRowData(Row tmpRow) {
		Map<String, String> rowData = new HashMap<String, String>();
		DataFormatter formatter = new DataFormatter();

		for (int j = 0; j < this.columnNum; j++) {
			String temp = "";
			try {
				Cell cell = tmpRow.getCell(j);
				if (cell != null) {
					//判断单元格数据的类型，不同类型调用不同的方法
					switch (cell.getCellTypeEnum()) {
						//数值类型
						case NUMERIC:
							//进一步判断 ，单元格格式是日期格式
							if (DateUtil.isCellDateFormatted(cell)) {
								temp = formatter.formatCellValue(cell);
							} else {
								//数值
								double value = cell.getNumericCellValue();
								int intValue = (int) value;
								temp = value - intValue == 0 ? String.valueOf(intValue) : String.valueOf(value);
							}
							break;
						case STRING:
							temp = cell.getStringCellValue();
							break;
						case BOOLEAN:
							temp = String.valueOf(cell.getBooleanCellValue());
							break;
						//判断单元格是公式格式，需要做一种特殊处理来得到相应的值
						case FORMULA: {
							try {
								temp = String.valueOf(cell.getNumericCellValue());
							} catch (IllegalStateException e) {
								temp = String.valueOf(cell.getRichStringCellValue());
							}

						}
						break;
						case BLANK:
							temp = "";
							break;
						case ERROR:
							temp = "";
							break;
						default:
							temp = cell.toString().trim();
							break;
					}
				} else {
					temp = "null";
				}
			} catch (ArrayIndexOutOfBoundsException ex) {
				temp = "";
			}
			rowData.put(this.columnnName[j], temp);
		}

		return rowData;
	}

	public boolean hasNext() {
		// TODO Auto-generated method stub
		if (this.rowCount == 0 || this.curRowNo >= this.rowCount) {
			return false;
		} else
			return true;
	}

	public Object[] next() {
		log.debug("curRowNo:{};rowCount:{}", curRowNo, rowCount);
		Object r[] = new Object[1];
		r[0] = data[curRowNo];

		this.curRowNo++;
		return r;
	}

	public void remove() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("remove unsupported.");
	}

	@Override
	public void forEachRemaining(Consumer<? super Object[]> action) {
		// TODO Auto-generated method stub

	}

}