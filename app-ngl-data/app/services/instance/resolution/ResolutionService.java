package services.instance.resolution;

import static services.description.DescriptionFactory.*;

import java.util.ArrayList;
import java.util.List;

import fr.cea.ig.MongoDBDAO;

import validation.ContextValidation;
import models.laboratory.common.instance.Resolution;
import models.laboratory.common.instance.StateResolution;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;

public class ResolutionService {
	
	
	public static void main(ContextValidation ctx) {
		
		if (play.Play.application().configuration().getString("institute").contains("CNG")) {
			saveResolutionsCNG(ctx);
		}
		if (play.Play.application().configuration().getString("institute").contains("CNS")) {
			saveResolutionsCNS(ctx);
		}
	}
	
	
	
	public static void saveResolutionsCNG(ContextValidation ctx) {
		
		List<StateResolution> l = new ArrayList<StateResolution>();
		
		//Run
		l.add(newStateResolution("indéterminé","PbM-indetermine","PbM", (short) 1));
		
		l.add(newStateResolution("chiller","PbM-chiller","PbM", (short) 2));
		l.add(newStateResolution("pelletier","PbM-pelletier","PbM", (short) 3));
		l.add(newStateResolution("fluidique","PbM-fluidiq","PbM", (short) 4));
		l.add(newStateResolution("laser","PbM-laser","PbM", (short) 5));
		l.add(newStateResolution("camera","PbM-camera","PbM", (short) 6));
		l.add(newStateResolution("focus","PbM-focus","PbM", (short) 7));
		l.add(newStateResolution("pb de vide","PbM-pbVide","PbM", (short) 8));
		l.add(newStateResolution("PE module","PbM-PEmodule","PbM", (short) 9));
		l.add(newStateResolution("zone de dépôt","PbM-zoneDepot","PbM", (short) 10));				
		l.add(newStateResolution("cBot","PbM-cBot","PbM", (short) 11));		
			
		l.add(newStateResolution("indéterminé","PbR-indetermine","PbR", (short) 1));
		l.add(newStateResolution("flowcell","PbR-FC","PbR", (short) 2));
		l.add(newStateResolution("cBot","PbR-cBot","PbR", (short) 3));
		l.add(newStateResolution("séquencage","PbR-sequencage","PbR", (short) 4));
		l.add(newStateResolution("indexing","PbR-indexing","PbR", (short) 5));
		l.add(newStateResolution("PE module","PbR-PEmodule","PbR", (short) 6));
		l.add(newStateResolution("rehyb primer R1","PbR-rehybR1","PbR", (short) 7));
		l.add(newStateResolution("rehyb primer R2","PbR-rehybR2","PbR", (short) 9));
		l.add(newStateResolution("erreur réactifs","PbR-erreurReac","PbR", (short) 10));
		l.add(newStateResolution("rajout réactifs","PbR-ajoutReac","PbR", (short) 11));
		
		l.add(newStateResolution("intensité","SAV-intensite","SAV", (short) 1));
		
		l.add(newStateResolution("intensité faible A","SAV-intFbleA","SAV", (short) 2));
		l.add(newStateResolution("intensité faible T","SAV-intFbleT","SAV", (short) 3));
		l.add(newStateResolution("intensité faible C","SAV-intFbleC","SAV", (short) 4));
		l.add(newStateResolution("intensité faible G","SAV-intFbleG","SAV", (short) 5));
		
		l.add(newStateResolution("densité clusters trop élevée","SAV-densiteElevee","SAV", (short) 6));
		l.add(newStateResolution("densité clusters trop faible","SAV-densiteFaible","SAV", (short) 7));
		l.add(newStateResolution("densité clusters nulle","SAV-densiteNulle","SAV", (short) 8));
		l.add(newStateResolution("%PF","SAV-PF","SAV", (short) 9));
		l.add(newStateResolution("phasing","SAV-phasing","SAV", (short) 10));
		l.add(newStateResolution("prephasing","SAV-prephasing","SAV", (short) 11));
		l.add(newStateResolution("error rate","SAV-errorRate","SAV", (short) 12));
		l.add(newStateResolution("focus","SAV-focus","SAV", (short) 13));
		l.add(newStateResolution("Q30","SAV-Q30","SAV", (short) 14));
		
		l.add(newStateResolution("% bases déséquilibré","SAV-perctBasesDeseq","SAV", (short) 15));
		l.add(newStateResolution("index non représenté","SAV-indexNonPresent","SAV", (short) 16));
		l.add(newStateResolution("index sous-représenté","SAV-indexFblePerc","SAV", (short) 17));
		
		l.add(newStateResolution("indexing / demultiplexage","SAV-IndDemultiplex","SAV", (short) 18));
		
		l.add(newStateResolution("construction librairie","LIB-construction","RUN-LIB", (short) 1));
		l.add(newStateResolution("cause profil : librairie","LIB-profilIntLib","RUN-LIB", (short) 2));
		l.add(newStateResolution("cause profil : exp type","LIB-profilIntExpType","RUN-LIB", (short) 3));
		l.add(newStateResolution("pb dilution","LIB-pbDilution","RUN-LIB", (short) 4));
		l.add(newStateResolution("pb dilution spike-In","LIB-pbDilSpikeIn","RUN-LIB", (short) 5));
		
		l.add(newStateResolution("indéterminé","PbI-indetermine","PbI", (short) 1));
		l.add(newStateResolution("PC","PbI-PC","PbI", (short) 2));
		l.add(newStateResolution("écran","PbI-ecran","PbI", (short) 3));
		l.add(newStateResolution("espace disq insuf","PbI-espDisqInsuf","PbI", (short) 4));
		l.add(newStateResolution("logiciel","PbI-logiciel","PbI", (short) 5));
		l.add(newStateResolution("reboot PC","PbI-rebootPC","PbI", (short) 6));
		l.add(newStateResolution("retard robocopy","PbI-robocopy","PbI", (short) 7));
		l.add(newStateResolution("erreur paramétrage run","PbI-parametrageRun","PbI", (short) 8));
		
		l.add(newStateResolution("run de validation","Info-runValidation","RUN-Info", (short) 1));
		l.add(newStateResolution("remboursement","Info-remboursement","RUN-Info", (short) 4));

		l.add(newStateResolution("intensité B.M.S","QC-intBMS","QC", (short) 1));
		l.add(newStateResolution("tiles out","QC-tilesOut","QC", (short) 2));
		l.add(newStateResolution("saut de chimie","QC-sautChimie","QC", (short) 3));
		

		
		Resolution r = new Resolution();
		r.code = "runReso";
		r.stateResolutions = l;
		r.type = "Run";
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, Resolution.class, "runReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
		
		/***************************************/
		
		l = new ArrayList<StateResolution>();
		
		//ReadSet
		l.add(newStateResolution("lane abandonnée","Run-abandonLane","Run", (short) 1));
		l.add(newStateResolution("répartition bases","Qlte-repartitionBases","Qlte", (short) 3));				
		
		l.add(newStateResolution("nb seq brutes faible","Qte-seqRawInsuf","Qte",(short) 3));
		l.add(newStateResolution("couverture en X hors spec.","Qte-couverture","Qte",(short) 4));

		l.add(newStateResolution("index incorrect","IND-indexIncorrect","IND",(short) 3));
				
		l.add(newStateResolution("Q30 hors spec.","Qlte-Q30HorsSpec","Qlte",(short) 2));
		l.add(newStateResolution("% adaptateurs détectés","Qlte-adapterPercent","Qlte",(short) 5));
		
		l.add(newStateResolution("% duplicat élevé","Qlte-duplicatElevee","Qlte",(short) 7));
						
		l.add(newStateResolution("% mapping faible","MAP-PercMappingFble","MAP",(short) 1));
		
		l.add(newStateResolution("test Dev","Info-testDev","Info",(short) 1));
		l.add(newStateResolution("test Prod","Info-testProd","Info",(short) 2));
		
		l.add(newStateResolution("sexe incorrect","Sample-sexeIncorrect","Sample",(short) 1));
		
		l.add(newStateResolution("erreur Experimental Type","LIMS-erreurExpType","LIMS",(short) 1));
			
		r = new Resolution();
		r.code = "readSetReso";
		r.stateResolutions = l;
		r.type = "ReadSet";
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, Resolution.class, "readSetReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
		
	}
	
	
	
	
	public static void saveResolutionsCNS(ContextValidation ctx) {	
		
		List<StateResolution> l = new ArrayList<StateResolution>();
		
		//Run
		l.add(newStateResolution("indéterminé","PbM-indetermine","PbM",  (short) 1));
		l.add(newStateResolution("chiller","PbM-chiller","PbM", (short) 2));
		l.add(newStateResolution("pelletier","PbM-pelletier","PbM", (short) 3));
		l.add(newStateResolution("fluidique","PbM-fluidiq","PbM", (short) 4));
		l.add(newStateResolution("laser","PbM-laser","PbM", (short) 5));
		l.add(newStateResolution("camera","PbM-camera","PbM", (short) 6));
		l.add(newStateResolution("focus","PbM-focus","PbM", (short) 7));
		l.add(newStateResolution("pb de vide","PbM-pbVide","PbM", (short) 8));
		l.add(newStateResolution("PE module","PbM-PEmodule","PbM", (short) 9));
		l.add(newStateResolution("cBot","PbM-cBot","PbM", (short) 11));		
			
		l.add(newStateResolution("indéterminé","PbR-indetermine","PbR", (short) 1));
		l.add(newStateResolution("flowcell","PbR-FC","PbR", (short) 2));
		l.add(newStateResolution("cBot","PbR-cBot","PbR", (short) 3));
		l.add(newStateResolution("séquencage","PbR-sequencage","PbR", (short) 4));
		l.add(newStateResolution("indexing","PbR-indexing","PbR", (short) 5));
		l.add(newStateResolution("PE module","PbR-PEmodule","PbR", (short) 6));
		l.add(newStateResolution("rehyb primer R1","PbR-rehybR1","PbR", (short) 7));
		l.add(newStateResolution("rehyb indexing","PbR-rehybIndexing","PbR", (short) 8));
		l.add(newStateResolution("rehyb primer R2","PbR-rehybR2","PbR", (short) 9));
		l.add(newStateResolution("erreur réactifs","PbR-erreurReac","PbR", (short) 10));
		l.add(newStateResolution("rajout réactifs","PbR-ajoutReac","PbR", (short) 11));
		
		l.add(newStateResolution("intensité","SAV-intensite","SAV", (short) 1));
		l.add(newStateResolution("densité clusters trop élevée","SAV-densiteElevee","SAV", (short) 6));
		l.add(newStateResolution("densité clusters trop faible","SAV-densiteFaible","SAV", (short) 7));
		l.add(newStateResolution("densité clusters nulle","SAV-densiteNulle","SAV", (short) 8));
		l.add(newStateResolution("%PF","SAV-PF","SAV", (short) 9));
		l.add(newStateResolution("phasing","SAV-phasing","SAV", (short) 10));
		l.add(newStateResolution("prephasing","SAV-prephasing","SAV", (short) 11));
		l.add(newStateResolution("error rate","SAV-errorRate","SAV", (short) 12));
		l.add(newStateResolution("Q30","SAV-Q30","SAV", (short) 14));
		l.add(newStateResolution("indexing / demultiplexage","SAV-IndDemultiplex","SAV", (short) 18));
		
		l.add(newStateResolution("indéterminé","PbI-indetermine","PbI", (short) 1));
		l.add(newStateResolution("PC","PbI-PC","PbI", (short) 2));
		l.add(newStateResolution("écran","PbI-ecran","PbI", (short) 3));
		l.add(newStateResolution("espace disq insuf","PbI-espDisqInsuf","PbI", (short) 4));
		l.add(newStateResolution("logiciel","PbI-logiciel","PbI", (short) 5));
		l.add(newStateResolution("reboot PC","PbI-rebootPC","PbI", (short) 6));
		l.add(newStateResolution("erreur paramétrage run","PbI-parametrageRun","PbI", (short) 8));
		
		l.add(newStateResolution("run de validation","Info-runValidation","RUN-Info", (short) 1));
		l.add(newStateResolution("arret séquenceur","Info-arretSeq","RUN-Info", (short) 2));
		l.add(newStateResolution("arret logiciel","Info_arretLogiciel","RUN-Info", (short) 3));
		l.add(newStateResolution("remboursement","Info-remboursement","RUN-Info", (short) 4));
		l.add(newStateResolution("flowcell redéposée","Info-FCredeposee","RUN-Info", (short) 5));


		
		Resolution r = new Resolution();
		r.code = "runReso";
		r.stateResolutions = l;
		r.type = "Run";
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, Resolution.class, "runReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
		
		/**************************/
		
		l = new ArrayList<StateResolution>();
		
		//ReadSet
		l.add(newStateResolution("lane abandonnée","Run-abandonLane","Run", (short) 1));
		l.add(newStateResolution("répartition bases","Qlte-repartitionBases","Qlte", (short) 3));				
		
		l.add(newStateResolution("pb protocole banque","LIB-pbProtocole","LIB",(short) 1));
		l.add(newStateResolution("erreur dépôt banque","LIB-erreurDepot","LIB",(short) 2));
		
		l.add(newStateResolution("seq valides insuf","Qte-seqValInsuf","Qte",(short) 1));
		l.add(newStateResolution("seq utiles insuf","Qte-seqUtileInsuf","Qte",(short) 2));

		l.add(newStateResolution("pb demultiplexage","IND-pbDemultiplex","IND",(short) 1));
		l.add(newStateResolution("pb manip","IND-pbManip","IND",(short) 2));
				
		l.add(newStateResolution("Q30","Qlte-Q30","Qlte",(short) 1));				
		l.add(newStateResolution("adaptateurs/Kmers","Qlte-adapterKmer","Qlte",(short) 4));
		
		l.add(newStateResolution("duplicat > 30","Qlte-duplicat","Qlte",(short) 6));
				
		l.add(newStateResolution("conta indéterminée","TAXO-contaIndeterm","TAXO",(short) 1));
		l.add(newStateResolution("conta manip","TAXO-contaManip","TAXO",(short) 2));
		l.add(newStateResolution("conta mat ori","TAXO-contaMatOri","TAXO",(short) 3));
		l.add(newStateResolution("non conforme","TAXO-nonConforme","TAXO",(short) 4));
		l.add(newStateResolution("mitochondrie","TAXO-mitochondrie","TAXO",(short) 5));
		l.add(newStateResolution("chloroplast","TAXO-chloroplast","TAXO",(short) 6));
		l.add(newStateResolution("virus","TAXO-virus","TAXO",(short) 7));
		l.add(newStateResolution("bactérie","TAXO-bacteria","TAXO",(short) 8)); 
		l.add(newStateResolution("fungi","TAXO-fungi","TAXO",(short) 9));
				
		l.add(newStateResolution("% rRNA élevé","RIBO-percEleve","RIBO",(short) 1));
		
		l.add(newStateResolution("% MP","MAP-PercentMP","MAP",(short) 2));
		l.add(newStateResolution("taille moyenne MP","MAP-tailleMP","MAP",(short) 3));
		
		l.add(newStateResolution("% lec mergées","MERG-PercLecMerg","MERG",(short) 1));
		l.add(newStateResolution("médiane lect mergées","MERG-MedLecMerg","MERG",(short) 2));

		r = new Resolution();
		r.code = "readSetReso";
		r.stateResolutions = l;
		r.type = "ReadSet";
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, Resolution.class, "readSetReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);
		
		/**************************/
		
		l = new ArrayList<StateResolution>();
		
		//Analysis
		l.add(newStateResolution("% merging","MERG-mergingPercent","BA-MERG",(short) 1));
		l.add(newStateResolution("reads size","MERG-readSize","BA-MERG",(short) 2));
		l.add(newStateResolution("N50","CTG-N50","CTG",(short) 1));
		l.add(newStateResolution("cumul","CTG-cumul","CTG",(short)2));
		l.add(newStateResolution("nb contigs","CTG-nbCtgs","CTG",(short)3));
		l.add(newStateResolution("max size","CTG-maxSize","CTG",(short)4));
		l.add(newStateResolution("assembled reads","CTG-assReads","CTG",(short)5));
		l.add(newStateResolution("% lost bases","SIZE-lostBasesPerc","SIZE",(short)1));
		l.add(newStateResolution("N50","SCAFF-N50","SCAFF",(short) 1));
		l.add(newStateResolution("cumul","SCAFF-cumul","SCAFF",(short) 2));
		l.add(newStateResolution("nb scaff","SCAFF-nbScaff","SCAFF",(short) 3));
		l.add(newStateResolution("max size","SCAFF-maxSize","SCAFF",(short) 4));
		l.add(newStateResolution("median insert size","SCAFF-medInsertSize","SCAFF",(short) 5));
		l.add(newStateResolution("% satisfied pairs","SCAFF-satisfPairsPerc","SCAFF",(short) 6));
		l.add(newStateResolution("% N","SCAFF-Npercent","SCAFF",(short) 7));
		l.add(newStateResolution("gap sum","GAP-sum","GAP",(short) 1));
		l.add(newStateResolution("gap count","GAP-count","GAP",(short) 2));
		l.add(newStateResolution("corrected gap sum","GAP-correctedSum","GAP",(short) 3));
		l.add(newStateResolution("corrected gap count","GAP-correctedCount","GAP",(short) 4));
		l.add(newStateResolution("% N","GAP-Npercent","GAP",(short) 5));
		
		r = new Resolution();
		r.code = "analysisReso";
		r.stateResolutions = l;
		r.type = "Analysis";
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, Resolution.class, "analysisReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);

		/**************************/
		
		l = new ArrayList<StateResolution>();
		
		//Experiment
		l.add(newStateResolution("déroulement correct",	"correct", "default", (short) 1));
		l.add(newStateResolution("échec expérience", "echec-experience", "default", (short) 1));
		
		r = new Resolution();
		r.code = "experimentReso";
		r.stateResolutions = l;
		r.type = "Experiment";
		
		MongoDBDAO.deleteByCode(InstanceConstants.RESOLUTION_COLL_NAME, Resolution.class, "experimentReso");
		InstanceHelpers.save(InstanceConstants.RESOLUTION_COLL_NAME,r,ctx, false);

		
	}
}
