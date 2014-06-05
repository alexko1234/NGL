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
		l.add(newInstrumentCategory("Thermo","thermo"));
		l.add(newInstrumentCategory("Agilent","agilent"));
		l.add(newInstrumentCategory("QuBit","qubit"));
		l.add(newInstrumentCategory("qPCR","qpcr"));
		l.add(newInstrumentCategory("Main","hand"));
		l.add(newInstrumentCategory("CBot","cbot"));
		l.add(newInstrumentCategory("Sequenceurs Illumina","seq-illumina"));
		l.add(newInstrumentCategory("Cartographie Optique Opgen","opt-map-opgen"));
	
		DAOHelpers.saveModels(InstrumentCategory.class, l, errors);
		
	}
	
	public static void saveInstrumentUsedTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		
		List<InstrumentUsedType> l = new ArrayList<InstrumentUsedType>();
		
		//CNS
		l.add(newInstrumentUsedType("Main", "hand", InstrumentCategory.find.findByCode("hand"), null, 
				getInstruments(
						createInstrument("Main", "Main", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ),
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("Covaris S2", "covaris-s2", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("Covaris1", "Covaris1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Covaris2", "Covaris2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ) ,
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("Covaris E210", "covaris-e210", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("Covaris3", "Covaris3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Covaris4", "Covaris4", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ) , 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("Spri", "spri", InstrumentCategory.find.findByCode("spri"), getSpriProperties(), 
				getInstruments(
						createInstrument("Spri1", "Spri1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Spri2", "Spri2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Spri3", "Spri3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ), 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("Thermo", "thermo", InstrumentCategory.find.findByCode("thermo"), getThermoProperties(), 
				getInstruments(
						createInstrument("ThermoS1", "ThermoS1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("ThermoS2", "ThermoS2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("ThermoS3", "ThermoS3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ), 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("Agilent 2100", "agilent-2100", InstrumentCategory.find.findByCode("agilent"), getAgilentProperties(), 
				getInstruments(
						createInstrument("BioAnalyzer1", "BioAnalyzer1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("BioAnalyzer2", "BioAnalyzer2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ), 
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("iQuBit", "iqubit", InstrumentCategory.find.findByCode("qubit"), getQuBitProperties(), 
				getInstruments(
						createInstrument("QuBit1", "QuBit1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("QuBit2", "QuBit2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ) , 
				getContainerSupportCategories(new String[]{"tube"}),null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("iqPCR", "iqpcr", InstrumentCategory.find.findByCode("qpcr"), getQPCRProperties(), 
				getInstruments(
						createInstrument("qPCR1", "qPCR1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ), 
				getContainerSupportCategories(new String[]{"tube"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newInstrumentUsedType("cBot", "cBot", InstrumentCategory.find.findByCode("cbot"), getPropertiesCBot(), 
				getInstruments(
						createInstrument("cBot1", "cBot1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot2", "cBot2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot3", "cBot3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot4", "cBot4", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS))), 
				getContainerSupportCategories(new String[]{"tube"}), getContainerSupportCategories(new String[]{"flowcell-8"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newInstrumentUsedType("cBot-interne", "cBot-interne", InstrumentCategory.find.findByCode("cbot"), getPropertiesCBot(), 
				getInstruments(
						createInstrument("cBot Fluor A", "cBot-Fluor-A", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot Fluor B", "cBot-Fluor-B", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot Platine A", "cBot-Platine-A", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot Platine B", "cBot-Platine-B", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot Mimosa", "cBot-Mimosa", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)),
						createInstrument("cBot Melisse", "cBot-Melisse", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS))), 
				getContainerSupportCategories(new String[]{"tube"}), getContainerSupportCategories(new String[]{"flowcell-2","flowcell-1"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		//CNG et CNS
		l.add(newInstrumentUsedType("GAIIx", "GAIIx", InstrumentCategory.find.findByCode("seq-illumina"), null, 
				getInstrumentGAII(),
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG,Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("MISEQ", "MISEQ", InstrumentCategory.find.findByCode("seq-illumina"), getMiseqProperties(), 
				getInstrumentMiSeq(),
				getContainerSupportCategories(new String[]{"flowcell-1"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG,Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("HISEQ2000", "HISEQ2000", InstrumentCategory.find.findByCode("seq-illumina"), getHiseq2000Properties(), 
				getInstrumentHiseq2000(),
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG,Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("HISEQ2500", "HISEQ2500", InstrumentCategory.find.findByCode("seq-illumina"), getHiseq2000Properties(), 
				getInstrumentHiseq2500(),
				getContainerSupportCategories(new String[]{"flowcell-8","flowcell-2"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG,Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("ARGUS", "ARGUS", InstrumentCategory.find.findByCode("opt-map-opgen"), getArgusProperties(), 
				getInstrumentOpgen(),
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"mapcard"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		DAOHelpers.saveModels(InstrumentUsedType.class, l, errors);
	}

	

	
	private static List<PropertyDefinition> getPropertiesCBot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Type lectures","readType"
        		, LevelService.getLevels(Level.CODE.Instrument,Level.CODE.Content),String.class, true,DescriptionFactory.newValues("SR","PE"),"single"));
        return propertyDefinitions;
	}

	private static List<PropertyDefinition> getHiseq2000Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Position","location"
        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single"));
        propertyDefinitions.add(newPropertiesDefinition("Type lecture", "readType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        return propertyDefinitions;
	}

	private static List<PropertyDefinition> getMiseqProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Type lecture", "readType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read1", "nbCyclesRead1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index1", "nbCyclesReadIndex1", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read2", "nbCyclesRead2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles Read Index2", "nbCyclesReadIndex2", LevelService.getLevels(Level.CODE.Instrument),Integer.class, true, "single"));
        return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getArgusProperties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Référence Carte", "mapcardRef", LevelService.getLevels(Level.CODE.Instrument),String.class, true, "single"));	
        return propertyDefinitions;
	}


	/*private static List<PropertyDefinition> getHiseq2500Properties() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		   propertyDefinitions.add(newPropertiesDefinition("Type run","runType"
	        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("RHS2500","RHS2500R"), "single"));
        propertyDefinitions.add(newPropertiesDefinition("Position","location"
        		, LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("A","B"), "single"));
        propertyDefinitions.add(newPropertiesDefinition("Type lecture", "readType", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR","PE"), "single"));	
        propertyDefinitions.add(newPropertiesDefinition("Nb cycles", "cycleNumber", LevelService.getLevels(Level.CODE.Instrument),String.class, true,DescriptionFactory.newValues("SR50", "SR100", "PE50", "PE100","PE150"), "single"));
        return propertyDefinitions;
	}*/

	private static List<Instrument> getInstrumentMiSeq() throws DAOException {
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(createInstrument("MELISSE", "MELISSE", true, "/env/atelier/solexa_MELISSE", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("MIMOSA", "MIMOSA", true, "/env/atelier/solexa_MIMOSA", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("MISEQ1", "MISEQ1", true, "/env/atelier/solexa_MISEQ1", DescriptionFactory.getInstitutes(Institute.CODE.CNS)) );
		instruments.add(createInstrument("MISEQ1", "MISEQ1", false, "/env/atelier/solexa_MISEQ1", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) );
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
		instruments.add( createInstrument("HISEQ11", "HISEQ11", true, "/env/export/cngstkprd003/v_igseq7/HISEQ11/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		instruments.add( createInstrument("HISEQ10", "HISEQ10", false, "/env/export/cngstkprd003/v_igseq7/HISEQ10/", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
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
	
	private static List<PropertyDefinition> getCovarisProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("Frag_PE300","Frag_PE400","Frag_PE500","Frag_cDNA_Solexa"), "single"));
		//Data test
		l.add(newPropertiesDefinition("key container in", "keyContainerIn",LevelService.getLevels(Level.CODE.ContainerIn), String.class, true, newValues("val1","val2","val3"), "single"));
		return l;
	}
	
	private static List<PropertyDefinition> getSpriProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("300-600"), "single"));		
		return l;
	}
	
	private static List<PropertyDefinition> getThermoProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("15","18"), "single"));		
		return l;
	}
	
	private static List<PropertyDefinition> getAgilentProperties() throws DAOException {
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
