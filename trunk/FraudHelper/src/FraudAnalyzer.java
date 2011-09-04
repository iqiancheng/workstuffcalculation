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
		
		// create new sheet
		Sheet reportSheet = outWb.createSheet("report");
		if(!errorRows.isEmpty()) {
			// create report
			int i = 0;
			for(Integer rowIndex: errorRows) {
				Row newRow = reportSheet.createRow(i);
				Row fromRow = outWb.getSheetAt(0).getRow(rowIndex);
				Sheet fromSheet = outWb.getSheetAt(0);
				ExcelUtil.copyRow(fromSheet, fromRow, reportSheet, newRow);
				i++;
			}
		} else {
			// report OK
			Row firstRow = reportSheet.createRow(0);
			Cell firstCell = firstRow.createCell(0);
			firstCell.setCellValue("OK");
			
			CellStyle style = outWb.createCellStyle();
		    style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		    Font font = outWb.createFont();
		    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		    style.setFont(font);
		    firstCell.setCellStyle(style);
			
			
		}
		
		
	}
	
	/**
	 * Groups the rows in given sheet according to first cell	 * 
	 * @param newSheet  working sheet with rows
	 */
	private void groupRows(HSSFSheet newSheet) {
		// TODO Auto-generated method stub
		int startRowPos = 0, endRowPos = 0;
		/* colums from excel file */
		String stationGroup = null, 
		viiteNum = null, 
		client = null, 
		service = null;
		
		for(Row row: newSheet) {
			/* skip first row with headers */
			if(row.getRowNum() == 0)
				continue;
			/* if the first cell of row is NOT blank */
			if(!row.getCell(0, Row.CREATE_NULL_AS_BLANK).
					getStringCellValue().isEmpty()) {
				/* retrieve data from row */
				stationGroup = row.getCell(0, Row.CREATE_NULL_AS_BLANK)
				.getStringCellValue();
				viiteNum = row.getCell(1, Row.CREATE_NULL_AS_BLANK)
				.getStringCellValue();
				client = row.getCell(2, Row.CREATE_NULL_AS_BLANK)
				.getStringCellValue();
				service = row.getCell(3, Row.CREATE_NULL_AS_BLANK)
				.getStringCellValue();				
			} else {
				/* fill empty row with  retrieved data */ 
				row.getCell(0, Row.CREATE_NULL_AS_BLANK)
				.setCellValue(stationGroup);
				row.getCell(1, Row.CREATE_NULL_AS_BLANK)
				.setCellValue(viiteNum);
				row.getCell(2, Row.CREATE_NULL_AS_BLANK)
				.setCellValue(client);
				row.getCell(3, Row.CREATE_NULL_AS_BLANK)
				.setCellValue(service);
				/* empty line counter */
				endRowPos++;
				
			}
		}
	}
	
	private List<Integer> processData(Sheet newSheet) {
		// TODO Auto-generated method stub
		
		// list of error rows position indexes
		List<Integer> errorRows = new ArrayList<Integer>();
		// get data start column index
		int dataStartColNum = ExcelUtil.getColNumStartsWith(
				newSheet.getRow(0), /* first row */
				"Total"); // string to search
		
		if(dataStartColNum == -1)
			try {
				throw new InvalidPropertiesFormatException("Total not found");
			} catch (InvalidPropertiesFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		for(Row row: newSheet) {
			// skip first row with descriptions 
			if(row.getRowNum() == 0)
				continue;
			// parse values from row 
			double[] values = ExcelUtil.getDoubleValuesFromRow(
					row,
					dataStartColNum,
					(int) row.getLastCellNum());
			// some apache math library magic
			DescriptiveStatistics stats = new DescriptiveStatistics(values);
			// our new value is the last value in row
			double newVal  = values[values.length - 1];
			double max = stats.getMax();

			// search criteria
			int crit1 = this.options.getCritPersent();
			double crit2 = (double) this.options.getMoreThan();

			// if fraud found
			if( newVal > (max * crit1 )  && (newVal > (max + crit2)) ) {
				// add new cell with fraud 
				Cell cellFraud = row.createCell(row.getLastCellNum());
				cellFraud.setCellValue("FRAUD");
				
				//style manipulation
				CellStyle style = newSheet.getWorkbook().createCellStyle();
			    style.setFillForegroundColor(IndexedColors.RED.getIndex());
			    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cellFraud.setCellStyle(style);
				// store error rows
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
