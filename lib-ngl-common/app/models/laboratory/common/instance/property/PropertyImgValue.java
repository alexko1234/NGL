package models.laboratory.common.instance.property;

import java.io.IOException;
import java.util.Collection;

import play.Logger;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
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
		super._type=PropertyValue.imgType;
	}
	
	public PropertyImgValue(java.io.File value, Integer width, Integer height) throws IOException {
		super(value);
		super._type=PropertyValue.imgType;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyImgValue other = (PropertyImgValue) obj;
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		return true;
	}


}
