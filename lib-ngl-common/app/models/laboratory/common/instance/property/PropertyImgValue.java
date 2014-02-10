package models.laboratory.common.instance.property;

import java.io.IOException;
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
	
	@Override
	public String toString() {
		return "PropertyImgValue [fullname=" + fullname + ", ext=" + extension + ", width=" + width + ", height=" + height  +", class="+value.getClass().getName()+"]";
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation); 
		ValidationHelper.required(contextValidation, this.width, "width");
		ValidationHelper.required(contextValidation, this.height, "height");
	}


}
