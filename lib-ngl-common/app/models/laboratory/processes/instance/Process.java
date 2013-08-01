package models.laboratory.processes.instance;

import static validation.utils.ConstraintsHelper.addErrors;
import static validation.utils.ConstraintsHelper.getKey;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.DescriptionValidationHelper;
import validation.InstanceValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ContextValidation;
import fr.cea.ig.DBObject;




@MongoCollection(name="Process")
public class Process extends DBObject implements IValidation{
	
	public String typeCode;
	public String categoryCode;
	
	public String stateCode;
	public String resolutionCode;
	
	public TraceInformation traceInformation;
	public List<Comment> comments;

	public Map<String,PropertyValue> properties;
	
	// Creating one process create many process instance there are sample on container selected
	// public String aggregationKey; //? used containerInputCode as aggregationKey
	
	// User or Equipe
	//public List<String> equipeCode;
	
	// Projects ref
	public String projectCode;
	// Samples ref
	public String sampleCode;

	public String currentExperimentTypeCode;
	
	public String containerInputCode;
	
	
	@JsonIgnore
	public Container getInputContainer(){
		return new HelperObjects<Container>().getObject(Container.class, containerInputCode);
	}
	
	@JsonIgnore
	public Sample getSample(){
		return new HelperObjects<Sample>().getObject(Sample.class, sampleCode);
	}
	
	@JsonIgnore
	public ProcessType getProcessType(){
		return new HelperObjects<ProcessType>().getObject(ProcessType.class, typeCode);
	}
	
	@JsonIgnore
	public ProcessCategory getProcessCategory(){
		return new HelperObjects<ProcessCategory>().getObject(ProcessCategory.class, categoryCode);
	}
	
	@JsonIgnore
	public Project getProject(){
		return new HelperObjects<Project>().getObject(Project.class, projectCode);
	}
	
	@JsonIgnore
	public ExperimentType getCurrentOutExperimentType(){
		return new HelperObjects<ExperimentType>().getObject(ExperimentType.class, currentExperimentTypeCode);
	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		if(this == null){
			throw new IllegalArgumentException("Process is null");
		}

		contextValidation.contextObjects.put("_id",this._id);
		BusinessValidationHelper.validateUniqueInstanceCode(contextValidation, this.code, Process.class,InstanceConstants.CONTAINER_COLL_NAME);
		
		//??????????
		if(this._id == null){
			Container container = BusinessValidationHelper.validateRequiredInstanceCode(contextValidation.errors, this.containerInputCode,"containerInputCode",Container.class,InstanceConstants.CONTAINER_COLL_NAME,true);
			if(!container.stateCode.equals("A")){
				addErrors(contextValidation.errors,this.containerInputCode, getKey(null,"containerNotIWPOrN"));
			}
		}
		InstanceValidationHelper.validationSampleCode(sampleCode, contextValidation);
		InstanceValidationHelper.validationProjectCode(projectCode, contextValidation);
		InstanceValidationHelper.validationStateCode(stateCode, contextValidation);
		DescriptionValidationHelper.validationProcessTypeCode(typeCode,contextValidation);
		
		//TODO
	/*	ProcessType thisType = this.getProcessType();
		if(thisType != null && thisType.propertiesDefinitions != null && !thisType.propertiesDefinitions.isEmpty()){
			validateProperties(contextValidation.errors, this.properties, this.getProcessType().propertiesDefinitions, getKey(null,"nullPropertiesDefinitions"));
		}
	*/
		traceInformation.validate(contextValidation);
	}
	
}
