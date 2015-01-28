package services.description.instrument;

import static services.description.DescriptionFactory.newInstrumentCategory;
import static services.description.DescriptionFactory.newInstrumentUsedType;
import static services.description.DescriptionFactory.newPropertiesDefinition;
import static services.description.DescriptionFactory.newValues;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.Logger;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;

public class InstrumentService {
	
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{
		
		Logger.debug("Begin remove Instrument");
		DAOHelpers.removeAll(Instrument.class, Instrument.find);
		
		Logger.debug("Begin remove Instrument Used Type");
		DAOHelpers.removeAll(InstrumentUsedType.class, InstrumentUsedType.find);
		
		Logger.debug("Begin remove Instrument Category !!!");
		DAOHelpers.removeAll(InstrumentCategory.class, InstrumentCategory.find);
		
		Logger.debug("Begin save categories");
		saveInstrumentCategories(errors);
		
		Logger.debug("Begin save Instrument Used Type");
		saveInstrumentUsedTypes(errors);	
		
		Logger.debug("End Instrument service");
	}
	
	
	public static void saveInstrumentCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<InstrumentCategory> l = new ArrayList<InstrumentCategory>();
		l.add(newInstrumentCategory("Covaris","covaris"));
		l.add(newInstrumentCategory("Spri","spri"));
		l.add(newInstrumentCategory("Thermocycleur","thermocycler"));
		
		l.add(newInstrumentCategory("Quantification par fluorométrie","fluorometer"));
		l.add(newInstrumentCategory("Appareil de qPCR","qPCR-system"));
		l.add(newInstrumentCategory("Electrophorèse sur puce","chip-electrophoresis"));
		
		l.add(newInstrumentCategory("Main","hand"));
		l.add(newInstrumentCategory("CBot","cbot"));
		
		l.add(newInstrumentCategory("Séquenceur Illumina","illumina-sequencer"));
		l.add(newInstrumentCategory("Cartographie Optique Opgen","opt-map-opgen"));
		l.add(newInstrumentCategory("Extérieur","extseq"));
		
		l.add(newInstrumentCategory("Robot pipetage","liquid-handling-robot"));
		l.add(newInstrumentCategory("Appareil de sizing","sizing-system"));
				
		DAOHelpers.saveModels(InstrumentCategory.class, l, errors);
		
	}
	
	public static void saveInstrumentUsedTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		
		List<InstrumentUsedType> l = new ArrayList<InstrumentUsedType>();
		
		//CNS
		
		l.add(newInstrumentUsedType("Covaris S2", "covaris-s2", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("Covaris1", "Covaris1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Covaris2", "Covaris2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ) ,
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newInstrumentUsedType("Spri", "spri", InstrumentCategory.find.findByCode("spri"), getSpriProperties(), 
				getInstruments(
						createInstrument("Spri1", "Spri1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Spri2", "Spri2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Spri3", "Spri3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ), 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newInstrumentUsedType("Fluoroskan", "fluoroskan", InstrumentCategory.find.findByCode("fluorometer"), getQuBitProperties(), 
				getInstruments(
						createInstrument("Fluoroskan1", "Fluoroskan1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS))), 
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS))); //ok
		
		
		l.add(newInstrumentUsedType("Stratagene qPCR system", "stratagene-qPCR", InstrumentCategory.find.findByCode("qPCR-system"), getQPCRProperties(), 
				getInstruments(
						createInstrument("Stratagene1", "Stratagene1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS))),
				getContainerSupportCategories(new String[]{"tube","sheet-96"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newInstrumentUsedType("cBot-interne", "cBot-interne", InstrumentCategory.find.findByCode("cbot"), getCBotInterneProperties(), 
				getInstruments(
						createInstrument("cBot Fluor A", "cBot-Fluor-A", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot Fluor B", "cBot-Fluor-B", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot Platine A", "cBot-Platine-A", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot Platine B", "cBot-Platine-B", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot Mimosa", "cBot-Mimosa", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot Melisse", "cBot-Melisse", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS))), 
				getContainerSupportCategories(new String[]{"tube"}), getContainerSupportCategories(new String[]{"flowcell-2","flowcell-1"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

		
		l.add(newInstrumentUsedType("ARGUS", "ARGUS", InstrumentCategory.find.findByCode("opt-map-opgen"), getArgusProperties(), 
				getInstrumentOpgen(),
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"mapcard"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));	
		
		l.add(newInstrumentUsedType("EXTSOLEXA", "EXTSOLEXA", InstrumentCategory.find.findByCode("extseq"), null, 
				getInstrumentExtSolexa(),
				getContainerSupportCategories(new String[]{"flowcell-2","flowcell-1","flowcell-8"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newInstrumentUsedType("Biomek FX", "biomekFX", InstrumentCategory.find.findByCode("liquid-handling-robot"), null, 
				getInstruments(
						createInstrument("walle", "WALLE", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("r2d2", "R2D2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ) ,
				getContainerSupportCategories(new String[]{"sheet-96"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

		l.add(newInstrumentUsedType("TECAN evo 100", "tecan-evo-100", InstrumentCategory.find.findByCode("liquid-handling-robot"), null, 
				getInstruments(
						createInstrument("Tecan1", "Tecan1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS))),
						getContainerSupportCategories(new String[]{"sheet-96"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

		//TODO : verify getContainerSupportCategories parameters
		l.add(newInstrumentUsedType("Blue pippin", "blue-pippin", InstrumentCategory.find.findByCode("sizing-system"), null, 
				getInstruments(
						createInstrument("BluePippin1", "BluePippin1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS))),
						getContainerSupportCategories(new String[]{"sheet-96"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		/*****************************************************************************************************/
		//CNG et CNS
		l.add(newInstrumentUsedType("Main", "hand", InstrumentCategory.find.findByCode("hand"), null, 
				getInstruments(
						createInstrument("hand", "Main", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)) ),
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("QuBit", "qubit", InstrumentCategory.find.findByCode("fluorometer"), getQuBitProperties(), 
				getInstruments(
						createInstrument("quBit1", "QuBit1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)),
						createInstrument("QuBit2", "QuBit2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("QuBit3", "QuBit3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS))
						), 
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG))); //ok
		
		l.add(newInstrumentUsedType("GAIIx", "GAIIx", InstrumentCategory.find.findByCode("illumina-sequencer"), null, 
				getInstrumentGAII(),
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newInstrumentUsedType("MISEQ", "MISEQ", InstrumentCategory.find.findByCode("illumina-sequencer"), getMiseqProperties(), 
				getInstrumentMiSeq(),
				getContainerSupportCategories(new String[]{"flowcell-1"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG,Institute.CODE.CNS)));
		
		l.add(newInstrumentUsedType("HISEQ2000", "HISEQ2000", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq2000Properties(), 
				getInstrumentHiseq2000(),
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG,Institute.CODE.CNS)));
		
		l.add(newInstrumentUsedType("HISEQ2500", "HISEQ2500", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq2500Properties(), 
				getInstrumentHiseq2500(),
				getContainerSupportCategories(new String[]{"flowcell-8","flowcell-2"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG,Institute.CODE.CNS)));
		
		l.add(newInstrumentUsedType("Agilent 2100 bioanalyzer", "agilent-2100-bioanalyzer", InstrumentCategory.find.findByCode("chip-electrophoresis"), getChipElectrophoresisProperties(), 
				getInstruments(
						createInstrument("bioAnalyzer1", "BioAnalyzer1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG )), 
						createInstrument("bioAnalyzer2", "BioAnalyzer2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)) ), 
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("Thermocycleur", "thermocycler", InstrumentCategory.find.findByCode("thermocycler"), getThermocyclerProperties(), 
				getInstruments(
						createInstrument("thermoS1", "ThermoS1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("thermoS2", "ThermoS2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("thermoS3", "ThermoS3",  true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("thermo1", "Thermo1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)), 
						createInstrument("thermo2", "Thermo2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)), 
						createInstrument("thermo3", "Thermo3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("thermo4", "Thermo4", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)) 
						), 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("cBot", "cBot", InstrumentCategory.find.findByCode("cbot"), getCBotProperties(), 
				getInstruments(
						createInstrument("cBot1", "cBot1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)),
						createInstrument("cBot2", "cBot2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)),
						createInstrument("cBot3", "cBot3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)),
						createInstrument("cBot4", "cBot4", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}), getContainerSupportCategories(new String[]{"flowcell-8","flowcell-2"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("Covaris E210", "covaris-e210", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("covaris3", "Covaris3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("covaris4", "Covaris4", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cov1", "Cov1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)) 
						) , 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
		
		
		l.add(newInstrumentUsedType("LabChip GX", "labChipGX", InstrumentCategory.find.findByCode("chip-electrophoresis"), null, 
				getInstruments(
						createInstrument("labGX", "Lab_GX", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)) ,
						createInstrument("labChip1", "LabChip1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ) ,
				getContainerSupportCategories(new String[]{"sheet-384","sheet-96"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG)));
		/*****************************************************************************************************/
		
		
		//CNG
		l.add(newInstrumentUsedType("NEXTSEQ500", "NEXTSEQ500", InstrumentCategory.find.findByCode("illumina-sequencer"), getNEXTSEQ500Properties(), 
				getInstrumentNEXTSEQ500(),
				getContainerSupportCategories(new String[]{"flowcell-4"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("Roche Lightcycler qPCR system", "rocheLightCycler-qPCR", InstrumentCategory.find.findByCode("qPCR-system"), getQPCRProperties(), 
				getInstruments(
						createInstrument("lightCycler1", "LightCycler1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("lightCycler2", "LightCycler2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG))
						),
				getContainerSupportCategories(new String[]{"tube","sheet-96"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		

		
		l.add(newInstrumentUsedType("Covaris LE220", "covaris-le220", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("cov2", "Cov2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)) ) , 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG))); //ok
		
		
		
		l.add(newInstrumentUsedType("cBot-onboard", "cBot-onboard", InstrumentCategory.find.findByCode("cbot"), getCBotInterneProperties(), 
				getInstruments(
						createInstrument("cBot-Hi9", "cBot-interne-Hi9", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot-Hi10", "cBot-interne-Hi10", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot-Hi11", "cBot-interne-Hi11", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot-Miseq1", "cBot-interne-Miseq1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot-NextSeq1", "cBot-interne-Nextseq1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}), getContainerSupportCategories(new String[]{"flowcell-2","flowcell-1"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));


		DAOHelpers.saveModels(InstrumentUsedType.class, l, errors);
	}

	
	private static List<PropertyDefinition> getCBotProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Type lectures","sequencingProgramType"
        		, LevelService.getLevels(Level.CODE.Instrument,Level.CODE.ContainerSupport),String.class, true,DescriptionFactory.newValues("SR","PE"),"single"));
        propertyDefinitions.add(newPropertiesDefinition("Type flowcell","flowcellType"
        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("Paired End FC Hiseq-v3","Single FC Hiseq-v3","Rapid FC PE HS 2500-v1","Rapid FC SR HS 2500-v1"),"single"));
        propertyDefinitions.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Piste contrôle","control", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValuesWithDefault("Pas de piste contrôle (auto-calibrage)","Pas de piste contrôle (auto-calibrage)","1",
        		"2","3","4","5","6","7","8"),"Pas de piste contrôle (auto-calibrage)","single"));
        return propertyDefinitions;
	}

	
	private static List<PropertyDefinition> getCBotInterneProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Type lectures","sequencingProgramType"
        		, LevelService.getLevels(Level.CODE.Instrument,Level.CODE.ContainerSupport),String.class, true,DescriptionFactory.newValues("SR","PE"),"single"));
        propertyDefinitions.add(newPropertiesDefinition("Type flowcell","flowcellType"
        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("Rapid FC PE HS 2500-v1","Rapid FC SR HS 2500-v1",
        				"FC Miseq-v2","FC Miseq-v3"),"single"));
        propertyDefinitions.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Piste contrôle","control", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValuesWithDefault("Pas de piste contrôle (auto-calibrage)","Pas de piste contrôle (auto-calibrage)","1",
        		"2"),"Pas de piste contrôle (auto-calibrage)","single"));
        return propertyDefinitions;
	}

	private static List<PropertyDefinition> getHiseq2000Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Position","position"
        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single"));
        propertyDefinitions.add(newPropertiesDefinition("Type lecture", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        return propertyDefinitions;
	}

	private static List<PropertyDefinition> getMiseqProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Type lecture", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nom cassette Miseq", "miseqReagentCassette",LevelService.getLevels(Level.CODE.Instrument),String.class,true,"single"));
        return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getNEXTSEQ500Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Type lecture", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getArgusProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Type de MapCard", "mapcardType", LevelService.getLevels(Level.CODE.Instrument),String.class, true, newValues("standard","HD"), "single"));
        propertyDefinitions.add(newPropertiesDefinition("Référence Carte", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Enzyme de restriction", "restrictionEnzyme", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("AfIII","ApaLI","BamHI","BgIII","EcoRI","HindIII","KpnI","MIuI","Ncol","NdeI","NheI","NotI","PvuII","SpeI","XbaI","XhoI"), "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Tableau sélection enzyme","enzymeChooser",LevelService.getLevels(Level.CODE.Instrument), Image.class, false, "img"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Photo digestion","digestionForTracking",LevelService.getLevels(Level.CODE.Instrument), Image.class, false, "img"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Metrix","anlyseMetrics",LevelService.getLevels(Level.CODE.Instrument), Image.class, false, "img"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Statistiques Contigs","contigStatistics",LevelService.getLevels(Level.CODE.Instrument), Image.class, false, "img"));
        return propertyDefinitions;
	}


	private static List<PropertyDefinition> getHiseq2500Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = getHiseq2000Properties();		
	   propertyDefinitions.add(newPropertiesDefinition("Mode run","runMode"
	        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("RHS2500","RHS2500R"), "single"));
        return propertyDefinitions;
	}
	
	

	private static List<Instrument> getInstrumentMiSeq() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("MELISSE", "MELISSE", true, "/env/atelier/solexa_MELISSE", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("MIMOSA", "MIMOSA", true, "/env/atelier/solexa_MIMOSA", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("MISEQ1", "MISEQ1", false, "/env/atelier/solexa_MISEQ1", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("MISEQ1", "MISEQ1", true, "/env/atelier/solexa_MISEQ1/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );
		return instruments;
	}
	
	private static List<Instrument> getInstrumentNEXTSEQ500() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("NEXTSEQ1", "NEXTSEQ1", true, "/env/atelier/solexa_NEXTSEQ1/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );
		return instruments;
	}


	public static List<Instrument> getInstrumentHiseq2000() throws DAOException{
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("HISEQ1", "HISEQ1", true, "/env/export/cngstkprd003/v_igseq4/HISEQ1/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ2", "HISEQ2", true, "/env/export/cngstkprd003/v_igseq4/HISEQ2/", DescriptionFactory.getInstitutes(Institute.CODE.CNG/*,Institute.CODE.CNS*/)));
		instruments.add(createInstrument("HISEQ2", "HISEQ2", false, "/env/export/cngstkprd003/v_igseq4/HISEQ2/", DescriptionFactory.getInstitutes(Institute.CODE.CNS/*,Institute.CODE.CNS*/)));
		instruments.add(createInstrument("HISEQ3", "HISEQ3", true, "/env/export/cngstkprd003/v_igseq4/HISEQ3/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ4", "HISEQ4", true, "/env/export/cngstkprd003/v_igseq5/HISEQ4/", DescriptionFactory.getInstitutes(Institute.CODE.CNG/*,Institute.CODE.CNS*/)));
		instruments.add(createInstrument("HISEQ4", "HISEQ4", false, "/env/export/cngstkprd003/v_igseq5/HISEQ4/", DescriptionFactory.getInstitutes(Institute.CODE.CNS/*,Institute.CODE.CNS*/)));
		instruments.add(createInstrument("HISEQ5", "HISEQ5", true, "/env/export/cngstkprd003/v_igseq5/HISEQ5/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ6", "HISEQ6", true, "/env/export/cngstkprd003/v_igseq5/HISEQ6/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ7", "HISEQ7", true, "/env/export/cngstkprd003/v_igseq6/HISEQ7/", DescriptionFactory.getInstitutes(Institute.CODE.CNG/*,Institute.CODE.CNS*/)));
		instruments.add(createInstrument("HISEQ7", "HISEQ7", false, "/env/export/cngstkprd003/v_igseq6/HISEQ7/", DescriptionFactory.getInstitutes(Institute.CODE.CNS/*,Institute.CODE.CNS*/)));
		instruments.add(createInstrument("HISEQ8", "HISEQ8", true, "/env/export/cngstkprd003/v_igseq6/HISEQ8/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );
		instruments.add(createInstrument("AZOTE", "AZOTE", false, "/env/atelier/solexa_AZOTE", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("CARBONE", "CARBONE", true, "/env/atelier/solexa_CARBONE", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("CHROME", "CHROME", false, "/env/atelier/solexa_CHROME", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("MERCURE", "MERCURE", true, "/env/atelier/solexa_MERCURE", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("SOUFRE", "SOUFRE", true, "/env/atelier/solexa_SOUFRE", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add( createInstrument("PHOSPHORE", "PHOSPHORE", true, "/env/atelier/solexa_PHOSPHORE", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		return instruments;
	}
	

	
	private static List<Instrument> getInstrumentGAII() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("BISMUTH", "BISMUTH", false, "/env/atelier/solexa_BISMUTH", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("HELIUM", "HELIUM", false, "/env/atelier/solexa_HELIUM", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		return instruments;
	}

	
	public static List<Instrument> getInstrumentHiseq2500() throws DAOException{
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add( createInstrument("HISEQ9", "HISEQ9", true, "/env/export/cngstkprd003/v_igseq6/HISEQ9/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add( createInstrument("HISEQ9", "HISEQ9", false, "/env/export/cngstkprd003/v_igseq6/HISEQ9/", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		instruments.add( createInstrument("HISEQ10", "HISEQ10", true, "/env/export/cngstkprd003/v_igseq7/HISEQ10/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add( createInstrument("HISEQ10", "HISEQ10", false, "/env/export/cngstkprd003/v_igseq7/HISEQ10/", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		instruments.add( createInstrument("HISEQ11", "HISEQ11", true, "/env/export/cngstkprd003/v_igseq7/HISEQ11/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add( createInstrument("HISEQ11", "HISEQ11", false, "/env/export/cngstkprd003/v_igseq7/HISEQ11/", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));		
		instruments.add( createInstrument("FLUOR", "FLUOR", true, "/env/atelier/solexa_FLUOR", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		instruments.add( createInstrument("PLATINE", "PLATINE", true, "/env/atelier/solexa_PLATINE", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		return instruments;
	}
	
	public static List<Instrument> getInstrumentOpgen()throws DAOException{
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add( createInstrument("APOLLON", "APOLLON", true, "/env/atelier/opgen/APOLLON", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		return instruments;
	}
	
	public static List<Instrument> getInstrumentExtSolexa()throws DAOException{
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add( createInstrument("EXTGAIIX", "EXTGAIIX", true, "/env/atelier", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		instruments.add( createInstrument("EXTHISEQ", "EXTHISEQ", true, "/env/atelier", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		instruments.add( createInstrument("EXTMISEQ", "EXTMISEQ", true, "/env/atelier", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		return instruments;
	}
	
	private static List<PropertyDefinition> getCovarisProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("Frag_PE300","Frag_PE400","Frag_PE500","Frag_cDNA_Solexa"), "single"));
		return l;
	}
	
	private static List<PropertyDefinition> getSpriProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("300-600"), "single"));		
		return l;
	}
	
	private static List<PropertyDefinition> getThermocyclerProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("15","18"), "single"));		
		return l;
	}
	
	private static List<PropertyDefinition> getChipElectrophoresisProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Type puce", "chipType", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("DNA HS", "DNA 12000", "RNA"), "single"));		
		return l;
	}
	
	private static List<PropertyDefinition> getQuBitProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Kit", "kit", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("HS", "BR"), "single"));		
		return l;
	}
	
	private static List<PropertyDefinition> getQPCRProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Nb. Echantillon", "sampleNumber", LevelService.getLevels(Level.CODE.Instrument), Integer.class, true, "single"));		
		return l;
	}
	
	private static List<PropertyDefinition> getRocheLightCyclerProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		// actuellement non defini l.add(newPropertiesDefinition("Nb. Echantillon", "sampleNumber", LevelService.getLevels(Level.CODE.Instrument), Integer.class, true, "single"));		
		return l;
	}
	
	private static Instrument createInstrument(String code, String name, Boolean active, String path, List<Institute> institutes) {
		Instrument i = new Instrument();
		i.code = code;
		i.name = name;
		i.active=active;
		i.path=path;
		i.institutes=institutes;
		return i;
	}

	private static List<Instrument> getInstruments(Instrument...instruments) {
		List<Instrument> linstruments = new ArrayList<Instrument>(); 
		for (Instrument instrument : instruments) {
			linstruments.add(instrument); 
		}
		return linstruments; 
	}

	
	private static List<ContainerSupportCategory> getContainerSupportCategories(String[] codes) throws DAOException{		
		return DAOHelpers.getModelByCodes(ContainerSupportCategory.class,ContainerSupportCategory.find, codes);
	}


}
