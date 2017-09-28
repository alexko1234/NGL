package workflows.sra.experiment;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.sra.submit.sra.instance.Experiment;
import models.utils.InstanceConstants;
import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;

public class ExperimentWorkflows extends Workflows<Experiment>{

	@Override
	public void applyPreStateRules(ContextValidation validation, Experiment exp, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, Experiment object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, Experiment object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applySuccessPostStateRules(ContextValidation validation, Experiment exp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation, Experiment exp, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setState(ContextValidation contextValidation, Experiment experiment, State nextState) {
		contextValidation.setUpdateMode();

		CommonValidationHelper.validateState(ObjectType.CODE.Sample, nextState, contextValidation); 	
		Logger.debug("contextValidation.error apres validateState " + contextValidation.errors);

		if(!contextValidation.hasErrors() && !nextState.code.equals(experiment.state.code)){
			applyPreStateRules(contextValidation, experiment, nextState);
			//submission.validate(contextValidation);
			if(!contextValidation.hasErrors()){
				// Gerer l'historique des states :
				experiment.state = updateHistoricalNextState(experiment.state, nextState);	
				// sauver le state dans la base avec traceInformation
				MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME,  Experiment.class, 
						DBQuery.is("code", experiment.code),
						DBUpdate.set("state", experiment.state).set("traceInformation", experiment.traceInformation));
				applySuccessPostStateRules(contextValidation, experiment);
				nextState(contextValidation, experiment);		
			}else{
				applyErrorPostStateRules(contextValidation, experiment, nextState);	
			}
		} else {
			Logger.debug("ATTENTION ERROR :"+contextValidation.errors);
		}
	}

	@Override
	public void nextState(ContextValidation contextValidation, Experiment object) {
		// TODO Auto-generated method stub
		
	}

}
