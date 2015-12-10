package models.laboratory.common.instance.property;

import java.io.IOException;
import java.util.Collection;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import validation.ContextValidation;
import validation.utils.ValidationHelper;

import com.google.common.io.Files;


/**
 * Used to stock a File
 * 
 * 
 */
public class PropertyFileValue extends PropertyByteValue{
	
	
	public PropertyFileValue() {
		super(PropertyValue.fileType);
	}
	
	public PropertyFileValue(java.io.File value) throws IOException {
		super(PropertyValue.fileType, Files.toByteArray(value));
		this.extension = Files.getFileExtension(value.getName());
		this.fullname = value.getName();
	}
	
	public String fullname;
	public String extension;
	
	@Override
	public String toString() {
		return "PropertyFileValue [name=" + fullname + ", ext=" + extension+", class="+value.getClass().getName()+"]";
	}
	
	
	@Override
	public void validate(ContextValidation contextValidation) {
		PropertyDefinition propertyDefinition = (PropertyDefinition) ((Collection<PropertyDefinition>)contextValidation.getObject("propertyDefinitions")).toArray()[0];
		super.validate(contextValidation);
		ValidationHelper.required(contextValidation, this.fullname, propertyDefinition.code + ".fullname");
		ValidationHelper.required(contextValidation, this.extension, propertyDefinition.code + ".extension");
	}
	

}
