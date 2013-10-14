package models.laboratory.run.instance;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import validation.ContextValidation;
import validation.IValidation;
import validation.run.instance.FileValidationHelper;
import validation.utils.ValidationHelper;

public class File implements IValidation {

	//concatenation de flotseqname + flotseqext
	public String fullname;
	public String extension;
	public Boolean usable = Boolean.FALSE;
	public String typeCode; //id du type de fichier
	public String stateCode;
	public Map<String, PropertyValue> properties = new HashMap<String, PropertyValue>();

	/*
	asciiEncoding	encodage ascii du fichier
	label			id du label du fichier READ1 / READ2 / SINGLETON
	*/

	@Override
	public void validate(ContextValidation contextValidation) {
		FileValidationHelper.validateFileFullName(this.fullname, contextValidation);
		FileValidationHelper.validateStateCode(this.stateCode, contextValidation);
		ValidationHelper.required(contextValidation, this.extension, "extension");
		ValidationHelper.required(contextValidation, this.typeCode, "typeCode");
		ValidationHelper.required(contextValidation, this.usable, "usable");
		FileValidationHelper.validateFileProperties(this.properties, contextValidation);		
	}

}
