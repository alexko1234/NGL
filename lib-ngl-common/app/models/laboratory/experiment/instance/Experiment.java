package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.project.instance.Project;
import models.laboratory.reagent.instance.ReagentUsed;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

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
	
	// ExperimentType
	public String typeCode;
	public String categoryCode;
	// Informations
	public TraceInformation traceInformation;
	public Map<String,PropertyValue> experimentProperties;
	public Map<String, PropertyValue> instrumentProperties;
	
	public InstrumentUsed instrument;
	public String protocolCode;
	
	// States
	public State state;
	
	//TODO delete class InputOutputContainer
	//public List<InputOutputContainer> listInputOutputContainers;
	
	//Idea for replace listInputOutputContainers attribut
	public Map<Integer,AtomicTransfertMethod> atomicTransfertMethods; 
	
	public List<ReagentUsed> reagentsUsed;
	
	public List<Comment> comments;
	
	// For search optimisation
	//Projects ref
	public List<String> projectCodes;
	//Samples ref
	public List<String> sampleCodes;
	
	public Experiment(){
		traceInformation=new TraceInformation();
	}
	
	public Experiment(String code){
		this.code=code;
		traceInformation=new TraceInformation();
	}
	
	@JsonIgnore
	public ExperimentType getExperimentType(){
		return new HelperObjects<ExperimentType>().getObject(ExperimentType.class, typeCode);
	}

	@JsonIgnore
	public ExperimentCategory getExperimentCategory(){
		return new HelperObjects<ExperimentCategory>().getObject(ExperimentCategory.class, categoryCode);
	}

	
	@JsonIgnore
	public List<Sample> getSamples(){
		return new HelperObjects<Sample>().getObjects(Sample.class, sampleCodes);
	}
	
	@JsonIgnore
	public List<Project> getProjects(){
		return new HelperObjects<Project>().getObjects(Project.class, projectCodes);
	}
	
	
	@JsonIgnore
	public Protocol getProtocol(){
		return new HelperObjects<Protocol>().getObject(Protocol.class, protocolCode);
	}
	
	@JsonIgnore
	public State getState(){
		return new HelperObjects<State>().getObject(State.class, state.code);
	}
	
	@JsonIgnore
	public List<Resolution> getResolution(){
		return new HelperObjects<Resolution>().getObjects(Resolution.class, resolutionCodes);
	}

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
	@Override
	public void validate(ContextValidation contextValidation) {
				
		ExperimentValidationHelper.validateCode(this, InstanceConstants.EXPERIMENT_COLL_NAME, contextValidation);
//		ExperimentValidationHelper.validateState(this.typeCode, this.state, contextValidation);
		ExperimentValidationHelper.validationExperimentType(typeCode, experimentProperties, contextValidation);
		ExperimentValidationHelper.validationExperimentCategoryCode(categoryCode, contextValidation);
		ExperimentValidationHelper.validateResolutionCodes(resolutionCodes,contextValidation);
		ExperimentValidationHelper.validationProtocol(typeCode,protocolCode,contextValidation);
		ExperimentValidationHelper.validateInstrumentUsed(typeCode,instrument,instrumentProperties,contextValidation);
		ExperimentValidationHelper.validateAtomicTransfertMethodes(atomicTransfertMethods,contextValidation);
		ExperimentValidationHelper.validateReagents(reagentsUsed,contextValidation);
		ExperimentValidationHelper.validateTraceInformation(traceInformation, contextValidation);			

	}

	
}
