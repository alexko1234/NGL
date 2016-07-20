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
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.experiment.AbstractExperimentService;

public class MetaBarCoding {
	
	
public static List<ExperimentType> getExperimentMetaBarCoding(){
		
		List<ExperimentType> l = new ArrayList<ExperimentType>();
		
		l.add(newExperimentType("Ext to MetaBarcoding (sans sizing)","ext-to-tag-pcr-and-dna-library",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Ext to MetaBarcoding avec sizing","ext-to-tag-pcr-and-dna-library-with-sizing",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Tags-PCR + purif","tag-pcr","TAG",800,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsRNAIlluminaIndexedLibrary(),
				AbstractExperimentService.getInstrumentUsedTypes("thermocycler"),"OneToOne", AbstractExperimentService.getSampleTypes("amplicon"),true,
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		return l;
	}

	public static void getETNMetaBarCoding(){
		
		newExperimentTypeNode("ext-to-tag-pcr-and-dna-library", AbstractExperimentService.getExperimentTypes("ext-to-tag-pcr-and-dna-library").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("ext-to-tag-pcr-and-dna-library-with-sizing", AbstractExperimentService.getExperimentTypes("ext-to-tag-pcr-and-dna-library-with-sizing").get(0), false, false, false, null, null, null, null).save();
		
		newExperimentTypeNode("tag-pcr",AbstractExperimentService.getExperimentTypes("tag-pcr").get(0),true, true,false,AbstractExperimentService.getExperimentTypeNodes("ext-to-tag-pcr-and-dna-library","ext-to-tag-pcr-and-dna-library-with-sizing")
									,null,AbstractExperimentService.getExperimentTypes("fluo-quantification","chip-migration"),null).save();
	}


	private static List<PropertyDefinition> getPropertyDefinitionsRNAIlluminaIndexedLibrary() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 12, true, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null,
				null,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"),"single",13, true,null,null));
		propertyDefinitions.add(newPropertiesDefinition("Nb de PCR", "nbPCR", LevelService.getLevels(Level.CODE.ContainerIn), Integer.class, true, null, 
				null,  null, null, null,"single", 15, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Sample Type", "sampleTypeCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, false, "N", null, 
				"single", 17, false, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Projet", "projectCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, "IP", 
				null, null ,null ,null ,"single", 20, true, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Echantillon", "sampleCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, false, null, 
				null, null, null, null,"single", 25, false, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("DNA polymerase", "dnaPolymerase", LevelService.getLevels(Level.CODE.Experiment), String.class, false, null, 
				DescriptionFactory.newValues("taq Phusion"), null, null, null,"single", 1, false, "taq Phusion",null));
		propertyDefinitions.add(newPropertiesDefinition("Amorces", "amplificationPrimers", LevelService.getLevels(Level.CODE.Experiment,Level.CODE.Content), String.class, true, null, 
				null, null, null, null,"single", 2, true, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Région ciblée", "targetedRegion", LevelService.getLevels(Level.CODE.Experiment), String.class, true, null, 
				null, null, null, null,"single", 3, true, null,null));
		
		return propertyDefinitions;
	}

}
