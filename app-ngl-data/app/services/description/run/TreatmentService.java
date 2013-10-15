package services.description.run;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.description.RunCategory;
import models.laboratory.run.description.RunType;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.TreatmentContext;
import models.laboratory.run.description.TreatmentType;
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
		l.add(DescriptionFactory.newTreatmentType("NGSRG","ngsrg-illumina", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.ngsrg.name()), "ngsrg", getNGSRGTreatmentPopertyDefinitions(), getTreatmentContexts("default")));
		l.add(DescriptionFactory.newTreatmentType("Global","global", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.global.name()), "global", getReadSetTreatmentPropertyDefinitions(), getTreatmentContexts("default")));
		l.add(DescriptionFactory.newTreatmentType("SAV","sav", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.sequencing.name()), "sav", getSAVTreatmentPropertyDefinitions(), getTreatmentContexts("read1", "read2")));
		l.add(DescriptionFactory.newTreatmentType("Read Quality","read-quality", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "readQualityRaw,readQualityClean", getReadQualityTreatmentPropertyDefinitions(), getTreatmentContexts("read1", "read2")));
		l.add(DescriptionFactory.newTreatmentType("Duplicates","duplicates", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.quality.name()), "duplicatesRaw,duplicatesClean", getDuplicatesTreatmentPropertyDefinitions(), getTreatmentContexts("read1", "read2", "pairs")));
		
		DAOHelpers.saveModels(TreatmentType.class, l, errors);
	}
	
	
	private static List<TreatmentContext> getTreatmentContexts(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(TreatmentContext.class, TreatmentContext.find, codes);
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
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateDuplicateReads","estimateDuplicateReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateDuplicateReadsPercent","estimateDuplicateReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Float.class, true));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateDuplicateReadsNtimes.times","estimateDuplicateReadsNtimes.time",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateDuplicateReadsNtimes.percent","estimateDuplicateReadsNtimes.percent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Float.class, true));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateUniqueReads","estimateUniqueReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("estimateUniqueReadsPercent","estimateUniqueReadsPercent",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Read1, Level.CODE.Read2, Level.CODE.Pairs), Float.class, true));
		
		
		
		return propertyDefinitions;		
	}
}

