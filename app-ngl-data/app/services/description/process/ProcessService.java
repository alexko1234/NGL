package services.description.process;

import services.description.DescriptionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.common.LevelService;


public class ProcessService {
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		DAOHelpers.removeAll(ProcessType.class, ProcessType.find);
		DAOHelpers.removeAll(ProcessCategory.class, ProcessCategory.find);
		
		saveProcessCategories(errors);
		saveProcessTypes(errors);
	}

	public static void saveProcessCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessCategory> l = new ArrayList<ProcessCategory>();
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Banque", "pre-library"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Banque", "library"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Sequençage", "sequencing"));		
		DAOHelpers.saveModels(ProcessCategory.class, l, errors);
		
	}
	
	private static void saveProcessTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessType> l = new ArrayList<ProcessType>();
		l.add(DescriptionFactory.newProcessType("Banque 300-600", "lib-300-600", ProcessCategory.find.findByCode("library"), getPropertyDefinitionsLib300600(), getExperimentTypes("fragmentation","librairie","amplification"), 
				getExperimentTypes("fragmentation").get(0), getExperimentTypes("amplification").get(0), getExperimentTypes("void-lib-300-600").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(DescriptionFactory.newProcessType("Run Illumina", "illumina-run", ProcessCategory.find.findByCode("sequencing"),getPropertyDefinitionsIlluminaDepot() , getExperimentTypes("prepa-flowcell","illumina-depot"), getExperimentTypes("prepa-flowcell").get(0), getExperimentTypes("illumina-depot").get(0),getExperimentTypes("void-illumina-depot").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		DAOHelpers.saveModels(ProcessType.class, l, errors);
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//TO do multi value
        propertyDefinitions.add(
        		DescriptionFactory.newPropertiesDefinition("Type séquencage","sequencingType"
        				, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("GAIIx", "Hiseq 2000", "Hiseq 2500 normal" , "Hiseq 2500 rapide" ,"Miseq")));
        propertyDefinitions.add(
        		DescriptionFactory.newPropertiesDefinition("Type de lecture", "readType"
        		, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("SR","PE")));		
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Longueur de lecture", "readLength"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("50","100","150","250","300","500","600")));
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type séquenceur", "sequencerType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("GAIIx","HISEQ2000","HISEQ2500","MISEQ")));

		return propertyDefinitions;
	}

	private static List<ExperimentType> getExperimentTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentType.class,ExperimentType.find, codes);
	}
	
	
	
	//TODO
	// Key to validate
	public static List<PropertyDefinition> getPropertyDefinitionsLib300600() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Valeur par defaut SPRI
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Robot","robotUsing", LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("SPRI")));
     // //Measure par defaut ng
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Quantité à engager", "quantityUsing", LevelService.getLevels(Level.CODE.Process),Double.class, true, DescriptionFactory.newValues("250","500")));		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Objectif Expérience ", "goalExperiment", LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("PE_400","Fragm_cDNA")));
		//Valeur par defaut 300-600pb
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Objectif Taille", "goalSize", LevelService.getLevels(Level.CODE.Process),String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Catégorie imputation", "imputationCategory", LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("PRODUCTION","DEVELOPPEMENT")));
		return propertyDefinitions;
	}
	

}
