package services.description.experiment.cns;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ExperimentTypeNode;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.experiment.AbstractExperimentService;

public class MetaTProcess {
	
	public static List<ExperimentType> getExperimentMetaTBqRNA(){
		
		List<ExperimentType> l = new ArrayList<ExperimentType>();
		
		l.add(newExperimentType("Ext to MetaT bq RNA","ext-to-rna-lib-transcriptomic-process",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Bq RNA Illumina indexée","rna-illumina-indexed-library","LIB",800,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsRNAIlluminaIndexedLibrary(),
				AbstractExperimentService.getInstrumentUsedTypes("biomek-fx-and-cDNA-thermocycler","hand"),"OneToOne", null,true,
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		return l;
	}

	public static void getETNMetaTBqRNA(){
		//Metatranscriptome
		newExperimentTypeNode("ext-to-rna-lib-transcriptomic-process", AbstractExperimentService.getExperimentTypes("ext-to-rna-lib-transcriptomic-process").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("rna-illumina-indexed-library",AbstractExperimentService.getExperimentTypes("rna-illumina-indexed-library").get(0),false, false,false,AbstractExperimentService.getExperimentTypeNodes("ext-to-rna-lib-transcriptomic-process"),null,null,null).save();
	}
	
	private static List<PropertyDefinition> getPropertyDefinitionsRNAIlluminaIndexedLibrary() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, false, null,
				null,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"),"single",12, true,null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 13, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Tag", "tag", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), String.class, true, null, 
				null, null,null,null,"single", 13, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Catégorie de Tag", "tagCategory", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), String.class, true, null, 
				null, null,null,null,"single", 13, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Volume", "volume", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, null, 
				null, null, null, null,"single", 15, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Orientation du brin séquencé read 1", "strandOrientation", LevelService.getLevels(Level.CODE.Experiment,Level.CODE.Content), String.class, true, null, 
				getStrandOrientation(), null, null, null,"single", 1, true, null,null));

		return propertyDefinitions;
	}

	private static List<Value> getStrandOrientation(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("forward", "forward"));		
		values.add(DescriptionFactory.newValue("reverse", "reverse"));		
		values.add(DescriptionFactory.newValue("unstranded", "unstranded"));		
		return values;	
	}
}
