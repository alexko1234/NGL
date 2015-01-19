package services.instance.resolution;

import static services.description.DescriptionFactory.newResolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionCategory;
import models.laboratory.resolutions.instance.ResolutionConfiguration;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import play.Logger.ALogger;
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


	public static void main(ContextValidation ctx) {	
		//FDS 15/01 reecriture...
		String inst=play.Play.application().configuration().getString("institute");
		
		if (inst.equals("CNG") || inst.equals("CNG") ) {
			Logger.info("1/ Create "+inst+ " resolution categories ...");
			resolutionCategories = createResolutionCategories(inst);
			
			Logger.info("2/ Save "+inst+ " resolutions to MongoDB...");
			saveResolutions(ctx, inst);
		}
		else {
			Logger.error("You need to specify only one institute !");
		}
		
		ctx.displayErrors(logger);
	}
	

	// FDS 15/01 fusion en 1 seule methode avec param Institute pour meilleure lisibilité
	public static HashMap<String, ResolutionCategory> createResolutionCategories(String inst){	
		HashMap<String, ResolutionCategory> resoCategories = new HashMap<String, ResolutionCategory>();
		
		//1-Run resolutions
		if ( inst.equals("CNG") ) {
			resoCategories.put("SAV", new ResolutionCategory("Problème qualité : SAV", (short) 10)); 
			resoCategories.put("PbM", new ResolutionCategory("Problème machine", (short) 20));
			resoCategories.put("PbR", new ResolutionCategory("Problème réactifs", (short) 30)); 
			resoCategories.put("LIB", new ResolutionCategory("Problème librairie", (short) 50)); 
			resoCategories.put("PbI", new ResolutionCategory("Problème informatique", (short) 60));
			resoCategories.put("RUN-Info", new ResolutionCategory("Informations", (short) 70)); 
			resoCategories.put("QC", new ResolutionCategory("Observations QC", (short) 80));
		}
		else {
			resoCategories.put("PbM", new ResolutionCategory("Problème machine", (short) 20));
			resoCategories.put("PbR", new ResolutionCategory("Problème réactifs", (short) 30)); 
			resoCategories.put("SAV", new ResolutionCategory("Problème qualité : SAV", (short) 40)); 
			resoCategories.put("PbI", new ResolutionCategory("Problème informatique", (short) 60));
			resoCategories.put("Info", new ResolutionCategory("Informations", (short) 70));        // FDS PB ??? Info est aussi definie dans ReadSet resolutions
																									// faut l'appeler RUN-Info comme pour CNG ???
		}
		
		//2-ReadSet resolutions
		if ( inst.equals("CNG") ) {
			resoCategories.put("Run", new ResolutionCategory("Problème run", (short) 5));
			resoCategories.put("Qte", new ResolutionCategory("Problème quantité", (short) 15));
			resoCategories.put("IND", new ResolutionCategory("Problème indexing", (short) 20));
			resoCategories.put("Qlte", new ResolutionCategory("Problème qualité", (short) 25));
			resoCategories.put("MAP", new ResolutionCategory("Problème mapping", (short) 40));
			resoCategories.put("Sample", new ResolutionCategory("Problème échantillon", (short) 55));
			resoCategories.put("LIMS", new ResolutionCategory("Problème déclaration LIMS", (short) 60));
			resoCategories.put("Info", new ResolutionCategory("Informations", (short) 65));		
		}
		else {
			resoCategories.put("Run", new ResolutionCategory("Problème run", (short) 5));
			resoCategories.put("LIB", new ResolutionCategory("Problème librairie", (short) 10));
			resoCategories.put("Qte", new ResolutionCategory("Problème quantité", (short) 15));
			resoCategories.put("IND", new ResolutionCategory("Problème indexing", (short) 20));
			resoCategories.put("Qlte", new ResolutionCategory("Problème qualité", (short) 25));
			resoCategories.put("TAXO", new ResolutionCategory("Problème taxon", (short) 30));
			resoCategories.put("RIBO", new ResolutionCategory("Problème ribosomes", (short) 35));
			resoCategories.put("MAP", new ResolutionCategory("Problème mapping", (short) 40));
			resoCategories.put("MERG", new ResolutionCategory("Problème merging", (short) 45));
			resoCategories.put("Info", new ResolutionCategory("Informations", (short) 50));
		}
		
		//3-Analysis resolutions
		if ( inst.equals("CNG") ) {
			Logger.warn("No CNG resolution categories for Analysis ???...");
		}
		else {
			resoCategories.put("BA-MERG", new ResolutionCategory("Merging", (short) 10)); 
			resoCategories.put("CTG", new ResolutionCategory("Contigage", (short) 20));
			resoCategories.put("SIZE", new ResolutionCategory("Size Filter", (short) 30));
			resoCategories.put("SCAFF", new ResolutionCategory("Scaffolding", (short) 40));
			resoCategories.put("GAP", new ResolutionCategory("Gap Closing", (short) 50));
		}

		/* FDS 16/01  manque optgenDepot Resolutions pour CNS ????????
		   if ( inst.equals("CNS") ) {
		     resoCategories.put("XXX", new ResolutionCategory("xxx", (short) 10)); 
		     resoCategories.put("YYY", new ResolutionCategory("yyy", (short) 20)); 
		    }
		*/
		
		//4-Experiment resolutions
		// pas d'infos pour l'instant...
		
		// default
		resoCategories.put("Default", new ResolutionCategory("Default", (short) 0));
		
		return resoCategories;
	}
	


	// FDS 15/01 fusion en 1 seule methode avec param inst
	public static void saveResolutions(ContextValidation ctx, String inst) {	
		if ( inst.equals("CNG") ) {
			createRunResolutionCNG(ctx); 
			createReadSetResolutionCNG(ctx); 
			// FDS 15/01 no Analysis Resolutions ???
			// FDS 15/01 No illumina Depot Resolutions ???
			createExperimentResolution(ctx);
		}
		else if ( inst.equals("CNS") ){
			createRunResolutionCNS(ctx); 
			createReadSetResolutionCNS(ctx); 
			createAnalysisResolutionCNS(ctx); 
			createOpgenDepotResolutionCNS(ctx);
			// FDS15/01 No illumina Depot Resolutions ???
			createExperimentResolution(ctx); 
		}
	}
	
	/* sub-methods */
	
	public static void createRunResolutionCNG(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		// FDS 16/01 rendre moins verbeux avec variables XXRc
		
		// PbM
		ResolutionCategory PbMRc= resolutionCategories.get("PbM");
		
		l.add(newResolution("indéterminé","PbM-indetermine", PbMRc, (short) 1));	
		l.add(newResolution("chiller","PbM-chiller", PbMRc, (short) 2));
		l.add(newResolution("pelletier","PbM-pelletier", PbMRc, (short) 3));
		l.add(newResolution("fluidique","PbM-fluidiq", PbMRc, (short) 4));
		l.add(newResolution("laser","PbM-laser", PbMRc, (short) 5));
		l.add(newResolution("camera","PbM-camera", PbMRc, (short) 6));
		l.add(newResolution("focus","PbM-focus", PbMRc, (short) 7));
		l.add(newResolution("pb de vide","PbM-pbVide", PbMRc, (short) 8));
		l.add(newResolution("PE module","PbM-PEmodule", PbMRc, (short) 9));
		l.add(newResolution("zone de dépôt","PbM-zoneDepot", PbMRc, (short) 10));				
		l.add(newResolution("cBot","PbM-cBot", PbMRc, (short) 11));		
		
		// SAV
		ResolutionCategory SAVRc= resolutionCategories.get("SAV");
		
		l.add(newResolution("intensité","SAV-intensite", SAVRc, (short) 1));
		l.add(newResolution("intensité faible A","SAV-intFbleA", SAVRc, (short) 2));
		l.add(newResolution("intensité faible T","SAV-intFbleT", SAVRc, (short) 3));
		l.add(newResolution("intensité faible C","SAV-intFbleC", SAVRc, (short) 4));
		l.add(newResolution("intensité faible G","SAV-intFbleG", SAVRc, (short) 5));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee", SAVRc, (short) 6));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible", SAVRc, (short) 7));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle", SAVRc, (short) 8));
		l.add(newResolution("%PF","SAV-PF", SAVRc, (short) 9));
		l.add(newResolution("phasing","SAV-phasing", SAVRc, (short) 10));
		l.add(newResolution("prephasing","SAV-prephasing", SAVRc, (short) 11));
		l.add(newResolution("error rate","SAV-errorRate", SAVRc, (short) 12));
		l.add(newResolution("focus","SAV-focus", SAVRc, (short) 13));
		l.add(newResolution("Q30","SAV-Q30", SAVRc, (short) 14));
		l.add(newResolution("% bases déséquilibré","SAV-perctBasesDeseq", SAVRc, (short) 15));
		l.add(newResolution("index non représenté","SAV-indexNonPresent", SAVRc, (short) 16));
		l.add(newResolution("index sous-représenté","SAV-indexFblePerc", SAVRc, (short) 17));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex", SAVRc, (short) 18));
		
		//PbR
		ResolutionCategory PbRRc= resolutionCategories.get("PbR");
		
		l.add(newResolution("indéterminé","PbR-indetermine", PbRRc, (short) 1));
		l.add(newResolution("flowcell","PbR-FC", PbRRc, (short) 2));
		l.add(newResolution("cBot","PbR-cBot", PbRRc, (short) 3));
		l.add(newResolution("séquencage","PbR-sequencage", PbRRc, (short) 4));
		l.add(newResolution("indexing","PbR-indexing", PbRRc, (short) 5));
		l.add(newResolution("PE module","PbR-PEmodule", PbRRc, (short) 6));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1", PbRRc, (short) 7));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2", PbRRc, (short) 8));
		l.add(newResolution("erreur réactifs","PbR-erreurReac", PbRRc, (short) 9));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac", PbRRc, (short) 10));
		
		//LIB
		ResolutionCategory LIBRc= resolutionCategories.get("LIB");
		
		l.add(newResolution("construction librairie","LIB-construction", LIBRc, (short) 1));
		l.add(newResolution("cause profil : librairie","LIB-profilIntLib", LIBRc, (short) 2));
		l.add(newResolution("cause profil : exp type","LIB-profilIntExpType", LIBRc, (short) 3));
		l.add(newResolution("pb dilution","LIB-pbDilution", LIBRc, (short) 4));
		l.add(newResolution("pb dilution spike-In","LIB-pbDilSpikeIn", LIBRc, (short) 5));
		
		//PbI
		ResolutionCategory PbIRc= resolutionCategories.get("PbI");
		
		l.add(newResolution("indéterminé","PbI-indetermine", PbIRc, (short) 1));
		l.add(newResolution("PC","PbI-PC", PbIRc, (short) 2));
		l.add(newResolution("écran","PbI-ecran", PbIRc, (short) 3));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf", PbIRc, (short) 4));
		l.add(newResolution("logiciel","PbI-logiciel", PbIRc, (short) 5));
		l.add(newResolution("reboot PC","PbI-rebootPC", PbIRc, (short) 6));
		l.add(newResolution("retard robocopy","PbI-robocopy", PbIRc, (short) 7));
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun", PbIRc, (short) 8));
		
		//RUN-Info
		ResolutionCategory RUNInfoRc= resolutionCategories.get("RUN-Info");
		
		l.add(newResolution("run de validation","Info-runValidation", RUNInfoRc, (short) 1));
		l.add(newResolution("remboursement","Info-remboursement", RUNInfoRc, (short) 2));

		//QC
		ResolutionCategory QCRc= resolutionCategories.get("QC");
		
		l.add(newResolution("intensité B.M.S","QC-intBMS", QCRc, (short) 1));
		l.add(newResolution("tiles out","QC-tilesOut", QCRc, (short) 2));
		l.add(newResolution("saut de chimie","QC-sautChimie", QCRc, (short) 3));
		
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
		
		// FDS 16/01 rendre moins verbeux avec variables XXRc

		// Run
		l.add(newResolution("lane abandonnée","Run-abandonLane", resolutionCategories.get("Run"), (short) 1));
		
		//Qte
		l.add(newResolution("nb seq brutes faible","Qte-seqRawInsuf", resolutionCategories.get("Qte"),(short) 1));
		l.add(newResolution("couverture en X hors spec.","Qte-couverture", resolutionCategories.get("Qte"),(short) 2));
	
		//IND
		l.add(newResolution("index incorrect","IND-indexIncorrect", resolutionCategories.get("IND"),(short) 1));
				
		//Qlte
		ResolutionCategory QlteRc= resolutionCategories.get("Qlte");
		
		l.add(newResolution("Q30 hors spec.","Qlte-Q30HorsSpec", QlteRc,(short) 1));
		l.add(newResolution("répartition bases","Qlte-repartitionBases", QlteRc, (short) 2));
		l.add(newResolution("% adaptateurs détectés","Qlte-adapterPercent", QlteRc,(short) 3));
		l.add(newResolution("% duplicat élevé","Qlte-duplicatElevee", QlteRc,(short) 4));	
		l.add(newResolution("% NT 30X","Qlte-30XntPercent", QlteRc,(short)5));
		l.add(newResolution("% Target","Qlte-targetPercent", QlteRc,(short)6));

		// MAP
		l.add(newResolution("% mapping faible","MAP-PercMappingFble", resolutionCategories.get("MAP"),(short) 1));
		
		// Sample
		l.add(newResolution("sexe incorrect","Sample-sexeIncorrect", resolutionCategories.get("Sample"),(short) 1));
		
		// Info
		ResolutionCategory InfoRc= resolutionCategories.get("Info");
		
		l.add(newResolution("test Dev","Info-testDev", InfoRc,(short) 1));
		l.add(newResolution("test Prod","Info-testProd", InfoRc,(short) 2));
		l.add(newResolution("redo effectué","Info-redoDone", InfoRc,(short) 3));
		
		// LIMS
		l.add(newResolution("erreur Experimental Type","LIMS-erreurExpType", resolutionCategories.get("LIMS"),(short) 1));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "readSetReso";
		r.resolutions = l;
		r.objectTypeCode = "ReadSet";
		ArrayList<String> al = new ArrayList<String>();
		al.add("default-readset");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "readSetReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createRunResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		// FDS 16/01 rendre moins verbeux avec variables XXRc
		
		// PbM
		ResolutionCategory PbMRc= resolutionCategories.get("PbM");

		l.add(newResolution("indéterminé","PbM-indetermine", PbMRc,  (short) 1));
		l.add(newResolution("chiller","PbM-chiller", PbMRc, (short) 2));
		l.add(newResolution("pelletier","PbM-pelletier", PbMRc, (short) 3));
		l.add(newResolution("fluidique","PbM-fluidiq", PbMRc, (short) 4));
		l.add(newResolution("laser","PbM-laser", PbMRc, (short) 5));
		l.add(newResolution("camera","PbM-camera", PbMRc, (short) 6));
		l.add(newResolution("focus","PbM-focus", PbMRc, (short) 7));    
		l.add(newResolution("pb de vide","PbM-pbVide", PbMRc, (short) 8));
		l.add(newResolution("PE module","PbM-PEmodule", PbMRc, (short) 9));
		l.add(newResolution("cBot","PbM-cBot", PbMRc, (short) 10));		
			
		// PbR
		ResolutionCategory PbRRc= resolutionCategories.get("PbR");
		
		l.add(newResolution("indéterminé","PbR-indetermine", PbRRc, (short) 1));
		l.add(newResolution("flowcell","PbR-FC", PbRRc, (short) 2));
		l.add(newResolution("cBot","PbR-cBot", PbRRc, (short) 3));
		l.add(newResolution("séquencage","PbR-sequencage", PbRRc, (short) 4));
		l.add(newResolution("indexing","PbR-indexing", PbRRc, (short) 5));
		l.add(newResolution("PE module","PbR-PEmodule", PbRRc, (short) 6));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1", PbRRc, (short) 7));
		l.add(newResolution("rehyb indexing","PbR-rehybIndexing", PbRRc, (short) 8));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2", PbRRc, (short) 9));
		l.add(newResolution("erreur réactifs","PbR-erreurReac", PbRRc, (short) 10));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac", PbRRc, (short) 11));

		// SAV
		ResolutionCategory SAVRc= resolutionCategories.get("SAV");
		
		l.add(newResolution("intensité","SAV-intensite", SAVRc, (short) 1));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee", SAVRc, (short) 2));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible", SAVRc, (short) 3));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle", SAVRc, (short) 4));
		l.add(newResolution("%PF","SAV-PF", SAVRc, (short) 5));
		l.add(newResolution("phasing","SAV-phasing", SAVRc, (short) 6));
		l.add(newResolution("prephasing","SAV-prephasing", SAVRc, (short) 7));
		l.add(newResolution("error rate","SAV-errorRate", SAVRc, (short) 8));
		l.add(newResolution("Q30","SAV-Q30", SAVRc, (short) 9));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex", SAVRc, (short) 10));
		
		// PbI
		ResolutionCategory PbIRc= resolutionCategories.get("PbI");
		
		l.add(newResolution("indéterminé","PbI-indetermine", PbIRc, (short) 1));
		l.add(newResolution("PC","PbI-PC", PbIRc, (short) 2));
		l.add(newResolution("écran","PbI-ecran", PbIRc, (short) 3));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf", PbIRc, (short) 4));
		l.add(newResolution("logiciel","PbI-logiciel", PbIRc, (short) 5));
		l.add(newResolution("reboot PC","PbI-rebootPC", PbIRc, (short) 6));
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun", PbIRc, (short) 7));
		
		// Info
		ResolutionCategory InfoRc= resolutionCategories.get("Info");
		
		l.add(newResolution("run de validation","Info-runValidation", InfoRc, (short) 1));
		l.add(newResolution("arrêt séquenceur","Info-arretSeq", InfoRc, (short) 2));
		l.add(newResolution("arrêt logiciel","Info_arretLogiciel", InfoRc, (short) 3));
		l.add(newResolution("remboursement","Info-remboursement", InfoRc, (short) 4));
		l.add(newResolution("flowcell redéposée","Info-FCredeposee", InfoRc, (short) 5));		
		
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
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "runReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createReadSetResolutionCNS(ContextValidation ctx) {	
		List<Resolution> l = new ArrayList<Resolution>();

		// FDS 16/01 rendre moins verbeux avec variables XXRc
		
		// Run
		l.add(newResolution("lane abandonnée","Run-abandonLane", resolutionCategories.get("Run"), (short) 1));
		
		// LIB
		l.add(newResolution("pb protocole banque","LIB-pbProtocole", resolutionCategories.get("LIB"),(short) 1));
		l.add(newResolution("erreur dépôt banque","LIB-erreurDepot", resolutionCategories.get("LIB"),(short) 2));
		
		// Qte
		l.add(newResolution("seq valides insuf","Qte-seqValInsuf", resolutionCategories.get("Qte"),(short) 1));
		l.add(newResolution("seq utiles insuf","Qte-seqUtileInsuf", resolutionCategories.get("Qte"),(short) 2));
		
		// IND
		l.add(newResolution("pb demultiplexage","IND-pbDemultiplex", resolutionCategories.get("IND"),(short) 1));
		l.add(newResolution("pb manip","IND-pbManip", resolutionCategories.get("IND"),(short) 2));

		// Qlte
		ResolutionCategory QlteRc= resolutionCategories.get("Qlte");
		
		l.add(newResolution("Q30","Qlte-Q30", QlteRc,(short) 1));				
		l.add(newResolution("répartition bases","Qlte-repartitionBases", QlteRc, (short) 2));				
		l.add(newResolution("adaptateurs/Kmers","Qlte-adapterKmer", QlteRc,(short) 3));		
		l.add(newResolution("duplicat > 30","Qlte-duplicat", QlteRc,(short) 4));
				
		// TAXO
		ResolutionCategory TAXORc= resolutionCategories.get("TAXO");
		
		l.add(newResolution("conta indéterminée","TAXO-contaIndeterm", TAXORc,(short) 1));
		l.add(newResolution("conta manip","TAXO-contaManip", TAXORc,(short) 2));
		l.add(newResolution("conta mat ori","TAXO-contaMatOri", TAXORc,(short) 3));
		l.add(newResolution("non conforme","TAXO-nonConforme", TAXORc,(short) 4));
		l.add(newResolution("mitochondrie","TAXO-mitochondrie", TAXORc,(short) 5));
		l.add(newResolution("chloroplast","TAXO-chloroplast", TAXORc,(short) 6));
		l.add(newResolution("virus","TAXO-virus", TAXORc,(short) 7));
		l.add(newResolution("bactérie","TAXO-bacteria", TAXORc,(short) 8)); 
		l.add(newResolution("fungi","TAXO-fungi", TAXORc,(short) 9));
				
		// RIBO
		l.add(newResolution("% rRNA élevé","RIBO-percEleve", resolutionCategories.get("RIBO"),(short) 1));
		
		// MAP
		l.add(newResolution("% MP","MAP-PercentMP", resolutionCategories.get("MAP"),(short) 1));
		l.add(newResolution("taille moyenne MP","MAP-tailleMP", resolutionCategories.get("MAP"),(short) 2));
		
		// MERG
		l.add(newResolution("% lec mergées","MERG-PercLecMerg", resolutionCategories.get("MERG"),(short) 1));
		l.add(newResolution("médiane lect mergées","MERG-MedLecMerg", resolutionCategories.get("MERG"),(short) 2));
		
		// Info
		l.add(newResolution("test Dev","Info-testDev", resolutionCategories.get("Info"),(short) 1));
		
		ResolutionConfiguration r = new ResolutionConfiguration();
		r.code = "readSetReso";
		r.resolutions = l;
		r.objectTypeCode = "ReadSet";
		ArrayList<String> al = new ArrayList<String>();
		 al.add("default-readset");
		 al.add("RSARGUS");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class, "readSetReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createAnalysisResolutionCNS(ContextValidation ctx) {
		List<Resolution> l = new ArrayList<Resolution>();
		
		// FDS 16/01 rendre moins verbeux avec variables XXRc

		// BA-MERG
		l.add(newResolution("% merging","MERG-BA-MERGPercent", resolutionCategories.get("BA-MERG"),(short) 1));
		l.add(newResolution("reads size","MERG-readSize", resolutionCategories.get("BA-MERG"),(short) 2));
		
		// CTG
		ResolutionCategory CTGRc= resolutionCategories.get("CTG");
		
		l.add(newResolution("N50","CTG-N50", CTGRc,(short) 1));
		l.add(newResolution("cumul","CTG-cumul", CTGRc,(short)2));
		l.add(newResolution("nb contigs","CTG-nbCtgs", CTGRc,(short)3));
		l.add(newResolution("max size","CTG-maxSize", CTGRc,(short)4));
		l.add(newResolution("assembled reads","CTG-assReads", CTGRc,(short)5));
		
		// SIZE
		l.add(newResolution("% lost bases","SIZE-lostBasesPerc", resolutionCategories.get("SIZE"),(short)1));
		
		// SCAFF
		ResolutionCategory SCAFFRc= resolutionCategories.get("SCAFF");
		
		l.add(newResolution("N50","SCAFF-N50", SCAFFRc,(short) 1));
		l.add(newResolution("cumul","SCAFF-cumul", SCAFFRc,(short) 2));
		l.add(newResolution("nb scaff","SCAFF-nbScaff", SCAFFRc,(short) 3));
		l.add(newResolution("max size","SCAFF-maxSize", SCAFFRc,(short) 4));
		l.add(newResolution("median insert size","SCAFF-medInsertSize", SCAFFRc,(short) 5));
		l.add(newResolution("% satisfied pairs","SCAFF-satisfPairsPerc", SCAFFRc,(short) 6));
		l.add(newResolution("% N","SCAFF-Npercent", SCAFFRc,(short) 7));
		
		// GAP
		ResolutionCategory GAPRc= resolutionCategories.get("GAP");
		
		l.add(newResolution("gap sum","GAP-sum",GAPRc,(short) 1));
		l.add(newResolution("gap count","GAP-count",GAPRc,(short) 2));
		l.add(newResolution("corrected gap sum","GAP-correctedSum",GAPRc,(short) 3));
		l.add(newResolution("corrected gap count","GAP-correctedCount",GAPRc,(short) 4));
		l.add(newResolution("% N","GAP-Npercent",GAPRc,(short) 5));
		
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
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.add(newResolution("déroulement correct",	"correct", resolutionCategories.get("Default"), (short) 1));
		l.add(newResolution("échec expérience", "echec-experience", resolutionCategories.get("Default"), (short) 2));	
		
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
		
		l.add(newResolution("déroulement correct",	"correct", resolutionCategories.get("Default"), (short) 1));       // FDS deja declare plus haut !!!
		l.add(newResolution("échec expérience", "echec-experience", resolutionCategories.get("Default"), (short) 2));  // FDS deja declare plus haut !!!
		
		l.add(newResolution("nombre molécules insuffisant pour assemblage correct", "echec-nbMoleculesInsuf", resolutionCategories.get("Default"), (short) 3));
		l.add(newResolution("surface cassée", "echec-surface", resolutionCategories.get("Default"), (short) 4));	
		l.add(newResolution("problème digestion", "echec-digestion", resolutionCategories.get("Default"), (short) 5));	
		
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
}