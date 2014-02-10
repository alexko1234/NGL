package models.laboratory.common.instance.property;

import java.io.IOException;

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
		super();
	}
	
	public PropertyFileValue(java.io.File value) throws IOException {
		super(Files.toByteArray(value));
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
		super.validate(contextValidation);
		ValidationHelper.required(contextValidation, this.fullname, "fullname");
		ValidationHelper.required(contextValidation, this.extension, "extension");
	}
	

}
