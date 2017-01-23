package services.instance.resolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.typesafe.config.ConfigFactory;

import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionCategory;
import models.laboratory.resolutions.instance.ResolutionConfiguration;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import play.Logger.ALogger;
import services.instance.InstanceFactory;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

/**
 * Create Resolutions : for more flexibility, these data are created in a specific collection (in MongoDB) 
 * instead of being created in the description database
 * 23-06-2014  
 * @author dnoisett
 *
 */
public class ResolutionService {
	

	private static final ALogger logger = Logger.of("ResolutionService");
	private static HashMap<String, ResolutionCategory> resolutionCategories; 
	
	// FDS 15/01 reecriture...
	public static void main(ContextValidation ctx) {	
		
		String inst=play.Play.application().configuration().getString("institute");
		
		if ( inst.equals("CNS") || inst.equals("CNG") || inst.equals("TEST") ) {
			Logger.info("Create and save "+inst+ " resolution categories ...");
			
			saveResolutions(ctx, inst);
			Logger.info(inst+" Resolution collection creation is done!");
		}
		else {
			Logger.error("You need to specify only one institute !");
		}
		
		ctx.displayErrors(logger);
	}
	
	// FDS 15/01: fusion en 1 seule methode avec parametre inst
	// FDS 20/01: ajout creation des categories
	// FDS 23/11/2016 methodes distinctes par institut pour ProcessResolution
	public static void saveResolutions(ContextValidation ctx, String inst) {	
		if ( inst.equals("CNG") ){
			resolutionCategories = createResolutionCategoriesCNG();
			
			createRunResolutionCNG(ctx); 
			createReadSetResolutionCNG(ctx); 
			// FDS 15/01: no Analysis Resolutions ???
			createIlluminaPrepFCDepotResolutionCNG(ctx);
			createPrepPcrFreeResolutionCNG(ctx);
			createQCMiseqResolutionCNG(ctx);
			createExperimentResolution(ctx); // ajoute les resolutions par defaut sur toutes les experiences
			createProcessResolutionCNG(ctx);
		}
		else if ( inst.equals("CNS") ){			
			resolutionCategories = createResolutionCategoriesCNS();
			
			createRunResolutionCNS(ctx); 
			createReadSetResolutionCNS(ctx); 
			createAnalysisResolutionCNS(ctx); 
			createOpgenDepotResolutionCNS(ctx);
			createIlluminaPrepFCDepotResolutionCNS(ctx);
			createIryPreparationNLRSResolutionCNS(ctx);
			createDepotBionanoResolutionCNS(ctx);
			createSamplePrepResolutionCNS(ctx);
			createGelMigrationResolutionCNS(ctx);
			createExperimentResolution(ctx); // ajoute les resolutions par defaut sur toutes les experiences
			createProcessResolutionCNS(ctx);
			createContainerResolutionCNS(ctx);
		}
		else if ( inst.equals("TEST") ){		
			resolutionCategories = createResolutionCategoriesCNS();
			
			createExperimentResolution(ctx); 
			createProcessResolutionCNS(ctx); // on met CNG ou CNS???
		}
	}


	// FDS 20/01 retour aux 2 methodes initiales, mais correction pour CNG: ajout    resoCategories.put("Default",...
	public static HashMap<String, ResolutionCategory> createResolutionCategoriesCNG(){	
		HashMap<String, ResolutionCategory> resoCategories = new HashMap<String, ResolutionCategory>();
		
		//Run
		resoCategories.put("SAV", new ResolutionCategory("Problème qualité : SAV", (short) 10)); //10 for CNG only
		resoCategories.put("PbM", new ResolutionCategory("Problème machine", (short) 20));
		resoCategories.put("PbR", new ResolutionCategory("Problème réactifs", (short) 30)); 
		resoCategories.put("LIB", new ResolutionCategory("Problème librairie", (short) 50)); 
		resoCategories.put("PbI", new ResolutionCategory("Problème informatique", (short) 60));
		resoCategories.put("RUN-Info", new ResolutionCategory("Informations", (short) 70)); 
		resoCategories.put("QC", new ResolutionCategory("Observations QC", (short) 80));
		
		//ReadSet
		resoCategories.put("Run", new ResolutionCategory("Problème run", (short) 5));
		resoCategories.put("Qte", new ResolutionCategory("Problème quantité", (short) 15));
		resoCategories.put("IND", new ResolutionCategory("Problème indexing", (short) 20));
		resoCategories.put("Qlte", new ResolutionCategory("Problème qualité", (short) 25));
		resoCategories.put("MAP", new ResolutionCategory("Problème mapping", (short) 40));
		resoCategories.put("Sample", new ResolutionCategory("Problème échantillon", (short) 55));
		resoCategories.put("LIMS", new ResolutionCategory("Problème déclaration LIMS", (short) 60));
		resoCategories.put("Info", new ResolutionCategory("Informations", (short) 65));	
		
		//Analysis
		
		//Experiment
		
		resoCategories.put("Default", new ResolutionCategory("Default", (short) 0));
		
		return resoCategories;
	}
	

	public static HashMap<String, ResolutionCategory> createResolutionCategoriesCNS(){	
		HashMap<String, ResolutionCategory> resoCategories = new HashMap<String, ResolutionCategory>();
		//Run
		resoCategories.put("PbM", new ResolutionCategory("Problème machine", (short) 20));
		resoCategories.put("PbR", new ResolutionCategory("Problème réactifs", (short) 30)); 
		resoCategories.put("SAV", new ResolutionCategory("Problème qualité : SAV", (short) 40)); //40 for CNS only
		resoCategories.put("PbI", new ResolutionCategory("Problème informatique", (short) 60));
		resoCategories.put("Info", new ResolutionCategory("Informations", (short) 70));
		
		//ReadSet
		resoCategories.put("Run", new ResolutionCategory("Problème run", (short) 5));
		resoCategories.put("LIB", new ResolutionCategory("Problème librairie", (short) 10));
		resoCategories.put("Qte", new ResolutionCategory("Problème quantité", (short) 15));
		resoCategories.put("IND", new ResolutionCategory("Problème indexing", (short) 20));
		resoCategories.put("Qlte", new ResolutionCategory("Problème qualité", (short) 25));
		resoCategories.put("TAXO", new ResolutionCategory("Problème taxon", (short) 30));
		resoCategories.put("RIBO", new ResolutionCategory("Problème ribosomes", (short) 35));
		resoCategories.put("MAP", new ResolutionCategory("Problème mapping", (short) 40));
		resoCategories.put("MERG", new ResolutionCategory("Problème merging", (short) 45));
		resoCategories.put("Info", new ResolutionCategory("Informations", (short) 50));     //FDS 20/01/15   doublon ?????
		
		//Analysis
		resoCategories.put("BA-MERG", new ResolutionCategory("Merging", (short) 10)); 
		resoCategories.put("CTG", new ResolutionCategory("Contigage", (short) 20));
		resoCategories.put("SIZE", new ResolutionCategory("Size Filter", (short) 30));
		resoCategories.put("SCAFF", new ResolutionCategory("Scaffolding", (short) 40));
		resoCategories.put("GAP", new ResolutionCategory("Gap Closing", (short) 50));
		
		//Experiment	
		
		resoCategories.put("Default", new ResolutionCategory("Default", (short) 0));
		
		return resoCategories;
	}


	
	/* sub-methods */
	
	public static void createRunResolutionCNG(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		// FDS 16/01: rendre moins verbeux avec variables XXrC
		
		// PbM
		ResolutionCategory PbMrC= resolutionCategories.get("PbM");		
				
		l.add(InstanceFactory.newResolution("Indéterminé","PbM-indetermine", PbMrC, (short) 1));	
		l.add(InstanceFactory.newResolution("Chiller","PbM-chiller", PbMrC, (short) 2));
		l.add(InstanceFactory.newResolution("Pelletier","PbM-pelletier", PbMrC, (short) 3));
		l.add(InstanceFactory.newResolution("Fluidique","PbM-fluidiq", PbMrC, (short) 4));
		l.add(InstanceFactory.newResolution("Laser","PbM-laser", PbMrC, (short) 5));
		l.add(InstanceFactory.newResolution("Camera","PbM-camera", PbMrC, (short) 6));
		l.add(InstanceFactory.newResolution("Focus","PbM-focus", PbMrC, (short) 7));
		l.add(InstanceFactory.newResolution("Pb de vide","PbM-pbVide", PbMrC, (short) 8));
		l.add(InstanceFactory.newResolution("PE module","PbM-PEmodule", PbMrC, (short) 9));
		l.add(InstanceFactory.newResolution("Zone de dépôt","PbM-zoneDepot", PbMrC, (short) 10));				
		l.add(InstanceFactory.newResolution("cBot","PbM-cBot", PbMrC, (short) 11));		
		
		// SAV
		ResolutionCategory SAVrC= resolutionCategories.get("SAV");	
		
		l.add(InstanceFactory.newResolution("Intensité","SAV-intensite", SAVrC, (short) 1));
		l.add(InstanceFactory.newResolution("Intensité faible A","SAV-intFbleA", SAVrC, (short) 2));
		l.add(InstanceFactory.newResolution("Intensité faible T","SAV-intFbleT", SAVrC, (short) 3));
		l.add(InstanceFactory.newResolution("Intensité faible C","SAV-intFbleC", SAVrC, (short) 4));
		l.add(InstanceFactory.newResolution("Intensité faible G","SAV-intFbleG", SAVrC, (short) 5));
		l.add(InstanceFactory.newResolution("Densité clusters trop élevée","SAV-densiteElevee", SAVrC, (short) 6));
		l.add(InstanceFactory.newResolution("Densité clusters trop faible","SAV-densiteFaible", SAVrC, (short) 7));
		l.add(InstanceFactory.newResolution("Densité clusters nulle","SAV-densiteNulle", SAVrC, (short) 8));
		l.add(InstanceFactory.newResolution("%PF","SAV-PF", SAVrC, (short) 9));
		l.add(InstanceFactory.newResolution("Phasing","SAV-phasing", SAVrC, (short) 10));
		l.add(InstanceFactory.newResolution("Prephasing","SAV-prephasing", SAVrC, (short) 11));
		l.add(InstanceFactory.newResolution("Error rate","SAV-errorRate", SAVrC, (short) 12));
		l.add(InstanceFactory.newResolution("Focus","SAV-focus", SAVrC, (short) 13));
		l.add(InstanceFactory.newResolution("Q30","SAV-Q30", SAVrC, (short) 14));
		l.add(InstanceFactory.newResolution("% bases déséquilibré","SAV-perctBasesDeseq", SAVrC, (short) 15));
		l.add(InstanceFactory.newResolution("Index non représenté","SAV-indexNonPresent", SAVrC, (short) 16));
		l.add(InstanceFactory.newResolution("Index sous-représenté","SAV-indexFblePerc", SAVrC, (short) 17));
		l.add(InstanceFactory.newResolution("Indexing / demultiplexage","SAV-IndDemultiplex", SAVrC, (short) 18));
		
		//PbR
		ResolutionCategory PbRrC= resolutionCategories.get("PbR");
			
		l.add(InstanceFactory.newResolution("Indéterminé","PbR-indetermine", PbRrC, (short) 1));
		l.add(InstanceFactory.newResolution("Flowcell","PbR-FC", PbRrC, (short) 2));
		l.add(InstanceFactory.newResolution("cBot","PbR-cBot", PbRrC, (short) 3));
		l.add(InstanceFactory.newResolution("Séquencage","PbR-sequencage", PbRrC, (short) 4));
		l.add(InstanceFactory.newResolution("Indexing","PbR-indexing", PbRrC, (short) 5));
		l.add(InstanceFactory.newResolution("PE module","PbR-PEmodule", PbRrC, (short) 6));
		l.add(InstanceFactory.newResolution("Rehyb primer R1","PbR-rehybR1", PbRrC, (short) 7));
		l.add(InstanceFactory.newResolution("Rehyb primer R2","PbR-rehybR2", PbRrC, (short) 8));
		l.add(InstanceFactory.newResolution("Erreur réactifs","PbR-erreurReac", PbRrC, (short) 9));
		l.add(InstanceFactory.newResolution("Rajout réactifs","PbR-ajoutReac", PbRrC, (short) 10));
		
		//LIB
		ResolutionCategory LIBRc= resolutionCategories.get("LIB");
		
		l.add(InstanceFactory.newResolution("Construction librairie","LIB-construction", LIBRc, (short) 1));
		l.add(InstanceFactory.newResolution("Cause profil : librairie","LIB-profilIntLib", LIBRc, (short) 2));
		l.add(InstanceFactory.newResolution("Cause profil : exp type","LIB-profilIntExpType", LIBRc, (short) 3));
		l.add(InstanceFactory.newResolution("Pb dilution","LIB-pbDilution", LIBRc, (short) 4));
		l.add(InstanceFactory.newResolution("Pb dilution spike-In","LIB-pbDilSpikeIn", LIBRc, (short) 5));
		
		//PbI
		ResolutionCategory PbIrC= resolutionCategories.get("PbI");		
		
		l.add(InstanceFactory.newResolution("Indéterminé","PbI-indetermine", PbIrC, (short) 1));
		l.add(InstanceFactory.newResolution("PC","PbI-PC", PbIrC, (short) 2));
		l.add(InstanceFactory.newResolution("Ecran","PbI-ecran", PbIrC, (short) 3));
		l.add(InstanceFactory.newResolution("Espace disq insuf","PbI-espDisqInsuf", PbIrC, (short) 4));
		l.add(InstanceFactory.newResolution("Logiciel","PbI-logiciel", PbIrC, (short) 5));
		l.add(InstanceFactory.newResolution("Reboot PC","PbI-rebootPC", PbIrC, (short) 6));
		l.add(InstanceFactory.newResolution("Retard robocopy","PbI-robocopy", PbIrC, (short) 7));
		l.add(InstanceFactory.newResolution("Erreur paramétrage run","PbI-parametrageRun", PbIrC, (short) 8));
		
		//RUN-Info
		ResolutionCategory RUNInforC= resolutionCategories.get("RUN-Info");
			
		l.add(InstanceFactory.newResolution("Run de validation","Info-runValidation", RUNInforC, (short) 1));
		l.add(InstanceFactory.newResolution("Remboursement","Info-remboursement", RUNInforC, (short) 2));

		//QC
		ResolutionCategory QCRc= resolutionCategories.get("QC");
		
		l.add(InstanceFactory.newResolution("Intensité B.M.S","QC-intBMS", QCRc, (short) 1));
		l.add(InstanceFactory.newResolution("Tiles out","QC-tilesOut", QCRc, (short) 2));
		l.add(InstanceFactory.newResolution("Saut de chimie","QC-sautChimie", QCRc, (short) 3));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "runReso";
		r.resolutions = l;
		r.objectTypeCode = "Run";
		ArrayList<String> al = new ArrayList<String>();
		al.add("RHS2000");
		al.add("RHS2500");
		al.add("RHS2500R");
		al.add("RHS4000");
		al.add("RHSX");
		al.add("RMISEQ");
		al.add("RNEXTSEQ500");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "runReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	
	public static void createReadSetResolutionCNG(ContextValidation ctx) {	
		List<Resolution> l = new ArrayList<Resolution>();
		
		// FDS 16/01 rendre moins verbeux avec variables XXrC

		// Run
		l.add(InstanceFactory.newResolution("Lane abandonnée","Run-abandonLane", resolutionCategories.get("Run"), (short) 1));
		
		//Qte
		l.add(InstanceFactory.newResolution("Nb seq brutes faible","Qte-seqRawInsuf", resolutionCategories.get("Qte"),(short) 1));
		l.add(InstanceFactory.newResolution("Couverture en X hors spec.","Qte-couverture", resolutionCategories.get("Qte"),(short) 2));
	
		//IND
		l.add(InstanceFactory.newResolution("Index incorrect","IND-indexIncorrect", resolutionCategories.get("IND"),(short) 1));
				
		//Qlte
		ResolutionCategory QlterC= resolutionCategories.get("Qlte");
			
		l.add(InstanceFactory.newResolution("Q30 hors spec.","Qlte-Q30HorsSpec", QlterC,(short) 1));
		l.add(InstanceFactory.newResolution("Répartition bases","Qlte-repartitionBases", QlterC, (short) 2));
		l.add(InstanceFactory.newResolution("% adaptateurs détectés","Qlte-adapterPercent", QlterC,(short) 3));
		l.add(InstanceFactory.newResolution("% duplicat élevé","Qlte-duplicatElevee", QlterC,(short) 4));	
		l.add(InstanceFactory.newResolution("% NT 30X","Qlte-30XntPercent", QlterC,(short)5));
		l.add(InstanceFactory.newResolution("% Target","Qlte-targetPercent", QlterC,(short)6));

		// MAP
		l.add(InstanceFactory.newResolution("% mapping faible","MAP-PercMappingFble", resolutionCategories.get("MAP"),(short) 1));
		
		// Sample
		l.add(InstanceFactory.newResolution("Sexe incorrect","Sample-sexeIncorrect", resolutionCategories.get("Sample"),(short) 1));
		
		// Info
		ResolutionCategory InforC= resolutionCategories.get("Info");
				
		l.add(InstanceFactory.newResolution("Test Dev","Info-testDev", InforC,(short) 1));
		l.add(InstanceFactory.newResolution("Test Prod","Info-testProd", InforC,(short) 2));
		l.add(InstanceFactory.newResolution("Redo effectué","Info-redoDone", InforC,(short) 3));
		
		// LIMS
		l.add(InstanceFactory.newResolution("erreur Experimental Type","LIMS-erreurExpType", resolutionCategories.get("LIMS"),(short) 1));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "readSetReso";
		r.resolutions = l;
		r.objectTypeCode = "ReadSet";
		ArrayList<String> al = new ArrayList<String>();
		al.add("default-readset");
		al.add("rsillumina");		
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "readSetReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createRunResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		// FDS 16/01 rendre moins verbeux avec variables XXrC
		
		// PbM
		ResolutionCategory PbMrC= resolutionCategories.get("PbM");

		l.add(InstanceFactory.newResolution("Indéterminé","PbM-indetermine", PbMrC,  (short) 1));
		l.add(InstanceFactory.newResolution("Chiller","PbM-chiller", PbMrC, (short) 2));
		l.add(InstanceFactory.newResolution("Pelletier","PbM-pelletier", PbMrC, (short) 3));
		l.add(InstanceFactory.newResolution("Fluidique","PbM-fluidiq", PbMrC, (short) 4));
		l.add(InstanceFactory.newResolution("Laser","PbM-laser", PbMrC, (short) 5));
		l.add(InstanceFactory.newResolution("Camera","PbM-camera", PbMrC, (short) 6));
		l.add(InstanceFactory.newResolution("Focus","PbM-focus", PbMrC, (short) 7));    
		l.add(InstanceFactory.newResolution("Pb de vide","PbM-pbVide", PbMrC, (short) 8));
		l.add(InstanceFactory.newResolution("PE module","PbM-PEmodule", PbMrC, (short) 9));
		l.add(InstanceFactory.newResolution("cBot","PbM-cBot", PbMrC, (short) 10));		
			
		// PbR
		ResolutionCategory PbRrC= resolutionCategories.get("PbR");
			
		l.add(InstanceFactory.newResolution("Indéterminé","PbR-indetermine", PbRrC, (short) 1));
		l.add(InstanceFactory.newResolution("Flowcell","PbR-FC", PbRrC, (short) 2));
		l.add(InstanceFactory.newResolution("cBot","PbR-cBot", PbRrC, (short) 3));
		l.add(InstanceFactory.newResolution("Séquencage","PbR-sequencage", PbRrC, (short) 4));
		l.add(InstanceFactory.newResolution("Indexing","PbR-indexing", PbRrC, (short) 5));
		l.add(InstanceFactory.newResolution("PE module","PbR-PEmodule", PbRrC, (short) 6));
		l.add(InstanceFactory.newResolution("Rehyb primer R1","PbR-rehybR1", PbRrC, (short) 7));
		l.add(InstanceFactory.newResolution("Rehyb indexing","PbR-rehybIndexing", PbRrC, (short) 8));
		l.add(InstanceFactory.newResolution("Rehyb primer R2","PbR-rehybR2", PbRrC, (short) 9));
		l.add(InstanceFactory.newResolution("Erreur réactifs","PbR-erreurReac", PbRrC, (short) 10));
		l.add(InstanceFactory.newResolution("Rajout réactifs","PbR-ajoutReac", PbRrC, (short) 11));

		// SAV
		ResolutionCategory SAVrC= resolutionCategories.get("SAV");
		
		l.add(InstanceFactory.newResolution("Intensité","SAV-intensite", SAVrC, (short) 1));
		l.add(InstanceFactory.newResolution("Densité clusters trop élevée","SAV-densiteElevee", SAVrC, (short) 2));
		l.add(InstanceFactory.newResolution("Densité clusters trop faible","SAV-densiteFaible", SAVrC, (short) 3));
		l.add(InstanceFactory.newResolution("Densité clusters nulle","SAV-densiteNulle", SAVrC, (short) 4));
		l.add(InstanceFactory.newResolution("%PF","SAV-PF", SAVrC, (short) 5));
		l.add(InstanceFactory.newResolution("Phasing","SAV-phasing", SAVrC, (short) 6));
		l.add(InstanceFactory.newResolution("Prephasing","SAV-prephasing", SAVrC, (short) 7));
		l.add(InstanceFactory.newResolution("Error rate","SAV-errorRate", SAVrC, (short) 8));
		l.add(InstanceFactory.newResolution("Q30","SAV-Q30", SAVrC, (short) 9));
		l.add(InstanceFactory.newResolution("Indexing / demultiplexage","SAV-IndDemultiplex", SAVrC, (short) 10));
		
		// PbI
		ResolutionCategory PbIrC= resolutionCategories.get("PbI");
		
		l.add(InstanceFactory.newResolution("Indéterminé","PbI-indetermine", PbIrC, (short) 1));
		l.add(InstanceFactory.newResolution("PC","PbI-PC", PbIrC, (short) 2));
		l.add(InstanceFactory.newResolution("Ecran","PbI-ecran", PbIrC, (short) 3));
		l.add(InstanceFactory.newResolution("Espace disq insuf","PbI-espDisqInsuf", PbIrC, (short) 4));
		l.add(InstanceFactory.newResolution("Logiciel","PbI-logiciel", PbIrC, (short) 5));
		l.add(InstanceFactory.newResolution("Reboot PC","PbI-rebootPC", PbIrC, (short) 6));
		l.add(InstanceFactory.newResolution("Erreur paramétrage run","PbI-parametrageRun", PbIrC, (short) 7));
		
		// Info
		ResolutionCategory InforC= resolutionCategories.get("Info");
				
		l.add(InstanceFactory.newResolution("Run de validation","Info-runValidation", InforC, (short) 1));
		l.add(InstanceFactory.newResolution("Arrêt séquenceur","Info-arretSeq", InforC, (short) 2));
		l.add(InstanceFactory.newResolution("Arrêt logiciel","Info_arretLogiciel", InforC, (short) 3));
		l.add(InstanceFactory.newResolution("Remboursement","Info-remboursement", InforC, (short) 4));
		l.add(InstanceFactory.newResolution("Flowcell redéposée","Info-FCredeposee", InforC, (short) 5));		
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "runReso";
		r.resolutions = l;
		r.objectTypeCode = "Run";
		ArrayList<String> al = new ArrayList<String>();
		al.add("RHS2000");
		al.add("RHS2500");
		al.add("RHS2500R");
		al.add("RMISEQ");
		al.add("RGAIIx");
		//al.add("RARGUS");
		
		al.add("RMINION");
		al.add("RMKI");
		al.add("RMKIB");
		al.add("RHS4000");
		
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "runReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createReadSetResolutionCNS(ContextValidation ctx) {	
		List<Resolution> l = new ArrayList<Resolution>();

		// FDS 16/01 rendre moins verbeux avec variables XXrC
		
		// Run
		l.add(InstanceFactory.newResolution("Lane abandonnée","Run-abandonLane", resolutionCategories.get("Run"), (short) 1));
		
		// LIB
		l.add(InstanceFactory.newResolution("Pb protocole banque","LIB-pbProtocole", resolutionCategories.get("LIB"),(short) 1));
		l.add(InstanceFactory.newResolution("Erreur dépôt banque","LIB-erreurDepot", resolutionCategories.get("LIB"),(short) 2));
		
		// Qte
		l.add(InstanceFactory.newResolution("Seq valides insuf","Qte-seqValInsuf", resolutionCategories.get("Qte"),(short) 1));
		l.add(InstanceFactory.newResolution("Seq utiles insuf","Qte-seqUtileInsuf", resolutionCategories.get("Qte"),(short) 2));
		
		// IND
		l.add(InstanceFactory.newResolution("Pb demultiplexage","IND-pbDemultiplex", resolutionCategories.get("IND"),(short) 1));
		l.add(InstanceFactory.newResolution("Pb manip","IND-pbManip", resolutionCategories.get("IND"),(short) 2));

		// Qlte
		ResolutionCategory QlterC= resolutionCategories.get("Qlte");
	
		l.add(InstanceFactory.newResolution("Q30","Qlte-Q30", QlterC,(short) 1));				
		l.add(InstanceFactory.newResolution("Répartition bases","Qlte-repartitionBases", QlterC, (short) 2));				
		l.add(InstanceFactory.newResolution("Adaptateurs/Kmers","Qlte-adapterKmer", QlterC,(short) 3));		
		l.add(InstanceFactory.newResolution("Duplicat pairs > 20","Qlte-duplicatPairs", QlterC,(short) 4));
		l.add(InstanceFactory.newResolution("Duplicat > 30","Qlte-duplicat", QlterC,(short) 5));
		
		// TAXO
		ResolutionCategory TAXOrC= resolutionCategories.get("TAXO");
			
		l.add(InstanceFactory.newResolution("Conta indéterminée","TAXO-contaIndeterm", TAXOrC,(short) 1));
		l.add(InstanceFactory.newResolution("Conta manip","TAXO-contaManip", TAXOrC,(short) 2));
		l.add(InstanceFactory.newResolution("Conta mat ori","TAXO-contaMatOri", TAXOrC,(short) 3));
		l.add(InstanceFactory.newResolution("Non conforme","TAXO-nonConforme", TAXOrC,(short) 4));
		l.add(InstanceFactory.newResolution("Mitochondrie","TAXO-mitochondrie", TAXOrC,(short) 5));
		l.add(InstanceFactory.newResolution("Chloroplast","TAXO-chloroplast", TAXOrC,(short) 6));
		l.add(InstanceFactory.newResolution("Virus","TAXO-virus", TAXOrC,(short) 7));
		l.add(InstanceFactory.newResolution("Bactérie","TAXO-bacteria", TAXOrC,(short) 8)); 
		l.add(InstanceFactory.newResolution("Fungi","TAXO-fungi", TAXOrC,(short) 9));
		l.add(InstanceFactory.newResolution("OK post clean rRNA","TAXO-postCleanrRNA", TAXOrC,(short) 10));
				
		// RIBO
		l.add(InstanceFactory.newResolution("% rRNA élevé","RIBO-percEleve", resolutionCategories.get("RIBO"),(short) 1));
		
		// MAP
		l.add(InstanceFactory.newResolution("% MP","MAP-PercentMP", resolutionCategories.get("MAP"),(short) 1));
		l.add(InstanceFactory.newResolution("Taille moyenne MP","MAP-tailleMP", resolutionCategories.get("MAP"),(short) 2));
		
		// MERG
		l.add(InstanceFactory.newResolution("% lec mergées","MERG-PercLecMerg", resolutionCategories.get("MERG"),(short) 1));
		l.add(InstanceFactory.newResolution("Médiane lect mergées","MERG-MedLecMerg", resolutionCategories.get("MERG"),(short) 2));
		l.add(InstanceFactory.newResolution("Distribution lect mergées","MERG-Distribution", resolutionCategories.get("MERG"),(short) 3));
	
		// Info
		l.add(InstanceFactory.newResolution("Test Dev","Info-testDev", resolutionCategories.get("Info"),(short) 1));
		l.add(InstanceFactory.newResolution("Nouveaux critères d'évaluation","Info-nvoCritereEval", resolutionCategories.get("Info"),(short) 2));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "readSetReso";
		r.resolutions = l;
		r.objectTypeCode = "ReadSet";
		ArrayList<String> al = new ArrayList<String>();
		al.add("default-readset");
		al.add("rsillumina");
		al.add("rsnanopore");
		
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "readSetReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createAnalysisResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		// FDS 16/01 rendre moins verbeux avec variables XXrC

		// BA-MERG
		l.add(InstanceFactory.newResolution("% merging","MERG-BA-MERGPercent", resolutionCategories.get("BA-MERG"),(short) 1));
		l.add(InstanceFactory.newResolution("reads size","MERG-readSize", resolutionCategories.get("BA-MERG"),(short) 2));
		
		// CTG
		ResolutionCategory CTGrC= resolutionCategories.get("CTG");
		
		l.add(InstanceFactory.newResolution("N50","CTG-N50", CTGrC,(short) 1));
		l.add(InstanceFactory.newResolution("Cumul","CTG-cumul", CTGrC,(short)2));
		l.add(InstanceFactory.newResolution("Nb contigs","CTG-nbCtgs", CTGrC,(short)3));
		l.add(InstanceFactory.newResolution("Max size","CTG-maxSize", CTGrC,(short)4));
		l.add(InstanceFactory.newResolution("Assembled reads","CTG-assReads", CTGrC,(short)5));
		
		// SIZE
		l.add(InstanceFactory.newResolution("% lost bases","SIZE-lostBasesPerc", resolutionCategories.get("SIZE"),(short)1));
		
		// SCAFF
		ResolutionCategory SCAFFrC= resolutionCategories.get("SCAFF");
				
		l.add(InstanceFactory.newResolution("N50","SCAFF-N50", SCAFFrC,(short) 1));
		l.add(InstanceFactory.newResolution("Cumul","SCAFF-cumul", SCAFFrC,(short) 2));
		l.add(InstanceFactory.newResolution("Nb scaff","SCAFF-nbScaff", SCAFFrC,(short) 3));
		l.add(InstanceFactory.newResolution("Max size","SCAFF-maxSize", SCAFFrC,(short) 4));
		l.add(InstanceFactory.newResolution("Median insert size","SCAFF-medInsertSize", SCAFFrC,(short) 5));
		l.add(InstanceFactory.newResolution("% satisfied pairs","SCAFF-satisfPairsPerc", SCAFFrC,(short) 6));
		l.add(InstanceFactory.newResolution("% N","SCAFF-Npercent", SCAFFrC,(short) 7));
		
		// GAP
		ResolutionCategory GAPrC= resolutionCategories.get("GAP");
			
		l.add(InstanceFactory.newResolution("Gap sum","GAP-sum",GAPrC,(short) 1));
		l.add(InstanceFactory.newResolution("Gap count","GAP-count",GAPrC,(short) 2));
		l.add(InstanceFactory.newResolution("Corrected gap sum","GAP-correctedSum",GAPrC,(short) 3));
		l.add(InstanceFactory.newResolution("Corrected gap count","GAP-correctedCount",GAPrC,(short) 4));
		l.add(InstanceFactory.newResolution("% N","GAP-Npercent",GAPrC,(short) 5));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "analysisReso";
		r.resolutions = l;
		r.objectTypeCode = "Analysis";
		ArrayList<String> al = new ArrayList<String>();
		al.add("BPA");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "analysisReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}

	public static void createExperimentResolution(ContextValidation ctx) {	
		List<Resolution> l = getDefaultResolutionCNS();
				
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "experimentReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>(); 
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class,r.code);
		List<String> typeCodes=MongoDBDAO.getCollection(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class).distinct("typeCodes");
		
		try {
			List<ExperimentType> expTypes=ExperimentType.find.findAll();
			for(ExperimentType expType:expTypes){
				if(typeCodes == null || !typeCodes.contains(expType.code)){
					Logger.debug("Add experimentType default resolution "+ expType.code);
					al.add(expType.code);
				}	
			}
		} catch (DAOException e) {
			Logger.error("Creation Resolution for ExperimentType error "+e.getMessage());
		}
		
		r.typeCodes = al;
		ctx.setCreationMode();
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}
	

	public static void createOpgenDepotResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());
		
		l.add(InstanceFactory.newResolution("Nombre molécules insuffisant pour assemblage correct", "echec-nbMoleculesInsuf", resolutionCategories.get("Default"), (short) 4));
		l.add(InstanceFactory.newResolution("Surface cassée", "echec-surface", resolutionCategories.get("Default"), (short) 5));	
		l.add(InstanceFactory.newResolution("Problème digestion", "echec-digestion", resolutionCategories.get("Default"), (short) 6));	
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "expODReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("void-opgen-depot");
		al.add("opgen-depot");		
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}
	
	public static void createIlluminaPrepFCDepotResolutionCNG(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());
		
		l.add(InstanceFactory.newResolution("Réhybridation FC", "rehyb-FC", resolutionCategories.get("Default"), (short) 4));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "expIPDReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("ext-to-prepa-flowcell");
		al.add("prepa-flowcell");		
		al.add("ext-to-prepa-fc-ordered"); //FDS ajout 10/11/2015  -- JIRA NGL-838
		al.add("prepa-fc-ordered");	       //FDS ajout 10/11/2015  -- JIRA NGL-838
		al.add("illumina-depot");	
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}
	
	public static void createIlluminaPrepFCDepotResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());
		
		l.add(InstanceFactory.newResolution("Réhybridation FC", "rehyb-FC", resolutionCategories.get("Default"), (short) 4));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "expIPDReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("ext-to-prepa-flowcell");
		al.add("prepa-flowcell");	
		al.add("prepa-fc-ordered");	
		al.add("illumina-depot");	
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}
	
	private static void createIryPreparationNLRSResolutionCNS(ContextValidation ctx) {
	List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());
		
		l.add(InstanceFactory.newResolution("Marquage incorrect", "echec-labeling", resolutionCategories.get("Default"), (short) 4));
		l.add(InstanceFactory.newResolution("Hors gamme", "out-of-range", resolutionCategories.get("Default"), (short) 5));
		l.add(InstanceFactory.newResolution("Conc. < 5 : over-staining risk", "over-staining-risk", resolutionCategories.get("Default"), (short) 6));
		l.add(InstanceFactory.newResolution("Conc. > 9 : over-loading risk", "over-loading-risk", resolutionCategories.get("Default"), (short) 7));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "expIrysPrepNLRSReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("irys-nlrs-prep");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}
	
	private static void createDepotBionanoResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());
		
		l.add(InstanceFactory.newResolution("Nb cycles insuffisant", "echec-nbCycleInsuf", resolutionCategories.get("Default"), (short) 4));
		l.add(InstanceFactory.newResolution("Problème passage des molécules région pillar", "echec-pillarRegion", resolutionCategories.get("Default"), (short) 5));
		l.add(InstanceFactory.newResolution("Labelling incorrect", "echec-labeling", resolutionCategories.get("Default"), (short) 6));
		l.add(InstanceFactory.newResolution("Utilisation du NanoAnalyzer", "nanoAnalyzer", resolutionCategories.get("Default"), (short) 7));
		
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "expDepotBionanoReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("bionano-depot");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);	
	}
	
	
	private static void createSamplePrepResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());	
		l.add(InstanceFactory.newResolution("Tube cassé dans cryobroyeur", "broken-tube-in-freezer-mill", resolutionCategories.get("Default"), (short) 4));
		l.add(InstanceFactory.newResolution("Tube vide", "empty-tube", resolutionCategories.get("Default"), (short) 5));
		l.add(InstanceFactory.newResolution("Colonne élution bouchée", "elution-column-blocked", resolutionCategories.get("Default"), (short) 6));

		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "expExtractionDNARNAReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("dna-rna-extraction");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
		
		
		l = new ArrayList<Resolution>();

		l.addAll(getDefaultResolutionCNS());	

		r = new ResolutionConfiguration();
		r.code = "expBroyageReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		al = new ArrayList<String>();
		al.add("grinding");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
		
	}
	private static void createGelMigrationResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());	
		l.add(InstanceFactory.newResolution("Tâche de faible poids moléculaire", "low-molecular-weight-spot", resolutionCategories.get("Default"), (short) 4));
		l.add(InstanceFactory.newResolution("Contamination ARN", "rna-contamination", resolutionCategories.get("Default"), (short) 5));
		l.add(InstanceFactory.newResolution("ADN dégradé", "degraded-dna", resolutionCategories.get("Default"), (short) 6));
		l.add(InstanceFactory.newResolution("MétaGénome", "metagenome", resolutionCategories.get("Default"), (short) 7));
		l.add(InstanceFactory.newResolution("Présence de plasmide(s)", "plasmid-presence", resolutionCategories.get("Default"), (short) 8));
		l.add(InstanceFactory.newResolution("Profil inhabituel", "unusual-profile", resolutionCategories.get("Default"), (short) 9));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "expGelMigrationReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("gel-migration");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}

	
	// FDS 05/02/2016 -- JIRA NGL-894 experience ey processus X5
	private static void createPrepPcrFreeResolutionCNG(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());
		
		l.add(InstanceFactory.newResolution("Echec échantillons par puits", "echec-echPuit", resolutionCategories.get("Default"), (short) 4));
		l.add(InstanceFactory.newResolution("Contamination", "contamination", resolutionCategories.get("Default"), (short) 5));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "expPrepPcrFreeReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("prep-pcr-free"); 
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);	
	}
	
	private static void createQCMiseqResolutionCNG(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());
		

		l.add(InstanceFactory.newResolution("Run Miseq invalide : résultats non importés", "invalid-miseq-run", resolutionCategories.get("Default"), (short) 4));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "expMiseqQCReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("miseq-qc"); 
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);	
	}
	
	// FDS 23/11/2016 NGL-1158: renommage pour separation des resolutions de Processus entre CNG et CNS
	public static void createProcessResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		if(ConfigFactory.load().getString("ngl.env").equals("PROD") ){	
			/* OLD*/
			l.add(InstanceFactory.newResolution("Déroulement correct","correct", resolutionCategories.get("Default"), (short) 1));
			l.add(InstanceFactory.newResolution("Processus partiel","processus-partiel", resolutionCategories.get("Default"), (short) 2));
			l.add(InstanceFactory.newResolution("Arrêt - abandon","stop-abandon", resolutionCategories.get("Default"), (short) 3));
			l.add(InstanceFactory.newResolution("Arrêt - à ré-extraire","stop-reextraire", resolutionCategories.get("Default"), (short) 4));
			l.add(InstanceFactory.newResolution("Arrêt - à ré-amplifier","stop-reamplifier", resolutionCategories.get("Default"), (short) 5));
			l.add(InstanceFactory.newResolution("Arrêt - à re-synthétiser","stop-resynthétiser", resolutionCategories.get("Default"), (short) 6));
			l.add(InstanceFactory.newResolution("Arrêt - à re-fragmenter","stop-refragmenter", resolutionCategories.get("Default"), (short) 7));
			
		
		} else if(ConfigFactory.load().getString("ngl.env").equals("DEV") ){	
			//NEW 17/01/2017
			l.add(InstanceFactory.newResolution("Déroulement correct","correct", resolutionCategories.get("Default"), (short) 1));
			l.add(InstanceFactory.newResolution("Standby","standby", resolutionCategories.get("Default"), (short) 2));
			l.add(InstanceFactory.newResolution("Arrêt - abandon","stop-abandon", resolutionCategories.get("Default"), (short) 3));
			l.add(InstanceFactory.newResolution("Arrêt - pb broyage","stop-pb-broyage", resolutionCategories.get("Default"), (short) 4));
			l.add(InstanceFactory.newResolution("Arrêt - pb cryobroyeur","stop-pb-cryobroyeur", resolutionCategories.get("Default"), (short) 5));
			l.add(InstanceFactory.newResolution("Arrêt - pb extraction ADN/ARN","stop-pb-extraction", resolutionCategories.get("Default"), (short) 6));
			l.add(InstanceFactory.newResolution("Arrêt - pb bq RNA","stop-pb-bq-rna", resolutionCategories.get("Default"), (short) 7));
			l.add(InstanceFactory.newResolution("Arrêt - pb synthèse cDNA","stop-pb-synthese-cdna", resolutionCategories.get("Default"), (short) 8));
			l.add(InstanceFactory.newResolution("Arrêt - pb fragmentation","stop-pb-fragmentation", resolutionCategories.get("Default"), (short) 9));
			l.add(InstanceFactory.newResolution("Arrêt - pb prep Tag","stop-pb-prep-tag", resolutionCategories.get("Default"), (short) 10));
			l.add(InstanceFactory.newResolution("Arrêt - pb bq DNA","stop-pb-bq-dna", resolutionCategories.get("Default"), (short) 11));
			l.add(InstanceFactory.newResolution("Arrêt - pb PCR amplif","stop-pb-pcr-ampli", resolutionCategories.get("Default"), (short) 12));
			l.add(InstanceFactory.newResolution("Arrêt - pb sizing sur gel","stop-pb-sizing-gel", resolutionCategories.get("Default"), (short) 13));
			l.add(InstanceFactory.newResolution("Arrêt - pb Ampure/SpriSelect","stop-pb-ampure-spriselect", resolutionCategories.get("Default"), (short) 14));
			l.add(InstanceFactory.newResolution("Arrêt - pb sol stock","stop-pb-sol-stock", resolutionCategories.get("Default"), (short) 15));
			l.add(InstanceFactory.newResolution("Arrêt - échec run","stop-pb-run", resolutionCategories.get("Default"), (short) 16));
			
			l.add(InstanceFactory.newResolution("Processus partiel (=> Standby)","processus-partiel", resolutionCategories.get("Default"), (short) 17));
			l.add(InstanceFactory.newResolution("Arrêt - à ré-amplifier  (MUST BE REPLACE)","stop-reamplifier", resolutionCategories.get("Default"), (short) 18));
			l.add(InstanceFactory.newResolution("Arrêt - à re-synthétiser (MUST BE REPLACE)","stop-resynthétiser", resolutionCategories.get("Default"), (short) 19));
			
		}
		
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "processReso";
		r.resolutions = l;
		r.objectTypeCode = "Process";
		ArrayList<String> al = new ArrayList<String>();
		
		try {
			List<ProcessType> processTypes=ProcessType.find.findAll();
			for(ProcessType processType:processTypes){
					Logger.debug("Add processType default resolution "+ processType.code);
					al.add(processType.code);
			}
		} catch (DAOException e) {
			Logger.error("Creation Resolution for Process Type error "+e.getMessage());
		}
		
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}
	
	// FDS 23/11/2016 NGL-1158: creation pour separation des resolutions de Processus entre CNG et CNS
	public static void createProcessResolutionCNG(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();

		// pour l'instant les 2 premieres ne sont pas demandees...
		//l.add(InstanceFactory.newResolution("Déroulement correct","correct", resolutionCategories.get("Default"), (short) 1));
		//l.add(InstanceFactory.newResolution("Processus partiel","processus-partiel", resolutionCategories.get("Default"), (short) 2));
		
		l.add(InstanceFactory.newResolution("REDO","stop-redo", resolutionCategories.get("Default"), (short) 3));
		l.add(InstanceFactory.newResolution("concentration insuffisante","stop-conc-insuffisante", resolutionCategories.get("Default"), (short) 4));
		l.add(InstanceFactory.newResolution("problème profil","stop-pb-profil", resolutionCategories.get("Default"), (short) 5));
		l.add(InstanceFactory.newResolution("problème technique","stop-pb-technique", resolutionCategories.get("Default"), (short) 6));
		l.add(InstanceFactory.newResolution("contamination","stop-contamination", resolutionCategories.get("Default"), (short) 7));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "processReso";
		r.resolutions = l;
		r.objectTypeCode = "Process";
		ArrayList<String> al = new ArrayList<String>();
		
		try {
			List<ProcessType> processTypes=ProcessType.find.findAll();
			for(ProcessType processType:processTypes){
					Logger.debug("Add processType default resolution "+ processType.code);
					al.add(processType.code);
			}
		} catch (DAOException e) {
			Logger.error("Creation Resolution for Process Type error "+e.getMessage());
		}
		
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}
	
	public static List<Resolution> getDefaultResolutionCNS(){
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.add(InstanceFactory.newResolution("Déroulement correct",	"correct", resolutionCategories.get("Default"), (short) 1));
		l.add(InstanceFactory.newResolution("Problème signalé en commentaire", "pb-commentaire", resolutionCategories.get("Default"), (short) 2));
		l.add(InstanceFactory.newResolution("Echec expérience", "echec-experience", resolutionCategories.get("Default"), (short) 3));	

		return l;
	}
	
	
	public static void createContainerResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();

		l.add(InstanceFactory.newResolution("Sauvegarde prod","prod-backup", resolutionCategories.get("Default"), (short) 1));
		l.add(InstanceFactory.newResolution("Epuisé","empty", resolutionCategories.get("Default"), (short) 2));
		l.add(InstanceFactory.newResolution("Renvoyé collaborateur","return-collab", resolutionCategories.get("Default"), (short) 3));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "containerReso";
		r.resolutions = l;
		r.objectTypeCode = "Container";
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}
}