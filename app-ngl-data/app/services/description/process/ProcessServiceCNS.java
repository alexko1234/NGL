package services.description.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessExperimentType;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;

import com.typesafe.config.ConfigFactory;

public class ProcessServiceCNS extends AbstractProcessService {


	public void saveProcessCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessCategory> l = new ArrayList<ProcessCategory>();
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			//l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Sequencage", "pre-sequencing"));
			//l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Banque", "pre-library"));

		}
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Banque", "library"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Sequençage", "sequencing"));		
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Optical mapping", "mapping"));
		DAOHelpers.saveModels(ProcessCategory.class, l, errors);

	}

	public void saveProcessTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessType> l = new ArrayList<ProcessType>();
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){

			//Bionano
			l.add(DescriptionFactory.newProcessType("NLRS, Irys chip, dépôt", "bionano-nlrs-process", ProcessCategory.find.findByCode("mapping"), getPropertyDefinitionsBionano(), getExperimentTypes("irys-nlrs-prep","irys-chip-preparation","bionano-depot"), 
					getExperimentTypes("irys-nlrs-prep").get(0), getExperimentTypes("bionano-depot").get(0), getExperimentTypes("ext-to-nlrs-prep").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
			
			l.add(DescriptionFactory.newProcessType("Irys Chip, dépôt", "bionano-chip-process", ProcessCategory.find.findByCode("mapping"), getPropertyDefinitionsBionano(), getExperimentTypes("irys-chip-preparation","bionano-depot"), 
					getExperimentTypes("irys-chip-preparation").get(0), getExperimentTypes("bionano-depot").get(0), getExperimentTypes("ext-to-nlrs-prep").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
			
			l.add(DescriptionFactory.newProcessType("Redépôt BioNano", "bionano-run", ProcessCategory.find.findByCode("mapping"), getPropertyDefinitionsBionano(), getExperimentTypes("bionano-depot"), 
					getExperimentTypes("bionano-depot").get(0), getExperimentTypes("bionano-depot").get(0), getExperimentTypes("ext-to-nlrs-prep").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		/*
			l.add(DescriptionFactory.newProcessType("Librairie PE sans sizing", "Lib-PE-NoSizing", ProcessCategory.find.findByCode("library"), getPropertyDefinitionsLib300600(), getExperimentTypes("fragmentation","librairie-indexing","amplification"), 
					getExperimentTypes("fragmentation").get(0), getExperimentTypes("amplification").get(0), getExperimentTypes("ext-to-library").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		//	l.add(DescriptionFactory.newProcessType("qPCR et normalisation", "qPCR-normalisation", ProcessCategory.find.findByCode("pre-sequencing"), getPropertyDefinitionsQPCRQuantification(), getExperimentTypes("qPCR-quantification","solution-stock"), 
			//		getExperimentTypes("qPCR-quantification").get(0), getExperimentTypes("solution-stock").get(0), getExperimentTypes("ext-to-qpcr").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
			
			l.add(DescriptionFactory.newProcessType("Normalisation", "normalisation-process", ProcessCategory.find.findByCode("pre-sequencing"), getPropertyDefinitionsQPCRQuantification(), getExperimentTypes("solution-stock"), 
					getExperimentTypes("solution-stock").get(0), getExperimentTypes("solution-stock").get(0), getExperimentTypes("ext-to-solution-stock").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
			*/
			//Bionano
			l.add(DescriptionFactory.newProcessType("NLRS, Irys chip, dépôt", "bionano-nlrs-process", ProcessCategory.find.findByCode("mapping"), getPropertyDefinitionsBionano(), 
					Arrays.asList(getPET("ext-to-irys-nlrs-prep",-1),getPET("irys-nlrs-prep",0),getPET("irys-chip-preparation",1),getPET("bionano-depot",2)), 
					getExperimentTypes("irys-nlrs-prep").get(0), getExperimentTypes("bionano-depot").get(0), getExperimentTypes("ext-to-irys-nlrs-prep").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
			
			l.add(DescriptionFactory.newProcessType("Irys Chip, dépôt", "bionano-chip-process", ProcessCategory.find.findByCode("mapping"), getPropertyDefinitionsBionano(), 
					Arrays.asList(getPET("ext-to-irys-chip-preparation",-1),getPET("irys-chip-preparation",0),getPET("bionano-depot",1)),
					getExperimentTypes("irys-chip-preparation").get(0), getExperimentTypes("bionano-depot").get(0), getExperimentTypes("ext-to-irys-chip-preparation").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
			
			l.add(DescriptionFactory.newProcessType("Redépôt BioNano", "bionano-run", ProcessCategory.find.findByCode("mapping"), getPropertyDefinitionsBionano(), 
					Arrays.asList(getPET("ext-to-bionano-depot",-1), getPET("bionano-depot",0)), 
					getExperimentTypes("bionano-depot").get(0), getExperimentTypes("bionano-depot").get(0), getExperimentTypes("ext-to-bionano-depot").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
								
		}
	
		l.add(DescriptionFactory.newProcessType("Frg, Lib ONT, Dépôt", "nanopore-process-library", ProcessCategory.find.findByCode("library"),getPropertyDefinitionsNanoporeFragmentation() , 
				Arrays.asList(getPET("ext-to-nanopore-frg-precr",-1),getPET("nanopore-fragmentation",0),getPET("nanopore-library",1),getPET("nanopore-depot",2)), 
				getExperimentTypes("nanopore-fragmentation").get(0), getExperimentTypes("nanopore-depot").get(0),getExperimentTypes("ext-to-nanopore-frg-precr").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(DescriptionFactory.newProcessType("Lib ONT, Dépôt", "nanopore-process-library-no-frg", ProcessCategory.find.findByCode("library"),getPropertyDefinitionsNanoporeLibrary() , 
				Arrays.asList(getPET("ext-to-lib-ont",-1),getPET("nanopore-library",0),getPET("nanopore-depot",1)),
				getExperimentTypes("nanopore-library").get(0), getExperimentTypes("nanopore-depot").get(0),getExperimentTypes("ext-to-lib-ont").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
				
		l.add(DescriptionFactory.newProcessType("Run Nanopore", "nanopore-run", ProcessCategory.find.findByCode("sequencing"),null , 
				Arrays.asList(getPET("ext-to-nanopore-depot",-1), getPET("nanopore-depot",0)),
				getExperimentTypes("nanopore-depot").get(0), getExperimentTypes("nanopore-depot").get(0),getExperimentTypes("ext-to-nanopore-depot").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		
		l.add(DescriptionFactory.newProcessType("Run Illumina", "illumina-run", ProcessCategory.find.findByCode("sequencing"),getPropertyDefinitionsIlluminaDepotCNS() , 
				Arrays.asList(getPET("ext-to-prepa-flowcell",-1),getPET("prepa-flowcell",0),getPET("prepa-fc-ordered",0),getPET("illumina-depot",1)), 
				getExperimentTypes("prepa-flowcell").get(0), getExperimentTypes("illumina-depot").get(0),getExperimentTypes("ext-to-prepa-flowcell").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(DescriptionFactory.newProcessType("Run Opgen", "opgen-run", ProcessCategory.find.findByCode("mapping"),null , 
				Arrays.asList(getPET("ext-to-opgen-depot",-1), getPET("opgen-depot",0)), 
				getExperimentTypes("opgen-depot").get(0), getExperimentTypes("opgen-depot").get(0),getExperimentTypes("ext-to-opgen-depot").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		DAOHelpers.saveModels(ProcessType.class, l, errors);
		
		

	

	/*private static List<PropertyDefinition> getPropertyDefinitionsOpgenDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type MapCard","mapcardType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("standard","HD"), "single"));
		return propertyDefinitions;
	}*/

	private List<PropertyDefinition> getPropertyDefinitionsBionano() {
		// TODO Auto-generated method stub
		return null;
	}

	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepotCNS() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		//TO do multi value
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type séquencage","sequencingType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, getSequencingType(), "single",100));
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

	private static List<Value> getSequencingType(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("Hiseq 2000/2500N", "Hiseq 2000 / 2500 N"));
		values.add(DescriptionFactory.newValue("Hiseq 2500 Rapide", "Hiseq 2500 Rapide"));
		values.add(DescriptionFactory.newValue("Miseq", "Miseq"));
		values.add(DescriptionFactory.newValue("Hiseq 4000", "Hiseq 4000"));
		return values;	
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
		return propertyDefinitions;
	}
	
	
	public static List<PropertyDefinition> getPropertyDefinitionsNanoporeFragmentation() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Type processus banque","libProcessTypeCode",LevelService.getLevels(Level.CODE.Process,Level.CODE.Content),String.class, true, getLibProcessTypeCodeValues(), "ONT","single" ,1));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Taille banque souhaitée","librarySize",LevelService.getLevels(Level.CODE.Process),Integer.class,true, DescriptionFactory.newValues("8","20")
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE),MeasureUnit.find.findByCode( "kb"),MeasureUnit.find.findByCode( "kb"), "single",2));
		
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsNanoporeLibrary() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Type processus banque","libProcessTypeCode",LevelService.getLevels(Level.CODE.Process,Level.CODE.Content),String.class, true, getLibProcessTypeCodeValues(), "ONT","single" ,1));
		
		return propertyDefinitions;
	}

	private static List<Value> getLibProcessTypeCodeValues(){
        List<Value> values = new ArrayList<Value>();
         values.add(DescriptionFactory.newValue("ONT","ONT - Nanopore"));
         return values;
	}



	
}
