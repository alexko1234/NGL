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
 * FDS 14/01/2016 desactivation import des lanes (plus necessaire depuis la mise en production NGL-SQ 10/2015)
 */

public class ContainerImportCNG extends AbstractImportDataCNG{

	public ContainerImportCNG (FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ContainerImportCNG",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {	
		
		// -1-  !!! les samples ne sont pas au sens NGL des containers bien qu'ils soient importés ici !!!
		//      ils sont necessaires avant tout import de containers...
	    loadSamples();	 	
	    updateSamples();
		
		// FDS: loadContainers: le 2 eme param "experiment-type-code" est l'experience d'ou est sensé venir le container 
		//                      le 3 eme param "importState" est le status du container a importer
		
		// -2- FDS 14-01-2016 NGL-909 ajouter l'import des plaques de samples SOLEXA 
		/* Ce sont les containers !!!
		   si on reprend la methode loadContainers comment alors distinger les plaques de sample des plaques de libraries ??
		     =>surcharger un peu le parametre containerCategoryCode
		       sample-well / library-well au lieu de simplement 'well'
		   12/04/2016: importer a l'etat "iw-p" au lieu de "is"
		*/	
	    loadContainers("sample-well",null,"iw-p"); //iw-p=in waiting processus
	    updateContainers("sample-well",null);
	    
		// -3- librairies en tube
		
		//-3.1- lib-normalization= solexa[ lib10nM + libXnM >= 1nM ]	
	    
	    loadContainers("tube","lib-normalization","is"); // is=in stock
	    loadContainers("tube","lib-normalization","iw-p"); //iw-p=in waiting processus
	    updateContainers("tube","lib-normalization"); // pas de specificite de status pour la mise a jour
		
		//-3.2- denat-dil-lib = solexa[ libXnM < 1nM  ]
	    
	    loadContainers("tube","denat-dil-lib","is"); //is=in stock
	    loadContainers("tube","denat-dil-lib","iw-p"); //iw-p=in waiting processus
	    updateContainers("tube","denat-dil-lib"); // pas de specificite de status pour la mise a jour
		
	    
		// -4- 15/05/2016 NGL-1044 : importer librairies en plaques-96 : lib-normalization et denat-dil-lib
	    
		//-4.1- lib-normalization = solexa[ lib10nM + libXnM >= 1nM ] ( !! attention probleme connu avec les puits WATER )
	    
		loadContainers("library-well","lib-normalization","iw-p"); // importer a l'etat iw-p 
		
		//pas testé la mise de plaques...
		//updateContainers("library-well","lib-normalization");
		
		//-4.2- denat-dil-lib = solexa[ libXnM < 0.06 nM  ]
		
		loadContainers("library-well","denat-dil-lib","iw-p"); // importer a l'etat iw-p 
		
		//pas testé la mise de plaques...
		//updateContainers("library-well","denat-dil-lib");	
		
	    /* 14/01/2016 desactivé puisque la creation des flowcells est faite dans NGL-SQ
		// -5- lanes/flowcell
		 * 
		loadContainers("lane","prepa-flowcell",null);
		updateContainers("lane","prepa-flowcell");
		*/
	}
	
	public void loadSamples() throws SQLException, DAOException {
		Logger.debug("Start LOADING samples");
			
		//-1- chargement depuis la base source Postgresql
		//Logger.debug("1/3 loading from source database...");
		List<Sample> samples = limsServices.findSampleToCreate(contextError, null) ;
		
		//-2- sauvegarde dans la base cible MongoDb
		//Logger.debug("2/3 saving to dest database...");
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
		
		//-3- timestamp-er dans la base source Postgresql ce qui a été traité
		//Logger.debug("3/3 updating source database...");
		limsServices.updateLimsSamples(samps, contextError, "creation");
		
		Logger.debug("End loading samples");
	}
	
	public void updateSamples() throws SQLException, DAOException {
		Logger.debug("start UPDATING samples");
		
		//-1- chargement depuis la base source Postgresql
		//Logger.debug("1/3 loading from source database...");
		List<Sample>  samples = limsServices.findSampleToModify(contextError, null);
		
		//-2a- trouver les samples concernés dans la base mongoDB et les supprimer
		//Logger.debug("2a/3 delete from dest database...");
		for (Sample sample : samples) {
			Sample oldSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
			
			sample.traceInformation = InstanceHelpers.getUpdateTraceInformation(oldSample.traceInformation, "ngl-data");
			
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
		}
		
		//-2b- sauvegarder les samples dans la base cible MongoDb
		//Logger.debug("2b/3 saving to dest database...");
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
		
		//-3- mise a jour dans la base source Postgresql ce qui a été traité
		//Logger.debug("3/3 updating source database...");
		limsServices.updateLimsSamples(samps, contextError, "update");
		
		Logger.debug("End updating samples");
	}
	
	// 22/10/2015 ajout parametre importState pour la reprise
	
	public void loadContainers(String containerCategoryCode, String experimentTypeCode, String importState) throws SQLException, DAOException {
		Logger.debug("Start loading containers of type:" + containerCategoryCode + " from experiment type: "+ experimentTypeCode);		
		
		//-1- chargement depuis la base source Postgresql
		List<Container> containers = limsServices.findContainerToCreate(contextError, containerCategoryCode, experimentTypeCode, importState);
		
		HashMap<String, PropertyValue<String>> mapCodeSupportSeq = null;
		
		/* 14/01/2016  on n'importe plus de lanes...
		if (containerCategoryCode.equals("lane")) {
			// propriété specifique aux containers "lanes"
			mapCodeSupportSeq = limsServices.setSequencingProgramTypeToContainerSupport(contextError, "creation");
		}
		*/
		
		//-2- création des containerSupports
		ContainerHelper.createSupportFromContainers(containers, mapCodeSupportSeq, contextError);
		
		//-3- sauvegarde dans la base cible MongoDb des containers
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		//-4- mise a jours dans la base source Postresql ce qui a été transféré
		// FDS 14/01/2016 differentiencier les cas sample-well, library-well 
		//                on n'importe plus les lanes
		/*if (containerCategoryCode.equals("lane")) {
			limsServices.updateLimsLanes(ctrs, contextError, "creation");		
		}
		else */
		if (containerCategoryCode.equals("tube")) {
			limsServices.updateLimsTubes(ctrs, contextError, "creation");
		}
		else if (containerCategoryCode.equals("sample-well")) {
			limsServices.updateLimsSamplePlates(ctrs, contextError, "creation");
		}
		else if (containerCategoryCode.equals("library-well")) {
			limsServices.updateLimsTubePlates(ctrs, contextError, "creation");
		}
		
		Logger.debug("End loading containers of type " + containerCategoryCode+ " from experiment type: "+ experimentTypeCode);		
	}
	
	
	public void updateContainers(String containerCategoryCode, String experimentTypeCode) throws SQLException, DAOException {
		Logger.debug("Start updating containers of type: " + containerCategoryCode+ " from experiment type: "+ experimentTypeCode);		
		
		//-1- chargement depuis la base source Postgresql
		List<Container> containers = limsServices.findContainerToModify(contextError, containerCategoryCode, experimentTypeCode);
		
		HashMap<String, PropertyValue<String>> mapCodeSupportSeq = null;
		
		/* 14/01/2016 on n'importe plus les lanes
		if (containerCategoryCode.equals("lane")) {
			mapCodeSupportSeq = limsServices.setSequencingProgramTypeToContainerSupport(contextError, "update");
		}
		*/
		
		//-2- Modifier les containersSupports
		ContainerHelper.updateSupportFromUpdatedContainers(containers, mapCodeSupportSeq, contextError);
		
		//-3- trouver les containers concernés dans la base mongoDB et les supprimer
		for (Container container : containers) {
			Container oldContainer = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
			
			container.traceInformation = InstanceHelpers.getUpdateTraceInformation(oldContainer.traceInformation, "ngl-data");
			
			MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
		}
		
		//-4- sauvegarder dans la base cible MongoDb les container modifiés
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		//-5- mise a jour dans la base source Postresql ce qui a été traité
		
		/* 14/01/2016 on ne traite plus les lanes
		if (containerCategoryCode.equals("lane")) {
			limsServices.updateLimsLanes(ctrs, contextError, "update");		
		}
		else */
		if  (containerCategoryCode.equals("tube")) {
			limsServices.updateLimsTubes(ctrs, contextError, "update");
		}
		else if (containerCategoryCode.equals("sample-well")) {
			limsServices.updateLimsSamplePlates(ctrs, contextError, "update");
		}
		else if (containerCategoryCode.equals("library-well")) {
			limsServices.updateLimsTubePlates(ctrs, contextError, "update");
		}
		
		Logger.debug("End updating containers of type: " + containerCategoryCode+ " from experiment type: "+ experimentTypeCode);	
	}
}
