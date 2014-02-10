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
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème quantité", "Qté"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème indexing", "IND"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème qualité", "Qlté"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème taxon", "TAXO"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème ribosomes", "RIBO"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème mapping", "MAP"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème merging", "MERG"));
		
		//default value
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Default", "default"));
		
		DAOHelpers.saveModels(ResolutionCategory.class, l, errors);
	}
	
	public static void saveResolutions(Map<String,List<ValidationError>> errors) throws DAOException{
		List<Resolution> l = new ArrayList<Resolution>();
				
		//Run
		l.add(newResolution("indéterminé","pbMIndetermine",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("chiller","pbMChiller",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("pelletier","pbMPelletier",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("fluidique","pbMFluidiq",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("laser","pbMLaser",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("camera","pbMCamera",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("lentille","pbMLentille",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("PE module","pbMPEmodule",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("cBot","pbMCbot",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));		
		l.add(newResolution("indéterminé","pbRIndetermine",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("flowcell","pbRFC",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("cBot","pbRCbot",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("séquencage","pbRSeq",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("indexing","pbRIndexing",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("PE module","pbRPEmodule",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("rehyb primer R1","pbRRehybR1",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("rehyb primer R2","pbRRehybR2",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("erreur réactifs","pbRErreurReac",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("rajout réactifs","pbRAjoutReac",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("intensité","savIntensite",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("densité clusters trop élevée","savDensiteElevee",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("densité clusters trop faible","savDensiteFaible",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("%PF","savPF",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("phasing","savPhasing",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("prephasing","savPrephasing",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("error rate","savErrRate",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("Q30","savQ30",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("indexing/demultiplexage","savIndDemultiplex",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("PC","pbIPC",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("écran","pbIEcran",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("espace disq insuf","pbIEspDisqInsuf",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("logiciel","pbILogiciel",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("reboot PC","pbIRebootPC",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("run de validation","infoRunValidation",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("arret séquenceur","infoArretSeq",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("arret logiciel","infoArretLogiciel",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		l.add(newResolution("remboursement","infoRemboursement",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.Run.name())));
		
		//ReadSet
		l.add(newResolution("seq valides insuf","qteSeqValInsuf",ResolutionCategory.find.findByCode("Qté"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("seq utiles insuf","qteSeqUtileInsuf",ResolutionCategory.find.findByCode("Qté"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("pb demultiplexage","indPbDemultiplex",ResolutionCategory.find.findByCode("IND"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("pb manip","indPbManip",ResolutionCategory.find.findByCode("IND"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("Q30","qlteQ30",ResolutionCategory.find.findByCode("Qlté"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("répartition bases","qlteRepartitionBases",ResolutionCategory.find.findByCode("Qlté"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("adaptateurs/Kmers","qlteAdapterKmer",ResolutionCategory.find.findByCode("Qlté"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("duplicat > 30","qlteDuplicat",ResolutionCategory.find.findByCode("Qlté"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("conta indéterminée","taxoContaIndeterm",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("conta manip","taxoContaManip",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("conta mat ori","taxoContaMatOri",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("non conforme","taxoNonConforme",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("mitochondrie","taxoMitochondrie",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("chloroplast","taxoChloroplast",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("virus","taxoVirus",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("bactérie","taxoBacteria",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name()))); 
		l.add(newResolution("fungi","taxoFungi",ResolutionCategory.find.findByCode("TAXO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));
		l.add(newResolution("% rRNA élevé","riboEleve",ResolutionCategory.find.findByCode("RIBO"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), DescriptionFactory.getObjectTypes(ObjectType.CODE.ReadSet.name())));

		
		DAOHelpers.saveModels(Resolution.class, l, errors);
	}

}
