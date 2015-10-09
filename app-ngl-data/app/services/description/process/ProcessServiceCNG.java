package services.description.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;

import com.typesafe.config.ConfigFactory;

public class ProcessServiceCNG  extends AbstractProcessService{


	public void saveProcessCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessCategory> l = new ArrayList<ProcessCategory>();
		
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Sequençage", "sequencing"));		
		DAOHelpers.saveModels(ProcessCategory.class, l, errors);
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Banque", "library"));
			l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Sequencage", "pre-sequencing"));
			l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Banque", "pre-library"));

		}
		

	}

	public void saveProcessTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessType> l = new ArrayList<ProcessType>();
		
		
		// JIRA 781 renommer le Processus long 
		l.add(DescriptionFactory.newProcessType("Dénat, prep FC, dépôt", "illumina-run", ProcessCategory.find.findByCode("sequencing"),getPropertyDefinitionsIlluminaDepotCNG() ,getExperimentTypes("denat-dil-lib","prepa-flowcell","illumina-depot"), 
				getExperimentTypes("denat-dil-lib").get(0), getExperimentTypes("illumina-depot").get(0),getExperimentTypes("ext-to-denat-dil-lib").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
	    // JIRA 781 ajouter un processus court ( sans denat)
		l.add(DescriptionFactory.newProcessType("Prep FC, dépôt", "prepfc-depot", ProcessCategory.find.findByCode("sequencing"),getPropertyDefinitionsIlluminaDepotCNG() ,getExperimentTypes("prepa-flowcell","illumina-depot"), 
				getExperimentTypes("prepa-flowcell").get(0), getExperimentTypes("illumina-depot").get(0),getExperimentTypes("ext-to-prepa-flowcell").get(0), DescriptionFactory.getInstitutes(Institute.CODE.CNG)));

		DAOHelpers.saveModels(ProcessType.class, l, errors);
	}


	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepotCNG() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		/*
		Date prévue (cBot) 	cBotExpectedDate
		Nom du séquenceur	sequencerName
		Position	position		
		Nb lanes	numberOfLanes
		Concentration dilution finale (pM)	finalConcentrationLib
		% PhiX	phixPercentage
		*/
		
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Nom du séquenceur","sequencerName"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, 
						DescriptionFactory.newValues("HISEQ1", "HISEQ2" ,"HISEQ3" ,"HISEQ4" ,"HISEQ5" ,"HISEQ6" ,"HISEQ7" ,"HISEQ8" ,"HISEQ9" ,"HISEQ10" ,"HISEQ11", "MISEQ1","NEXTSEQ1"), "single",150));
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Position","position"
						, LevelService.getLevels(Level.CODE.Process),String.class, false, DescriptionFactory.newValues("A", "B"), "single",200));
		
		/*  JIRA 781 : les proprietes ci dessous ne sont pas retenues...
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Date prévue (cBot)","cBotExpectedDate"
						, LevelService.getLevels(Level.CODE.Process),Date.class, true, "single",100));
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Nb lanes","numberOfLanes"
						, LevelService.getLevels(Level.CODE.Process),Double.class, true, "single",250));		
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Concentration dilution finale","finalConcentrationLib"
						, LevelService.getLevels(Level.CODE.Process),Double.class, true, null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION), MeasureUnit.find.findByCode("pM"), MeasureUnit.find.findByCode("nM"),
						"single",300));
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("% PhiX","phixPercentage"
						, LevelService.getLevels(Level.CODE.Process),Integer.class, true, "single",350));
		//FDS 11-03-2015 =>NGL-356: supression GAIIx, ajout Nextseq, fusion  "Hiseq 2000", "Hiseq 2500 normal"-> "Hiseq 2000/2500N"
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type séquencage","sequencingType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("Hiseq 2000 / 2500 high throughput" , "Hiseq 2500 Fast" , "Miseq" , "Nextseq"), "single",400));
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type de lectures", "readType"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("SR","PE"), "single",450));		
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Longueur de lecture", "readLength"
						, LevelService.getLevels(Level.CODE.Process),String.class, true, DescriptionFactory.newValues("50","100","150","250","300","500","600"), "single",500));		
		*/
		
		return propertyDefinitions;
	}

	
	private static List<ExperimentType> getExperimentTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentType.class,ExperimentType.find, codes);
	}




}
