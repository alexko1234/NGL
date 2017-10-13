package workflows.sra.submission;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.sra.submit.common.instance.Submission;
import models.utils.InstanceConstants;
import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;
@Service
public class SubmissionWorkflows extends Workflows<Submission>{

	@Autowired
	SubmissionWorkflowsHelper submissionWorkflowsHelper;
	
	@Override
	public void applyPreStateRules(ContextValidation validation,
			Submission submission, State nextState) {
		if("IP-SUB-R".equals(submission.state.code) && "F-SUB".equals(nextState.code)){
			Logger.debug("call update submission Release");
			submissionWorkflowsHelper.updateSubmissionRelease(submission);
		}
		if("IW-SUB-R".equals(nextState.code)){
			submissionWorkflowsHelper.createDirSubmission(submission, validation);
		}
		
		if("IW-SUB".equals(nextState.code)){
			submissionWorkflowsHelper.activationPrimarySubmission(validation, submission);
		}
		Logger.debug("dans apply pre state rules avec nextState = '" + nextState.code + "'");
		updateTraceInformation(submission.traceInformation, nextState); 

	}

	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, Submission object) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, Submission object) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void applySuccessPostStateRules(ContextValidation validation, Submission submission) {
		
		if (! submission.state.code.equalsIgnoreCase("N") && ! submission.state.code.equalsIgnoreCase("N-R") && !submission.state.code.equalsIgnoreCase("IW-SUB-R")){
			submissionWorkflowsHelper.updateSubmissionChildObject(submission, validation);
		}
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation,
			Submission submission, State nextState) {
		if("IW-SUB-R".equals(submission.code)){
			submissionWorkflowsHelper.rollbackSubmission(submission, validation);
		}
		if(validation.hasErrors()){
			Logger.error("Problem on SubmissionWorkflow.applyErrorPostStateRules : "+validation.errors.toString());
		}
	}

	@Override
	public void setState(ContextValidation contextValidation, Submission submission, State nextState) {
		Logger.debug("dans setState avec submission" + submission.code +" et et submission.state="+submission.state.code+ " et nextState="+nextState.code);

		contextValidation.setUpdateMode();
		// verifier que le state à installer est valide avant de mettre à jour base de données : 
		// verification qui ne passe pas par VariableSRA [SraValidationHelper.requiredAndConstraint(contextValidation, nextState.code , VariableSRA.mapStatus, "state.code")]		
		// mais par CommonValidationHelper.validateState(ObjectType.CODE.SRASubmission, nextState, contextValidation); 
		// pour uniformiser avec reste du code ngl
		Logger.debug("dans setState");
		Logger.debug("contextValidation.error avant validateState " + contextValidation.errors);

		CommonValidationHelper.validateState(ObjectType.CODE.SRASubmission, nextState, contextValidation); 	
		Logger.debug("contextValidation.error apres validateState " + contextValidation.errors);

		if(!contextValidation.hasErrors() && !nextState.code.equals(submission.state.code)){
			applyPreStateRules(contextValidation, submission, nextState);
			//submission.validate(contextValidation);
			if(!contextValidation.hasErrors()){
				// Gerer l'historique des states :
				submission.state = updateHistoricalNextState(submission.state, nextState);	
				// sauver le state dans la base avec traceInformation
				MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME,  Submission.class, 
						DBQuery.is("code", submission.code),
						DBUpdate.set("state", submission.state).set("traceInformation", submission.traceInformation));
				applySuccessPostStateRules(contextValidation, submission);
				nextState(contextValidation, submission);		
			}else{
				applyErrorPostStateRules(contextValidation, submission, nextState);	
			}
		} else {
			Logger.error("ATTENTION ERROR :"+contextValidation.errors);
		}
	}

	@Override
	public void nextState(ContextValidation contextValidation, Submission object) {
		// TODO Auto-generated method stub

	}


}
