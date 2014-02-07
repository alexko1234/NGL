package services.description.common;

import services.description.DescriptionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.laboratory.common.description.ObjectType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;


public class ObjectTypeService {
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{
		
		//Can not be removed cause integrity constraints ...
		//DAOHelpers.removeAll(ObjectType.class, ObjectType.find);
		
		saveObjectTypes(errors);
		
		updateStatesObjectTypes(errors);
		
		updateResolutionsObjectTypes(errors);
	}
	

	
	public static void saveObjectTypes(Map<String,List<ValidationError>> errors) throws DAOException{
			
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Container.name()), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Project.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Experiment.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Process.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Run.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.ReadSet.name() ), errors);
			//DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.File.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Sample.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Instrument.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Reagent.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class, DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Import.name() ), errors);
			DAOHelpers.saveModel(ObjectType.class,DescriptionFactory.newDefaultObjectType(ObjectType.CODE.Treatment.name() ), errors);
	}
	
	
	public static void updateStatesObjectTypes(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ObjectType> l = new ArrayList<ObjectType>();
		
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Container.name(), false, DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "N", "IW-P", "IW-E", "IU","IS" ) ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Project.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Experiment.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Process.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Run.name(), false, DescriptionFactory.getStates("N", "F", "IW-V", "IP-V", "F-V", "IP-S", "F-S", "FE-S", "IW-RG", "IP-RG", "F-RG") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.ReadSet.name(), false, DescriptionFactory.getStates("N", "A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		//l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.File.name(), false, DescriptionFactory.getStates("A","UA", "IW-QC", "IP-QC", "F-QC", "IW-V", "IP-V", "F-V", "IP-RG", "F-RG") ));
		//default values
		//TODO : define values
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Sample.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Instrument.name(), false, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Reagent.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Import.name(), true, DescriptionFactory.getStates("F", "N", "IP") ));
		l.add(DescriptionFactory.setStatesToObjectType(ObjectType.CODE.Treatment.name(), false, DescriptionFactory.getStates("F", "N", "IP") ));
		
		DAOHelpers.updateModels(ObjectType.class, l, errors);	
	}
	
	
	public static void updateResolutionsObjectTypes(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ObjectType> l = new ArrayList<ObjectType>();
		
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.Run.name(), false, DescriptionFactory.getResolutions("pbMIndetermine","pbMChiller","pbMPelletier","pbMFluidiq","pbMLaser","pbMCamera","pbMLentille","pbMPEmodule","pbMCbot","pbRIndetermine","pbRFC","pbRCbot","pbRSeq","pbRIndexing","pbRPEmodule","pbRRehybR1","pbRRehybR2","pbRErreurReac","pbRAjoutReac","savIntensite","savDensiteElevee","savDensiteFaible","savPF","savPhasing","savPrephasing","savErrRate","savQ30","savIndDemultiplex","pbIPC","pbIEcran","pbIEspDisqInsuf","pbILogiciel","pbIRebootPC","infoRunValidation","infoArretSeq","infoArretLogiciel","infoRemboursement") ));
		
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.ReadSet.name(), false, DescriptionFactory.getResolutions("qteSeqValInsuf", "qteSeqUtileInsuf", "indPbDemultiplex", "indPbManip", "qlteQ30", "qlteRepartitionBases", "qlteAdapterKmer", "qlteDuplicat", "taxoContaIndeterm", "taxoContaManip", "taxoContaMatOri", "taxoNonConforme", "taxoMitochondrie", "taxoChloroplast", "taxoVirus", "taxoBacteria", "taxoFungi", "riboEleve" ) ));
		
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.Container.name(), false, DescriptionFactory.getResolutions("cont_reso1","cont_reso2","cont_reso3") ));
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.Project.name(), false, DescriptionFactory.getResolutions("pro_reso1","pro_reso2","pro_reso3") ));
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.Process.name(), false, DescriptionFactory.getResolutions("proj_reso1","proj_reso2","proj_reso3") ));
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.Experiment.name(), false, DescriptionFactory.getResolutions("exp_reso1","exp_reso2","exp_reso3") ));
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.Sample.name(), false, DescriptionFactory.getResolutions("samp_reso1","samp_reso2","samp_reso3") ));
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.Instrument.name(), false, DescriptionFactory.getResolutions("ins_reso1","ins_reso2","ins_reso3") ));
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.Reagent.name(), false, DescriptionFactory.getResolutions("reag_reso1","reag_reso2","reag_reso3") ));
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.Import.name(), false, DescriptionFactory.getResolutions("imp_reso1","imp_reso2","imp_reso3") ));
		l.add(DescriptionFactory.setResolutionsToObjectType(ObjectType.CODE.Treatment.name(), false, DescriptionFactory.getResolutions("trt_reso1","trt_reso2","trt_reso3") ));
		
		DAOHelpers.updateModels(ObjectType.class, l, errors);	
	}
	
	


	
}
