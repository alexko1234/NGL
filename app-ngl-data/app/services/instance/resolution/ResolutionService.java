package services.instance.resolution;

import static services.description.DescriptionFactory.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import play.Logger;

import fr.cea.ig.MongoDBDAO;

import validation.ContextValidation;
import models.laboratory.common.instance.ResolutionCategory;
import models.laboratory.common.instance.ResolutionConfigurations;
import models.laboratory.common.instance.Resolution;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;

public class ResolutionService {
	

	private static HashMap<String, Short> resolutionCategories = createResolutionCategories(); 


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
	
	
	
	
	public static HashMap<String, Short> createResolutionCategories(){	
		
		HashMap<String, Short> resoCategories = new HashMap<String, Short>();
		
		//Run
		resoCategories.put("Problème machine", (short) 20);
		resoCategories.put("Problème réactifs", (short) 30); 
		resoCategories.put("Problème qualité : SAV", (short) 40);
		resoCategories.put("Problème librairie", (short) 50);
		resoCategories.put("Problème informatique", (short) 60);
		resoCategories.put("Informations", (short) 70);
		resoCategories.put("Observations QC", (short) 80);
		//ReadSet
		resoCategories.put("Problème run", (short) 5);
		resoCategories.put("Problème librairie", (short) 10);
		resoCategories.put("Problème quantité", (short) 15);
		resoCategories.put("Problème indexing", (short) 20);
		resoCategories.put("Problème qualité", (short) 25);
		resoCategories.put("Problème taxon", (short) 30);
		resoCategories.put("Problème ribosomes", (short) 35);
		resoCategories.put("Problème mapping", (short) 40);
		resoCategories.put("Problème merging", (short) 45);	
		resoCategories.put("Problème échantillon", (short) 55);
		resoCategories.put("Problème déclaration LIMS", (short) 60);
		resoCategories.put("Informations", (short) 65);		
		//Analysis
		resoCategories.put("Merging", (short) 10);
		resoCategories.put("Contigage", (short) 20);
		resoCategories.put("Size Filter", (short) 30);
		resoCategories.put("Scaffolding", (short) 40);
		resoCategories.put("Gap Closing", (short) 50);
		//Experiment	
		resoCategories.put("Default", (short) 0);
		
		return resoCategories;
	}
	

	
	public static void saveResolutionsCNG(ContextValidation ctx) {
		
		List<Resolution> l =createRunResolutionCNG( new ArrayList<Resolution>()); 

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
		
		/***************************************/		
		l =createReadSetResolutionCNG( new ArrayList<Resolution>()); 		
			
		r = new ResolutionConfigurations();
		r.code = "readSetReso";
		r.resolutions = l;
		r.objectTypeCode = "ReadSet";
		al = new ArrayList<String>();
		al.add("default-readset");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, "readSetReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
	}
	
	
	
	
	public static void saveResolutionsCNS(ContextValidation ctx) {	
		
		List<Resolution> l =createRunResolutionCNS( new ArrayList<Resolution>()); 
		
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
		
		/**************************/		
		l =createReadSetResolutionCNS( new ArrayList<Resolution>()); 
		
		r = new ResolutionConfigurations();
		r.code = "readSetReso";
		r.resolutions = l;
		r.objectTypeCode = "ReadSet";
		 al = new ArrayList<String>();
		 al.add("default-readset");
		 al.add("RSARGUS");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, "readSetReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
		
		/**************************/
		l =createAnalysisResolutionCNS( new ArrayList<Resolution>()); 
		
		r = new ResolutionConfigurations();
		r.code = "analysisReso";
		r.resolutions = l;
		r.objectTypeCode = "Analysis";
		al = new ArrayList<String>();
		al.add("BPA");
		r.typeCodes = al;
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, "analysisReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);

		/**************************/
		l =createExperimentResolutionCNS( new ArrayList<Resolution>()); 
		
		r = new ResolutionConfigurations();
		r.code = "experimentReso";
		r.resolutions = l;
		r.objectTypeCode = "Experiment";
		al = new ArrayList<String>();
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
	
	
	
	/* sub-methods */
	
	
	
	public static List<Resolution> createRunResolutionCNG(List<Resolution> l) {
		
		l.add(newResolution("indéterminé","PbM-indetermine", getResolutionCategory("Problème machine"), (short) 1 ));		
		l.add(newResolution("chiller","PbM-chiller",getResolutionCategory("Problème machine"), (short) 2));
		l.add(newResolution("pelletier","PbM-pelletier",getResolutionCategory("Problème machine"), (short) 3));
		l.add(newResolution("fluidique","PbM-fluidiq",getResolutionCategory("Problème machine"), (short) 4));
		l.add(newResolution("laser","PbM-laser",getResolutionCategory("Problème machine"), (short) 5));
		l.add(newResolution("camera","PbM-camera",getResolutionCategory("Problème machine"), (short) 6));
		l.add(newResolution("focus","PbM-focus",getResolutionCategory("Problème machine"), (short) 7));
		l.add(newResolution("pb de vide","PbM-pbVide",getResolutionCategory("Problème machine"), (short) 8));
		l.add(newResolution("PE module","PbM-PEmodule",getResolutionCategory("Problème machine"), (short) 9));
		l.add(newResolution("zone de dépôt","PbM-zoneDepot",getResolutionCategory("Problème machine"), (short) 10));				
		l.add(newResolution("cBot","PbM-cBot",getResolutionCategory("Problème machine"), (short) 11));		
			
		l.add(newResolution("indéterminé","PbR-indetermine",getResolutionCategory("Problème réactifs"), (short) 1));
		l.add(newResolution("flowcell","PbR-FC",getResolutionCategory("Problème réactifs"), (short) 2));
		l.add(newResolution("cBot","PbR-cBot",getResolutionCategory("Problème réactifs"), (short) 3));
		l.add(newResolution("séquencage","PbR-sequencage",getResolutionCategory("Problème réactifs"), (short) 4));
		l.add(newResolution("indexing","PbR-indexing",getResolutionCategory("Problème réactifs"), (short) 5));
		l.add(newResolution("PE module","PbR-PEmodule",getResolutionCategory("Problème réactifs"), (short) 6));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1",getResolutionCategory("Problème réactifs"), (short) 7));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2",getResolutionCategory("Problème réactifs"), (short) 8));
		l.add(newResolution("erreur réactifs","PbR-erreurReac",getResolutionCategory("Problème réactifs"), (short) 9));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac",getResolutionCategory("Problème réactifs"), (short) 10));
		
		l.add(newResolution("intensité","SAV-intensite",getResolutionCategory("Problème qualité : SAV"), (short) 1));		
		l.add(newResolution("intensité faible A","SAV-intFbleA",getResolutionCategory("Problème qualité : SAV"), (short) 2));
		l.add(newResolution("intensité faible T","SAV-intFbleT",getResolutionCategory("Problème qualité : SAV"), (short) 3));
		l.add(newResolution("intensité faible C","SAV-intFbleC",getResolutionCategory("Problème qualité : SAV"), (short) 4));
		l.add(newResolution("intensité faible G","SAV-intFbleG",getResolutionCategory("Problème qualité : SAV"), (short) 5));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee",getResolutionCategory("Problème qualité : SAV"), (short) 6));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible",getResolutionCategory("Problème qualité : SAV"), (short) 7));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle",getResolutionCategory("Problème qualité : SAV"), (short) 8));
		l.add(newResolution("%PF","SAV-PF",getResolutionCategory("Problème qualité : SAV"), (short) 9));
		l.add(newResolution("phasing","SAV-phasing",getResolutionCategory("Problème qualité : SAV"), (short) 10));
		l.add(newResolution("prephasing","SAV-prephasing",getResolutionCategory("Problème qualité : SAV"), (short) 11));
		l.add(newResolution("error rate","SAV-errorRate",getResolutionCategory("Problème qualité : SAV"), (short) 12));
		l.add(newResolution("focus","SAV-focus",getResolutionCategory("Problème qualité : SAV"), (short) 13));
		l.add(newResolution("Q30","SAV-Q30",getResolutionCategory("Problème qualité : SAV"), (short) 14));
		l.add(newResolution("% bases déséquilibré","SAV-perctBasesDeseq",getResolutionCategory("Problème qualité : SAV"), (short) 15));
		l.add(newResolution("index non représenté","SAV-indexNonPresent",getResolutionCategory("Problème qualité : SAV"), (short) 16));
		l.add(newResolution("index sous-représenté","SAV-indexFblePerc",getResolutionCategory("Problème qualité : SAV"), (short) 17));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex",getResolutionCategory("Problème qualité : SAV"), (short) 18));
		
		l.add(newResolution("construction librairie","LIB-construction",getResolutionCategory("Problème librairie"), (short) 1));
		l.add(newResolution("cause profil : librairie","LIB-profilIntLib",getResolutionCategory("Problème librairie"), (short) 2));
		l.add(newResolution("cause profil : exp type","LIB-profilIntExpType",getResolutionCategory("Problème librairie"), (short) 3));
		l.add(newResolution("pb dilution","LIB-pbDilution",getResolutionCategory("Problème librairie"), (short) 4));
		l.add(newResolution("pb dilution spike-In","LIB-pbDilSpikeIn",getResolutionCategory("Problème librairie"), (short) 5));
		
		l.add(newResolution("indéterminé","PbI-indetermine",getResolutionCategory("Problème informatique"), (short) 1));
		l.add(newResolution("PC","PbI-PC",getResolutionCategory("Problème informatique"), (short) 2));
		l.add(newResolution("écran","PbI-ecran",getResolutionCategory("Problème informatique"), (short) 3));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf",getResolutionCategory("Problème informatique"), (short) 4));
		l.add(newResolution("logiciel","PbI-logiciel",getResolutionCategory("Problème informatique"), (short) 5));
		l.add(newResolution("reboot PC","PbI-rebootPC",getResolutionCategory("Problème informatique"), (short) 6));
		l.add(newResolution("retard robocopy","PbI-robocopy",getResolutionCategory("Problème informatique"), (short) 7));
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun",getResolutionCategory("Problème informatique"), (short) 8));
		
		l.add(newResolution("run de validation","Info-runValidation",getResolutionCategory("Informations"), (short) 1));
		l.add(newResolution("remboursement","Info-remboursement",getResolutionCategory("Informations"), (short) 2));

		l.add(newResolution("intensité B.M.S","QC-intBMS",getResolutionCategory("Observations QC"), (short) 1));
		l.add(newResolution("tiles out","QC-tilesOut",getResolutionCategory("Observations QC"), (short) 2));
		l.add(newResolution("saut de chimie","QC-sautChimie",getResolutionCategory("Observations QC"), (short) 3));
		
		return l;
	}
	
	
	public static List<Resolution> createReadSetResolutionCNG(List<Resolution> l) {

		l.add(newResolution("lane abandonnée","Run-abandonLane",getResolutionCategory("Problème run"), (short) 1));
		
		l.add(newResolution("nb seq brutes faible","Qte-seqRawInsuf",getResolutionCategory("Problème quantité"),(short) 1));
		l.add(newResolution("couverture en X hors spec.","Qte-couverture",getResolutionCategory("Problème quantité"),(short) 2));
	
		l.add(newResolution("index incorrect","IND-indexIncorrect",getResolutionCategory("Problème indexing"),(short) 1));
				
		l.add(newResolution("Q30 hors spec.","Qlte-Q30HorsSpec",getResolutionCategory("Problème qualité"),(short) 1));
		l.add(newResolution("répartition bases","Qlte-repartitionBases",getResolutionCategory("Problème qualité"), (short) 2));
		l.add(newResolution("% adaptateurs détectés","Qlte-adapterPercent",getResolutionCategory("Problème qualité"),(short) 3));
		l.add(newResolution("% duplicat élevé","Qlte-duplicatElevee",getResolutionCategory("Problème qualité"),(short) 4));
						
		l.add(newResolution("% mapping faible","MAP-PercMappingFble",getResolutionCategory("Problème mapping"),(short) 1));
		
		l.add(newResolution("test Dev","Info-testDev",getResolutionCategory("Informations"),(short) 1));
		l.add(newResolution("test Prod","Info-testProd",getResolutionCategory("Informations"),(short) 2));
		
		l.add(newResolution("sexe incorrect","Sample-sexeIncorrect",getResolutionCategory("Problème échantillon"),(short) 1));
		
		l.add(newResolution("erreur Experimental Type","LIMS-erreurExpType",getResolutionCategory("Problème déclaration LIMS"),(short) 1));

		return l;
	}
	
	
	public static List<Resolution> createRunResolutionCNS(List<Resolution> l) {

		l.add(newResolution("indéterminé","PbM-indetermine",getResolutionCategory("Problème machine"),  (short) 1));
		l.add(newResolution("chiller","PbM-chiller",getResolutionCategory("Problème machine"), (short) 2));
		l.add(newResolution("pelletier","PbM-pelletier",getResolutionCategory("Problème machine"), (short) 3));
		l.add(newResolution("fluidique","PbM-fluidiq",getResolutionCategory("Problème machine"), (short) 4));
		l.add(newResolution("laser","PbM-laser",getResolutionCategory("Problème machine"), (short) 5));
		l.add(newResolution("camera","PbM-camera",getResolutionCategory("Problème machine"), (short) 6));
		l.add(newResolution("focus","PbM-focus",getResolutionCategory("Problème machine"), (short) 7));
		l.add(newResolution("pb de vide","PbM-pbVide",getResolutionCategory("Problème machine"), (short) 8));
		l.add(newResolution("PE module","PbM-PEmodule",getResolutionCategory("Problème machine"), (short) 9));
		l.add(newResolution("cBot","PbM-cBot",getResolutionCategory("Problème machine"), (short) 10));		
			
		l.add(newResolution("indéterminé","PbR-indetermine",getResolutionCategory("Problème réactifs"), (short) 1));
		l.add(newResolution("flowcell","PbR-FC",getResolutionCategory("Problème réactifs"), (short) 2));
		l.add(newResolution("cBot","PbR-cBot",getResolutionCategory("Problème réactifs"), (short) 3));
		l.add(newResolution("séquencage","PbR-sequencage",getResolutionCategory("Problème réactifs"), (short) 4));
		l.add(newResolution("indexing","PbR-indexing",getResolutionCategory("Problème réactifs"), (short) 5));
		l.add(newResolution("PE module","PbR-PEmodule",getResolutionCategory("Problème réactifs"), (short) 6));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1",getResolutionCategory("Problème réactifs"), (short) 7));
		l.add(newResolution("rehyb indexing","PbR-rehybIndexing",getResolutionCategory("Problème réactifs"), (short) 8));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2",getResolutionCategory("Problème réactifs"), (short) 9));
		l.add(newResolution("erreur réactifs","PbR-erreurReac",getResolutionCategory("Problème réactifs"), (short) 10));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac",getResolutionCategory("Problème réactifs"), (short) 11));
		
		l.add(newResolution("intensité","SAV-intensite",getResolutionCategory("Problème qualité : SAV"), (short) 1));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee",getResolutionCategory("Problème qualité : SAV"), (short) 2));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible",getResolutionCategory("Problème qualité : SAV"), (short) 3));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle",getResolutionCategory("Problème qualité : SAV"), (short) 4));
		l.add(newResolution("%PF","SAV-PF",getResolutionCategory("Problème qualité : SAV"), (short) 5));
		l.add(newResolution("phasing","SAV-phasing",getResolutionCategory("Problème qualité : SAV"), (short) 6));
		l.add(newResolution("prephasing","SAV-prephasing",getResolutionCategory("Problème qualité : SAV"), (short) 7));
		l.add(newResolution("error rate","SAV-errorRate",getResolutionCategory("Problème qualité : SAV"), (short) 8));
		l.add(newResolution("Q30","SAV-Q30",getResolutionCategory("Problème qualité : SAV"), (short) 9));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex",getResolutionCategory("Problème qualité : SAV"), (short) 10));
		
		l.add(newResolution("indéterminé","PbI-indetermine",getResolutionCategory("Problème informatique"), (short) 1));
		l.add(newResolution("PC","PbI-PC",getResolutionCategory("Problème informatique"), (short) 2));
		l.add(newResolution("écran","PbI-ecran",getResolutionCategory("Problème informatique"), (short) 3));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf",getResolutionCategory("Problème informatique"), (short) 4));
		l.add(newResolution("logiciel","PbI-logiciel",getResolutionCategory("Problème informatique"), (short) 5));
		l.add(newResolution("reboot PC","PbI-rebootPC",getResolutionCategory("Problème informatique"), (short) 6));
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun",getResolutionCategory("Problème informatique"), (short) 7));
		
		l.add(newResolution("run de validation","Info-runValidation",getResolutionCategory("Informations"), (short) 1));
		l.add(newResolution("arret séquenceur","Info-arretSeq",getResolutionCategory("Informations"), (short) 2));
		l.add(newResolution("arret logiciel","Info_arretLogiciel",getResolutionCategory("Informations"), (short) 3));
		l.add(newResolution("remboursement","Info-remboursement",getResolutionCategory("Informations"), (short) 4));
		l.add(newResolution("flowcell redéposée","Info-FCredeposee",getResolutionCategory("Informations"), (short) 5));		
		
		return l;
	}
	
	
	public static List<Resolution> createReadSetResolutionCNS(List<Resolution> l) {

		l.add(newResolution("lane abandonnée","Run-abandonLane",getResolutionCategory("Problème run"), (short) 1));

		l.add(newResolution("Q30","Qlte-Q30",getResolutionCategory("Problème qualité"),(short) 1));				
		l.add(newResolution("répartition bases","Qlte-repartitionBases",getResolutionCategory("Problème qualité"), (short) 2));				
		l.add(newResolution("adaptateurs/Kmers","Qlte-adapterKmer",getResolutionCategory("Problème qualité"),(short) 3));		
		l.add(newResolution("duplicat > 30","Qlte-duplicat",getResolutionCategory("Problème qualité"),(short) 4));
		
		l.add(newResolution("pb protocole banque","LIB-pbProtocole",getResolutionCategory("Problème librairie"),(short) 1));
		l.add(newResolution("erreur dépôt banque","LIB-erreurDepot",getResolutionCategory("Problème librairie"),(short) 2));
		
		l.add(newResolution("seq valides insuf","Qte-seqValInsuf",getResolutionCategory("Problème quantité"),(short) 1));
		l.add(newResolution("seq utiles insuf","Qte-seqUtileInsuf",getResolutionCategory("Problème quantité"),(short) 2));

		l.add(newResolution("pb demultiplexage","IND-pbDemultiplex",getResolutionCategory("Problème indexing"),(short) 1));
		l.add(newResolution("pb manip","IND-pbManip",getResolutionCategory("Problème indexing"),(short) 2));
				
		l.add(newResolution("conta indéterminée","TAXO-contaIndeterm",getResolutionCategory("Problème taxon"),(short) 1));
		l.add(newResolution("conta manip","TAXO-contaManip",getResolutionCategory("Problème taxon"),(short) 2));
		l.add(newResolution("conta mat ori","TAXO-contaMatOri",getResolutionCategory("Problème taxon"),(short) 3));
		l.add(newResolution("non conforme","TAXO-nonConforme",getResolutionCategory("Problème taxon"),(short) 4));
		l.add(newResolution("mitochondrie","TAXO-mitochondrie",getResolutionCategory("Problème taxon"),(short) 5));
		l.add(newResolution("chloroplast","TAXO-chloroplast",getResolutionCategory("Problème taxon"),(short) 6));
		l.add(newResolution("virus","TAXO-virus",getResolutionCategory("Problème taxon"),(short) 7));
		l.add(newResolution("bactérie","TAXO-bacteria",getResolutionCategory("Problème taxon"),(short) 8)); 
		l.add(newResolution("fungi","TAXO-fungi",getResolutionCategory("Problème taxon"),(short) 9));
				
		l.add(newResolution("% rRNA élevé","RIBO-percEleve",getResolutionCategory("Problème ribosomes"),(short) 1));
		
		l.add(newResolution("% MP","MAP-PercentMP",getResolutionCategory("Problème mapping"),(short) 1));
		l.add(newResolution("taille moyenne MP","MAP-tailleMP",getResolutionCategory("Problème mapping"),(short) 2));
		
		l.add(newResolution("% lec mergées","MERG-PercLecMerg",getResolutionCategory("Problème merging"),(short) 1));
		l.add(newResolution("médiane lect mergées","MERG-MedLecMerg",getResolutionCategory("Problème merging"),(short) 2));
		
		
		return l;
	}
	
	
	public static List<Resolution> createAnalysisResolutionCNS(List<Resolution> l) {

		l.add(newResolution("% merging","MERG-mergingPercent",getResolutionCategory("Merging"),(short) 1));
		l.add(newResolution("reads size","MERG-readSize",getResolutionCategory("Merging"),(short) 2));
		
		l.add(newResolution("N50","CTG-N50",getResolutionCategory("Contigage"),(short) 1));
		l.add(newResolution("cumul","CTG-cumul",getResolutionCategory("Contigage"),(short)2));
		l.add(newResolution("nb contigs","CTG-nbCtgs",getResolutionCategory("Contigage"),(short)3));
		l.add(newResolution("max size","CTG-maxSize",getResolutionCategory("Contigage"),(short)4));
		l.add(newResolution("assembled reads","CTG-assReads",getResolutionCategory("Contigage"),(short)5));
		
		l.add(newResolution("% lost bases","SIZE-lostBasesPerc",getResolutionCategory("Size Filter"),(short)1));
		
		l.add(newResolution("N50","SCAFF-N50",getResolutionCategory("Scaffolding"),(short) 1));
		l.add(newResolution("cumul","SCAFF-cumul",getResolutionCategory("Scaffolding"),(short) 2));
		l.add(newResolution("nb scaff","SCAFF-nbScaff",getResolutionCategory("Scaffolding"),(short) 3));
		l.add(newResolution("max size","SCAFF-maxSize",getResolutionCategory("Scaffolding"),(short) 4));
		l.add(newResolution("median insert size","SCAFF-medInsertSize",getResolutionCategory("Scaffolding"),(short) 5));
		l.add(newResolution("% satisfied pairs","SCAFF-satisfPairsPerc",getResolutionCategory("Scaffolding"),(short) 6));
		l.add(newResolution("% N","SCAFF-Npercent",getResolutionCategory("Scaffolding"),(short) 7));
		
		l.add(newResolution("gap sum","GAP-sum",getResolutionCategory("Gap Closing"),(short) 1));
		l.add(newResolution("gap count","GAP-count",getResolutionCategory("Gap Closing"),(short) 2));
		l.add(newResolution("corrected gap sum","GAP-correctedSum",getResolutionCategory("Gap Closing"),(short) 3));
		l.add(newResolution("corrected gap count","GAP-correctedCount",getResolutionCategory("Gap Closing"),(short) 4));
		l.add(newResolution("% N","GAP-Npercent",getResolutionCategory("Gap Closing"),(short) 5));		
		
		return l;
	}

	
	public static List<Resolution> createExperimentResolutionCNS(List<Resolution> l) {
		l.add(newResolution("déroulement correct",	"correct", getResolutionCategory("Default"), (short) 1));
		l.add(newResolution("échec expérience", "echec-experience", getResolutionCategory("Default"), (short) 2));				
		return l;
	}
	
	
	public static ResolutionCategory getResolutionCategory(String name) {
		ResolutionCategory rc = new ResolutionCategory();
		rc.name = name;
		rc.displayOrder = resolutionCategories.get(name);
		return rc;
	}
}
