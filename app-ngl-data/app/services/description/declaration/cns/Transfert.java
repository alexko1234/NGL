package services.description.declaration.cns;

import static services.description.DescriptionFactory.*;

import java.util.ArrayList;
import java.util.Arrays;
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

public class Transfert extends AbstractDeclaration {

	@Override
	protected List<ExperimentType> getExperimentTypeCommon() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		l.add(newExperimentType("Aliquot","aliquoting",null,10100,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()),
				getPropertyAliquoting(), getInstrumentUsedTypes("hand"),"OneToMany", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Pool Tube","pool-tube",null,10200,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), getPropertyDefinitionPoolTube(),
				getInstrumentUsedTypes("hand","tecan-evo-100"),"ManyToOne", false,
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		
		l.add(newExperimentType("Pool générique","pool",null,10300,
			ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), getPropertyDefinitionPoolTube(),
			getInstrumentUsedTypes("tecan-evo-100", "hand"),"ManyToOne", 
			DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Normalisation","normalisation",null,10400,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), getPropertyDefinitionNormalisation(),
				getInstrumentUsedTypes("biomek-fx","hand"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Tubes -> Plaque","tubes-to-plate",null,10500,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), null,
				getInstrumentUsedTypes("hand","tecan-evo-100","biomek-fx"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Plaque -> Tubes","plate-to-tubes",null,10600,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), null,
				getInstrumentUsedTypes("hand","tecan-evo-100","biomek-fx"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Plaques -> Plaque","plates-to-plate",null,10700,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), null,
				getInstrumentUsedTypes("hand","tecan-evo-100","biomek-fx"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Tubes / Plaques -> Plaque","x-to-plate",null,10700,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transfert.name()), null,
				getInstrumentUsedTypes("hand","tecan-evo-100","biomek-fx"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		
		return l;
	}
	
	@Override
	protected List<ExperimentType> getExperimentTypeDEV() {
		return null;
	}

	@Override
	protected List<ExperimentType> getExperimentTypePROD() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		return l;
	}

	@Override
	protected List<ExperimentType> getExperimentTypeUAT() {
		return null;
	}

	@Override
	protected void getExperimentTypeNodeCommon() {
		// TODO Auto-generated method stub
		
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
	protected List<ProcessType> getProcessTypeCommon() {
		// TODO Auto-generated method stub
		return null;
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

	private static List<PropertyDefinition> getPropertyDefinitionNormalisation() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//InputContainer
		propertyDefinitions.add(newPropertiesDefinition("Mode calcul", "computeMode", LevelService.getLevels(Level.CODE.ContainerIn), String.class, true, null, 
				Arrays.asList(newValue("fixeCfVi", "Volume à engager fixe"), newValue("fixeCfVf", "Volume final fixe")), "single", 19, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 20, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume tampon", "bufferVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 21, true, null,null));
		
		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyAliquoting() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",10, false));
		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionPoolTube() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//InputContainer
		
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 20, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume tampon", "bufferVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 25, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Label de travail", "workName", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Container), String.class, false, null, null, 
				"single", 30, true, null,null));
		
	/*	propertyDefinitions.add(newPropertiesDefinition("Volume tampon à rajouter", "bufferVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, false, null,
				MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",9, false)); */
		//Outputcontainer
		/*	propertyDefinitions.add(newPropertiesDefinition("Volume final", "finalVolume", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, true, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"), "single",13,true));
		propertyDefinitions.add(newPropertiesDefinition("Concentration finale", "finalConcentration", LevelService.getLevels(Level.CODE.ContainerOut), Double.class, false, null
				, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_CONCENTRATION),MeasureUnit.find.findByCode( "nM"),MeasureUnit.find.findByCode( "nM"),"single",14,true));	*/	
		
		return propertyDefinitions;
	}

	

		
}
