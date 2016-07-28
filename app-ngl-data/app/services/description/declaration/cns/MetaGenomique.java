package services.description.declaration.cns;

import static services.description.DescriptionFactory.newExperimentType;
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
import models.laboratory.common.description.Value;

public class MetaGenomique extends AbstractDeclaration {

	@Override
	protected List<ExperimentType> getExperimentTypeCommon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ExperimentType> getExperimentTypeDEV() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		l.add(newExperimentType("Ext to  MetaGénomique (bq sizée)","ext-to-metagenomic-process-with-sizing",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Ext to MetaGénomique","ext-to-metagenomic-process",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		return l;
	}

	@Override
	protected List<ExperimentType> getExperimentTypePROD() {
		// TODO Auto-generated method stub
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
		List<ProcessType> l = new ArrayList<ProcessType>();

		l.add(DescriptionFactory.newProcessType("MetaGénomique", "metagenomic-process", ProcessCategory.find.findByCode("library"), getPropertiesMetaGenomique(),
				Arrays.asList(getPET("ext-to-metagenomic-process",-1)
						,getPET("dna-rna-extraction",-1)
						, getPET("fragmentation",0)
						, getPET("dna-illumina-indexed-library",1)
						, getPET("pcr-amplification-and-purification",2)
						, getPET("solution-stock",3)
						, getPET("prepa-flowcell",4)
						, getPET("prepa-fc-ordered",4)
						, getPET("illumina-depot",5)), 
				getExperimentTypes("fragmentation").get(0), getExperimentTypes("illumina-depot").get(0), getExperimentTypes("ext-to-metagenomic-process").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(DescriptionFactory.newProcessType("MetaGénomique (bq sizée)", "metagenomic-process-with-sizing", ProcessCategory.find.findByCode("library"), getPropertiesMetaGenomiqueWithSizing(),
				Arrays.asList(getPET("ext-to-metagenomic-process-with-sizing",-1)
						,getPET("dna-rna-extraction",-1)
						, getPET("fragmentation",0)
						, getPET("dna-illumina-indexed-library",1)
						, getPET("pcr-amplification-and-purification",2)
						, getPET("sizing",3)
						, getPET("solution-stock",4)
						, getPET("prepa-flowcell",5)
						, getPET("prepa-fc-ordered",5)
						, getPET("illumina-depot",6)), 
				getExperimentTypes("fragmentation").get(0), getExperimentTypes("illumina-depot").get(0), getExperimentTypes("ext-to-metagenomic-process").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		return l;
	}

	private List<PropertyDefinition> getPropertiesMetaGenomiqueWithSizing() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Type processus Banque", "libProcessTypeCode", LevelService.getLevels(Level.CODE.Process), String.class, true, null, getLibProcessDB(), 
				null,null,null,"single", 13, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Protocole banque DNA", "dnaLibraryProtocol", LevelService.getLevels(Level.CODE.Process), String.class, true, null, DescriptionFactory.newValues("NEB Ultra 2","low cost","super low cost"), 
				null,null,null,"single", 14, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Objectif sizing 1", "sizingGoal", LevelService.getLevels(Level.CODE.Process), String.class, true, null, DescriptionFactory.newValues("ss0.6/0.53","ss0.7/0.58"), 
				null,null,null,"single", 15, true, null, null));

		propertyDefinitions.addAll(RunIllumina.getPropertyDefinitionsIlluminaDepotCNS());
		return propertyDefinitions;
	}

	private List<PropertyDefinition> getPropertiesMetaGenomique() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Type processus Banque", "libProcessTypeCode", LevelService.getLevels(Level.CODE.Process), String.class, true, null, getLibProcessDA(), 
				null,null,null,"single", 13, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Protocole banque DNA", "dnaLibraryProtocol", LevelService.getLevels(Level.CODE.Process), String.class, true, null, DescriptionFactory.newValues("NEB Ultra 2","low cost","super low cost"), 
				null,null,null,"single", 14, true, null, null));

		propertyDefinitions.addAll(RunIllumina.getPropertyDefinitionsIlluminaDepotCNS());
		return propertyDefinitions;
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
	
	
	private List<Value> getLibProcessDA() {
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("DA", "DA - DNAseq "));
		return values;
	}
	
	private List<Value> getLibProcessDB() {
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("DB", "DB - DNAseq avec sizing"));
		return values;
	}

}
