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
import models.laboratory.common.description.StateCategory;
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
	
	/**
	 * Save all ExperimentCategory
	 * @param errors
	 * @throws DAOException 
	 */
	public static void saveStateCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<StateCategory> l = new ArrayList<StateCategory>();
				
		for (StateCategory.CODE code : StateCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(StateCategory.class, code.name(), code.name()));
		}
		DAOHelpers.saveModels(StateCategory.class, l, errors);
		
	}
	
	private static void saveExperimentTypes(
			Map<String, List<ValidationError>> errors) throws DAOException {
		List<ExperimentType> l = new ArrayList<ExperimentType>();
		
		//transformation
		l.add(newExperimentType("Fragmentation","fragmentation",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsFragmentation(), getProtocols("fragmentation_ptr_sox140_1"), getInstrumentUsedTypes("hand","covaris-s2","covaris-e210"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS) ));
		l.add(newExperimentType("Librairie","librairie",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null, getProtocols("bqspri_ptr_sox142_1"), getInstrumentUsedTypes("hand","spri"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newExperimentType("Amplification","amplification",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null, getProtocols("amplif_ptr_sox144_1") , getInstrumentUsedTypes("hand","thermo"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		//qc
		l.add(newExperimentType("Bioanalyzer Non Ampli","bioanalyzer-na",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsBioanalyzer(), null, getInstrumentUsedTypes("agilent-2100"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newExperimentType("Bioanalyzer Ampli","bioanalyzer-a",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsBioanalyzer(), null, getInstrumentUsedTypes("agilent-2100"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newExperimentType("QuBit","qubit",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), null, null, getInstrumentUsedTypes("iqubit"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newExperimentType("qPCR","qpcr",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), null, null, getInstrumentUsedTypes("iqpcr"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		//purif
		l.add(newExperimentType("Ampure Non Ampli","ampure-na",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), null, null, getInstrumentUsedTypes("hand"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newExperimentType("Ampure Ampli","ampure-a",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), null, null, getInstrumentUsedTypes("hand"),"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		
		//void
		l.add(newExperimentType("Void Banque 300-600","void-lib-300-600",ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null, null,"OneToOne", DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		
		DAOHelpers.saveModels(ExperimentType.class, l, errors);
		
	}

	private static void saveExperimentTypeNodes(Map<String, List<ValidationError>> errors) throws DAOException {
		newExperimentTypeNode("void-lib-300-600", getExperimentTypes("void-lib-300-600").get(0), false, false, null, null, null).save();
		newExperimentTypeNode("fragmentation", getExperimentTypes("fragmentation").get(0), false, false, getExperimentTypeNodes("void-lib-300-600"), getExperimentTypes("ampure-na"), getExperimentTypes("bioanalyzer-na")).save();
		newExperimentTypeNode("librairie", getExperimentTypes("librairie").get(0), false, false, getExperimentTypeNodes("fragmentation"), getExperimentTypes("ampure-na"), getExperimentTypes("qubit","bioanalyzer-na")).save();
		newExperimentTypeNode("amplification", getExperimentTypes("amplification").get(0), false, false, getExperimentTypeNodes("librairie"), getExperimentTypes("ampure-na"), getExperimentTypes("bioanalyzer-na")).save();
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
	
	
	//Data Test
	public static List<PropertyDefinition> getPropertyDefinitionsFragmentation() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Libelle 1","Key1", LevelService.getLevels(Level.CODE.Experiment),Double.class, false,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("kb"), MeasureUnit.find.findByCode("kb")));
		propertyDefinitions.add(newPropertiesDefinition("Libelle 2", "Key2", LevelService.getLevels(Level.CODE.ContainerOut),String.class, false));
		propertyDefinitions.add(newPropertiesDefinition("Libelle 3", "Key3", LevelService.getLevels(Level.CODE.ContainerIn),String.class, false));
		return propertyDefinitions;
	}
	
	//TODO
	// Propriete taille en output et non en input ?
	// Valider les keys
	public static List<PropertyDefinition> getPropertyDefinitionsBioanalyzer() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		// A supprimer une fois le type de support category sera géré
        propertyDefinitions.add(newPropertiesDefinition("Position","location", LevelService.getLevels(Level.CODE.ContainerIn),Integer.class, true));
        propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "committedVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true));		
		propertyDefinitions.add(newPropertiesDefinition("Taille", "size", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("kb"), MeasureUnit.find.findByCode("kb")));
		// Voir avec Guillaume comment gérer les fichiers
        propertyDefinitions.add(newPropertiesDefinition("Profil DNA HS", "fileResult", LevelService.getLevels(Level.CODE.ContainerOut),String.class, true));
		return propertyDefinitions;
	}
	
}
