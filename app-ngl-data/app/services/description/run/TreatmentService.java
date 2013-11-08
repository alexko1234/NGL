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
	
	/*
			treatmentCodes : ngsrg, global, sav		

			treatmentTypeCodes : ngsrg-illumina, global, sav		

			treatmentCatTypeCodes : ngsrg, global, sequencing			

			treatmentContextCodes : default, read1, read2, pairs, single
			
			runStatesCode : IP_S, IP_RG, F_RG, F			

			readSetStatesCode : IP_RG, F_RG, IW_QC, F_QC, A, UA

			runTypeCode : RHS2000, RHS2500, RHS2500R	
			
		//getReadSetGlobalPropertyDefinitions : 
		"usefulSequences","usefulSequences",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class
		"usefulBases","usefulBases",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class
	 */

	
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
		//newTreatmentType(String name, String code, TreatmentCategory category, String names, List<PropertyDefinition> propertiesDefinitions, List<TreatmentContext>  contexts)
		l.add(DescriptionFactory.newTreatmentType("NGSRG","ngsrg-illumina", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.ngsrg.name()), "ngsrg", getNGSRGTreatmentPopertyDefinitions(), getTreatmentTypeContexts("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(DescriptionFactory.newTreatmentType("Global","global", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.global.name()), "global", getReadSetTreatmentPropertyDefinitions(), getTreatmentTypeContexts("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(DescriptionFactory.newTreatmentType("SAV","sav", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.sequencing.name()), "sav", getSAVTreatmentPropertyDefinitions(), 
				Arrays.asList(getTreatmentTypeContext("read1", Boolean.TRUE), getTreatmentTypeContext("read2", Boolean.FALSE)), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(DescriptionFactory.newTreatmentType("Read Quality","read-quality", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "readQualityRaw,readQualityClean", getReadQualityTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1", "read2"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(DescriptionFactory.newTreatmentType("Duplicates","duplicates", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "duplicatesRaw,duplicatesClean", getDuplicatesTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1", "read2", "pairs"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		
		l.add(DescriptionFactory.newTreatmentType("Trimming","trimming", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "trimming", getTrimmingTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1", "read2", "pairs", "single"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(DescriptionFactory.newTreatmentType("Contamination","contamination", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "contaminationPhiX", getContaminationPhiXTreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs", "single"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(DescriptionFactory.newTreatmentType("Taxonomy","taxonomy", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "taxonomy", getTaxonomyTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(DescriptionFactory.newTreatmentType("Sorting Ribo","sorting-ribo", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "sortingRibo", getSortingRiboTreatmentPropertyDefinitions(), getTreatmentTypeContexts("read1", "read2", "pairs", "single"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(DescriptionFactory.newTreatmentType("Mapping","mapping", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "mapping", getMappingTreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(DescriptionFactory.newTreatmentType("Merging","merging", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "merging", getMergingTreatmentPropertyDefinitions(), getTreatmentTypeContexts("pairs"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		
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
	
	private static List<PropertyDefinition> getNGSRGTreatmentPopertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//String name, String code, List<Level> levels, Class<?> type, Boolean required
        //Run level
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("flowcellPosition","flowcellPosition", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycle","nbCycle", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("flowcellVersion","flowcellVersion", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterIlluminaFilter","nbClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Run, Level.CODE.Lane, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBase","nbBase", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mismatch","mismatch", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Boolean.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("controlLane","controlLane", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rtaVersion","rtaVersion", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterTotal","nbClusterTotal", LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Long.class, true));
        
        //Lane et ReadSet
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCluster","nbCluster", LevelService.getLevels(Level.CODE.Lane, Level.CODE.ReadSet, Level.CODE.Default), Long.class, true));
        
        
        // Lane level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("prephasing","prephasing", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, true));
        //propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterIlluminaFilter","nbClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Lane), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentClusterInternalAndIlluminaFilter","percentClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("phasing","phasing", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleReadIndex2","nbCycleReadIndex2", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleRead2","nbCycleRead2", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleRead1","nbCycleRead1", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleReadIndex1","nbCycleReadIndex1", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, true));
        
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentClusterIlluminaFilter","percentClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBaseInternalAndIlluminaFilter","nbBaseInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterInternalAndIlluminaFilter","nbClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Long.class, true));
        // ReadSet level
        //nbCluster define in the lane level for the 2 levels
		//propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCluster","nbCluster", LevelService.getLevels(Level.CODE.ReadSet), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Q30","Q30", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBases","nbBases", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("fraction","fraction", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualityScore","qualityScore", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbReadIllumina","nbReadIllumina", LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Default), Integer.class, true));
       
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
		/*
		sampleInput
		organism
		taxonomie
		taxonBilan
		divisionBilan
		keywordBilan
		krona
		*/
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sampleInput","sampleInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("organism","organism",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("taxonomy","taxonomie",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1), String.class, true));
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
		
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getSortingRiboTreatmentPropertyDefinitions() throws DAOException{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		/*
		readsInput
		no_rRNA
		rRNA
		rRNAPercent
		detail_rRNAPercent
		 */
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsInput","readsInput",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("no_rRNA","no_rRNA",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNA","rRNA",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNAPercent","rRNAPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNABilan.type","rRNABilan.type",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), String.class, true,
				DescriptionFactory.newValues("PhiX", "Eukaryotic 18S", "Eukaryotic 28S", "Bacteria 16S", "Bacteria 23S", "Archeae 16S", "Archeae 23S", "Rfam 5.8S", "Rfam 5S")));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rRNABilan.percent","rRNABilan.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Single), Float.class, true));
		
		/*
		usefulSequences
		usefulBases

		 */
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

