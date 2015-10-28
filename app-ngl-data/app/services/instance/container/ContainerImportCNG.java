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
 * FDS remplacement de l'appel a Logger par logger
 */

public class ContainerImportCNG extends AbstractImportDataCNG{

	public ContainerImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ContainerImportCNG",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {	
		
		// -1-  !!! les samples ne sont pas au sens NGL des containers bien qu'ils soient importés ici !!!
		loadSamples();	 	
		updateSamples();
		
		// -2- tubes
		// FDS le 2 eme param "experiment-type-code" est l'experience d'ou est est sensé venir le container 
		//     le 3 eme param "importState" est le status du container a importer
	
		// lib-normalization= solexa[ lib10nM + libXnM >= 1nM ]
		// FDS 15/09/2015 pas encore..en prod
		
		//loadContainers("tube","lib-normalization","is");//uniquement pour la reprise du passif ; commenter en synchro normale
		loadContainers("tube","lib-normalization","iw-p");
		updateContainers("tube","lib-normalization"); // pas de specificite de status pour la mise a jour
		
		// NOTE: denat-dil-lib = solexa[ libXnM < 1nM  ]
		//loadContainers("tube","denat-dil-lib","is");//uniquement pour la reprise du passif ; commenter en synchro normale
		loadContainers("tube","denat-dil-lib","iw-p");
		updateContainers("tube","denat-dil-lib"); // pas de specificite de status pour la mise a jour
		
		// -3-  TODO  import des puits/plaques96  10nM et XnM 
		       
		/*  PAS ENCORE EN PROD.. prochaine version??
		loadContainers("plate-well","lib-normalization","iw-p");
		updateContainers("plate-well","lib-normalization");
		
		loadContainers("plate-well","denat-dil-lib","iw-p");
		updateContainers("plate-well","denat-dil-lib");
		*/
		
		// --4- lanes/flowcells
		/* a importer pendant la phase de transition: les flowcells sont crees dans Solexaprod
		   puis importees comme si elles avait ete crees par prepa-flowcell...
		   ==> a desactiver une fois la creation des Flowcells faites dans NGL 
		       pas de status sur une lane dans solexa...
		*/
		//loadContainers("lane","prepa-flowcell",null);
		//updateContainers("lane","prepa-flowcell");
	}
	
	public void loadSamples() throws SQLException, DAOException {
		Logger.debug("Start loading samples");
			
		//-1- chargement depuis la base source Postgresql
		Logger.debug("1/3 loading from source database...");
		List<Sample> samples = limsServices.findSampleToCreate(contextError, null) ;
		
		//-2- sauvegarde dans la base cible MongoDb
		Logger.debug("2/3 saving to dest database...");
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
		
		//-3- timestamp-er dans la base source Postgresql ce qui a été traité
		Logger.debug("3/3 updating source database...");
		limsServices.updateLimsSamples(samps, contextError, "creation");
		
		Logger.debug("End loading samples");
	}
	
	public void updateSamples() throws SQLException, DAOException {
		Logger.debug("start updating samples");
		
		//-1- chargement depuis la base source Postgresql
		Logger.debug("1/3 loading from source database...");
		List<Sample>  samples = limsServices.findSampleToModify(contextError, null);
		
		//-2a- trouver les samples concernés dans la base mongoDB et les supprimer
		Logger.debug("2a/3 delete from dest database...");
		for (Sample sample : samples) {
			Sample oldSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
			
			sample.traceInformation = InstanceHelpers.getUpdateTraceInformation(oldSample.traceInformation, "ngl-data");
			
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
		}
		
		//-2b- sauvegarde dans la base cible MongoDb
		Logger.debug("2b/3 saving to dest database...");
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
		
		//-3- timestamp-er dans la base source Postgresql ce qui a été traité
		Logger.debug("3/3 updating source database...");
		limsServices.updateLimsSamples(samps, contextError, "update");	
		
		Logger.debug("End updating samples");
	}
	
	// 22/10/2015 ajout parametre importState pour la reprise
	public void loadContainers(String containerCategoryCode, String experimentTypeCode, String importState) throws SQLException, DAOException {
		Logger.debug("Start loading containers of type:" + containerCategoryCode + " from experiment type: "+ experimentTypeCode);		
		
		//-1- chargement depuis la base source Postgresql
		List<Container> containers = limsServices.findContainerToCreate(contextError, containerCategoryCode, experimentTypeCode, importState);
		
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
			//"tube" OR "plate-well"
			limsServices.updateLimsTubes(ctrs, contextError, "creation");
		}
			
		Logger.debug("End loading containers of type " + containerCategoryCode+ " from experiment type: "+ experimentTypeCode);		
	}
	
	
	public void updateContainers(String containerCategoryCode, String experimentTypeCode) throws SQLException, DAOException {
		Logger.debug("Start updating containers of type: " + containerCategoryCode+ " from experiment type: "+ experimentTypeCode);		
		
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
			//"tube" OR "plate-well"
			limsServices.updateLimsTubes(ctrs, contextError, "update");
		}
		
		Logger.debug("End updating containers of type: " + containerCategoryCode+ " from experiment type: "+ experimentTypeCode);	
	}
}
