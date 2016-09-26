package services.description.experiment;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;

import com.typesafe.config.ConfigFactory;

public class ExperimentServiceCNG extends AbstractExperimentService{
	
	
	@SuppressWarnings("unchecked")
	public void saveProtocolCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProtocolCategory> l = new ArrayList<ProtocolCategory>();
		l.add(DescriptionFactory.newSimpleCategory(ProtocolCategory.class, "Developpement", "development"));
		l.add(DescriptionFactory.newSimpleCategory(ProtocolCategory.class, "Production", "production"));
		DAOHelpers.saveModels(ProtocolCategory.class, l, errors);
	}
	
	/**
	 * Save all Experiment Categories
	 * @param errors
	 * @throws DAOException 
	 */
	public  void saveExperimentCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ExperimentCategory> l = new ArrayList<ExperimentCategory>();
		
		l.add(DescriptionFactory.newSimpleCategory(ExperimentCategory.class, "Purification", ExperimentCategory.CODE.purification.name()));
		l.add(DescriptionFactory.newSimpleCategory(ExperimentCategory.class, "Control qualité", ExperimentCategory.CODE.qualitycontrol.name()));
		l.add(DescriptionFactory.newSimpleCategory(ExperimentCategory.class, "Transfert", ExperimentCategory.CODE.transfert.name()));
		l.add(DescriptionFactory.newSimpleCategory(ExperimentCategory.class, "Transformation", ExperimentCategory.CODE.transformation.name()));
		l.add(DescriptionFactory.newSimpleCategory(ExperimentCategory.class, "Void process", ExperimentCategory.CODE.voidprocess.name()));
		
		DAOHelpers.saveModels(ExperimentCategory.class, l, errors);
	}

	/**
	 * Save all Experiment Types
	 * @param errors
	 * @throws DAOException 
	 */
	public void saveExperimentTypes(Map<String, List<ValidationError>> errors) throws DAOException {
			List<ExperimentType> l = new ArrayList<ExperimentType>();
			
			/** voidprocess: ext-to-**  display order -1 **/
			
			l.add(newExperimentType("Ext to prepa flowcell","ext-to-prepa-flowcell",null,-1,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), 
					null, 
					null,
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			l.add(newExperimentType("Ext to prepa flowcell ordered","ext-to-prepa-fc-ordered",null,-1,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), 
					null, 
					null,
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			l.add(newExperimentType("Ext to librairie dénaturée","ext-to-denat-dil-lib",null,-1,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), 
					null, 
					null,
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			l.add(newExperimentType("Ext to X5_WG PCR free","ext-to-x5-wg-pcr-free",null,-1,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()),
					null, 
					null ,
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			l.add(newExperimentType("Ext to X5_norm,FC ord, dépôt","ext-to-norm-fc-ordered-depot",null,-1,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()),
					null, 
					null ,
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			//FDS 10/08/2016 ajout -- JIRA NGL-1047: processus X5_WG NANO; mise en prod 01/09/2016
			l.add(newExperimentType("Ext to X5_WG NANO","ext-to-x5-wg-nano",null,-1,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()),
					null, 
					null ,
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));		
			
			
			/************************************ DEV / UAT ONLY **********************************************/
			if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
				//FDS 31/05/2016 ajout -- JIRA NGL-1025: processus et experiments pour RNASeq 

				l.add(newExperimentType("Ext to RNASeq","ext-to-rna-sequencing",null,-1,
						ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()),
						null, 
						null ,
						"OneToOne", 
						DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
				
				l.add(newExperimentType("Ext to RNA norm+pool,FC ord, dépôt","ext-to-norm-and-pool-fc-ord-depot",null,-1,
						ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()),
						null, 
						null ,
						"OneToOne", 
						DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			}
				

			
			
			/** Transformation, ordered by display order **/
			
			//FDS 01/02/2016 ajout -- JIRA NGL-894: experiments pour X5
			l.add(newExperimentType("Prep. PCR free","prep-pcr-free",null,500,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),
					getPropertyDefinitionsPrepPcrFree_WgNano(), 
					getInstrumentUsedTypes("covaris-e210-and-sciclone-ngsx","covaris-le220-and-sciclone-ngsx","covaris-e220-and-sciclone-ngsx"),
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			//commun X5_WG NANO et RNAseq; mise prod 01/09/2016
			l.add(newExperimentType("PCR+purification","pcr-and-purification",null,700,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),
					getPropertyDefinitionsPcrAndPurification(),
					getInstrumentUsedTypes("mastercycler-epg-and-zephyr"),
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
		/************************************ DEV / UAT ONLY **********************************************/
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			//FDS 31/05/2016 ajout -- JIRA NGL-1025: processus et experiments pour RNASeq 
			//FDS 10/08/2016 ajout -- JIRA NGL-1047: processus X5_WG NANO 	
			//l.add(newExperimentType("Prep. RNASeq","prep-rna-sequencing",null,600,
			l.add(newExperimentType("Prep. Librairie (sans frg)","library-prep",null,600,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),
					getPropertyDefinitionsLibraryPrep(),
					getInstrumentUsedTypes("sciclone-ngsx"),
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
				
			l.add(newExperimentType("Normalisation+Pooling","normalization-and-pooling",null,800,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),
					getPropertyDefinitionsNormalizationAndPooling(), 
					getInstrumentUsedTypes("janus"),
					"ManyToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));	
		}
		
		    // dupliquer experience prep-pcr-free en prep-wg-nano; prod 26/09/206
		    l.add(newExperimentType("Prep. WG Nano","prep-wg-nano",null,500,
				   ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),
				   getPropertyDefinitionsPrepPcrFree_WgNano(), 
				   getInstrumentUsedTypes("covaris-e210-and-sciclone-ngsx","covaris-le220-and-sciclone-ngsx","covaris-e220-and-sciclone-ngsx"),
				   "OneToOne", 
				   DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			l.add(newExperimentType("Librairie normalisée","lib-normalization",null,900,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), 
					getPropertyDefinitionsLibNormalization(),
					getInstrumentUsedTypes("hand","janus"), 
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));			
			
			l.add(newExperimentType("Dénaturation-dilution","denat-dil-lib",null,1000,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), 
					getPropertyDefinitionsDenatDilLibCNG(),
					getInstrumentUsedTypes("hand"),
					"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
					
			l.add(newExperimentType("Préparation flowcell","prepa-flowcell",null,1200,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), 
					getPropertyDefinitionsPrepaflowcellCNG(),
					getInstrumentUsedTypes("cBot", "cBot-onboard"),"ManyToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			//FDS ajout 04/11/2015 -- JIRA NGL-838: ajout prepa-fc-ordered, attention pas cBot-onboard
			//FDS modif 29/03/2016 -- JIRA NGL-893: ajout instrument janus-and-cBot
			l.add(newExperimentType("Prép. flowcell ordonnée","prepa-fc-ordered",null,1300,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), 
					getPropertyDefinitionsPrepaflowcellOrderedCNG(),
					getInstrumentUsedTypes("cBot","janus-and-cBot"),
					"ManyToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
	
			//FDS 28/10/2015  ajout "HISEQ4000","HISEQX"
			l.add(newExperimentType("Dépôt sur séquenceur", "illumina-depot",null, 1400,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),
					getPropertyDefinitionsIlluminaDepot(),
					getInstrumentUsedTypes("MISEQ","HISEQ2000","HISEQ2500","NEXTSEQ500","HISEQ4000","HISEQX"), 
					"OneToVoid", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));			
		
			
			/** Quality Control, ordered by display order **/
			
			// FDS 07/04/2016 ajout --JIRA NGL-894: experiments pour X5
			// 22/09/2016 modification du name => suppression profil mais garder code a cause existant
			l.add(newExperimentType("LABCHIP_GX","labchip-migration-profile", null, 100,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), 
					getPropertyDefinitionsChipMigration(), 
					getInstrumentUsedTypes("labChipGX"),
					"OneToVoid", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
					
			//FDS 01/02/2016 ajout -- JIRA NGL-894: experiments pour X5
			l.add(newExperimentType("Quantification qPCR","qpcr-quantification", null, 200,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), 
					getPropertyDefinitionsQPCR(), 
					getInstrumentUsedTypes("qpcr-lightcycler-480II"),
					"OneToVoid", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG))); 
			
			l.add(newExperimentType("QC Miseq","miseq-qc", null, 300,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), 
					getPropertyDefinitionsQCMiseq(), 
					getInstrumentUsedTypes("MISEQ-QC-MODE"),
					"OneToVoid", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));

			
			/** Transfert, ordered by display order **/
			// NOTE: pas de Node a creer pour experiences type transfert 
			
			l.add(newExperimentType("Aliquot","aliquoting",null, 10300,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()),
					getPropertyAliquoting(), 
					getInstrumentUsedTypes("hand"),
					"OneToMany", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			// FDS 10/08/2016 NGL-1029: "pool : plaques vers plaque" 
			l.add(newExperimentType("Pool plaques -> plaque","pool",null,10400,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), 
					getPropertyDefinitionPool(),
					getInstrumentUsedTypes("janus","hand"),
					"ManyToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
				
			l.add(newExperimentType("Tubes -> Plaque","tubes-to-plate",null,10500,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			l.add(newExperimentType("Plaque -> Tubes","plate-to-tubes",null,10600,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			l.add(newExperimentType("Plaques -> Plaque","plates-to-plate",null,10700,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			
			l.add(newExperimentType("Tubes / Plaques -> Plaque","x-to-plate",null,10800,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNG)));
			

		DAOHelpers.saveModels(ExperimentType.class, l, errors);
	}


	/**
	 * Save all Experiment TypeNodes
	 * @param errors
	 * @throws DAOException 
	 */
	public void saveExperimentTypeNodes(Map<String, List<ValidationError>> errors) throws DAOException {
		//NOTE FDS: les nodes qui apparaissent en previous doivent etre crees avant sinon==>message : experimentTypeNode is mandatory

		newExperimentTypeNode("ext-to-prepa-flowcell", getExperimentTypes("ext-to-prepa-flowcell").get(0), 
				false, false, false, 
				null, 
				null, 
				null, 
				null
				).save();
		
		newExperimentTypeNode("ext-to-prepa-fc-ordered", getExperimentTypes("ext-to-prepa-fc-ordered").get(0), 
				false, false, false, 
				null, 
				null, 
				null, 
				null
				).save();
		
		newExperimentTypeNode("ext-to-denat-dil-lib", getExperimentTypes("ext-to-denat-dil-lib").get(0),
				false, false, false,
				null, 
				null, 
				null,
				null
				).save();
		
		//FDS ajout 01/02/2016 -- JIRA NGL-894 : processus et experiments pour X5
		newExperimentTypeNode("ext-to-x5-wg-pcr-free",getExperimentTypes("ext-to-x5-wg-pcr-free").get(0),
				false,false,false,
				null, 
				null, 
				null, 
				null
				).save();
		
		//FDS ajout 15/04/2016 -- JIRA NGL-894 : processus court pour X5
		newExperimentTypeNode("ext-to-norm-fc-ordered-depot",getExperimentTypes("ext-to-norm-fc-ordered-depot").get(0),
				false,false,false,
				null, 
				null, 
				null, 
				null
				).save();	
		
		// FDS 10/08/2016 JIRA NGL-1047: X5_WG NANO; mise en prod 01/09/2016
		newExperimentTypeNode("ext-to-x5-wg-nano",getExperimentTypes("ext-to-x5-wg-nano").get(0),
				false,false,false,
				null,
				null,
				null,
				null
				).save();
		
	/************************************ DEV / UAT ONLY **********************************************/
	if ( !ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			
		//FDS ajout 31/05/2016 -- JIRA NGL-1025 RNA_Seq; processus long
		newExperimentTypeNode("ext-to-norm-and-pool-fc-ord-depot",getExperimentTypes("ext-to-norm-and-pool-fc-ord-depot").get(0),
				false,false,false,
				null,
				null,
				null,
				null
				).save();	
		
		//FDS ajout 31/05/2016 -- JIRA NGL-1025 RNA_Seq; processus court
		newExperimentTypeNode("ext-to-rna-sequencing",getExperimentTypes("ext-to-rna-sequencing").get(0),
				false,false,false,
				null,
				null,
				null,
				null
				).save();	
	}
		
	
	    // 26/09/2016 duplication du node prep-pcr-free en prod

		newExperimentTypeNode("prep-pcr-free",getExperimentTypes("prep-pcr-free").get(0),
				false,false,false,
				getExperimentTypeNodes("ext-to-x5-wg-pcr-free"), //ext-to-x5-wg-pcr-free  uniqt
				null,
				getExperimentTypes("qpcr-quantification","labchip-migration-profile","miseq-qc"),
				getExperimentTypes("aliquoting")  
				).save();	
		
		newExperimentTypeNode("prep-wg-nano",getExperimentTypes("prep-wg-nano").get(0),
				false,false,false,
				getExperimentTypeNodes("ext-to-x5-wg-nano"), //ext-to-x5-wg-nano   uniqut
				null,
				getExperimentTypes("qpcr-quantification","labchip-migration-profile","miseq-qc"),
				getExperimentTypes("aliquoting")  
				).save();	

	/************************************ DEV / UAT ONLY **********************************************/
	if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){	
		
		// remise 22/09/2016 car perdue.... 
		newExperimentTypeNode("library-prep",getExperimentTypes("library-prep").get(0),
				true,false,false,
				getExperimentTypeNodes("ext-to-rna-sequencing"), 
				null,
				null, // pas de QC ???????????????????????
				null
				).save();
			
		//commun WG_NANO et RNAseq
		newExperimentTypeNode("pcr-and-purification",getExperimentTypes("pcr-and-purification").get(0),
				true,false,false,
				getExperimentTypeNodes("library-prep","prep-wg-nano"), // prep-pcr-free remplacee par prep-wg-nano
				null,
				getExperimentTypes("labchip-migration-profile"), 
				null
				).save();
		
		newExperimentTypeNode("normalization-and-pooling",getExperimentTypes("normalization-and-pooling").get(0),
				false,false,false,
				getExperimentTypeNodes("ext-to-norm-and-pool-fc-ord-depot","pcr-and-purification"),
				null,
				null,
				null
				).save();	
	} else {
		// 26/09/2016 en prod previous=prep-wg-nano
		newExperimentTypeNode("pcr-and-purification",getExperimentTypes("pcr-and-purification").get(0),
				true,false,false,
				getExperimentTypeNodes("prep-wg-nano"),
				null,
				getExperimentTypes("labchip-migration-profile"), 
				null
				).save();
	}


		//FDS ...../2016 -- JIRA NGL-894: processus et experiments pour X5
		//FDS 15/04/2016 -- JIRA NGL-894: processus court pour X5: ajout "ext-to-norm-fc-ordered-depot" dans les previous
		//FDS 20/06/2016 -- JIRA NGL-1029: ajout transfert pool
		//FDS 01/09/2016 -- ajout "pcr-and-purification" en previous (fait partie de WG_Nano)
		newExperimentTypeNode("lib-normalization",getExperimentTypes("lib-normalization").get(0), 
				false, false, false, 
				getExperimentTypeNodes("ext-to-norm-fc-ordered-depot", "prep-pcr-free","pcr-and-purification"), 
				null, 
				getExperimentTypes("miseq-qc"),
				getExperimentTypes("aliquoting","pool")
				).save();
		
		//FDS 20/06/2016 -- JIRA NGL-1029: ajout transfert pool
		newExperimentTypeNode("denat-dil-lib",getExperimentTypes("denat-dil-lib").get(0),
				false,false,false,
				getExperimentTypeNodes("ext-to-denat-dil-lib", "lib-normalization"),
				null,
				null, 
				getExperimentTypes("aliquoting","pool")
				).save();
		
		newExperimentTypeNode("prepa-flowcell",getExperimentTypes("prepa-flowcell").get(0),
				false,false,false,
				getExperimentTypeNodes("ext-to-prepa-flowcell","denat-dil-lib"),
				null,
				null,
				null
				).save();
		
	if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
		newExperimentTypeNode("prepa-fc-ordered",getExperimentTypes("prepa-fc-ordered").get(0),
				false,false,false,
				getExperimentTypeNodes("ext-to-prepa-fc-ordered","lib-normalization", "normalization-and-pooling"),
				null,
				null,
				null
				).save();
			
	} else {
		newExperimentTypeNode("prepa-fc-ordered",getExperimentTypes("prepa-fc-ordered").get(0),
				false,false,false,
				getExperimentTypeNodes("ext-to-prepa-fc-ordered","lib-normalization"), //processus RNAseq pas encore en prod
				null,
				null,
				null
				).save();
		}
		
		newExperimentTypeNode("illumina-depot",getExperimentTypes("illumina-depot").get(0),
				false,false,false,
				getExperimentTypeNodes("prepa-flowcell","prepa-fc-ordered"),
				null,
				null,
				null
				).save();
		
	}

	
	private List<PropertyDefinition> getPropertyDefinitionsQPCR() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		// laisser editable au cas ou la valeur calculée ne convient pas...
		propertyDefinitions.add(newPropertiesDefinition("Concentration", "concentration1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, "F", null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION), MeasureUnit.find.findByCode( "nM"), MeasureUnit.find.findByCode("nM"),
				"single", 11, true, null, "2"));	
		
		propertyDefinitions.add(newPropertiesDefinition("Taille librairie (facteur correctif)", "correctionFactorLibrarySize", LevelService.getLevels(Level.CODE.ContainerIn), Integer.class, true, "F", null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode( "pb"), MeasureUnit.find.findByCode("pb"),
				"single", 12, true, "470", null));
		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyAliquoting() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode( "µL"), MeasureUnit.find.findByCode("µL"), "single",10, false));
		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionsPrepaflowcellCNG() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Conc. chargement", "finalConcentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION), MeasureUnit.find.findByCode("pM"), MeasureUnit.find.findByCode("nM"), "single",25));

		//Outputcontainer		
		propertyDefinitions.add(newPropertiesDefinition("% phiX", "phixPercent", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, null, null, null, "single",51,false,"1",null));		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
						, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("µL"), MeasureUnit.find.findByCode("µL"), "single",52, false));
		
		return propertyDefinitions;
	}
	
	//FDS ajout 09/11/2015 -- JIRA NGL-838
	private List<PropertyDefinition> getPropertyDefinitionsPrepaflowcellOrderedCNG() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Vol. engagé", "inputVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "5"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("µL"), MeasureUnit.find.findByCode("µL"),"single",21));
		propertyDefinitions.add(newPropertiesDefinition("Vol. NaOH", "NaOHVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "5"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("µL"), MeasureUnit.find.findByCode("µL"),"single",22));
		propertyDefinitions.add(newPropertiesDefinition("Conc. NaOH", "NaOHConcentration", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true, null
				,null, null, null, "single",23,true,"0.1N",null));
		propertyDefinitions.add(newPropertiesDefinition("Vol. TrisHCL", "trisHCLVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "5"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("µL"), MeasureUnit.find.findByCode("µL"), "single",24));
		propertyDefinitions.add(newPropertiesDefinition("Conc. TrisHCL", "trisHCLConcentration", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "200000000" 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "mM"), MeasureUnit.find.findByCode( "nM"), "single",25));
		propertyDefinitions.add(newPropertiesDefinition("Vol. master EPX", "masterEPXVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "35"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("µL"), MeasureUnit.find.findByCode("µL"),"single",26));
		propertyDefinitions.add(newPropertiesDefinition("Concentration finale", "finalConcentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true,  null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("pM"), MeasureUnit.find.findByCode("nM"),"single",27,false));

		//OuputContainer
		//keep order declaration between phixPercent and finalVolume
		propertyDefinitions.add(newPropertiesDefinition("% phiX", "phixPercent", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null, null
				, null, null, "single",51,false,"1",null));		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode( "µL"), MeasureUnit.find.findByCode( "µL"), "single",28,false, "50",null));
		
		return propertyDefinitions;
		
	}

	private static List<PropertyDefinition> getPropertyDefinitionsDenatDilLibCNG() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//FDS 01/02/2016 pourquoi est commenté ???
		//propertyDefinitions.add(newPropertiesDefinition("Stockage", "storage", LevelService.getLevels(Level.CODE.ContainerOut), String.class, false, null, null, null, null, "single",55,true,null));		
		
		return propertyDefinitions;
	}
	
	
	public static List<PropertyDefinition> getPropertyDefinitionsQCMiseq() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer 
		propertyDefinitions.add(newPropertiesDefinition("Densité de clusters", "clusterDensity", LevelService.getLevels(Level.CODE.ContainerIn), Integer.class, false, null, null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION), MeasureUnit.find.findByCode("c/mm²"), MeasureUnit.find.findByCode("c/mm²"),
				"single", 11, true, null, null));
		//FDS 26/08/2016 -- JIRA NGL-1046: label modifé + level.CODE.Content
		propertyDefinitions.add(newPropertiesDefinition("Taille d'insert médiane", "measuredInsertSize", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content), Integer.class, false, null, null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"),
				"single", 18, true, null, null));
		
		//FDS 26/08/2016 -- JIRA NGL-1046: ajouter toutes les autres propriétés du fichier
		//    22/09/2016 
		propertyDefinitions.add(newPropertiesDefinition("% cluster", "clusterPercentage", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				null,null,null,"single", 12, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("% PF", "passingFilter", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				null,null,null,"single", 13, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("% Aligned R1", "R1AlignedPercentage", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				null,null,null,"single", 14, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("% Aligned R2", "R2AlignedPercentage", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				null,null,null,"single", 15, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("% Mismatch R1", "R1MismatchPercentage", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				null,null,null,"single", 16, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("% Mismatch R1", "R2MismatchPercentage", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				null,null,null,"single", 17, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Taille Min", "minInsertSize", LevelService.getLevels(Level.CODE.ContainerIn), Integer.class, false, null, null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"),
				"single", 19, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Taille Max", "maxInsertSize", LevelService.getLevels(Level.CODE.ContainerIn), Integer.class, false, null, null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"),
				"single", 20, true, null, null));
		// !! NGL-1046 et SUPSQCNG-413 =>mettre des doubles
		propertyDefinitions.add(newPropertiesDefinition("Observed Diversity", "observedDiversity", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				null,null,null,"single", 21, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Estimated Diversity", "estimatedDiversity", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				null,null,null,"single", 22, true, null, null));
	
		return propertyDefinitions;
	}
	
	// FDS JIRA NGL-1030 Ajouter la propriété size et rendre les 2 obligatoires au status "F"(Terminé)
	public static List<PropertyDefinition> getPropertyDefinitionsChipMigration() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		//InputContainer (pas d'outputContainer sur une experience QC )
		// l'unité est variable. NE PLUS LA SPECIFIER..., MeasureUnit.find.findByCode("ng/µl"),MeasureUnit.find.findByCode("ng/µl")
		propertyDefinitions.add(newPropertiesDefinition("Concentration", "concentration1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, "F", null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION), null, null,
				"single", 11, true, null, null));
		
		// laiser la position 12 libre pour la colonne unit

		propertyDefinitions.add(newPropertiesDefinition("Size", "size1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, "F", null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"),
				"single", 13, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Profil de migration", "migrationProfile", LevelService.getLevels(Level.CODE.ContainerIn), Image.class, false, null, null, 				
				"img", 14, false, null, null));
		
		return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//Utiliser par import ngl-data CNG de creation des depot-illumina
		//propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Experiment), Integer.class, false, "single"));	
		propertyDefinitions.add(newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, true, "single"));
		
		return propertyDefinitions;
	}
	
	// FDS ajout 05/02/2016 -- JIRA NGL-894: experiment PrepPcrFree pour le process X5
	// 16/09/2016 modification du nom car commun a prep-pcr-free et prep-wgnano + supression des valeurs par defaut
	private List<PropertyDefinition> getPropertyDefinitionsPrepPcrFree_WgNano() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Vol. engagé dans Frag", "inputVolumeFrag", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("µL"), MeasureUnit.find.findByCode("µL"),"single",20));
		
		propertyDefinitions.add(newPropertiesDefinition("Qté. engagée dans Frag", "inputQuantityFrag", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY), MeasureUnit.find.findByCode("ng"), MeasureUnit.find.findByCode("ng"),"single",21));
		
		propertyDefinitions.add(newPropertiesDefinition("Vol. engagé dans Lib", "inputVolumeLib", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("µL"), MeasureUnit.find.findByCode("µL"),"single",22));
		
		propertyDefinitions.add(newPropertiesDefinition("Qté. engagée dans Lib", "inputQuantityLib", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY), MeasureUnit.find.findByCode("ng"), MeasureUnit.find.findByCode("ng"),"single",23));
	
		//OuputContainer
		// GA 08/02/2016 =>  ces proprietes de containerOut doivent etre propagees au content
		// GA 14/03/2016 => il faut specifier l'état auquel les propriétés sont obligatoires: ici Finished (F)

		propertyDefinitions.add(newPropertiesDefinition("Tag", "tag", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), String.class, true, "F", getTagIllumina(), 
				"single", 30, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Catégorie de Tag", "tagCategory", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), String.class, true, "F", getTagCategories(), 
				"single", 31, true, null,null));		
		
		// pas de niveau content car théoriques( J Guy..)
		propertyDefinitions.add(newPropertiesDefinition("Taille insert (théorique)", "insertSize", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"),"single",32,true,"350", null));
		
		propertyDefinitions.add(newPropertiesDefinition("Taille librairie (théorique)", "librarySize", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, true, null	
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"),"single",33, true,"470",null));
	
		return propertyDefinitions;
	}
	
	
	// FDS ajout 05/02/2016 -- JIRA NGL-894: experiment librairie normalization pour le process X5
	private List<PropertyDefinition> getPropertyDefinitionsLibNormalization() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer
		// calculé automatiquement en fonction du volume final et concentration final demandés ou saisie libre, non obligatoire
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("µL"), MeasureUnit.find.findByCode("µL"),"single", 20, true, null,null));
		
		//buffer est sur ContainerIn ????????????
		propertyDefinitions.add(newPropertiesDefinition("Volume tampon", "bufferVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("µL"), MeasureUnit.find.findByCode("µL"),"single", 20, true, null,null));		
		
		//OuputContainer
		
		return propertyDefinitions;
	}
	
	// FDS ajout 02/06/2016 -- JIRA NGL-1028: experiment normalization-and-pooling
	private List<PropertyDefinition> getPropertyDefinitionsNormalizationAndPooling() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer
		// calculé automatiquement en fonction du volume final et concentration final demandés ou saisie libre, non obligatoire VERIFIER
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode( "µL"), MeasureUnit.find.findByCode("µL"),"single", 20, true, null,null));
		
		//OuputContainer 
		propertyDefinitions.add(newPropertiesDefinition("Volume tampon Tris", "bufferVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, false, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode( "µL"), MeasureUnit.find.findByCode("µL"),"single", 25, true, null,null));		
			
		return propertyDefinitions;
	}
	
	// FDS ajout 17/06/2016 -- JIRA NGL-1029: experiment pool en plaque
	private static List<PropertyDefinition> getPropertyDefinitionPool() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode("µL"),MeasureUnit.find.findByCode("µL"),"single", 20, true, null,null));
		
		//OuputContainer 
		propertyDefinitions.add(newPropertiesDefinition("Volume tampon", "bufferVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, false, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode("µL"),MeasureUnit.find.findByCode("µL"),"single", 25, true, null,null));
		
		return propertyDefinitions;
	}
	
	// FDS ajout 01/08/2016 -- JIRA NGL-1027: experiment PCR + purification en plaque	
	private static List<PropertyDefinition> getPropertyDefinitionsPcrAndPurification() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer
		// volume engagé editable et obligatoire, qté pas editable calculée en fonction volume engagé et pas sauvegardée
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode("µL"),MeasureUnit.find.findByCode("µL"),"single", 20, true, null,null));
		
		/* suppression demandée lors du test avant mise en prod
		propertyDefinitions.add(newPropertiesDefinition("Qté engagée", "inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode("ng"),MeasureUnit.find.findByCode("ng"),"single", 25, true, null,null));
		*/
		
		//OuputContainer 
		// rien...??
		
		return propertyDefinitions;
	}
	
	//FDS ajout 03/08/2016 -- JIRA NGL-1026: experiment library prepartion sans fragmentation ( duplication a partir de pcr-free .. et suppression de la fragmentation
	private List<PropertyDefinition> getPropertyDefinitionsLibraryPrep() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer
		// valeur par defaut pour volume et qté engagées ?? Pas pour l'instant...
		propertyDefinitions.add(newPropertiesDefinition("Vol. engagé dans Lib", "inputVolumeLib", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode("µL"), MeasureUnit.find.findByCode("µL"),"single",22, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Qté. engagée dans Lib", "inputQuantityLib", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY), MeasureUnit.find.findByCode("ng"), MeasureUnit.find.findByCode("ng"),"single",23, true, null, null));
	
		//OuputContainer
		// ces propriétés de containerOut doivent etre propagees au content
		// il faut specifier l'état auquel les propriétés sont obligatoires: ici Finished (F)

		propertyDefinitions.add(newPropertiesDefinition("Tag", "tag", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), String.class, true, "F", getTagIllumina(), 
				"single", 30, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Catégorie de Tag", "tagCategory", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), String.class, true, "F", getTagCategories(), 
				"single", 31, true, null,null));		
		
		// pas de niveau content car théoriques(J Guy..) | meme valeurs ??? voir spec
		propertyDefinitions.add(newPropertiesDefinition("Taille insert (théorique)", "insertSize", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, true, null, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"),"single",32,true,"350", null));
		
		propertyDefinitions.add(newPropertiesDefinition("Taille librairie (théorique)", "librarySize", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, true, null, null	
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"),"single",33, true,"470",null));
	
		return propertyDefinitions;
	}
	
}