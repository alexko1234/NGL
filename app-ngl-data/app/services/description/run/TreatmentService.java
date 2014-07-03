package services.description.run;

import java.io.File;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.typesafe.config.ConfigFactory;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.TreatmentContext;
import models.laboratory.run.description.TreatmentType;
import models.laboratory.run.description.TreatmentTypeContext;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;

public class TreatmentService {

	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{		
		DAOHelpers.removeAll(TreatmentContext.class, TreatmentContext.find);
		DAOHelpers.removeAll(TreatmentType.class, TreatmentType.find);
		DAOHelpers.removeAll(TreatmentCategory.class, TreatmentCategory.find);		
		saveTreatmentCategory(errors);
		saveTreatmentContext(errors);
		saveTreatmentType(errors);	
	}
		
	public static void saveTreatmentCategory(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentCategory> l = new ArrayList<TreatmentCategory>();
		for (TreatmentCategory.CODE code : TreatmentCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(TreatmentCategory.class, code.name(), code.name()));
		}
		DAOHelpers.saveModels(TreatmentCategory.class, l, errors);
	}
	
	public static void saveTreatmentContext(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentContext> l = new ArrayList<TreatmentContext>();
		l.add(DescriptionFactory.newTreatmentContext("Default","default"));
		l.add(DescriptionFactory.newTreatmentContext("Read1","read1"));
		l.add(DescriptionFactory.newTreatmentContext("Read2","read2"));
		l.add(DescriptionFactory.newTreatmentContext("Pairs","pairs"));
		l.add(DescriptionFactory.newTreatmentContext("Single","single"));
		
		DAOHelpers.saveModels(TreatmentContext.class, l, errors);
	}
	
	public static void saveTreatmentType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentType> l = new ArrayList<TreatmentType>();
		// common CNS - CNG
		l.add(DescriptionFactory.newTreatmentType("SAV","sav", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.sequencing.name()), "sav", 
				getSAVPropertyDefinitions(), 
				Arrays.asList(getTreatmentTypeContext("read1", Boolean.TRUE), getTreatmentTypeContext("read2", Boolean.FALSE)), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "10"));
		
		l.add(DescriptionFactory.newTreatmentType("NGSRG","ngsrg-illumina", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.ngsrg.name()), "ngsrg", 
				getNGSRGPropertyDefinitions(), 
				getTreatmentTypeContexts("default"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "20"));		
		
		l.add(DescriptionFactory.newTreatmentType("Global","global", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.global.name()), "global", 
				getReadSetPropertyDefinitions(), 
				getTreatmentTypeContexts("default"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "0"));
		
		l.add(DescriptionFactory.newTreatmentType("Read Quality","read-quality", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "readQualityRaw,readQualityClean", 
				getReadQualityPropertyDefinitions(), 
				Arrays.asList(getTreatmentTypeContext("read1",Boolean.TRUE), getTreatmentTypeContext("read2", Boolean.FALSE)), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "30,83"));
		
		l.add(DescriptionFactory.newTreatmentType("Duplicates","duplicates", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "duplicatesRaw,duplicatesClean", 
				getDuplicatesPropertyDefinitions(), 
				Arrays.asList(getTreatmentTypeContext("read1",Boolean.TRUE), getTreatmentTypeContext("read2", Boolean.FALSE), getTreatmentTypeContext("pairs", Boolean.FALSE)), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "32,86"));
		
		l.add(DescriptionFactory.newTreatmentType("Mapping","mapping", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "mapping", 
				getMappingPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs", "default"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "90"));
		
		l.add(DescriptionFactory.newTreatmentType("Trimming","trimming", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "trimmingStd,trimmingVector", 
				getTrimmingPropertyDefinitions(), 
				Arrays.asList(getTreatmentTypeContext("read1",Boolean.TRUE), getTreatmentTypeContext("read2", Boolean.FALSE), getTreatmentTypeContext("pairs", Boolean.FALSE), 
				getTreatmentTypeContext("single", Boolean.FALSE)), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS, Institute.CODE.CNG), "33,50"));
		
		l.add(DescriptionFactory.newTreatmentType("Contamination","contamination", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "contaminationColi,contaminationVector,contaminationPhiX", 
				getContaminationPropertyDefinitions(), 
				Arrays.asList(getTreatmentTypeContext("read1",Boolean.FALSE), getTreatmentTypeContext("pairs", Boolean.FALSE), 
				getTreatmentTypeContext("single", Boolean.FALSE)), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS), "35,36,60"));
		
		l.add(DescriptionFactory.newTreatmentType("Taxonomy","taxonomy", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "taxonomy", 
				getTaxonomyPropertyDefinitions(), 
				getTreatmentTypeContexts("read1"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS), "70"));
		
		l.add(DescriptionFactory.newTreatmentType("Sorting Ribo","sorting-ribo", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "sortingRibo", 
				getSortingRiboPropertyDefinitions(), 
				Arrays.asList(getTreatmentTypeContext("read1",Boolean.TRUE), getTreatmentTypeContext("read2", Boolean.FALSE), getTreatmentTypeContext("pairs", Boolean.FALSE), getTreatmentTypeContext("single", Boolean.FALSE)), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS), "80"));
		
		l.add(DescriptionFactory.newTreatmentType("Merging","merging", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "merging", 
				getMergingPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS), "100"));
		
		l.add(DescriptionFactory.newTreatmentType("Merging BA","merging-ba", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.ba.name()), "mergingBA", 
				getMergingBAPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS), "110"));
		
		l.add(DescriptionFactory.newTreatmentType("Assembly","assembly", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.ba.name()), "assembly", 
				getAssemblyPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS), "120"));
		
		l.add(DescriptionFactory.newTreatmentType("Scaffolding","scaffolding", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.ba.name()), "scaffolding", 
				getScaffoldingPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS), "130"));

		l.add(DescriptionFactory.newTreatmentType("Gap Closing","gapClosing", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.ba.name()), "gapClosing", 
				getGapClosingPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNS), "140"));

		//specific CNG 
		l.add(DescriptionFactory.newTreatmentType("alignSingleRead BLAT","alignsingleread-blat", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "alignSingleReadBLATRaw", 
				getASRBPropertyDefinitions(), 
				getTreatmentTypeContexts("read1", "read2"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG), "50"));
		
		l.add(DescriptionFactory.newTreatmentType("alignSingleRead SOAP2","alignsingleread-soap2", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "alignSingleReadSOAP2Raw", 
				getASRSPropertyDefinitions(), 
				getTreatmentTypeContexts("read1", "read2"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG), "60"));
		
		l.add(DescriptionFactory.newTreatmentType("Exome","exome", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "exome", 
				getExomeTreatmentPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG), "100"));
		
		l.add(DescriptionFactory.newTreatmentType("Whole Genome","whole-genome", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "wholeGenome", 
				getWholeExomeTreatmentPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG), "110"));
		
		l.add(DescriptionFactory.newTreatmentType("RNAseq","rna-seq", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "RNAseq", 
				getRnaSeqTreatmentPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG), "120"));
		
		l.add(DescriptionFactory.newTreatmentType("ChiPseq","chip-seq", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "ChiPseq", 
				getChiPSeqTreatmentPropertyDefinitions(), 
				getTreatmentTypeContexts("read1"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG), "130"));
		
		l.add(DescriptionFactory.newTreatmentType("ChiPseq-PE","chipseq-pe", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "ChiPseqPE", 
				getChiPSeqPETreatmentPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG), "130"));
		
		l.add(DescriptionFactory.newTreatmentType("FAIREseq","faire-seq", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "FAIREseq", 
				getFaireSeqTreatmentPropertyDefinitions(), 
				getTreatmentTypeContexts("read1"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG), "150"));
		
		//TODO : level parameter must be verified
		l.add(DescriptionFactory.newTreatmentType("Sample Control","sample-control", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "sampleControl", 
				getSampleControlTreatmentPropertyDefinitions(), 
				getTreatmentTypeContexts("pairs"), 
				DescriptionFactory.getInstitutes(Institute.CODE.CNG), "160"));
		DAOHelpers.saveModels(TreatmentType.class, l, errors);
	}
	
	private static List<TreatmentTypeContext> getTreatmentTypeContexts(String...codes) throws DAOException {
		List<TreatmentTypeContext> contexts = new ArrayList<TreatmentTypeContext>();
		for(String code : codes){
			contexts.add(getTreatmentTypeContext(code, Boolean.TRUE));
		}		
		return contexts;
	}

	private static TreatmentTypeContext getTreatmentTypeContext(String code, Boolean required) throws DAOException {
		TreatmentContext tc = DAOHelpers.getModelByCode(TreatmentContext.class, TreatmentContext.find, code);
		TreatmentTypeContext ttc = new TreatmentTypeContext(tc, required);
		return ttc;	
	}
	
	
	
	
	private static List<PropertyDefinition> getASRBPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Sample input","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of mapped reads","mappedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent of mapped reads","mappedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of reads with 100% of matching bases","reads100pcMatchBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent of reads with 100% of matching bases","reads100pcMatchBasesPerc", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of reads with >=90% of matching bases","reads90pcMatchBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent of reads with >=90% of matching bases","reads90pcMatchBasesPerc", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of aligned bases","alignedBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of errors","nbErrors", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of insertions","nbInsertions", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of deletions","nbDeletions", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of mismatches","nbMismatches", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of perfect reads","nbPerfectReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reference genome coverage","coverageDistribution", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Errors position in reads","errorPosition", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("observed quality values in reads","qualityValueDistribution", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "single"));
		return propertyDefinitions;
	}
	

	private static List<PropertyDefinition> getASRSPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Sample input","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of mapped reads","mappedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent of mapped reads","mappedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of aligned bases","alignedBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of mismatches","nbMismatches", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent of mismatches","mismatchesPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Errors position in reads (SOAP 2)","errorPosition", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "single"));
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getExomeTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb reads","nbReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mapped reads","mappedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent Mapped reads","mappedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent Pairing","pairingPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Duplicated reads","duplicatedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent Duplicated Reads","duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Size Regions Tiled","sizeRegionsTiled", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent target","targetPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent NT 30X","percentNT30X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent NT 20X","percentNT20X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent NT 10X","percentNT10X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent NT 5X","percentNT5X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb regions 0X","nbRegions0X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent regions 0X","regions0XPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb regions full cover","nbRegionsFullCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent Regions Full Cover","regionsFullCoverPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mean cover","meanCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Median cover","medianCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("SD cover","sdCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getWholeExomeTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb reads","nbReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mapped reads","mappedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent Mapped reads","mappedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent Pairing","pairingPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Duplicated reads","duplicatedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent Duplicated Reads","duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent NT 30X","percentNT30X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent NT 20X","percentNT20X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent NT 10X","percentNT10X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent NT 5X","percentNT5X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb regions 0X","nbRegions0X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent regions 0X","regions0XPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mean cover","meanCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Median cover","medianCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("SD cover","sdCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getRnaSeqTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb reads","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% both surviving","storedPairsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% R1 only surviving","storedForwardReadPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% R2 only surviving","storedReverseReadPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% dropped","rejectedPairsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% mapped reads","alignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% unique mapped reads","uniqueReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% unique mapped reads of total","uniqueAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% base mismatch","mismatchPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% duplicated mapped reads","duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mean insert length","insertLengthMean", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("SD insert length","insertLengthMeanSd", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% R1 sense","1SenseAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% R2 sense","2SenseAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% rRNA","rRNAPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% intragenic","intragenicReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% exonic","exonicReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb protein coding genes","proteinCodingGenes", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb protein coding transcripts","proteinCodingTranscripts", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb lincRNA genes","lncRNAGenes", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb lincRNA transcripts","lncRNATranscripts", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getChiPSeqTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb reads","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% both surviving","storedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% dropped","rejectedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% mapped reads","alignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% unique mapped reads","uniqueReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% unique mapped reads of total","uniqueAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% base mismatch","mismatchPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% duplicated mapped reads","duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mean insert length","insertLengthMean", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("SD insert length","insertLengthMeanSd", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% rRNA","rRNAPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		return propertyDefinitions;
	}
		
	private static List<PropertyDefinition> getChiPSeqPETreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb reads","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% both surviving","storedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% R1 only surviving", "storedForwardReadPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% R2 only surviving", "storedReverseReadPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% dropped", "rejectedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% mapped reads", "alignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% unique mapped reads", "uniqueReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% unique mapped reads of total", "uniqueAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% base mismatch", "mismatchPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% duplicated mapped reads", "duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mean insert length", "insertLengthMean", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("SD insert length", "insertLengthMeanSd", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% R1 sense", "1SenseAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% R2 sense", "2SenseAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% rRNA","rRNAPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% intragenic", "intragenicReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% exonic", "exonicReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getFaireSeqTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb reads","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% both surviving","storedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% dropped","rejectedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mapping Rate","alignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Unique Rate of mapped","uniqueReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mapped Unique Rate of Total","uniqueAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Base Mismatch Rate","mismatchPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Duplication Rate of Mapped","duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Insert Length Mean","insertLengthMean", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Standard Deviation","insertLengthMeanSd", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNA rate","rRNAPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "single"));
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getSampleControlTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//TODO : Level "pairs" must be confirmed 
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Sample input","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Sample sexe","sampleSexe", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), String.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Samples comparison","samplesComparison", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getNGSRGPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        //Run level
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Position flowcell","flowcellPosition", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), String.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb cycles","nbCycle", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Long.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Version flowcell","flowcellVersion", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), String.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb clusters filt. (total)","nbClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Run, Level.CODE.Lane, Level.CODE.Default), Long.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% clusters filtrés (Illumina)","percentClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Run, Level.CODE.Lane, Level.CODE.Default), Double.class, false, "single"));        
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb bases (total)","nbBase", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Long.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mismatch autorisé pour le démultiplexage","mismatch", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Boolean.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Piste contrôle","controlLane", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Integer.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Version RTA","rtaVersion", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), String.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb clusters (total)","nbClusterTotal", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Long.class, true, "single"));
        //Lane & ReadSet level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb clusters","nbCluster", LevelService.getLevels(Level.CODE.Lane, Level.CODE.ReadSet, Level.CODE.Default), Long.class, true, "single"));
        // Lane level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Prephasing","prephasing", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% séquences filtrées (interne)","percentClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Double.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Phasing","phasing", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb cycles read index2","nbCycleReadIndex2", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb cycles read2","nbCycleRead2", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb cycles read1","nbCycleRead1", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb cycles read index1","nbCycleReadIndex1", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb bases (filtre Illumina + interne)","nbBaseInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Long.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb séquences (filtre Illumina + interne)","nbClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Long.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% Perte","seqLossPercent", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Float.class, false, "single"));
        // ReadSet level
        //nbCluster define in the lane level for the 2 levels
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% >= Q30","Q30", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Double.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb bases","nbBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Long.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Fraction de run","fraction", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Double.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Score qualité moyen","qualityScore", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Double.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb Read Illumina","nbReadIllumina", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Integer.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% séquences valides/piste","validSeqPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Float.class, false, "single"));
                 
        return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getReadSetPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        // just readset level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Useful sequences","usefulSequences", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Long.class, true, "single"));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Useful bases","usefulBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Long.class, true, "single"));
        return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getSAVPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Density","clusterDensity",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("+/-","clusterDensityStd",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% Cluster PF","clusterPFPerc",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("+/-","clusterPFPercStd",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Phasing","phasing",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Prephasing","prephasing",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reads","reads",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reads PF","readsPF",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("greaterQ30Perc","greaterQ30Perc",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Cycles Err Rated","cyclesErrRated",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% Aligned","alignedPerc",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("+/-","alignedPercStd",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Error Rate","errorRatePerc",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("+/-","errorRatePercStd",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Error Rate cycle 35","errorRatePercCycle35",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1,Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("+/-","errorRatePercCycle35Std",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Error Rate cycle 75","errorRatePercCycle75",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("+/-","errorRatePercCycle75Std",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Error Rate cycle 100","errorRatePercCycle100",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("+/-","errorRatePercCycle100Std",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Intensity cycle 1","intensityCycle1",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("+/-","intensityCycle1Std",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1,Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% Intensity cycle 20","intensityCycle20Perc",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("+/-","intensityCycle20PercStd",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getReadQualityPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Sample input","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Quality scores for each read position","qualScore",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "img"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Distribution of nucleotids for each read position","nuclDistribution",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "img"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent of reads with sequenced N","readWithNpercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "img"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Read size distribution","readSizeDistribution",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "img"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Contamination adaptateurs : liste d'adaptateurs vs cycles run","adapterContamination",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "img"));		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Adaptateurs sur-représentés","adapters",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), String.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Proportion of reads for different values of G+C content","GCDistribution",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "img"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Position of N in reads","positionN",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Image.class, true, "img"));				
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Suspected Kmers (Kmer)","suspectedKmers.Kmer",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), String.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Suspected Kmers (Nb occurences)","suspectedKmers.nbOccurences",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, false, "object_list"));		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Suspected primers","suspectedPrimers",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), String.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Estimation du nb de lectures de taille","maxSizeReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Pourcentage de lectures de taille","maxSizeReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Taille maximale de lecture","maxSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, false, "single"));
		
		//new properties to generate image
		if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("adapterContaminationDetails.adapterName","adapterContaminationDetails.adapterName",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), String.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("adapterContaminationDetails.contaminationIntensities","adapterContaminationDetails.contaminationIntensities",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), List.class, true, "object_list"));
			
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readWithNpercentDetails.numberOfN","readWithNpercentDetails.numberOfN",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readWithNpercentDetails.percentOfReads","readWithNpercentDetails.percentOfReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
			
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("positionNdetails.positionInReads","positionNdetails.positionInReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("positionNdetails.numberOfN","positionNdetails.numberOfN",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readSizeDistributionDetails.readsLength","readSizeDistributionDetails.readsLength",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readSizeDistributionDetails.percentOfReads","readSizeDistributionDetails.percentOfReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
			
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualScoreDetails.position","qualScoreDetails.position",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualScoreDetails.minQualityScore","qualScoreDetails.minQualityScore",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualScoreDetails.maxQualityScore","qualScoreDetails.maxQualityScore",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualScoreDetails.meanQualityScore","qualScoreDetails.meanQualityScore",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualScoreDetails.Q1","qualScoreDetails.Q1",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualScoreDetails.medianQualityScore","qualScoreDetails.medianQualityScore",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualScoreDetails.Q3","qualScoreDetails.Q3",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualScoreDetails.lowerWhisker","qualScoreDetails.lowerWhisker",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualScoreDetails.upperWhisker","qualScoreDetails.upperWhisker",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nuclDistributionDetails.readPosition","nuclDistributionDetails.readPosition",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nuclDistributionDetails.APercent","nuclDistributionDetails.APercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nuclDistributionDetails.CPercent","nuclDistributionDetails.CPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nuclDistributionDetails.GPercent","nuclDistributionDetails.GPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nuclDistributionDetails.TPercent","nuclDistributionDetails.TPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nuclDistributionDetails.NPercent","nuclDistributionDetails.NPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
	
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("GCDistributionDetails.percentGCcontent","GCDistributionDetails.percentGCcontent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
			propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("GCDistributionDetails.percentOfReads","GCDistributionDetails.percentOfReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "object_list"));
		}
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getDuplicatesPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Sample input","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimation lectures dupliquées","estimateDuplicatedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimation % lectures dupliquées","estimateDuplicatedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("lectures dupliquées X+1 fois (nb)","estimateDuplicatedReadsNTimes.times",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Integer.class, true, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("lectures dupliquées X+1 fois (%)","estimateDuplicatedReadsNTimes.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Float.class, true, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimation lectures uniques","estimateUniqueReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimation % lectures uniques","estimateUniqueReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Float.class, true, "single"));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getTrimmingPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Size range","sizeRange",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), String.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reads input","readsInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reads output","readsOutput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reads noTrim.","readsNoTrim",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reads noTrimPercent","readsNoTrimPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reads trim.","readsTrim",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% Reads trim.","readsTrimPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nucleotides trim.","nucleotidesTrim",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Rejected (short)","trimRejectedShort",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Rejected (length0)","trimRejectedLength0",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Trim. Stored","trimStored",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Stored pairs","storedPairs",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Rejected pairs","rejectedPairs",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("stored singleton","storedSingleton",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Single), Long.class, true, "single"));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getContaminationPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reads input","readsInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Pairs, Level.CODE.Single), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Removed reads","removedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Pairs, Level.CODE.Single), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Remaining reads","remainingReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Pairs, Level.CODE.Single), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% removed reads","removedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Pairs, Level.CODE.Single), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Remaining nucleotides","remainingNucleotides",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Pairs), Long.class, false, "single"));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getTaxonomyPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Sample input","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Organisme","organism",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Taxonomie","taxonomy",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true, "list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan par taxon (taxon)","taxonBilan.taxon",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan par taxon (nb seq)","taxonBilan.nbSeq",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Long.class, true, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan par taxon (%)","taxonBilan.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "object_list"));	
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan par division (division)","divisionBilan.division",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true,
				DescriptionFactory.newValues("eukaryota","bacteria","cellular organisms","archaea","viruses"), "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan par division (nb seq)","divisionBilan.nbSeq",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Long.class, true, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan par division (%)","divisionBilan.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan par mot-clé (mot-clé)","keywordBilan.keyword",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true, 
				DescriptionFactory.newValues("mitochondri","virus","chloroplast","transposase",	"BAC"), "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan par mot-clé (nb seq)","keywordBilan.nbSeq",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Long.class, true, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan par mot-clé (%)","keywordBilan.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("krona","krona",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), File.class, true, "file"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Arbre phylogénétique","phylogeneticTree",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Image.class, false, "img"));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getSortingRiboPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reads input","readsInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Non-rRNA","no_rRNA",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNA","rRNA",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% rRNA","rRNAPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan rRNA (type)","rRNABilan.type",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), String.class, true,
				DescriptionFactory.newValues("PhiX", "Eukaryotic 18S", "Eukaryotic 28S", "Bacteria 16S", "Bacteria 23S", "Archeae 16S", "Archeae 23S", "Rfam 5.8S", "Rfam 5S"), "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Bilan rRNA (%)","rRNABilan.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Float.class, true, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Useful sequences","usefulSequences",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Useful bases","usefulBases",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, false, "single"));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getMappingPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Sample input","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Non-chimeric aligned reads","nonChimericAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("FR (PE) aligned reads","FRAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("RF (MP) aligned reads","RFAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("FF aligned reads","FFAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("RR aligned reads","RRAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Trans aligned reads","transAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Single aligned reads","singleAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% non-chimeric aligned reads","nonChimericAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% FR (PE) aligned reads","FRAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% RF (MP) aligned reads","RFAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% FF aligned reads","FFAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% RR aligned reads","RRAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% Trans aligned reads","transAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% Single aligned reads","singleAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Distance between MP reads","MPReadDistanceSeparation",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Image.class, true, "img"));		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("MP insert size estimated","estimatedMPInsertSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Distance between PE reads","PEReadDistanceSeparation",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Image.class, true, "img"));	
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("PE insert size estimated","estimatedPEInsertSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reference","reference",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), String.class, true, "single"));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getMergingPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Sample input","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Merged reads","mergedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% merged reads","mergedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mediane size (bases)","medianeSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Average size (bases)","avgSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Min size (bases)","minSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Max size (bases)","maxSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Merged reads distribution","mergedReadsDistrib",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Image.class, false, "img"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Overlap distribution","overlapDistrib",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Image.class, true, "img"));
		return propertyDefinitions;		
	}
	
	
	public static List<PropertyDefinition> getMergingBAPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Reads input","readsInput", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Merged Reads","mergedReads", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% merged reads","mergedReadsPercent", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Mediane size (bases)","medianeSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Average size (bases)","avgSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Min size (bases)","minSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Max size (bases)","maxSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Overlap distribution","overlapDistrib", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Image.class, true, "img"));
		return propertyDefinitions;		
	}
	
	
	public static List<PropertyDefinition> getAssemblyPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();	
		//for further development
		/*
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("N50 contig size","N50ContigSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of contigs","N50ContigNb", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("N80 contig size","N80ContigSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of contigs","N80ContigNb", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("N90 contig size","N90ContigSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of contigs","N90ContigNb", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Assembly size","assemblyContigSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of contigs","assemblyContigNb", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Smallest contig size","minContigSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Largest contig size","maxContigSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Average contig size","averageContigSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Float.class, true, "single"));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Size","contigSizeRepartition.size", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number","contigSizeRepartition.contigNumber", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent","contigSizeRepartition.contigPercent", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Cumulative size","contigSizeRepartition.cumulativeSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));	 
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% cumulative size","contigSizeRepartition.cumulativeSizePercent", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Float.class, true, "single"));
		*/		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Assembly statistics","assemblyStatistics",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), String.class, true, "single"));	
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percentage of assembled reads","readsAssembledPercent", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Float.class, true, "single"));
		return propertyDefinitions;		
	}
	
	
	public static List<PropertyDefinition> getScaffoldingPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		//for further development
		/*
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Path","path",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), String.class, true, "single"));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("N50 scaffold size","N50ScaffoldSize",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of scaffolds","N50ScaffoldNb",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("N80 scaffold size","N80ScaffoldSize",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of scaffolds","N80ScaffoldNb",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("N90 scaffold size","N90ScaffoldSize",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of scaffolds","N90ScaffoldNb",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Assembly size","assemblyScaffoldSize",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Long.class, true, "single"));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number of scaffolds","assemblyScaffoldNb",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Smallest scaffold size","minScaffoldSize",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Largest scaffold size","maxScaffoldSize",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Average scaffold size","averageScaffoldSize",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Float.class, true, "single"));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Size","scaffoldSizeRepartition.size", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Number","scaffoldSizeRepartition.scaffoldNumber", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Percent","scaffoldSizeRepartition.scaffoldPercent", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Cumulative size","scaffoldSizeRepartition.cumulativeSize", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Integer.class, true, "single"));	 
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% cumulative size","scaffoldSizeRepartition.cumulativeSizePercent", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Default), Float.class, true, "single"));
		*/
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Scaffolding statistics","scaffoldingStatistics",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), String.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nombre de paires satisfaisantes","nbPairedSatisfied",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nombre de paires non satisfaisantes","nbPairedUnsatisfied",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb paires mappées","nbMappedPairs",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Nb séquences mappées","nbMappedSequences",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Float.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Taille d'insert médiane","medianInsertSize",LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Float.class, true, "single"));
		
		return propertyDefinitions;		
	}
	
	
	public static List<PropertyDefinition> getGapClosingPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Actual gap sum","actualGapSum", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Extended gap sum","extendGapSum", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Actual gap count","actualGapCount", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Extended gap count","extendGapCount", LevelService.getLevels(Level.CODE.Analysis, Level.CODE.Pairs), Long.class, true, "single"));
		return propertyDefinitions;		
	}
		
		
	
	
	
}

