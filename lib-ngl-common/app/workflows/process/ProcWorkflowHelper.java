package workflows.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;
import org.mongojack.DBUpdate.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.container.ContWorkflows;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;

@Service
public class ProcWorkflowHelper {

	
	public static final String TAG_PROPERTY_NAME = "tag";
	@Autowired
	ContWorkflows contWorkflows;
	
	public void updateInputContainerToStartProcess(ContextValidation contextValidation, Process process) {
		ProcessType processType = ProcessType.find.findByCode(process.typeCode);
		String voidExpTypeCode = processType.voidExperimentType.code;
		
		DBQuery.Query query = getInputContainerQuery(process);
		
		Builder builder = DBUpdate.addToSet("processCodes", process.code)
				.addToSet("processTypeCodes", process.typeCode);
				
		if(process.properties != null && process.properties.size() > 0){
			builder.set("contents.$.processProperties", process.properties);
		}
		
		if(process.comments != null && process.comments.size() > 0){
			builder.set("contents.$.processComments", process.comments);
		}
		
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
				query,
				builder);	
		
		
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
				DBQuery.is("code",process.inputContainerCode).or(DBQuery.notExists("fromTransformationTypeCodes"),DBQuery.size("fromTransformationTypeCodes", 0)),
				DBUpdate.addToSet("fromTransformationTypeCodes", voidExpTypeCode));
		
		
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,process.inputContainerCode);
		State nextState = new State();
		nextState.code=contWorkflows.getContainerStateFromExperimentCategory(processType.firstExperimentType.category.code);
		nextState.user = contextValidation.getUser();
		
		contextValidation.putObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT, "workflow");
		contextValidation.putObject(CommonValidationHelper.FIELD_UPDATE_CONTAINER_SUPPORT_STATE, Boolean.TRUE);
		contWorkflows.setState(contextValidation, container, nextState);
		
	}

	
	public void updateContentProcessPropertiesAttribute(ContextValidation validation, Process process) {
		if(process.properties != null && process.properties.size() > 0){
			
			//1 find tag inside inputContainer and all outputContainer if input does not have a tag
			String tag = getTagAssignFromProcessContainers(process);
			//2 find container with processProperties and tag if needed
			//DBQuery.Query query = getChildContainerQueryForProcessProperties(process, tag);			
			//3 update processProperties
			/*
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
					query,DBUpdate.set("contents.$.processProperties", process.properties));
			*/
			//TODO Problem when property disappeared after pool fusion, we had a new property
			List<String> containerCodes = new ArrayList<String>();
			containerCodes.add(process.inputContainerCode);
			if(null != process.outputContainerCodes){
				containerCodes.addAll(process.outputContainerCodes);
			}
			
			MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,  DBQuery.in("code", containerCodes).elemMatch("contents",DBQuery.exists("processProperties")))
			.cursor.forEach(container -> {
				container.traceInformation.setTraceInformation(validation.getUser());
				container.contents.stream()
					.filter(content -> ((process.sampleCodes.contains(content.sampleCode) && process.projectCodes.contains(content.projectCode) && !content.properties.containsKey(TAG_PROPERTY_NAME))
							|| (null != tag && process.sampleCodes.contains(content.sampleCode) && process.projectCodes.contains(content.projectCode) && content.properties.containsKey(TAG_PROPERTY_NAME) 
									&&  tag.equals(content.properties.get(TAG_PROPERTY_NAME).value))))
					.forEach(content -> {
						content.processProperties = process.properties;
						content.processComments = process.comments;	
						MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, getContentQuery(container, content), DBUpdate.set("contents.$", content));
					});
					
			});
			
		}
	}
	
	private Query getContentQuery(Container container, Content content) {
		Query query = DBQuery.is("code",container.code);
		
		Query contentQuery =  DBQuery.is("projectCode", content.projectCode).is("sampleCode", content.sampleCode);
		
		if(content.properties.containsKey(TAG_PROPERTY_NAME)){
			contentQuery.is("properties.tag.value", content.properties.get(TAG_PROPERTY_NAME).value);
		}
		query.elemMatch("contents", contentQuery);
		
		return query;
	}


	/**
	 * Find the tag assign during process or existing at the beginning of process
	 * @param process
	 * @return
	 */
	public String getTagAssignFromProcessContainers(Process process) {
		
		if(process.sampleOnInputContainer.properties.containsKey(TAG_PROPERTY_NAME)){
			return process.sampleOnInputContainer.properties.get(TAG_PROPERTY_NAME).value.toString();
		}else if(process.outputContainerCodes != null && process.outputContainerCodes.size() > 0){
			
			DBQuery.Query query = DBQuery.in("code",process.outputContainerCodes)
						.size("contents", 1)  //only one content is very important because we targeting the lib container and not a pool after lib prep.
						.elemMatch("contents", DBQuery.in("sampleCode", process.sampleCodes)
													.in("projectCode",  process.projectCodes)
													.exists("properties.tag"));
			
			MongoDBResult<Container> containersWithTag = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,query).sort("traceInformation.creationDate",Sort.ASC);
			if(containersWithTag.size() > 0){
				return containersWithTag.cursor.next().contents.get(0).properties.get(TAG_PROPERTY_NAME).value.toString();
			}else{
				return null;
			}
		}else{
			return null;
		}
	}


	public void updateContentPropertiesWithContentProcessProperties(ContextValidation validation, Process process) {
			//update output container with new process property values
		List<String> propertyCodes = getProcessesPropertyDefinitionCodes(process, Level.CODE.Content);
		
		if(null != process.outputContainerCodes && process.outputContainerCodes.size() > 0 
				&& process.properties != null && process.properties.size() > 0 && propertyCodes.size() > 0){
			Map<String,PropertyValue> updatedProperties = process.properties.entrySet()
														.stream()
														.filter(e -> propertyCodes.contains(e.getKey()))
														.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));		
		
			//1 find tag inside inputContainer and all outputContainer if input does not have a tag
			String tag = getTagAssignFromProcessContainers(process);
			
			Logger.debug("UpdateProperties for tag "+tag);
			
			//2 update content properties
			MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,  DBQuery.in("code", process.outputContainerCodes))
			.cursor.forEach(container -> {
				container.traceInformation.setTraceInformation(validation.getUser());
				container.contents.stream()
					.filter(content -> ((process.sampleCodes.contains(content.sampleCode) && process.projectCodes.contains(content.projectCode) && !content.properties.containsKey(TAG_PROPERTY_NAME))
							|| (null != tag && process.sampleCodes.contains(content.sampleCode) && process.projectCodes.contains(content.projectCode) && content.properties.containsKey(TAG_PROPERTY_NAME) 
									&&  tag.equals(content.properties.get(TAG_PROPERTY_NAME).value))))
					.forEach(content -> {
						content.properties.replaceAll((k,v) -> (updatedProperties.containsKey(k))?updatedProperties.get(k):v);							
						updatedProperties.forEach((k,v)-> content.properties.putIfAbsent(k, v));
						
						MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, getContentQuery(container, content), DBUpdate.set("contents.$", content));						
					});								
			});
		
			//update readsets with new process property values
			MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,	DBQuery.in("sampleOnContainer.containerCode", process.outputContainerCodes).in("sampleCode", process.sampleCodes).in("projectCode", process.projectCodes))
				.cursor
				.forEach(readset -> {
					if(!readset.sampleOnContainer.properties.containsKey(TAG_PROPERTY_NAME)
							|| (null != tag && readset.sampleOnContainer.properties.containsKey(TAG_PROPERTY_NAME) 
							&&  tag.equals(readset.sampleOnContainer.properties.get(TAG_PROPERTY_NAME).value))){
						readset.traceInformation.setTraceInformation(validation.getUser());
						readset.sampleOnContainer.properties.replaceAll((k,v) -> (updatedProperties.containsKey(k))?updatedProperties.get(k):v);
						updatedProperties.forEach((k,v)-> readset.sampleOnContainer.properties.putIfAbsent(k, v));
						MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readset);
					}
			});			
			
			
			//update processes with new exp property values
			MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class, DBQuery.in("sampleOnInputContainer.containerCode", process.outputContainerCodes).in("sampleOnInputContainer.sampleCode", process.sampleCodes).in("sampleOnInputContainer.projectCode", process.projectCodes))
			.cursor
			.forEach(otherProcess -> {
				if(!otherProcess.sampleOnInputContainer.properties.containsKey(TAG_PROPERTY_NAME)
						|| (null != tag && otherProcess.sampleOnInputContainer.properties.containsKey(TAG_PROPERTY_NAME) 
						&&  tag.equals(otherProcess.sampleOnInputContainer.properties.get(TAG_PROPERTY_NAME).value))){
					otherProcess.traceInformation.setTraceInformation(validation.getUser());
					otherProcess.sampleOnInputContainer.properties.replaceAll((k,v) -> (updatedProperties.containsKey(k))?updatedProperties.get(k):v);
					updatedProperties.forEach((k,v)-> otherProcess.sampleOnInputContainer.properties.putIfAbsent(k, v));
					MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, otherProcess);
				}
			});
		}
	}
	
	
	private List<String> getProcessesPropertyDefinitionCodes(Process process, Level.CODE level) {		
		ProcessType processType = ProcessType.find.findByCode(process.typeCode);
		return processType.getPropertyDefinitionByLevel(level)
				.stream()
				.map(pd -> pd.code)
				.collect(Collectors.toList());		
	}
	
	
	
	/**
	 * Query to retrieve container and content (using tag if exist)
	 * @param process
	 * @return
	 */
	private DBQuery.Query getInputContainerQuery(Process process) {
		DBQuery.Query query = DBQuery.is("code",process.inputContainerCode);
		if(process.sampleOnInputContainer.properties.containsKey(TAG_PROPERTY_NAME)){
			query.elemMatch("contents", DBQuery.is("sampleCode", process.sampleOnInputContainer.sampleCode)
												.is("projectCode",  process.sampleOnInputContainer.projectCode)
												.is("properties.tag.value", process.sampleOnInputContainer.properties.get(TAG_PROPERTY_NAME).value));
			
		}else{
			query.elemMatch("contents", DBQuery.is("sampleCode", process.sampleOnInputContainer.sampleCode).is("projectCode",  process.sampleOnInputContainer.projectCode));
			
		}
		
		return query;
	}

}
