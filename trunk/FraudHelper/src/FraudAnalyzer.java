import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;


import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;


import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import os_utils.excel_tools.ExcelUtil;

public class FraudAnalyzer implements Runnable {
	
	private SettingsData options;

	public FraudAnalyzer(SettingsData opts) {
		// TODO Auto-generated constructor stub
		this.options = opts;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		HSSFWorkbook wb = null;
		HSSFWorkbook outWb = new HSSFWorkbook();
		try {
			wb = new HSSFWorkbook(new FileInputStream(options.getInputFile()));		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(wb != null) {
			HSSFSheet newSheet = outWb.createSheet("copy");
			// copy sheet from original file
			ExcelUtil.copySheets(newSheet, wb.getSheetAt(0));
			groupRows(newSheet);
			List<Integer> errorRows = processData(newSheet);
			createReport(outWb, errorRows);
			
						
		}
		
		// write file
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream("workbook.xls");
			outWb.write(fileOut);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {			 
		    try {
				fileOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}   
		
	}

	private void createReport(HSSFWorkbook outWb, List<Integer> errorRows) {
		// TODO Auto-generated method stub
		/*
		 * BUDEM DELATJ report
		 */
		System.out.println("Report");
		
	}

	private void groupRows(HSSFSheet newSheet) {
		// TODO Auto-generated method stub
		int startRowPos = 0, endRowPos = 0;
		String stationGroup = null, 
		viiteNum = null, 
		client = null, 
		service = null;
		
		for(Row row: newSheet) {
			if(row.getRowNum() == 0)
				continue;
			if(!row.getCell(0, Row.CREATE_NULL_AS_BLANK).
					getStringCellValue().isEmpty()) {
				stationGroup = row.getCell(0, Row.CREATE_NULL_AS_BLANK)
				.getStringCellValue();
				viiteNum = row.getCell(1, Row.CREATE_NULL_AS_BLANK)
				.getStringCellValue();
				client = row.getCell(2, Row.CREATE_NULL_AS_BLANK)
				.getStringCellValue();
				service = row.getCell(3, Row.CREATE_NULL_AS_BLANK)
				.getStringCellValue();					
			} else {
				row.getCell(0, Row.CREATE_NULL_AS_BLANK)
				.setCellValue(stationGroup);
				row.getCell(1, Row.CREATE_NULL_AS_BLANK)
				.setCellValue(viiteNum);
				row.getCell(2, Row.CREATE_NULL_AS_BLANK)
				.setCellValue(client);
				row.getCell(3, Row.CREATE_NULL_AS_BLANK)
				.setCellValue(service);
				endRowPos++;
				
			}
		}
	}

	private List<Integer> processData(Sheet newSheet) {
		// TODO Auto-generated method stub
		// get data start index
		List<Integer> errorRows = new ArrayList<Integer>();
		int dataStartColNum = ExcelUtil.getColNumStartsWith(
				newSheet.getRow(0),
				"Total");
		if(dataStartColNum == -1)
			try {
				throw new InvalidPropertiesFormatException("Total not found");
			} catch (InvalidPropertiesFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		for(Row row: newSheet) {
			if(row.getRowNum() == 0)
				continue;
			double[] values = ExcelUtil.getDoubleValuesFromRow(
					row,
					dataStartColNum,
					(int) row.getLastCellNum());
			DescriptiveStatistics stats = new DescriptiveStatistics(values);
			double newVal  = values[values.length - 1];
			double max = stats.getMax();
			// if fraud found
			if( newVal > (max * 1.1)  && (newVal > (max + 100.0)) ) {
				Cell cellFraud = row.createCell(row.getLastCellNum());
				cellFraud.setCellValue("FRAUD");
				CellStyle style = newSheet.getWorkbook().createCellStyle();
			    style.setFillForegroundColor(IndexedColors.RED.getIndex());
			    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cellFraud.setCellStyle(style);
				errorRows.add(row.getRowNum());
			} else {
				Cell cellFraud = row.createCell(row.getLastCellNum());
				cellFraud.setCellValue("OK");
				Workbook currWb = newSheet.getWorkbook();
				CellStyle style = currWb.createCellStyle();
			    style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
			    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			    Font font = currWb.createFont();
			    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			    style.setFont(font);
				cellFraud.setCellStyle(style);
				
			}
			

		}
		return errorRows;
	}

	

}
