package models.sra.submit.common.instance;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.sra.SraValidationHelper;
//import play.Logger;

public class ExternalSample extends AbstractSample {
	public static final play.Logger.ALogger logger = play.Logger.of(ExternalSample.class);
	
	public ExternalSample() {
		super(AbstractSample.externalSampleType);	
		state = new State("F-SUB", null); // Reference sur "models.laboratory.common.instance.state"
	}


	@Override
	public void validate(ContextValidation contextValidation) {
		System.out.println("ok dans ExternalSample.validate\n");
		logger.info("ok dans ExternalSample.validate\n");
		contextValidation.addKeyToRootKeyName("externalSample");
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		SraValidationHelper.validateCode(this, InstanceConstants.SRA_SAMPLE_COLL_NAME, contextValidation);
		//SraValidationHelper.requiredAndConstraint(contextValidation, this.state.code , VariableSRA.mapExternalStatus, "state.code");
		SraValidationHelper.validateState(ObjectType.CODE.SRASubmission, this.state, contextValidation);

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
