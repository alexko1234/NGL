package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.ResolutionCategory;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;

@Deprecated
public class ResolutionService {
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		
		DAOHelpers.removeAll(Resolution.class, Resolution.find);
		DAOHelpers.removeAll(ResolutionCategory.class, ResolutionCategory.find);
		
		saveResolutionCategories(errors);	
		saveResolutions(errors);	
	}
		
		
	public static void saveResolutionCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ResolutionCategory> l = new ArrayList<ResolutionCategory>();
		
		//Run
		l.add(DescriptionFactory.newResolutionCategory("Problème machine", "PbM", (short) 20));
		l.add(DescriptionFactory.newResolutionCategory("Problème réactifs", "PbR", (short) 30)); 
		l.add(DescriptionFactory.newResolutionCategory("Problème qualité : SAV", "SAV", (short) 40));
		l.add(DescriptionFactory.newResolutionCategory("Problème librairie", "RUN-LIB", (short) 50));
		l.add(DescriptionFactory.newResolutionCategory("Problème informatique", "PbI", (short) 60));
		l.add(DescriptionFactory.newResolutionCategory("Informations", "RUN-Info", (short) 70));
		l.add(DescriptionFactory.newResolutionCategory("Observations QC", "QC", (short) 80));

		//ReadSet
		l.add(DescriptionFactory.newResolutionCategory("Problème run", "Run", (short) 5));
		l.add(DescriptionFactory.newResolutionCategory("Problème librairie", "LIB", (short) 10));
		l.add(DescriptionFactory.newResolutionCategory("Problème quantité", "Qte", (short) 15));
		l.add(DescriptionFactory.newResolutionCategory("Problème indexing", "IND", (short) 20));
		l.add(DescriptionFactory.newResolutionCategory("Problème qualité", "Qlte", (short) 25));
		l.add(DescriptionFactory.newResolutionCategory("Problème taxon", "TAXO", (short) 30));
		l.add(DescriptionFactory.newResolutionCategory("Problème ribosomes", "RIBO", (short) 35));
		l.add(DescriptionFactory.newResolutionCategory("Problème mapping", "MAP", (short) 40));
		l.add(DescriptionFactory.newResolutionCategory("Problème merging", "MERG", (short) 45));	
		l.add(DescriptionFactory.newResolutionCategory("Problème échantillon", "Sample", (short) 55));
		l.add(DescriptionFactory.newResolutionCategory("Problème déclaration LIMS", "LIMS", (short) 60));
		l.add(DescriptionFactory.newResolutionCategory("Informations", "Info", (short) 65));
		
		
		//Analysis
		l.add(DescriptionFactory.newResolutionCategory("Merging", "BA-MERG", (short) 10));
		l.add(DescriptionFactory.newResolutionCategory("Contigage", "CTG", (short) 20));
		l.add(DescriptionFactory.newResolutionCategory("Size Filter", "SIZE", (short) 30));
		l.add(DescriptionFactory.newResolutionCategory("Scaffolding", "SCAFF", (short) 40));
		l.add(DescriptionFactory.newResolutionCategory("Gap Closing", "GAP", (short) 50));
		
		// for experiment	
		l.add(DescriptionFactory.newResolutionCategory("Default", "Default", (short) 0));
		
		DAOHelpers.saveModels(ResolutionCategory.class, l, errors);
	}
	
	public static void saveResolutions(Map<String,List<ValidationError>> errors) throws DAOException{
		List<Resolution> l = new ArrayList<Resolution>();
		
		//Run
		l.add(DescriptionFactory.newResolution("indéterminé","PbM-indetermine",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("chiller","PbM-chiller",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("pelletier","PbM-pelletier",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 3));
		l.add(DescriptionFactory.newResolution("fluidique","PbM-fluidiq",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 4));
		l.add(DescriptionFactory.newResolution("laser","PbM-laser",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 5));
		l.add(DescriptionFactory.newResolution("camera","PbM-camera",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 6));
		l.add(DescriptionFactory.newResolution("focus","PbM-focus",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 7));
		l.add(DescriptionFactory.newResolution("pb de vide","PbM-pbVide",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 8));
		l.add(DescriptionFactory.newResolution("PE module","PbM-PEmodule",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 9));
		l.add(DescriptionFactory.newResolution("zone de dépôt","PbM-zoneDepot",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 10));				
		l.add(DescriptionFactory.newResolution("cBot","PbM-cBot",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 11));		
			
		l.add(DescriptionFactory.newResolution("indéterminé","PbR-indetermine",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("flowcell","PbR-FC",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("cBot","PbR-cBot",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 3));
		l.add(DescriptionFactory.newResolution("séquencage","PbR-sequencage",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 4));
		l.add(DescriptionFactory.newResolution("indexing","PbR-indexing",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 5));
		l.add(DescriptionFactory.newResolution("PE module","PbR-PEmodule",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 6));
		l.add(DescriptionFactory.newResolution("rehyb primer R1","PbR-rehybR1",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 7));
		l.add(DescriptionFactory.newResolution("rehyb indexing","PbR-rehybIndexing",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 8));
		l.add(DescriptionFactory.newResolution("rehyb primer R2","PbR-rehybR2",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 9));
		l.add(DescriptionFactory.newResolution("erreur réactifs","PbR-erreurReac",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 10));
		l.add(DescriptionFactory.newResolution("rajout réactifs","PbR-ajoutReac",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 11));
		
		l.add(DescriptionFactory.newResolution("intensité","SAV-intensite",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 1));
		
		l.add(DescriptionFactory.newResolution("intensité faible A","SAV-intFbleA",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("intensité faible T","SAV-intFbleT",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 3));
		l.add(DescriptionFactory.newResolution("intensité faible C","SAV-intFbleC",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 4));
		l.add(DescriptionFactory.newResolution("intensité faible G","SAV-intFbleG",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 5));
		
		l.add(DescriptionFactory.newResolution("densité clusters trop élevée","SAV-densiteElevee",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 6));
		l.add(DescriptionFactory.newResolution("densité clusters trop faible","SAV-densiteFaible",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 7));
		l.add(DescriptionFactory.newResolution("densité clusters nulle","SAV-densiteNulle",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 8));
		l.add(DescriptionFactory.newResolution("%PF","SAV-PF",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 9));
		l.add(DescriptionFactory.newResolution("phasing","SAV-phasing",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 10));
		l.add(DescriptionFactory.newResolution("prephasing","SAV-prephasing",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 11));
		l.add(DescriptionFactory.newResolution("error rate","SAV-errorRate",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 12));
		l.add(DescriptionFactory.newResolution("focus","SAV-focus",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 13));
		l.add(DescriptionFactory.newResolution("Q30","SAV-Q30",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 14));
		
		l.add(DescriptionFactory.newResolution("% bases déséquilibré","SAV-perctBasesDeseq",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 15));
		l.add(DescriptionFactory.newResolution("index non représenté","SAV-indexNonPresent",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 16));
		l.add(DescriptionFactory.newResolution("index sous-représenté","SAV-indexFblePerc",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 17));
		
		l.add(DescriptionFactory.newResolution("indexing / demultiplexage","SAV-IndDemultiplex",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 18));
			
		l.add(DescriptionFactory.newResolution("construction librairie","LIB-construction",ResolutionCategory.find.findByCode("RUN-LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("cause profil : librairie","LIB-profilIntLib",ResolutionCategory.find.findByCode("RUN-LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("cause profil : exp type","LIB-profilIntExpType",ResolutionCategory.find.findByCode("RUN-LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 3));
		l.add(DescriptionFactory.newResolution("pb dilution","LIB-pbDilution",ResolutionCategory.find.findByCode("RUN-LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 4));
		l.add(DescriptionFactory.newResolution("pb dilution spike-In","LIB-pbDilSpikeIn",ResolutionCategory.find.findByCode("RUN-LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 5));
		
		l.add(DescriptionFactory.newResolution("indéterminé","PbI-indetermine",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("PC","PbI-PC",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("écran","PbI-ecran",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 3));
		l.add(DescriptionFactory.newResolution("espace disq insuf","PbI-espDisqInsuf",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 4));
		l.add(DescriptionFactory.newResolution("logiciel","PbI-logiciel",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 5));
		l.add(DescriptionFactory.newResolution("reboot PC","PbI-rebootPC",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 6));
		l.add(DescriptionFactory.newResolution("retard robocopy","PbI-robocopy",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 7));
		l.add(DescriptionFactory.newResolution("erreur paramétrage run","PbI-parametrageRun",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 8));
		
		l.add(DescriptionFactory.newResolution("run de validation","Info-runValidation",ResolutionCategory.find.findByCode("RUN-Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("arret séquenceur","Info-arretSeq",ResolutionCategory.find.findByCode("RUN-Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("arret logiciel","Info_arretLogiciel",ResolutionCategory.find.findByCode("RUN-Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 3));
		l.add(DescriptionFactory.newResolution("remboursement","Info-remboursement",ResolutionCategory.find.findByCode("RUN-Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 4));
		l.add(DescriptionFactory.newResolution("flowcell redéposée","Info-FCredeposee",ResolutionCategory.find.findByCode("RUN-Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 5));

		l.add(DescriptionFactory.newResolution("intensité B.M.S","QC-intBMS",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("tiles out","QC-tilesOut",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("saut de chimie","QC-sautChimie",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name()), (short) 3));

		//ReadSet
		
		//CNS + CNG
		l.add(DescriptionFactory.newResolution("lane abandonnée","Run-abandonLane",ResolutionCategory.find.findByCode("Run"), DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("répartition bases","Qlte-repartitionBases",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 3));				
		
		//CNS or CNG only
		l.add(DescriptionFactory.newResolution("pb protocole banque","LIB-pbProtocole",ResolutionCategory.find.findByCode("LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("erreur dépôt banque","LIB-erreurDepot",ResolutionCategory.find.findByCode("LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 2));
		
		l.add(DescriptionFactory.newResolution("seq valides insuf","Qte-seqValInsuf",ResolutionCategory.find.findByCode("Qte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("seq utiles insuf","Qte-seqUtileInsuf",ResolutionCategory.find.findByCode("Qte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("nb seq brutes faible","Qte-seqRawInsuf",ResolutionCategory.find.findByCode("Qte"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 3));
		l.add(DescriptionFactory.newResolution("couverture en X hors spec.","Qte-couverture",ResolutionCategory.find.findByCode("Qte"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 4));

		l.add(DescriptionFactory.newResolution("pb demultiplexage","IND-pbDemultiplex",ResolutionCategory.find.findByCode("IND"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("pb manip","IND-pbManip",ResolutionCategory.find.findByCode("IND"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("index incorrect","IND-indexIncorrect",ResolutionCategory.find.findByCode("IND"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 3));
				
		l.add(DescriptionFactory.newResolution("Q30","Qlte-Q30",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));				
		l.add(DescriptionFactory.newResolution("Q30 hors spec.","Qlte-Q30HorsSpec",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("adaptateurs/Kmers","Qlte-adapterKmer",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 4));
		l.add(DescriptionFactory.newResolution("% adaptateurs détectés","Qlte-adapterPercent",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 5));
		
		l.add(DescriptionFactory.newResolution("duplicat > 30","Qlte-duplicat",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 6));
		l.add(DescriptionFactory.newResolution("% duplicat élevé","Qlte-duplicatElevee",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 7));
				
		l.add(DescriptionFactory.newResolution("conta indéterminée","TAXO-contaIndeterm",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("conta manip","TAXO-contaManip",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("conta mat ori","TAXO-contaMatOri",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 3));
		l.add(DescriptionFactory.newResolution("non conforme","TAXO-nonConforme",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 4));
		l.add(DescriptionFactory.newResolution("mitochondrie","TAXO-mitochondrie",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 5));
		l.add(DescriptionFactory.newResolution("chloroplast","TAXO-chloroplast",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 6));
		l.add(DescriptionFactory.newResolution("virus","TAXO-virus",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 7));
		l.add(DescriptionFactory.newResolution("bactérie","TAXO-bacteria",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 8)); 
		l.add(DescriptionFactory.newResolution("fungi","TAXO-fungi",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 9));
				
		l.add(DescriptionFactory.newResolution("% rRNA élevé","RIBO-percEleve",ResolutionCategory.find.findByCode("RIBO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
		
		l.add(DescriptionFactory.newResolution("% mapping faible","MAP-PercMappingFble",ResolutionCategory.find.findByCode("MAP"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("% MP","MAP-PercentMP",ResolutionCategory.find.findByCode("MAP"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("taille moyenne MP","MAP-tailleMP",ResolutionCategory.find.findByCode("MAP"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 3));
		
		l.add(DescriptionFactory.newResolution("test Dev","Info-testDev",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("test Prod","Info-testProd",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 2));
		
		l.add(DescriptionFactory.newResolution("sexe incorrect","Sample-sexeIncorrect",ResolutionCategory.find.findByCode("Sample"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
		
		l.add(DescriptionFactory.newResolution("erreur Experimental Type","LIMS-erreurExpType",ResolutionCategory.find.findByCode("LIMS"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
			
		l.add(DescriptionFactory.newResolution("% lec mergées","MERG-PercLecMerg",ResolutionCategory.find.findByCode("MERG"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("médiane lect mergées","MERG-MedLecMerg",ResolutionCategory.find.findByCode("MERG"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()), (short) 2));

		//Analysis
		l.add(DescriptionFactory.newResolution("% merging","MERG-mergingPercent",ResolutionCategory.find.findByCode("BA-MERG"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("reads size","MERG-readSize",ResolutionCategory.find.findByCode("BA-MERG"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("N50","CTG-N50",ResolutionCategory.find.findByCode("CTG"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("cumul","CTG-cumul",ResolutionCategory.find.findByCode("CTG"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short)2));
		l.add(DescriptionFactory.newResolution("nb contigs","CTG-nbCtgs",ResolutionCategory.find.findByCode("CTG"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short)3));
		l.add(DescriptionFactory.newResolution("max size","CTG-maxSize",ResolutionCategory.find.findByCode("CTG"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short)4));
		l.add(DescriptionFactory.newResolution("assembled reads","CTG-assReads",ResolutionCategory.find.findByCode("CTG"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short)5));
		l.add(DescriptionFactory.newResolution("% lost bases","SIZE-lostBasesPerc",ResolutionCategory.find.findByCode("SIZE"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short)1));
		l.add(DescriptionFactory.newResolution("N50","SCAFF-N50",ResolutionCategory.find.findByCode("SCAFF"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("cumul","SCAFF-cumul",ResolutionCategory.find.findByCode("SCAFF"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("nb scaff","SCAFF-nbScaff",ResolutionCategory.find.findByCode("SCAFF"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 3));
		l.add(DescriptionFactory.newResolution("max size","SCAFF-maxSize",ResolutionCategory.find.findByCode("SCAFF"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 4));
		l.add(DescriptionFactory.newResolution("median insert size","SCAFF-medInsertSize",ResolutionCategory.find.findByCode("SCAFF"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 5));
		l.add(DescriptionFactory.newResolution("% satisfied pairs","SCAFF-satisfPairsPerc",ResolutionCategory.find.findByCode("SCAFF"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 6));
		l.add(DescriptionFactory.newResolution("% N","SCAFF-Npercent",ResolutionCategory.find.findByCode("SCAFF"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 7));
		l.add(DescriptionFactory.newResolution("gap sum","GAP-sum",ResolutionCategory.find.findByCode("GAP"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("gap count","GAP-count",ResolutionCategory.find.findByCode("GAP"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 2));
		l.add(DescriptionFactory.newResolution("corrected gap sum","GAP-correctedSum",ResolutionCategory.find.findByCode("GAP"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 3));
		l.add(DescriptionFactory.newResolution("corrected gap count","GAP-correctedCount",ResolutionCategory.find.findByCode("GAP"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 4));
		l.add(DescriptionFactory.newResolution("% N","GAP-Npercent",ResolutionCategory.find.findByCode("GAP"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Analysis.name()), (short) 5));

		
		
		//Experiment
		//CNS
		l.add(DescriptionFactory.newResolution("déroulement correct",	"correct",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(DescriptionFactory.newResolution("échec expérience",	"echec-experience",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));

	/*	l.add(newResolution("% rRNA élevé","riboEleve",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("échec expérience",	"echec-experience",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("erreur programme",	"erreur-prgme",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("erreur protocole",	"erreur-proto",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("erreur matériel",	"erreur-sample",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("erreur manip",	"erreur-manip",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("erreur kit",	"erreur-kit",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("erreur réactif",	"erreur-reactif",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("erreur input",	"erreur-input",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("erreur TAG utilisé",	"erreur-tag",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("inversion d'index",	"inversion-index",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("contamination manip",	"conta-manip",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("réactifs : lot à  problème",	"pb-lotReactif",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("pb manip",	"pb-manip",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("pb qté adaptateurs utilisé",	"pb-qteAdaptateurs",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("pb de purification",	"pb-purif",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("perte de matériel après purif",	"perte-materiel",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("sizing - pb de gel",	"sizing-pbGel",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("sizing - pb de migration",	"sizing-pbMigration",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("sizing - pb cuve",	"sizing-pbCuve",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("accident de manipulation",	"accident-manip",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("tube tombé en sortie d'exp",	"chute-supportContainer",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("pb robot",	"pb-robot",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("casse du tube au covaris",	"casse-supportContainer",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("suramplification",	"suramplification",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("rendement bq trop fble",	"concLibFble",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("rendement bq ampli trop fble",	"concLibAmpliFble",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("qté en sortie d'exp insuf",	"qteFinaleInsuff",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("tube bq transformé en sol stock",	"transforme-solStock",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("résultat dosage trop faible",	"dosage-tropFble",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("Qbit : non détecté",	"dosage-nonDetecte",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("profil bioanalyzer non valide",	"profil-nonValide",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("profil  bioanalyzer atypique",	"profil-atypique",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("profil  bioanalyzer plat",	"profil-plat",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("profil bioanalyzer : pic adaptateurs",	"presenceAdaptateurs",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("qPCR : conc trop faible",	"concQpcrFble",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("qPCR : pb ecart-type Ct",	"pbEcartTypeQpcr",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
		l.add(newResolution("créé par erreur",	"erreur-creation",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name()), (short) 1));
*/
		DAOHelpers.saveModels(Resolution.class, l, errors);
	}

}
