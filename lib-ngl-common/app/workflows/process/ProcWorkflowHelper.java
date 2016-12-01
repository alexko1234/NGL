package workflows.process;

import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.api.modules.spring.Spring;
import fr.cea.ig.MongoDBDAO;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.container.ContWorkflows;

@Service
public class ProcWorkflowHelper {

	
	@Autowired
	ContWorkflows contWorkflows;
	
	public void updateContainerToStartProcess(ContextValidation contextValidation, Process process) {
		ProcessType processType = ProcessType.find.findByCode(process.typeCode);
		String voidExpTypeCode = processType.voidExperimentType.code;
		
		DBQuery.Query query = getContainerQuery(process);
		
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
				query,
				DBUpdate.addToSet("processCodes", process.code)
					.addToSet("processTypeCodes", process.typeCode)
					.set("contents.$.processProperties", process.properties));
		
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
				DBQuery.is("code",process.inputContainerCode).notExists("fromTransformationTypeCodes"),
				DBUpdate.addToSet("fromTransformationTypeCodes", voidExpTypeCode));
		
		
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,process.inputContainerCode);
		State nextState = new State();
		nextState.code=contWorkflows.getContainerStateFromExperimentCategory(processType.firstExperimentType.category.code);
		nextState.user = contextValidation.getUser();
		
		contextValidation.putObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT, "workflow");
		contextValidation.putObject(CommonValidationHelper.FIELD_UPDATE_CONTAINER_SUPPORT_STATE, Boolean.TRUE);
		contWorkflows.setState(contextValidation, container, nextState);
		
	}

	/**
	 * Query to retrieve container and content (using tag if exist)
	 * @param process
	 * @return
	 */
	private DBQuery.Query getContainerQuery(Process process) {
		DBQuery.Query query = DBQuery.is("code",process.inputContainerCode);
		
		if(process.sampleOnInputContainer.properties.containsKey("tag")){
			query.elemMatch("contents", DBQuery.is("sampleCode", process.sampleOnInputContainer.sampleCode)
												.is("projectCode",  process.sampleOnInputContainer.projectCode)
												.is("properties.tag.value", process.sampleOnInputContainer.properties.get("tag").value));
			
		}else{
			query.elemMatch("contents", DBQuery.is("sampleCode", process.sampleOnInputContainer.sampleCode).is("projectCode",  process.sampleOnInputContainer.projectCode));
			
		}
		
		return query;
	}

}
