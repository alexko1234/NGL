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

import play.Logger;
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
	
	public TraceInformation traceInformation = new TraceInformation();
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
		long t0 = System.currentTimeMillis();
		if(contextValidation.getObject(FIELD_STATE_CODE) == null){
			contextValidation.putObject(FIELD_STATE_CODE , state.code);
			
		}
		contextValidation.putObject(FIELD_EXPERIMENT , this);
		
		long t1 = System.currentTimeMillis();
		validateId(this, contextValidation);
		
		long t2 = System.currentTimeMillis();
		validateCode(this, InstanceConstants.EXPERIMENT_COLL_NAME, contextValidation);
		
		long t3 = System.currentTimeMillis();
		validationExperimentType(typeCode, experimentProperties, contextValidation);
		
		long t4 = System.currentTimeMillis();
		validationExperimentCategoryCode(categoryCode, contextValidation);
		
		long t5 = System.currentTimeMillis();
		validateState(this.typeCode, this.state, contextValidation);
		
		long t6 = System.currentTimeMillis();
		validateStatus(this.typeCode, this.status, contextValidation);
		
		long t7 = System.currentTimeMillis();
		validationProtocoleCode(typeCode,protocolCode,contextValidation);
		
		long t8 = System.currentTimeMillis();
		validateInstrumentUsed(instrument,instrumentProperties,contextValidation);
		
		long t9 = System.currentTimeMillis();
		validateAtomicTransfertMethods(typeCode, instrument, atomicTransfertMethods,contextValidation);
		
		long t10 = System.currentTimeMillis();
		validateReagents(reagents,contextValidation); //TODO active reagents validation inside ReagentUsed
		
		long t11 = System.currentTimeMillis();
		validateTraceInformation(traceInformation, contextValidation);
		
		long t12 = System.currentTimeMillis();
		validateComments(comments, contextValidation);
		
		long t13 = System.currentTimeMillis();
		validateRules(this,contextValidation);
		contextValidation.removeObject(FIELD_EXPERIMENT);
		
		long t14 = System.currentTimeMillis();
		/*
		Logger.debug("Experiment validate \n "
				+"1 = "+(t1-t0)+" ms\n"
				+"2 = "+(t2-t1)+" ms\n"
				+"3 = "+(t3-t2)+" ms\n"
				+"4 = "+(t4-t3)+" ms\n"
				+"5 = "+(t5-t4)+" ms\n"
				+"6 = "+(t6-t5)+" ms\n"
				+"7 = "+(t7-t6)+" ms\n"
				+"8 = "+(t8-t7)+" ms\n"
				+"9 = "+(t9-t8)+" ms\n"
				+"10 = "+(t10-t9)+" ms\n"
				+"11 = "+(t11-t10)+" ms\n"
				+"12 = "+(t12-t11)+" ms\n"
				+"13 = "+(t13-t12)+" ms\n"
				+"14 = "+(t14-t13)+" ms\n"
				+"15 = "+(t14-t0)+" ms\n"
				
				);
		*/	
				
	}

	
	

	
}
