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
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sequence index inconnu","varIndex.unknownIndexSequence",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sequence index connu","varIndex.expectedIndexSequence",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nom index connu","varIndex.expectedIndexName",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), String.class, false, "object_list"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("distance entre séquence inconnu et séquence index connu","varIndex.distanceFromExpectedIndex",LevelService.getLevels(Level.CODE.Lane, Level.CODE.Default), Integer.class, false, "object_list"));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Stats tiles FC","tilesStats",LevelService.getLevels(Level.CODE.Run, Level.CODE.Default), Image.class, false, "img"));
		
		return propertyDefinitions;
	}

}
