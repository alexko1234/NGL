package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import validation.ContextValidation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.cea.ig.DBObject;


@JsonTypeInfo(use=Id.NAME, include=As.EXTERNAL_PROPERTY, property="_type", visible=true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = DefaultFieldConfiguration.class, name = AbstractFieldConfiguration.defaultType),
	@JsonSubTypes.Type(value = ExcelFieldConfiguration.class, name = AbstractFieldConfiguration.excelType),
	@JsonSubTypes.Type(value = PropertiesFieldConfiguration.class, name = AbstractFieldConfiguration.propertiesType)
})
public abstract class AbstractFieldConfiguration {
	public static final String defaultType = "default";
	public static final String excelType = "excel";
	public static final String propertiesType = "properties";
	
	public String _type;
	public Boolean required = Boolean.FALSE;
	
	public AbstractFieldConfiguration(String _type) {
		this._type = _type;
	}

	public abstract void populateField(Field field, DBObject dbObject,
			AbstractFieldConfiguration fieldConfiguration,
			Map<Integer, String> rowMap, ContextValidation contextValidation) throws Exception;
	
	
	protected void populateField(Field field, DBObject dbObject, String value)
			throws Exception {
		//in case of collection, we tranform single value to the good collection type
		if(Collection.class.isAssignableFrom(field.getType())){
			if(Set.class.isAssignableFrom(field.getType())){
				field.set(dbObject, Collections.singleton(value));
			}else{
				field.set(dbObject, Collections.singletonList(value));
			}			
		}else {
			field.set(dbObject, value);
		}
	}
	
}
