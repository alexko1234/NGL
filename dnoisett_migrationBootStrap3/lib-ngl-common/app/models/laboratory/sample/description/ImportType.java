package models.laboratory.sample.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.CommonInfoType.AbstractCommonInfoTypeFinder;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.sample.description.dao.ImportTypeDAO;

/**
 * Additional information collaborator
 * @author ejacoby
 *
 */
public class ImportType extends CommonInfoType{

	public ImportCategory category;
	
	public static CommonInfoType.AbstractCommonInfoTypeFinder<ImportType> find = new CommonInfoType.AbstractCommonInfoTypeFinder<ImportType>(ImportTypeDAO.class);
	
	public ImportType()
	{
		super(ImportTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionSampleLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Sample);
	}
}
