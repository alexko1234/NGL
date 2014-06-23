package models.laboratory.processes.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.InstanceConstants;
import org.mongojack.MongoCollection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.processes.instance.ProcessValidationHelper;
import fr.cea.ig.DBObject;




@MongoCollection(name="Process")
public class Process extends DBObject implements IValidation{
	
	public String typeCode;
	public String categoryCode;
	
	public State state;
	
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
		
		ProcessValidationHelper.validateId(this, contextValidation);
		ProcessValidationHelper.validateCode(this, InstanceConstants.PROCESS_COLL_NAME, contextValidation);
		ProcessValidationHelper.validateProcessType(typeCode,properties,contextValidation);
		ProcessValidationHelper.validateState(typeCode,state, contextValidation);
		ProcessValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		ProcessValidationHelper.validateContainerCode(containerInputCode, contextValidation);
		ProcessValidationHelper.validateProjectCode(projectCode, contextValidation);
		ProcessValidationHelper.validateSampleCode(sampleCode, projectCode, contextValidation);
	}
	
}
