package services.instance.container;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import play.Logger;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Support;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNG;
import validation.container.instance.ContainerValidationHelper;

public class ContainerImportCNG extends AbstractImportDataCNG{

	public ContainerImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ContainerImportCNG",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		
		logger.info("Start loading samples ..."); 
		
		List<Sample> samples = limsServices.findSampleToCreate(contextError, null) ;
		

		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
			
		limsServices.updateLimsSamples(samps, contextError);
		
		logger.info("End of load samples !");
		
		
		logger.info("Start loading containers ..."); 
		
		List<Container> containers = limsServices.findContainerToCreate(contextError) ;
		
		/***********************************************************************************/
		//MANAGE SUPPORT
		HashMap<String,Support> hmSupports = new HashMap<String,Support>();
		
		for (Container container : containers) {
			if (container.support != null) {
				Support support = ContainerValidationHelper.createSupport(container.support, container.projectCodes, container.sampleCodes);
				if (!hmSupports.containsKey(support.code)) {
					hmSupports.put(support.code, support);
				}
				else {
					Support oldSupport = (Support) hmSupports.get(support.code);
					List<String> oldProjectCodes = oldSupport.projectCodes;
					List<String> oldSampleCodes = oldSupport.sampleCodes;
					support.projectCodes = InstanceHelpers.addCodesList(support.projectCodes, oldProjectCodes); 
					support.sampleCodes = InstanceHelpers.addCodesList(support.sampleCodes, oldSampleCodes);
					//update the hashMap with the support up to date (in terms of projectCodes & sampleCodes)
					hmSupports.remove(support.code);
					hmSupports.put(support.code, support);
				}
				
			}
		}
		
		//update dataBase 
		InstanceHelpers.save(InstanceConstants.SUPPORT_COLL_NAME, new ArrayList<Support>(hmSupports.values()), contextError, true);
		/***********************************************************************************/
		
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		limsServices.updateLimsContainers(ctrs, contextError);
		
		logger.info("End of load containers !"); 
		
		logger.info("Start loading supports ...");
		
		
		logger.info("End of load supports !");

	}

}
