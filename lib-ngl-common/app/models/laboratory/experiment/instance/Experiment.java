package models.laboratory.experiment.instance;

import java.util.ArrayList;
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
import static validation.common.instance.CommonValidationHelper.*;
import static validation.experiment.instance.ExperimentValidationHelper.*;

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
	
	@Deprecated
	@JsonIgnore
	public List<InputContainerUsed> getAllInputContainers(){
		List<InputContainerUsed> containersUSed=new ArrayList<InputContainerUsed>();
		if(this.atomicTransfertMethods!=null){
			for(int i=0;i<this.atomicTransfertMethods.size();i++){
				if(this.atomicTransfertMethods.get(i)!=null && this.atomicTransfertMethods.get(i).inputContainerUseds.size()>0){
					containersUSed.addAll(this.atomicTransfertMethods.get(i).inputContainerUseds);
				}
				
			}

		}
		return containersUSed;
	}
	@Deprecated
	@JsonIgnore
	public List<OutputContainerUsed> getAllOutputContainers(){
		List<OutputContainerUsed> containersUSed=new ArrayList<OutputContainerUsed>();
		if(this.atomicTransfertMethods!=null){
			for(int i=0;i<this.atomicTransfertMethods.size();i++){
				if(this.atomicTransfertMethods.get(i).outputContainerUseds != null && this.atomicTransfertMethods.get(i).outputContainerUseds.size()!=0){
					containersUSed.addAll(this.atomicTransfertMethods.get(i).outputContainerUseds);
				}
			}

		}
		return containersUSed;
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
		
		
		//TODO GA Validation not mandatory because is computing by NGL and can decrease performance ??
		validateInputContainerSupport(inputContainerSupportCodes,getAllInputContainers(),contextValidation);
		//validateOutputContainerSupport(outputContainerSupportCodes,getAllOutputContainers(),contextValidation); //because empty with void
		//TODO GA Validate projectCodes, sampleCodes. same question 
		
		validateRules(this,contextValidation);
		contextValidation.removeObject(FIELD_EXPERIMENT);
	}

	
	

	
}
