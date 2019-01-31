package services.io;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

public class ExcelHelper {
	
//	public static String getStringValue(Cell cell){
//		//test cell existance  first !!
//		//a string can also be a formula
//		if ( null != cell && ( Cell.CELL_TYPE_STRING == cell.getCellType()|| Cell.CELL_TYPE_FORMULA == cell.getCellType())){
//			return cell.getStringCellValue();
//		}else{
//			return null;
//		}
//	}
	public static String getStringValue(Cell cell) {
		if (cell == null)
			return null;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING  :
		case Cell.CELL_TYPE_FORMULA : return cell.getStringCellValue();
		default                     : return null;
		}
	}
	
//	public static Double getNumericValue(Cell cell) {
//		//test cell existance ffirst !!
//		if(null != cell &&  (Cell.CELL_TYPE_NUMERIC == cell.getCellType() || Cell.CELL_TYPE_FORMULA == cell.getCellType())){
//			return cell.getNumericCellValue();
//		}else if(Cell.CELL_TYPE_STRING == cell.getCellType()){
//			return Double.valueOf(cell.getStringCellValue());
//		}else{
//			return null;
//		}
//		
//	}
	public static Double getNumericValue(Cell cell) {
		if (cell == null)
			return null;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC :
		case Cell.CELL_TYPE_FORMULA : return cell.getNumericCellValue();
		case Cell.CELL_TYPE_STRING  : return Double.valueOf(cell.getStringCellValue());
		default                     : return null;
		}
	}
	
//	public static String convertToStringValue(Cell cell){
//		//test cell existance  first !!
//		//a string can also be a formula
//		if ( null != cell && ( Cell.CELL_TYPE_STRING == cell.getCellType())){
//			return cell.getStringCellValue();
//		}else if(null != cell &&  (Cell.CELL_TYPE_NUMERIC == cell.getCellType())){
//			if (HSSFDateUtil.isCellDateFormatted(cell)){
//				return cell.getDateCellValue().getTime()+"";
//			}else{
//				DataFormatter df = new DataFormatter();
//				DecimalFormat decimalFormat = (DecimalFormat)df.getDefaultFormat(cell);
//				String value =  decimalFormat.format(cell.getNumericCellValue()).replace(",", ".");
//				//Logger.debug(cell.getColumnIndex()+" "+value+" / "+cell.getNumericCellValue()+" / "+df.formatCellValue(cell));
//				return value;
//							
//			}			
//		}else if(null != cell &&  (Cell.CELL_TYPE_BLANK == cell.getCellType())){
//			return cell.getStringCellValue();
//		}else if(null != cell &&  (Cell.CELL_TYPE_BOOLEAN == cell.getCellType())){
//			return String.valueOf(cell.getBooleanCellValue());
//		}else if(null != cell &&  (Cell.CELL_TYPE_FORMULA == cell.getCellType())){
//			cell.setCellType(Cell.CELL_TYPE_STRING); //transform all result of formula un string to don't have problem with numerci or string
//			return cell.getStringCellValue();
//		}else{
//			return null;
//		}
//	}
	public static String convertToStringValue(Cell cell) {
		if (cell == null)
			return null;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING : 
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_NUMERIC : 
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				return Long.toString(cell.getDateCellValue().getTime());
			} else {
				DataFormatter df = new DataFormatter();
//				DecimalFormat decimalFormat = (DecimalFormat)df.getDefaultFormat(cell);
//				String value =  decimalFormat.format(cell.getNumericCellValue()).replace(",", ".");
				String value =  df.getDefaultFormat(cell).format(cell.getNumericCellValue()).replace(",", ".");
				//Logger.debug(cell.getColumnIndex()+" "+value+" / "+cell.getNumericCellValue()+" / "+df.formatCellValue(cell));
				return value;
			}			
		case Cell.CELL_TYPE_BLANK   : 
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_BOOLEAN : 
			return String.valueOf(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_FORMULA :
			cell.setCellType(Cell.CELL_TYPE_STRING); //transform all result of formula un string to don't have problem with numerci or string
			return cell.getStringCellValue();
		default                     : 
			return null;
		}
	}
	
}
