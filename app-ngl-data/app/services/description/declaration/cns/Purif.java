package services.description.declaration.cns;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessType;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.declaration.AbstractDeclaration;

public class Purif extends AbstractDeclaration {

	@Override
	protected List<ExperimentType> getExperimentTypeCommon() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		//purif
		l.add(newExperimentType("Traitement DNAse","dnase-treatment",null, 30100,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), getPropertyDefinitionsDNAseTreatment(),
				getInstrumentUsedTypes("hand"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Déplétion ARN ribo","rrna-depletion",null, 30200,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), null,
				getInstrumentUsedTypes("hand"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Ampure","ampure",null, 30300,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), getPropertyDefinitionsPostAmupurePCR(),
				getInstrumentUsedTypes("hand","biomek-fx"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));	
		
		l.add(newExperimentType("Ampure Post-PCR","post-pcr-ampure",null, 30400,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), getPropertyDefinitionsPostAmupurePCR(),
				getInstrumentUsedTypes("hand","biomek-fx"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));	
		
		l.add(newExperimentType("Purification sur colonne","spin-column-purification",null, 30500,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), getPropertyDefinitionsSpinColumnPurification(),
				getInstrumentUsedTypes("hand"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));	
		
		
		return l;
		
	}
	
	
	private List<PropertyDefinition> getPropertyDefinitionsDNAseTreatment() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 12, true, null,"1"));
		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, false, null,
				null,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"),"single",13, true,null,"1"));
		
		return propertyDefinitions;
	}
	
	
	private List<PropertyDefinition> getPropertyDefinitionsPostAmupurePCR() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Ratio billes (Ampure Post PCR)", "adnBeadVolumeRatio", LevelService.getLevels(Level.CODE.Experiment), String.class, true, null, 
				null, null, null, null,"single", 2, true, null, null));
	
		return propertyDefinitions;
	}

	
	private List<PropertyDefinition> getPropertyDefinitionsSpinColumnPurification() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 12, true, null,"1"));
		
		return propertyDefinitions;
	}
	
	
	
	@Override
	protected List<ExperimentType> getExperimentTypeDEV() {
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
		//GA 07/11/2016 USED FOR PROCESS who start with ampure
		newExperimentTypeNode("post-pcr-ampure",getExperimentTypes("post-pcr-ampure").get(0),false, false,false,
				getExperimentTypeNodes("pcr-amplification-and-purification")
				,null,null,null).save();
		
	}
	
	@Override
	protected void getExperimentTypeNodeDEV() {
		
		
		
	}

	@Override
	protected void getExperimentTypeNodePROD() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getExperimentTypeNodeUAT() {
		// TODO Auto-generated method stub
		
	}


}
