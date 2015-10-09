package services.description.run;

import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.run.description.AnalysisType;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.description.RunCategory;
import models.laboratory.run.description.RunType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;

public class RunServiceCNG  extends AbstractRunService{
	
	public void saveReadSetType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ReadSetType> l = new ArrayList<ReadSetType>();
		l.add(DescriptionFactory.newReadSetType("Default","default-readset",  getReadSetPropertyDefinitions(),  DescriptionFactory.getInstitutes(Institute.CODE.CNG) ));
		l.add(DescriptionFactory.newReadSetType("rsillumina","rsillumina",  getReadSetPropertyDefinitions(),  DescriptionFactory.getInstitutes( Institute.CODE.CNG) ));
		
		DAOHelpers.saveModels(ReadSetType.class, l, errors);
	}
	
	public void saveAnalysisType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<AnalysisType> l = new ArrayList<AnalysisType>();		
		DAOHelpers.saveModels(AnalysisType.class, l, errors);
	}

	public void saveRunCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunCategory> l = new ArrayList<RunCategory>();
		l.add(DescriptionFactory.newSimpleCategory(RunCategory.class, "Illumina", "illumina"));
		DAOHelpers.saveModels(RunCategory.class, l, errors);
	}
	
	public void saveRunType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunType> l = new ArrayList<RunType>();
		l.add(DescriptionFactory.newRunType("RHS2000","RHS2000", 8, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(),  DescriptionFactory.getInstitutes(Institute.CODE.CNG) ));
		l.add(DescriptionFactory.newRunType("RHS2500","RHS2500", 8, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(),  DescriptionFactory.getInstitutes(Institute.CODE.CNG) ));
		l.add(DescriptionFactory.newRunType("RHS2500R","RHS2500R", 2, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(),   DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		l.add(DescriptionFactory.newRunType("RMISEQ","RMISEQ", 1, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(),   DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		l.add(DescriptionFactory.newRunType("RNEXTSEQ500","RNEXTSEQ500", 4, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(),   DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		l.add(DescriptionFactory.newRunType("RHS4000","RHS4000", 1, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(), DescriptionFactory.getInstitutes(Institute.CODE.CNG)));
		l.add(DescriptionFactory.newRunType("RHSX","RHSX", 1, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(), DescriptionFactory.getInstitutes(Institute.CODE.CNG)));

		DAOHelpers.saveModels(RunType.class, l, errors);
	}
	
	private static List<PropertyDefinition> getReadSetPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("asciiEncoding","asciiEncoding",LevelService.getLevels(Level.CODE.File), String.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("label","label",LevelService.getLevels(Level.CODE.File), String.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("md5","md5",LevelService.getLevels(Level.CODE.File), String.class, false, "single"));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("isSentCCRT","isSentCCRT",LevelService.getLevels(Level.CODE.ReadSet), Boolean.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("isSentCollaborator","isSentCollaborator",LevelService.getLevels(Level.CODE.ReadSet), Boolean.class, false, "single"));
		
		//use only for dynamic filters and dynamic properties
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Type processus banque","libProcessTypeCode",LevelService.getLevels(Level.CODE.Content), String.class, false,
				getLibProcessTypeCodeValues(), "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Category d'index","tagCategory",LevelService.getLevels(Level.CODE.Content), String.class, false, getTagCategories(), "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("% par piste","percentPerLane",LevelService.getLevels(Level.CODE.Content), Double.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Layout Nominal Length","libLayoutNominalLength",LevelService.getLevels(Level.CODE.Content), Integer.class, false, "single"));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Objectif taille insert","insertSizeGoal",LevelService.getLevels(Level.CODE.Content), String.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Orientation brin synthétisé","strandOrientation",LevelService.getLevels(Level.CODE.Content), String.class, false, "single"));
		
		//GA 21/07/2015 ajouter la propriété sampleAliquoteCode au readset, niveau content n'est pas idéal mais résoud le pb actuel (JIRA 672)
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Code aliquot","sampleAliquoteCode",LevelService.getLevels(Level.CODE.Content), String.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Tag", "tag", LevelService.getLevels(Level.CODE.Content), String.class, false, "single"));
		
		return propertyDefinitions;
	}
	
	//GA 24/07/2015 ajout des TagCategories
	private static List<Value> getTagCategories(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("SINGLE-INDEX", "SINGLE-INDEX"));
		values.add(DescriptionFactory.newValue("DUAL-INDEX", "DUAL-INDEX"));
		values.add(DescriptionFactory.newValue("MID", "MID"));
		return values;	
	}
	
	private static List<Value> getLibProcessTypeCodeValues(){
        List<Value> values = new ArrayList<Value>();
         values.add(DescriptionFactory.newValue("CA","CA - DefCap008_Rg"));
         values.add(DescriptionFactory.newValue("CAA","CAA - DefCap023"));
         values.add(DescriptionFactory.newValue("CB","CB - DefCap005_Ex"));
         values.add(DescriptionFactory.newValue("CC","CC - DefCap006_Ex"));
         values.add(DescriptionFactory.newValue("CD","CD - DefCap004_Rg"));
         values.add(DescriptionFactory.newValue("CE","CE - DefCap003_Ex"));
         values.add(DescriptionFactory.newValue("CF","CF - DefCap002_Ex"));
         values.add(DescriptionFactory.newValue("CG","CG - DefCap001_Ex"));
         values.add(DescriptionFactory.newValue("CH","CH - DefCap009_Ex"));
         values.add(DescriptionFactory.newValue("CI","CI - DefCap010_Ex"));
         values.add(DescriptionFactory.newValue("CJ","CJ - DefCap011_Ex"));
         values.add(DescriptionFactory.newValue("CK","CK - DefCap007_Ex"));
         values.add(DescriptionFactory.newValue("CL","CL - DefCapLUPA"));
         values.add(DescriptionFactory.newValue("CM","CM - DefCap012_Rg"));
         values.add(DescriptionFactory.newValue("CN","CN - DefCapINRA1_Rg"));
         values.add(DescriptionFactory.newValue("CO","CO - DefCapCAPSEQAN"));
         values.add(DescriptionFactory.newValue("CP","CP - DefCap013_Ex"));
         values.add(DescriptionFactory.newValue("CQ","CQ - DefCap014_Rg"));
         values.add(DescriptionFactory.newValue("CR","CR - DefCap015_Ex"));
         values.add(DescriptionFactory.newValue("CS","CS - DefCap016_Ex"));
         values.add(DescriptionFactory.newValue("CT","CT - CapNimGenV3_017_Ex"));
         values.add(DescriptionFactory.newValue("CV","CV - DefCap018_Ex"));
         values.add(DescriptionFactory.newValue("CW","CW - DefCap019_Rg"));
         values.add(DescriptionFactory.newValue("CX","CX - DefCap020_Ex"));
         values.add(DescriptionFactory.newValue("CY","CY - DefCap021"));
         values.add(DescriptionFactory.newValue("CZ","CZ - DefCap022"));
         values.add(DescriptionFactory.newValue("DA","DA - DNASeq"));
         values.add(DescriptionFactory.newValue("DB","DB - MatePairSeq"));
         values.add(DescriptionFactory.newValue("DC","DC - Dnase-ISeq"));
         values.add(DescriptionFactory.newValue("DD","DD - PCR-NANO-DNASeq"));
         values.add(DescriptionFactory.newValue("FA","FA - MeDipSeq"));
         values.add(DescriptionFactory.newValue("FB","FB - ChipSeq"));
         values.add(DescriptionFactory.newValue("FC","FC - MeDipSeq/Depl"));
         values.add(DescriptionFactory.newValue("FD","FD - BisSeq"));
         values.add(DescriptionFactory.newValue("FE","FE - FAIRESeq"));
         values.add(DescriptionFactory.newValue("FF","FF - MBDSeq"));
         values.add(DescriptionFactory.newValue("FG","FG - GROSeq"));
         values.add(DescriptionFactory.newValue("FH","FH - oxBisSeq"));
         values.add(DescriptionFactory.newValue("FI","FI - ATACSeq"));
         values.add(DescriptionFactory.newValue("RA","RA - RNASeq"));
         values.add(DescriptionFactory.newValue("RB","RB - smallRNASeq"));
         values.add(DescriptionFactory.newValue("RC","RC - ssRNASeq"));
         values.add(DescriptionFactory.newValue("RD","RD - ssmRNASeq"));
         values.add(DescriptionFactory.newValue("RE","RE - sstRNASeq"));
         values.add(DescriptionFactory.newValue("RF","RF - sstRNASeqGlobin"));
         values.add(DescriptionFactory.newValue("UN","UN - UKNOWN"));
        return values;
    } 
	
	
	private static List<PropertyDefinition> getRunIlluminaPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Type lectures","sequencingProgramType"
	        		, LevelService.getLevels(Level.CODE.Run),String.class, false, DescriptionFactory.newValues("SR","PE"),"single"));
		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Types processus banque","libProcessTypeCodes",LevelService.getLevels(Level.CODE.Run), String.class, false,
				getLibProcessTypeCodeValues(), "list"));
		
	    return propertyDefinitions;
	}
	


}
