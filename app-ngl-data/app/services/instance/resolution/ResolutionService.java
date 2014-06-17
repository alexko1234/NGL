package services.instance.resolution;

import static services.description.DescriptionFactory.*;

import java.util.ArrayList;
import java.util.List;

import fr.cea.ig.MongoDBDAO;

import validation.ContextValidation;
import models.laboratory.common.instance.ResolutionConfigurations;
import models.laboratory.common.instance.Resolution;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;

public class ResolutionService {
	
	
	public static void main(ContextValidation ctx) {
		
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
	}
	
	
	
	public static void saveResolutionsCNG(ContextValidation ctx) {
		
		List<Resolution> l = new ArrayList<Resolution>();
		
		//Run
		l.add(newResolution("indéterminé","PbM-indetermine","PbM", (short) 1));		
		l.add(newResolution("chiller","PbM-chiller","PbM", (short) 2));
		l.add(newResolution("pelletier","PbM-pelletier","PbM", (short) 3));
		l.add(newResolution("fluidique","PbM-fluidiq","PbM", (short) 4));
		l.add(newResolution("laser","PbM-laser","PbM", (short) 5));
		l.add(newResolution("camera","PbM-camera","PbM", (short) 6));
		l.add(newResolution("focus","PbM-focus","PbM", (short) 7));
		l.add(newResolution("pb de vide","PbM-pbVide","PbM", (short) 8));
		l.add(newResolution("PE module","PbM-PEmodule","PbM", (short) 9));
		l.add(newResolution("zone de dépôt","PbM-zoneDepot","PbM", (short) 10));				
		l.add(newResolution("cBot","PbM-cBot","PbM", (short) 11));		
			
		l.add(newResolution("indéterminé","PbR-indetermine","PbR", (short) 1));
		l.add(newResolution("flowcell","PbR-FC","PbR", (short) 2));
		l.add(newResolution("cBot","PbR-cBot","PbR", (short) 3));
		l.add(newResolution("séquencage","PbR-sequencage","PbR", (short) 4));
		l.add(newResolution("indexing","PbR-indexing","PbR", (short) 5));
		l.add(newResolution("PE module","PbR-PEmodule","PbR", (short) 6));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1","PbR", (short) 7));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2","PbR", (short) 8));
		l.add(newResolution("erreur réactifs","PbR-erreurReac","PbR", (short) 9));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac","PbR", (short) 10));
		
		l.add(newResolution("intensité","SAV-intensite","SAV", (short) 1));		
		l.add(newResolution("intensité faible A","SAV-intFbleA","SAV", (short) 2));
		l.add(newResolution("intensité faible T","SAV-intFbleT","SAV", (short) 3));
		l.add(newResolution("intensité faible C","SAV-intFbleC","SAV", (short) 4));
		l.add(newResolution("intensité faible G","SAV-intFbleG","SAV", (short) 5));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee","SAV", (short) 6));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible","SAV", (short) 7));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle","SAV", (short) 8));
		l.add(newResolution("%PF","SAV-PF","SAV", (short) 9));
		l.add(newResolution("phasing","SAV-phasing","SAV", (short) 10));
		l.add(newResolution("prephasing","SAV-prephasing","SAV", (short) 11));
		l.add(newResolution("error rate","SAV-errorRate","SAV", (short) 12));
		l.add(newResolution("focus","SAV-focus","SAV", (short) 13));
		l.add(newResolution("Q30","SAV-Q30","SAV", (short) 14));
		l.add(newResolution("% bases déséquilibré","SAV-perctBasesDeseq","SAV", (short) 15));
		l.add(newResolution("index non représenté","SAV-indexNonPresent","SAV", (short) 16));
		l.add(newResolution("index sous-représenté","SAV-indexFblePerc","SAV", (short) 17));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex","SAV", (short) 18));
		
		l.add(newResolution("construction librairie","LIB-construction","RUN-LIB", (short) 1));
		l.add(newResolution("cause profil : librairie","LIB-profilIntLib","RUN-LIB", (short) 2));
		l.add(newResolution("cause profil : exp type","LIB-profilIntExpType","RUN-LIB", (short) 3));
		l.add(newResolution("pb dilution","LIB-pbDilution","RUN-LIB", (short) 4));
		l.add(newResolution("pb dilution spike-In","LIB-pbDilSpikeIn","RUN-LIB", (short) 5));
		
		l.add(newResolution("indéterminé","PbI-indetermine","PbI", (short) 1));
		l.add(newResolution("PC","PbI-PC","PbI", (short) 2));
		l.add(newResolution("écran","PbI-ecran","PbI", (short) 3));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf","PbI", (short) 4));
		l.add(newResolution("logiciel","PbI-logiciel","PbI", (short) 5));
		l.add(newResolution("reboot PC","PbI-rebootPC","PbI", (short) 6));
		l.add(newResolution("retard robocopy","PbI-robocopy","PbI", (short) 7));
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun","PbI", (short) 8));
		
		l.add(newResolution("run de validation","Info-runValidation","RUN-Info", (short) 1));
		l.add(newResolution("remboursement","Info-remboursement","RUN-Info", (short) 2));

		l.add(newResolution("intensité B.M.S","QC-intBMS","QC", (short) 1));
		l.add(newResolution("tiles out","QC-tilesOut","QC", (short) 2));
		l.add(newResolution("saut de chimie","QC-sautChimie","QC", (short) 3));
		

		
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
		
		l = new ArrayList<Resolution>();
		
		//ReadSet
		l.add(newResolution("lane abandonnée","Run-abandonLane","Run", (short) 1));
		
		l.add(newResolution("nb seq brutes faible","Qte-seqRawInsuf","Qte",(short) 1));
		l.add(newResolution("couverture en X hors spec.","Qte-couverture","Qte",(short) 2));

		l.add(newResolution("index incorrect","IND-indexIncorrect","IND",(short) 1));
				
		l.add(newResolution("Q30 hors spec.","Qlte-Q30HorsSpec","Qlte",(short) 1));
		l.add(newResolution("répartition bases","Qlte-repartitionBases","Qlte", (short) 2));
		l.add(newResolution("% adaptateurs détectés","Qlte-adapterPercent","Qlte",(short) 3));
		l.add(newResolution("% duplicat élevé","Qlte-duplicatElevee","Qlte",(short) 4));
						
		l.add(newResolution("% mapping faible","MAP-PercMappingFble","MAP",(short) 1));
		
		l.add(newResolution("test Dev","Info-testDev","Info",(short) 1));
		l.add(newResolution("test Prod","Info-testProd","Info",(short) 2));
		
		l.add(newResolution("sexe incorrect","Sample-sexeIncorrect","Sample",(short) 1));
		
		l.add(newResolution("erreur Experimental Type","LIMS-erreurExpType","LIMS",(short) 1));
			
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
		
		List<Resolution> l = new ArrayList<Resolution>();
		
		//Run
		l.add(newResolution("indéterminé","PbM-indetermine","PbM",  (short) 1));
		l.add(newResolution("chiller","PbM-chiller","PbM", (short) 2));
		l.add(newResolution("pelletier","PbM-pelletier","PbM", (short) 3));
		l.add(newResolution("fluidique","PbM-fluidiq","PbM", (short) 4));
		l.add(newResolution("laser","PbM-laser","PbM", (short) 5));
		l.add(newResolution("camera","PbM-camera","PbM", (short) 6));
		l.add(newResolution("focus","PbM-focus","PbM", (short) 7));
		l.add(newResolution("pb de vide","PbM-pbVide","PbM", (short) 8));
		l.add(newResolution("PE module","PbM-PEmodule","PbM", (short) 9));
		l.add(newResolution("cBot","PbM-cBot","PbM", (short) 10));		
			
		l.add(newResolution("indéterminé","PbR-indetermine","PbR", (short) 1));
		l.add(newResolution("flowcell","PbR-FC","PbR", (short) 2));
		l.add(newResolution("cBot","PbR-cBot","PbR", (short) 3));
		l.add(newResolution("séquencage","PbR-sequencage","PbR", (short) 4));
		l.add(newResolution("indexing","PbR-indexing","PbR", (short) 5));
		l.add(newResolution("PE module","PbR-PEmodule","PbR", (short) 6));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1","PbR", (short) 7));
		l.add(newResolution("rehyb indexing","PbR-rehybIndexing","PbR", (short) 8));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2","PbR", (short) 9));
		l.add(newResolution("erreur réactifs","PbR-erreurReac","PbR", (short) 10));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac","PbR", (short) 11));
		
		l.add(newResolution("intensité","SAV-intensite","SAV", (short) 1));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee","SAV", (short) 2));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible","SAV", (short) 3));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle","SAV", (short) 4));
		l.add(newResolution("%PF","SAV-PF","SAV", (short) 5));
		l.add(newResolution("phasing","SAV-phasing","SAV", (short) 6));
		l.add(newResolution("prephasing","SAV-prephasing","SAV", (short) 7));
		l.add(newResolution("error rate","SAV-errorRate","SAV", (short) 8));
		l.add(newResolution("Q30","SAV-Q30","SAV", (short) 9));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex","SAV", (short) 10));
		
		l.add(newResolution("indéterminé","PbI-indetermine","PbI", (short) 1));
		l.add(newResolution("PC","PbI-PC","PbI", (short) 2));
		l.add(newResolution("écran","PbI-ecran","PbI", (short) 3));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf","PbI", (short) 4));
		l.add(newResolution("logiciel","PbI-logiciel","PbI", (short) 5));
		l.add(newResolution("reboot PC","PbI-rebootPC","PbI", (short) 6));
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun","PbI", (short) 7));
		
		l.add(newResolution("run de validation","Info-runValidation","RUN-Info", (short) 1));
		l.add(newResolution("arret séquenceur","Info-arretSeq","RUN-Info", (short) 2));
		l.add(newResolution("arret logiciel","Info_arretLogiciel","RUN-Info", (short) 3));
		l.add(newResolution("remboursement","Info-remboursement","RUN-Info", (short) 4));
		l.add(newResolution("flowcell redéposée","Info-FCredeposee","RUN-Info", (short) 5));


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
		
		l = new ArrayList<Resolution>();
		
		//ReadSet
		l.add(newResolution("lane abandonnée","Run-abandonLane","Run", (short) 1));

		l.add(newResolution("Q30","Qlte-Q30","Qlte",(short) 1));				
		l.add(newResolution("répartition bases","Qlte-repartitionBases","Qlte", (short) 2));				
		l.add(newResolution("adaptateurs/Kmers","Qlte-adapterKmer","Qlte",(short) 3));		
		l.add(newResolution("duplicat > 30","Qlte-duplicat","Qlte",(short) 4));
		
		l.add(newResolution("pb protocole banque","LIB-pbProtocole","LIB",(short) 1));
		l.add(newResolution("erreur dépôt banque","LIB-erreurDepot","LIB",(short) 2));
		
		l.add(newResolution("seq valides insuf","Qte-seqValInsuf","Qte",(short) 1));
		l.add(newResolution("seq utiles insuf","Qte-seqUtileInsuf","Qte",(short) 2));

		l.add(newResolution("pb demultiplexage","IND-pbDemultiplex","IND",(short) 1));
		l.add(newResolution("pb manip","IND-pbManip","IND",(short) 2));
				
		l.add(newResolution("conta indéterminée","TAXO-contaIndeterm","TAXO",(short) 1));
		l.add(newResolution("conta manip","TAXO-contaManip","TAXO",(short) 2));
		l.add(newResolution("conta mat ori","TAXO-contaMatOri","TAXO",(short) 3));
		l.add(newResolution("non conforme","TAXO-nonConforme","TAXO",(short) 4));
		l.add(newResolution("mitochondrie","TAXO-mitochondrie","TAXO",(short) 5));
		l.add(newResolution("chloroplast","TAXO-chloroplast","TAXO",(short) 6));
		l.add(newResolution("virus","TAXO-virus","TAXO",(short) 7));
		l.add(newResolution("bactérie","TAXO-bacteria","TAXO",(short) 8)); 
		l.add(newResolution("fungi","TAXO-fungi","TAXO",(short) 9));
				
		l.add(newResolution("% rRNA élevé","RIBO-percEleve","RIBO",(short) 1));
		
		l.add(newResolution("% MP","MAP-PercentMP","MAP",(short) 1));
		l.add(newResolution("taille moyenne MP","MAP-tailleMP","MAP",(short) 2));
		
		l.add(newResolution("% lec mergées","MERG-PercLecMerg","MERG",(short) 1));
		l.add(newResolution("médiane lect mergées","MERG-MedLecMerg","MERG",(short) 2));

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
		
		l = new ArrayList<Resolution>();
		
		//Analysis (CNS)
		l.add(newResolution("% merging","MERG-mergingPercent","BA-MERG",(short) 1));
		l.add(newResolution("reads size","MERG-readSize","BA-MERG",(short) 2));
		
		l.add(newResolution("N50","CTG-N50","CTG",(short) 1));
		l.add(newResolution("cumul","CTG-cumul","CTG",(short)2));
		l.add(newResolution("nb contigs","CTG-nbCtgs","CTG",(short)3));
		l.add(newResolution("max size","CTG-maxSize","CTG",(short)4));
		l.add(newResolution("assembled reads","CTG-assReads","CTG",(short)5));
		
		l.add(newResolution("% lost bases","SIZE-lostBasesPerc","SIZE",(short)1));
		
		l.add(newResolution("N50","SCAFF-N50","SCAFF",(short) 1));
		l.add(newResolution("cumul","SCAFF-cumul","SCAFF",(short) 2));
		l.add(newResolution("nb scaff","SCAFF-nbScaff","SCAFF",(short) 3));
		l.add(newResolution("max size","SCAFF-maxSize","SCAFF",(short) 4));
		l.add(newResolution("median insert size","SCAFF-medInsertSize","SCAFF",(short) 5));
		l.add(newResolution("% satisfied pairs","SCAFF-satisfPairsPerc","SCAFF",(short) 6));
		l.add(newResolution("% N","SCAFF-Npercent","SCAFF",(short) 7));
		
		l.add(newResolution("gap sum","GAP-sum","GAP",(short) 1));
		l.add(newResolution("gap count","GAP-count","GAP",(short) 2));
		l.add(newResolution("corrected gap sum","GAP-correctedSum","GAP",(short) 3));
		l.add(newResolution("corrected gap count","GAP-correctedCount","GAP",(short) 4));
		l.add(newResolution("% N","GAP-Npercent","GAP",(short) 5));
		
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
		
		l = new ArrayList<Resolution>();
		
		//Experiment (CNS)
		l.add(newResolution("déroulement correct",	"correct", "default", (short) 1));
		l.add(newResolution("échec expérience", "echec-experience", "default", (short) 2));
		
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
}
