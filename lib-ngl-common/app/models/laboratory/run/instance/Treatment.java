package models.laboratory.run.instance;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.description.TreatmentType;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;

import  com.fasterxml.jackson.annotation.JsonAnyGetter;
import  com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.run.instance.TreatmentValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;


public class Treatment implements IValidation{
	
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
		TreatmentType treatmentType = TreatmentValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", TreatmentType.find,true);
		if(null != treatmentType){
			TreatmentValidationHelper.validateCode(treatmentType, code, contextValidation);
			TreatmentValidationHelper.validateTreatmentCategoryCode(treatmentType, categoryCode, contextValidation);
			TreatmentValidationHelper.validateResults(treatmentType, results, contextValidation);						
		}					
	}
}
