package services.instance.container;

import java.sql.SQLException;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNG;

public class ContainerImportCNG extends AbstractImportDataCNG{

	public ContainerImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ContainerImportCNG",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		
		Logger.info("Start loading samples ..."); 
		
		List<Sample> samples = limsServices.findSampleToCreate(contextError, null) ;
		

		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
			
		limsServices.updateLimsSamples(samps, contextError);
		
		Logger.info("End of load samples !");
		
		
		Logger.info("Start loading containers ..."); 
		
		List<Container> containers = limsServices.findContainerToCreate(contextError) ;

		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		limsServices.updateLimsContainers(ctrs, contextError);
		
		Logger.info("End of load containers !"); 
	}

}
