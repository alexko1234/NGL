package models.laboratory.run.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.dao.ReadSetTypeDAO;


public class ReadSetType extends CommonInfoType{

	public static Finder<ReadSetType> find = new Finder<ReadSetType>(ReadSetTypeDAO.class.getName()); 
	
	public ReadSetType() {
		super(ReadSetTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.ReadSet);
	}
}
