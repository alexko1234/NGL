package models.laboratory.common.description;

import java.util.List;

import play.data.validation.Constraints.Required;

/**
 * Type property definition 
 * @author ejacoby
 *
 */
public class PropertyDefinition {

	public Long id;

	@Required
	public String code;

	@Required	
	public String name;

	public String description;

	public Boolean required = Boolean.FALSE;

	public Boolean active = Boolean.TRUE;
	public Boolean choiceInList = Boolean.FALSE;
	
	@Required	
	public String type;
	public String displayFormat;
	public Integer displayOrder;

	public String level;
	//Propagation des propriétés
	public boolean propagation;
	//propriétés d'entrée ou sortie pour les niveaux contenant et contenu
	public String inOut;

	public List<Value> possibleValues;

	public String defaultValue;

	public MeasureCategory measureCategory;

	public MeasureValue measureValue;

	
}
