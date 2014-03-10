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
import static services.description.DescriptionFactory.*;

public class ResolutionService {
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		
		DAOHelpers.removeAll(Resolution.class, Resolution.find);
		DAOHelpers.removeAll(ResolutionCategory.class, ResolutionCategory.find);
		
		saveResolutionCategories(errors);	
		saveResolutions(errors);	
	}
		
	public static void saveResolutionCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ResolutionCategory> l = new ArrayList<ResolutionCategory>();
				
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème machine", "PbM"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème réactifs", "PbR")); 
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème qualité : SAV", "SAV"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème informatique", "PbI"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Informations", "Info"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème quantité", "Qte"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème indexing", "IND"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème qualité", "Qlte"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème taxon", "TAXO"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème ribosomes", "RIBO"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème mapping", "MAP"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème merging", "MERG"));		
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème run", "Run"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème échantillon", "Sample"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème librairie", "LIB"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Observations QC", "QC"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème déclaration LIMS", "LIMS"));
			

		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Default", "Default"));
		
		DAOHelpers.saveModels(ResolutionCategory.class, l, errors);
	}
	
	public static void saveResolutions(Map<String,List<ValidationError>> errors) throws DAOException{
		List<Resolution> l = new ArrayList<Resolution>();
		
		//Run
		l.add(newResolution("indéterminé","PbM-indetermine",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("chiller","PbM-chiller",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("pelletier","PbM-pelletier",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("fluidique","PbM-fluidiq",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("laser","PbM-laser",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("camera","PbM-camera",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("lentille","PbM-lentille",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("pb de vide","PbM-pbVide",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("PE module","PbM-PEmodule",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("zone de dépôt","PbM-zoneDepot",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));		
		l.add(newResolution("cBot","PbM-cBot",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));		
		
		l.add(newResolution("indéterminé","PbR-indetermine",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("flowcell","PbR-FC",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("cBot","PbR-cBot",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("séquencage","PbR-sequencage",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("indexing","PbR-indexing",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("PE module","PbR-PEmodule",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("rehyb primer R1","PbR-rehybR1",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("rehyb primer R2","PbR-rehybR2",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("erreur réactifs","PbR-erreurReac",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("rajout réactifs","PbR-ajoutReac",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		
		l.add(newResolution("intensité","Sav-intensite",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("densité clusters trop élevée","SAV-densiteElevee",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("densité clusters trop faible","SAV-densiteFaible",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("densité clusters nulle","SAV-densiteNulle",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("%PF","SAV-PF",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("phasing","SAV-phasing",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("prephasing","SAV-prephasing",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("error rate","SAV-errorRate",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("focus","SAV-focus",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("Q30","SAV-Q30",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("indexing / demultiplexage","SAV-IndDemultiplex",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
			
		l.add(newResolution("construction librairie","LIB-construction",ResolutionCategory.find.findByCode("LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("cause profil : librairie","LIB-profilIntLib",ResolutionCategory.find.findByCode("LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("cause profil : exp type","LIB-profilIntExpType",ResolutionCategory.find.findByCode("LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("pb dilution","LIB-pbDilution",ResolutionCategory.find.findByCode("LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("pb dilution spike-In","LIB-pbDilSpikeIn",ResolutionCategory.find.findByCode("LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		
		l.add(newResolution("indéterminé","PbI-indetermine",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("PC","PbI-PC",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("écran","PbI-ecran",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("espace disq insuf","PbI-espDisqInsuf",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("logiciel","PbI-logiciel",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("reboot PC","PbI-rebootPC",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));	
		l.add(newResolution("erreur paramétrage run","PbI-parametrageRun",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		
		l.add(newResolution("run de validation","Info-runValidation",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("arret séquenceur","Info-arretSeq",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("arret logiciel","Info_arretLogiciel",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("remboursement","Info-remboursement",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("flowcell redéposée","Info-FCredeposee",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));

		l.add(newResolution("densité hétérogène","QC-densiteHeterogen",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("densité diff. BOT/TOP","QC-densiteTopBot",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("densité SD fort","QC-densiteSdFort",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("densité PF SD fort","QC-densitePfSdFort",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité hétérogène","QC-intHeterogen",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité fble ttes bases","QC-intFbleBases",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité fble base A","QC-intFbleA",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité fble base T","QC-intFbleT",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité fble base C","QC-intFbleC",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité fble base G","QC-intFbleG",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensités oscillantes","QC-IntOscill",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité : chute","QC-intChute",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité nulle","QC-intNulle",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité TOP/BOT déséq.","QC-intTopBotDeseq",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité haut/bas déséq.","QC-intHautBasDeseq",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité B.M.S","QC-intBMS",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité fble zone dépôt","QC-intFbleZoneDepot",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité cycle 20 fble","QC-intCycle20Fble",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("tiles out","QC-tilesOut",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("saut de chimie","QC-sautChimie",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("% bases déséq.(non prévu)","QC-perctBasesDeseq",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("error rate fort","QC-errorRateFort",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("Q30 hétérogène","QC-q30Heterogen",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("alignement fble","QC-alignementFble",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("indexing-pb read index","QC-pbReadIndex",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("indexing-PF Reads Id. fble","QC-indexingPfReadsId",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("indexing-fble IND éch(s)","QC-indexingFbleInd",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("indexing-IND non présent","QC-indexNonPresent",ResolutionCategory.find.findByCode("QC"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));

		//ReadSet
		
		//CNS + CNG
		l.add(newResolution("lane abandonnée","Run-abandonLane",ResolutionCategory.find.findByCode("Run"), DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		
		//CNS only
		l.add(newResolution("pb construction banque","LIB-pbConstruction",ResolutionCategory.find.findByCode("LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("erreur dépôt banque","LIB-erreurDepot",ResolutionCategory.find.findByCode("LIB"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));

		l.add(newResolution("seq valides insuf","Qte-seqValInsuf",ResolutionCategory.find.findByCode("Qte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("seq utiles insuf","Qte-seqUtileInsuf",ResolutionCategory.find.findByCode("Qte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("couverture en X hors spec.","Qte-couverture",ResolutionCategory.find.findByCode("Qte"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("nb seq brutes faible","Qte-seqRawInsuf",ResolutionCategory.find.findByCode("Qte"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		
		l.add(newResolution("pb demultiplexage","IND-pbDemultiplex",ResolutionCategory.find.findByCode("IND"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("pb manip","IND-pbManip",ResolutionCategory.find.findByCode("IND"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("index incorrect","IND-indexIncorrect",ResolutionCategory.find.findByCode("IND"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
				
		l.add(newResolution("Q30","Qlte-Q30",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));				
		l.add(newResolution("Q30 hors spec.","Qlte-Q30HorsSpec",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("répartition bases","Qlte-repartitionBases",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));				
		l.add(newResolution("adaptateurs/Kmers","Qlte-adapterKmer",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("duplicat > 30","Qlte-duplicat",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("% adaptateurs détectés","Qlte-adapterPercent",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("% duplicat élevé","Qlte-duplicatElevee",ResolutionCategory.find.findByCode("Qlte"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		
		
		l.add(newResolution("conta indéterminée","TAXO-contaIndeterm",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("conta manip","TAXO-contaManip",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("conta mat ori","TAXO-contaMatOri",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("non conforme","TAXO-nonConforme",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("mitochondrie","TAXO-mitochondrie",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("chloroplast","TAXO-chloroplast",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("virus","TAXO-virus",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("bactérie","TAXO-bacteria",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()))); 
		l.add(newResolution("fungi","TAXO-fungi",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		
		l.add(newResolution("% rRNA élevé","RIBO-percEleve",ResolutionCategory.find.findByCode("RIBO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
				
		l.add(newResolution("% mapping faible","MAP-PercMappingFble",ResolutionCategory.find.findByCode("MAP"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		
		l.add(newResolution("test Dev","Info-testDev",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("test Prod","Info-testProd",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		
		l.add(newResolution("sexe incorrect","Sample-sexeIncorrect",ResolutionCategory.find.findByCode("Sample"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
				
		l.add(newResolution("erreur Experimental Type","LIMS-erreurExpType",ResolutionCategory.find.findByCode("LIMS"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
			

		//Experiment
		//CNS
		l.add(newResolution("% rRNA élevé","riboEleve",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("déroulement correct",	"correct",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("échec expérience",	"echec-experience",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("erreur programme",	"erreur-prgme",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("erreur protocole",	"erreur-proto",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("erreur matériel",	"erreur-sample",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("erreur manip",	"erreur-manip",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("erreur kit",	"erreur-kit",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("erreur réactif",	"erreur-reactif",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("erreur input",	"erreur-input",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("erreur TAG utilisé",	"erreur-tag",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("inversion d'index",	"inversion-index",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("contamination manip",	"conta-manip",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("réactifs : lot à problème",	"pb-lotReactif",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("pb manip",	"pb-manip",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("pb qté adaptateurs utilisé",	"pb-qteAdaptateurs",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("pb de purification",	"pb-purif",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("perte de matériel après purif",	"perte-materiel",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("sizing - pb de gel",	"sizing-pbGel",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("sizing - pb de migration",	"sizing-pbMigration",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("sizing - pb cuve",	"sizing-pbCuve",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("accident de manipulation",	"accident-manip",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("tube tombé en sortie d'exp",	"chute-supportContainer",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("pb robot",	"pb-robot",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("casse du tube au covaris",	"casse-supportContainer",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("suramplification",	"suramplification",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("rendement bq trop fble",	"concLibFble",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("rendement bq ampli trop fble",	"concLibAmpliFble",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("qté en sortie d'exp insuf",	"qteFinaleInsuff",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("tube bq transformé en sol stock",	"transforme-solStock",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("résultat dosage trop faible",	"dosage-tropFble",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("Qbit : non détecté",	"dosage-nonDetecte",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("profil bioanalyzer non valide",	"profil-nonValide",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("profil  bioanalyzer atypique",	"profil-atypique",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("profil  bioanalyzer plat",	"profil-plat",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("profil bioanalyzer : pic adaptateurs",	"presenceAdaptateurs",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("qPCR : conc trop faible",	"concQpcrFble",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("qPCR : pb ecart-type Ct",	"pbEcartTypeQpcr",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));
		l.add(newResolution("créé par erreur",	"erreur-creation",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Experiment.name())));

		DAOHelpers.saveModels(Resolution.class, l, errors);
	}

}
