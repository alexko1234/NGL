package models.laboratory.reagent.description;

import validation.IValidation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.cea.ig.DBObject;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="category", defaultImpl= models.laboratory.reagent.description.ReagentCatalog.class)
@JsonSubTypes({
	@JsonSubTypes.Type(value =  models.laboratory.reagent.description.ReagentCatalog.class, name = "Reagent"),
	@JsonSubTypes.Type(value =  models.laboratory.reagent.description.BoxCatalog.class, name = "Box"),
	@JsonSubTypes.Type(value =  models.laboratory.reagent.description.KitCatalog.class, name = "Kit")
})
public abstract class AbstractCatalog extends DBObject implements IValidation{

	public AbstractCatalog() {
		super();
	}
	
	public String name;
	public String providerCode;
}