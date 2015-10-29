package services.description.instrument;

import static services.description.DescriptionFactory.newInstrumentCategory;
import static services.description.DescriptionFactory.newInstrumentUsedType;
import static services.description.DescriptionFactory.newPropertiesDefinition;
import static services.description.DescriptionFactory.newValues;

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
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;

public class InstrumentServiceCNG extends AbstractInstrumentService{
	
	
	public void saveInstrumentCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<InstrumentCategory> l = new ArrayList<InstrumentCategory>();
		l.add(newInstrumentCategory("Covaris","covaris"));
		l.add(newInstrumentCategory("Spri","spri"));
		l.add(newInstrumentCategory("Thermocycleur","thermocycler"));
		l.add(newInstrumentCategory("Centrifugeuse","centrifuge"));

		
		l.add(newInstrumentCategory("Quantification par fluorométrie","fluorometer"));
		l.add(newInstrumentCategory("Appareil de qPCR","qPCR-system"));
		l.add(newInstrumentCategory("Electrophorèse sur puce","chip-electrophoresis"));
		
		l.add(newInstrumentCategory("Main","hand"));
		l.add(newInstrumentCategory("CBot","cbot"));
		
		l.add(newInstrumentCategory("Séquenceur Illumina","illumina-sequencer"));
		l.add(newInstrumentCategory("Cartographie Optique Opgen","opt-map-opgen"));
		l.add(newInstrumentCategory("Nanopore","nanopore"));
		l.add(newInstrumentCategory("Extérieur","extseq"));
		
		l.add(newInstrumentCategory("Robot pipetage","liquid-handling-robot"));
		l.add(newInstrumentCategory("Appareil de sizing","sizing-system"));
				
		DAOHelpers.saveModels(InstrumentCategory.class, l, errors);
		
	}
	
	public void saveInstrumentUsedTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		
		List<InstrumentUsedType> l = new ArrayList<InstrumentUsedType>();
		

		l.add(newInstrumentUsedType("Main", "hand", InstrumentCategory.find.findByCode("hand"), null, 
				getInstruments(
						createInstrument("hand", "Main", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)) ),
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("QuBit", "qubit", InstrumentCategory.find.findByCode("fluorometer"), getQuBitProperties(), 
				getInstruments(
						createInstrument("quBit1", "QuBit1", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG))
						), 
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG))); //ok
		
	
		l.add(newInstrumentUsedType("MISEQ", "MISEQ", InstrumentCategory.find.findByCode("illumina-sequencer"), getMiseqProperties(), 
				getInstrumentMiSeq(),
				getContainerSupportCategories(new String[]{"flowcell-1"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("HISEQ2000", "HISEQ2000", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq2000Properties(), 
				getInstrumentHiseq2000(),
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("HISEQ2500", "HISEQ2500", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq2500Properties(), 
				getInstrumentHiseq2500(),
				getContainerSupportCategories(new String[]{"flowcell-8","flowcell-2"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("NEXTSEQ500", "NEXTSEQ500", InstrumentCategory.find.findByCode("illumina-sequencer"), getNextseq500Properties(), 
				getInstrumentNextseq500(),
				getContainerSupportCategories(new String[]{"flowcell-4"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("HISEQ4000", "HISEQ4000", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseq4000Properties(), 
				getInstrumentHiseq4000(),
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("HISEQX", "HISEQX", InstrumentCategory.find.findByCode("illumina-sequencer"), getHiseqXProperties(), 
				getInstrumentHiseqX(),
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("Agilent 2100 bioanalyzer", "agilent-2100-bioanalyzer", InstrumentCategory.find.findByCode("chip-electrophoresis"), getChipElectrophoresisProperties(), 
				getInstruments(
						createInstrument("bioAnalyzer1", "BioAnalyzer1", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG )), 
						createInstrument("bioAnalyzer2", "BioAnalyzer2", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)) ), 
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("Thermocycleur", "thermocycler", InstrumentCategory.find.findByCode("thermocycler"), getThermocyclerProperties(), 
				getInstruments(
						createInstrument("thermo1", "Thermo1", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)), 
						createInstrument("thermo2", "Thermo2", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)), 
						createInstrument("thermo3", "Thermo3", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("thermo4", "Thermo4", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)) 
						), 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("cBot", "cBot", InstrumentCategory.find.findByCode("cbot"), getCBotProperties(), 
				getInstruments(
						createInstrument("cBot1", "cBot1", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot2", "cBot2", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot3", "cBot3", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot4", "cBot4", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}), getContainerSupportCategories(new String[]{"flowcell-8","flowcell-2"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newInstrumentUsedType("Covaris E210", "covaris-e210", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("cov1", "Cov1", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)) 
						) , 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		
		l.add(newInstrumentUsedType("LabChip GX", "labChipGX", InstrumentCategory.find.findByCode("chip-electrophoresis"), null, 
				getInstruments(
						createInstrument("labGX", "Lab_GX", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG))  ) ,
				getContainerSupportCategories(new String[]{"sheet-384","sheet-96"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		
		
		l.add(newInstrumentUsedType("Roche Lightcycler qPCR system", "rocheLightCycler-qPCR", InstrumentCategory.find.findByCode("qPCR-system"), getQPCRProperties(), 
				getInstruments(
						createInstrument("lightCycler1", "LightCycler1", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("lightCycler2", "LightCycler2", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG))
						),
				getContainerSupportCategories(new String[]{"tube","sheet-96"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		

		
		l.add(newInstrumentUsedType("Covaris LE220", "covaris-le220", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("cov2", "Cov2", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)) ) , 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG))); //ok
		
		
		// FDS 11-03-2015 =>NGL-356: manquait la flowcell-4 pour NextSeq...
		l.add(newInstrumentUsedType("cBot-onboard", "cBot-onboard", InstrumentCategory.find.findByCode("cbot"), getCBotInterneProperties(), 
				getInstruments(
						createInstrument("cBot-Hi9", "cBot-interne-Hi9", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot-Hi10", "cBot-interne-Hi10", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot-Hi11", "cBot-interne-Hi11", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot-Miseq1", "cBot-interne-Miseq1", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("cBot-NextSeq1", "cBot-interne-Nextseq1", null, true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNG))), 
				getContainerSupportCategories(new String[]{"tube"}), getContainerSupportCategories(new String[]{"flowcell-2","flowcell-1","flowcell-4" }), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));

		DAOHelpers.saveModels(InstrumentUsedType.class, l, errors);
	}


	private static List<PropertyDefinition> getCBotProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Type lectures","sequencingProgramType"
        		, LevelService.getLevels(Level.CODE.Instrument,Level.CODE.ContainerSupport),String.class, true,DescriptionFactory.newValues("SR","PE"),"single"));
      //  propertyDefinitions.add(newPropertiesDefinition("Type flowcell","flowcellType"
       // 		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("Paired End FC Hiseq-v3","Single FC Hiseq-v3","Rapid FC PE HS 2500-v1","Rapid FC SR HS 2500-v1"),"single"));
        propertyDefinitions.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Piste contrôle","controlLane", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValuesWithDefault("Pas de piste contrôle (auto-calibrage)","Pas de piste contrôle (auto-calibrage)","1",
        		"2","3","4","5","6","7","8"),"Pas de piste contrôle (auto-calibrage)","single"));
        return propertyDefinitions;
	}

	
	private static List<PropertyDefinition> getCBotInterneProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Type lectures","sequencingProgramType"
        		, LevelService.getLevels(Level.CODE.Instrument,Level.CODE.ContainerSupport),String.class, true,DescriptionFactory.newValues("SR","PE"),"single"));
     //   propertyDefinitions.add(newPropertiesDefinition("Type flowcell","flowcellType"
        //		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("Rapid FC PE HS 2500-v1","Rapid FC SR HS 2500-v1",
        	//			"FC Miseq-v2","FC Miseq-v3"),"single"));
        propertyDefinitions.add(newPropertiesDefinition("Code Flowcell", "containerSupportCode", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Piste contrôle","controlLane", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValuesWithDefault("Pas de piste contrôle (auto-calibrage)","Pas de piste contrôle (auto-calibrage)","1",
        		"2"),"Pas de piste contrôle (auto-calibrage)","single"));
        return propertyDefinitions;
	}

	private static List<PropertyDefinition> getHiseq2000Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Position","position"
        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single",200));
        propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",300));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",700));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
        propertyDefinitions.add(newPropertiesDefinition("Piste contrôle","controlLane", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValuesWithDefault("Pas de piste contrôle (auto-calibrage)","Pas de piste contrôle (auto-calibrage)","1",
        		"2","3","4","5","6","7","8"),"Pas de piste contrôle (auto-calibrage)","single",100));
        return propertyDefinitions;
	}

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
	
	private static List<PropertyDefinition> getNextseq500Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",100));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",200));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
        return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getHiseq2500Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = getHiseq2000Properties();		
	   propertyDefinitions.add(0, newPropertiesDefinition("Mode run","runMode"
	        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("high-throughput","rapid run"), "single",50));
        return propertyDefinitions;
	}
	
	// FDS ajout static 
	private static List<PropertyDefinition> getHiseq4000Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Position","position"
		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single",100));
		
		propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",200));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
		
		return propertyDefinitions;
	}
	
	// FDS ajout static
	private static List<PropertyDefinition> getHiseqXProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Position","position"
		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single",100));
		
		propertyDefinitions.add(newPropertiesDefinition("Type lectures", "sequencingProgramType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single",200));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",300));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",400));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",600));
		propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single",500));
		
		return propertyDefinitions;
	}
	
	private static List<Instrument> getInstrumentMiSeq() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("MISEQ1", "MISEQ1", null, true, "/env/atelier/solexa_MISEQ1/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );
		instruments.add(createInstrument("MISEQ2", "MISEQ2", null, false, "/env/atelier/solexa_MISEQ2/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );
		return instruments;
	}
	
	private static List<Instrument> getInstrumentNextseq500() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("NEXTSEQ1", "NEXTSEQ1", null, true, "/env/atelier/solexa_NEXTSEQ1/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );
		return instruments;
	}

	/* FDS 28/10/2015 activated=true + ajout / final  + ajout static!!*/
	private static List<Instrument> getInstrumentHiseq4000() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("FALBALA", "FALBALA", null, true, "/env/ig/atelier/illumina_FALBALA/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );		
		return instruments;
	}
	
	/* FDS 28/10/2015 activated=true + ajout / final + ajout static!!*/
	private static List<Instrument> getInstrumentHiseqX() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("ASTERIX",   "ASTERIX",    null, true, "/env/ig/atelier/illumina_ASTERIX/",    DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );	
		instruments.add(createInstrument("DIAGNOSTIX","DIAGNOSTIX", null, true, "/env/ig/atelier/illumina_DIAGNOSTIX/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );	
		instruments.add(createInstrument("IDEFIX",    "IDEFIX",     null, true, "/env/ig/atelier/illumina_IDEFIX/",     DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );	
		instruments.add(createInstrument("OBELIX",    "OBELIX",     null, true, "/env/ig/atelier/illumina_OBELIX/",     DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );	
		instruments.add(createInstrument("PANORAMIX", "PANORAMIX",  null, true, "/env/ig/atelier/illumina_PANORAMIX/",  DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );	
		
		return instruments;
	}

	public static List<Instrument> getInstrumentHiseq2000() throws DAOException{
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("HISEQ1", "HISEQ1", null, true, "/env/atelier/solexa_HISEQ1/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ2", "HISEQ2", null, true, "/env/atelier/solexa_HISEQ2/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ3", "HISEQ3", null, true, "/env/atelier/solexa_HISEQ3/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ4", "HISEQ4", null, true, "/env/atelier/solexa_HISEQ4/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ5", "HISEQ5", null, true, "/env/atelier/solexa_HISEQ5/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ6", "HISEQ6", null, true, "/env/atelier/solexa_HISEQ6/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ7", "HISEQ7", null, true, "/env/atelier/solexa_HISEQ7/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add(createInstrument("HISEQ8", "HISEQ8", null, true, "/env/atelier/solexa_HISEQ8/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );
		return instruments;
	}

	
	public static List<Instrument> getInstrumentHiseq2500() throws DAOException{
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add( createInstrument("HISEQ9", "HISEQ9", null, true, "/env/atelier/solexa_HISEQ9/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add( createInstrument("HISEQ10", "HISEQ10", null, true, "/env/atelier/solexa_HISEQ10/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add( createInstrument("HISEQ11", "HISEQ11", null, true, "/env/atelier/solexa_HISEQ11/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		return instruments;
	}
	

	
	private static List<PropertyDefinition> getCovarisProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("Frag_PE300","Frag_PE400","Frag_PE500","Frag_cDNA_Solexa"), "single"));
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
	
	


}
