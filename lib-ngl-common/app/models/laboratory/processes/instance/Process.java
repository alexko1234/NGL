package models.laboratory.processes.instance;

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
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import fr.cea.ig.DBObject;




@MongoCollection(name="Process")
public class Process extends DBObject {
	
	public String codeType;
	public String categoryType;
	
	public String stateCode;
	public String resolutionCode;
	
	public TraceInformation traceinformation;
	public List<Comment> comments;

	public Map<String,PropertyValue> properties;
	
	// Creating one process create many process instance there are sample on container selected
	public String aggregationKey;
	
	// User or Equipe
	//public List<String> equipeCode;
	
	// Projects ref
	public String projectCode;
	// Samples ref
	public String sampleCode;

	public String currentExperimentTypeCode;
	
	public String containerInputCode;
	//public List<String> containerOutCodes;

	
	@JsonIgnore
	public Container getInputContainer(){
		return new HelperObjects<Container>().getObject(Container.class, containerInputCode, null);
	}
	
	@JsonIgnore
	public Sample getSample(){
		return new HelperObjects<Sample>().getObject(Sample.class, sampleCode,null);
	}
	
	@JsonIgnore
	public ProcessType getProcessType(){
		return new HelperObjects<ProcessType>().getObject(ProcessType.class, codeType, null);
	}
	
	@JsonIgnore
	public ProcessCategory getProcessCategory(){
		return new HelperObjects<ProcessCategory>().getObject(ProcessCategory.class, categoryType, null);
	}
	
	@JsonIgnore
	public Project getProject(){
		return new HelperObjects<Project>().getObject(Project.class, projectCode,null);
	}
	
	@JsonIgnore
	public ExperimentType getCurrentOutExperimentType(){
		return new HelperObjects<ExperimentType>().getObject(ExperimentType.class, currentExperimentTypeCode,null);
	}
	
}
