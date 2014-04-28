package models.laboratory.run.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.dao.AnalysisTypeDAO;

public class AnalysisType extends CommonInfoType{

	public static CommonInfoType.AbstractCommonInfoTypeFinder<AnalysisType> find = new CommonInfoType.AbstractCommonInfoTypeFinder<AnalysisType>(AnalysisTypeDAO.class); 
	
	public AnalysisType() {
		super(AnalysisTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Analysis);
	}
}
