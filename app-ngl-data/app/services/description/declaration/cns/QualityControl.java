package services.description.declaration.cns;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.*;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.declaration.AbstractDeclaration;


public class QualityControl extends AbstractDeclaration {

	@Override
	protected List<ExperimentType> getExperimentTypeCommon() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();
		
		l.add(newExperimentType("Dosage fluorométrique","fluo-quantification", null,20100,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsDosageFluorometrique(), 
				getInstrumentUsedTypes("qubit","fluoroskan"),"OneToVoid", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS))); 
		
		l.add(newExperimentType("Migration sur gel","gel-migration", null,20200,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsGelMigration(), 
				getInstrumentUsedTypes("hand"),"OneToVoid", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS))); 
		
		l.add(newExperimentType("Migration sur puce (eval ARN)","chip-migration-rna-evaluation", null,20300,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigrationARNEvaluation(), 
				getInstrumentUsedTypes("agilent-2100-bioanalyzer"),"OneToVoid", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS))); 
		
		
		l.add(newExperimentType("Migration sur puce (hors eval ARN)","chip-migration", null,20350,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsChipMigration(), 
				getInstrumentUsedTypes("agilent-2100-bioanalyzer", "labchip-gx"),"OneToVoid", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS))); 
		
		
		l.add(newExperimentType("PCR + gel","control-pcr-and-gel", null,20400,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsPCRGel(), 
				getInstrumentUsedTypes("thermocycler"),"OneToVoid", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS))); 
	
		l.add(newExperimentType("Quantification qPCR","qpcr-quantification", null,20500,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.qualitycontrol.name()), getPropertyDefinitionsQPCR(), 
				getInstrumentUsedTypes("tecan-evo-100-and-stratagene-qPCR-system"),"OneToVoid", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		return l;
	}
	
	@Override
	protected List<ExperimentType> getExperimentTypeDEV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ExperimentType> getExperimentTypePROD() {
				return null;
	}

	@Override
	protected List<ExperimentType> getExperimentTypeUAT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ProcessType> getProcessTypeCommon() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<ProcessType> getProcessTypeDEV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ProcessType> getProcessTypePROD() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ProcessType> getProcessTypeUAT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void getExperimentTypeNodeCommon() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void getExperimentTypeNodeDEV() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getExperimentTypeNodePROD() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getExperimentTypeNodeUAT() {
		// TODO Auto-generated method stub
		
	}
	
	private List<PropertyDefinition> getPropertyDefinitionsDosageFluorometrique() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 12, true, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Concentration", "concentration1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "ng/µL"),MeasureUnit.find.findByCode( "ng/µL"),"single", 13, true, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Volume sortie", "volume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 14, true, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Quantité", "quantity1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"),"single", 15, true, null,null));
		return propertyDefinitions;
		
	}

	
	private List<PropertyDefinition> getPropertyDefinitionsGelMigration() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 12, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Photo de gel 30 min.", "electrophoresisGelPhoto", LevelService.getLevels(Level.CODE.ContainerIn), Image.class, true, "F", null, 				
				"img", 13, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Photo de gel 60 min.", "electrophoresisGelPhoto2", LevelService.getLevels(Level.CODE.ContainerIn), Image.class, false, null, null, 				
				"img", 14, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume sortie", "volume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 15, true, null,null));
		
		return propertyDefinitions;
	}

	
	private List<PropertyDefinition> getPropertyDefinitionsPCRGel() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 12, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Photo de gel", "electrophoresisGelPhoto", LevelService.getLevels(Level.CODE.ContainerIn), Image.class, true, "F", null, 				
				"img", 13, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume sortie", "volume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 14, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Amorces", "amplificationPrimers", LevelService.getLevels(Level.CODE.Experiment), String.class, true, null, 
				null, null, null, null,"single", 1, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Région ciblée","targetedRegion", LevelService.getLevels(Level.CODE.Experiment), String.class, true, null, 
				null, null, null, null,"single", 2, true, null,null));

		return propertyDefinitions;
	}

	
	public static List<PropertyDefinition> getPropertyDefinitionsChipMigrationARNEvaluation() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		// A supprimer une fois le type de support category sera géré
		
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode( "µL"), MeasureUnit.find.findByCode("µL"),
				"single", 11, true, null, "0"));		
	
		propertyDefinitions.add(newPropertiesDefinition("Profil de migration", "migrationProfile", LevelService.getLevels(Level.CODE.ContainerIn), Image.class, true, "F", null, 				
				"img", 14, true, null, null));
		
		
		propertyDefinitions.add(newPropertiesDefinition("RIN", "rin", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 				
				"single", 15, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Couleur éval.", "rnaEvaluation", LevelService.getLevels(Level.CODE.ContainerIn), String.class, false, null, 
				newValues("vert","jaune","orange","rouge"), "single", 16, true, null, null));
		
		
		
		propertyDefinitions.add(newPropertiesDefinition("Volume sortie", "volume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 15, true, null,null));
		
		
		return propertyDefinitions;
	}
	
	
	public static List<PropertyDefinition> getPropertyDefinitionsChipMigration() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		// A supprimer une fois le type de support category sera géré
		
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME), MeasureUnit.find.findByCode( "µL"), MeasureUnit.find.findByCode("µL"),
				"single", 11, true, null, "0"));		
		
		propertyDefinitions.add(newPropertiesDefinition("Taille", "measuredSize", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, "F", null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"),
				"single", 13, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Taille 2", "measuredSize2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"),
				"single", 14, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Profil de migration", "migrationProfile", LevelService.getLevels(Level.CODE.ContainerIn), Image.class, true, "F", null, 				
				"img", 15, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume sortie", "volume1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 16, true, null,null));
		
		
		return propertyDefinitions;
	}
	
	private List<PropertyDefinition> getPropertyDefinitionsQPCR() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(newPropertiesDefinition("Concentration", "concentration1", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, "F", null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION), MeasureUnit.find.findByCode( "nM"), MeasureUnit.find.findByCode("nM"),
				"single", 12, true, null, "2"));		
		
		propertyDefinitions.add(newPropertiesDefinition("Concentration", "concentration2", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, "F", null, 
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION), MeasureUnit.find.findByCode("ng/µl"), MeasureUnit.find.findByCode("ng/µl"),
				"single", 13, true, null, "2"));		
		
		return propertyDefinitions;
	}



	
}
