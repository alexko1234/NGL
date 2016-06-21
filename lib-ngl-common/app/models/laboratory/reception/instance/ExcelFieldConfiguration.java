package models.laboratory.reception.instance;

import static validation.utils.ValidationConstants.ERROR_REQUIRED_MSG;

import java.lang.reflect.Field;
import java.util.Map;

import validation.ContextValidation;


public class ExcelFieldConfiguration extends AbstractFieldConfiguration {
	
	public String headerValue;
	public Integer cellPosition;
	
	public ExcelFieldConfiguration() {
		super(AbstractFieldConfiguration.excelType);		
	}

	@Override
	public void populateField(Field field, Object dbObject,
			Map<Integer, String> rowMap, ContextValidation contextValidation) throws Exception {
		
		if(rowMap.containsKey(cellPosition)){
			String value = rowMap.get(cellPosition);			
			populateField(field, dbObject, value);				
		}else if(required){
			contextValidation.addErrors(headerValue, ERROR_REQUIRED_MSG);
		}
	}

}
