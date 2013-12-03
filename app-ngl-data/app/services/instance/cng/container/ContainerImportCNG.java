package services.instance.cng.container;

import java.sql.SQLException;
import java.util.List;

import play.Logger;

import models.laboratory.container.instance.Container;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportData;

public class ContainerImportCNG extends AbstractImportData{

	public ContainerImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super(durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		
		Logger.info("Start loading samples ..."); 
		
		List<Sample> samples = limsCNGServices.findSampleToCreate(contextError, null) ;
		

		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
			
		limsCNGServices.updateLimsSamples(samps, contextError);
		
		Logger.info("End of load samples !");
		
		
		Logger.info("Start loading containers ..."); 
		
		List<Container> containers = limsCNGServices.findContainerToCreate(contextError) ;

		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		limsCNGServices.updateLimsContainers(ctrs, contextError);
		
		Logger.info("End of load containers !"); 
	}

}
