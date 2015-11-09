package models.sra.submit.common.instance;


import org.apache.commons.lang3.StringUtils;

import validation.ContextValidation;
import validation.sra.SraValidationHelper;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import models.laboratory.common.instance.State;


public class ExternalStudy extends AbstractStudy {

	public ExternalStudy() {
		super();
		state = new State("submitted", null); // Reference sur "models.laboratory.common.instance.state"
	}


	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("externalStudy");
		// Pour l'instant externalStudy apparait uniquement dans sra d'ou verif du context.type inutile
		/*
		if (!StringUtils.isNotBlank((CharSequence) contextValidation.getContextObjects().get("type"))){
			contextValidation.addErrors(" study non evaluable ", "sans type de contexte de validation");
			contextValidation.removeKeyFromRootKeyName("external_study");
			return;
		}
		if (contextValidation.getContextObjects().get("type").equals("sra")) {
			System.out.println("contextValidationType  = sra");
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_STUDY_COLL_NAME, contextValidation);
		} else if (contextValidation.getContextObjects().get("type").equals("wgs")) {
			System.out.println("contextValidationType  = wgs");
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_STUDY_WGS_COLL_NAME, contextValidation);
		} else {
			System.out.println("contextValidationType = "+contextValidation.getContextObjects().get("type"));
			contextValidation.addErrors("study non evaluable ", "avec type de contexte de validation " + contextValidation.getContextObjects().get("type"));	
		}*/
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		SraValidationHelper.validateCode(this, InstanceConstants.SRA_STUDY_COLL_NAME, contextValidation);
		SraValidationHelper.requiredAndConstraint(contextValidation, this.state.code , VariableSRA.mapExternalStatus, "state.code");

		contextValidation.removeKeyFromRootKeyName("externalStudy");
	}

}	
