package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import validation.ContextValidation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;




@JsonTypeInfo(use=Id.NAME, include=As.EXTERNAL_PROPERTY, property="_type", visible=true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = DefaultFieldConfiguration.class, name = AbstractFieldConfiguration.defaultType),
	@JsonSubTypes.Type(value = ExcelFieldConfiguration.class, name = AbstractFieldConfiguration.excelType),
	@JsonSubTypes.Type(value = PropertiesFieldConfiguration.class, name = AbstractFieldConfiguration.propertiesType),
	@JsonSubTypes.Type(value = PropertyValueFieldConfiguration.class, name = AbstractFieldConfiguration.propertyValueType),
	@JsonSubTypes.Type(value = ObjectFieldConfiguration.class, name = AbstractFieldConfiguration.objectType),
	@JsonSubTypes.Type(value = CommentsFieldConfiguration.class, name = AbstractFieldConfiguration.commentsType),
	@JsonSubTypes.Type(value = ContentsFieldConfiguration.class, name = AbstractFieldConfiguration.contentsType)
})
public abstract class AbstractFieldConfiguration {
	public static final String defaultType = "default";
	public static final String excelType = "excel";
	public static final String propertiesType = "properties";
	public static final String objectType = "object";
	public static final String propertyValueType = "propertyValue";
	public static final String commentsType = "comments";
	public static final String contentsType = "contents";
	
	public String _type;
	public Boolean required = Boolean.FALSE;
	
	public AbstractFieldConfiguration(String _type) {
		this._type = _type;
	}

	/**
	 * Extract value and then set value in object
	 * @param field
	 * @param dbObject
	 * @param rowMap
	 * @param contextValidation
	 * @param action TODO
	 * @throws Exception
	 */
	public abstract void populateField(Field field, Object dbObject,
			Map<Integer, String> rowMap, ContextValidation contextValidation, Action action) throws Exception;
	
	
	/**
	 * Set value directly in object
	 * @param field
	 * @param dbObject
	 * @param value
	 * @throws Exception
	 */
	protected void populateField(Field field, Object dbObject, Object value)
			throws Exception {
		//in case of collection, we tranform single value to the good collection type
		if(Collection.class.isAssignableFrom(field.getType()) && !Collection.class.isAssignableFrom(value.getClass())){
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
