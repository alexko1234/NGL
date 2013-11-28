package models.laboratory.run.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.dao.ReadSetTypeDAO;
import models.utils.dao.AbstractDAOCommonInfoType.CommonInfoTypeFinder;


public class ReadSetType extends CommonInfoType{

	public static CommonInfoTypeFinder<ReadSetTypeDAO,ReadSetType> find = new CommonInfoTypeFinder<ReadSetTypeDAO,ReadSetType>(ReadSetTypeDAO.class); 
	
	public ReadSetType() {
		super(ReadSetTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.ReadSet);
	}
}
