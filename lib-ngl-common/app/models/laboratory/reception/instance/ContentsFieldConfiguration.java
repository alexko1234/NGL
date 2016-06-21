package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import models.laboratory.container.instance.Content;
import validation.ContextValidation;

public class ContentsFieldConfiguration extends ObjectFieldConfiguration<Content> {
	
	public Boolean pool = Boolean.FALSE;
	
	public ContentsFieldConfiguration() {
		super(AbstractFieldConfiguration.contentsType);		
	}

	@Override
	public void populateField(Field field, Object dbObject,
			Map<Integer, String> rowMap, ContextValidation contextValidation)
			throws Exception {
		
		Content content = new Content();

		populateSubFields(content, rowMap, contextValidation);
		
		if(!pool){
			populateField(field, dbObject, Collections.singletonList(content));	
		}else{
			throw new RuntimeException("Pool not managed");
		}
			
	}

}
