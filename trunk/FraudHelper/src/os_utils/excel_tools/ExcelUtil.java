package os_utils.excel_tools;


import java.util.List;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
 
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
        
      /**  
       *  
       * @author jk  
       * getted from http://jxls.cvs.sourceforge.net/jxls/jxls/src/java/org/jxls/util/Util.java?revision=1.8&view=markup  
       * by Leonid Vysochyn   
       * and modified (adding styles copying)  
       * modified by Philipp Löpmeier (replacing deprecated classes and methods, using generic types)  
       */  
      public final class ExcelUtil {   
             
            /**
             * DEFAULT CONSTRUCTOR.
             */
            private ExcelUtil() {}
            
          /**
           * @param newSheet the sheet to create from the copy.
           * @param sheet the sheet to copy.
           */
          public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet){   
              copySheets(newSheet, sheet, true);   
          }   
             
          /**
           * @param newSheet the sheet to create from the copy.
           * @param sheet the sheet to copy.
           * @param copyStyle true copy the style.
           */
          public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet, boolean copyStyle){   
              int maxColumnNum = 0;   
              Map<Integer, HSSFCellStyle> styleMap = (copyStyle) ? new HashMap<Integer, HSSFCellStyle>() : null;   
              for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {   
                  HSSFRow srcRow = sheet.getRow(i);   
                  HSSFRow destRow = newSheet.createRow(i);   
                  if (srcRow != null) {   
                      ExcelUtil.copyRow(sheet, newSheet, srcRow, destRow, styleMap);   
                      if (srcRow.getLastCellNum() > maxColumnNum) {   
                          maxColumnNum = srcRow.getLastCellNum();   
                      }   
                  }   
              }   
              for (int i = 0; i <= maxColumnNum; i++) {   
                  newSheet.setColumnWidth(i, sheet.getColumnWidth(i));   
              }   
          }   
        
          /**
           * @param srcSheet the sheet to copy.
           * @param destSheet the sheet to create.
           * @param srcRow the row to copy.
           * @param destRow the row to create.
           * @param styleMap -
           */
          public static void copyRow(HSSFSheet srcSheet, HSSFSheet destSheet, HSSFRow srcRow, HSSFRow destRow, Map<Integer, HSSFCellStyle> styleMap) {   
              // manage a list of merged zone in order to not insert two times a merged zone
            Set<CellRangeAddressWrapper> mergedRegions = new TreeSet<CellRangeAddressWrapper>();   
              destRow.setHeight(srcRow.getHeight());   
              // pour chaque row
              for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {   
                  HSSFCell oldCell = srcRow.getCell(j);   // ancienne cell
                  HSSFCell newCell = destRow.getCell(j);  // new cell 
                  if (oldCell != null) {   
                      if (newCell == null) {   
                          newCell = destRow.createCell(j);   
                      }   
                      // copy chaque cell
                      copyCell(oldCell, newCell, styleMap);   
                      // copy les informations de fusion entre les cellules
                      //System.out.println("row num: " + srcRow.getRowNum() + " , col: " + (short)oldCell.getColumnIndex());
                      CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(), (short)oldCell.getColumnIndex());   
                      
                      if (mergedRegion != null) { 
                        //System.out.println("Selected merged region: " + mergedRegion.toString());
                        CellRangeAddress newMergedRegion = new CellRangeAddress(mergedRegion.getFirstRow(), mergedRegion.getLastRow(), mergedRegion.getFirstColumn(),  mergedRegion.getLastColumn());
                          //System.out.println("New merged region: " + newMergedRegion.toString());
                          CellRangeAddressWrapper wrapper = new CellRangeAddressWrapper(newMergedRegion);
                          if (isNewMergedRegion(wrapper, mergedRegions)) {
                              mergedRegions.add(wrapper);
                              destSheet.addMergedRegion(wrapper.range);   
                          }   
                      }   
                  }   
              }   
                 
          }   
             
          /**
           * @param oldCell
           * @param newCell
           * @param styleMap
           */
          public static void copyCell(HSSFCell oldCell, HSSFCell newCell, Map<Integer, HSSFCellStyle> styleMap) {   
              if(styleMap != null) {   
                  if(oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()){   
                      newCell.setCellStyle(oldCell.getCellStyle());   
                  } else{   
                      int stHashCode = oldCell.getCellStyle().hashCode();   
                      HSSFCellStyle newCellStyle = styleMap.get(stHashCode);   
                      if(newCellStyle == null){   
                          newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();   
                          newCellStyle.cloneStyleFrom(oldCell.getCellStyle());   
                          styleMap.put(stHashCode, newCellStyle);   
                      }   
                      newCell.setCellStyle(newCellStyle);   
                  }   
              }   
              switch(oldCell.getCellType()) {   
                  case HSSFCell.CELL_TYPE_STRING:   
                      newCell.setCellValue(oldCell.getStringCellValue());   
                      break;   
                case HSSFCell.CELL_TYPE_NUMERIC:   
                      newCell.setCellValue(oldCell.getNumericCellValue());   
                      break;   
                  case HSSFCell.CELL_TYPE_BLANK:   
                      newCell.setCellType(HSSFCell.CELL_TYPE_BLANK);   
                      break;   
                  case HSSFCell.CELL_TYPE_BOOLEAN:   
                      newCell.setCellValue(oldCell.getBooleanCellValue());   
                      break;   
                  case HSSFCell.CELL_TYPE_ERROR:   
                      newCell.setCellErrorValue(oldCell.getErrorCellValue());   
                      break;   
                  case HSSFCell.CELL_TYPE_FORMULA:   
                      newCell.setCellFormula(oldCell.getCellFormula());   
                      break;   
                  default:   
                      break;   
              }   
                 
          }   
             
          /**
           * Récupère les informations de fusion des cellules dans la sheet source pour les appliquer
           * à la sheet destination...
           * Récupère toutes les zones merged dans la sheet source et regarde pour chacune d'elle si
           * elle se trouve dans la current row que nous traitons.
           * Si oui, retourne l'objet CellRangeAddress.
           * 
           * @param sheet the sheet containing the data.
           * @param rowNum the num of the row to copy.
           * @param cellNum the num of the cell to copy.
           * @return the CellRangeAddress created.
           */
          public static CellRangeAddress getMergedRegion(HSSFSheet sheet, int rowNum, short cellNum) {   
              for (int i = 0; i < sheet.getNumMergedRegions(); i++) { 
                  CellRangeAddress merged = sheet.getMergedRegion(i);   
                  if (merged.isInRange(rowNum, cellNum)) {   
                      return merged;   
                  }   
              }   
              return null;   
          }   
        
          /**
           * Check that the merged region has been created in the destination sheet.
           * @param newMergedRegion the merged region to copy or not in the destination sheet.
           * @param mergedRegions the list containing all the merged region.
           * @return true if the merged region is already in the list or not.
           */
          private static boolean isNewMergedRegion(CellRangeAddressWrapper newMergedRegion, Set<CellRangeAddressWrapper> mergedRegions) {
            return !mergedRegions.contains(newMergedRegion);   
          }
          
          /** Gets column number that starts with string prefix
           * 
           * @param row		row where to search
           * @param prefix	string to search
           * @return column index or -1 if not found
           */
          public static int getColNumStartsWith(Row row, String prefix) {
      		// TODO Auto-generated method stub		
      		
      		for(Cell cell: row) {
      			if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
      				String res = cell.getRichStringCellValue().getString();  	                
  	                if( res.startsWith(prefix))
  	                	return cell.getColumnIndex() + 1;
      			}
      		}      		
      		return -1;
          }
          
          public class MyCell extends HSSFCell {

			protected MyCell(HSSFWorkbook book, HSSFSheet sheet,
					CellValueRecordInterface cval) {
				super(book, sheet, cval);
				// TODO Auto-generated constructor stub
			}
			
			protected MyCell(HSSFWorkbook book,
					HSSFSheet sheet,
					int row,
					short col) {
				super(book, sheet, row, col);
			}
        	  
			protected MyCell(HSSFWorkbook book,
					HSSFSheet sheet,
					int row, short col, int type) {
				super(book, sheet, row, col, type);
			}
			
          }

		public static double[] getDoubleValuesFromRow(Row row,
				int dataStartColNum, int lastCellNum) {
			// TODO Auto-generated method stub
			List<Double> results = new LinkedList<Double>();
			for(int i=dataStartColNum; i < lastCellNum; i++) {
				Double value = row.getCell(i).getNumericCellValue();
				results.add(value);
			}
			if(!results.isEmpty()){
				double res[] = new double[results.size()];
				for(int i = 0; i < results.size(); i++) {
					res[i] = results.get(i);
				}				 
				return res;
			}
			return null;
		}

		
      	
             
      } 
