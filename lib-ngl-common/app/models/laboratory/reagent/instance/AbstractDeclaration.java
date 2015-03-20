package models.laboratory.reagent.instance;

import java.util.Date;
import java.util.List;

import models.laboratory.common.description.State;
import models.laboratory.common.instance.TraceInformation;
import validation.IValidation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.cea.ig.DBObject;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="category", defaultImpl= models.laboratory.reagent.instance.Reagent.class)
@JsonSubTypes({
	@JsonSubTypes.Type(value =  models.laboratory.reagent.instance.Reagent.class, name = "Reagent"),
	@JsonSubTypes.Type(value =  models.laboratory.reagent.instance.Box.class, name = "Box"),
	@JsonSubTypes.Type(value =  models.laboratory.reagent.instance.Kit.class, name = "Kit")
})
public abstract class AbstractDeclaration  extends DBObject implements IValidation{

	public int possibleUseNumber;
	public Date receptionDate;
	
	public String barCode;

	public Date startToUseDate;
	public Date stopToUseDate;
	
	public State state;
	
	public String orderCode;
	
	public TraceInformation traceInformation;
	
	public Date expirationDate;
	
	public List<String> comments;
}