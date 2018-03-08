package models.laboratory.run.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.CommonInfoType.AbstractCommonInfoTypeFinder;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.dao.ReadSetTypeDAO;

public class ReadSetType extends CommonInfoType {

	public static final CommonInfoType.AbstractCommonInfoTypeFinder<ReadSetType,ReadSetTypeDAO> find = 
			new CommonInfoType.AbstractCommonInfoTypeFinder<>(ReadSetTypeDAO.class); 
	
	public ReadSetType() {
		super(ReadSetTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.ReadSet);
	}
	
}
