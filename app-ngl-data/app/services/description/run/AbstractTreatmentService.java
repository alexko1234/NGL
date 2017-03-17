package services.description.run;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.TreatmentContext;
import models.laboratory.run.description.TreatmentType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;

public abstract class AbstractTreatmentService {
	
	public void main(Map<String, List<ValidationError>> errors)  throws DAOException{		
		DAOHelpers.removeAll(TreatmentContext.class, TreatmentContext.find);
		DAOHelpers.removeAll(TreatmentType.class, TreatmentType.find);
		DAOHelpers.removeAll(TreatmentCategory.class, TreatmentCategory.find);		
		saveTreatmentCategory(errors);
		saveTreatmentContext(errors);
		saveTreatmentType(errors);	
	}

	public abstract void saveTreatmentType(Map<String, List<ValidationError>> errors) throws DAOException;
	public abstract void saveTreatmentContext(Map<String, List<ValidationError>> errors) throws DAOException;
	public abstract void saveTreatmentCategory(Map<String, List<ValidationError>> errors) throws DAOException;
		
	public static List<PropertyDefinition> getTopIndexPropertyDefinitions()
	{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sequence index inconnu","unknownIndex.sequence",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("pourcentage index inconnu","unknownIndex.percent",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Double.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nom index inconnu","unknownIndex.name",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, false, "object_list"));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sequence index inconnu","varIndex.unknownIndexSequence",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sequence index connu","varIndex.expectedIndexSequence",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nom index connu","varIndex.expectedIndexName",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("distance entre séquence inconnu et séquence index connu","varIndex.distanceFromExpectedIndex",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, false, "object_list"));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Stats tiles FC","tilesStats",LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Image.class, false, "img"));
		
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getChromiumPropertyDefinitions()
	{
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Version logiciel","softwareVersion",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), String.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("barcode exact match ratio","barcodeExactMatchRatio",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Double.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("barcode Q30 base ratio","barcodeQ30BaseRatio",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Double.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("BC on white list","bcOnWhitelist",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Double.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("gem count estimate","gemCountEstimate",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mean Barcode QScore","meanBarcodeQscore",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Double.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("number Reads","numberReads",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Long.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("read1 Q30 base ratio","read1Q30BaseRatio",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Double.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("read2 Q30 base ratio","read2Q30BaseRatio",LevelService.getLevels(Level.CODE.ReadSet, Level.CODE.Pairs), Double.class, true, "single"));
		return propertyDefinitions;
	}

}
