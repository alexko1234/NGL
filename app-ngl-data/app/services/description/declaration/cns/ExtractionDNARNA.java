package services.description.declaration.cns;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import play.Logger;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.declaration.AbstractDeclaration;

public class ExtractionDNARNA extends AbstractDeclaration{

	@Override
	protected List<ExperimentType> getExperimentTypeCommon() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		l.add(newExperimentType("Ext to Extraction ADN / ARN (corail)","ext-to-grinding-and-dna-rna-extraction",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Broyage ","grinding",null,650,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), null,
				getInstrumentUsedTypes("fast-prep"),"OneToMany", null,true,
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Ext to Extraction ADN / ARN (plancton)","ext-to-dna-rna-extraction-process",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Extraction ADN / ARN","dna-rna-extraction","AN",700,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsExtractionADNARN(),
				getInstrumentUsedTypes("cryobroyeur","hand"),"OneToMany", getSampleTypes("DNA","RNA"),true,
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		
		
		
		
		return l;
	}

	@Override
	protected List<ExperimentType> getExperimentTypePROD() {
		return null;
	}

	@Override
	protected List<ExperimentType> getExperimentTypeDEV() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		//LBIOMEG ADRI PROCESS
		l.add(newExperimentType("Ext to Extraction ARN (17-200 et >200nt)","ext-to-small-and-large-rna-extraction",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Extraction ARN total","total-rna-extraction","RNA",710,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsExtractionARNtotal(),
				getInstrumentUsedTypes("hand"),"OneToOne", getSampleTypes("total-RNA"),true,
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Séparation ARN 17-200 et > 200nt","small-and-large-rna-isolation","RNA",720,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsExtractionARNSmallLarge(),
				getInstrumentUsedTypes("hand"),"OneToMany", getSampleTypes("RNA"),true,
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		//END LBIOMEG
		return l;
	}

	@Override
	protected List<ExperimentType> getExperimentTypeUAT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ProcessType> getProcessTypeCommon() {
		List<ProcessType> l=new ArrayList<ProcessType>();

		l.add(DescriptionFactory.newProcessType("Extraction ADN / ARN (plancton ou à partir d'aliquot corail poisson)", "dna-rna-extraction-process", 
				ProcessCategory.find.findByCode("sample-prep"), 1,
				null, 
				Arrays.asList(
						getPET("ext-to-dna-rna-extraction-process",-1),
						getPET("grinding",-1),
						getPET("dna-rna-extraction",0)), 
						getExperimentTypes("dna-rna-extraction").get(0), getExperimentTypes("dna-rna-extraction").get(0), getExperimentTypes("ext-to-dna-rna-extraction-process").get(0), 
						DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(DescriptionFactory.newProcessType("Extraction ADN / ARN (corail et poisson)", "grinding-and-dna-rna-extraction", 
				ProcessCategory.find.findByCode("sample-prep"), 2,
				null, 
				Arrays.asList(
						getPET("ext-to-grinding-and-dna-rna-extraction",-1),
						getPET("grinding",0),
						getPET("dna-rna-extraction",1)), 
						getExperimentTypes("grinding").get(0), getExperimentTypes("dna-rna-extraction").get(0), getExperimentTypes("ext-to-grinding-and-dna-rna-extraction").get(0), 
						DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		return l;	
	}
	
	@Override
	protected List<ProcessType> getProcessTypeDEV() {
		List<ProcessType> l=new ArrayList<ProcessType>();

		l.add(DescriptionFactory.newProcessType("Extraction ARN (17-200  et  >200nt)", "small-and-large-rna-extraction", 
				ProcessCategory.find.findByCode("sample-prep"), 3,
				null, 
				Arrays.asList(
						getPET("ext-to-small-and-large-rna-extraction",-1),
						getPET("total-rna-extraction",0),
						getPET("small-and-large-rna-isolation",1)), 
						getExperimentTypes("total-rna-extraction").get(0), getExperimentTypes("small-and-large-rna-isolation").get(0), getExperimentTypes("ext-to-small-and-large-rna-extraction").get(0), 
						DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		return l;	
	}

	@Override
	protected List<ProcessType> getProcessTypePROD() {
		return null;
	}

	@Override
	protected List<ProcessType> getProcessTypeUAT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void getExperimentTypeNodeCommon() {
		//Sample Preparation 
		newExperimentTypeNode("ext-to-grinding-and-dna-rna-extraction", getExperimentTypes("ext-to-grinding-and-dna-rna-extraction").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("grinding",getExperimentTypes("grinding").get(0),false, false,false,getExperimentTypeNodes("ext-to-grinding-and-dna-rna-extraction"),null,null,null).save();
		newExperimentTypeNode("ext-to-dna-rna-extraction-process", getExperimentTypes("ext-to-dna-rna-extraction-process").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("dna-rna-extraction",getExperimentTypes("dna-rna-extraction").get(0),false, false,false,getExperimentTypeNodes("ext-to-dna-rna-extraction-process","grinding"),
				getExperimentTypes("dnase-treatment"),getExperimentTypes("fluo-quantification","chip-migration","gel-migration","control-pcr-and-gel"),getExperimentTypes("aliquoting")).save();
	}

	@Override
	protected void getExperimentTypeNodeDEV() {
		newExperimentTypeNode("ext-to-small-and-large-rna-extraction", getExperimentTypes("ext-to-small-and-large-rna-extraction").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("total-rna-extraction",getExperimentTypes("total-rna-extraction").get(0),false, false,false,getExperimentTypeNodes("ext-to-small-and-large-rna-extraction"),
				getExperimentTypes("dnase-treatment"),getExperimentTypes("fluo-quantification","chip-migration"),null).save();
		newExperimentTypeNode("small-and-large-rna-isolation", getExperimentTypes("small-and-large-rna-isolation").get(0), false, false, false, getExperimentTypeNodes("total-rna-extraction"), 
				getExperimentTypes("dnase-treatment"),getExperimentTypes("fluo-quantification","chip-migration"), null).save();
		
	}

	@Override
	protected void getExperimentTypeNodePROD() {

	}

	@Override
	protected void getExperimentTypeNodeUAT() {
		// TODO Auto-generated method stub

	}

	private List<PropertyDefinition> getPropertyDefinitionsExtractionADNARN() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//InputContainer

		propertyDefinitions.add(newPropertiesDefinition("Sample Type", "sampleTypeCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, "N", null, 
				"single", 15, false, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Projet", "projectCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, null, 
				null, null ,null ,null ,"single", 20, false, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Echantillon", "sampleCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, null, 
				null, null, null, null,"single", 25, false, null,null));

		return propertyDefinitions;
	}

	
	private List<PropertyDefinition> getPropertyDefinitionsExtractionARNtotal() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//InputContainer

		propertyDefinitions.add(newPropertiesDefinition("Sample Type", "sampleTypeCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, "N", 
				Collections.singletonList(DescriptionFactory.newValue("total-RNA","ARN total")),"single", 15, true, "total-RNA",null));
		propertyDefinitions.add(newPropertiesDefinition("Projet", "projectCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, null, 
				null, null ,null ,null ,"single", 20, false, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Echantillon", "sampleCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, null, 
				null, null, null, null,"single", 25, false, null,null));

		return propertyDefinitions;
	}
	
	private List<PropertyDefinition> getPropertyDefinitionsExtractionARNSmallLarge() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//InputContainer

		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, false, null,
				null,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"),"single",13, true,null,"1"));
		
		propertyDefinitions.add(newPropertiesDefinition("Sample Type", "sampleTypeCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, "N", 
				Collections.singletonList(DescriptionFactory.newValue("RNA","ARN")), "single", 15, true, "ARN",null));
		propertyDefinitions.add(newPropertiesDefinition("Projet", "projectCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, null, 
				null, "single", 20, false, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Echantillon", "sampleCode", LevelService.getLevels(Level.CODE.ContainerOut), String.class, true, null, 
				null, "single", 25, false, null,null));

		propertyDefinitions.add(newPropertiesDefinition("taille ARN", "rnaSize", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), String.class, true, "N", 
				DescriptionFactory.newValues("17-200nt",">200nt"), "single", 26, false, null,null));

		
		return propertyDefinitions;
	}


}
