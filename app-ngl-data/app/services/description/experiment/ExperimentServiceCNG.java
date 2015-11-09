package services.description.experiment;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
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
	 * Save all ExperimentCategory
	 * @param errors
	 * @throws DAOException 
	 */
	public  void saveExperimentCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ExperimentCategory> l = new ArrayList<ExperimentCategory>();
		for (ExperimentCategory.CODE code : ExperimentCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(ExperimentCategory.class, code.name(), code.name()));
		}
		DAOHelpers.saveModels(ExperimentCategory.class, l, errors);
	}


	public void saveExperimentTypes(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ExperimentType> l = new ArrayList<ExperimentType>();
		
		l.add(newExperimentType("Ext to prepa flowcell","ext-to-prepa-flowcell",null,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		//FDS ajout 04/11/2015 -- JIRA NGL-838: ajout prepa-fc-ordered
		l.add(newExperimentType("Ext to prepa flowcell ordered","ext-to-prepa-fc-ordered",null,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newExperimentType("Préparation flowcell","prepa-flowcell",null,1100,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsPrepaflowcellCNG(),
				getInstrumentUsedTypes("cBot", "cBot-onboard"),"ManyToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		//FDS ajout 04/11/2015 -- JIRA NGL-838: ajout prepa-fc-ordered, attention pas cBot-onboard                                
		l.add(newExperimentType("Prép. flowcell ordonnée","prepa-fc-ordered",null,1100,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsPrepaflowcellOrderedCNG(),
				getInstrumentUsedTypes("cBot"),"ManyToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		l.add(newExperimentType("Ext to librairie dénaturée","ext-to-denat-dil-lib",null,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		// FDS ajout 02-02-2015, intrument Used =>robot oui mais lequel???
		l.add(newExperimentType("Dénaturation-dilution","denat-dil-lib",null,1100,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsDenatDilLibCNG(),
				getInstrumentUsedTypes("hand"),"OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));

		//GA : 03/08/2015 new declaration. lib-normalization became void to avoid to display this step in IHM
		l.add(newExperimentType("Librairie normalisée","lib-normalization",null,1000,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null, "OneToOne", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));		
		
		//FDS 28/10/2015  ajout "HISEQ4000","HISEQX"
		l.add(newExperimentType("Dépôt sur séquenceur", "illumina-depot",null, 1400,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()),getPropertyDefinitionsIlluminaDepot(),
				getInstrumentUsedTypes("MISEQ","HISEQ2000","HISEQ2500","NEXTSEQ500","HISEQ4000","HISEQX"), "OneToVoid", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
				
		l.add(newExperimentType("Aliquot","aliquoting",null,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()),
				getPropertyAliquoting(), getInstrumentUsedTypes("hand"),"OneToMany", 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
		
			//quality control

			//purif

			//transformation
			
		
			
			
			
			/*
			l.add(newExperimentType("Migration sur puce","chip-migration",
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigration(), 
					getProtocols("proto_qc_v1"), getInstrumentUsedTypes("agilent-2100-bioanalyzer","labChipGX"),"OneToVoid", 
					DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
			*/
			
//			l.add(newExperimentType("Migration sur puce (ampli)","chip-migration-post-pcr",null,650,
//					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigration(), 
//					getInstrumentUsedTypes("agilent-2100-bioanalyzer", "labchipGX"),"OneToVoid", 
//					DescriptionFactory.getInstitutes( Institute.CODE.CNG)));
//			
//			l.add(newExperimentType("Migration sur puce (non ampli)","chip-migration-pre-pcr",null,250,
//					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigration(), 
//					getInstrumentUsedTypes("agilent-2100-bioanalyzer", "labchipGX"),"OneToVoid", 
//					DescriptionFactory.getInstitutes( Institute.CODE.CNG)));
//			
//			
//			l.add(newExperimentType("Dosage fluorimétrique","fluo-quantification",null,450,
//					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), null, 
//					getInstrumentUsedTypes("qubit"),"OneToVoid", 
//					DescriptionFactory.getInstitutes( Institute.CODE.CNG)));
//			
//			l.add(newExperimentType("Quantification qPCR","qPCR-quantification",null,850,
//					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), null, 
//					getInstrumentUsedTypes("rocheLightCycler-qPCR"/*,"stratagene-qPCR"*/),"OneToVoid", 
//					DescriptionFactory.getInstitutes( Institute.CODE.CNG))); 	
//					


			
			/**********************************************************************************/
			
			
			
		}

		DAOHelpers.saveModels(ExperimentType.class, l, errors);

	}


	
	public void saveExperimentTypeNodes(Map<String, List<ValidationError>> errors) throws DAOException {

		newExperimentTypeNode("ext-to-prepa-flowcell", getExperimentTypes("ext-to-prepa-flowcell").get(0), false, false, null, null, null).save();
		
		//FDS ajout 04/11/2015 -- JIRA NGL-838: ajout prepa-fc-ordered
		newExperimentTypeNode("ext-to-prepa-fc-ordered", getExperimentTypes("ext-to-prepa-fc-ordered").get(0), false, false, null, null, null).save();
		
		newExperimentTypeNode("ext-to-denat-dil-lib", getExperimentTypes("ext-to-denat-dil-lib").get(0), false, false, null, null, null).save();
		
		newExperimentTypeNode("lib-normalization",getExperimentTypes("lib-normalization").get(0),false,false,null,null,null).save();
		
		
		newExperimentTypeNode("denat-dil-lib",getExperimentTypes("denat-dil-lib").get(0),false,false,getExperimentTypeNodes("ext-to-denat-dil-lib", "lib-normalization"),
				null,null).save();
		
		newExperimentTypeNode("prepa-flowcell",getExperimentTypes("prepa-flowcell").get(0),false,false,getExperimentTypeNodes("ext-to-prepa-flowcell","denat-dil-lib"),
				null,null).save();
		
		//FDS ajout 04/11/2015 -- JIRA NGL-838 ajout prepa-fc-ordered, attention previous node normal est "lib-normalization" (et non "denat-dil-lib")
		newExperimentTypeNode("prepa-fc-ordered",getExperimentTypes("prepa-fc-ordered").get(0),false,false,getExperimentTypeNodes("ext-to-prepa-fc-ordered","lib-normalization"),
				null,null).save();
	
		//FDS modif 04/11/2015 -- JIRA NGL-838: ajout prepa-fc-ordered
		newExperimentTypeNode("illumina-depot",getExperimentTypes("illumina-depot").get(0),false,false,getExperimentTypeNodes("prepa-flowcell","prepa-fc-ordered"),
				null,null).save();
		
		newExperimentTypeNode("aliquoting",getExperimentTypes("aliquoting").get(0),false,false,getExperimentTypeNodes("denat-dil-lib"),
				null,null).save();
		
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
					
			
			// GA : 03/08/2015 new temporary declaration
			
			
		}
		


	}

	private static List<PropertyDefinition> getPropertyAliquoting() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µl"),MeasureUnit.find.findByCode( "µl"), "single",10, false));
		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionsPrepaflowcellCNG() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Conc. chargement", "finalConcentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null 
						, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"), "single",25));

		//Outputcontainer		
		propertyDefinitions.add(newPropertiesDefinition("% phiX", "phixPercent", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null, null, null, null, "single",51,false,"1"));		
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
						, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",52, false));
		
		
		return propertyDefinitions;
	}
	
	//FDS ajout 09/11/2015 -- JIRA NGL-838
	private List<PropertyDefinition> getPropertyDefinitionsPrepaflowcellOrderedCNG() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(newPropertiesDefinition("Vol. engagé", "inputVolume2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "5"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",21));
		propertyDefinitions.add(newPropertiesDefinition("Vol. NaOH", "NaOHVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "5"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",22));
		propertyDefinitions.add(newPropertiesDefinition("Conc. NaOH", "NaOHConcentration", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true, null, null, null, null, "single",23,true,"0.1N"));
		propertyDefinitions.add(newPropertiesDefinition("Vol. TrisHCL", "trisHCLVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "5"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",24));
		propertyDefinitions.add(newPropertiesDefinition("Conc. TrisHCL", "trisHCLConcentration", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "200000000" 
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "mM"),MeasureUnit.find.findByCode( "nM"), "single",25));
		propertyDefinitions.add(newPropertiesDefinition("Vol. master EPX", "masterEPXVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, "35"
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",26));
		propertyDefinitions.add(newPropertiesDefinition("Concentration finale", "finalConcentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false,  null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "pM"),MeasureUnit.find.findByCode( "nM"),"single",27,false));

		//OuputContainer
		propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",28,false, "50"));
		
		propertyDefinitions.add(newPropertiesDefinition("% phiX", "phixPercent", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null, null, null, null, "single",51,false,"1"));		
		
		return propertyDefinitions;
		
	}

	private static List<PropertyDefinition> getPropertyDefinitionsDenatDilLibCNG() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		//propertyDefinitions.add(newPropertiesDefinition("Stockage", "storage", LevelService.getLevels(Level.CODE.ContainerOut), String.class, false, null, null, null, null, "single",55,true,null));		
		
		return propertyDefinitions;
	}
	
	//TODO
	// Propriete taille en output et non en input ?
	// Valider les keys
	public static List<PropertyDefinition> getPropertyDefinitionsChipMigration() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		// A supprimer une fois le type de support category sera géré
		propertyDefinitions.add(newPropertiesDefinition("Position","position", LevelService.getLevels(Level.CODE.ContainerIn),Integer.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));		
		propertyDefinitions.add(newPropertiesDefinition("Taille", "size", LevelService.getLevels(Level.CODE.ContainerOut),Integer.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("kb"), MeasureUnit.find.findByCode("kb"), "single"));
		// Voir avec Guillaume comment gérer les fichiers
		propertyDefinitions.add(newPropertiesDefinition("Profil DNA HS", "fileResult", LevelService.getLevels(Level.CODE.ContainerOut),String.class, true, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionsIlluminaDepot() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Utiliser par import ngl-data CNG de creation des depot-illumina
		//propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Experiment), Integer.class, false, "single"));	
		propertyDefinitions.add(newPropertiesDefinition("Date réelle de dépôt", "runStartDate", LevelService.getLevels(Level.CODE.Experiment), Date.class, true, "single"));
		return propertyDefinitions;
	}
	
}
