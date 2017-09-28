package workflows.sra.study;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.sra.submit.common.instance.Study;
import models.utils.InstanceConstants;
import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;
@Service
public class StudyWorkflows extends Workflows<Study>{

	@Autowired
	StudyWorkflowsHelper studyWorkflowsHelper;

	@Override
	public void applyPreStateRules(ContextValidation validation,
			Study study, State nextState) {
		Logger.debug("apply pre state rules");
		updateTraceInformation(study.traceInformation, nextState); 
		if(nextState.code.equals("IW-SUB-R")){
			studyWorkflowsHelper.createSubmissionEntityforRelease(study, validation);
		}

	}

	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, Study study) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, Study study) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void applySuccessPostStateRules(ContextValidation validation, Study study) {

	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation,
			Study study, State nextState) {

	}

	@Override
	public void setState(ContextValidation contextValidation, Study study, State nextState) {
		System.out.println("dans setState avec study" + study.code +" et et study.state="+study.state.code+ " et nextState="+nextState.code);

		contextValidation.setUpdateMode();
		// verifier que le state à installer est valide avant de mettre à jour base de données : 
		// verification qui ne passe pas par VariableSRA [SraValidationHelper.requiredAndConstraint(contextValidation, nextState.code , VariableSRA.mapStatus, "state.code")]		
		// mais par CommonValidationHelper.validateState(ObjectType.CODE.SRASubmission, nextState, contextValidation); 
		// pour uniformiser avec reste du code ngl
		Logger.debug("dans setState");
		Logger.debug("contextValidation.error avant validateState " + contextValidation.errors);

		CommonValidationHelper.validateState(ObjectType.CODE.SRAStudy, nextState, contextValidation); 	
		Logger.debug("contextValidation.error apres validateState " + contextValidation.errors);

		if(!contextValidation.hasErrors() && !nextState.code.equals(study.state.code)){
			applyPreStateRules(contextValidation, study, nextState);
			//submission.validate(contextValidation);
			if(!contextValidation.hasErrors()){
				// Gerer l'historique des states :
				study.state = updateHistoricalNextState(study.state, nextState);	
				// sauver le state dans la base avec traceInformation
				MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME,  Study.class, 
						DBQuery.is("code", study.code),
						DBUpdate.set("state", study.state).set("traceInformation", study.traceInformation));
				applySuccessPostStateRules(contextValidation, study);
				nextState(contextValidation, study);		
			}else{
				applyErrorPostStateRules(contextValidation, study, nextState);	
			}
		} else {
			Logger.debug("ATTENTION ERROR :"+contextValidation.errors);
		}
	}


	@Override
	public void nextState(ContextValidation contextValidation, Study study) {
		// TODO Auto-generated method stub

	}




}
