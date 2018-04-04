package models.laboratory.run.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.dao.AnalysisTypeDAO;

public class AnalysisType extends CommonInfoType {

//	public static CommonInfoType.AbstractCommonInfoTypeFinder<AnalysisType> find = new CommonInfoType.AbstractCommonInfoTypeFinder<AnalysisType>(AnalysisTypeDAO.class);
	
	// AnalysisTypeDAO = AbstractDAOCommonInfoType<AnalysisType>
	@SuppressWarnings("hiding") // Super class should not be concrete
	public static final CommonInfoType.AbstractCommonInfoTypeFinder<AnalysisType,AnalysisTypeDAO> find = 
			new CommonInfoType.AbstractCommonInfoTypeFinder<>(AnalysisTypeDAO.class); 
	
	public AnalysisType() {
		super(AnalysisTypeDAO.class.getName());
	}
		
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Analysis);
	}

}
