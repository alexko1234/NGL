package controllers.instruments.io.utils;


import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import validation.ContextValidation;

public abstract class AbstractInput {
	
	public abstract Experiment importFile(Experiment experiment, PropertyFileValue pfv, ContextValidation contextValidation) throws Exception;
	
	protected static String getStringValue(Cell cell){
		//test cell existance  first !!
		//a string can also be a formula
		if ( null != cell && ( Cell.CELL_TYPE_STRING == cell.getCellType()|| Cell.CELL_TYPE_FORMULA == cell.getCellType())){
			return cell.getStringCellValue();
		}else{
			return null;
		}
	}
	
	protected static Double getNumericValue(Cell cell) {
		//test cell existance ffirst !!
		if(null != cell &&  (Cell.CELL_TYPE_NUMERIC == cell.getCellType() || Cell.CELL_TYPE_FORMULA == cell.getCellType())){
			return cell.getNumericCellValue();
		}else if(Cell.CELL_TYPE_STRING == cell.getCellType()){
			return Double.valueOf(cell.getStringCellValue());
		}else{
			return null;
		}
		
	}
	
	protected PropertySingleValue getPSV(InputContainerUsed icu, String code) {
		PropertySingleValue psv;
		if(null == icu.experimentProperties)icu.experimentProperties = new HashMap<String,PropertyValue>(0);
		
		
		if(!icu.experimentProperties.containsKey(code)){
			psv = new PropertySingleValue();
			icu.experimentProperties.put(code, psv);
		}else{
			psv = (PropertySingleValue)icu.experimentProperties.get(code);
		}
		return psv;
	}
}
