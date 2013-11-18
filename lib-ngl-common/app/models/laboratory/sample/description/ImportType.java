package models.laboratory.sample.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.sample.description.dao.ImportTypeDAO;
import models.utils.dao.AbstractDAOCommonInfoType.CommonInfoTypeFinder;

/**
 * Additional information collaborator
 * @author ejacoby
 *
 */
public class ImportType extends CommonInfoType{

	public ImportCategory category;
	
	public static CommonInfoTypeFinder<ImportTypeDAO,ImportType> find = new CommonInfoTypeFinder<ImportTypeDAO,ImportType>(ImportTypeDAO.class);
	
	public ImportType()
	{
		super(ImportTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionSampleLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Sample);
	}
}
