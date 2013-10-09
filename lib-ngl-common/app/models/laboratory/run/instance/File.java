package models.laboratory.run.instance;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

import java.util.HashMap;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;

import models.laboratory.common.instance.PropertyValue;
import models.utils.InstanceConstants;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

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
		ReadSet readSet = (ReadSet) contextValidation.getObject("readSet");
		if(ValidationHelper.required(contextValidation, this.fullname, "fullname")) {
			//Validate unique file.code if not already exists
			if(contextValidation.isCreationMode() && MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.is("files.fullname", this.fullname)))){
				contextValidation.addErrors("fullname",ValidationConstants.ERROR_NOTUNIQUE_MSG, this.fullname);
			}			
		}

		if(ValidationHelper.required(contextValidation, this.stateCode, "stateCode")){
			if(!RunPropertyDefinitionHelper.getReadSetStateCodes().contains(this.stateCode)){
				contextValidation.addErrors("stateCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, this.stateCode);
			}
		}
		
		ValidationHelper.required(contextValidation, this.extension, "extension");
		ValidationHelper.required(contextValidation, this.typeCode, "typeCode");
		ValidationHelper.required(contextValidation, this.usable, "usable");

		contextValidation.addKeyToRootKeyName("properties");
		ValidationHelper.validateProperties(contextValidation, this.properties, RunPropertyDefinitionHelper.getFilePropertyDefinitions());
		contextValidation.removeKeyFromRootKeyName("properties");

	}

}
