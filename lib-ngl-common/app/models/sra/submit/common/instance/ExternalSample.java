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

public class ExternalSample extends AbstractSample {
	
	public ExternalSample() {
		super();	
		state = new State("submitted", null); // Reference sur "models.laboratory.common.instance.state"
	}


	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("externalSample");
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		SraValidationHelper.validateCode(this, InstanceConstants.SRA_SAMPLE_COLL_NAME, contextValidation);
		SraValidationHelper.requiredAndConstraint(contextValidation, this.state.code , VariableSRA.mapExternalStatus, "state.code");

		// Pour l'instant externalSample apparait uniquement dans sra d'ou verif du context.type inutile
		/*if (!StringUtils.isNotBlank((CharSequence) contextValidation.getContextObjects().get("type"))){
			contextValidation.addErrors("sample non evaluable ", "sans type de contexte de validation");
		} else if (contextValidation.getContextObjects().get("type").equals("sra")) {
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_SAMPLE_COLL_NAME, contextValidation);
		} else if (contextValidation.getContextObjects().get("type").equals("wgs")) {
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_SAMPLE_WGS_COLL_NAME, contextValidation);
		}  else if (contextValidation.getContextObjects().get("type").equals("external_sra")) {
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_SAMPLE_COLL_NAME, contextValidation);
		}  else {
			contextValidation.addErrors("sample non evaluable ", "avec type de contexte de validation " + contextValidation.getContextObjects().get("type"));	
		}*/
		contextValidation.removeKeyFromRootKeyName("externalSample");
	}


}
