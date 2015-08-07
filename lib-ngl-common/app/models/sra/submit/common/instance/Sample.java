package models.sra.submit.common.instance;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.sra.SraValidationHelper;
import fr.cea.ig.DBObject;

public class Sample extends DBObject implements IValidation {

	// SampleType
	//public String alias;         // required mais remplacé par code herité de DBObject
	public String projectCode;     // required pour nos stats //Reference code de la collection project NGL  
	public int taxonId;            // required 
	public String classification;     
	public String commonName;     
	public String scientificName;  // required next soon 
	public String title;           // required next soon 
	public String description;      
	public String clone;           
	public String accession;       // numeros d'accession attribué par ebi 
	public Date releaseDate;       // required, date de mise à disposition en public par l'EBI
	public State state; //= new State();// Reference sur "models.laboratory.common.instance.state" 
	 // pour gerer les differents etats de l'objet.
	 // Les etapes utilisateurs = (new, inWaitingConfiguration,) inProgressConfiguration, finishConfiguration, 
	 // Les etapes automatisables via birds : inWaitingSubmission, inProgressSubmission, finishSubmission, submit
	public TraceInformation traceInformation = new TraceInformation(); 

	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("sample");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.state.code , VariableSRA.mapStatus, "state.code");
		SraValidationHelper.validateProjectCode(this.projectCode, contextValidation);
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		if (!StringUtils.isNotBlank((CharSequence) contextValidation.getContextObjects().get("type"))){
			contextValidation.addErrors("sample non evaluable ", "sans type de contexte de validation");
		} else if (contextValidation.getContextObjects().get("type").equals("sra")) {
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_SAMPLE_COLL_NAME, contextValidation);
		} else if (contextValidation.getContextObjects().get("type").equals("wgs")) {
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_SAMPLE_WGS_COLL_NAME, contextValidation);
		} else {
			contextValidation.addErrors("sample non evaluable ", "avec type de contexte de validation " + contextValidation.getContextObjects().get("type"));	
		}
		contextValidation.removeKeyFromRootKeyName("sample");
	}

}
