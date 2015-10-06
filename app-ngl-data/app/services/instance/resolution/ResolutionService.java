package services.instance.resolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		
		if ( inst.equals("CNS") || inst.equals("CNG") ) {
			Logger.info("Create and save "+inst+ " resolution categories ...");
			
			saveResolutions(ctx, inst);
		}
		else {
			Logger.error("You need to specify only one institute !");
		}
		
		ctx.displayErrors(logger);
	}
	
	// FDS 15/01: fusion en 1 seule methode avec parametre inst
	// FDS 20/01: ajout creation des categories
	public static void saveResolutions(ContextValidation ctx, String inst) {	
		if ( inst.equals("CNG") ){
			resolutionCategories = createResolutionCategoriesCNG();
			
			createRunResolutionCNG(ctx); 
			createReadSetResolutionCNG(ctx); 
			// FDS 15/01: no Analysis Resolutions ???
			// FDS 15/01: No illumina Depot Resolutions ???
			createIlluminaPrepFCDepotResolutionCNG(ctx);
			createExperimentResolution(ctx);
			createProcessResolution(ctx);
		}
		else if ( inst.equals("CNS") ){			
			resolutionCategories = createResolutionCategoriesCNS();
			
			createRunResolutionCNS(ctx); 
			createReadSetResolutionCNS(ctx); 
			createAnalysisResolutionCNS(ctx); 
			createOpgenDepotResolutionCNS(ctx);
			createIlluminaPrepFCDepotResolutionCNS(ctx);
			// FDS 15/01: No illumina Depot Resolutions ???
			createExperimentResolution(ctx); 
			createProcessResolution(ctx);
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
				
		l.add(InstanceFactory.newResolution("indéterminé","PbM-indetermine", PbMrC, (short) 1));	
		l.add(InstanceFactory.newResolution("chiller","PbM-chiller", PbMrC, (short) 2));
		l.add(InstanceFactory.newResolution("pelletier","PbM-pelletier", PbMrC, (short) 3));
		l.add(InstanceFactory.newResolution("fluidique","PbM-fluidiq", PbMrC, (short) 4));
		l.add(InstanceFactory.newResolution("laser","PbM-laser", PbMrC, (short) 5));
		l.add(InstanceFactory.newResolution("camera","PbM-camera", PbMrC, (short) 6));
		l.add(InstanceFactory.newResolution("focus","PbM-focus", PbMrC, (short) 7));
		l.add(InstanceFactory.newResolution("pb de vide","PbM-pbVide", PbMrC, (short) 8));
		l.add(InstanceFactory.newResolution("PE module","PbM-PEmodule", PbMrC, (short) 9));
		l.add(InstanceFactory.newResolution("zone de dépôt","PbM-zoneDepot", PbMrC, (short) 10));				
		l.add(InstanceFactory.newResolution("cBot","PbM-cBot", PbMrC, (short) 11));		
		
		// SAV
		ResolutionCategory SAVrC= resolutionCategories.get("SAV");	
		
		l.add(InstanceFactory.newResolution("intensité","SAV-intensite", SAVrC, (short) 1));
		l.add(InstanceFactory.newResolution("intensité faible A","SAV-intFbleA", SAVrC, (short) 2));
		l.add(InstanceFactory.newResolution("intensité faible T","SAV-intFbleT", SAVrC, (short) 3));
		l.add(InstanceFactory.newResolution("intensité faible C","SAV-intFbleC", SAVrC, (short) 4));
		l.add(InstanceFactory.newResolution("intensité faible G","SAV-intFbleG", SAVrC, (short) 5));
		l.add(InstanceFactory.newResolution("densité clusters trop élevée","SAV-densiteElevee", SAVrC, (short) 6));
		l.add(InstanceFactory.newResolution("densité clusters trop faible","SAV-densiteFaible", SAVrC, (short) 7));
		l.add(InstanceFactory.newResolution("densité clusters nulle","SAV-densiteNulle", SAVrC, (short) 8));
		l.add(InstanceFactory.newResolution("%PF","SAV-PF", SAVrC, (short) 9));
		l.add(InstanceFactory.newResolution("phasing","SAV-phasing", SAVrC, (short) 10));
		l.add(InstanceFactory.newResolution("prephasing","SAV-prephasing", SAVrC, (short) 11));
		l.add(InstanceFactory.newResolution("error rate","SAV-errorRate", SAVrC, (short) 12));
		l.add(InstanceFactory.newResolution("focus","SAV-focus", SAVrC, (short) 13));
		l.add(InstanceFactory.newResolution("Q30","SAV-Q30", SAVrC, (short) 14));
		l.add(InstanceFactory.newResolution("% bases déséquilibré","SAV-perctBasesDeseq", SAVrC, (short) 15));
		l.add(InstanceFactory.newResolution("index non représenté","SAV-indexNonPresent", SAVrC, (short) 16));
		l.add(InstanceFactory.newResolution("index sous-représenté","SAV-indexFblePerc", SAVrC, (short) 17));
		l.add(InstanceFactory.newResolution("indexing / demultiplexage","SAV-IndDemultiplex", SAVrC, (short) 18));
		
		//PbR
		ResolutionCategory PbRrC= resolutionCategories.get("PbR");
			
		l.add(InstanceFactory.newResolution("indéterminé","PbR-indetermine", PbRrC, (short) 1));
		l.add(InstanceFactory.newResolution("flowcell","PbR-FC", PbRrC, (short) 2));
		l.add(InstanceFactory.newResolution("cBot","PbR-cBot", PbRrC, (short) 3));
		l.add(InstanceFactory.newResolution("séquencage","PbR-sequencage", PbRrC, (short) 4));
		l.add(InstanceFactory.newResolution("indexing","PbR-indexing", PbRrC, (short) 5));
		l.add(InstanceFactory.newResolution("PE module","PbR-PEmodule", PbRrC, (short) 6));
		l.add(InstanceFactory.newResolution("rehyb primer R1","PbR-rehybR1", PbRrC, (short) 7));
		l.add(InstanceFactory.newResolution("rehyb primer R2","PbR-rehybR2", PbRrC, (short) 8));
		l.add(InstanceFactory.newResolution("erreur réactifs","PbR-erreurReac", PbRrC, (short) 9));
		l.add(InstanceFactory.newResolution("rajout réactifs","PbR-ajoutReac", PbRrC, (short) 10));
		
		//LIB
		ResolutionCategory LIBRc= resolutionCategories.get("LIB");
		
		l.add(InstanceFactory.newResolution("construction librairie","LIB-construction", LIBRc, (short) 1));
		l.add(InstanceFactory.newResolution("cause profil : librairie","LIB-profilIntLib", LIBRc, (short) 2));
		l.add(InstanceFactory.newResolution("cause profil : exp type","LIB-profilIntExpType", LIBRc, (short) 3));
		l.add(InstanceFactory.newResolution("pb dilution","LIB-pbDilution", LIBRc, (short) 4));
		l.add(InstanceFactory.newResolution("pb dilution spike-In","LIB-pbDilSpikeIn", LIBRc, (short) 5));
		
		//PbI
		ResolutionCategory PbIrC= resolutionCategories.get("PbI");		
		
		l.add(InstanceFactory.newResolution("indéterminé","PbI-indetermine", PbIrC, (short) 1));
		l.add(InstanceFactory.newResolution("PC","PbI-PC", PbIrC, (short) 2));
		l.add(InstanceFactory.newResolution("écran","PbI-ecran", PbIrC, (short) 3));
		l.add(InstanceFactory.newResolution("espace disq insuf","PbI-espDisqInsuf", PbIrC, (short) 4));
		l.add(InstanceFactory.newResolution("logiciel","PbI-logiciel", PbIrC, (short) 5));
		l.add(InstanceFactory.newResolution("reboot PC","PbI-rebootPC", PbIrC, (short) 6));
		l.add(InstanceFactory.newResolution("retard robocopy","PbI-robocopy", PbIrC, (short) 7));
		l.add(InstanceFactory.newResolution("erreur paramétrage run","PbI-parametrageRun", PbIrC, (short) 8));
		
		//RUN-Info
		ResolutionCategory RUNInforC= resolutionCategories.get("RUN-Info");
			
		l.add(InstanceFactory.newResolution("run de validation","Info-runValidation", RUNInforC, (short) 1));
		l.add(InstanceFactory.newResolution("remboursement","Info-remboursement", RUNInforC, (short) 2));

		//QC
		ResolutionCategory QCRc= resolutionCategories.get("QC");
		
		l.add(InstanceFactory.newResolution("intensité B.M.S","QC-intBMS", QCRc, (short) 1));
		l.add(InstanceFactory.newResolution("tiles out","QC-tilesOut", QCRc, (short) 2));
		l.add(InstanceFactory.newResolution("saut de chimie","QC-sautChimie", QCRc, (short) 3));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "runReso";
		r.resolutions = l;
		r.objectTypeCode = "Run";
		ArrayList<String> al = new ArrayList<String>();
		al.add("RHS2000");
		al.add("RHS2500");
		al.add("RHS2500R");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "runReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	
	public static void createReadSetResolutionCNG(ContextValidation ctx) {	
		List<Resolution> l = new ArrayList<Resolution>();
		
		// FDS 16/01 rendre moins verbeux avec variables XXrC

		// Run
		l.add(InstanceFactory.newResolution("lane abandonnée","Run-abandonLane", resolutionCategories.get("Run"), (short) 1));
		
		//Qte
		l.add(InstanceFactory.newResolution("nb seq brutes faible","Qte-seqRawInsuf", resolutionCategories.get("Qte"),(short) 1));
		l.add(InstanceFactory.newResolution("couverture en X hors spec.","Qte-couverture", resolutionCategories.get("Qte"),(short) 2));
	
		//IND
		l.add(InstanceFactory.newResolution("index incorrect","IND-indexIncorrect", resolutionCategories.get("IND"),(short) 1));
				
		//Qlte
		ResolutionCategory QlterC= resolutionCategories.get("Qlte");
			
		l.add(InstanceFactory.newResolution("Q30 hors spec.","Qlte-Q30HorsSpec", QlterC,(short) 1));
		l.add(InstanceFactory.newResolution("répartition bases","Qlte-repartitionBases", QlterC, (short) 2));
		l.add(InstanceFactory.newResolution("% adaptateurs détectés","Qlte-adapterPercent", QlterC,(short) 3));
		l.add(InstanceFactory.newResolution("% duplicat élevé","Qlte-duplicatElevee", QlterC,(short) 4));	
		l.add(InstanceFactory.newResolution("% NT 30X","Qlte-30XntPercent", QlterC,(short)5));
		l.add(InstanceFactory.newResolution("% Target","Qlte-targetPercent", QlterC,(short)6));

		// MAP
		l.add(InstanceFactory.newResolution("% mapping faible","MAP-PercMappingFble", resolutionCategories.get("MAP"),(short) 1));
		
		// Sample
		l.add(InstanceFactory.newResolution("sexe incorrect","Sample-sexeIncorrect", resolutionCategories.get("Sample"),(short) 1));
		
		// Info
		ResolutionCategory InforC= resolutionCategories.get("Info");
				
		l.add(InstanceFactory.newResolution("test Dev","Info-testDev", InforC,(short) 1));
		l.add(InstanceFactory.newResolution("test Prod","Info-testProd", InforC,(short) 2));
		l.add(InstanceFactory.newResolution("redo effectué","Info-redoDone", InforC,(short) 3));
		
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

		l.add(InstanceFactory.newResolution("indéterminé","PbM-indetermine", PbMrC,  (short) 1));
		l.add(InstanceFactory.newResolution("chiller","PbM-chiller", PbMrC, (short) 2));
		l.add(InstanceFactory.newResolution("pelletier","PbM-pelletier", PbMrC, (short) 3));
		l.add(InstanceFactory.newResolution("fluidique","PbM-fluidiq", PbMrC, (short) 4));
		l.add(InstanceFactory.newResolution("laser","PbM-laser", PbMrC, (short) 5));
		l.add(InstanceFactory.newResolution("camera","PbM-camera", PbMrC, (short) 6));
		l.add(InstanceFactory.newResolution("focus","PbM-focus", PbMrC, (short) 7));    
		l.add(InstanceFactory.newResolution("pb de vide","PbM-pbVide", PbMrC, (short) 8));
		l.add(InstanceFactory.newResolution("PE module","PbM-PEmodule", PbMrC, (short) 9));
		l.add(InstanceFactory.newResolution("cBot","PbM-cBot", PbMrC, (short) 10));		
			
		// PbR
		ResolutionCategory PbRrC= resolutionCategories.get("PbR");
			
		l.add(InstanceFactory.newResolution("indéterminé","PbR-indetermine", PbRrC, (short) 1));
		l.add(InstanceFactory.newResolution("flowcell","PbR-FC", PbRrC, (short) 2));
		l.add(InstanceFactory.newResolution("cBot","PbR-cBot", PbRrC, (short) 3));
		l.add(InstanceFactory.newResolution("séquencage","PbR-sequencage", PbRrC, (short) 4));
		l.add(InstanceFactory.newResolution("indexing","PbR-indexing", PbRrC, (short) 5));
		l.add(InstanceFactory.newResolution("PE module","PbR-PEmodule", PbRrC, (short) 6));
		l.add(InstanceFactory.newResolution("rehyb primer R1","PbR-rehybR1", PbRrC, (short) 7));
		l.add(InstanceFactory.newResolution("rehyb indexing","PbR-rehybIndexing", PbRrC, (short) 8));
		l.add(InstanceFactory.newResolution("rehyb primer R2","PbR-rehybR2", PbRrC, (short) 9));
		l.add(InstanceFactory.newResolution("erreur réactifs","PbR-erreurReac", PbRrC, (short) 10));
		l.add(InstanceFactory.newResolution("rajout réactifs","PbR-ajoutReac", PbRrC, (short) 11));

		// SAV
		ResolutionCategory SAVrC= resolutionCategories.get("SAV");
		
		l.add(InstanceFactory.newResolution("intensité","SAV-intensite", SAVrC, (short) 1));
		l.add(InstanceFactory.newResolution("densité clusters trop élevée","SAV-densiteElevee", SAVrC, (short) 2));
		l.add(InstanceFactory.newResolution("densité clusters trop faible","SAV-densiteFaible", SAVrC, (short) 3));
		l.add(InstanceFactory.newResolution("densité clusters nulle","SAV-densiteNulle", SAVrC, (short) 4));
		l.add(InstanceFactory.newResolution("%PF","SAV-PF", SAVrC, (short) 5));
		l.add(InstanceFactory.newResolution("phasing","SAV-phasing", SAVrC, (short) 6));
		l.add(InstanceFactory.newResolution("prephasing","SAV-prephasing", SAVrC, (short) 7));
		l.add(InstanceFactory.newResolution("error rate","SAV-errorRate", SAVrC, (short) 8));
		l.add(InstanceFactory.newResolution("Q30","SAV-Q30", SAVrC, (short) 9));
		l.add(InstanceFactory.newResolution("indexing / demultiplexage","SAV-IndDemultiplex", SAVrC, (short) 10));
		
		// PbI
		ResolutionCategory PbIrC= resolutionCategories.get("PbI");
		
		l.add(InstanceFactory.newResolution("indéterminé","PbI-indetermine", PbIrC, (short) 1));
		l.add(InstanceFactory.newResolution("PC","PbI-PC", PbIrC, (short) 2));
		l.add(InstanceFactory.newResolution("écran","PbI-ecran", PbIrC, (short) 3));
		l.add(InstanceFactory.newResolution("espace disq insuf","PbI-espDisqInsuf", PbIrC, (short) 4));
		l.add(InstanceFactory.newResolution("logiciel","PbI-logiciel", PbIrC, (short) 5));
		l.add(InstanceFactory.newResolution("reboot PC","PbI-rebootPC", PbIrC, (short) 6));
		l.add(InstanceFactory.newResolution("erreur paramétrage run","PbI-parametrageRun", PbIrC, (short) 7));
		
		// Info
		ResolutionCategory InforC= resolutionCategories.get("Info");
				
		l.add(InstanceFactory.newResolution("run de validation","Info-runValidation", InforC, (short) 1));
		l.add(InstanceFactory.newResolution("arrêt séquenceur","Info-arretSeq", InforC, (short) 2));
		l.add(InstanceFactory.newResolution("arrêt logiciel","Info_arretLogiciel", InforC, (short) 3));
		l.add(InstanceFactory.newResolution("remboursement","Info-remboursement", InforC, (short) 4));
		l.add(InstanceFactory.newResolution("flowcell redéposée","Info-FCredeposee", InforC, (short) 5));		
		
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
		al.add("RARGUS");
		
		al.add("RMINION");
		al.add("RMKI");
		
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "runReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createReadSetResolutionCNS(ContextValidation ctx) {	
		List<Resolution> l = new ArrayList<Resolution>();

		// FDS 16/01 rendre moins verbeux avec variables XXrC
		
		// Run
		l.add(InstanceFactory.newResolution("lane abandonnée","Run-abandonLane", resolutionCategories.get("Run"), (short) 1));
		
		// LIB
		l.add(InstanceFactory.newResolution("pb protocole banque","LIB-pbProtocole", resolutionCategories.get("LIB"),(short) 1));
		l.add(InstanceFactory.newResolution("erreur dépôt banque","LIB-erreurDepot", resolutionCategories.get("LIB"),(short) 2));
		
		// Qte
		l.add(InstanceFactory.newResolution("seq valides insuf","Qte-seqValInsuf", resolutionCategories.get("Qte"),(short) 1));
		l.add(InstanceFactory.newResolution("seq utiles insuf","Qte-seqUtileInsuf", resolutionCategories.get("Qte"),(short) 2));
		
		// IND
		l.add(InstanceFactory.newResolution("pb demultiplexage","IND-pbDemultiplex", resolutionCategories.get("IND"),(short) 1));
		l.add(InstanceFactory.newResolution("pb manip","IND-pbManip", resolutionCategories.get("IND"),(short) 2));

		// Qlte
		ResolutionCategory QlterC= resolutionCategories.get("Qlte");
	
		l.add(InstanceFactory.newResolution("Q30","Qlte-Q30", QlterC,(short) 1));				
		l.add(InstanceFactory.newResolution("répartition bases","Qlte-repartitionBases", QlterC, (short) 2));				
		l.add(InstanceFactory.newResolution("adaptateurs/Kmers","Qlte-adapterKmer", QlterC,(short) 3));		
		l.add(InstanceFactory.newResolution("duplicat pairs > 20","Qlte-duplicatPairs", QlterC,(short) 4));
		l.add(InstanceFactory.newResolution("duplicat > 30","Qlte-duplicat", QlterC,(short) 5));
		
		// TAXO
		ResolutionCategory TAXOrC= resolutionCategories.get("TAXO");
			
		l.add(InstanceFactory.newResolution("conta indéterminée","TAXO-contaIndeterm", TAXOrC,(short) 1));
		l.add(InstanceFactory.newResolution("conta manip","TAXO-contaManip", TAXOrC,(short) 2));
		l.add(InstanceFactory.newResolution("conta mat ori","TAXO-contaMatOri", TAXOrC,(short) 3));
		l.add(InstanceFactory.newResolution("non conforme","TAXO-nonConforme", TAXOrC,(short) 4));
		l.add(InstanceFactory.newResolution("mitochondrie","TAXO-mitochondrie", TAXOrC,(short) 5));
		l.add(InstanceFactory.newResolution("chloroplast","TAXO-chloroplast", TAXOrC,(short) 6));
		l.add(InstanceFactory.newResolution("virus","TAXO-virus", TAXOrC,(short) 7));
		l.add(InstanceFactory.newResolution("bactérie","TAXO-bacteria", TAXOrC,(short) 8)); 
		l.add(InstanceFactory.newResolution("fungi","TAXO-fungi", TAXOrC,(short) 9));
				
		// RIBO
		l.add(InstanceFactory.newResolution("% rRNA élevé","RIBO-percEleve", resolutionCategories.get("RIBO"),(short) 1));
		
		// MAP
		l.add(InstanceFactory.newResolution("% MP","MAP-PercentMP", resolutionCategories.get("MAP"),(short) 1));
		l.add(InstanceFactory.newResolution("taille moyenne MP","MAP-tailleMP", resolutionCategories.get("MAP"),(short) 2));
		
		// MERG
		l.add(InstanceFactory.newResolution("% lec mergées","MERG-PercLecMerg", resolutionCategories.get("MERG"),(short) 1));
		l.add(InstanceFactory.newResolution("médiane lect mergées","MERG-MedLecMerg", resolutionCategories.get("MERG"),(short) 2));
		l.add(InstanceFactory.newResolution("distribution lect mergées","MERG-Distribution", resolutionCategories.get("MERG"),(short) 3));
	
		// Info
		l.add(InstanceFactory.newResolution("test Dev","Info-testDev", resolutionCategories.get("Info"),(short) 1));
		
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
		l.add(InstanceFactory.newResolution("cumul","CTG-cumul", CTGrC,(short)2));
		l.add(InstanceFactory.newResolution("nb contigs","CTG-nbCtgs", CTGrC,(short)3));
		l.add(InstanceFactory.newResolution("max size","CTG-maxSize", CTGrC,(short)4));
		l.add(InstanceFactory.newResolution("assembled reads","CTG-assReads", CTGrC,(short)5));
		
		// SIZE
		l.add(InstanceFactory.newResolution("% lost bases","SIZE-lostBasesPerc", resolutionCategories.get("SIZE"),(short)1));
		
		// SCAFF
		ResolutionCategory SCAFFrC= resolutionCategories.get("SCAFF");
				
		l.add(InstanceFactory.newResolution("N50","SCAFF-N50", SCAFFrC,(short) 1));
		l.add(InstanceFactory.newResolution("cumul","SCAFF-cumul", SCAFFrC,(short) 2));
		l.add(InstanceFactory.newResolution("nb scaff","SCAFF-nbScaff", SCAFFrC,(short) 3));
		l.add(InstanceFactory.newResolution("max size","SCAFF-maxSize", SCAFFrC,(short) 4));
		l.add(InstanceFactory.newResolution("median insert size","SCAFF-medInsertSize", SCAFFrC,(short) 5));
		l.add(InstanceFactory.newResolution("% satisfied pairs","SCAFF-satisfPairsPerc", SCAFFrC,(short) 6));
		l.add(InstanceFactory.newResolution("% N","SCAFF-Npercent", SCAFFrC,(short) 7));
		
		// GAP
		ResolutionCategory GAPrC= resolutionCategories.get("GAP");
			
		l.add(InstanceFactory.newResolution("gap sum","GAP-sum",GAPrC,(short) 1));
		l.add(InstanceFactory.newResolution("gap count","GAP-count",GAPrC,(short) 2));
		l.add(InstanceFactory.newResolution("corrected gap sum","GAP-correctedSum",GAPrC,(short) 3));
		l.add(InstanceFactory.newResolution("corrected gap count","GAP-correctedCount",GAPrC,(short) 4));
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

	// FDS pas de distingo CNS/CNG ??
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
	
	// FDS 16/01 il faudrait une resolutionCategorie autre que "Default" ???
	public static void createOpgenDepotResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());
		
		l.add(InstanceFactory.newResolution("nombre molécules insuffisant pour assemblage correct", "echec-nbMoleculesInsuf", resolutionCategories.get("Default"), (short) 4));
		l.add(InstanceFactory.newResolution("surface cassée", "echec-surface", resolutionCategories.get("Default"), (short) 5));	
		l.add(InstanceFactory.newResolution("problème digestion", "echec-digestion", resolutionCategories.get("Default"), (short) 6));	
		
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
		
		l.add(InstanceFactory.newResolution("réhybridation FC", "rehyb-FC", resolutionCategories.get("Default"), (short) 4));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "expIPDReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("ext-to-prepa-flowcell");
		al.add("prepa-flowcell");	
		al.add("illumina-depot");	
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, r.code);
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}
	
	public static void createIlluminaPrepFCDepotResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.addAll(getDefaultResolutionCNS());
		
		l.add(InstanceFactory.newResolution("réhybridation FC", "rehyb-FC", resolutionCategories.get("Default"), (short) 4));
		
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
	
	public static void createProcessResolution(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();

		l.add(InstanceFactory.newResolution("processus partiel","processus-partiel", resolutionCategories.get("Default"), (short) 1));
		
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
		
		l.add(InstanceFactory.newResolution("déroulement correct",	"correct", resolutionCategories.get("Default"), (short) 1));
		l.add(InstanceFactory.newResolution("problème signalé en commentaire", "pb-commentaire", resolutionCategories.get("Default"), (short) 2));
		l.add(InstanceFactory.newResolution("échec expérience", "echec-experience", resolutionCategories.get("Default"), (short) 3));	

		return l;
	}
}