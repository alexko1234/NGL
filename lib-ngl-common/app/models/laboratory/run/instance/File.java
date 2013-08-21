package models.laboratory.run.instance;

import validation.utils.ValidationHelper;

import java.util.HashMap;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;

import models.laboratory.common.instance.PropertyValue;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import validation.utils.BusinessValidationHelper;
import validation.utils.ContextValidation;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class File extends DBObject implements IValidation {
	
	//concatenation de flotseqname + flotseqext	
	public String fullname;
	public String extension;
	public Boolean usable = Boolean.FALSE;
	public String typeCode; //id du type de fichier
	
	public Map<String, PropertyValue> properties = new HashMap<String, PropertyValue>();
	
	/*
	asciiEncoding	encodage ascii du fichier
	label			id du label du fichier READ1 / READ2 / SINGLETON	
	*/
	
	
	
	@Override
	public void validate(ContextValidation contextValidation) {
		
		Run run = (Run) contextValidation.getObject("run");
		
		//Validate unique file.code if not already exist
		Run runExist = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.files.fullname", this.fullname));
		if(runExist != null && run._id == null){ //when new run
			ValidationHelper.addErrors(contextValidation.errors, ValidationHelper.getKey(contextValidation.rootKeyName,"fullname"),ValidationConstants.ERROR_NOTUNIQUE, this.fullname);
		}
		 
		ValidationHelper.required(contextValidation.errors, this.extension, ValidationHelper.getKey(contextValidation.rootKeyName,"extension"));
		ValidationHelper.required(contextValidation.errors, this.fullname, ValidationHelper.getKey(contextValidation.rootKeyName,"fullname"));
		ValidationHelper.required(contextValidation.errors, this.typeCode, ValidationHelper.getKey(contextValidation.rootKeyName,"typeCode"));
		ValidationHelper.required(contextValidation.errors, this.usable, ValidationHelper.getKey(contextValidation.rootKeyName,"usable"));	
		
		String rootKeyNameProp = ValidationHelper.getKey(contextValidation.rootKeyName,"properties");
		ValidationHelper.validateProperties(contextValidation, this.properties, RunPropertyDefinitionHelper.getFilePropertyDefinitions(), rootKeyNameProp);		
		
	}

}
