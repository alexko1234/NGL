package models.sra.submit.common.instance;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.description.ObjectType;
import models.utils.InstanceConstants;
//import play.Logger;
import validation.ContextValidation;
import validation.sra.SraValidationHelper;

public class Sample extends AbstractSample {
	private static final play.Logger.ALogger logger = play.Logger.of(Sample.class);
	
	// SampleType
	//public String alias;         // required mais remplacé par code herité de DBObject
	public String projectCode;     // required pour nos stats //Reference code de la collection project NGL  
	public int taxonId;            // required 
	public String commonName;     
	public String scientificName;  // required next soon 
	public String title;           // required next soon 
	public String anonymizedName;      // not required.
	public String description;      
	public String clone;           
	//public String accession;       // numeros d'accession attribué par ebi champs mis dans AbstractSample
 	public String externalId = null;
	public Date releaseDate;         // required, date de mise à disposition en public par l'EBI
	//public State state; //= new State();// Reference sur "models.laboratory.common.instance.state" mis dans AbstractSample
	//public TraceInformation traceInformation = new TraceInformation(); champs mis dans AbstractSample
	public Date firstSubmissionDate;        

	public Sample() {
		super(AbstractSample.sampleType);
	}

	
	@Override
	public void validate(ContextValidation contextValidation) {
		logger.info("Validate sample");
		//Logger.info("ok dans Sample.validate\n");
		contextValidation.addKeyToRootKeyName("sample");
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateFreeText(contextValidation,"description", this.description);
		SraValidationHelper.validateFreeText(contextValidation,"title", this.title);
		SraValidationHelper.validateFreeText(contextValidation,"anonymizedName", this.anonymizedName);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		//SraValidationHelper.requiredAndConstraint(contextValidation, this.state.code , VariableSRA.mapStatus, "state.code");
		SraValidationHelper.validateState(ObjectType.CODE.SRASubmission, this.state, contextValidation);
		
		if (!StringUtils.isNotBlank((CharSequence) contextValidation.getContextObjects().get("type"))){
			contextValidation.addErrors("sample non evaluable ", "sans type de contexte de validation");
		} else if (contextValidation.getContextObjects().get("type").equals("sra")) {
			SraValidationHelper.validateProjectCode(this.projectCode, contextValidation);
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_SAMPLE_COLL_NAME, contextValidation);
		} else if (contextValidation.getContextObjects().get("type").equals("wgs")) {
			SraValidationHelper.validateProjectCode(this.projectCode, contextValidation);
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_SAMPLE_WGS_COLL_NAME, contextValidation);
		}  else if (contextValidation.getContextObjects().get("type").equals("external_sra")) {
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_SAMPLE_COLL_NAME, contextValidation);
		}  else {
			contextValidation.addErrors("sample non evaluable ", "avec type de contexte de validation " + contextValidation.getContextObjects().get("type"));	
		}
		contextValidation.removeKeyFromRootKeyName("sample");
		logger.debug("sortie de sample.validate pour {}", this.code);
	}



	
}
