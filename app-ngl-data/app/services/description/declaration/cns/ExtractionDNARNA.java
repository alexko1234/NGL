package services.description.declaration.cns;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.declaration.AbstractDeclaration;

public class ExtractionDNARNA extends AbstractDeclaration{

	@Override
	protected List<ExperimentType> getExperimentTypePROD() {
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

		l.add(newExperimentType("Extraction ADN / ARN ","dna-rna-extraction","AN",700,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsExtractionADNARN(),
				getInstrumentUsedTypes("cryobroyeur","hand"),"OneToMany", getSampleTypes("DNA","RNA"),true,
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		return l;
	}

	@Override
	protected List<ExperimentType> getExperimentTypeDEV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ExperimentType> getExperimentTypeUAT() {
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
		List<ProcessType> l=new ArrayList<ProcessType>();
				
		l.add(DescriptionFactory.newProcessType("Extraction ADN / ARN (plancton)", "dna-rna-extraction-process", ProcessCategory.find.findByCode("sample-prep"), null,
				Arrays.asList(getPET("ext-to-dna-rna-extraction-process",-1),getPET("dna-rna-extraction",0)), 
				getExperimentTypes("dna-rna-extraction").get(0), getExperimentTypes("dna-rna-extraction").get(0), getExperimentTypes("ext-to-dna-rna-extraction-process").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(DescriptionFactory.newProcessType("Extraction ADN / ARN (corail)", "grinding-and-dna-rna-extraction", ProcessCategory.find.findByCode("sample-prep"), null,
				Arrays.asList(getPET("ext-to-grinding-and-dna-rna-extraction",-1),getPET("grinding",0),getPET("dna-rna-extraction",1)), 
				getExperimentTypes("grinding").get(0), getExperimentTypes("dna-rna-extraction").get(0), getExperimentTypes("ext-to-grinding-and-dna-rna-extraction").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		return l;
	}

	@Override
	protected List<ProcessType> getProcessTypeUAT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void getExperimentTypeNodeDEV() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void getExperimentTypeNodePROD() {
		//Sample Preparation 
		newExperimentTypeNode("ext-to-grinding-and-dna-rna-extraction", getExperimentTypes("ext-to-grinding-and-dna-rna-extraction").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("grinding",getExperimentTypes("grinding").get(0),false, false,false,getExperimentTypeNodes("ext-to-grinding-and-dna-rna-extraction"),null,null,null).save();
		newExperimentTypeNode("ext-to-dna-rna-extraction-process", getExperimentTypes("ext-to-dna-rna-extraction-process").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("dna-rna-extraction",getExperimentTypes("dna-rna-extraction").get(0),false, false,false,getExperimentTypeNodes("ext-to-dna-rna-extraction-process","grinding"),getExperimentTypes("dnase-treatment"),getExperimentTypes("fluo-quantification","chip-migration","gel-migration","control-pcr-and-gel"),getExperimentTypes("aliquoting")).save();

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

}
