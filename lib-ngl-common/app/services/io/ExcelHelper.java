package services.io;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelHelper {
	
	public static String getStringValue(Cell cell){
		//test cell existance  first !!
		//a string can also be a formula
		if ( null != cell && ( Cell.CELL_TYPE_STRING == cell.getCellType()|| Cell.CELL_TYPE_FORMULA == cell.getCellType())){
			return cell.getStringCellValue();
		}else{
			return null;
		}
	}
	
	public static Double getNumericValue(Cell cell) {
		//test cell existance ffirst !!
		if(null != cell &&  (Cell.CELL_TYPE_NUMERIC == cell.getCellType() || Cell.CELL_TYPE_FORMULA == cell.getCellType())){
			return cell.getNumericCellValue();
		}else if(Cell.CELL_TYPE_STRING == cell.getCellType()){
			return Double.valueOf(cell.getStringCellValue());
		}else{
			return null;
		}
		
	}
	
	public static String convertToStringValue(Cell cell){
		//test cell existance  first !!
		//a string can also be a formula
		if ( null != cell && ( Cell.CELL_TYPE_STRING == cell.getCellType())){
			return cell.getStringCellValue();
		}else if(null != cell &&  (Cell.CELL_TYPE_NUMERIC == cell.getCellType())){
			return String.valueOf(cell.getNumericCellValue());
		}else if(null != cell &&  (Cell.CELL_TYPE_BLANK == cell.getCellType())){
			return cell.getStringCellValue();
		}else if(null != cell &&  (Cell.CELL_TYPE_BOOLEAN == cell.getCellType())){
			return String.valueOf(cell.getBooleanCellValue());
		}else if(null != cell &&  (Cell.CELL_TYPE_FORMULA == cell.getCellType())){
			return cell.getStringCellValue();
		}else{
			return null;
		}
	}
}
