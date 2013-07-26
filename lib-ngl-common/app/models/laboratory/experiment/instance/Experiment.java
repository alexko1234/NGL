package models.laboratory.experiment.instance;

import static validation.utils.ConstraintsHelper.addErrors;
import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;
import static validation.utils.ConstraintsHelper.validateProperties;
import static validation.utils.ConstraintsHelper.validateTraceInformation;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.project.instance.Project;
import models.laboratory.reagent.instance.ReagentUsed;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import play.data.validation.ValidationError;

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
	public String stateCode;
	public String resolutionCode;
	
	//TODO delete class InputOutputContainer
	//public List<InputOutputContainer> listInputOutputContainers;
	
	//Idea for replace listInputOutputContainers attribut
	public Map<Integer,AtomicTransfertMethod> atomicTransfertMethods; 
	
	public List<ReagentUsed> reagentUseds;
	
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
		return new HelperObjects<State>().getObject(State.class, stateCode);
	}
	
	@JsonIgnore
	public Resolution getResolution(){
		return new HelperObjects<Resolution>().getObject(Resolution.class, resolutionCode);
	}

	@JsonIgnore
	@Override
	public void validate(Map<String, List<ValidationError>> errors) {
		String rootKeyName = null;
		if(this == null){
			throw new IllegalArgumentException("this is null");
		}
		
		if(this._id == null){
			validation.utils.BusinessValidationHelper.validateUniqueInstanceCode(errors, this.code, Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME);
		}
		
		validateTraceInformation(errors, this.traceInformation, this._id);
		
		required(errors, this.stateCode, "stateCode"); 
		
		validation.utils.BusinessValidationHelper.validateRequiredDescriptionCode(errors, this.typeCode, "typeCode", ExperimentType.find);
	
	}

	
}
