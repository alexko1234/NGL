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
		//updateSamples();
		
		// FDS a quoi correspond le 2 eme param "experiment-type-code"?   l'experience d'ou est est sensé venir le container  ????
		// TODO lister les "experiment-type-code possibles...
		
		//loadContainers("tube","lib-normalization");
		//updateContainers("tube","lib-normalization");
		
		// TODO autres categories de libraries en tube => denat-dil-lib [ lib XnM < 1nM ( 2pM....) ]
		loadContainers("tube","denat-dil-lib");
		///updateContainers("tube","denat-dil-lib");
		
		// TODO ???? autres categories de libraries en tube
		
		// 17/06/2015il faut prévoir aussi l'import des plaques96  XnM !!!existant dans SOLEXA
		
		
		// si on importe des lanes c'est qu'elle ont ete cree par prepa-flowcell-cng...
		//loadContainers("lane","prepa-flowcell-cng");
		//updateContainers("lane","prepa-flowcell-cng");
		
	}
	
	
	
	public void loadSamples() throws SQLException, DAOException {
		Logger.debug("Start loading samples");
		
		//-1- chargement depuis la base source Postgresql
		List<Sample> samples = limsServices.findSampleToCreate(contextError, null) ;
		
		//-2- sauvegarde dans la base cible MongoDb
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
		
		//-3- tagger dans la base source Postgresql ce qui a été traité
		limsServices.updateLimsSamples(samps, contextError, "creation");
		
		Logger.debug("End loading samples");
	}
	
	public void updateSamples() throws SQLException, DAOException {
		Logger.debug("start updating samples");
		
		//-1- chargement depuis la base source Postgresql
		List<Sample>  samples = limsServices.findSampleToModify(contextError, null);
		
		//- trouver les samples concernés dans la base mongoDB et les supprimer
		for (Sample sample : samples) {
			Sample oldSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
			
			sample.traceInformation = InstanceHelpers.getUpdateTraceInformation(oldSample.traceInformation, "ngl-data");
			
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
		}
		
		//-2- sauvegarde dans la base cible MongoDb
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
		
		//-3- tagger dans la base source Postgresql ce qui a été traité
		limsServices.updateLimsSamples(samps, contextError, "update");	
		
		Logger.debug("End updating samples");
	}
	
	public void loadContainers(String containerCategoryCode, String experimentTypeCode) throws SQLException, DAOException {
		Logger.debug("Start loading containers of type :" + containerCategoryCode + "from experimenf type :"+ experimentTypeCode);		
		
		//-1- chargement depuis la base source Postgresql
		List<Container> containers = limsServices.findContainerToCreate(contextError, containerCategoryCode, experimentTypeCode );
		
		HashMap<String, PropertyValue<String>> mapCodeSupportSeq = null;
		
		if (containerCategoryCode.equals("lane")) {
			// propriété specifique aux containers "lanes"
			mapCodeSupportSeq = limsServices.setSequencingProgramTypeToContainerSupport(contextError, "creation");
		}
		
		ContainerHelper.createSupportFromContainers(containers, mapCodeSupportSeq, contextError);
		
		//-2- sauvegarde dans la base cible MongoDb
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		//-3- tagger dans la base source Postresql ce qui a été transféré
		if (containerCategoryCode.equals("lane")) {
			limsServices.updateLimsLanes(ctrs, contextError, "creation");		
		}
		else {
			//"tube"
			limsServices.updateLimsTubes(ctrs, contextError, "creation");
		}
		//prévoir des well (plaques96) !!!!
			
		Logger.debug("End loading containers of type " + containerCategoryCode);		
	}
	
	
	public void updateContainers(String containerCategoryCode, String experimentTypeCode) throws SQLException, DAOException {
		Logger.debug("start updating containers of type " + containerCategoryCode);		
		
		//-1- chargement depuis la base source Postgresql
		List<Container> containers = limsServices.findContainerToModify(contextError, containerCategoryCode,experimentTypeCode);
		
		HashMap<String, PropertyValue<String>> mapCodeSupportSeq = null;
		
		if (containerCategoryCode.equals("lane")) {
			// propriété specifique aux containers "lanes"
			mapCodeSupportSeq = limsServices.setSequencingProgramTypeToContainerSupport(contextError, "update");
		}
		
		ContainerHelper.updateSupportFromUpdatedContainers(containers, mapCodeSupportSeq, contextError);
		
		//- trouver les containers concernés dans la base mongoDB et les supprimer
		for (Container container : containers) {
			Container oldContainer = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
			
			container.traceInformation = InstanceHelpers.getUpdateTraceInformation(oldContainer.traceInformation, "ngl-data");
			
			MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
		}
		
		//-2- sauvegarde dans la base cible MongoDb
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		//-3- tagger dans la base source Postresql ce qui a été traité
		if (containerCategoryCode.equals("lane")) {
			limsServices.updateLimsLanes(ctrs, contextError, "update");		
		}
		else {
			//"tube"
			limsServices.updateLimsTubes(ctrs, contextError, "update");
		}
		//prévoir les well (plaques96) !!!!
		
		Logger.debug("end updating containers of type " + containerCategoryCode);	
	}

}
