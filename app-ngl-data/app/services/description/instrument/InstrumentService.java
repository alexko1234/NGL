package services.description.instrument;

import static services.description.DescriptionFactory.newInstrumentCategory;
import static services.description.DescriptionFactory.newInstrumentUsedType;
import static services.description.DescriptionFactory.newPropertiesDefinition;
import static services.description.DescriptionFactory.newValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.Logger;
import play.data.validation.ValidationError;
import services.description.common.LevelService;

public class InstrumentService {
	
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{
		Logger.debug("Begin remove Instrument Used TYpe");
		DAOHelpers.removeAll(InstrumentUsedType.class, InstrumentUsedType.find);
		DAOHelpers.removeAll(InstrumentCategory.class, InstrumentCategory.find);
		Logger.debug("End Remove");
		saveInstrumentCategories(errors);
		Logger.debug("End Inst category");
		saveInstrumentUsedTypes(errors);	
		Logger.debug("End Inst type");
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
		DAOHelpers.saveModels(InstrumentCategory.class, l, errors);
		
	}
	
	public static void saveInstrumentUsedTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		
		List<InstrumentUsedType> l = new ArrayList<InstrumentUsedType>();
		l.add(newInstrumentUsedType("Main", "hand", InstrumentCategory.find.findByCode("hand"), null, getIntruments(new String[]{"Main"}),getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"})));
		l.add(newInstrumentUsedType("Covaris S2", "covaris-s2", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), getIntruments(new String[]{"Covaris 1", "Covaris 2"}),getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"})));
		l.add(newInstrumentUsedType("Covaris E210", "covaris-e210", InstrumentCategory.find.findByCode("covaris"), getCovarisProperties(), getIntruments(new String[]{"Covaris 3", "Covaris 4"}),getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"})));
		l.add(newInstrumentUsedType("Spri", "spri", InstrumentCategory.find.findByCode("spri"), getSpriProperties(), getIntruments(new String[]{"Spri 1", "Spri 2", "Spri 3"}),getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"})));
		l.add(newInstrumentUsedType("Thermo", "thermo", InstrumentCategory.find.findByCode("thermo"), getThermoProperties(), getIntruments(new String[]{"Thermo s1", "Thermo s2", "Thermo s3"}),getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"})));
		l.add(newInstrumentUsedType("Agilent 2100", "agilent-2100", InstrumentCategory.find.findByCode("agilent"), getAgilentProperties(), getIntruments(new String[]{"BioAnalyzer 1", "BioAnalyzer 2"}),getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"})));
		l.add(newInstrumentUsedType("iQuBit", "iqubit", InstrumentCategory.find.findByCode("qubit"), getQuBitProperties(), getIntruments(new String[]{"QuBit 1", "QuBit 2"}),getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"})));
		l.add(newInstrumentUsedType("iqPCR", "iqpcr", InstrumentCategory.find.findByCode("qpcr"), getQPCRProperties(), getIntruments(new String[]{"qPCR 1"}),getContainerSupportCategories(new String[]{"tube"}),getContainerSupportCategories(new String[]{"tube"})));
		
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

	private static List<Instrument> getIntruments(String[] names) {
		List<Instrument> l = new ArrayList<Instrument>();
		for(String name: names){
			Instrument instrument = new Instrument();
			instrument.name = name;
			instrument.code = name.toLowerCase().replaceAll("\\s+", "-");
			l.add(instrument);
		}
		
		
		return l;
	}
	
	private static List<ContainerSupportCategory> getContainerSupportCategories(String[] codes) throws DAOException{		
		return DAOHelpers.getModelByCodes(ContainerSupportCategory.class,ContainerSupportCategory.find, codes);
	}

}
