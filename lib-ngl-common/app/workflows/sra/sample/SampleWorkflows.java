package workflows.sra.sample;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.stereotype.Service;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
//import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;

@Service
public class SampleWorkflows extends Workflows<Sample>{
	private static final play.Logger.ALogger logger = play.Logger.of(SampleWorkflows.class);

	@Override
	public void applyPreStateRules(ContextValidation validation, Sample sample, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, Sample object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, Sample object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applySuccessPostStateRules(ContextValidation validation, Sample exp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation, Sample exp, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setState(ContextValidation contextValidation, Sample sample, State nextState) {
		contextValidation.setUpdateMode();

		CommonValidationHelper.validateState(ObjectType.CODE.SRASample, nextState, contextValidation); 	
		logger.debug("contextValidation.error apres validateState " + contextValidation.errors);

		if(!contextValidation.hasErrors() && !nextState.code.equals(sample.state.code)){
			applyPreStateRules(contextValidation, sample, nextState);
			//submission.validate(contextValidation);
			if(!contextValidation.hasErrors()){
				// Gerer l'historique des states :
				sample.state = updateHistoricalNextState(sample.state, nextState);	
				// sauver le state dans la base avec traceInformation
				MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME,  Sample.class, 
						DBQuery.is("code", sample.code),
						DBUpdate.set("state", sample.state).set("traceInformation", sample.traceInformation));
				applySuccessPostStateRules(contextValidation, sample);
				nextState(contextValidation, sample);		
			}else{
				applyErrorPostStateRules(contextValidation, sample, nextState);	
			}
		} else {
			logger.error("ATTENTION ERROR :"+contextValidation.errors);
		}
	}

	@Override
	public void nextState(ContextValidation contextValidation, Sample sample) {

		
	}

}
