package models.laboratory.experiment.instance;

import static validation.utils.ConstraintsHelper.validateTraceInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.project.instance.Project;
import models.laboratory.reagent.instance.ReagentUsed;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;
import validation.utils.BusinessValidationHelper;
import validation.utils.ConstraintsHelper;
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

	
	@JsonIgnore
	public final static String LEVEL_SEARCH_EXP=Level.CODE.Experiment.toString();

	@JsonIgnore
	public final static String LEVEL_SEARCH_INS=Level.CODE.Instrument.toString();

	
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
		
		if(this == null){
			throw new IllegalArgumentException("this is null");
		}
		
		if(this._id == null){
			validation.utils.BusinessValidationHelper.validateUniqueInstanceCode(errors, this.code, Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME);
		}
		
		validation.utils.BusinessValidationHelper.validateRequiredDescriptionCode(errors, this.stateCode, "stateCode", State.find);
		validation.utils.BusinessValidationHelper.validateRequiredDescriptionCode(errors, this.typeCode, "typeCode", ExperimentType.find);
		validation.utils.BusinessValidationHelper.validateRequiredDescriptionCode(errors, this.categoryCode, "categoryCode", ExperimentCategory.find);

		validation.utils.BusinessValidationHelper.validateExistDescriptionCode(errors, this.resolutionCode, "resolutionCode", Resolution.find);
		validation.utils.BusinessValidationHelper.validateExistDescriptionCode(errors, this.protocolCode, "protocolCode", Protocol.find);
		validation.utils.BusinessValidationHelper.validateExistDescriptionCode(errors, this.instrumentUsedTypeCode, "instrumentUsedTypeCode", InstrumentUsedType.find);

		validateTraceInformation(errors, this.traceInformation, this._id);
		
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(errors, this.typeCode, "typeCode", ExperimentType.find,true);
		InstrumentUsedType instrumentUsedType=BusinessValidationHelper.validateRequiredDescriptionCode(errors, this.instrumentUsedTypeCode,"instrumentUsedTypeCode", InstrumentUsedType.find,true);

		ConstraintsHelper.validatePropertiesforLevel(errors, this.experimentProperties, exType.propertiesDefinitions,"",LEVEL_SEARCH_EXP);
		ConstraintsHelper.validatePropertiesforLevel(errors, this.instrumentProperties, instrumentUsedType.propertiesDefinitions,"",LEVEL_SEARCH_INS);
		
		for(int i=0;i<atomicTransfertMethods.size();i++){
			atomicTransfertMethods.get(i).validate(errors);
		}
		
		instrument.validate(errors);
		
		for(ReagentUsed reagentUsed:reagentUseds){
			reagentUsed.validate(errors);
		}
					
	}

	
}
