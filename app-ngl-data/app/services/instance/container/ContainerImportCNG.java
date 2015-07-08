package services.instance.container;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNG;
import play.Logger;

public class ContainerImportCNG extends AbstractImportDataCNG{

	public ContainerImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ContainerImportCNG",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {		
		//loadSamples();	 	
		updateSamples();
		
		//loadContainers("lane","prepa-flowcell-cng");
		updateContainers("lane","prepa-flowcell-cng");
		
		//loadContainers("tube","lib-normalization");
		updateContainers("tube","lib-normalization");
		
		// TODO ???? autres categories de libraries
		//loadContainers("tube","lib-XXX");
		///updateContainers("tube","lib-XXX");
	}
	
	
	
	public void loadSamples() throws SQLException, DAOException {
		Logger.debug("start loading samples");
		
		List<Sample> samples = limsServices.findSampleToCreate(contextError, null) ;
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
			
		limsServices.updateLimsSamples(samps, contextError, "creation");
		
		Logger.debug("end loading samples");
	}
	
	public void updateSamples() throws SQLException, DAOException {
		Logger.debug("start updating samples");
		
		List<Sample>  samples = limsServices.findSampleToModify(contextError, null);
		
		for (Sample sample : samples) {
			Sample oldSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
			
			sample.traceInformation = InstanceHelpers.getUpdateTraceInformation(oldSample.traceInformation, "ngl-data");
			
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
		}
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
			
		limsServices.updateLimsSamples(samps, contextError, "update");	
		
		Logger.debug("end updating samples");
	}
	
	public void loadContainers(String containerCategoryCode, String experimentTypeCode) throws SQLException, DAOException {
		Logger.debug("start loading containers of type " + containerCategoryCode);		
		
		List<Container> containers = limsServices.findContainerToCreate(contextError, containerCategoryCode, experimentTypeCode );
		
		HashMap<String, PropertyValue<String>> mapCodeSupportSeq = null;
		
		if (containerCategoryCode.equals("lane")) {
			mapCodeSupportSeq = limsServices.setSequencingProgramTypeToContainerSupport(contextError, "creation");
		}
		
		ContainerHelper.createSupportFromContainers(containers, mapCodeSupportSeq, contextError);
		
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		if (containerCategoryCode.equals("lane")) {
			limsServices.updateLimsLanes(ctrs, contextError, "creation");		
		}
		else {
			limsServices.updateLimsTubes(ctrs, contextError, "creation");
		}
		
		Logger.debug("end loading containers of type " + containerCategoryCode);		
	}
	
	
	public void updateContainers(String containerCategoryCode,String experimentTypeCode) throws SQLException, DAOException {
		Logger.debug("start updating containers of type " + containerCategoryCode);		
		
		List<Container> containers = limsServices.findContainerToModify(contextError, containerCategoryCode,experimentTypeCode);
		
		HashMap<String, PropertyValue<String>> mapCodeSupportSeq = null;
		
		if (containerCategoryCode.equals("lane")) {
			mapCodeSupportSeq = limsServices.setSequencingProgramTypeToContainerSupport(contextError, "update");
		}
		
		ContainerHelper.updateSupportFromUpdatedContainers(containers, mapCodeSupportSeq, contextError);
		
		for (Container container : containers) {
			Container oldContainer = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
			
			container.traceInformation = InstanceHelpers.getUpdateTraceInformation(oldContainer.traceInformation, "ngl-data");
			
			MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
		}
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		if (containerCategoryCode.equals("lane")) {
			limsServices.updateLimsLanes(ctrs, contextError, "update");		
		}
		else {
			limsServices.updateLimsTubes(ctrs, contextError, "update");
		}
		
		Logger.debug("end updating containers of type " + containerCategoryCode);	
	}
	

}
