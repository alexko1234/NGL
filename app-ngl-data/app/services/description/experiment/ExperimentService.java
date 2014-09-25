package services.description.experiment;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;
import static services.description.DescriptionFactory.newProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;

import com.typesafe.config.ConfigFactory;
public class ExperimentService {
	
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		DAOHelpers.removeAll(ProcessType.class, ProcessType.find);
		DAOHelpers.removeAll(ExperimentTypeNode.class, ExperimentTypeNode.find);

		DAOHelpers.removeAll(ExperimentType.class, ExperimentType.find);
		DAOHelpers.removeAll(ExperimentCategory.class, ExperimentCategory.find);

		DAOHelpers.removeAll(Protocol.class, Protocol.find);		
		DAOHelpers.removeAll(ProtocolCategory.class, ProtocolCategory.find);

		saveProtocolCategories(errors);
		saveProtocol(errors);
		saveExperimentCategories(errors);
		saveExperimentTypes(errors);
		saveExperimentTypeNodes(errors);
	}

	@SuppressWarnings("unchecked")
	public static void saveProtocolCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProtocolCategory> l = new ArrayList<ProtocolCategory>();
		l.add(DescriptionFactory.newSimpleCategory(ProtocolCategory.class, "Developpement", "development"));
		l.add(DescriptionFactory.newSimpleCategory(ProtocolCategory.class, "Production", "production"));
		DAOHelpers.saveModels(ProtocolCategory.class, l, errors);

	}

	public static void saveProtocol(Map<String, List<ValidationError>> errors) throws DAOException {
		List<Protocol> l = new ArrayList<Protocol>();
		l.add(newProtocol("Fragmentation_ptr_sox140_1","fragmentation_ptr_sox140_1","path1","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("BqSPRI_ptr_sox142_1","bqspri_ptr_sox142_1","path2","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Amplif_ptr_sox144_1","amplif_ptr_sox144_1","path3","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Depot_Illumina_prt_1","depot_illumina_ptr_1","path4","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Depot_Illumina_prt_2","depot_illumina_ptr_2","path5","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Depot_Illumina_prt_3","depot_illumina_ptr_3","path6","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Depot_Opgen_prt_1","depot_opgen_ptr_1","path7","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("PrepFC_CBot_ptr_sox139_1","prepfc_cbot_ptr_sox139_1","path7","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("Proto_QC_v1","proto_qc_v1","path7","1", ProtocolCategory.find.findByCode("production")));

		DAOHelpers.saveModels(Protocol.class, l, errors);

	}

	/**
	 * Save all ExperimentCategory
	 * @param errors
	 * @throws DAOException 
	 */
	public static void saveExperimentCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ExperimentCategory> l = new ArrayList<ExperimentCategory>();
		for (ExperimentCategory.CODE code : ExperimentCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(ExperimentCategory.class, code.name(), code.name()));
		}
		DAOHelpers.saveModels(ExperimentCategory.class, l, errors);
	}


	private static void saveExperimentTypes(
			Map<String, List<ValidationError>> errors) throws DAOException {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		l.add(newExperimentType("Void Opgen Illumina","void-opgen-depot",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null, null,"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newExperimentType("Depot Opgen", "opgen-depot"
				, ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),null, getProtocols("depot_opgen_ptr_1"), getInstrumentUsedTypes("ARGUS"), "ManyToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		//Prepaflowcell : to finish
		l.add(newExperimentType("Void Depot Illumina","void-illumina-depot",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null, null,"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newExperimentType("Preparation flowcell", "prepa-flowcell", ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsPrepaflowcell(),  getProtocols("prepfc_cbot_ptr_sox139_1"), getInstrumentUsedTypes("cBot-interne","cBot"), "ManyToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
		
			//transformation

			//library
			l.add(newExperimentType("Fragmentation","fragmentation",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null, getProtocols("fragmentation_ptr_sox140_1"), getInstrumentUsedTypes("hand","covaris-s2","covaris-e210"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS) ));
			l.add(newExperimentType("Librairie indexée","librairie-indexing",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibIndexing(), getProtocols("bqspri_ptr_sox142_1"), getInstrumentUsedTypes("hand","spri"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			l.add(newExperimentType("Librairie dual indexing","librairie-dualindexing",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibDualIndexing(), getProtocols("bqspri_ptr_sox142_1"), getInstrumentUsedTypes("hand","spri"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			l.add(newExperimentType("Amplification","amplification",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null, getProtocols("amplif_ptr_sox144_1") , getInstrumentUsedTypes("hand","thermo"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

			//pre-sequencing
			//attention proto, attention robot voir avec julie
			l.add(newExperimentType("Solution stock","solution-stock",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null, getProtocols("amplif_ptr_sox144_1") , getInstrumentUsedTypes("hand"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

			//qc
			l.add(newExperimentType("Bioanalyzer Non Ampli","bioanalyzer-na",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsBioanalyzer(), getProtocols("proto_qc_v1"), getInstrumentUsedTypes("agilent-2100"),"OneToVoid", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			l.add(newExperimentType("Bioanalyzer Ampli","bioanalyzer-a",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsBioanalyzer(), getProtocols("proto_qc_v1"), getInstrumentUsedTypes("agilent-2100"),"OneToVoid", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			l.add(newExperimentType("QuBit","qubit",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), null, getProtocols("proto_qc_v1"), getInstrumentUsedTypes("iqubit"),"OneToVoid", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			l.add(newExperimentType("qPCR","qpcr",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), null, getProtocols("proto_qc_v1"), getInstrumentUsedTypes("iqpcr"),"OneToVoid", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

			//purif
			l.add(newExperimentType("Ampure Non Ampli","ampure-na",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), null, null, getInstrumentUsedTypes("hand"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			l.add(newExperimentType("Ampure Ampli","ampure-a",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), null, null, getInstrumentUsedTypes("hand"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

			//void
			l.add(newExperimentType("Void Banque","void-banque",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null, null,"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			l.add(newExperimentType("Void qPCR","void-qpcr",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null, null,"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			l.add(newExperimentType("Void Depot Illumina","void-illumina-depot",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null, null,"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

			
			//Depot solexa
			l.add(newExperimentType("Depot Illumina", "illumina-depot"
					, ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),null, getProtocols("depot_illumina_ptr_1","depot_illumina_ptr_2","depot_illumina_ptr_3"), getInstrumentUsedTypes("MISEQ","HISEQ2000","HISEQ2500"), "OneToVoid", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

		}

		DAOHelpers.saveModels(ExperimentType.class, l, errors);

	}



	

	private static void saveExperimentTypeNodes(Map<String, List<ValidationError>> errors) throws DAOException {

		newExperimentTypeNode("void-opgen-depot", getExperimentTypes("void-opgen-depot").get(0), false, false, null, null, null).save();
		newExperimentTypeNode("void-illumina-depot", getExperimentTypes("void-illumina-depot").get(0), false, false, null, null, null).save();
		if(ConfigFactory.load().getString("ngl.env").equals("PROD")){
			newExperimentTypeNode("prepa-flowcell",getExperimentTypes("prepa-flowcell").get(0),false,false,getExperimentTypeNodes("void-illumina-depot"),null,null).save();
		}
		newExperimentTypeNode("opgen-depot",getExperimentTypes("opgen-depot").get(0),false,false,getExperimentTypeNodes("void-opgen-depot"),null,null).save();
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){

			newExperimentTypeNode("void-banque", getExperimentTypes("void-banque").get(0), false, false, null, null, null).save();
			newExperimentTypeNode("void-qpcr", getExperimentTypes("void-qpcr").get(0), false, false, null, null, null).save();
			newExperimentTypeNode("solution-stock",getExperimentTypes("solution-stock").get(0),false,false,getExperimentTypeNodes("void-qpcr"),null,null).save();
			newExperimentTypeNode("prepa-flowcell",getExperimentTypes("prepa-flowcell").get(0),false,false,getExperimentTypeNodes("void-illumina-depot","solution-stock"),null,null).save();
			newExperimentTypeNode("illumina-depot",getExperimentTypes("illumina-depot").get(0),false,false,getExperimentTypeNodes("prepa-flowcell"),null,null).save();				
			newExperimentTypeNode("fragmentation", getExperimentTypes("fragmentation").get(0), false, false, getExperimentTypeNodes("void-banque"), getExperimentTypes("ampure-na"), getExperimentTypes("bioanalyzer-na")).save();
			newExperimentTypeNode("librairie-indexing", getExperimentTypes("librairie-indexing").get(0), false, false, getExperimentTypeNodes("fragmentation"), getExperimentTypes("ampure-na"), getExperimentTypes("qubit","bioanalyzer-na")).save();
			newExperimentTypeNode("librairie-dualindexing", getExperimentTypes("librairie-dualindexing").get(0), false, false, getExperimentTypeNodes("fragmentation"), getExperimentTypes("ampure-na"), getExperimentTypes("qubit","bioanalyzer-na")).save();
			newExperimentTypeNode("amplification", getExperimentTypes("amplification").get(0), false, false, getExperimentTypeNodes("librairie-indexing","librairie-dualindexing"), getExperimentTypes("ampure-na"), getExperimentTypes("bioanalyzer-na")).save();
		}
		


	}

	private static List<Protocol> getProtocols(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(Protocol.class,Protocol.find, codes);
	}

	private static List<InstrumentUsedType> getInstrumentUsedTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(InstrumentUsedType.class,InstrumentUsedType.find, codes);
	}

	private static List<ExperimentType> getExperimentTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentType.class,ExperimentType.find, codes);
	}

	private static List<ExperimentTypeNode> getExperimentTypeNodes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentTypeNode.class,ExperimentTypeNode.find, codes);
	}

	private static List<PropertyDefinition> getPropertyDefinitionsPrepaflowcell() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Outputcontainer
		propertyDefinitions.add(newPropertiesDefinition("% phiX", "phixPercent", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null, null, null, null, "single",1));		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",2));
		
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Volume sol. stock à engager dénat.", "requiredVolume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",1, false));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume NaOH", "NaOHVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",2));
		
		propertyDefinitions.add(newPropertiesDefinition("Conc. solution NaOH", "NaOHConcentration", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
				,null,null,null,"single",3));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume EB", "EBVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",4, false));

		propertyDefinitions.add(newPropertiesDefinition("Concentration dénat. ", "finalConcentration1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "nM"),MeasureUnit.find.findByCode( "nM"),"single",5));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume dénat.", "finalVolume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",6));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume dénat. à engager dans dilution", "requiredVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",7, false));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume HT1", "HT1Volume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",8, false));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume Phix", "phixVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",9, false));

		propertyDefinitions.add(newPropertiesDefinition("Conc. phiX", "phixConcentration", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "pM"),"single",10));

		propertyDefinitions.add(newPropertiesDefinition("Conc. dilution", "finalConcentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"), "single",11));

		propertyDefinitions.add(newPropertiesDefinition("Volume dilution", "finalVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",12));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume dilution à engager sur la piste", "requiredVolume3", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",13, false));		
		
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitionsLibIndexing() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Ajouter la liste des index illumina
		propertyDefinitions.add(newPropertiesDefinition("Index","tag", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.SampleUsed),String.class, true, "single"));
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitionsLibDualIndexing() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Ajouter la liste des index illumina
		propertyDefinitions.add(newPropertiesDefinition("Index1","tag1", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.SampleUsed),String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Index2","tag2", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.SampleUsed),String.class, true, "single"));
		return propertyDefinitions;
	}


	//TODO
	// Propriete taille en output et non en input ?
	// Valider les keys
	public static List<PropertyDefinition> getPropertyDefinitionsBioanalyzer() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		// A supprimer une fois le type de support category sera géré
		propertyDefinitions.add(newPropertiesDefinition("Position","location", LevelService.getLevels(Level.CODE.ContainerIn),Integer.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "committedVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));		
		propertyDefinitions.add(newPropertiesDefinition("Taille", "size", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("kb"), MeasureUnit.find.findByCode("kb"), "single"));
		// Voir avec Guillaume comment gérer les fichiers
		propertyDefinitions.add(newPropertiesDefinition("Profil DNA HS", "fileResult", LevelService.getLevels(Level.CODE.ContainerOut),String.class, true, "single"));
		return propertyDefinitions;
	}

}
