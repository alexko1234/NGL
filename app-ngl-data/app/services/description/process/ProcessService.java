package services.description.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.typesafe.config.ConfigFactory;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
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
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Banque", "library"));
			l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Sequencage", "pre-sequencing"));
			l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Banque", "pre-library"));

		}
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Sequençage", "sequencing"));		
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Optical mapping", "mapping"));
		DAOHelpers.saveModels(ProcessCategory.class, l, errors);

	}

	private static void saveProcessTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessType> l = new ArrayList<ProcessType>();
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){

			

			l.add(DescriptionFactory.newProcessType("Librairie Nanopore", "nanopore-process-library", ProcessCategory.find.findByCode("sequencing"),getPropertyDefinitionsNanopore() , getExperimentTypes("nanopore-library"), 
					getExperimentTypes("nanopore-library").get(0), getExperimentTypes("nanopore-library").get(0),getExperimentTypes("ext-to-nanopore").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			l.add(DescriptionFactory.newProcessType("Run Nanopore", "nanopore-run", ProcessCategory.find.findByCode("sequencing"),getPropertyDefinitionsNanopore() , getExperimentTypes("nanopore-depot"), 
					getExperimentTypes("nanopore-depot").get(0), getExperimentTypes("nanopore-depot").get(0),getExperimentTypes("ext-to-nanopore").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			
			l.add(DescriptionFactory.newProcessType("Librairie PE sans sizing", "Lib-PE-NoSizing", ProcessCategory.find.findByCode("library"), getPropertyDefinitionsLib300600(), getExperimentTypes("fragmentation","librairie-indexing","amplification"), 
					getExperimentTypes("fragmentation").get(0), getExperimentTypes("amplification").get(0), getExperimentTypes("ext-to-library").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			l.add(DescriptionFactory.newProcessType("qPCR et normalisation", "qPCR-normalisation", ProcessCategory.find.findByCode("pre-sequencing"), getPropertyDefinitionsQPCRQuantification(), getExperimentTypes("qPCR-quantification","solution-stock"), 
					getExperimentTypes("qPCR-quantification").get(0), getExperimentTypes("solution-stock").get(0), getExperimentTypes("ext-to-qpcr").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			l.add(DescriptionFactory.newProcessType("Normalisation", "normalisation-process", ProcessCategory.find.findByCode("pre-sequencing"), getPropertyDefinitionsQPCRQuantification(), getExperimentTypes("solution-stock"), 
					getExperimentTypes("solution-stock").get(0), getExperimentTypes("solution-stock").get(0), getExperimentTypes("ext-to-solution-stock").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
			
			l.add(DescriptionFactory.newProcessType("Run Illumina", "illumina-run-cng", ProcessCategory.find.findByCode("sequencing"),getPropertyDefinitionsIlluminaDepotCNG() , getExperimentTypes("prepa-flowcell-cng","illumina-depot"), 
					getExperimentTypes("prepa-flowcell-cng").get(0), getExperimentTypes("illumina-depot").get(0),getExperimentTypes("ext-to-prepa-flowcell").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
				
		}
		
		l.add(DescriptionFactory.newProcessType("Run Illumina", "illumina-run", ProcessCategory.find.findByCode("sequencing"),getPropertyDefinitionsIlluminaDepotCNS() , getExperimentTypes("prepa-flowcell","illumina-depot"), 
				getExperimentTypes("prepa-flowcell").get(0), getExperimentTypes("illumina-depot").get(0),getExperimentTypes("ext-to-prepa-flowcell").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(DescriptionFactory.newProcessType("Run Opgen", "opgen-run", ProcessCategory.find.findByCode("mapping"),null , getExperimentTypes("opgen-depot"), getExperimentTypes("opgen-depot").get(0), getExperimentTypes("opgen-depot").get(0),getExperimentTypes("ext-to-opgen-depot").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		DAOHelpers.saveModels(ProcessType.class, l, errors);
	}


	/*private static List<PropertyDefinition> getPropertyDefinitionsOpgenDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type MapCard","mapcardType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("standard","HD"), "single"));
		return propertyDefinitions;
	}*/

	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepotCNS() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		//TO do multi value
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type séquencage","sequencingType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("Hiseq 2000/2500N" , "Hiseq 2500 Rapide" ,"Miseq"), "single",100));
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type de lectures", "readType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("SR","PE"), "single",200));		
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Longueur de lecture", "readLength"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("50","100","150","250","300","500","600"), "single",300));
		
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("% à déposer prévisionnel", "estimatedPercentPerLane"
						, LevelService.getLevels(Level.CODE.Process),Double.class, true,"single",400));	
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepotCNG() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		//TO do multi value
		//FDS 11-03-2015 =>NGL-356: supression GAIIx, ajout Nextseq, fusion  "Hiseq 2000", "Hiseq 2500 normal"-> "Hiseq 2000/2500N"
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type séquencage","sequencingType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("Hiseq 2000/2500N" , "Hiseq 2500 rapide" ,"Miseq","Nextseq"), "single",100));
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type de lectures", "readType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("SR","PE"), "single",200));		
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Longueur de lecture", "readLength"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("50","100","150","250","300","500","600"), "single",300));		

		return propertyDefinitions;
	}

	
	private static List<ExperimentType> getExperimentTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentType.class,ExperimentType.find, codes);
	}


	public static List<PropertyDefinition> getPropertyDefinitionsLib300600() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Robot à utiliser","autoVsManuel", LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("MAN","ROBOT"), "single",100));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Quantité à engager", "inputQuantity", LevelService.getLevels(Level.CODE.Process),Double.class, true, DescriptionFactory.newValues("250","500"), "single",200));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Objectif Taille de banque finale", "librarySizeGoal", LevelService.getLevels(Level.CODE.Process),String.class,true,DescriptionFactory.newValues("300-600","300-800"), "single",300));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Catégorie Imputation", "imputationCat", LevelService.getLevels(Level.CODE.Process),String.class,true,DescriptionFactory.newValues("PRODUCTION","DEVELOPPEMENT"), "single",400));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Date limite", "deadline", LevelService.getLevels(Level.CODE.Process),Date.class,false, "single",500));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Indexing", "indexing", LevelService.getLevels(Level.CODE.Process),String.class,true,DescriptionFactory.newValues("Single indexing","Dual indexing","Pas d'indexing"), "single",600));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsQPCRQuantification() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();		
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Commentaire","comment",LevelService.getLevels(Level.CODE.Process),String.class,false,"single"));
		
		return propertyDefinitions;
	}
	
	
	public static List<PropertyDefinition> getPropertyDefinitionsNanopore() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();		
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Commentaire","comment",LevelService.getLevels(Level.CODE.Process),String.class,false,"single"));
		
		return propertyDefinitions;
	}



}
