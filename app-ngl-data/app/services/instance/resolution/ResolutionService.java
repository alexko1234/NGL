package services.instance.resolution;

import static services.description.DescriptionFactory.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import play.Logger;
import services.description.DescriptionFactory;

import fr.cea.ig.MongoDBDAO;

import validation.ContextValidation;
import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionCategory;
import models.laboratory.resolutions.instance.ResolutionConfigurations;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;

public class ResolutionService {
	

	private static HashMap<String, ResolutionCategory> resolutionCategories = createResolutionCategories(); 


	public static void main(ContextValidation ctx) {		
		Logger.debug("Start create resolutions");
		boolean all = true;
		if (play.Play.application().configuration().getString("institute") != null) {
			if (play.Play.application().configuration().getString("institute").equals("CNG")) {
				saveResolutionsCNG(ctx);
				all = false;
			}
			if (play.Play.application().configuration().getString("institute").equals("CNS")) {
				saveResolutionsCNS(ctx);
				all = false;
			}
		}
		if (all) {
			saveResolutionsCNG(ctx);
			saveResolutionsCNS(ctx);
		}
		Logger.debug("End create resolutions");
	}
	

	
	public static HashMap<String, ResolutionCategory> createResolutionCategories(){	
		HashMap<String, ResolutionCategory> resoCategories = new HashMap<String, ResolutionCategory>();
		
		//Run
		resoCategories.put("PbM", new ResolutionCategory("Problème machine", (short) 20));
		resoCategories.put("PbR", new ResolutionCategory("Problème réactifs", (short) 30)); 
		resoCategories.put("SAV", new ResolutionCategory("Problème qualité : SAV", (short) 40));
		resoCategories.put("RUN-LIB", new ResolutionCategory("Problème librairie", (short) 50));
		resoCategories.put("PbI", new ResolutionCategory("Problème informatique", (short) 60));
		resoCategories.put("RUN-Info", new ResolutionCategory("Informations", (short) 70));
		resoCategories.put("QC", new ResolutionCategory("Observations QC", (short) 80));
		//ReadSet
		resoCategories.put("Run", new ResolutionCategory("Problème run", (short) 5));
		resoCategories.put("LIB", new ResolutionCategory("Problème librairie", (short) 10));
		resoCategories.put("Qte", new ResolutionCategory("Problème quantité", (short) 15));
		resoCategories.put("IND", new ResolutionCategory("Problème indexing", (short) 20));
		resoCategories.put("Qlte", new ResolutionCategory("Problème qualité", (short) 25));
		resoCategories.put("TAXO", new ResolutionCategory("Problème taxon", (short) 30));
		resoCategories.put("RIBO", new ResolutionCategory("Problème ribosomes", (short) 35));
		resoCategories.put("MAP", new ResolutionCategory("Problème mapping", (short) 40));
		resoCategories.put("MERG", new ResolutionCategory("Problème BA-MERG", (short) 45));	
		resoCategories.put("Sample", new ResolutionCategory("Problème échantillon", (short) 55));
		resoCategories.put("LIMS", new ResolutionCategory("Problème déclaration LIMS", (short) 60));
		resoCategories.put("INFO", new ResolutionCategory("Informations", (short) 65));		
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
	

	
	public static void saveResolutionsCNG(ContextValidation ctx) {	
		createRunResolutionCNG(ctx); 
		createReadSetResolutionCNG(ctx); 				
	}
	
	
	
	
	public static void saveResolutionsCNS(ContextValidation ctx) {	
		createRunResolutionCNS(ctx); 
		createReadSetResolutionCNS(ctx); 
		createAnalysisResolutionCNS(ctx); 
		createExperimentResolutionCNS(ctx); 
	}
	
	
	
	/* sub-methods */
	
	
	
	public static void createRunResolutionCNG(ContextValidation ctx) {
		
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.add(newResolution("indéterminé","PbM-indetermine", resolutionCategories.get("PbM"), (short) 1));			
		l.add(newResolution("chiller","PbM-chiller", resolutionCategories.get("PbM"), (short) 2));
		l.add(newResolution("pelletier","PbM-pelletier", resolutionCategories.get("PbM"), (short) 3));
		l.add(newResolution("fluidique","PbM-fluidiq", resolutionCategories.get("PbM"), (short) 4));
		l.add(newResolution("laser","PbM-laser", resolutionCategories.get("PbM"), (short) 5));
		l.add(newResolution("camera","PbM-camera", resolutionCategories.get("PbM"), (short) 6));
		l.add(newResolution("focus","PbM-focus", resolutionCategories.get("PbM"), (short) 7));
		l.add(newResolution("pb de vide","PbM-pbVide", resolutionCategories.get("PbM"), (short) 8));
		l.add(newResolution("PE module","PbM-PEmodule", resolutionCategories.get("PbM"), (short) 9));
		l.add(newResolution("zone de dépôt","PbM-zoneDepot", resolutionCategories.get("PbM"), (short) 10));				
		l.add(newResolution("cBot","PbM-cBot", resolutionCategories.get("PbM"), (short) 11));		
			
		l.add(newResolution("indéterminé","PbR-indetermine", resolutionCategories.get("PbR"), (short) 1));
		l.add(newResolution("flowcell","PbR-FC", resolutionCategories.get("PbR"), (short) 2));
		l.add(newResolution("cBot","PbR-cBot", resolutionCategories.get("PbR"), (short) 3));
		l.add(newResolution("séquencage","PbR-sequencage", resolutionCategories.get("PbR"), (short) 4));
		l.add(newResolution("indexing","PbR-indexing", resolutionCategories.get("PbR"), (short) 5));
		l.add(newResolution("PE module","PbR-PEmodule", resolutionCategories.get("PbR"), (short) 6));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1", resolutionCategories.get("PbR"), (short) 7));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2", resolutionCategories.get("PbR"), (short) 8));
		l.add(newResolution("erreur réactifs","PbR-erreurReac", resolutionCategories.get("PbR"), (short) 9));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac", resolutionCategories.get("PbR"), (short) 10));
		
		l.add(newResolution("intensité","SAV-intensite", resolutionCategories.get("SAV"), (short) 1));		
		l.add(newResolution("intensité faible A","SAV-intFbleA", resolutionCategories.get("SAV"), (short) 2));
		l.add(newResolution("intensité faible T","SAV-intFbleT", resolutionCategories.get("SAV"), (short) 3));
		l.add(newResolution("intensité faible C","SAV-intFbleC", resolutionCategories.get("SAV"), (short) 4));
		l.add(newResolution("intensité faible G","SAV-intFbleG", resolutionCategories.get("SAV"), (short) 5));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee", resolutionCategories.get("SAV"), (short) 6));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible", resolutionCategories.get("SAV"), (short) 7));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle", resolutionCategories.get("SAV"), (short) 8));
		l.add(newResolution("%PF","SAV-PF", resolutionCategories.get("SAV"), (short) 9));
		l.add(newResolution("phasing","SAV-phasing", resolutionCategories.get("SAV"), (short) 10));
		l.add(newResolution("prephasing","SAV-prephasing", resolutionCategories.get("SAV"), (short) 11));
		l.add(newResolution("error rate","SAV-errorRate", resolutionCategories.get("SAV"), (short) 12));
		l.add(newResolution("focus","SAV-focus", resolutionCategories.get("SAV"), (short) 13));
		l.add(newResolution("Q30","SAV-Q30", resolutionCategories.get("SAV"), (short) 14));
		l.add(newResolution("% bases déséquilibré","SAV-perctBasesDeseq", resolutionCategories.get("SAV"), (short) 15));
		l.add(newResolution("index non représenté","SAV-indexNonPresent", resolutionCategories.get("SAV"), (short) 16));
		l.add(newResolution("index sous-représenté","SAV-indexFblePerc", resolutionCategories.get("SAV"), (short) 17));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex", resolutionCategories.get("SAV"), (short) 18));
		
		l.add(newResolution("construction librairie","LIB-construction", resolutionCategories.get("RUN-LIB"), (short) 1));
		l.add(newResolution("cause profil : librairie","LIB-profilIntLib", resolutionCategories.get("RUN-LIB"), (short) 2));
		l.add(newResolution("cause profil : exp type","LIB-profilIntExpType", resolutionCategories.get("RUN-LIB"), (short) 3));
		l.add(newResolution("pb dilution","LIB-pbDilution", resolutionCategories.get("RUN-LIB"), (short) 4));
		l.add(newResolution("pb dilution spike-In","LIB-pbDilSpikeIn", resolutionCategories.get("RUN-LIB"), (short) 5));
		
		l.add(newResolution("indéterminé","PbI-indetermine", resolutionCategories.get("PbI"), (short) 1));
		l.add(newResolution("PC","PbI-PC", resolutionCategories.get("PbI"), (short) 2));
		l.add(newResolution("écran","PbI-ecran", resolutionCategories.get("PbI"), (short) 3));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf", resolutionCategories.get("PbI"), (short) 4));
		l.add(newResolution("logiciel","PbI-logiciel", resolutionCategories.get("PbI"), (short) 5));
		l.add(newResolution("reboot PC","PbI-rebootPC", resolutionCategories.get("PbI"), (short) 6));
		l.add(newResolution("retard robocopy","PbI-robocopy", resolutionCategories.get("PbI"), (short) 7));
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun", resolutionCategories.get("PbI"), (short) 8));
		
		l.add(newResolution("run de validation","Info-runValidation", resolutionCategories.get("RUN-Info"), (short) 1));
		l.add(newResolution("remboursement","Info-remboursement", resolutionCategories.get("RUN-Info"), (short) 2));

		l.add(newResolution("intensité B.M.S","QC-intBMS", resolutionCategories.get("QC"), (short) 1));
		l.add(newResolution("tiles out","QC-tilesOut", resolutionCategories.get("QC"), (short) 2));
		l.add(newResolution("saut de chimie","QC-sautChimie", resolutionCategories.get("QC"), (short) 3));
		
		ResolutionConfigurations r = new ResolutionConfigurations();
		r.code = "runReso";
		r.resolutions = l;
		r.objectTypeCode = "Run";
		ArrayList<String> al = new ArrayList<String>();
		al.add("RHS2000");
		al.add("RHS2500");
		al.add("RHS2500R");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, "runReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createReadSetResolutionCNG(ContextValidation ctx) {
		
		List<Resolution> l = new ArrayList<Resolution>();

		l.add(newResolution("lane abandonnée","Run-abandonLane", resolutionCategories.get("Run"), (short) 1));
		
		l.add(newResolution("nb seq brutes faible","Qte-seqRawInsuf", resolutionCategories.get("Qte"),(short) 1));
		l.add(newResolution("couverture en X hors spec.","Qte-couverture", resolutionCategories.get("Qte"),(short) 2));
	
		l.add(newResolution("index incorrect","IND-indexIncorrect", resolutionCategories.get("IND"),(short) 1));
				
		l.add(newResolution("Q30 hors spec.","Qlte-Q30HorsSpec", resolutionCategories.get("Qlte"),(short) 1));
		l.add(newResolution("répartition bases","Qlte-repartitionBases", resolutionCategories.get("Qlte"), (short) 2));
		l.add(newResolution("% adaptateurs détectés","Qlte-adapterPercent", resolutionCategories.get("Qlte"),(short) 3));
		l.add(newResolution("% duplicat élevé","Qlte-duplicatElevee", resolutionCategories.get("Qlte"),(short) 4));
						
		l.add(newResolution("% mapping faible","MAP-PercMappingFble", resolutionCategories.get("MAP"),(short) 1));
		
		l.add(newResolution("test Dev","Info-testDev", resolutionCategories.get("INFO"),(short) 1));
		l.add(newResolution("test Prod","Info-testProd", resolutionCategories.get("INFO"),(short) 2));
		
		l.add(newResolution("sexe incorrect","Sample-sexeIncorrect", resolutionCategories.get("Sample"),(short) 1));
		
		l.add(newResolution("erreur Experimental Type","LIMS-erreurExpType", resolutionCategories.get("LIMS"),(short) 1));
		
		ResolutionConfigurations r = new ResolutionConfigurations();
		r.code = "readSetReso";
		r.resolutions = l;
		r.objectTypeCode = "ReadSet";
		ArrayList<String> al = new ArrayList<String>();
		al.add("default-readset");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, "readSetReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createRunResolutionCNS(ContextValidation ctx) {
		
		List<Resolution> l = new ArrayList<Resolution>();

		l.add(newResolution("indéterminé","PbM-indetermine", resolutionCategories.get("PbM"),  (short) 1));
		l.add(newResolution("chiller","PbM-chiller", resolutionCategories.get("PbM"), (short) 2));
		l.add(newResolution("pelletier","PbM-pelletier", resolutionCategories.get("PbM"), (short) 3));
		l.add(newResolution("fluidique","PbM-fluidiq", resolutionCategories.get("PbM"), (short) 4));
		l.add(newResolution("laser","PbM-laser", resolutionCategories.get("PbM"), (short) 5));
		l.add(newResolution("camera","PbM-camera", resolutionCategories.get("PbM"), (short) 6));
		l.add(newResolution("focus","PbM-focus", resolutionCategories.get("PbM"), (short) 7));
		l.add(newResolution("pb de vide","PbM-pbVide", resolutionCategories.get("PbM"), (short) 8));
		l.add(newResolution("PE module","PbM-PEmodule", resolutionCategories.get("PbM"), (short) 9));
		l.add(newResolution("cBot","PbM-cBot", resolutionCategories.get("PbM"), (short) 10));		
			
		l.add(newResolution("indéterminé","PbR-indetermine", resolutionCategories.get("PbR"), (short) 1));
		l.add(newResolution("flowcell","PbR-FC", resolutionCategories.get("PbR"), (short) 2));
		l.add(newResolution("cBot","PbR-cBot", resolutionCategories.get("PbR"), (short) 3));
		l.add(newResolution("séquencage","PbR-sequencage", resolutionCategories.get("PbR"), (short) 4));
		l.add(newResolution("indexing","PbR-indexing", resolutionCategories.get("PbR"), (short) 5));
		l.add(newResolution("PE module","PbR-PEmodule", resolutionCategories.get("PbR"), (short) 6));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1", resolutionCategories.get("PbR"), (short) 7));
		l.add(newResolution("rehyb indexing","PbR-rehybIndexing", resolutionCategories.get("PbR"), (short) 8));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2", resolutionCategories.get("PbR"), (short) 9));
		l.add(newResolution("erreur réactifs","PbR-erreurReac", resolutionCategories.get("PbR"), (short) 10));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac", resolutionCategories.get("PbR"), (short) 11));

		l.add(newResolution("intensité","SAV-intensite", resolutionCategories.get("SAV"), (short) 1));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee", resolutionCategories.get("SAV"), (short) 2));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible", resolutionCategories.get("SAV"), (short) 3));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle", resolutionCategories.get("SAV"), (short) 4));
		l.add(newResolution("%PF","SAV-PF", resolutionCategories.get("SAV"), (short) 5));
		l.add(newResolution("phasing","SAV-phasing", resolutionCategories.get("SAV"), (short) 6));
		l.add(newResolution("prephasing","SAV-prephasing", resolutionCategories.get("SAV"), (short) 7));
		l.add(newResolution("error rate","SAV-errorRate", resolutionCategories.get("SAV"), (short) 8));
		l.add(newResolution("Q30","SAV-Q30", resolutionCategories.get("SAV"), (short) 9));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex", resolutionCategories.get("SAV"), (short) 10));
		
		l.add(newResolution("indéterminé","PbI-indetermine", resolutionCategories.get("PbI"), (short) 1));
		l.add(newResolution("PC","PbI-PC", resolutionCategories.get("PbI"), (short) 2));
		l.add(newResolution("écran","PbI-ecran", resolutionCategories.get("PbI"), (short) 3));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf", resolutionCategories.get("PbI"), (short) 4));
		l.add(newResolution("logiciel","PbI-logiciel", resolutionCategories.get("PbI"), (short) 5));
		l.add(newResolution("reboot PC","PbI-rebootPC", resolutionCategories.get("PbI"), (short) 6));
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun", resolutionCategories.get("PbI"), (short) 7));
		
		l.add(newResolution("run de validation","Info-runValidation", resolutionCategories.get("RUN-Info"), (short) 1));
		l.add(newResolution("arret séquenceur","Info-arretSeq", resolutionCategories.get("RUN-Info"), (short) 2));
		l.add(newResolution("arret logiciel","Info_arretLogiciel", resolutionCategories.get("RUN-Info"), (short) 3));
		l.add(newResolution("remboursement","Info-remboursement", resolutionCategories.get("RUN-Info"), (short) 4));
		l.add(newResolution("flowcell redéposée","Info-FCredeposee", resolutionCategories.get("RUN-Info"), (short) 5));		
		
		ResolutionConfigurations r = new ResolutionConfigurations();
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
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, "runReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createReadSetResolutionCNS(ContextValidation ctx) {
		
		List<Resolution> l = new ArrayList<Resolution>();

		l.add(newResolution("lane abandonnée","Run-abandonLane", resolutionCategories.get("Run"), (short) 1));

		l.add(newResolution("Q30","Qlte-Q30", resolutionCategories.get("Qlte"),(short) 1));				
		l.add(newResolution("répartition bases","Qlte-repartitionBases", resolutionCategories.get("Qlte"), (short) 2));				
		l.add(newResolution("adaptateurs/Kmers","Qlte-adapterKmer", resolutionCategories.get("Qlte"),(short) 3));		
		l.add(newResolution("duplicat > 30","Qlte-duplicat", resolutionCategories.get("Qlte"),(short) 4));
		
		l.add(newResolution("pb protocole banque","LIB-pbProtocole", resolutionCategories.get("LIB"),(short) 1));
		l.add(newResolution("erreur dépôt banque","LIB-erreurDepot", resolutionCategories.get("LIB"),(short) 2));
		
		l.add(newResolution("seq valides insuf","Qte-seqValInsuf", resolutionCategories.get("Qte"),(short) 1));
		l.add(newResolution("seq utiles insuf","Qte-seqUtileInsuf", resolutionCategories.get("Qte"),(short) 2));

		l.add(newResolution("pb demultiplexage","IND-pbDemultiplex", resolutionCategories.get("IND"),(short) 1));
		l.add(newResolution("pb manip","IND-pbManip", resolutionCategories.get("IND"),(short) 2));
				
		l.add(newResolution("conta indéterminée","TAXO-contaIndeterm", resolutionCategories.get("TAXO"),(short) 1));
		l.add(newResolution("conta manip","TAXO-contaManip", resolutionCategories.get("TAXO"),(short) 2));
		l.add(newResolution("conta mat ori","TAXO-contaMatOri", resolutionCategories.get("TAXO"),(short) 3));
		l.add(newResolution("non conforme","TAXO-nonConforme", resolutionCategories.get("TAXO"),(short) 4));
		l.add(newResolution("mitochondrie","TAXO-mitochondrie", resolutionCategories.get("TAXO"),(short) 5));
		l.add(newResolution("chloroplast","TAXO-chloroplast", resolutionCategories.get("TAXO"),(short) 6));
		l.add(newResolution("virus","TAXO-virus", resolutionCategories.get("TAXO"),(short) 7));
		l.add(newResolution("bactérie","TAXO-bacteria", resolutionCategories.get("TAXO"),(short) 8)); 
		l.add(newResolution("fungi","TAXO-fungi", resolutionCategories.get("TAXO"),(short) 9));
				
		l.add(newResolution("% rRNA élevé","RIBO-percEleve", resolutionCategories.get("RIBO"),(short) 1));
		
		l.add(newResolution("% MP","MAP-PercentMP", resolutionCategories.get("MAP"),(short) 1));
		l.add(newResolution("taille moyenne MP","MAP-tailleMP", resolutionCategories.get("MAP"),(short) 2));
		
		l.add(newResolution("% lec mergées","MERG-PercLecMerg", resolutionCategories.get("MERG"),(short) 1));
		l.add(newResolution("médiane lect mergées","MERG-MedLecMerg", resolutionCategories.get("MERG"),(short) 2));
		
		ResolutionConfigurations r = new ResolutionConfigurations();
		r.code = "readSetReso";
		r.resolutions = l;
		r.objectTypeCode = "ReadSet";
		ArrayList<String> al = new ArrayList<String>();
		 al.add("default-readset");
		 al.add("RSARGUS");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, "readSetReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	public static void createAnalysisResolutionCNS(ContextValidation ctx) {
		
		List<Resolution> l = new ArrayList<Resolution>();

		l.add(newResolution("% BA-MERG","MERG-BA-MERGPercent", resolutionCategories.get("BA-MERG"),(short) 1));
		l.add(newResolution("reads size","MERG-readSize", resolutionCategories.get("BA-MERG"),(short) 2));
		
		l.add(newResolution("N50","CTG-N50", resolutionCategories.get("CTG"),(short) 1));
		l.add(newResolution("cumul","CTG-cumul", resolutionCategories.get("CTG"),(short)2));
		l.add(newResolution("nb contigs","CTG-nbCtgs", resolutionCategories.get("CTG"),(short)3));
		l.add(newResolution("max size","CTG-maxSize", resolutionCategories.get("CTG"),(short)4));
		l.add(newResolution("assembled reads","CTG-assReads", resolutionCategories.get("CTG"),(short)5));
		
		l.add(newResolution("% lost bases","SIZE-lostBasesPerc", resolutionCategories.get("SIZE"),(short)1));
		
		l.add(newResolution("N50","SCAFF-N50", resolutionCategories.get("SCAFF"),(short) 1));
		l.add(newResolution("cumul","SCAFF-cumul", resolutionCategories.get("SCAFF"),(short) 2));
		l.add(newResolution("nb scaff","SCAFF-nbScaff", resolutionCategories.get("SCAFF"),(short) 3));
		l.add(newResolution("max size","SCAFF-maxSize", resolutionCategories.get("SCAFF"),(short) 4));
		l.add(newResolution("median insert size","SCAFF-medInsertSize", resolutionCategories.get("SCAFF"),(short) 5));
		l.add(newResolution("% satisfied pairs","SCAFF-satisfPairsPerc", resolutionCategories.get("SCAFF"),(short) 6));
		l.add(newResolution("% N","SCAFF-Npercent", resolutionCategories.get("SCAFF"),(short) 7));
		
		l.add(newResolution("gap sum","GAP-sum", resolutionCategories.get("GAP"),(short) 1));
		l.add(newResolution("gap count","GAP-count", resolutionCategories.get("GAP"),(short) 2));
		l.add(newResolution("corrected gap sum","GAP-correctedSum", resolutionCategories.get("GAP"),(short) 3));
		l.add(newResolution("corrected gap count","GAP-correctedCount", resolutionCategories.get("GAP"),(short) 4));
		l.add(newResolution("% N","GAP-Npercent", resolutionCategories.get("GAP"),(short) 5));
		
		ResolutionConfigurations r = new ResolutionConfigurations();
		r.code = "analysisReso";
		r.resolutions = l;
		r.objectTypeCode = "Analysis";
		ArrayList<String> al = new ArrayList<String>();
		al.add("BPA");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, "analysisReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}

	
	public static void createExperimentResolutionCNS(ContextValidation ctx) {
		
		List<Resolution> l = new ArrayList<Resolution>();
		
		l.add(newResolution("déroulement correct",	"correct", resolutionCategories.get("Default"), (short) 1));
		l.add(newResolution("échec expérience", "echec-experience", resolutionCategories.get("Default"), (short) 2));	
		
		ResolutionConfigurations r = new ResolutionConfigurations();
		r.code = "experimentReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		ArrayList<String> al = new ArrayList<String>();
		al.add("void-opgen-depot");
		al.add("opgen-depot");
		al.add("void-illumina-depot");
		al.add("prepa-flowcell");
		al.add("fragmentation");
		al.add("librairie-indexing");
	    al.add("librairie-dualindexing");
	    al.add("amplification");
	    al.add("solution-stock");
	    al.add("bioanalyzer-na");
	    al.add("bioanalyzer-a");
	    al.add("qubit");
	    al.add("qpcr");
	    al.add("ampure-na");
	    al.add("ampure-a");
	    al.add("void-banque");
	    al.add("void-qpcr");
	    al.add("illumina-depot");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, "experimentReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME, r,ctx, false);
	}
	

}