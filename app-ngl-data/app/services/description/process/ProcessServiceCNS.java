package services.description.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.declaration.cns.Bionano;
import services.description.declaration.cns.ExtractionDNARNA;
import services.description.declaration.cns.MetaBarCoding;
import services.description.declaration.cns.MetaTProcess;
import services.description.declaration.cns.Nanopore;
import services.description.declaration.cns.Opgen;
import services.description.declaration.cns.RunIllumina;

import com.typesafe.config.ConfigFactory;

public class ProcessServiceCNS extends AbstractProcessService {


	public void saveProcessCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessCategory> l = new ArrayList<ProcessCategory>();
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			//l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Sequencage", "pre-sequencing"));
			//l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Banque", "pre-library"));

		}
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Préparation échantillon", "sample-prep"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Banque", "library"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Sequençage", "sequencing"));		
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Optical mapping", "mapping"));
		DAOHelpers.saveModels(ProcessCategory.class, l, errors);

	}

	public void saveProcessTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessType> l = new ArrayList<ProcessType>();
		
		if(ConfigFactory.load().getString("ngl.env").equals("DEV") ){
			/*
			l.add(DescriptionFactory.newProcessType("Librairie PE sans sizing", "Lib-PE-NoSizing", ProcessCategory.find.findByCode("library"), getPropertyDefinitionsLib300600(),
					Arrays.asList(getPET("fragmentation",-1),getPET("librairie-indexing",0),getPET("amplification",1)), 
					getExperimentTypes("fragmentation").get(0), getExperimentTypes("amplification").get(0), getExperimentTypes("ext-to-library").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
			*/
			l.add(DescriptionFactory.newProcessType("qPCR-norm, FC, dépôt Illumina", "qpcr-norm-fc-depot-illumina", ProcessCategory.find.findByCode("sequencing"), getPropertyDefinitionsQPCRQuantification(),
					Arrays.asList(getPET("ext-to-qpcr-norm-fc-depot-illumina",-1),getPET("sizing",-1),getPET("pcr-amplification-and-purification",-1),getPET("qpcr-quantification",0),getPET("solution-stock",0),getPET("prepa-flowcell",1),getPET("prepa-fc-ordered",1),getPET("illumina-depot",2)), 
					getExperimentTypes("qpcr-quantification").get(0), getExperimentTypes("illumina-depot").get(0), getExperimentTypes("ext-to-qpcr-norm-fc-depot-illumina").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
			
			l.add(DescriptionFactory.newProcessType("Norm, FC, dépôt Illumina", "norm-fc-depot-illumina", ProcessCategory.find.findByCode("sequencing"), getPropertyDefinitionsQPCRQuantification(),
					Arrays.asList(getPET("ext-to-norm-fc-depot-illumina",-1),getPET("sizing",-1),getPET("pcr-amplification-and-purification",-1),getPET("solution-stock",0),getPET("prepa-flowcell",1),getPET("prepa-fc-ordered",1),getPET("illumina-depot",2)), 
					getExperimentTypes("solution-stock").get(0), getExperimentTypes("illumina-depot").get(0), getExperimentTypes("ext-to-norm-fc-depot-illumina").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		}

		l.addAll(new MetaBarCoding().getProcessType());
		l.addAll(new MetaTProcess().getProcessType());
		l.addAll(new Bionano().getProcessType());
		l.addAll(new Nanopore().getProcessType());
		l.addAll(new RunIllumina().getProcessType());
		l.addAll(new Opgen().getProcessType());
		l.addAll(new ExtractionDNARNA().getProcessType());
		
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
						, LevelService.getLevels(Level.CODE.Process),String.class, true, getSequencingType(), "single",100));
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type de lectures", "readType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, getReadType(), "single",200));		
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Longueur de lecture", "readLength"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, getReadLenght(), "single",300));
		
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
		values.add(DescriptionFactory.newValue("undefined","Non déterminé"));
		return values;	
	}
	

	private static List<Value> getReadType(){
			List<Value> values = new ArrayList<Value>();
			values.add(DescriptionFactory.newValue("SR", "SR"));
			values.add(DescriptionFactory.newValue("PE", "PE"));
			values.add(DescriptionFactory.newValue("undefined","Non déterminé"));
			return values;
	}
		
	private static List<Value> getReadLenght(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("50","50"));
		values.add(DescriptionFactory.newValue("100","100"));
		values.add(DescriptionFactory.newValue("150","150"));
		values.add(DescriptionFactory.newValue("250","250"));
		values.add(DescriptionFactory.newValue("300","300"));
		values.add(DescriptionFactory.newValue("500","500"));
		values.add(DescriptionFactory.newValue("600","600"));
		values.add(DescriptionFactory.newValue("undefined","Non déterminé"));
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
		List<PropertyDefinition> propertyDefinitions =getPropertyDefinitionsIlluminaDepotCNS();			
		return propertyDefinitions;
	}
	
	
	
	
}
