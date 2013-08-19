package models.laboratory.run.instance;package models.laboratory.run.instance;

import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;

import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;

import models.laboratory.common.instance.PropertyValue;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import validation.utils.BusinessValidationHelper;
import validation.utils.ConstraintsHelper;
import validation.utils.ContextValidation;
import validation.utils.RunPropertyDefinitionHelper;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class File extends DBObject implements IValidation {
	
	//concatenation de flotseqname + flotseqext	
	public String fullname;
	public String extension;
	public Boolean usable = Boolean.FALSE;
	public String typeCode; //id du type de fichier
	
	public Map<String, PropertyValue> properties= InstanceHelpers.getLazyMapPropertyValue();
	
	/*
	asciiEncoding	encodage ascii du fichier
	label			id du label du fichier READ1 / READ2 / SINGLETON	
	*/
	
	
	
	@Override
	public void validate(ContextValidation contextValidation) {
		
		Run run = (Run) contextValidation.contextObjects.get("run");
		
		//Validate unique file.code if not already exist
		Run runExist = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.files.fullname", this.fullname));
		if(runExist != null && run._id == null){ //when new run
			ConstraintsHelper.addErrors(contextValidation.errors, getKey(contextValidation.rootKeyName,"fullname"),InstanceConstants.ERROR_NOTUNIQUE, this.fullname);
		}
		 
		required(contextValidation.errors, this.extension, getKey(contextValidation.rootKeyName,"extension"));
		required(contextValidation.errors, this.fullname, getKey(contextValidation.rootKeyName,"fullname"));
		required(contextValidation.errors, this.typeCode, getKey(contextValidation.rootKeyName,"typeCode"));
		required(contextValidation.errors, this.usable, getKey(contextValidation.rootKeyName,"usable"));	
		
		String rootKeyNameProp = getKey(contextValidation.rootKeyName,"properties");
		ConstraintsHelper.validateProperties(contextValidation.errors, this.properties, RunPropertyDefinitionHelper.getFilePropertyDefinitions(), rootKeyNameProp);		
		
	}

}
