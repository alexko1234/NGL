package services.description.run;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
		l.add(DescriptionFactory.newTreatmentType("SAV","sav", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.sequencing.name()), "sav", getSAVTreatmentPropertyDefinitions(), 
				Arrays.asList(getTreatmentTypeContext("read1", Boolean.TRUE), getTreatmentTypeContext("read2", Boolean.FALSE)), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "10"));
		l.add(DescriptionFactory.newTreatmentType("NGSRG","ngsrg-illumina", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.ngsrg.name()), "ngsrg", getNGSRGTreatmentPopertyDefinitions(), getTreatmentTypeContexts("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "20"));		
		l.add(DescriptionFactory.newTreatmentType("Global","global", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.global.name()), "global", getReadSetTreatmentPropertyDefinitions(), getTreatmentTypeContexts("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "0"));
		l.add(DescriptionFactory.newTreatmentType("Read Quality","read-quality", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "readQualityRaw,readQualityClean,readQualityNoRiboClean", getReadQualityTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1", "read2"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "30,83,83"));
		l.add(DescriptionFactory.newTreatmentType("Duplicates","duplicates", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "duplicatesRaw,duplicatesClean,duplicatesNoRiboClean", getDuplicatesTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1", "read2", "pairs"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "40,86,86"));
		l.add(DescriptionFactory.newTreatmentType("Mapping","mapping", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "mappingRaw,mappingClean,mappingNoRiboClean", getMappingTreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs", "default"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS), "90,90"));
		// specific CNS
		l.add(DescriptionFactory.newTreatmentType("Trimming","trimming", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "trimmingRaw", getTrimmingTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1", "read2", "pairs", "single"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), "50"));
		l.add(DescriptionFactory.newTreatmentType("Contamination","contamination", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "contaminationPhiXTrim", getContaminationPhiXTreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs", "single"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), "60"));
		l.add(DescriptionFactory.newTreatmentType("Taxonomy","taxonomy", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "taxonomyClean", getTaxonomyTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), "70"));
		l.add(DescriptionFactory.newTreatmentType("Sorting Ribo","sorting-ribo", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "sortingRiboClean", getSortingRiboTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1", "read2", "pairs", "single"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), "80"));
		l.add(DescriptionFactory.newTreatmentType("Merging","merging", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "mergingClean,mergingNoRiboClean", getMergingTreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs"), DescriptionFactory.getInstitutes(Institute.CODE.CNS), "100,100"));
		//specific CNG 
		l.add(DescriptionFactory.newTreatmentType("alignSingleRead BLAT","alignsingleread-blat", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "alignSingleReadBLATRaw", getASRBTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1", "read2"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), "50"));
		l.add(DescriptionFactory.newTreatmentType("alignSingleRead SOAP2","alignsingleread-soap2", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "alignSingleReadSOAP2Raw", getASRSTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1", "read2"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), "60"));
		l.add(DescriptionFactory.newTreatmentType("Exome","exome", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "exome", getExomeTreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), "100"));
		l.add(DescriptionFactory.newTreatmentType("Whole Genome","whole-genome", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "wholeGenome", getWholeExomeTreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), "110"));
		l.add(DescriptionFactory.newTreatmentType("RNAseq","rna-seq", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "RNAseq", getRnaSeqTreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), "120"));
		l.add(DescriptionFactory.newTreatmentType("ChiPseq","chip-seq", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "ChiPseq", getChiPSeqTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), "130"));
		l.add(DescriptionFactory.newTreatmentType("ChiPseq-PE","chipseq-pe", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "ChiPseqPE", getChiPSeqPETreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), "130"));
		l.add(DescriptionFactory.newTreatmentType("FAIREseq","faire-seq", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "FAIREseq", getFaireSeqTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), "150"));
		//TODO : level parameter must be verified
		l.add(DescriptionFactory.newTreatmentType("Sample Control","sample-control", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "sampleControl", getSampleControlTreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs"), DescriptionFactory.getInstitutes(Institute.CODE.CNG), "160"));
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
	
	private static List<PropertyDefinition> getASRBTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mappedReads","mappedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mappedReadsPercent","mappedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("reads100pcMatchBases","reads100pcMatchBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("reads100pcMatchBasesPerc","reads100pcMatchBasesPerc", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("reads90pcMatchBases","reads90pcMatchBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("reads90pcMatchBasesPerc","reads90pcMatchBasesPerc", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("alignedBases","alignedBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbErrors","nbErrors", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbInsertions","nbInsertions", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbDeletions","nbDeletions", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbMismatches","nbMismatches", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbPerfectReads","nbPerfectReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("coverageDistribution","coverageDistribution", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorPosition","errorPosition", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualityValueDistribution","qualityValueDistribution", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		return propertyDefinitions;
	}
	

	private static List<PropertyDefinition> getASRSTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mappedReads","mappedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mappedReadsPercent","mappedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("alignedBases","alignedBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbMismatches","nbMismatches", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mismatchesPercent","mismatchesPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorPosition","errorPosition", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getExomeTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbReads","nbReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mappedReads","mappedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mappedReadsPercent","mappedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("pairingPercent","pairingPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("duplicatedReads","duplicatedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("duplicatedReadsPercent","duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sizeRegionsTiled","sizeRegionsTiled", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("targetPercent","targetPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentNT30X","percentNT30X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentNT20X","percentNT20X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentNT10X","percentNT10X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentNT5X","percentNT5X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbRegions0X","nbRegions0X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("regions0XPercent","regions0XPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbRegionsFullCover","nbRegionsFullCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("RegionsFullCoverPercent","RegionsFullCoverPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("meanCover","meanCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("medianCover","medianCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sdCover","sdCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getWholeExomeTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbReads","nbReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mappedReads","mappedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mappedReadsPercent","mappedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("pairingPercent","pairingPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("duplicatedReads","duplicatedReads", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("duplicatedReadsPercent","duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentNT30X","percentNT30X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentNT20X","percentNT20X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentNT10X","percentNT10X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentNT5X","percentNT5X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbRegions0X","nbRegions0X", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("regions0XPercent","regions0XPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("meanCover","meanCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("medianCover","medianCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sdCover","sdCover", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getRnaSeqTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("storedPairsPercent","storedPairsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("storedForwardReadPercent","storedForwardReadPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("storedReverseReadPercent","storedReverseReadPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rejectedPairsPercent","rejectedPairsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("alignedReadsPercent","alignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("uniqueReadsPercent","uniqueReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("uniqueAlignedReadsPercent","uniqueAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mismatchPercent","mismatchPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("duplicatedReadsPercent","duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("insertLengthMean","insertLengthMean", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("insertLengthMeanSd","insertLengthMeanSd", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("1SenseAlignedReadsPercent","1SenseAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("2SenseAlignedReadsPercent","2SenseAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNAPercent","rRNAPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("intragenicReadsPercent","intragenicReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("exonicReadsPercent","exonicReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("proteinCodingGenes","proteinCodingGenes", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("proteinCodingTranscripts","proteinCodingTranscripts", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("lncRNAGenes","lncRNAGenes", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("lncRNATranscripts","lncRNATranscripts", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getChiPSeqTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("storedReadsPercent","storedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rejectedReadsPercent","rejectedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("alignedReadsPercent","alignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("uniqueReadsPercent","uniqueReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("uniqueAlignedReadsPercent","uniqueAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mismatchPercent","mismatchPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("duplicatedReadsPercent","duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("insertLengthMean","insertLengthMean", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("insertLengthMeanSd","insertLengthMeanSd", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNAPercent","rRNAPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		return propertyDefinitions;
	}
		
	private static List<PropertyDefinition> getChiPSeqPETreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("storedReadsPercent","storedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("storedForwardReadPercent", "storedForwardReadPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("storedReverseReadPercent", "storedReverseReadPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rejectedReadsPercent", "rejectedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("alignedReadsPercent", "alignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("uniqueReadsPercent", "uniqueReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("uniqueAlignedReadsPercent", "uniqueAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mismatchPercent", "mismatchPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("duplicatedReadsPercent", "duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("insertLengthMean", "insertLengthMean", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("insertLengthMeanSd", "insertLengthMeanSd", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("1SenseAlignedReadsPercent", "1SenseAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("2SenseAlignedReadsPercent", "2SenseAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNAPercent","rRNAPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("intragenicReadsPercent", "intragenicReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("exonicReadsPercent", "exonicReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getFaireSeqTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("storedReadsPercent","storedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rejectedReadsPercent","rejectedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("alignedReadsPercent","alignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("uniqueReadsPercent","uniqueReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("uniqueAlignedReadsPercent","uniqueAlignedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mismatchPercent","mismatchPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("duplicatedReadsPercent","duplicatedReadsPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("insertLengthMean","insertLengthMean", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("insertLengthMeanSd","insertLengthMeanSd", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNAPercent","rRNAPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getSampleControlTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//TODO : Level "pairs" must be confirmed 
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleSexe","sampleSexe", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), String.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("samplesComparison","samplesComparison", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getNGSRGTreatmentPopertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        //Run level
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("flowcellPosition","flowcellPosition", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycle","nbCycle", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("flowcellVersion","flowcellVersion", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterIlluminaFilter","nbClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Run, Level.CODE.Lane, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentClusterIlluminaFilter","percentClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Run, Level.CODE.Lane, Level.CODE.Default), Double.class, false));        
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBase","nbBase", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mismatch","mismatch", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Boolean.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("controlLane","controlLane", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rtaVersion","rtaVersion", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterTotal","nbClusterTotal", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Long.class, true));
        //Lane & ReadSet level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCluster","nbCluster", LevelService.getLevels(Level.CODE.Lane, Level.CODE.ReadSet, Level.CODE.Default), Long.class, true));
        // Lane level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("prephasing","prephasing", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentClusterInternalAndIlluminaFilter","percentClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("phasing","phasing", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleReadIndex2","nbCycleReadIndex2", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleRead2","nbCycleRead2", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleRead1","nbCycleRead1", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleReadIndex1","nbCycleReadIndex1", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBaseInternalAndIlluminaFilter","nbBaseInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterInternalAndIlluminaFilter","nbClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("seqLossPercent","seqLossPercent", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Float.class, false));
        // ReadSet level
        //nbCluster define in the lane level for the 2 levels
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Q30","Q30", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBases","nbBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("fraction","fraction", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualityScore","qualityScore", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbReadIllumina","nbReadIllumina", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("validSeqPercent","validSeqPercent", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Float.class, false));
                 
        return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getReadSetTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        // just readset level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("usefulSequences","usefulSequences", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("usefulBases","usefulBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Long.class, true));
        return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getSAVTreatmentPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("clusterDensity","clusterDensity",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("clusterDensityStd","clusterDensityStd",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("clusterPFPerc","clusterPFPerc",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("clusterPFPercStd","clusterPFPercStd",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("phasing","phasing",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("prephasing","prephasing",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("reads","reads",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsPF","readsPF",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("greaterQ30Perc","greaterQ30Perc",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("cyclesErrRated","cyclesErrRated",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("alignedPerc","alignedPerc",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("alignedPercStd","alignedPercStd",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePerc","errorRatePerc",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercStd","errorRatePercStd",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle35","errorRatePercCycle35",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1,Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle35Std","errorRatePercCycle35Std",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle75","errorRatePercCycle75",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle75Std","errorRatePercCycle75Std",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle100","errorRatePercCycle100",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle100Std","errorRatePercCycle100Std",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("intensityCycle1","intensityCycle1",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("intensityCycle1Std","intensityCycle1Std",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1,Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("intensityCycle20Perc","intensityCycle20Perc",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("intensityCycle20PercStd","intensityCycle20PercStd",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		return propertyDefinitions;
	}
		
	public static List<PropertyDefinition> getReadQualityTreatmentPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualScore","qualScore",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nuclDistribution","nuclDistribution",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readWithNpercent","readWithNpercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readSizeDistribution","readSizeDistribution",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("adapterContamination","adapterContamination",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("adapters","adapters",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), String.class, false));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("GCDistribution","GCDistribution",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("positionN","positionN",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), File.class, true));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getDuplicatesTreatmentPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateDuplicatedReads","estimateDuplicatedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateDuplicatedReadsPercent","estimateDuplicatedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateDuplicatedReadsNTimes.times","estimateDuplicatedReadsNTimes.times",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateDuplicatedReadsNTimes.percent","estimateDuplicatedReadsNTimes.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateUniqueReads","estimateUniqueReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateUniqueReadsPercent","estimateUniqueReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Float.class, true));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getTrimmingTreatmentPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sizeRange","sizeRange",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), String.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsInput","readsInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsOutput","readsOutput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsNoTrim","readsNoTrim",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, false));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsNoTrimPercent","readsNoTrimPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, false));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsTrim","readsTrim",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsTrimPercent","readsTrimPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nucleotidesTrim","nucleotidesTrim",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("trimRejectedShort ","trimRejectedShort",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("trimRejectedLength0","trimRejectedLength0",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("trimStored","trimStored",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2), Long.class, false));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("storedPairs","storedPairs",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rejectedPairs","rejectedPairs",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, false));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("storedSingleton","storedSingleton",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Single), Long.class, true));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getContaminationPhiXTreatmentPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsInput","readsInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs, Level.CODE.Single), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("removedReads","removedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs, Level.CODE.Single), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("remainingReads","remainingReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs, Level.CODE.Single), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("removedReadsPercent","removedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs, Level.CODE.Single), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("remainingNucleotides","remainingNucleotides",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, false));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getTaxonomyTreatmentPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("organism","organism",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("taxonomy","taxonomy",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("taxonBilan.taxon","taxonBilan.taxon",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("taxonBilan.nbSeq","taxonBilan.nbSeq",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("taxonBilan.percent","taxonBilan.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));	
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("divisionBilan.division","divisionBilan.division",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true,
				DescriptionFactory.newValues("eukaryota","bacteria","cellular organisms","archaea","viruses")));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("divisionBilan.nbSeq","divisionBilan.nbSeq",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("divisionBilan.percent","divisionBilan.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("keywordBilan.keyword","keywordBilan.keyword",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true, 
				DescriptionFactory.newValues("mitochondri","virus","chloroplast","transposase",	"BAC")));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("keywordBilan.nbSeq","keywordBilan.nbSeq",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("keywordBilan.percent","keywordBilan.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("krona","krona",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), File.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("phylogeneticTree","phylogeneticTree",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), File.class, true));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getSortingRiboTreatmentPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsInput","readsInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("no_rRNA","no_rRNA",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNA","rRNA",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNAPercent","rRNAPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNABilan.type","rRNABilan.type",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), String.class, true,
				DescriptionFactory.newValues("PhiX", "Eukaryotic 18S", "Eukaryotic 28S", "Bacteria 16S", "Bacteria 23S", "Archeae 16S", "Archeae 23S", "Rfam 5.8S", "Rfam 5S")));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNABilan.percent","rRNABilan.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("usefulSequences","usefulSequences",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("usefulBases","usefulBases",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, false));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getMappingTreatmentPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nonChimericAlignedReads","nonChimericAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("FRAlignedReads","FRAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("RFAlignedReads","RFAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("FFAlignedReads","FFAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("RRAlignedReads","RRAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("transAlignedReads","transAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("singleAlignedReads","singleAlignedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nonChimericAlignedReadsPercent","nonChimericAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("FRAlignedReadsPercent","FRAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("RFAlignedReadsPercent","RFAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("FFAlignedReadsPercent","FFAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("RRAlignedReadsPercent","RRAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("transAlignedReadsPercent","transAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("singleAlignedReadsPercent","singleAlignedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("MPReadDistanceSeparation","MPReadDistanceSeparation",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), File.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("PEReadDistanceSeparation","PEReadDistanceSeparation",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), File.class, true));	
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("reference","reference",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), String.class, true));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getMergingTreatmentPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mergedReads","mergedReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mergedReadsPercent","mergedReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("medianeSize","medianeSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("avgSize","avgSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("minSize","minSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("maxSize","maxSize",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mergedReadsDistrib","mergedReadsDistrib",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), File.class, false));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("overlapDistrib","overlapDistrib",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), File.class, true));
		return propertyDefinitions;		
	}
}

