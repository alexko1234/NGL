package models.laboratory.experiment.instance;


import static validation.common.instance.CommonValidationHelper.FIELD_EXPERIMENT;
import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;
import static validation.common.instance.CommonValidationHelper.validateCode;
import static validation.common.instance.CommonValidationHelper.validateId;
import static validation.common.instance.CommonValidationHelper.validateTraceInformation;
import static validation.experiment.instance.ExperimentValidationHelper.validateAtomicTransfertMethods;
import static validation.experiment.instance.ExperimentValidationHelper.validateComments;
import static validation.experiment.instance.ExperimentValidationHelper.validateInstrumentUsed;
import static validation.experiment.instance.ExperimentValidationHelper.validateReagents;
import static validation.experiment.instance.ExperimentValidationHelper.validateRules;
import static validation.experiment.instance.ExperimentValidationHelper.validateState;
import static validation.experiment.instance.ExperimentValidationHelper.validateStatus;
import static validation.experiment.instance.ExperimentValidationHelper.validationExperimentCategoryCode;
import static validation.experiment.instance.ExperimentValidationHelper.validationExperimentType;
import static validation.experiment.instance.ExperimentValidationHelper.validationProtocoleCode;

import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.reagent.instance.ReagentUsed;
import models.utils.InstanceConstants;

import org.mongojack.MongoCollection;

import validation.ContextValidation;
import validation.IValidation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.DBObject;


/**
 * 
 * Experiment instance are stored one collection for the moment
 * 
 * For find the collection, see the value of attribut commoninfotype.collectionName in class experimentType
 * 
 * @author mhaquell
 *
 */

@MongoCollection(name="Experiment")
public class Experiment extends DBObject implements IValidation {
	

	public String typeCode;
	public String categoryCode;
	
	public TraceInformation traceInformation = new TraceInformation();;
	public Map<String,PropertyValue> experimentProperties;
	
	public Map<String, PropertyValue> instrumentProperties;
	
	public InstrumentUsed instrument;
	public String protocolCode;

	public State state = new State();
	public Valuation status = new Valuation();
	
	public List<AtomicTransfertMethod> atomicTransfertMethods; 
	
	public List<ReagentUsed> reagents;
	
	public List<Comment> comments;
	
	public Set<String> projectCodes;
	public Set<String> sampleCodes;
	
	public Set<String> inputContainerSupportCodes;
	public Set<String> inputContainerCodes;
	public Set<String> inputProcessCodes;
	public Set<String> inputProcessTypeCodes;
	public Set<String> inputFromTransformationTypeCodes;
	
	public Set<String> outputContainerCodes;
	public Set<String> outputContainerSupportCodes;
	
	
	
	public Experiment(){
		traceInformation=new TraceInformation();		
	}
	
	public Experiment(String code){
		this.code=code;		
	}
	
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {

		if(contextValidation.getObject(FIELD_STATE_CODE) == null){
			contextValidation.putObject(FIELD_STATE_CODE , state.code);
			
		}
		contextValidation.putObject(FIELD_EXPERIMENT , this);
		
		validateId(this, contextValidation);
		validateCode(this, InstanceConstants.EXPERIMENT_COLL_NAME, contextValidation);
		validationExperimentType(typeCode, experimentProperties, contextValidation);
		validationExperimentCategoryCode(categoryCode, contextValidation);
		validateState(this.typeCode, this.state, contextValidation);
		validateStatus(this.typeCode, this.status, contextValidation);
		validationProtocoleCode(typeCode,protocolCode,contextValidation);
		validateInstrumentUsed(instrument,instrumentProperties,contextValidation);
		validateAtomicTransfertMethods(typeCode, instrument, atomicTransfertMethods,contextValidation);
		validateReagents(reagents,contextValidation); //TODO active reagents validation inside ReagentUsed
		validateTraceInformation(traceInformation, contextValidation);		
		validateComments(comments, contextValidation);
		
		validateRules(this,contextValidation);
		contextValidation.removeObject(FIELD_EXPERIMENT);
	}

	
	

	
}
