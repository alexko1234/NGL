package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.reagent.instance.ReagentUsed;
import models.utils.InstanceConstants;
import org.mongojack.MongoCollection;

import com.fasterxml.jackson.annotation.JsonIgnore;


import validation.ContextValidation;
import validation.IValidation;
import validation.experiment.instance.ExperimentValidationHelper;
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
	
	public TraceInformation traceInformation;
	public Map<String,PropertyValue> experimentProperties;
	
	public Map<String, PropertyValue> instrumentProperties;
	
	public InstrumentUsed instrument;
	public String protocolCode;

	public State state;
	
	public Map<Integer,AtomicTransfertMethod> atomicTransfertMethods; 
	
	public List<ReagentUsed> reagentsUsed;
	
	public List<Comment> comments;
	
	public List<String> projectCodes;
	
	public List<String> sampleCodes;
	
	public Experiment(){
		traceInformation=new TraceInformation();
		state=new State();
	}
	
	public Experiment(String code){
		this.code=code;
		traceInformation=new TraceInformation();
		state=new State();
	}
	
	@JsonIgnore
	public List<ContainerUsed> getAllInPutContainer(){
		List<ContainerUsed> containersUSed=new ArrayList<ContainerUsed>();
		if(this.atomicTransfertMethods!=null){
			for(int i=0;i<this.atomicTransfertMethods.size();i++){
				containersUSed.addAll(this.atomicTransfertMethods.get(i).getInputContainers());
			}

		}
		return containersUSed;
	}
	
	@JsonIgnore
	public List<ContainerUsed> getAllOutPutContainerWhithInPutContainer(){
		List<ContainerUsed> containersUSed=new ArrayList<ContainerUsed>();
		if(this.atomicTransfertMethods!=null){
			for(int i=0;i<this.atomicTransfertMethods.size();i++){
				if(this.atomicTransfertMethods.get(i).getInputContainers().size()!=0){
					containersUSed.addAll(this.atomicTransfertMethods.get(i).getOutputContainers());
				}
			}

		}
		return containersUSed;
	}
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
				
		ExperimentValidationHelper.validateCode(this, InstanceConstants.EXPERIMENT_COLL_NAME, contextValidation);
//		ExperimentValidationHelper.validateState(this.typeCode, this.state, contextValidation);
		ExperimentValidationHelper.validationExperimentType(typeCode, experimentProperties, contextValidation);
		ExperimentValidationHelper.validationExperimentCategoryCode(categoryCode, contextValidation);
		ExperimentValidationHelper.validateResolutionCodes(state.resolutionCodes,contextValidation);
		ExperimentValidationHelper.validationProtocol(typeCode,protocolCode,contextValidation);
		ExperimentValidationHelper.validateInstrumentUsed(instrument,instrumentProperties,contextValidation);
		ExperimentValidationHelper.validateAtomicTransfertMethodes(atomicTransfertMethods,contextValidation);
		ExperimentValidationHelper.validateReagents(reagentsUsed,contextValidation);
		ExperimentValidationHelper.validateTraceInformation(traceInformation, contextValidation);		
		ExperimentValidationHelper.validateRules(this,contextValidation);

	}

	
}
