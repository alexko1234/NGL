package models.laboratory.run.instance;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
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
	@JsonIgnore
	public State state; //TODO remove later
	public Map<String, PropertyValue> properties = new HashMap<String, PropertyValue>();

	/*
	asciiEncoding	encodage ascii du fichier
	label			id du label du fichier READ1 / READ2 / SINGLETON
	*/

	@Override
	public void validate(ContextValidation contextValidation) {
		FileValidationHelper.validateFileFullName(this.fullname, contextValidation);
		ValidationHelper.required(contextValidation, this.extension, "extension");
		ValidationHelper.required(contextValidation, this.typeCode, "typeCode");
		ValidationHelper.required(contextValidation, this.usable, "usable");
		FileValidationHelper.validateFileProperties(this.properties, contextValidation);		
	}

}
