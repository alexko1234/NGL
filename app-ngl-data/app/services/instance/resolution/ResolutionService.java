package services.instance.resolution;

import static services.description.DescriptionFactory.*;

import java.util.ArrayList;
import java.util.List;

import play.Logger;

import fr.cea.ig.MongoDBDAO;

import validation.ContextValidation;
import models.laboratory.common.instance.ResolutionConfigurations;
import models.laboratory.common.instance.Resolution;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;

public class ResolutionService {
	
	
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

	
	public static void saveResolutionsCNG(ContextValidation ctx) {
		
		List<Resolution> l = new ArrayList<Resolution>();
		
		//Run
		l.add(newResolution("indéterminé","PbM-indetermine","Problème machine", (short) 1, (short) 20));		
		l.add(newResolution("chiller","PbM-chiller","Problème machine", (short) 2, (short) 20));
		l.add(newResolution("pelletier","PbM-pelletier","Problème machine", (short) 3, (short) 20));
		l.add(newResolution("fluidique","PbM-fluidiq","Problème machine", (short) 4, (short) 20));
		l.add(newResolution("laser","PbM-laser","Problème machine", (short) 5, (short) 20));
		l.add(newResolution("camera","PbM-camera","Problème machine", (short) 6, (short) 20));
		l.add(newResolution("focus","PbM-focus","Problème machine", (short) 7, (short) 20));
		l.add(newResolution("pb de vide","PbM-pbVide","Problème machine", (short) 8, (short) 20));
		l.add(newResolution("PE module","PbM-PEmodule","Problème machine", (short) 9, (short) 20));
		l.add(newResolution("zone de dépôt","PbM-zoneDepot","Problème machine", (short) 10, (short) 20));				
		l.add(newResolution("cBot","PbM-cBot","Problème machine", (short) 11, (short) 20));		
			
		l.add(newResolution("indéterminé","PbR-indetermine","Problème réactifs", (short) 1, (short) 30));
		l.add(newResolution("flowcell","PbR-FC","Problème réactifs", (short) 2, (short) 30));
		l.add(newResolution("cBot","PbR-cBot","Problème réactifs", (short) 3, (short) 30));
		l.add(newResolution("séquencage","PbR-sequencage","Problème réactifs", (short) 4, (short) 30));
		l.add(newResolution("indexing","PbR-indexing","Problème réactifs", (short) 5, (short) 30));
		l.add(newResolution("PE module","PbR-PEmodule","Problème réactifs", (short) 6, (short) 30));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1","Problème réactifs", (short) 7, (short) 30));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2","Problème réactifs", (short) 8, (short) 30));
		l.add(newResolution("erreur réactifs","PbR-erreurReac","Problème réactifs", (short) 9, (short) 30));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac","Problème réactifs", (short) 10, (short) 30));
		
		l.add(newResolution("intensité","SAV-intensite","Problème qualité : SAV", (short) 1, (short) 40));		
		l.add(newResolution("intensité faible A","SAV-intFbleA","Problème qualité : SAV", (short) 2, (short) 40));
		l.add(newResolution("intensité faible T","SAV-intFbleT","Problème qualité : SAV", (short) 3, (short) 40));
		l.add(newResolution("intensité faible C","SAV-intFbleC","Problème qualité : SAV", (short) 4, (short) 40));
		l.add(newResolution("intensité faible G","SAV-intFbleG","Problème qualité : SAV", (short) 5, (short) 40));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee","Problème qualité : SAV", (short) 6, (short) 40));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible","Problème qualité : SAV", (short) 7, (short) 40));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle","Problème qualité : SAV", (short) 8, (short) 40));
		l.add(newResolution("%PF","SAV-PF","Problème qualité : SAV", (short) 9, (short) 40));
		l.add(newResolution("phasing","SAV-phasing","Problème qualité : SAV", (short) 10, (short) 40));
		l.add(newResolution("prephasing","SAV-prephasing","Problème qualité : SAV", (short) 11, (short) 40));
		l.add(newResolution("error rate","SAV-errorRate","Problème qualité : SAV", (short) 12, (short) 40));
		l.add(newResolution("focus","SAV-focus","Problème qualité : SAV", (short) 13, (short) 40));
		l.add(newResolution("Q30","SAV-Q30","Problème qualité : SAV", (short) 14, (short) 40));
		l.add(newResolution("% bases déséquilibré","SAV-perctBasesDeseq","Problème qualité : SAV", (short) 15, (short) 40));
		l.add(newResolution("index non représenté","SAV-indexNonPresent","Problème qualité : SAV", (short) 16, (short) 40));
		l.add(newResolution("index sous-représenté","SAV-indexFblePerc","Problème qualité : SAV", (short) 17, (short) 40));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex","Problème qualité : SAV", (short) 18, (short) 40));
		
		l.add(newResolution("construction librairie","LIB-construction","Problème librairie", (short) 1, (short) 50));
		l.add(newResolution("cause profil : librairie","LIB-profilIntLib","Problème librairie", (short) 2, (short) 50));
		l.add(newResolution("cause profil : exp type","LIB-profilIntExpType","Problème librairie", (short) 3, (short) 50));
		l.add(newResolution("pb dilution","LIB-pbDilution","Problème librairie", (short) 4, (short) 50));
		l.add(newResolution("pb dilution spike-In","LIB-pbDilSpikeIn","Problème librairie", (short) 5, (short) 50));
		
		l.add(newResolution("indéterminé","PbI-indetermine","Problème Informatique", (short) 1, (short) 60));
		l.add(newResolution("PC","PbI-PC","Problème Informatique", (short) 2, (short) 60));
		l.add(newResolution("écran","PbI-ecran","Problème Informatique", (short) 3, (short) 60));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf","Problème Informatique", (short) 4, (short) 60));
		l.add(newResolution("logiciel","PbI-logiciel","Problème Informatique", (short) 5, (short) 60));
		l.add(newResolution("reboot PC","PbI-rebootPC","Problème Informatique", (short) 6, (short) 60));
		l.add(newResolution("retard robocopy","PbI-robocopy","Problème Informatique", (short) 7, (short) 60));
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun","Problème Informatique", (short) 8, (short) 60));
		
		l.add(newResolution("run de validation","Info-runValidation","Informations", (short) 1, (short) 70));
		l.add(newResolution("remboursement","Info-remboursement","Informations", (short) 2, (short) 70));

		l.add(newResolution("intensité B.M.S","QC-intBMS","Observations QC", (short) 1, (short) 80));
		l.add(newResolution("tiles out","QC-tilesOut","Observations QC", (short) 2, (short) 80));
		l.add(newResolution("saut de chimie","QC-sautChimie","Observations QC", (short) 3, (short) 80));
		

		
		ResolutionConfigurations r = new ResolutionConfigurations();
		r.code = "runReso";
		r.resolutions = l;
		r.objectTypeCode = "Problème run";
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
		l.add(newResolution("lane abandonnée","Run-abandonLane","Problème run", (short) 1, (short) 5));
		
		l.add(newResolution("nb seq brutes faible","Qte-seqRawInsuf","Problème quantité",(short) 1, (short) 15));
		l.add(newResolution("couverture en X hors spec.","Qte-couverture","Problème quantité",(short) 2, (short) 15));

		l.add(newResolution("index incorrect","IND-indexIncorrect","Problème indexing",(short) 1, (short) 20));
				
		l.add(newResolution("Q30 hors spec.","Qlte-Q30HorsSpec","Problème qualité",(short) 1, (short) 25));
		l.add(newResolution("répartition bases","Qlte-repartitionBases","Problème qualité", (short) 2, (short) 25));
		l.add(newResolution("% adaptateurs détectés","Qlte-adapterPercent","Problème qualité",(short) 3, (short) 25));
		l.add(newResolution("% duplicat élevé","Qlte-duplicatElevee","Problème qualité",(short) 4, (short) 25));
						
		l.add(newResolution("% mapping faible","MAP-PercMappingFble","Problème mapping",(short) 1, (short) 40));
		
		l.add(newResolution("test Dev","Info-testDev","Informations",(short) 1, (short) 65));
		l.add(newResolution("test Prod","Info-testProd","Informations",(short) 2, (short) 65));
		
		l.add(newResolution("sexe incorrect","Sample-sexeIncorrect","Problème échantillon",(short) 1, (short) 55));
		
		l.add(newResolution("erreur Experimental Type","LIMS-erreurExpType","Problème déclaration LIMS",(short) 1, (short) 60));
			
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
		l.add(newResolution("indéterminé","PbM-indetermine","Problème machine",  (short) 1, (short) 20));
		l.add(newResolution("chiller","PbM-chiller","Problème machine", (short) 2, (short) 20));
		l.add(newResolution("pelletier","PbM-pelletier","Problème machine", (short) 3, (short) 20));
		l.add(newResolution("fluidique","PbM-fluidiq","Problème machine", (short) 4, (short) 20));
		l.add(newResolution("laser","PbM-laser","Problème machine", (short) 5, (short) 20));
		l.add(newResolution("camera","PbM-camera","Problème machine", (short) 6, (short) 20));
		l.add(newResolution("focus","PbM-focus","Problème machine", (short) 7, (short) 20));
		l.add(newResolution("pb de vide","PbM-pbVide","Problème machine", (short) 8, (short) 20));
		l.add(newResolution("PE module","PbM-PEmodule","Problème machine", (short) 9, (short) 20));
		l.add(newResolution("cBot","PbM-cBot","Problème machine", (short) 10, (short) 20));		
			
		l.add(newResolution("indéterminé","PbR-indetermine","Problème réactifs", (short) 1, (short) 30));
		l.add(newResolution("flowcell","PbR-FC","Problème réactifs", (short) 2, (short) 30));
		l.add(newResolution("cBot","PbR-cBot","Problème réactifs", (short) 3, (short) 30));
		l.add(newResolution("séquencage","PbR-sequencage","Problème réactifs", (short) 4, (short) 30));
		l.add(newResolution("indexing","PbR-indexing","Problème réactifs", (short) 5, (short) 30));
		l.add(newResolution("PE module","PbR-PEmodule","Problème réactifs", (short) 6, (short) 30));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1","Problème réactifs", (short) 7, (short) 30));
		l.add(newResolution("rehyb indexing","PbR-rehybIndexing","Problème réactifs", (short) 8, (short) 30));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2","Problème réactifs", (short) 9, (short) 30));
		l.add(newResolution("erreur réactifs","PbR-erreurReac","Problème réactifs", (short) 10, (short) 30));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac","Problème réactifs", (short) 11, (short) 30));
		
		l.add(newResolution("intensité","SAV-intensite","Problème qualité : SAV", (short) 1, (short) 40));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee","Problème qualité : SAV", (short) 2, (short) 40));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible","Problème qualité : SAV", (short) 3, (short) 40));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle","Problème qualité : SAV", (short) 4, (short) 40));
		l.add(newResolution("%PF","SAV-PF","Problème qualité : SAV", (short) 5, (short) 40));
		l.add(newResolution("phasing","SAV-phasing","Problème qualité : SAV", (short) 6, (short) 40));
		l.add(newResolution("prephasing","SAV-prephasing","Problème qualité : SAV", (short) 7, (short) 40));
		l.add(newResolution("error rate","SAV-errorRate","Problème qualité : SAV", (short) 8, (short) 40));
		l.add(newResolution("Q30","SAV-Q30","Problème qualité : SAV", (short) 9, (short) 40));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex","Problème qualité : SAV", (short) 10, (short) 40));
		
		l.add(newResolution("indéterminé","PbI-indetermine","Problème Informatique", (short) 1, (short) 60));
		l.add(newResolution("PC","PbI-PC","Problème Informatique", (short) 2, (short) 60));
		l.add(newResolution("écran","PbI-ecran","Problème Informatique", (short) 3, (short) 60));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf","Problème Informatique", (short) 4, (short) 60));
		l.add(newResolution("logiciel","PbI-logiciel","Problème Informatique", (short) 5, (short) 60));
		l.add(newResolution("reboot PC","PbI-rebootPC","Problème Informatique", (short) 6, (short) 60));
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun","Problème Informatique", (short) 7, (short) 60));
		
		l.add(newResolution("run de validation","Info-runValidation","Informations", (short) 1, (short) 70));
		l.add(newResolution("arret séquenceur","Info-arretSeq","Informations", (short) 2, (short) 70));
		l.add(newResolution("arret logiciel","Info_arretLogiciel","Informations", (short) 3, (short) 70));
		l.add(newResolution("remboursement","Info-remboursement","Informations", (short) 4, (short) 70));
		l.add(newResolution("flowcell redéposée","Info-FCredeposee","Informations", (short) 5, (short) 70));


		ResolutionConfigurations r = new ResolutionConfigurations();
		r.code = "runReso";
		r.resolutions = l;
		r.objectTypeCode = "Problème run";
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
		l.add(newResolution("lane abandonnée","Run-abandonLane","Problème run", (short) 1, (short) 5));

		l.add(newResolution("Q30","Qlte-Q30","Problème qualité",(short) 1, (short) 15));				
		l.add(newResolution("répartition bases","Qlte-repartitionBases","Problème qualité", (short) 2, (short) 15));				
		l.add(newResolution("adaptateurs/Kmers","Qlte-adapterKmer","Problème qualité",(short) 3, (short) 15));		
		l.add(newResolution("duplicat > 30","Qlte-duplicat","Problème qualité",(short) 4, (short) 15));
		
		l.add(newResolution("pb protocole banque","LIB-pbProtocole","Problème librairie",(short) 1, (short) 10));
		l.add(newResolution("erreur dépôt banque","LIB-erreurDepot","Problème librairie",(short) 2, (short) 10));
		
		l.add(newResolution("seq valides insuf","Qte-seqValInsuf","Problème quantité",(short) 1, (short) 15));
		l.add(newResolution("seq utiles insuf","Qte-seqUtileInsuf","Problème quantité",(short) 2, (short) 15));

		l.add(newResolution("pb demultiplexage","IND-pbDemultiplex","Problème indexing",(short) 1, (short) 20));
		l.add(newResolution("pb manip","IND-pbManip","Problème indexing",(short) 2, (short) 20));
				
		l.add(newResolution("conta indéterminée","TAXO-contaIndeterm","Problème taxon",(short) 1, (short) 30));
		l.add(newResolution("conta manip","TAXO-contaManip","Problème taxon",(short) 2, (short) 30));
		l.add(newResolution("conta mat ori","TAXO-contaMatOri","Problème taxon",(short) 3, (short) 30));
		l.add(newResolution("non conforme","TAXO-nonConforme","Problème taxon",(short) 4, (short) 30));
		l.add(newResolution("mitochondrie","TAXO-mitochondrie","Problème taxon",(short) 5, (short) 30));
		l.add(newResolution("chloroplast","TAXO-chloroplast","Problème taxon",(short) 6, (short) 30));
		l.add(newResolution("virus","TAXO-virus","Problème taxon",(short) 7, (short) 30));
		l.add(newResolution("bactérie","TAXO-bacteria","Problème taxon",(short) 8, (short) 30)); 
		l.add(newResolution("fungi","TAXO-fungi","Problème taxon",(short) 9, (short) 30));
				
		l.add(newResolution("% rRNA élevé","RIBO-percEleve","Problème ribosomes",(short) 1, (short) 35));
		
		l.add(newResolution("% MP","MAP-PercentMP","Problème mapping",(short) 1, (short) 40));
		l.add(newResolution("taille moyenne MP","MAP-tailleMP","Problème mapping",(short) 2, (short) 40));
		
		l.add(newResolution("% lec mergées","MERG-PercLecMerg","Problème merging",(short) 1, (short) 45));
		l.add(newResolution("médiane lect mergées","MERG-MedLecMerg","Problème merging",(short) 2, (short) 45));

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
		l.add(newResolution("% merging","MERG-mergingPercent","Merging",(short) 1, (short) 10));
		l.add(newResolution("reads size","MERG-readSize","Merging",(short) 2, (short) 10));
		
		l.add(newResolution("N50","CTG-N50","Contigage",(short) 1, (short) 20));
		l.add(newResolution("cumul","CTG-cumul","Contigage",(short)2, (short) 20));
		l.add(newResolution("nb contigs","CTG-nbCtgs","Contigage",(short)3, (short) 20));
		l.add(newResolution("max size","CTG-maxSize","Contigage",(short)4, (short) 20));
		l.add(newResolution("assembled reads","CTG-assReads","Contigage",(short)5, (short) 20));
		
		l.add(newResolution("% lost bases","SIZE-lostBasesPerc","Size Filter",(short)1, (short) 30));
		
		l.add(newResolution("N50","SCAFF-N50","Scaffolding",(short) 1, (short) 40));
		l.add(newResolution("cumul","SCAFF-cumul","Scaffolding",(short) 2, (short) 40));
		l.add(newResolution("nb scaff","SCAFF-nbScaff","Scaffolding",(short) 3, (short) 40));
		l.add(newResolution("max size","SCAFF-maxSize","Scaffolding",(short) 4, (short) 40));
		l.add(newResolution("median insert size","SCAFF-medInsertSize","Scaffolding",(short) 5, (short) 40));
		l.add(newResolution("% satisfied pairs","SCAFF-satisfPairsPerc","Scaffolding",(short) 6, (short) 40));
		l.add(newResolution("% N","SCAFF-Npercent","Scaffolding",(short) 7, (short) 40));
		
		l.add(newResolution("gap sum","GAP-sum","Gap Closing",(short) 1, (short) 50));
		l.add(newResolution("gap count","GAP-count","Gap Closing",(short) 2, (short) 50));
		l.add(newResolution("corrected gap sum","GAP-correctedSum","Gap Closing",(short) 3, (short) 50));
		l.add(newResolution("corrected gap count","GAP-correctedCount","Gap Closing",(short) 4, (short) 50));
		l.add(newResolution("% N","GAP-Npercent","Gap Closing",(short) 5, (short) 50));
		
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
		l.add(newResolution("déroulement correct",	"correct", "default", (short) 1, (short) 1));
		l.add(newResolution("échec expérience", "echec-experience", "default", (short) 2, (short) 1));
		
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
