package services.description.sample;

import static services.description.DescriptionFactory.newImportType;
import static services.description.DescriptionFactory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;

public class ImportServiceCNG extends AbstractImportService {


	public  void saveImportCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ImportCategory> l = new ArrayList<ImportCategory>();
		l.add(saveImportCategory("Sample Import", "sample-import"));
		DAOHelpers.saveModels(ImportCategory.class, l, errors);
	}

	
	public void saveImportTypes(
			Map<String, List<ValidationError>> errors) throws DAOException {
		List<ImportType> l = new ArrayList<ImportType>();
		l.add(newImportType("Defaut", "default-import", ImportCategory.find.findByCode("sample-import"), getSampleCNGPropertyDefinitions(), getInstitutes(Constants.CODE.CNG)));
		
		l.add(newImportType("Import tubes", "tube-from-bank-reception", ImportCategory.find.findByCode("sample-import"), getBankReceptionPropertyDefinitions(), getInstitutes(Constants.CODE.CNG)));
		l.add(newImportType("Import plaques", "plate-from-bank-reception", ImportCategory.find.findByCode("sample-import"), getBankReceptionPropertyDefinitions(), getInstitutes(Constants.CODE.CNG)));
		
		// GA/FDS 14/06/2017 CONTOURNEMENT de la creation des libProcessTypecodes dans NGLBI ce qui pose des problemes dans le cas ISOPROD
		// creer un ImportType bidon pour declarer la propriété libProcessTypecodes et sa liste de valeurs...
		l.add(newImportType("Import bidon", "import-bidon", ImportCategory.find.findByCode("sample-import"), getLibProcessTypecodePropertyDefinitions(), getInstitutes(Constants.CODE.CNG)));
		
		DAOHelpers.saveModels(ImportType.class, l, errors);
	}

	private static List<PropertyDefinition> getBankReceptionPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Gender", "gender", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, null, 
				Arrays.asList(newValue("0","unknown"),newValue("1","male"),newValue("2","female")), null,null,null,"single", 17, false, null,null));		
		return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getSampleCNGPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, true, "single"));
		return propertyDefinitions;
	}
	
	
	
	// GA/FDS 14/06/2017 (reprise dans RunServiceCNG.java )
	private static List<PropertyDefinition> getLibProcessTypecodePropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Type processus banque","libProcessTypeCode",LevelService.getLevels(Level.CODE.Content), String.class, false, getLibProcessTypeCodeValues(), "single"));
	
		return propertyDefinitions;
	}
	
	// GA/FDS 14/06/2017 (reprise dans RunServiceCNG.java )
	private static List<Value> getLibProcessTypeCodeValues(){
        List<Value> values = new ArrayList<Value>();
        
         // codes for Captures
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
         
         // codes for DNA sequencing
         values.add(DescriptionFactory.newValue("DA","DA - DNASeq"));
         values.add(DescriptionFactory.newValue("DB","DB - MatePairSeq"));
         values.add(DescriptionFactory.newValue("DC","DC - Dnase-ISeq"));
         values.add(DescriptionFactory.newValue("DD","DD - PCR-NANO DNASeq"));
         values.add(DescriptionFactory.newValue("DE","DE - Chromium WG"));
         
         // codes for various sequencing
         values.add(DescriptionFactory.newValue("FA","FA - MeDipSeq"));
         values.add(DescriptionFactory.newValue("FB","FB - ChipSeq"));
         values.add(DescriptionFactory.newValue("FC","FC - MeDipSeq/Depl"));
         values.add(DescriptionFactory.newValue("FD","FD - BisSeq"));
         values.add(DescriptionFactory.newValue("FE","FE - FAIRESeq"));
         values.add(DescriptionFactory.newValue("FF","FF - MBDSeq"));
         values.add(DescriptionFactory.newValue("FG","FG - GROSeq"));
         values.add(DescriptionFactory.newValue("FH","FH - oxBisSeq"));
         values.add(DescriptionFactory.newValue("FI","FI - ATACSeq"));
         values.add(DescriptionFactory.newValue("FJ","FJ - RRBSeq"));  // ajout 12/06/2017
         values.add(DescriptionFactory.newValue("HIC","HIC - HiC"));
         
         // codes for RNA sequencing
         values.add(DescriptionFactory.newValue("RA","RA - RNASeq"));
         values.add(DescriptionFactory.newValue("RB","RB - smallRNASeq"));
         values.add(DescriptionFactory.newValue("RC","RC - ssRNASeq"));
         values.add(DescriptionFactory.newValue("RD","RD - ssmRNASeq"));
         values.add(DescriptionFactory.newValue("RE","RE - sstRNASeq"));
         values.add(DescriptionFactory.newValue("RF","RF - sstRNASeqGlobin"));
         values.add(DescriptionFactory.newValue("RG","RG - mRNASeq"));
         values.add(DescriptionFactory.newValue("RH","RH - sstRNASeqGold"));
         values.add(DescriptionFactory.newValue("UN","UN - UKNOWN"));
        return values;
    } 
	
}
