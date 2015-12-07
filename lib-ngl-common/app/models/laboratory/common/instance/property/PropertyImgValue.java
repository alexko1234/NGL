package models.laboratory.common.instance.property;

import java.io.IOException;
import java.util.Collection;

import play.Logger;

import models.laboratory.common.description.PropertyDefinition;
import validation.ContextValidation;
import validation.utils.ValidationHelper;

/**
 * Used to stock a file type image 2D
 * @author dnoisett
 * 
 */
public class PropertyImgValue extends PropertyFileValue {

	
	public PropertyImgValue() {
		super();
	}
	
	public PropertyImgValue(java.io.File value, Integer width, Integer height) throws IOException {
		super(value);
		this.width = width;
		this.height = height;
	}
	
	public Integer width;
	public Integer height;
	public String path; //for information
	
	@Override
	public String toString() {
		return "PropertyImgValue [fullname=" + fullname + ", ext=" + extension + ", width=" + width + ", height=" + height  +", path=" + path + ", class=" +value.getClass().getName()+"]";
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
			PropertyDefinition propertyDefinition = (PropertyDefinition) ((Collection<PropertyDefinition>)contextValidation.getObject("propertyDefinitions")).toArray()[0];
			super.validate(contextValidation); 
			ValidationHelper.required(contextValidation, this.width, propertyDefinition.code + ".width");
			ValidationHelper.required(contextValidation, this.height, propertyDefinition.code + ".height");
	}


}
