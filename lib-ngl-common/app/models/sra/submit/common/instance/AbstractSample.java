package models.sra.submit.common.instance;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import validation.IValidation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.cea.ig.DBObject;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="_type", defaultImpl=models.sra.submit.common.instance.Sample.class)
@JsonSubTypes({
	@JsonSubTypes.Type(value =  models.sra.submit.common.instance.Sample.class, name = "Sample"),
	@JsonSubTypes.Type(value =  models.sra.submit.common.instance.ExternalSample.class, name = "ExternalSample"),
})
public abstract class AbstractSample extends DBObject implements IValidation {

	public String accession;       // numeros d'accession attribué par ebi 
	public State state; //= new State();// Reference sur "models.laboratory.common.instance.state" 
	 // pour gerer les differents etats de l'objet.
	 // Les etapes utilisateurs = (new, inWaitingConfiguration,) inProgressConfiguration, finishConfiguration, 
	 // Les etapes automatisables via birds : inWaitingSubmission, inProgressSubmission, finishSubmission, submit
	public TraceInformation traceInformation = new TraceInformation();
	
	public AbstractSample() {
		super();
	} 
	
	

	
}
