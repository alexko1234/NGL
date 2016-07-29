package services.description.declaration.cns;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.declaration.AbstractDeclaration;
import services.description.experiment.AbstractExperimentService;

public class MetaTProcess extends AbstractDeclaration {


	@Override
	protected List<ExperimentType> getExperimentTypeCommon() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<ExperimentType> getExperimentTypeDEV() {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		l.add(newExperimentType("Ext to MetaT cDNA frg","ext-to-cDNA-frg-transcriptomic-process",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Synthèse cDNA","cdna-synthesis","cDNA",800,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsCdnaSynthesis(),
				AbstractExperimentService.getInstrumentUsedTypes("thermocycler"),"OneToOne", null,true,
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Ext to Metagenomic","ext-to-metagenomic-process",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Ext to Metagenomic with sizing","ext-to-metagenomic-process-with-sizing",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(newExperimentType("Fragmentation","fragmentation",null,200,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionFragmentation(),
				getInstrumentUsedTypes("covaris-e220-ext","covaris-e220","biomek-fx-and covaris-e220"),"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS) ));
		
		l.add(newExperimentType("Ext to MetaT bq RNA","ext-to-rna-lib-transcriptomic-process",null,-1,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
				DescriptionFactory.getInstitutes(Constants.CODE.CNS)));

		l.add(newExperimentType("Banque RNA","rna-illumina-indexed-library","LIB",800,
				ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsRNAIlluminaIndexedLibrary(),
				AbstractExperimentService.getInstrumentUsedTypes("biomek-fx-and-cDNA-thermocycler","hand"),"OneToOne", null,true,
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
	protected List<ProcessType> getProcessTypeDEV() {
		List<ProcessType> l = new ArrayList<ProcessType>();
		
		l.add(DescriptionFactory.newProcessType("MetaT bq RNA", "rna-lib-transcriptomic-process", ProcessCategory.find.findByCode("library"), getPropertiesMetaTRNA(),
				Arrays.asList(getPET("ext-to-rna-lib-transcriptomic-process",-1)
						,getPET("dna-rna-extraction",-1)
						, getPET("rna-illumina-indexed-library",0)
						, getPET("pcr-amplification-and-purification",1)
						, getPET("solution-stock",2)
						, getPET("prepa-flowcell",3)
						, getPET("prepa-fc-ordered",3)
						, getPET("illumina-depot",4)), 
				getExperimentTypes("rna-illumina-indexed-library").get(0), getExperimentTypes("illumina-depot").get(0), getExperimentTypes("ext-to-rna-lib-transcriptomic-process").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
		l.add(DescriptionFactory.newProcessType("MetaT cDNA frg", "cDNA-frg-transcriptomic-process", ProcessCategory.find.findByCode("library"), getPropertiesMetaTcDNA(),
				Arrays.asList(getPET("ext-to-cDNA-frg-transcriptomic-process",-1)
						,getPET("dna-rna-extraction",-1)
						, getPET("cdna-synthesis",0)
						, getPET("fragmentation",1)
						, getPET("dna-illumina-indexed-library",2)
						, getPET("pcr-amplification-and-purification",3)
						, getPET("solution-stock",4)
						, getPET("prepa-flowcell",5)
						, getPET("prepa-fc-ordered",5)
						, getPET("illumina-depot",6)), 
				getExperimentTypes("cdna-synthesis").get(0), getExperimentTypes("illumina-depot").get(0), getExperimentTypes("ext-to-cDNA-frg-transcriptomic-process").get(0), DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		return l;
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
	protected void getExperimentTypeNodeCommon() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void getExperimentTypeNodeDEV() {
		//Metatranscriptome
		
		newExperimentTypeNode("ext-to-cDNA-frg-transcriptomic-process", AbstractExperimentService.getExperimentTypes("ext-to-cDNA-frg-transcriptomic-process").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("cdna-synthesis",AbstractExperimentService.getExperimentTypes("cdna-synthesis").get(0),false, false,false,AbstractExperimentService.getExperimentTypeNodes("dna-rna-extraction","ext-to-cDNA-frg-transcriptomic-process"),null,null,null).save();
		
		newExperimentTypeNode("ext-to-metagenomic-process", AbstractExperimentService.getExperimentTypes("ext-to-metagenomic-process").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("ext-to-metagenomic-process-with-sizing", AbstractExperimentService.getExperimentTypes("ext-to-metagenomic-process-with-sizing").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("fragmentation", getExperimentTypes("fragmentation").get(0), false, false, getExperimentTypeNodes("cdna-synthesis","ext-to-metagenomic-process","ext-to-metagenomic-process-with-sizing"), 
				null,  getExperimentTypes("chip-migration")).save();
		
		newExperimentTypeNode("ext-to-rna-lib-transcriptomic-process", AbstractExperimentService.getExperimentTypes("ext-to-rna-lib-transcriptomic-process").get(0), false, false, false, null, null, null, null).save();
		newExperimentTypeNode("rna-illumina-indexed-library",AbstractExperimentService.getExperimentTypes("rna-illumina-indexed-library").get(0),false, false,false,AbstractExperimentService.getExperimentTypeNodes("dna-rna-extraction","ext-to-rna-lib-transcriptomic-process","fragmentation"),null,null,null).save();
	}

	@Override
	protected void getExperimentTypeNodePROD() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void getExperimentTypeNodeUAT() {
		// TODO Auto-generated method stub

	}
	
	private static List<PropertyDefinition> getPropertyDefinitionsRNAIlluminaIndexedLibrary() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 12, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, false, null,
				null,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"),"single",13, true,null,null));

		propertyDefinitions.add(newPropertiesDefinition("Tag", "tag", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), String.class, true, null, 
				MetaBarCoding.getTagIllumina(), null,null,null,"single", 14, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Catégorie de Tag", "tagCategory", LevelService.getLevels(Level.CODE.ContainerOut,Level.CODE.Content), String.class, true, null, 
				MetaBarCoding.getTagCategoriesIllumina(), null,null,null,"single", 15, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Orientation du brin séquencé read 1", "strandOrientation", LevelService.getLevels(Level.CODE.Experiment,Level.CODE.Content), String.class, true, null, 
				getStrandOrientation(), null, null, null,"single", 1, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Type synthèse cDNA", "cDNAsynthesisType", LevelService.getLevels(Level.CODE.Experiment,Level.CODE.Content), String.class, true, null, 
				DescriptionFactory.newValues("random","oligoDT"), null, null, null,"single", 1, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Protocole bq RNA", "rnaLibProtocol", LevelService.getLevels(Level.CODE.Experiment,Level.CODE.Content), String.class, true, null, 
				null, null, null, null,"single", 1, false, null,null));
		
		return propertyDefinitions;
	}

	private static List<Value> getStrandOrientation(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("forward", "forward"));		
		values.add(DescriptionFactory.newValue("reverse", "reverse"));		
		values.add(DescriptionFactory.newValue("unstranded", "unstranded"));		
		return values;	
	}

	private List<PropertyDefinition> getPropertyDefinitionsCdnaSynthesis() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, false, null,
				null,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"),"single",12, true,null,null));

		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 13, true, null,null));
		
		propertyDefinitions.add(newPropertiesDefinition("Orientation du brin séquencé read 1", "strandOrientation", LevelService.getLevels(Level.CODE.Experiment,Level.CODE.Content), String.class, true, null, 
				getStrandOrientation(), null, null, null,"single", 1, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Type synthèse cDNA", "cDNAsynthesisType", LevelService.getLevels(Level.CODE.Experiment,Level.CODE.Content), String.class, true, null, 
				DescriptionFactory.newValues("random","oligoDT"), null, null, null,"single", 1, true, null,null));

		propertyDefinitions.add(newPropertiesDefinition("Protocole bq RNA", "rnaLibProtocol", LevelService.getLevels(Level.CODE.Experiment,Level.CODE.Content), String.class, true, null, 
				null, null, null, null,"single", 1, false, null,null));

		return propertyDefinitions;
	}

	
	private static List<PropertyDefinition> getPropertyDefinitionFragmentation() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Volume à prélever", "requiredVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null,
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",11, false,null, "1"));

		propertyDefinitions.add(newPropertiesDefinition("Volume engagé", "inputVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single", 13, false, null,"1"));

		propertyDefinitions.add(newPropertiesDefinition("Quantité à engager","requiredQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null,
				null,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"),"single",14, true,null,"1"));
		
		propertyDefinitions.add(newPropertiesDefinition("Quantitée réellement engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, null,
				null,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_QUANTITY),MeasureUnit.find.findByCode( "ng"),MeasureUnit.find.findByCode( "ng"),"single",12, false,null,"1"));


		propertyDefinitions.add(newPropertiesDefinition("Volume tampon", "bufferVolume", LevelService.getLevels(Level.CODE.ContainerIn), Double.class, true, null
				,null,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_VOLUME),MeasureUnit.find.findByCode( "µL"),MeasureUnit.find.findByCode( "µL"),"single",18, false,null,"1"));
		
		return propertyDefinitions;
	}
	
	
	private List<PropertyDefinition> getPropertiesMetaTcDNA() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Type processus Banque", "libProcessTypeCode", LevelService.getLevels(Level.CODE.Process), String.class, true, null, getBanqueProcessTypeMetaRA(), 
				null,null,null,"single", 13, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Objectif Déplétion", "depletionMethod", LevelService.getLevels(Level.CODE.Process), String.class, true, null, DescriptionFactory.newValues("pas de déplétion","déplétion prok","déplétion polyA"), 
				null,null,null,"single", 14, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Protocole synthese cDNA", "cDNAsynthesisMethod", LevelService.getLevels(Level.CODE.Process), String.class, true, null, DescriptionFactory.newValues("Smarter V4","Ovation RNAseq system v2"), 
				null,null,null,"single", 15, true, null, null));

		propertyDefinitions.addAll(RunIllumina.getPropertyDefinitionsIlluminaDepotCNS());
		return propertyDefinitions;
	}


	


	private List<PropertyDefinition> getPropertiesMetaTRNA() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Type processus Banque", "libProcessTypeCode", LevelService.getLevels(Level.CODE.Process), String.class, true, null, getBanqueProcessTypeMetaRB(), 
				null,null,null,"single", 13, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Objectif Déplétion", "depletionMethod", LevelService.getLevels(Level.CODE.Process), String.class, true, null, DescriptionFactory.newValues("pas de déplétion","déplétion prok","déplétion polyA"), 
				null,null,null,"single", 14, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Protocole synthese cDNA", "cDNAsynthesisMethod", LevelService.getLevels(Level.CODE.Process), String.class, true, null, DescriptionFactory.newValues("TruSeq Stranded poly A","TruSeq Stranded Proc","Smarter Stranded"), 
				null,null,null,"single", 15, true, null, null));

		propertyDefinitions.addAll(RunIllumina.getPropertyDefinitionsIlluminaDepotCNS());
		return propertyDefinitions;
	}

	private List<Value> getBanqueProcessTypeMetaRA() {
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("RA", "RA - RNAseq"));
		return values;
	}
	
	private List<Value> getBanqueProcessTypeMetaRB() {
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("RB", "RB - RNAseq stranded"));
		return values;
	}

}
