package models.laboratory.experiment.instance;

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
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.DescriptionValidationHelper;
import validation.IValidation;
import validation.InstanceValidationHelper;
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
	
	public String instrumentUsedTypeCode;
	public InstrumentUsed instrument;
	public String protocolCode;
	
	// States
	public String stateCode;
	public List<String> resolutionCodes;
	
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
		return new HelperObjects<State>().getObject(State.class, stateCode);
	}
	
	@JsonIgnore
	public List<Resolution> getResolution(){
		return new HelperObjects<Resolution>().getObjects(Resolution.class, resolutionCodes);
	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		
		if(this == null){
			throw new IllegalArgumentException("this is null");
		}
		
		contextValidation.putObject("_id",this._id);
		
		validation.utils.BusinessValidationHelper.validateUniqueInstanceCode(contextValidation, this.code, Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME);
		
		DescriptionValidationHelper.validationStateCode(stateCode, contextValidation);
		DescriptionValidationHelper.validationExperimentTypeCode(typeCode, contextValidation);
		DescriptionValidationHelper.validationExperimentCategoryCode(categoryCode, contextValidation);
		DescriptionValidationHelper.validationResolutionCodes(resolutionCodes, contextValidation);
		DescriptionValidationHelper.validationProtocol(protocolCode,contextValidation);
		DescriptionValidationHelper.validationInstrumentUsedTypeCode(instrumentUsedTypeCode,contextValidation);
		
		//ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, this.typeCode, "typeCode", ExperimentType.find,true);
		//InstrumentUsedType instrumentUsedType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, this.instrumentUsedTypeCode,"typeCode", InstrumentUsedType.find,true);
		//ConstraintsHelper.validatePropertiesforLevel(contextValidation.errors, this.experimentProperties, exType.propertiesDefinitions,"",LEVEL_SEARCH_EXP);

		DescriptionValidationHelper.validationExperimentType(typeCode, experimentProperties, contextValidation);
		
		
		String rootKeyName=null;
		for(int i=0;i<atomicTransfertMethods.size();i++){
			rootKeyName="atomictransfertmethod"+"["+i+"]";
			contextValidation.addKeyToRootKeyName(rootKeyName);
			atomicTransfertMethods.get(i).validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName(rootKeyName);
		}
		
		instrument.validate(contextValidation);
		if(reagentsUsed != null){
			for(ReagentUsed reagentUsed:reagentsUsed){
				reagentUsed.validate(contextValidation);
			}
		}
		
		traceInformation.validate(contextValidation);
		InstanceValidationHelper.validationComments(comments, contextValidation);
		

	}

	
}
