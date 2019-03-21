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
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.Constants;
import services.description.common.LevelService;
import services.description.common.MeasureService;

public class InstrumentServiceGET extends AbstractInstrumentService{
	
	
	public void saveInstrumentCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<InstrumentCategory> l = new ArrayList<InstrumentCategory>();
		l.add(newInstrumentCategory("Covaris","covaris"));
//		l.add(newInstrumentCategory("Spri","spri"));
		l.add(newInstrumentCategory("Thermocycleur","thermocycler"));
		l.add(newInstrumentCategory("Séquenceur Pacific Biosciences","pacbio-sequencer"));
		l.add(newInstrumentCategory("Centrifugeuse","centrifuge"));

		
		l.add(newInstrumentCategory("Quantification par fluorométrie","fluorometer"));
		l.add(newInstrumentCategory("Appareil de qPCR","qPCR-system"));
		l.add(newInstrumentCategory("Electrophorèse sur puce","chip-electrophoresis"));
		
		l.add(newInstrumentCategory("Main","hand"));
		l.add(newInstrumentCategory("CBot","cbot"));
//		l.add(newInstrumentCategory("XP Workflow","xp-wf"));
		
		l.add(newInstrumentCategory("Séquenceur Illumina","illumina-sequencer"));
//		l.add(newInstrumentCategory("Cartographie Optique Opgen","opt-map-opgen"));
		l.add(newInstrumentCategory("Séquenceur Nanopore","nanopore-sequencer"));
		l.add(newInstrumentCategory("Extérieur","extseq"));
		
		l.add(newInstrumentCategory("Robot pipetage","liquid-handling-robot"));
		l.add(newInstrumentCategory("Appareil de sizing","sizing-system"));
				
		DAOHelpers.saveModels(InstrumentCategory.class, l, errors);
		
	}
	
	public void saveInstrumentUsedTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		
		List<InstrumentUsedType> l = new ArrayList<InstrumentUsedType>();
		
		//TODO : COVARIS  machine 
		
		l.add(newInstrumentUsedType("Covaris", "covaris-s2", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						//createInstrument("Covaris1", "Covaris1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET)), 
						createInstrument("COVARIS M220", "COVARIS", null, true, "/save/devcrgs/src/NGL_Feuille_route/COVARIS/", DescriptionFactory.getInstitutes(Constants.CODE.GET))) ,
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
//		l.add(newInstrumentUsedType("Spri", "spri", InstrumentCategory.find.findByCode("spri"), getSpriProperties(), 
//				getInstruments(
//						createInstrument("Spri1", "Spri1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET)), 
//						createInstrument("Spri2", "Spri2", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET)), 
//						createInstrument("Spri3", "Spri3", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET)) ), 
//				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
//				DescriptionFactory.getInstitutes(Constants.CODE.GET)));		
		
//		l.add(newInstrumentUsedType("Stratagene qPCR system", "stratagene-qPCR", InstrumentCategory.find.findByCode("qPCR-system"), getQPCRProperties(), 
//				getInstruments(
//						createInstrument("Stratagene1", "Stratagene1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET))),
//				getContainerSupportCategories(new String[]{"tube","96-well-plate"}), null, 
//				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
	
// TODO: cbot interne machine 
		
		l.add(newInstrumentUsedType("cBot-interne", "cBot-interne", InstrumentCategory.find.findByCode("cbot"), getCBotInterneProperties(), 
				getInstruments(
//						createInstrument("cBot-hiseq2500","cBot HiSeq2500",  null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET)),
						createInstrument("cBot-Miseq_1","cBot Miseq_1",  null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET)),
						createInstrument("cBot-Miseq_2","cBot Miseq_2",  null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET)),
						createInstrument("cBot-Miseq_4","cBot Miseq_4",  null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET)),
						createInstrument("cBot-Miseq_5","cBot Miseq_5",  null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET))
						),
				getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}), getContainerSupportCategories(new String[]{"flowcell-1"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));

		l.add(newInstrumentUsedType("cBot-interne-NovaSeq", "cBot-interne-novaseq", InstrumentCategory.find.findByCode("cbot"), getCBotInterneNSProperties(), 
				getInstruments(
						createInstrument("cBot-NovaSeq","cBot NovaSeq",  null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET))
						),
				getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}), getContainerSupportCategories(new String[]{"flowcell-2-s1","flowcell-2-s2","flowcell-4-s4"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
// TODO:  Added cbot machine
		
		l.add(newInstrumentUsedType("cBot", "cBot", InstrumentCategory.find.findByCode("cbot"), getCBotProperties(), 
				getInstruments(
						createInstrument("cBot1", "cBot-1", null, true, "/save/devcrgs/src/NGL_Feuille_route/cBot-1", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
				getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}), getContainerSupportCategories(new String[]{"flowcell-8"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
		//TODO : a valider  TECAN evo machine 
		
		l.add(newInstrumentUsedType("TECAN evo 150", "tecan-evo-150", InstrumentCategory.find.findByCode("liquid-handling-robot"), null, 
				getInstruments(
						createInstrument("EVO150","EVO 150 n° 1 Post-PCR", null, true,  "/save/devcrgs/src/NGL_Feuille_route/EVO150", DescriptionFactory.getInstitutes(Constants.CODE.GET)),
						createInstrument("EVO150_2","Evo 150 n° 2 Post-PCR", null, true,  "/save/devcrgs/src/NGL_Feuille_route/EVO150_2", DescriptionFactory.getInstitutes(Constants.CODE.GET)),
						createInstrument("EVO150_3","EVO 150 n° 3 Pré PCR", null, true,  "/save/devcrgs/src/NGL_Feuille_route/EVO150_3", DescriptionFactory.getInstitutes(Constants.CODE.GET))
						),
						getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}),getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}), 
						
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));

		l.add(newInstrumentUsedType("TECAN evo 200", "tecan-evo-200", InstrumentCategory.find.findByCode("liquid-handling-robot"), null, 
				getInstruments(
						createInstrument("EVO","TECAN200 EVO",  null, true, "/save/devcrgs/src/NGL_Feuille_route/EVO", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
						getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}),getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
		
//TODO : verify getContainerSupportCategories parameters
		
		l.add(newInstrumentUsedType("Blue pippin", "blue-pippin", InstrumentCategory.find.findByCode("sizing-system"), null, 
				getInstruments(
						createInstrument("BluePippin1", "BluePippin1", null, true, "/save/devcrgs/src/NGL_Feuille_route/BluePippin1", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
						getContainerSupportCategories(new String[]{"96-well-plate"}),null, 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		

		l.add(newInstrumentUsedType("Main", "hand", InstrumentCategory.find.findByCode("hand"), null, 
				getInstruments(
						createInstrument("hand", "Main", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET)) ),
						getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}),getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));

		l.add(newInstrumentUsedType("qubit", "QuBit", InstrumentCategory.find.findByCode("fluorometer"), getQuBitProperties(), 
				getInstruments(
						createInstrument("quBit1", "QuBit1", null, true, "/save/devcrgs/src/NGL_Feuille_route/QuBit1", DescriptionFactory.getInstitutes(Constants.CODE.GET))), 
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));

	
//TODO: MISEQ machine 
		
		l.add(newInstrumentUsedType("MiSeq", "MISEQ", InstrumentCategory.find.findByCode("illumina-sequencer"), getMiseqProperties(), 
				getInstruments(
				createInstrument("MISEQ", "MiSeq1 M00185", null, true,"/save/devcrgs/src/NGL_Feuille_route/MISEQ", DescriptionFactory.getInstitutes(Constants.CODE.GET)),
				createInstrument("MISEQ_2","MiSeq2 M02944", null, true,"/save/devcrgs/src/NGL_Feuille_route/MISEQ_2", DescriptionFactory.getInstitutes(Constants.CODE.GET)),
				createInstrument("MISEQ_4","MiSeq4 M01945R", null, true,"/save/devcrgs/src/NGL_Feuille_route/MISEQ_4", DescriptionFactory.getInstitutes(Constants.CODE.GET)),
				createInstrument("MISEQ_5","MiSeq5 M01764", null, true,"/save/devcrgs/src/NGL_Feuille_route/MISEQ_5", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
				getContainerSupportCategories(new String[]{"flowcell-1"}), null, 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
//TODO: HISEQ2500 machine 
//		
//		l.add(newInstrumentUsedType("HiSeq 2500", "HISEQ2500", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq2500Properties(), 
//				getInstruments(
//				 createInstrument("HiSeq2500", "HISEQ2000_2", null, true,"/save/devcrgs/src/NGL_Feuille_route/HISEQ2000_2/", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
//				getContainerSupportCategories(new String[]{"flowcell-8","flowcell-2"}), null, 
//				DescriptionFactory.getInstitutes(Constants.CODE.GET)));


//		l.add(newInstrumentUsedType("HISEQ4000", "HISEQ4000", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq4000Properties(), 
//				getInstrumentHiseq4000(),
//				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
//				DescriptionFactory.getInstitutes(Constants.CODE.GET)));

//TODO: HISEQ3000
		
		l.add(newInstrumentUsedType("HiSeq 3000", "HISEQ3000", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq3000Properties(), 
				getInstruments(
				 createInstrument("HISEQ3000","HiSeq3000 HWI-J00115",  null, true,"/save/devcrgs/src/NGL_Feuille_route/HISEQ3000", DescriptionFactory.getInstitutes(Constants.CODE.GET)),
				 createInstrument("HISEQ3000_2","HiSeq3000 HWI-J00173",  null, true,"/save/devcrgs/src/NGL_Feuille_route/HISEQ3000_2", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));

		/* NovaSeq*/
        l.add(newInstrumentUsedType("NovaSeq 6000", "NOVASEQ6000", InstrumentCategory.find.findByCode("illumina-sequencer"), getNovaSeqProperties(), 
                getInstruments(
                 createInstrument("NOVASEQ6000","NovaSeq6000_A00318",  null, true,"/save/devcrgs/src/NGL_Feuille_route/NOVASEQ6000", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
                 getContainerSupportCategories(new String[]{"flowcell-2-s1","flowcell-2-s2","flowcell-4-s4"}), null , 
                DescriptionFactory.getInstitutes(Constants.CODE.GET)));

		/* NovaSeq*/
//        l.add(newInstrumentUsedType("XP Workflow", "XPWORKFLOW ", InstrumentCategory.find.findByCode("xp-wf"), getNovaSeqProperties(), 
//                getInstruments(
//                 createInstrument("XPWORKFLOW","XP Workflow",  null, true,"/save/devcrgs/src/NGL_Feuille_route/XPWF", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
//                getContainerSupportCategories(new String[]{"tube","96-well-plate", "384-well-plate"}), getContainerSupportCategories(new String[]{"flowcell-S1","flowcell-S2","flowcell-S4"}), 
//                DescriptionFactory.getInstitutes(Constants.CODE.GET)));
        
//TODO: Fragment Analyser machine 
	
		l.add(newInstrumentUsedType("Agilent 2100 bioanalyzer", "agilent-2100-bioanalyzer", InstrumentCategory.find.findByCode("chip-electrophoresis"), getChipElectrophoresisProperties(), 
				getInstruments(
						createInstrument("BIOANALYZER_1","Bioanalyzer plage",  null, true, "/save/devcrgs/src/NGL_Feuille_route/BIOANALYZER_1", DescriptionFactory.getInstitutes(Constants.CODE.GET ))),
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));

		// TODO: Fragment Analyser machine 
		
		l.add(newInstrumentUsedType("Fragment Analyzer", "Fragment-Analyzer", InstrumentCategory.find.findByCode("chip-electrophoresis"), getChipElectrophoresisProperties(), 
				getInstruments( 
						createInstrument("FRAGANALYZER", "Fragment Analyzer",  null, true, "/save/devcrgs/src/NGL_Feuille_route/FRAGANALYZER", DescriptionFactory.getInstitutes(Constants.CODE.GET ))),
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
	//TODO : Added Thermocycleur Machine
		
		l.add(newInstrumentUsedType("Thermocycleur", "thermocycler", InstrumentCategory.find.findByCode("thermocycler"), getThermocyclerProperties(), 
				getInstruments(
						createInstrument("MasterCycler","Master Cycler",  null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET))
						), 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
//		l.add(newInstrumentUsedType("LabChip GX", "labChipGX", InstrumentCategory.find.findByCode("chip-electrophoresis"), null, 
//				getInstruments(
//						createInstrument("labChip1", "LabChip1", null, true, null, DescriptionFactory.getInstitutes(Constants.CODE.GET)) ) ,
//				getContainerSupportCategories(new String[]{"sheet-384","96-well-plate"}),null, 
//				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		l.add(newInstrumentUsedType("QuantStudio 6", "QS6", InstrumentCategory.find.findByCode("qPCR-system"), null, 
				getInstruments(createInstrument("QS6", "QS6", null, true, "/save/devcrgs/src/NGL_Feuille_route/QS6", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
				getContainerSupportCategories(new String[]{"96-well-plate","384-well-plate"}), null, 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));	
		l.add(newInstrumentUsedType("ABI 7900HT", "ABI7900HT", InstrumentCategory.find.findByCode("qPCR-system"), null, 
				getInstruments(
						createInstrument("ABI7900HT", "ABI7900HT", null, true, "/save/devcrgs/src/NGL_Feuille_route/ABI7900HT", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
				getContainerSupportCategories(new String[]{"96-well-plate","384-well-plate"}), null, 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		l.add(newInstrumentUsedType("PacBio RSII", "PacBioRSII", InstrumentCategory.find.findByCode("qPCR-system"), null, 
				getInstruments(
						createInstrument("RSII", "PacBioRSII", null, true, "/save/devcrgs/src/NGL_Feuille_route/RSII", DescriptionFactory.getInstitutes(Constants.CODE.GET))),
				getContainerSupportCategories(new String[]{"smrtcell-150k"}), null, 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));

//		l.add(newInstrumentUsedType("Eppendorf MiniSpin plus", "eppendorf-mini-spin-plus", InstrumentCategory.find.findByCode("centrifuge"), getNanoporeFragmentationProperties(),  getInstrumentEppendorfMiniSpinPlus()
//				,getContainerSupportCategories(new String[]{"tube"}), getContainerSupportCategories(new String[]{"tube"}), DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
		l.add(newInstrumentUsedType("GridION", "GridION", InstrumentCategory.find.findByCode("nanopore-sequencer"), getGridIonProperties(),
				getInstruments(
						createInstrument("GRIDION_3", "GridION GXB01176", null, true, "/save/devcrgs/src/NGL_Feuille_route/GridION", DescriptionFactory.getInstitutes(Constants.CODE.GET))), 
				getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}), getContainerSupportCategories(new String[]{"flowcell_R9-4-1_gd_RevD","flowcell_R9-4-1_gd","flowcell_R9-5_gd"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
		l.add(newInstrumentUsedType("PromethION", "PromethION", InstrumentCategory.find.findByCode("nanopore-sequencer"), getPromethIonProperties(),
				getInstruments(
						createInstrument("PROMETHION", "PromethION PCT0078", null, true, "/save/devcrgs/src/NGL_Feuille_route/PromethION", DescriptionFactory.getInstitutes(Constants.CODE.GET))), 
				getContainerSupportCategories(new String[]{"tube","96-well-plate","384-well-plate"}), getContainerSupportCategories(new String[]{"flowcell_R9-4-1_pt_RevD"}), 
				DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
//		l.add(newInstrumentUsedType("Mk1", "mk1", InstrumentCategory.find.findByCode("nanopore-sequencer"), getNanoporeDepotProperties(),getInstrumentMKI() 
//				,getContainerSupportCategories(new String[]{"tube"}), getContainerSupportCategories(new String[]{"flowcell-1"}), DescriptionFactory.getInstitutes(Constants.CODE.GET)));
		
		DAOHelpers.saveModels(InstrumentUsedType.class, l, errors);
	}


	private static List<PropertyDefinition> getHiseq3000Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",300));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",700));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
//        propertyDefinitions.add(newPropertiesDefinition("Piste contrôle","controlLane", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValuesWithDefault("Pas de piste contrôle (auto-calibrage)","Pas de piste contrôle (auto-calibrage)","1",
//        		"2","3","4","5","6","7","8"),"Pas de piste contrôle (auto-calibrage)","single",100));
        return propertyDefinitions;
	}

	private static List<PropertyDefinition> getNovaSeqProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Workflow", "workflow", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("Standard","XP"), null, "single",200));
        propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",300));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",700));
//        propertyDefinitions.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single",800, "n/a"));
        propertyDefinitions.add(newPropertiesDefinition("Position", "position", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B","n/a"), "n/a", "single",900));
        return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getNanoporeFragmentationProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument),String.class, true,
        		DescriptionFactory.newValues("G-TUBE"), "G-TUBE", null, null, null, "single", 1));
        propertyDefinitions.add(newPropertiesDefinition("Vitesse", "speed", LevelService.getLevels(Level.CODE.Instrument),String.class, false,
        		null, "8000", MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SPEED),MeasureUnit.find.findByCode( "rpm"),MeasureUnit.find.findByCode( "rpm"), "single", 2));
        // unite s
        propertyDefinitions.add(newPropertiesDefinition("Durée", "duration", LevelService.getLevels(Level.CODE.Instrument),String.class, false, 
        		null, "60",MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_TIME),MeasureUnit.find.findByCode( "s"),MeasureUnit.find.findByCode( "s"), "single", 3));
		return propertyDefinitions;
	}

	
	private static List<PropertyDefinition> getGridIonProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
//		propertyDefinitions = getNanoporeProperties();
		
		propertyDefinitions.add(newPropertiesDefinition("Code Flowcell valide", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single",100));
        propertyDefinitions.add(newPropertiesDefinition("Emplacement FC","empl_FC", LevelService.getLevels(Level.CODE.Instrument), String.class, false, 
        		DescriptionFactory.newValues("X1","X2","X3","X4","X5"), null, "single",101));
        
		return propertyDefinitions;
	}
	
	// à décommenter
	private static List<PropertyDefinition> getPromethIonProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
//		propertyDefinitions = getNanoporeProperties();
		
		propertyDefinitions.add(newPropertiesDefinition("Code Flowcell valide", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single",100));
        propertyDefinitions.add(newPropertiesDefinition("Emplacement FC","empl_FC", LevelService.getLevels(Level.CODE.Instrument), String.class, false, 
        		getPromethionPosition(),
        		null, "single",101));
        
		return propertyDefinitions;
	}
	


//	private static List<PropertyDefinition> getNanoporeProperties() throws DAOException {
//		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
//        propertyDefinitions.add(newPropertiesDefinition("Code Flowcell valide", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single",100));
////        propertyDefinitions.add(newPropertiesDefinition("Emplacement FC","empl_FC", LevelService.getLevels(Level.CODE.Instrument), String.class, false,"single",101));
//        
//        propertyDefinitions.add(newPropertiesDefinition("Nb de pores actifs à la réception","nb_pores_actifs_reception", LevelService.getLevels(Level.CODE.Instrument), Integer.class, false,"single",102));
//        propertyDefinitions.add(newPropertiesDefinition("Nb de pores actifs au dépôt","nb_pores_actifs", LevelService.getLevels(Level.CODE.Instrument), Integer.class, false,"single",103));
//        propertyDefinitions.add(newPropertiesDefinition("Nb pores G1","pores_G1", LevelService.getLevels(Level.CODE.Instrument), Integer.class, false,"single",104));
//        propertyDefinitions.add(newPropertiesDefinition("Nb pores G2","pores_G2", LevelService.getLevels(Level.CODE.Instrument), Integer.class, false,"single",105));
//        propertyDefinitions.add(newPropertiesDefinition("Nb pores G3","pores_G3", LevelService.getLevels(Level.CODE.Instrument), Integer.class, false,"single",106));
//        propertyDefinitions.add(newPropertiesDefinition("Nb pores G4","pores_G4", LevelService.getLevels(Level.CODE.Instrument), Integer.class, false,"single",107));
//        
//		return propertyDefinitions;
//	}
	// piqué au CNS
//	private static List<PropertyDefinition> getPromethIONProperties() throws DAOException {
//		List<PropertyDefinition> propertyDefinitions = new ArrayList<>();
//        
//		
//		propertyDefinitions.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.ContainerOut),String.class, true, null, 
//	        		null, "single", 48, true, null, null));
//	       
//        propertyDefinitions.add(newPropertiesDefinition("Version Flowcell", "flowcellChemistry", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content),String.class, true, null, 
//        		null, "single", 49, true, "R9.4-spot-on", null));
//        
//        propertyDefinitions.add(newPropertiesDefinition("Position", "position", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, null, 
//        		getPromethionPosition(), "single", 50, true, null, null));
//        
//		return propertyDefinitions;
//	}
	
	// piqué au CNS
	private static List<Value> getPromethionPosition() {
		List<Value> values = new ArrayList<>();
		values.add(DescriptionFactory.newValue("PH_p-101_0","pl1_A1-D1"));  
		values.add(DescriptionFactory.newValue("PH_p-101_2","pl1_A2-D2"));  
		values.add(DescriptionFactory.newValue("PH_p-105_0","pl1_A3-D3"));  
		values.add(DescriptionFactory.newValue("PH_p-105_2","pl1_A4-D4"));  
		values.add(DescriptionFactory.newValue("PH_p-109_0","pl1_A5-D5"));  
		values.add(DescriptionFactory.newValue("PH_p-109_2","pl1_A6-D6")); 
		values.add(DescriptionFactory.newValue("PH_p-101_1","pl1_E1-H1")); 
		values.add(DescriptionFactory.newValue("PH_p-101_3","pl1_E2-H2")); 
		values.add(DescriptionFactory.newValue("PH_p-105_1","pl1_E3-H3")); 
		values.add(DescriptionFactory.newValue("PH_p-105_3","pl1_E4-H4")); 
		values.add(DescriptionFactory.newValue("PH_p-109_1","pl1_E5-H5")); 
		values.add(DescriptionFactory.newValue("PH_p-109_3","pl1_E6-H6")); 
		values.add(DescriptionFactory.newValue("PH_p-102_0","pl1_A7-D7")); 
		values.add(DescriptionFactory.newValue("PH_p-102_2","pl1_A8-D8")); 
		values.add(DescriptionFactory.newValue("PH_p-106_0","pl1_A9-D9")); 
		values.add(DescriptionFactory.newValue("PH_p-106_2","pl1_A10-D10")); 
		values.add(DescriptionFactory.newValue("PH_p-110_0","pl1_A11-D11")); 
		values.add(DescriptionFactory.newValue("PH_p-110_2","pl1_A12-D12")); 
		values.add(DescriptionFactory.newValue("PH_p-102_1","pl1_E7-H7")); 
		values.add(DescriptionFactory.newValue("PH_p-102_3","pl1_E8-H8")); 
		values.add(DescriptionFactory.newValue("PH_p-106_1","pl1_E9-H9")); 
		values.add(DescriptionFactory.newValue("PH_p-106_3","pl1_E10-H10")); 
		values.add(DescriptionFactory.newValue("PH_p-110_1","pl1_E11-H11")); 
		values.add(DescriptionFactory.newValue("PH_p-110_3","pl1_E12-H12")); 
		values.add(DescriptionFactory.newValue("PH_p-103_0","pl2_A1-D1")); 
		values.add(DescriptionFactory.newValue("PH_p-103_2","pl2_A2-D2")); 
		values.add(DescriptionFactory.newValue("PH_p-107_0","pl2_A3-D3")); 
		values.add(DescriptionFactory.newValue("PH_p-107_2","pl2_A4-D4")); 
		values.add(DescriptionFactory.newValue("PH_p-111_0","pl2_A5-D5")); 
		values.add(DescriptionFactory.newValue("PH_p-111_2","pl2_A6-D6")); 
		values.add(DescriptionFactory.newValue("PH_p-103_1","pl2_E1-H1")); 
		values.add(DescriptionFactory.newValue("PH_p-103_3","pl2_E2-H2")); 
		values.add(DescriptionFactory.newValue("PH_p-107_1","pl2_E3-H3")); 
		values.add(DescriptionFactory.newValue("PH_p-107_3","pl2_E4-H4")); 
		values.add(DescriptionFactory.newValue("PH_p-111_1","pl2_E5-H5")); 
		values.add(DescriptionFactory.newValue("PH_p-111_3","pl2_E6-H6")); 
		values.add(DescriptionFactory.newValue("PH_p-104_0","pl2_A7-D7")); 
		values.add(DescriptionFactory.newValue("PH_p-104_2","pl2_A8-D8")); 
		values.add(DescriptionFactory.newValue("PH_p-108_0","pl2_A9-D9")); 
		values.add(DescriptionFactory.newValue("PH_p-108_2","pl2_A10-D10")); 
		values.add(DescriptionFactory.newValue("PH_p-112_0","pl2_A11-D11")); 
		values.add(DescriptionFactory.newValue("PH_p-112_2","pl2_A12-D12")); 
		values.add(DescriptionFactory.newValue("PH_p-104_1","pl2_E7-H7")); 
		values.add(DescriptionFactory.newValue("PH_p-104_3","pl2_E8-H8")); 
		values.add(DescriptionFactory.newValue("PH_p-108_1","pl2_E9-H9")); 
		values.add(DescriptionFactory.newValue("PH_p-108_3","pl2_E10-H10")); 
		values.add(DescriptionFactory.newValue("PH_p-112_1","pl2_E11-H11")); 
		values.add(DescriptionFactory.newValue("PH_p-112_3","pl2_E12-H12")); 	
		return values;
	}


	private static List<PropertyDefinition> getCBotProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Type lectures","sequencingProgramType"
        		, LevelService.getLevels(Level.CODE.Instrument,Level.CODE.ContainerSupport),String.class, true,DescriptionFactory.newValues("SR","PE"),"single"));
        propertyDefinitions.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single",100, "n/a"));
        return propertyDefinitions;
	}

	
	private static List<PropertyDefinition> getCBotInterneProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Type lectures","sequencingProgramType"
        		, LevelService.getLevels(Level.CODE.Instrument,Level.CODE.ContainerSupport),String.class, true,DescriptionFactory.newValues("SR","PE"),"single"));
        propertyDefinitions.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single",100,"n/a"));
        return propertyDefinitions;
	}
	

	private static List<PropertyDefinition> getCBotInterneNSProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Workflow", "workflow", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("Standard","XP"), null, "single",100));
        propertyDefinitions.add(newPropertiesDefinition("Type lectures","sequencingProgramType"
        		, LevelService.getLevels(Level.CODE.Instrument,Level.CODE.ContainerSupport),String.class, true,DescriptionFactory.newValues("SR","PE"),"single"));
        propertyDefinitions.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single",300,"n/a"));
        propertyDefinitions.add(newPropertiesDefinition("Position", "position", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B","n/a"), "n/a", "single",400));
        
        return propertyDefinitions;
	}
	
//	private static List<PropertyDefinition> getHiseq2000Properties() throws DAOException {
//		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
//        propertyDefinitions.add(newPropertiesDefinition("Position","position"
//        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single",200));
//        propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",300));
//        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
//        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
//        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",700));
//        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
////        propertyDefinitions.add(newPropertiesDefinition("Piste contrôle","controlLane", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValuesWithDefault("Pas de piste contrôle (auto-calibrage)","Pas de piste contrôle (auto-calibrage)","1",
////        		"2","3","4","5","6","7","8"),"Pas de piste contrôle (auto-calibrage)","single",100));
//        return propertyDefinitions;
//	}
	private static List<PropertyDefinition> getMiseqProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Nom cassette Miseq", "miseqReagentCassette",LevelService.getLevels(Level.CODE.Instrument),String.class,true,"single",100));
        propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",200));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
        return propertyDefinitions;
	}
	

//	private static List<PropertyDefinition> getHiseq2500Properties() throws DAOException {
//		List<PropertyDefinition> propertyDefinitions = getHiseq2000Properties();		
//	   propertyDefinitions.add(0, newPropertiesDefinition("Mode run","runMode"
//	        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("normal","rapide"), "single",50));
//        return propertyDefinitions;
//	}
	

//	private static List<Instrument> getInstrumentEppendorfMiniSpinPlus() throws DAOException {
//		List<Instrument> instruments=new ArrayList<Instrument>();
//		instruments.add(createInstrument("MiniSpin plus 1", "miniSpinPlus1", null, true, "path", DescriptionFactory.getInstitutes(Constants.CODE.GET)));
//		return instruments;
//	}

	
	

	private static List<PropertyDefinition> getCovarisProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("Frag_PE300","Frag_PE400","Frag_PE500","Frag_cDNA_Solexa"), "single"));
		return l;
	}
	
//	private static List<PropertyDefinition> getSpriProperties() throws DAOException {
//		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
//		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("300-600"), "single"));		
//		return l;
//	}
	
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
		
	

}
