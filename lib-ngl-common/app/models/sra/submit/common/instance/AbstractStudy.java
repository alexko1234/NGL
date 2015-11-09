package models.sra.submit.common.instance;




import validation.IValidation;
import fr.cea.ig.DBObject;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="_type", defaultImpl=models.sra.submit.common.instance.Study.class)
@JsonSubTypes({
	@JsonSubTypes.Type(value =  models.sra.submit.common.instance.Study.class, name = "Study"),
	@JsonSubTypes.Type(value =  models.sra.submit.common.instance.ExternalStudy.class, name = "ExternalStudy"),
})

public abstract class AbstractStudy extends DBObject implements IValidation {

	public String accession;       // numeros d'accession attribué par ebi 
	//public State state; 
	public TraceInformation traceInformation = new TraceInformation();
	public State state; 

	public AbstractStudy() {
		super();
	} 
	


	
	
}

