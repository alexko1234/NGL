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
import models.laboratory.processes.description.ProcessExperimentType; ///TEST FDS !!!
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
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Prep. Librairie", "library"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Normalisation", "normalization"));
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){	
			// 09/03/2016 N'existent pas au CNG
			//l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Sequencage", "pre-sequencing"));
			//l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Pre-Banque", "pre-library"));
		}
		
		DAOHelpers.saveModels(ProcessCategory.class, l, errors);
	}

	public void saveProcessTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessType> l = new ArrayList<ProcessType>();
		// Modif GA=> ListExperimentType doit etre ordonnee=> Arrays.asList( )
		//            par convention les experimentTypes externes aux processus doivent avoir l'indice (-1) dans la methode getPET
		// TODO: il faudra renommer les 'ext' en donnant le nom du processus ex: ext-to-denat-dil-lib---> ext-to-illumina-run
		//       pour distinguer les containers qui arrivent dans le processus sans "fromExperimentType" des containers qui viendraient 
		//       d'un collaborateur exterieur ex : ext-denat-dil-lib
		
		// JIRA 781 renommer le Processus long 
		l.add(DescriptionFactory.newProcessType("Dénat, prep FC, dépôt", "illumina-run", ProcessCategory.find.findByCode("sequencing"),
				getPropertyDefinitionsIlluminaDepotCNG("prepa-flowcell"),
            	Arrays.asList(getPET("ext-to-denat-dil-lib",-1), getPET("lib-normalization",-1), getPET("denat-dil-lib",0),getPET("prepa-flowcell",1),getPET("illumina-depot",2)),// ordered list of experiment type in process type
				getExperimentTypes("denat-dil-lib").get(0),        //first experiment type
				getExperimentTypes("illumina-depot").get(0),       //last  experiment type
				getExperimentTypes("ext-to-denat-dil-lib").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
	   
		// JIRA 781 ajouter un processus court (sans denat)
		l.add(DescriptionFactory.newProcessType("Prep FC, dépôt", "prepFC-depot", ProcessCategory.find.findByCode("sequencing"),
				getPropertyDefinitionsIlluminaDepotCNG("prepa-flowcell"),
				Arrays.asList(getPET("ext-to-prepa-flowcell",-1),getPET("denat-dil-lib",-1),getPET("prepa-flowcell",0),getPET("illumina-depot",1) ), //ordered list of experiment type in process type
				getExperimentTypes("prepa-flowcell").get(0),        //first experiment type
				getExperimentTypes("illumina-depot").get(0),        //last  experiment type
				getExperimentTypes("ext-to-prepa-flowcell").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));

		// FDS ajout 04/11/2015 -- JIRA 838: nouveau processus court prepa-fc-ordonée + illumina-depot
		l.add(DescriptionFactory.newProcessType("4000/X5 (prep FC ordonnée)", "prepFCordered-depot", ProcessCategory.find.findByCode("sequencing"),
				getPropertyDefinitionsIlluminaDepotCNG("prepa-fc-ordered"),
				Arrays.asList(getPET("ext-to-prepa-fc-ordered",-1),getPET("lib-normalization",-1),getPET("prepa-fc-ordered",0),getPET("illumina-depot",1) ), //ordered list of experiment type in process type
				getExperimentTypes("prepa-fc-ordered").get(0),        //first experiment type
				getExperimentTypes("illumina-depot").get(0),          //last  experiment type
				getExperimentTypes("ext-to-prepa-fc-ordered").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		
		// FDS ajout 27/01/2016 -- JIRA NGL-894: processus pour X5	
		l.add(DescriptionFactory.newProcessType("X5_WG PCR free", "x5-wg-pcr-free", ProcessCategory.find.findByCode("library"),
				getPropertyDefinitionsX5WgPcrFree(),
				Arrays.asList(getPET("ext-to-x5-wg-pcr-free",-1),getPET("prep-pcr-free",0),getPET("lib-normalization",1), getPET("prepa-fc-ordered",2), getPET("illumina-depot",3) ), //ordered list of experiment type in process type
				getExperimentTypes("prep-pcr-free").get(0),         //first experiment type
				getExperimentTypes("illumina-depot").get(0),        //last  experiment type
				getExperimentTypes("ext-to-x5-wg-pcr-free").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS ajout  12/04/2016 JIRA NGL-894/981 processus court demarrant a lib-normalization, pas de proprietes
		l.add(DescriptionFactory.newProcessType("X5_norm,FC ord, dépôt", "norm-fc-ordered-depot", ProcessCategory.find.findByCode("normalization"),
				null,
				Arrays.asList(getPET("ext-to-norm-fc-ordered-depot",-1),getPET("prep-pcr-free",-1), getPET("lib-normalization",0), getPET("prepa-fc-ordered",1), getPET("illumina-depot",2) ), //ordered list of experiment type in process type
				getExperimentTypes("lib-normalization").get(0),            //first experiment type
				getExperimentTypes("illumina-depot").get(0),               //last  experiment type
				getExperimentTypes("ext-to-norm-fc-ordered-depot").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));

		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			
			// FDS ajout 31/05/2016 JIRA NGL-1025 2 processus pour RNASeq
			// processus long type "library"
			l.add(DescriptionFactory.newProcessType("RNA Sequencing", "rna-sequencing", ProcessCategory.find.findByCode("library"),
					getPropertyDefinitionsRNAseq(),
					Arrays.asList(getPET("ext-to-rna-sequencing",-1),getPET("rna-prep",0),getPET("pcr-purif",1),getPET("normalization-and-pooling",2), getPET("prepa-fc-ordered",3), getPET("illumina-depot",4) ), //ordered list of experiment type in process type
					getExperimentTypes("rna-prep").get(0),              //first experiment type
					getExperimentTypes("illumina-depot").get(0),        //last  experiment type
					getExperimentTypes("ext-to-rna-sequencing").get(0), //void  experiment type
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			// processus court type "normalization"    pas de proprietes ????????????????????
			l.add(DescriptionFactory.newProcessType("RNA norm+pooling, FC ord, dépot", "norm-and-pool-fc-ord-depot", ProcessCategory.find.findByCode("normalization"),
					null,
					Arrays.asList(getPET("ext-to-norm-and-pool-fc-ord-depot",-1),getPET("normalization-and-pooling",0), getPET("prepa-fc-ordered",1), getPET("illumina-depot",2) ), //ordered list of experiment type in process type
					getExperimentTypes("normalization-and-pooling").get(0),          //first experiment type
					getExperimentTypes("illumina-depot").get(0),                     //last  experiment type
					getExperimentTypes("ext-to-norm-and-pool-fc-ord-depot").get(0),  //void  experiment type
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
				
		}
			
		DAOHelpers.saveModels(ProcessType.class, l, errors);
	}
	
	
	// FDS 09/11/2015  -- JIRA 838 : ajout parametre String pour construire 2 listes differentes
	//                               la liste des sequenceurs est differente pour le processType "4000/X5"
	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepotCNG(String expType) throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		// FDS 04/11/2015 -- JIRA 838 ajout  des HISEQ4000 et HISEQX; utilisation de listes intermediaires...
		List<Value> listSequencers =new ArrayList<Value>();
		
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
	//FDS 31/05/2016 renommer getLibProcessTypeCodeValues pour les distinguer de celles des processus RNA
	private static List<PropertyDefinition> getPropertyDefinitionsX5WgPcrFree() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
	
		// FDS 21/03/2016 ajout d'une propriete avec liste de choix, de niveau content pour quelle soit propagee
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type processus librairie","libProcessTypeCode"
						, LevelService.getLevels(Level.CODE.Process,Level.CODE.Content), String.class, true, "F"
						, getDNALibProcessTypeCodeValues(), "single" ,100, null, null, null));
		
		return propertyDefinitions;
	}

	//FDS 31/05/2016 renommer getLibProcessTypeCodeValues pour les distinguer de celles des processus RNA
	private static List<Value> getDNALibProcessTypeCodeValues(){
        List<Value> values = new ArrayList<Value>();
        
        // dans RunServiceCNG le nom reprend le code...
         values.add(DescriptionFactory.newValue("DA","DA - DNAseq"));
         values.add(DescriptionFactory.newValue("DD","DD - PCR-NANO-DNASeq"));
         
         return values;
	}
	
	//FDS ajout 31/05/2016 pour NGL-1025: processus RNASeq
	private static List<PropertyDefinition> getPropertyDefinitionsRNAseq() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
	
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type processus librairie","libProcessTypeCode"
						, LevelService.getLevels(Level.CODE.Process,Level.CODE.Content), String.class, true, "F"
						, getRNALibProcessTypeCodeValues(), "single" ,100, null, null, null));
		
		return propertyDefinitions;
	}
	
	private static List<Value> getRNALibProcessTypeCodeValues(){
        List<Value> values = new ArrayList<Value>();
        
        // dans RunServiceCNG le nom reprend le code...
        /// ????? question a Julie==>  et les autres codes  RA, RB, RC ???????
         values.add(DescriptionFactory.newValue("RD","RD - ssmRNASeq"));       //single stranded messenger RNA sequencing
         values.add(DescriptionFactory.newValue("RE","RE - sstRNASeq"));       //single stranded total RNA sequencing
         values.add(DescriptionFactory.newValue("RF","RF - sstRNASeqGlobin")); //single stranded total RNA from blood sequencing
         
         return values;
	}
}