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
import services.description.common.LevelService;
import services.description.DescriptionFactory;

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
		
		l.add(newInstrumentCategory("HISEQ2000","HISEQ2000"));
		l.add(newInstrumentCategory("HISEQ2500","HISEQ2500"));
		
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
						createInstrument("Covaris 1", "Covaris 1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Covaris 2", "Covaris 2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ) ,
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("Covaris E210", "covaris-e210", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), 
				getInstruments(
						createInstrument("Covaris 3", "Covaris 3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Covaris 4", "Covaris 4", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ) , 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("Spri", "spri", InstrumentCategory.find.findByCode("spri"), getSpriProperties(), 
				getInstruments(
						createInstrument("Spri 1", "Spri 1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Spri 2", "Spri 2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Spri 3", "Spri 3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ), 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("Thermo", "thermo", InstrumentCategory.find.findByCode("thermo"), getThermoProperties(), 
				getInstruments(
						createInstrument("Thermo s1", "Thermo s1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Thermo s2", "Thermo s2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("Thermo s3", "Thermo s3", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ), 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("Agilent 2100", "agilent-2100", InstrumentCategory.find.findByCode("agilent"), getAgilentProperties(), 
				getInstruments(
						createInstrument("BioAnalyzer 1", "BioAnalyzer 1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("BioAnalyzer 2", "BioAnalyzer 2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ), 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("iQuBit", "iqubit", InstrumentCategory.find.findByCode("qubit"), getQuBitProperties(), 
				getInstruments(
						createInstrument("QuBit 1", "QuBit 1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)), 
						createInstrument("QuBit 2", "QuBit 2", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ) , 
				getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newInstrumentUsedType("iqPCR", "iqpcr", InstrumentCategory.find.findByCode("qpcr"), getQPCRProperties(), 
				getInstruments(
						createInstrument("qPCR 1", "qPCR 1", true, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS)) ), 
				getContainerSupportCategories(new String[]{"tube"}), getContainerSupportCategories(new String[]{"tube"}), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		//CNG
		l.add(newInstrumentUsedType("HISEQ2000", "HISEQ2000", InstrumentCategory.find.findByCode("HISEQ2000"), null, 
				getInstruments(
						createInstrument("HISEQ1", "HISEQ1", true, "/env/export/cngstkprd003/v_igseq4/HISEQ1/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("HISEQ2", "HISEQ2", true, "/env/export/cngstkprd003/v_igseq4/HISEQ2/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("HISEQ3", "HISEQ3", true, "/env/export/cngstkprd003/v_igseq4/HISEQ3/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("HISEQ4", "HISEQ4", true, "/env/export/cngstkprd003/v_igseq5/HISEQ4/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("HISEQ5", "HISEQ5", true, "/env/export/cngstkprd003/v_igseq5/HISEQ5/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("HISEQ6", "HISEQ6", true, "/env/export/cngstkprd003/v_igseq5/HISEQ6/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("HISEQ7", "HISEQ7", true, "/env/export/cngstkprd003/v_igseq6/HISEQ7/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("HISEQ8", "HISEQ8", true, "/env/export/cngstkprd003/v_igseq6/HISEQ8/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) ), 
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		l.add(newInstrumentUsedType("HISEQ2500", "HISEQ2500", InstrumentCategory.find.findByCode("HISEQ2500"), null, 
				getInstruments(
						createInstrument("HISEQ9", "HISEQ9", true, "/env/export/cngstkprd003/v_igseq6/HISEQ9/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("HISEQ10", "HISEQ10", true, "/env/export/cngstkprd003/v_igseq7/HISEQ10/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)),
						createInstrument("HISEQ11", "HISEQ11", true, "/env/export/cngstkprd003/v_igseq7/HISEQ11/", DescriptionFactory.getInstitutes(Institute.CODE.CNG)) ), 
				getContainerSupportCategories(new String[]{"flowcell-8"}), null, 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		DAOHelpers.saveModels(InstrumentUsedType.class, l, errors);
	}
	
	
	private static List<PropertyDefinition> getCovarisProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("Frag_PE300","Frag_PE400","Frag_PE500","Frag_cDNA_Solexa")));
		//Data test
		l.add(newPropertiesDefinition("key container in", "keyContainerIn",LevelService.getLevels(Level.CODE.ContainerIn), String.class, true, newValues("val1","val2","val3")));
		return l;
	}
	
	private static List<PropertyDefinition> getSpriProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("300-600")));		
		return l;
	}
	
	private static List<PropertyDefinition> getThermoProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Programme", "program", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("15","18")));		
		return l;
	}
	
	private static List<PropertyDefinition> getAgilentProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Type puce", "chipType", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("DNA HS", "DNA 12000", "RNA")));		
		return l;
	}
	
	private static List<PropertyDefinition> getQuBitProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Kit", "kit", LevelService.getLevels(Level.CODE.Instrument), String.class, true, newValues("HS", "BR")));		
		return l;
	}
	
	private static List<PropertyDefinition> getQPCRProperties() throws DAOException {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		l.add(newPropertiesDefinition("Nb. Echantillon", "sampleNumber", LevelService.getLevels(Level.CODE.Instrument), Integer.class, true));		
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
