package services.description.experiment;

import static services.description.DescriptionFactory.newExperimentType;
import static services.description.DescriptionFactory.newExperimentTypeNode;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.ProtocolCategory;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import services.description.declaration.cns.Bionano;
import services.description.declaration.cns.ExtractionDNARNA;
import services.description.declaration.cns.MetaBarCoding;
import services.description.declaration.cns.MetaTProcess;
import services.description.declaration.cns.Nanopore;
import services.description.declaration.cns.Opgen;
import services.description.declaration.cns.Purif;
import services.description.declaration.cns.QualityControl;
import services.description.declaration.cns.RunIllumina;
import services.description.declaration.cns.Transfert;

import com.typesafe.config.ConfigFactory;
public class ExperimentServiceCNS extends AbstractExperimentService {

	
	@SuppressWarnings("unchecked")
	public  void saveProtocolCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProtocolCategory> l = new ArrayList<ProtocolCategory>();
		l.add(DescriptionFactory.newSimpleCategory(ProtocolCategory.class, "Developpement", "development"));
		l.add(DescriptionFactory.newSimpleCategory(ProtocolCategory.class, "Production", "production"));
		DAOHelpers.saveModels(ProtocolCategory.class, l, errors);

	}
	

	/**
	 * Save all ExperimentCategory
	 * @param errors
	 * @throws DAOException 
	 */
	public  void saveExperimentCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ExperimentCategory> l = new ArrayList<ExperimentCategory>();
		for (ExperimentCategory.CODE code : ExperimentCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(ExperimentCategory.class, code.name(), code.name()));
		}
		DAOHelpers.saveModels(ExperimentCategory.class, l, errors);
	}


	public void saveExperimentTypes(
			Map<String, List<ValidationError>> errors) throws DAOException {
		List<ExperimentType> l = new ArrayList<ExperimentType>();

		l.addAll(new Opgen().getExperimentType());
		l.addAll(new RunIllumina().getExperimentType());
		l.addAll(new Nanopore().getExperimentType());
		l.addAll(new Bionano().getExperimentType());
		l.addAll(new QualityControl().getExperimentType());
		l.addAll(new ExtractionDNARNA().getExperimentType());
		l.addAll(new Purif().getExperimentType());
		l.addAll(new Transfert().getExperimentType());
		l.addAll(new MetaBarCoding().getExperimentType());
		l.addAll(new MetaTProcess().getExperimentType());
		
		
		if(ConfigFactory.load().getString("ngl.env").equals("DEV") ){
			
			/*
			l.add(newExperimentType("Ext to Banque","ext-to-library",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.voidprocess.name()), null, null,"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
		
			l.add(newExperimentType("Ampure Non Ampli","ampure-na",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()),
					null, getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
			
			l.add(newExperimentType("Ampure Ampli","ampure-a",null,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.purification.name()), null,
					getInstrumentUsedTypes("hand"),"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNS)));		
			
			l.add(newExperimentType("Fragmentation","fragmentation",null,200,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionFragmentation(),
					getInstrumentUsedTypes("hand","covaris-s2","covaris-e210"),"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNS) ));
			
			l.add(newExperimentType("Librairie indexée","librairie-indexing",null,400,
					ExperimentCategory.find.findByCode(ExperimentCategory.CODE.transformation.name()), getPropertyDefinitionsLibIndexing(),
					getInstrumentUsedTypes("hand","spri"),"OneToOne", 
					DescriptionFactory.getInstitutes(Constants.CODE.CNS)));
			*/
			
		}
		
		DAOHelpers.saveModels(ExperimentType.class, l, errors);

	}

	public void saveExperimentTypeNodes(Map<String, List<ValidationError>> errors) throws DAOException {

		new Opgen().getExperimentTypeNode();
		new MetaBarCoding().getExperimentTypeNode();
		new MetaTProcess().getExperimentTypeNode();
		new RunIllumina().getExperimentTypeNode();
		new Nanopore().getExperimentTypeNode();
		new Bionano().getExperimentTypeNode();		
		new ExtractionDNARNA().getExperimentTypeNode();
		
		//newExperimentTypeNode("ext-to-qpcr", getExperimentTypes("ext-to-qpcr").get(0), false, false, false, null, null, null, null).save();	
		//newExperimentTypeNode("ext-to-solution-stock", getExperimentTypes("ext-to-solution-stock").get(0), false, false, false, null, null, null, null).save();
				
		/*if(	!ConfigFactory.load().getString("ngl.env").equals("PROD") ){
				
				newExperimentTypeNode("ext-to-library", getExperimentTypes("ext-to-library").get(0), false, false, null, null, null).save();
				newExperimentTypeNode("ext-to-qPCR-norm-fc-depot-illumina", getExperimentTypes("ext-to-qpcr-norm-fc-depot-illumina").get(0), false, false, null, null, null).save();
				newExperimentTypeNode("ext-to-norm-fc-depot-illumina", getExperimentTypes("ext-to-norm-fc-depot-illumina").get(0), false, false, null, null, null).save();
				
				//REM : experimentTypes list confirmées par Julie
				newExperimentTypeNode("fragmentation", getExperimentTypes("fragmentation").get(0), false, false, getExperimentTypeNodes("ext-to-library"), 
						getExperimentTypes("ampure-na"),  getExperimentTypes("fluo-quantification","chip-migration-pre-pcr")).save();
				
				newExperimentTypeNode("librairie-indexing", getExperimentTypes("librairie-indexing").get(0), false, false, getExperimentTypeNodes("fragmentation"), 
						getExperimentTypes("ampure-na"), getExperimentTypes("fluo-quantification","chip-migration-pre-pcr")).save();
				
				newExperimentTypeNode("librairie-dualindexing", getExperimentTypes("librairie-dualindexing").get(0), false, false, getExperimentTypeNodes("fragmentation"), 
						getExperimentTypes("ampure-na"), getExperimentTypes("fluo-quantification","chip-migration-pre-pcr")).save();			
									
			}
		*/

	}

	
	
	private static List<PropertyDefinition> getPropertyDefinitionFragmentation() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));
		return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getPropertyDefinitionsLibIndexing() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//Ajouter la liste des index illumina
		propertyDefinitions.add(newPropertiesDefinition("Tag","tag", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content),String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Catégorie tag","tagCategory", LevelService.getLevels(Level.CODE.ContainerIn,Level.CODE.Content),String.class, true, getTagCategories(),"single"));
		propertyDefinitions.add(newPropertiesDefinition("Quantité engagée","inputQuantity", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, false, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Volume engagé","inputVolume", LevelService.getLevels(Level.CODE.ContainerIn),Double.class, true, "single"));
		return propertyDefinitions;
	}
	
}
