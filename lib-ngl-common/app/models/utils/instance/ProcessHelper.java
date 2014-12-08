package models.utils.instance;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.processes.description.ProcessType;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import fr.cea.ig.MongoDBDAO;

public class ProcessHelper {

	public static void updateContainer(Container container, String typeCode, List<String> codes){
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
		MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME,container);
	}
	
	
	public static void updateContainerSupportFromContainer(Container container){
		ContainerSupport containerSupport=MongoDBDAO.findByCode(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, container.support.code);		
		containerSupport.fromExperimentTypeCodes=InstanceHelpers.addCodesList(container.fromExperimentTypeCodes, containerSupport.fromExperimentTypeCodes);
		
		MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME,ContainerSupport.class,
				DBQuery.is("code", container.support.code)
				,DBUpdate.set("fromExperimentTypeCodes",container.fromExperimentTypeCodes));
	}

}
