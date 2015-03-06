package services.description.experiment;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
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
		//saveProtocol(errors);
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
	/*
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
		
		//for CNG
		l.add(newProtocol("Sop_depot_1","sop_depot_1","path4","1", ProtocolCategory.find.findByCode("production")));
		l.add(newProtocol("SOP","SOP","path4","1", ProtocolCategory.find.findByCode("production")));

		DAOHelpers.saveModels(Protocol.class, l, errors);

	} */

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

		l.add(newExperimentType("Ext to dépôt opgen","ext-to-opgen-depot",
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), getPropertyDefinitionExtToOpgenDepot(), null,"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		/*
		l.add(newExperimentType("Depot Opgen", "opgen-depot",1600
				, ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),null, getProtocols("depot_opgen_ptr_1"), 
				getInstrumentUsedTypes("ARGUS"), "ManyToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS))); */
		
		l.add(newExperimentType("Depot Opgen", "opgen-depot",1600
				, ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionOpgenDepot(), 
				getInstrumentUsedTypes("ARGUS"), "ManyToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		//Prepaflowcell : to finish
		l.add(newExperimentType("Ext to prepa flowcell","ext-to-prepa-flowcell",
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)));
		
		l.add(newExperimentType("Preparation flowcell", "prepa-flowcell",1200, 
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsPrepaflowcell(),
				getInstrumentUsedTypes("cBot-interne","cBot"), "ManyToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
		
			//transformation CNS

			//library
			l.add(newExperimentType("Fragmentation","fragmentation",200,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionFragmentation(),
					getInstrumentUsedTypes("hand","covaris-s2","covaris-e210"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS) ));
			
			l.add(newExperimentType("Librairie indexée","librairie-indexing",400,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibIndexing(),
					getInstrumentUsedTypes("hand","spri"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			/*l.add(newExperimentType("Librairie dual indexing","librairie-dualindexing",600,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibDualIndexing(),  
					getInstrumentUsedTypes("hand","spri"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));*/
			
			l.add(newExperimentType("Amplification","amplification",800,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
					getInstrumentUsedTypes("hand","thermocycler"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

			//attention proto, attention robot voir avec julie
			l.add(newExperimentType("Solution stock","solution-stock",1000,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			

			//quality control

			//purif
			l.add(newExperimentType("Ampure Non Ampli","ampure-na",
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()),
					null, getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			l.add(newExperimentType("Ampure Ampli","ampure-a",
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

			//void
			l.add(newExperimentType("Ext to Banque","ext-to-library",
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			/**********************************************************************************/
			//transformation CNG & CNS
			
			l.add(newExperimentType("Ext to qPCR","ext-to-qpcr",
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)));
			
			l.add(newExperimentType("Ext to prepa flowcell","ext-to-prepa-flowcell",
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null,  null,"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)));
			
			/*
			l.add(newExperimentType("Migration sur puce","chip-migration",
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigration(), 
					getProtocols("proto_qc_v1"), getInstrumentUsedTypes("agilent-2100-bioanalyzer","labChipGX"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
			*/
			
			l.add(newExperimentType("Migration sur puce (ampli)","chip-migration-post-pcr",650,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigration(), 
					getInstrumentUsedTypes("agilent-2100-bioanalyzer", "labchipGX"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
			
			l.add(newExperimentType("Migration sur puce (non ampli)","chip-migration-pre-pcr",250,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigration(), 
					getInstrumentUsedTypes("agilent-2100-bioanalyzer", "labchipGX"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
			
			
			l.add(newExperimentType("Dosage fluorimétrique","fluo-quantification",450,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), null, 
					getInstrumentUsedTypes("qubit"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
			
			l.add(newExperimentType("Quantification qPCR","qPCR-quantification",850,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), null, 
					getInstrumentUsedTypes("rocheLightCycler-qPCR","stratagene-qPCR"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG))); 	
			
			//Depot solexa -- FDS 27/02/2015 devient commun
			l.add(newExperimentType("Depot Illumina", "illumina-depot", 1400,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),getPropertyDefinitionsIlluminaDepot(),
					getInstrumentUsedTypes("MISEQ","HISEQ2000","HISEQ2500"), "OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));

			
			/**********************************************************************************/
			
			//17-12-2014 transformation CNG

			//library
			l.add(newExperimentType("Fragmentation","fragmentation",200,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
					getInstrumentUsedTypes("hand","covaris-le220","covaris-e210"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG) ));
			
			l.add(newExperimentType("Librairie indexée","librairie-indexing",400,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibIndexing(),
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
			
			/*l.add(newExperimentType("Librairie dual indexing","librairie-dualindexing",600,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibDualIndexing(),
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));*/
			
			l.add(newExperimentType("PCR","pcr",800,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
					getInstrumentUsedTypes("hand","thermocycler"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));

			//pre-sequencing
			//    FDS remommage a la demande de Julie en solution-x-nM=> lib-normalization
			l.add(newExperimentType("Librairie normalisée","lib-normalization",1000,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
			
			// FDS new 02-02-2015, intrument Used =>robot oui mais lequel???
			/*l.add(newExperimentType("Librairie dénaturée","denat-dil-lib",1100,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));*/


			// NO qc au CNG ??
			// NO purif au CNG ??
			// NO void au CNG ??
			
	
			l.add(newExperimentType("Préparation flowcell","prepa-flowcell-cng",1100,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsPrepaflowcell(),
					getInstrumentUsedTypes("cBot", "cBot-onboard"),"ManyToOne", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));

		}

		DAOHelpers.saveModels(ExperimentType.class, l, errors);

	}



	

	private static List<PropertyDefinition> getPropertyDefinitionFragmentation() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));
		return propertyDefinitions;
	}

	private static void saveExperimentTypeNodes(Map<String, List<ValidationError>> errors) throws DAOException {

		newExperimentTypeNode("ext-to-opgen-depot", getExperimentTypes("ext-to-opgen-depot").get(0), false, false, null, null, null).save();
		newExperimentTypeNode("ext-to-prepa-flowcell", getExperimentTypes("ext-to-prepa-flowcell").get(0), false, false, null, null, null).save();
		if(ConfigFactory.load().getString("ngl.env").equals("PROD")){
			newExperimentTypeNode("prepa-flowcell",getExperimentTypes("prepa-flowcell").get(0),false,false,getExperimentTypeNodes("ext-to-prepa-flowcell"),null,null).save();
		}
		newExperimentTypeNode("opgen-depot",getExperimentTypes("opgen-depot").get(0),false,false,getExperimentTypeNodes("ext-to-opgen-depot"),null,null).save();
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){

			newExperimentTypeNode("ext-to-library", getExperimentTypes("ext-to-library").get(0), false, false, null, null, null).save();
			newExperimentTypeNode("ext-to-qpcr", getExperimentTypes("ext-to-qpcr").get(0), false, false, null, null, null).save();	
			
			//REM : experimentTypes list confirmées par Julie
			newExperimentTypeNode("fragmentation", getExperimentTypes("fragmentation").get(0), false, false, getExperimentTypeNodes("ext-to-library"), 
					getExperimentTypes("ampure-na"), getExperimentTypes("fluo-quantification","chip-migration-pre-pcr")).save();
			
			newExperimentTypeNode("librairie-indexing", getExperimentTypes("librairie-indexing").get(0), false, false, getExperimentTypeNodes("fragmentation"), 
					getExperimentTypes("ampure-na"), getExperimentTypes("fluo-quantification","chip-migration-pre-pcr")).save();
			
			/*newExperimentTypeNode("librairie-dualindexing", getExperimentTypes("librairie-dualindexing").get(0), false, false, getExperimentTypeNodes("fragmentation"), 
					getExperimentTypes("ampure-na"), getExperimentTypes("fluo-quantification","chip-migration-pre-pcr")).save();*/
			
			newExperimentTypeNode("amplification", getExperimentTypes("amplification").get(0), false, false, getExperimentTypeNodes("librairie-indexing"), 
					getExperimentTypes("ampure-a"), getExperimentTypes("fluo-quantification","chip-migration-post-pcr")).save();
			

			newExperimentTypeNode("solution-stock",getExperimentTypes("solution-stock").get(0),false,false,getExperimentTypeNodes("ext-to-qpcr","amplification"),
					null,null).save();
			
			newExperimentTypeNode("prepa-flowcell",getExperimentTypes("prepa-flowcell").get(0),false,false,getExperimentTypeNodes("ext-to-prepa-flowcell","solution-stock"),
					null,null).save();

			//FDS 02/02/2015 renommage demandé 
			newExperimentTypeNode("lib-normalization",getExperimentTypes("lib-normalization").get(0),false,false,getExperimentTypeNodes("ext-to-qpcr"),
					null,null).save();
			
			//FDS 02/02/2015  nouveau node necessaire pour denat-dil-lib , previousExp est lib-normalization ???
			/*newExperimentTypeNode("denat-dil-lib",getExperimentTypes("denat-dil-lib").get(0),false,false,getExperimentTypeNodes("lib-normalization"),
					null,null).save();*/
			
			// FDS 02/02/2015  nouveau node necessaire pour lib-b ??? previousExp est ???
			
			// FDS "solution-X-nM" n'existe plus...  en principe c'est lib-b pour "prepa-flowcell-cng" mais pas encore specifie =>  mettre "denat-dil-lib" ???
			newExperimentTypeNode("prepa-flowcell-cng",getExperimentTypes("prepa-flowcell-cng").get(0),false,false,getExperimentTypeNodes("ext-to-prepa-flowcell","lib-normalization"),
					null,null).save();
			
	        // FDS 27/02/2015 supression "illumina-depot-cng"
			
			newExperimentTypeNode("illumina-depot",getExperimentTypes("illumina-depot").get(0),false,false,getExperimentTypeNodes("prepa-flowcell"),
					null,null).save();
		}
		


	}

/*	private static List<Protocol> getProtocols(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(Protocol.class,Protocol.find, codes);
	}*/

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
		propertyDefinitions.add(newPropertiesDefinition("% phiX", "phixPercent", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null, null, null, null, "single",1,true,"1"));		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",2));
		
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Volume sol. stock à engager dénat.", "requiredVolume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",1, false));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume NaOH", "NaOHVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",2,true,"1"));
		
		propertyDefinitions.add(newPropertiesDefinition("Conc. solution NaOH", "NaOHConcentration", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true,DescriptionFactory.newValues("1N","2N"), "2N", "single",3));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume EB", "EBVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",4, false));

		propertyDefinitions.add(newPropertiesDefinition("Concentration dénat. ", "finalConcentration1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "nM"),MeasureUnit.find.findByCode( "nM"),"single",5,true,"2"));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume dénat.", "finalVolume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "20"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",6));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume dénat. à engager dans dilution", "requiredVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",7, false));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume HT1", "HT1Volume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",8, false));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume Phix", "phixVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",9, false));

		propertyDefinitions.add(newPropertiesDefinition("Conc. sol. mère Phix", "phixConcentration", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"),"single",10, true,"0.02"));

		propertyDefinitions.add(newPropertiesDefinition("Conc. dilution", "finalConcentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"), "single",11));

		propertyDefinitions.add(newPropertiesDefinition("Volume dilution", "finalVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "1000"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",12));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume dilution à engager sur la piste", "requiredVolume3", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",13, false));		
		
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitionsLibIndexing() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Ajouter la liste des index illumina
		propertyDefinitions.add(newPropertiesDefinition("Tag","tag", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content),String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Catégorie tag","tagCategory", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content),String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, false, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitionsLibDualIndexing() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Ajouter la liste des index illumina
		propertyDefinitions.add(newPropertiesDefinition("Index1","tag1", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content),String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Index2","tag2", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content),String.class, true, "single"));
		return propertyDefinitions;
	}


	//TODO
	// Propriete taille en output et non en input ?
	// Valider les keys
	public static List<PropertyDefinition> getPropertyDefinitionsChipMigration() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		// A supprimer une fois le type de support category sera géré
		propertyDefinitions.add(newPropertiesDefinition("Position","position", LevelService.getLevels(Level.CODE.ContainerIn),Integer.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));		
		propertyDefinitions.add(newPropertiesDefinition("Taille", "size", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("kb"), MeasureUnit.find.findByCode("kb"), "single"));
		// Voir avec Guillaume comment gérer les fichiers
		propertyDefinitions.add(newPropertiesDefinition("Profil DNA HS", "fileResult", LevelService.getLevels(Level.CODE.ContainerOut),String.class, true, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Utiliser par import ngl-data CNG de creation des depot-illumina
		//propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Experiment), Integer.class, false, "single"));	
		propertyDefinitions.add(newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, true, "single"));
		return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getPropertyDefinitionExtToOpgenDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, false, "single"));
		return propertyDefinitions;
	}	
	
	private static List<PropertyDefinition> getPropertyDefinitionOpgenDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, true, "single"));
		return propertyDefinitions;
	}

}
