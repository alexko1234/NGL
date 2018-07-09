package models.laboratory.sample.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.sample.description.dao.ImportTypeDAO;

/**
 * Additional information collaborator
 * TODO: Fix comment, seems odd, should refer to file format or data source.
 * @author ejacoby
 *
 */
public class ImportType extends CommonInfoType {

	@SuppressWarnings("hiding")
	public static final CommonInfoType.AbstractCommonInfoTypeFinder<ImportType,ImportTypeDAO> find = 
			new CommonInfoType.AbstractCommonInfoTypeFinder<>(ImportTypeDAO.class);
	
	public ImportCategory category;
	
	public ImportType()	{
		super(ImportTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionSampleLevel() {
		return getPropertyDefinitionByLevel(Level.CODE.Sample);
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionContainerLevel() {
		return getPropertyDefinitionByLevel(Level.CODE.Container);
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionContentLevel() {
		return getPropertyDefinitionByLevel(Level.CODE.Content);
	}
	
}
