package models.laboratory.run.instance;

import java.util.HashMap;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;

import fr.cea.ig.MongoDBDAO;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.BusinessValidationHelper;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.Level.CODE;
import models.laboratory.common.instance.PropertyValue;
import models.utils.InstanceConstants;


public class Treatment implements IValidation{
	
	private static final String LEVEL_KEY = "level";

	public Treatment() {
		super();
	
	}
	
	public String code;
	public String typeCode;
	public String categoryCode;
	
	@JsonIgnore
	public Map<String, Map<String, PropertyValue>> results = new HashMap<String, Map<String, PropertyValue>>();

	@JsonAnyGetter
    public Map<String,Map<String,PropertyValue>> results() {
        return results;
    }

    @JsonAnySetter
    public void set(String name, Map<String,PropertyValue> value) {
    	results.put(name, value);
    }

	@Override
	public void validate(ContextValidation contextValidation) {
		Level.CODE levelCode = (Level.CODE)contextValidation.getObject(LEVEL_KEY);
		if(null == levelCode){
			throw new IllegalArgumentException("missing level parameter");
		}
		
		if(ValidationHelper.required(contextValidation, this.code, "code")){
			if (contextValidation.isCreationMode() && isTreatmentExist(contextValidation, this.code)) {
		    	contextValidation.addErrors("code",ValidationConstants.ERROR_CODE_NOTUNIQUE_MSG, this.code);		    	
			}else if (contextValidation.isUpdateMode() && !isTreatmentExist(contextValidation, this.code)){
				contextValidation.addErrors("code",ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, this.code);
			}
			
			if(!RunPropertyDefinitionHelper.getTreatmentCodes().contains(this.code)){
				contextValidation.addErrors("code",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, this.code);
			}
			
		}
		
		if(ValidationHelper.required(contextValidation, this.typeCode, "typeCode")){
			if(!RunPropertyDefinitionHelper.getTreatmentTypeCodes().contains(this.typeCode)){
				contextValidation.addErrors("typeCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, this.typeCode);
			}
		}
		
		if(ValidationHelper.required(contextValidation, this.categoryCode, "categoryCode")){
			if(!RunPropertyDefinitionHelper.getTreatmentCatTypeCodes().contains(this.categoryCode)){
				contextValidation.addErrors("categoryCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, this.categoryCode);
			}
		}
		
		//TODO context must be mandatory
		if(ValidationHelper.required(contextValidation, results, "result")){
			validate(contextValidation, results);
		}
	}

	private boolean isTreatmentExist(ContextValidation contextValidation, String code) {
		Level.CODE levelCode = (Level.CODE)contextValidation.getObject(LEVEL_KEY);
		
		if(Level.CODE.ReadSet.equals(levelCode)){
			ReadSet readSet = (ReadSet) contextValidation.getObject("readSet");
			return MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.exists("treatments."+code)));
		}else if(Level.CODE.Lane.equals(levelCode)){
			Run run = (Run) contextValidation.getObject("run");
			Lane lane = (Lane) contextValidation.getObject("lane");
			
			return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", run.code), 
							DBQuery.elemMatch("lanes", 
									DBQuery.and(
											DBQuery.is("number", lane.number),
											DBQuery.exists("treatments."+code)))));
							
		}else if(Level.CODE.Run.equals(levelCode)){
			Run run = (Run) contextValidation.getObject("run");
			return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", run.code),DBQuery.exists("treatments."+code)));			
		}
		return false;
	}

	private void validate(ContextValidation contextValidation, Map<String, Map<String, PropertyValue>> results) {
		
		for(Map.Entry<String, Map<String, PropertyValue>> entry : results.entrySet()){
			if(!RunPropertyDefinitionHelper.getTreatmentContextCodes().contains(entry.getKey())){
				contextValidation.addErrors(entry.getKey(),ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, entry.getKey());
			}
			
			contextValidation.addKeyToRootKeyName(entry.getKey());
			ValidationHelper.validateProperties(contextValidation, entry.getValue(), 
					RunPropertyDefinitionHelper.getTreatmentPropertyDefinitions(this.code, (Level.CODE)contextValidation.getObject(LEVEL_KEY)));
			contextValidation.removeKeyFromRootKeyName(entry.getKey());
		}
		
	}

}
