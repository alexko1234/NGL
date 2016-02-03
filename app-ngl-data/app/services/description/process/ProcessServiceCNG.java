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
import models.laboratory.instrument.description.Instrument;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.instrument.InstrumentServiceCNG;

import com.typesafe.config.ConfigFactory;

public class ProcessServiceCNG  extends AbstractProcessService{


	public void saveProcessCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessCategory> l = new ArrayList<ProcessCategory>();
		
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Sequençage", "sequencing"));		
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Banque", "library"));// FDS ajout 27/01/2016: JIRA-NGL 894
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){	
			l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Sequencage", "pre-sequencing"));
			l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Banque", "pre-library"));
		}
		
		// FDS c'est ici qu'il faut la sauvegarde!!
		DAOHelpers.saveModels(ProcessCategory.class, l, errors);
	}

	public void saveProcessTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessType> l = new ArrayList<ProcessType>();
		
		// JIRA 781 renommer le Processus long 
		l.add(DescriptionFactory.newProcessType("Dénat, prep FC, dépôt", "illumina-run", ProcessCategory.find.findByCode("sequencing"),
				getPropertyDefinitionsIlluminaDepotCNG("prepa-flowcell") ,
				Arrays.asList(getPET("ext-to-denat-dil-lib",-1),getPET("lib-normalization",-1),getPET("denat-dil-lib",0),getPET("prepa-flowcell",1),getPET("illumina-depot",2)), 
				getExperimentTypes("denat-dil-lib").get(0), 
				getExperimentTypes("illumina-depot").get(0),
				getExperimentTypes("ext-to-denat-dil-lib").get(0), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
	   
		// JIRA 781 ajouter un processus court (sans denat)
		l.add(DescriptionFactory.newProcessType("Prep FC, dépôt", "prepFC-depot", ProcessCategory.find.findByCode("sequencing"),
				getPropertyDefinitionsIlluminaDepotCNG("prepa-flowcell") ,
				Arrays.asList(getPET("ext-to-prepa-flowcell",-1),getPET("denat-dil-lib",-1),getPET("prepa-flowcell",0),getPET("illumina-depot",1)), 
				getExperimentTypes("prepa-flowcell").get(0), 
				getExperimentTypes("illumina-depot").get(0),
				getExperimentTypes("ext-to-prepa-flowcell").get(0), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));

		// FDS ajout 04/11/2015 -- JIRA 838: nouveau processus court prepa-fc-ordonée + illumina-depot
		l.add(DescriptionFactory.newProcessType("4000/X5 (prep FC ordonnée)", "prepFCordered-depot", ProcessCategory.find.findByCode("sequencing"),
				getPropertyDefinitionsIlluminaDepotCNG("prepa-fc-ordered") ,
				Arrays.asList(getPET("ext-to-prepa-fc-ordered",-1),getPET("lib-normalization",-1),getPET("prepa-fc-ordered",0),getPET("illumina-depot",1)), 
				getExperimentTypes("prepa-fc-ordered").get(0),
				getExperimentTypes("illumina-depot").get(0),
				getExperimentTypes("ext-to-prepa-fc-ordered").get(0), 
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS ajout 27/01/2016 -- JIRA NGL-894: nouveau processus pour X5
		l.add(DescriptionFactory.newProcessType("X5_WG PCR free", "x5-wg-pcr-free", ProcessCategory.find.findByCode("library"),
				getPropertyDefinitionsX5WgPcrFree(),
				getExperimentTypes("prep-pcr-free","lib-normalization","prepa-fc-ordered","illumina-depot"), // list of experiment type in process type 
				getExperimentTypes("prep-pcr-free").get(0),        //first experiment type
				getExperimentTypes("illumina-depot").get(0),       //last experiment type
				getExperimentTypes("ext-to-prep-pcr-free").get(0), //void experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		DAOHelpers.saveModels(ProcessType.class, l, errors);
	}
	
	
	// FDS 09/11/2015  -- JIRA 838 : ajout parametre String pour construire 2 listes differentes
	//                               la liste des sequenceurs est differente pour le processType "4000/X5"
	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepotCNG(String expType) throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		// FDS 04/11/2015 -- JIRA 838 ajout  des HISEQ4000 et HISEQX; utilisation de listes intermediaires...
		List<Value> listSequencers =new ArrayList<Value>();
		/*
		List<Instrument> instruments = InstrumentServiceCNG.getInstrumentHiseq2000();
		for(Instrument instrument: instruments){
			listSequencers.add(DescriptionFactory.newValue(instrument.code, instrument.name));
		}
		*/
		
		if ( expType.equals("prepa-flowcell")) {
			// HISEQ2000
			listSequencers.addAll(DescriptionFactory.newValues("HISEQ1", "HISEQ2" , "HISEQ3" , "HISEQ4" ,"HISEQ5" ,"HISEQ6" ,"HISEQ7" ,"HISEQ8"));
			// HISEQ2500
			listSequencers.addAll(DescriptionFactory.newValues("HISEQ9", "HISEQ10", "HISEQ11"));
			// MISEQ
			listSequencers.addAll(DescriptionFactory.newValues("MISEQ1", "MISEQ2"));
			// NEXTSEQ500
			listSequencers.addAll(DescriptionFactory.newValues("NEXTSEQ1"));	
		}
		else if  ( expType.equals("prepa-fc-ordered")) {
			// HISEQX
			listSequencers.addAll(DescriptionFactory.newValues("ASTERIX","DIAGNOSTIX","IDEFIX","OBELIX","PANORAMIX"));		
			// HISEQ4000
			listSequencers.addAll(DescriptionFactory.newValues("FALBALA"));
		}
	

		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Nom du séquenceur","sequencerName",
						LevelService.getLevels(Level.CODE.Process),String.class, true, listSequencers, "single",150));
		
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

	//FDS ajout 28/01/2016 -- JIRA NGL-894: nouveau processus pour X5
	private static List<PropertyDefinition> getPropertyDefinitionsX5WgPcrFree() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
	
		//TODO 
		// propertyDefinitions.add(........);
		
		//il y a illumina-depot dans la liste des experiments de ce processus, 
		// ==> faut-il appeler  getPropertyDefinitionsIlluminaDepotCNG  ????
		//     dans la spec pour l'instant(29/01/2016) : PAS DE PRORIETE DE PROCESSUS"
		
		return propertyDefinitions;
	}

	
	private static List<ExperimentType> getExperimentTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentType.class,ExperimentType.find, codes);
	}
}