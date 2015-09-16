package models.laboratory.processes.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.utils.InstanceConstants;

import org.mongojack.MongoCollection;

import validation.ContextValidation;
import validation.IValidation;
import validation.processes.instance.ProcessValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.DBObject;




@MongoCollection(name="Process")
public class Process extends DBObject implements IValidation{

	public String typeCode;
	public String categoryCode;

	public State state;

	public TraceInformation traceInformation;
	public List<Comment> comments = new ArrayList<Comment>(0);

	public Map<String,PropertyValue> properties;

	// Projects ref
	public String projectCode;
	// Samples ref
	public String sampleCode;

	public String currentExperimentTypeCode;
	public String containerInputCode;

	public Set<String> newContainerSupportCodes;
	public Set<String> experimentCodes;

	public SampleOnInputContainer sampleOnInputContainer;

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {

		ProcessValidationHelper.validateId(this, contextValidation);
		ProcessValidationHelper.validateCode(this, InstanceConstants.PROCESS_COLL_NAME, contextValidation);
		ProcessValidationHelper.validateProcessType(typeCode,properties,contextValidation);
		ProcessValidationHelper.validateProcessCategory(categoryCode,contextValidation);
		ProcessValidationHelper.validateState(typeCode,state, contextValidation);
		ProcessValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		ProcessValidationHelper.validateContainerCode(containerInputCode, contextValidation);
		ProcessValidationHelper.validateProjectCode(projectCode, contextValidation);
		ProcessValidationHelper.validateSampleCode(sampleCode, projectCode, contextValidation);
		ProcessValidationHelper.validateCurrentExperimentTypeCode(currentExperimentTypeCode,contextValidation);		
		ProcessValidationHelper.validateSampleOnInputContainer(sampleOnInputContainer, contextValidation);


		//ProcessValidationHelper.validateExperimentCodes(experimentCodes, contextValidation);
	}

}
