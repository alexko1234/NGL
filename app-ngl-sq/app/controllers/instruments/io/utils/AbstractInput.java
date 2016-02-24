package controllers.instruments.io.utils;


import org.apache.poi.ss.usermodel.Cell;

import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;

public abstract class AbstractInput {
	
	public abstract Experiment importFile(Experiment experiment, PropertyFileValue pfv, ContextValidation contextValidation) throws Exception;
	
	protected static String getStringValue(Cell cell){
		if(Cell.CELL_TYPE_STRING == cell.getCellType()){
			return cell.getStringCellValue();
		}else{
			return null;
		}
	}
	
	protected static Double getNumericValue(Cell cell) {
		
		if(Cell.CELL_TYPE_NUMERIC == cell.getCellType() || Cell.CELL_TYPE_FORMULA == cell.getCellType()){
			return cell.getNumericCellValue();
		}else if(Cell.CELL_TYPE_STRING == cell.getCellType()){
			return Double.valueOf(cell.getStringCellValue());
		}else{
			return null;
		}
		
	}
}
