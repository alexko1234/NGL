package models.utils.instance;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;

import validation.ContextValidation;
import validation.container.instance.ContainerValidationHelper;
import validation.container.instance.SupportValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class ProcessHelper {

	public static void updateContainer(Container container, String typeCode, List<String> codes,ContextValidation contextValidation){
		if(container.fromExperimentTypeCodes == null || container.fromExperimentTypeCodes.size() == 0){
			container.fromExperimentTypeCodes = new ArrayList<String>();
			ProcessType processType;
			try {
				processType = ProcessType.find.findByCode(typeCode);
				container.fromExperimentTypeCodes.add(processType.voidExperimentType.code);
	
			} catch (DAOException e) {
				throw new RuntimeException();
			}
		}
		container.processTypeCode = typeCode;
		container.inputProcessCodes=InstanceHelpers.addCodesList(codes, container.inputProcessCodes);
		ContainerValidationHelper.validateProcessCodes(container.inputProcessCodes,contextValidation);
		ContainerValidationHelper.validateExperimentTypeCodes(container.fromExperimentTypeCodes, contextValidation);
		ContainerValidationHelper.validateProcessTypeCode(container.processTypeCode, contextValidation);
		if(!contextValidation.hasErrors()){
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
					DBQuery.is("code",container.code),
					DBUpdate.set("inputProcessCodes", container.inputProcessCodes)
							.set("processTypeCode", container.processTypeCode)
							.set("fromExperimentTypeCodes",container.fromExperimentTypeCodes));
		}
	}
	
	
	public static void updateContainerSupportFromContainer(Container container,ContextValidation contextValidation){
		ContainerSupport containerSupport=MongoDBDAO.findByCode(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, container.support.code);		
		containerSupport.fromExperimentTypeCodes=InstanceHelpers.addCodesList(container.fromExperimentTypeCodes, containerSupport.fromExperimentTypeCodes);
		SupportValidationHelper.validateExperimentTypeCodes(containerSupport.fromExperimentTypeCodes, contextValidation);
		if(!contextValidation.hasErrors()){
			MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME,ContainerSupport.class,
				DBQuery.is("code", container.support.code)
				,DBUpdate.set("fromExperimentTypeCodes",container.fromExperimentTypeCodes));
		}
	}


	public static void updateNewContainerSupportCodes(ContainerUsed outputContainerUsed,
			List<ContainerUsed> inputContainerUseds,Experiment experiment) {
		List<Query> queryOr = new ArrayList<Query>();
		queryOr.add(DBQuery.in("containerInputCode",ContainerUsedHelper.getContainerCodes(inputContainerUseds)));
		queryOr.add(DBQuery.in("newContainerSupportCodes",ContainerUsedHelper.getContainerSupportCodes(inputContainerUseds)));
		Query query=null;
		query=DBQuery.and(DBQuery.in("experimentCodes",experiment.code));
		if(queryOr.size()!=0){
			query=query.and(DBQuery.or(queryOr.toArray(new Query[queryOr.size()])));
		}

		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,query,
				DBUpdate.push("newContainerSupportCodes",outputContainerUsed.locationOnContainerSupport.code),true);

		
	}
	
	
	public static void updateNewContainerSupportCodes(List<ContainerUsed> outputContainerUseds,
			ContainerUsed inputContainerUsed,Experiment experiment) {
		List<Query> queryOr = new ArrayList<Query>();
		queryOr.add(DBQuery.is("containerInputCode",inputContainerUsed.code));
		queryOr.add(DBQuery.in("newContainerSupportCodes",inputContainerUsed.locationOnContainerSupport.code));
		Query query=null;
		query=DBQuery.and(DBQuery.in("experimentCodes",experiment.code));
		if(queryOr.size()!=0){
			query=query.and(DBQuery.or(queryOr.toArray(new Query[queryOr.size()])));
		}

		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,query,
				DBUpdate.pushAll("newContainerSupportCodes",ContainerUsedHelper.getContainerSupportCodes(outputContainerUseds)),true);

	}


	public static void updateNewContainerSupportCodes(ContainerUsed outputContainerUsed,
			ContainerUsed inputContainerUsed, Experiment experiment) {
		List<Query> queryOr = new ArrayList<Query>();
		Query query=null;
		String containerSupportCode=null;
		
		queryOr.add(DBQuery.is("containerInputCode",inputContainerUsed.code));
		
		if(inputContainerUsed.locationOnContainerSupport==null){
			containerSupportCode=inputContainerUsed.code;
		}else { 
			containerSupportCode=inputContainerUsed.locationOnContainerSupport.code;
		}
		
		queryOr.add(DBQuery.in("newContainerSupportCodes",containerSupportCode));
		
		query=DBQuery.and(DBQuery.in("experimentCodes",experiment.code));
		if(queryOr.size()!=0){
			query=query.and(DBQuery.or(queryOr.toArray(new Query[queryOr.size()])));
		}

		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,query,
				DBUpdate.push("newContainerSupportCodes",outputContainerUsed.locationOnContainerSupport.code),true);
		}

}
