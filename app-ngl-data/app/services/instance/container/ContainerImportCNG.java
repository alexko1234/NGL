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

/**
 * @author dnoisett
 * Import samples and container from CNG's LIMS to NGL 
 */

public class ContainerImportCNG extends AbstractImportDataCNG{

	public ContainerImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ContainerImportCNG",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {	
		
		// -1-  !!! les samples ne sont pas au sens NGL des containers bien qu'ils soient importés ici !!!
		//loadSamples();	 	
		//updateSamples();
		
		// -2- tubes
		// FDS a quoi correspond le 2 eme param "experiment-type-code"?   l'experience d'ou est est sensé venir le container ????
		// TODO lister les "experiment-type-code possibles pour des tubes venant de solexa...
		
		// lib-normalization= solexa[ lib10nM + libXnM >= 1nM ]
		loadContainers("tube","lib-normalization");
		//updateContainers("tube","lib-normalization");
		
		// denat-dil-lib = solexa[ libXnM < 1nM  ]
		loadContainers("tube","denat-dil-lib");
		//updateContainers("tube","denat-dil-lib");
		
		// TODO ???? autres categories de libraries en tube
		
		// -3-  TODO 17/06/2015 import des plaques96  10nM et XnM existant dans SOLEXA ???
		
		// si on importe des lanes c'est qu'elle ont ete cree par prepa-flowcell-cng...
		loadContainers("lane","prepa-flowcell-cng");
		//updateContainers("lane","prepa-flowcell-cng");
		
	}
	
	public void loadSamples() throws SQLException, DAOException {
		Logger.debug("Start loading samples");
		
		//-1- chargement depuis la base source Postgresql
		List<Sample> samples = limsServices.findSampleToCreate(contextError, null) ;
		
		//-2- sauvegarde dans la base cible MongoDb
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
		
		//-3- timestamp-er dans la base source Postgresql ce qui a été traité
		limsServices.updateLimsSamples(samps, contextError, "creation");
		
		Logger.debug("End loading samples");
	}
	
	public void updateSamples() throws SQLException, DAOException {
		Logger.debug("start updating samples");
		
		//-1- chargement depuis la base source Postgresql
		List<Sample>  samples = limsServices.findSampleToModify(contextError, null);
		
		//-2- trouver les samples concernés dans la base mongoDB et les supprimer
		for (Sample sample : samples) {
			Sample oldSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
			
			sample.traceInformation = InstanceHelpers.getUpdateTraceInformation(oldSample.traceInformation, "ngl-data");
			
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
		}
		
		//-3- sauvegarde dans la base cible MongoDb
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
		
		//-4- timestamp-er dans la base source Postgresql ce qui a été traité
		limsServices.updateLimsSamples(samps, contextError, "update");	
		
		Logger.debug("End updating samples");
	}
	
	public void loadContainers(String containerCategoryCode, String experimentTypeCode) throws SQLException, DAOException {
		Logger.debug("start loading containers of type:" + containerCategoryCode + " from experimenf type: "+ experimentTypeCode);		
		
		//-1- chargement depuis la base source Postgresql
		List<Container> containers = limsServices.findContainerToCreate(contextError, containerCategoryCode, experimentTypeCode );
		
		HashMap<String, PropertyValue<String>> mapCodeSupportSeq = null;
		
		if (containerCategoryCode.equals("lane")) {
			// propriété specifique aux containers "lanes"
			mapCodeSupportSeq = limsServices.setSequencingProgramTypeToContainerSupport(contextError, "creation");
		}
		
		//-2- création en mémoire
		ContainerHelper.createSupportFromContainers(containers, mapCodeSupportSeq, contextError);
		
		//-3- sauvegarde dans la base cible MongoDb
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		//-4- timestamp-er dans la base source Postresql ce qui a été transféré
		if (containerCategoryCode.equals("lane")) {
			limsServices.updateLimsLanes(ctrs, contextError, "creation");		
		}
		else {
			//"tube"
			limsServices.updateLimsTubes(ctrs, contextError, "creation");
		}
		//prévoir des well (plaques96) !!!!
			
		Logger.debug("end loading containers of type " + containerCategoryCode+ " from experimenf type: "+ experimentTypeCode);		
	}
	
	
	public void updateContainers(String containerCategoryCode, String experimentTypeCode) throws SQLException, DAOException {
		Logger.debug("start updating containers of type: " + containerCategoryCode+ " from experimenf type: "+ experimentTypeCode);		
		
		//-1- chargement depuis la base source Postgresql
		List<Container> containers = limsServices.findContainerToModify(contextError, containerCategoryCode,experimentTypeCode);
		
		HashMap<String, PropertyValue<String>> mapCodeSupportSeq = null;
		
		if (containerCategoryCode.equals("lane")) {
			// propriété specifique aux containers "lanes"
			mapCodeSupportSeq = limsServices.setSequencingProgramTypeToContainerSupport(contextError, "update");
		}
		
		//-2- Modifier les containers en mémoire
		ContainerHelper.updateSupportFromUpdatedContainers(containers, mapCodeSupportSeq, contextError);
		
		//-3- trouver les containers concernés dans la base mongoDB et les supprimer
		for (Container container : containers) {
			Container oldContainer = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
			
			container.traceInformation = InstanceHelpers.getUpdateTraceInformation(oldContainer.traceInformation, "ngl-data");
			
			MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
		}
		
		//-4- sauvegarde dans la base cible MongoDb
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		//-5- timestamp-er dans la base source Postresql ce qui a été traité
		if (containerCategoryCode.equals("lane")) {
			limsServices.updateLimsLanes(ctrs, contextError, "update");		
		}
		else {
			//"tube"
			limsServices.updateLimsTubes(ctrs, contextError, "update");
		}
		//prévoir les well (plaques96) !!!!
		
		Logger.debug("end updating containers of type: " + containerCategoryCode+ " from experimenf type: "+ experimentTypeCode);	
	}

}
