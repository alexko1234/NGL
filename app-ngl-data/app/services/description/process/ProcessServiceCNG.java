package services.description.process;

import static services.description.DescriptionFactory.newExperimentTypeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;// ajout FDS pour getPETForQCTransfertPurif

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.description.ProcessExperimentType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.instrument.InstrumentServiceCNG;
import services.description.declaration.cng.Nanopore;

import com.typesafe.config.ConfigFactory;

public class ProcessServiceCNG  extends AbstractProcessService{

	/**
	 * Save all Process Categories
	 * @param errors
	 * @throws DAOException 
	 */
	public void saveProcessCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessCategory> l = new ArrayList<ProcessCategory>();
		
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Prep. Lib. Illumina", "library"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Prep. Lib. Nanopore", "nanopore-library"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Normalisation", "normalization"));
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Sequençage", "sequencing"));
		// 28/11/2016 fdsanto JIRA NGL-1164; categorie de processus ne contenant aucune transformation mais uniquement des QC ou transferts...
		//  attention bug connu: manque la puce "terminer" dans le dispatch final
		l.add(DescriptionFactory.newSimpleCategory(ProcessCategory.class, "Exp satellites", "satellites"));

		DAOHelpers.saveModels(ProcessCategory.class, l, errors);
	}

	/**
	 * Save all Process types
	 * @param errors
	 * @throws DAOException 		
	 * warning "codes" must not have uppercase letters
	 */
	public void saveProcessTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProcessType> l = new ArrayList<ProcessType>();
		// par convention les experimentTypes externes aux processus doivent avoir l'indice (-1) dans la methode getPET
		// TODO: il faudra renommer les 'ext' en donnant le nom du processus ex: ext-to-denat-dil-lib---> ext-to-illumina-run
		//       pour distinguer les containers qui arrivent dans le processus sans "fromExperimentType" des containers qui viendraient 
		//       d'un collaborateur exterieur ex : ext-denat-dil-lib
		
		// FDS ajout 27/01/2016 -- JIRA NGL-894: processus pour X5; chgt label 16/09/2016
		l.add(DescriptionFactory.newProcessType("WG PCR free (FC ordonnée)", "x5-wg-pcr-free", ProcessCategory.find.findByCode("library"),
				1,
				getPropertyDefinitionsX5WgPcrFree(), 
				Arrays.asList(
						getPET("ext-to-x5-wg-pcr-free",-1), //ordered list of experiment type in process type
						getPET("prep-pcr-free",0),
						getPET("lib-normalization",1), 
						getPET("normalization-and-pooling",1), // ajout 06/09/2017 (NGL-1576)
						getPET("prepa-fc-ordered",2), 
						getPET("illumina-depot",3) ),         
				getExperimentTypes("prep-pcr-free").get(0),         //first experiment type    
				getExperimentTypes("illumina-depot").get(0),        //last  experiment type
				getExperimentTypes("ext-to-x5-wg-pcr-free").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS ajout 10/08/2016 JIRA NGL-1047 processus X5_WG NANO; mise en prod 1/09/2016; chgt label 16/06/2016
		// 26/09/2016 modif commence par ("prep-wg-nano",0)
		l.add(DescriptionFactory.newProcessType("WG NANO (FC ordonnée)", "x5-wg-nano", ProcessCategory.find.findByCode("library"),
				2, 
				getPropertyDefinitionsX5WgNanoDNAseq(), 
				Arrays.asList(
						getPET("ext-to-x5-wg-nano",-1), //ordered list of experiment type in process type
						getPET("prep-wg-nano",0),
						getPET("pcr-and-purification",1), 
						getPET("lib-normalization",2),
						getPET("normalization-and-pooling",2), // ajout 06/09/2017 (NGL-1576)
						getPET("prepa-fc-ordered",3), 
						getPET("illumina-depot",4) ),      
				getExperimentTypes("prep-wg-nano").get(0),      //first experiment type;
				getExperimentTypes("illumina-depot").get(0),    //last  experiment type
				getExperimentTypes("ext-to-x5-wg-nano").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));		
			
		// FDS 13/03/2017 NGL-1167 ajout "wg-chromium-lib-prep" en -1
		// FDS ajout 12/04/2016 JIRA NGL-894/981 : processus court demarrant a lib-normalization, pas de proprietes; chgt label 15/09/2016; chgt label 06/09/2017 (NGL-1576)
		l.add(DescriptionFactory.newProcessType("Norm(+pooling),FC ordonnée, dépôt", "norm-fc-ordered-depot", ProcessCategory.find.findByCode("normalization"),
				20,
				null,  // pas de propriétés ??
				Arrays.asList(
						getPET("ext-to-norm-fc-ordered-depot",-1), //ordered list of experiment type in process type
						getPET("prep-pcr-free",-1), 
						getPET("pcr-and-purification",-1), 
						getPET("wg-chromium-lib-prep",-1),
						getPET("lib-normalization",0), 
						getPET("normalization-and-pooling",0),
						getPET("prepa-fc-ordered",1), 
						getPET("illumina-depot",2) ),           
				getExperimentTypes("lib-normalization").get(0),            //first experiment type
				getExperimentTypes("illumina-depot").get(0),               //last  experiment type
				getExperimentTypes("ext-to-norm-fc-ordered-depot").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));			
		
		// FDS modif 28/10/2016 NGL-1025: renommage en "2000/2500_Dénat, prep FC, dépôt"
		l.add(DescriptionFactory.newProcessType("2000/2500_Dénat, prep FC, dépôt", "illumina-run", ProcessCategory.find.findByCode("sequencing"),
				40,
		        getPropertyDefinitionsIlluminaDepotCNG("prepa-flowcell"),
				Arrays.asList(
						getPET("ext-to-denat-dil-lib",-1), // ordered list of experiment type in process type
		            	getPET("lib-normalization",-1), 
		            	getPET("normalization-and-pooling",-1),	
		            	getPET("denat-dil-lib",0),
		            	getPET("prepa-flowcell",1),
		            	getPET("illumina-depot",2)),        
				getExperimentTypes("denat-dil-lib").get(0),         //first experiment type
				getExperimentTypes("illumina-depot").get(0),        //last  experiment type
				getExperimentTypes("ext-to-denat-dil-lib").get(0),  //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS 20/06/2017 NE PAS LIVRER EN PRODUCTION le processus modifié pour "2000/2500" car la normalisation en plaque n'est pas gérée pour l'instant...
		if (ConfigFactory.load().getString("ngl.env").equals("TODO-LATER??") ){
		// FDS 02/06/2017: NGL-1447 =>  duplication  "2000/2500_Prep FC, dépôt" avec tranfert en experience de niveau 0
		// => il faut declarer ce noeud 0 dans experimentService !!	
		l.add(DescriptionFactory.newProcessType("Transfert puis 2000/2500_Dénat, prep FC, dépôt", "tf-illumina-run", ProcessCategory.find.findByCode("sequencing"),
				41,
		        getPropertyDefinitionsIlluminaDepotCNG("prepa-flowcell"),
		        Arrays.asList(
						getPET("ext-to-denat-dil-lib",-1), // ordered list of experiment type in process type
		            	getPET("lib-normalization",-1), 
		            	getPET("normalization-and-pooling",-1),  	
		            	getPET("tubes-to-plate",0),
		            	getPET("denat-dil-lib",0),
		            	getPET("prepa-flowcell",1),
		            	getPET("illumina-depot",2)),          
				getExperimentTypes("tubes-to-plate").get(0),         //first experiment type
				getExperimentTypes("illumina-depot").get(0),        //last  experiment type
				getExperimentTypes("ext-to-denat-dil-lib").get(0),  //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));			
		}
		

		l.add(DescriptionFactory.newProcessType("2000/2500_Prep FC, dépôt", "prepfc-depot", ProcessCategory.find.findByCode("sequencing"),
				42,
				getPropertyDefinitionsIlluminaDepotCNG("prepa-flowcell"),
				Arrays.asList(
						getPET("ext-to-prepa-flowcell",-1), //ordered list of experiment type in process type
						getPET("denat-dil-lib",-1),
						getPET("prepa-flowcell",0),
						getPET("illumina-depot",1) ),        
				getExperimentTypes("prepa-flowcell").get(0),        //first experiment type
				getExperimentTypes("illumina-depot").get(0),        //last  experiment type
				getExperimentTypes("ext-to-prepa-flowcell").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
	
		l.add(DescriptionFactory.newProcessType("4000/X5 (prep FC ordonnée)", "prepfcordered-depot", ProcessCategory.find.findByCode("sequencing"),
				43,
				getPropertyDefinitionsIlluminaDepotCNG("prepa-fc-ordered"), 
				Arrays.asList(
						getPET("ext-to-prepa-fc-ordered",-1), //ordered list of experiment type in process type
						getPET("lib-normalization",-1),  
						getPET("normalization-and-pooling",-1),
						getPET("prepa-fc-ordered",0),
						getPET("illumina-depot",1) ),        
				getExperimentTypes("prepa-fc-ordered").get(0),        //first experiment type
				getExperimentTypes("illumina-depot").get(0),          //last  experiment type
				getExperimentTypes("ext-to-prepa-fc-ordered").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
			
		// FDS 02/06/2017: NGL-1447 =>  duplication 4000/X5 (prep FC ordonnée) avec tranfert en experience de niveau 0
		// => il faut declarer ce noeud 0 dans experimentService !!
		l.add(DescriptionFactory.newProcessType("Transfert puis 4000/X5 (prep FC ordonnée)", "tf-prepfcordered-depot", ProcessCategory.find.findByCode("sequencing"),
				44,
				getPropertyDefinitionsIlluminaDepotCNG("prepa-fc-ordered"), 
				Arrays.asList(
						getPET("ext-to-prepa-fc-ordered",-1), //ordered list of experiment type in process type
						getPET("lib-normalization",-1),  
						getPET("normalization-and-pooling",-1),
						getPET("tubes-to-plate",0),
						getPET("prepa-fc-ordered",0),
						getPET("illumina-depot",1) ),  
				getExperimentTypes("tubes-to-plate").get(0),          //first experiment type
				getExperimentTypes("illumina-depot").get(0),          //last  experiment type
				getExperimentTypes("ext-to-prepa-fc-ordered").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	

		// FDS ajout 31/05/2016 JIRA NGL-1025: processus long type "library"; 18/01/2017 JIRA NGL-1259 renommer en rna-lib-process
		l.add(DescriptionFactory.newProcessType("Prep lib RNAseq", "rna-lib-process", ProcessCategory.find.findByCode("library"),
				3,
				getPropertyDefinitionsRNAlib(), 
				Arrays.asList(
						getPET("ext-to-rna-lib-process",-1), //ordered list of experiment type in process type
						getPET("library-prep",0),
						getPET("pcr-and-purification",1),
						getPET("normalization-and-pooling",2) , 
						getPET("lib-normalization",2) ), // FDS 16/11/2016 ajout d'une 2eme exp de niveau 2 
				getExperimentTypes("library-prep").get(0),              //first experiment type
				getExperimentTypes("normalization-and-pooling").get(0), //last  experiment type (1 des 2 qui sont de niveau le + élevé)
				getExperimentTypes("ext-to-rna-lib-process").get(0),    //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
        // FDS ajout 28/10/2016 JIRA NGL-1025: nouveau processus court pour RNAseq; modif du label=> supprimer RNA;chgt label (NGL-1576)
		l.add(DescriptionFactory.newProcessType("Norm(+pooling), dénat, FC, dépot", "norm-and-pool-denat-fc-depot", ProcessCategory.find.findByCode("normalization"),
				21,   
				null, // pas de propriétés ??
				Arrays.asList(
						getPET("ext-to-norm-and-pool-denat-fc-depot",-1), //ordered list of experiment type in process type
						getPET("pcr-and-purification", -1),	
						getPET("normalization-and-pooling",0), 
						getPET("lib-normalization",0),
						getPET("denat-dil-lib",1),
						getPET("prepa-flowcell",2),
						getPET("illumina-depot",3) ),          
				getExperimentTypes("normalization-and-pooling").get(0),           //first experiment type         
				getExperimentTypes("illumina-depot").get(0),                      //last  experiment type
				getExperimentTypes("ext-to-norm-and-pool-denat-fc-depot").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
			
		// FDS ajout 28/11/2016 JIRA NGL-1164: nouveau processus pour "QC / TF / Purif "  (sans transformation)
		// FDS 10/10/2017 NGL-1625 renommer et utiliser getPETForTransfertQCPurif
		l.add(DescriptionFactory.newProcessType("Transfert puis satellites", "transfert-qc-purif", ProcessCategory.find.findByCode("satellites"), 
				60,
				null, // pas de propriétés ??  
				getPETForTransfertQCPurif(),
				getExperimentTypes("pool").get(0),                       //first experiment type ( 1 transfert n'importe lequel...)
				getExperimentTypes("ext-to-transfert-qc-purif").get(0),  //last  experiment type ( doit etre la ext-to....)
				getExperimentTypes("ext-to-transfert-qc-purif").get(0),  //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS 10/10/2017 ajout NGL-1625
		l.add(DescriptionFactory.newProcessType("QC puis satellites", "qc-transfert-purif", ProcessCategory.find.findByCode("satellites"), 
				70,
				null, // pas de propriétés ??  
				getPETForQCTransfertPurif(),
				getExperimentTypes("labchip-migration-profile").get(0),  //first experiment type ( 1 qc n'importe lequel..)
				getExperimentTypes("ext-to-qc-transfert-purif").get(0),  //last  experiment type ( doit etre la ext-to....)
				getExperimentTypes("ext-to-qc-transfert-purif").get(0),  //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS ajout 20/02/2017 NGL-1167: processus Chromium 10x WG
		// FDS modification 13/06/2017 NGL-1473: allongement du processus-> illumina depot (renommage ..FC ordonnée)
		l.add(DescriptionFactory.newProcessType("Prep Chromium WG (FC ordonnée)", "wg-chromium-lib-process", ProcessCategory.find.findByCode("library"),
				4,
				getPropertyDefinitionsWgChromium(), 
				Arrays.asList(
						getPET("ext-to-wg-chromium-lib-process",-1), //ordered list of experiment type in process type
						getPET("chromium-gem-generation",0),
						getPET("wg-chromium-lib-prep",1), 
						getPET("lib-normalization",2),
						getPET("normalization-and-pooling",2),
						getPET("prepa-fc-ordered",3),
						getPET("illumina-depot",4)), 			
				getExperimentTypes("chromium-gem-generation").get(0),        //first experiment type    
				getExperimentTypes("illumina-depot").get(0),                 //last  experiment type
				getExperimentTypes("ext-to-wg-chromium-lib-process").get(0), //void  experiment type
				DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
		
		// FDS ajout 03/03/2017 NGL-1225: processus Nanopore DEV
		l.addAll(new Nanopore().getProcessType());
		
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
	//FDS 10/08/2016 renommer  en getX5WgPcrFreeLibProcessTypeCodeValues 
	private static List<PropertyDefinition> getPropertyDefinitionsX5WgPcrFree() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
	
		// FDS 21/03/2016 ajout d'une propriete avec liste de choix, de niveau content pour quelle soit propagee
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type processus librairie","libProcessTypeCode"
						, LevelService.getLevels(Level.CODE.Process,Level.CODE.Content), String.class, true, "F"
						, getX5WgPcrFreeLibProcessTypeCodeValues(), "single" ,100, null, null, null));

		// FDS 27/10/2016 NGL-1025: ajout expectedCoverage: optionnel, editable, pas de defaut, de niveau content pour quelle soit propagee
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Couverture souhaitée","expectedCoverage"
						, LevelService.getLevels(Level.CODE.Process,Level.CODE.Content), String.class, false, "F"
						, null, "single" ,101, true, null, null));
		
		// FDS 23/11/2016 SUPSQCNG-424 : ajout sequencingType optionnelle avec liste de choix,  niveau process uniquement
		// FDS 02/08/2017 NGL-1543 la fonction getX5WgPcrFreeSequencingTypes a introduit des incohérences de codes...=> utiliser newValues
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type de séquencage","sequencingType"
						, LevelService.getLevels(Level.CODE.Process), String.class, false, "F"
						, DescriptionFactory.newValues("Hiseq 4000","Hiseq X"), "single" ,102, null, null, null));
		
		// FDS 18/01/2017 JIRA NGL-1259 ajout plateWorkLabel: optionnel,niveau process uniquement
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Nom de travail plaque","plateWorkLabel"
						, LevelService.getLevels(Level.CODE.Process), String.class, false, "F"
						, null, "single" ,103, true, null, null));
		
		// FDS 18/01/2017 JIRA NGL-1259 ajout ngsRunWorkLabel: optionnel,niveau process uniquement
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Nom de travail run NGS","ngsRunWorkLabel"
						, LevelService.getLevels(Level.CODE.Process), String.class, false, "F"
						, null, "single" ,104, true, null, null));
		
		return propertyDefinitions;
	}

	//FDS 10/08/2016 renommer en getX5WgPcrFreeLibProcessTypeCodeValues
	private static List<Value> getX5WgPcrFreeLibProcessTypeCodeValues(){
        List<Value> values = new ArrayList<Value>();
        
        // dans RunServiceCNG le nom reprend le code...
        values.add(DescriptionFactory.newValue("DA","DA - DNAseq"));
         
        return values;
	}
	
	/* FDS 02/08/2017 NGL-1543 cette fonction a introduite des incohérences de codes supprimer...  
	private static List<Value> getX5WgPcrFreeSequencingTypes(){
        List<Value> values = new ArrayList<Value>();
        
         values.add(DescriptionFactory.newValue("RHS4000","Hiseq 4000"));
         values.add(DescriptionFactory.newValue("RHSX","Hiseq X"));
         
        return values;
	}
	*/
	
	//FDS ajout 31/05/2016 pour JIRA NGL-1025: processus RNASeq; 18/01/2017 remommer en getPropertyDefinitionsRNAseq=> getPropertyDefinitionsRNAlib
	private static List<PropertyDefinition> getPropertyDefinitionsRNAlib() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
	
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type processus librairie","libProcessTypeCode"
						, LevelService.getLevels(Level.CODE.Process,Level.CODE.Content), String.class, true, "F"
						, getRNALibProcessTypeCodeValues(), "single" ,100, null, null, null));

		// FDS 27/10/2016 NGL-1025: ajout expectedCoverage: : optionnel, editable, pas de defaut
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Couverture souhaitée","expectedCoverage"
						, LevelService.getLevels(Level.CODE.Process,Level.CODE.Content), String.class, false, "F"
						, null, "single" ,101, true, null, null));
		
		// FDS 18/01/2017 JIRA NGL-1259 ajout plateWorkLabel: optionnel,niveau process uniquement
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Nom de travail plaque","plateWorkLabel"
						, LevelService.getLevels(Level.CODE.Process), String.class, false, "F"
						, null, "single" ,102, true, null, null));
		
		// FDS 18/01/2017 JIRA NGL-1259 ajout ngsRunWorkLabel: optionnel,niveau process uniquement
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Nom de travail run NGS","ngsRunWorkLabel"
						, LevelService.getLevels(Level.CODE.Process), String.class, false, "F"
						, null, "single" ,103, true, null, null));
		
		return propertyDefinitions;
	}
	
	private static List<Value> getRNALibProcessTypeCodeValues(){
        List<Value> values = new ArrayList<Value>();
        
        // dans RunServiceCNG le nom reprend le code...
        values.add(DescriptionFactory.newValue("RD","RD - ssmRNASeq"));       //single stranded messenger RNA sequencing
        values.add(DescriptionFactory.newValue("RE","RE - sstRNASeq"));       //single stranded total RNA sequencing
        values.add(DescriptionFactory.newValue("RF","RF - sstRNASeqGlobin")); //single stranded total RNA from blood sequencing
        values.add(DescriptionFactory.newValue("RG","RG - mRNASeq"));         //messenger RNA sequencing
        values.add(DescriptionFactory.newValue("RH","RH - sstRNASeqGold"));   //single stranded total RNA Gold
        
        return values;
	}
	
	//FDS ajout 10/08/2016 pour JIRA NGL-1047: processus X5_WG NANO
	private static List<PropertyDefinition> getPropertyDefinitionsX5WgNanoDNAseq() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
	
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Type processus librairie","libProcessTypeCode"
						, LevelService.getLevels(Level.CODE.Process,Level.CODE.Content), String.class, true, "F"
						, getX5WgNanoLibProcessTypeCodeValues(), "single" ,100, null, null, null));
		
		// FDS 27/10/2016 NGL-1025: ajout expectedCoverage: : optionnel, editable, pas de defaut
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Couverture souhaitée","expectedCoverage"
						, LevelService.getLevels(Level.CODE.Process,Level.CODE.Content), String.class, false, "F"
						, null, "single" ,101, true, null, null));
		
		// FDS 18/01/2017 JIRA NGL-1259 ajout plateWorkLabel: optionnel,niveau process uniquement
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Nom de travail plaque","plateWorkLabel"
						, LevelService.getLevels(Level.CODE.Process), String.class, false, "F"
						, null, "single" ,102, true, null, null));
		
		// FDS 18/01/2017 JIRA NGL-1259 ajout ngsRunWorkLabel
		propertyDefinitions.add(
				DescriptionFactory.newPropertiesDefinition("Nom de travail run NGS","ngsRunWorkLabel"
						, LevelService.getLevels(Level.CODE.Process), String.class, false, "F"
						, null, "single" ,103, true, null, null));
		
		return propertyDefinitions;
	}
	
	private static List<Value> getX5WgNanoLibProcessTypeCodeValues(){
        List<Value> values = new ArrayList<Value>();
        
        // dans RunServiceCNG le nom reprend le code...
        values.add(DescriptionFactory.newValue("DD","DD - PCR-NANO DNASeq"));   
         
        return values;
	}
	
	// FDS ajout 20/02/2017 NGL-1167
	private static List<PropertyDefinition> getPropertyDefinitionsWgChromium() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(
					DescriptionFactory.newPropertiesDefinition("Type processus librairie","libProcessTypeCode"
							, LevelService.getLevels(Level.CODE.Process, Level.CODE.Content), String.class, true, "F"
							, getWgChromiumLibProcessTypeCodeValues(), "single" ,100, null, null, null));

		 return propertyDefinitions;
	}
	
	// FDS ajout 20/02/2017 NGL-1167
	private static List<Value> getWgChromiumLibProcessTypeCodeValues(){
        List<Value> values = new ArrayList<Value>();
        
        // dans RunServiceCNG le nom reprend le code...
        values.add(DescriptionFactory.newValue("DE","DE - Chromium WG"));   
         
        return values;
	}
	
	// FDS 10/10/2017 duplication NGL-1625
	// toutes les transformation en -1
	// ext-to-qc-transfert-purif" en -1
	// 1 transfert n'importe lequel ???? : pool en 0
	private List<ProcessExperimentType> getPETForTransfertQCPurif(){
		List<ProcessExperimentType> pets = ExperimentType.find.findByCategoryCode("transformation")
			.stream()
			.map(et -> getPET(et.code, -1))
			.collect(Collectors.toList());

		pets.add(getPET("ext-to-transfert-qc-purif",-1));
		pets.add(getPET("pool",0));

		return pets;		
	}
	
	// toutes les transformation en -1
	// ext-to-qc-transfert-purif" en -1
	// 1 qc n'importe lequel ?????: labchip-migration-profile en 0 
	private List<ProcessExperimentType> getPETForQCTransfertPurif(){
		List<ProcessExperimentType> pets = ExperimentType.find.findByCategoryCode("transformation")
			.stream()
			.map(et -> getPET(et.code, -1))
			.collect(Collectors.toList());

		pets.add(getPET("ext-to-qc-transfert-purif",-1));
		pets.add(getPET("labchip-migration-profile",0));

		return pets;		
	}
	
}