package models.laboratory.reception.instance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


@JsonTypeInfo(use=Id.NAME, include=As.EXTERNAL_PROPERTY, property="_type", visible=true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = DefaultFieldConfiguration.class, name = FieldConfiguration.defaultType),
	@JsonSubTypes.Type(value = ExcelFieldConfiguration.class, name = FieldConfiguration.excelType)	
})
public abstract class FieldConfiguration {
	public static final String defaultType = "default";
	public static final String excelType = "excel";
}
