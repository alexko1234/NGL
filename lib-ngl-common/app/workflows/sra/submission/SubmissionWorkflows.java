package workflows.sra.submission;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;
@Service
public class SubmissionWorkflows extends Workflows<Submission>{

	@Autowired
	SubmissionWorkflowsHelper submissionWorkflowHelper;
	
	@Override
	public void applyPreStateRules(ContextValidation validation,
			Submission submission, State nextState) {
		updateTraceInformation(submission.traceInformation, nextState); 
		if("IP-SUB-R".equals(submission.state.code) && "F-SUB".equals(nextState)){
			submissionWorkflowHelper.updateSubmissionRelease(submission);
		}
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

		if (! submission.state.code.equalsIgnoreCase("N") && ! submission.state.code.equalsIgnoreCase("N-R")){
			System.out.println("dans applySuccessPostStateRules submission=" + submission.code + " avec state.code='"+submission.state.code+"'");
			// Pas de propagation des etats si N ou N-R car objets de la soumission comme study qui peut etre validé
			if ((submission.state.code.equalsIgnoreCase("N")) || (submission.state.code.equalsIgnoreCase("N-R"))) {
				System.out.println("dans applySuccessPostStateRules , sortie sans propagation des etats");
				return;
			}

			if (StringUtils.isNotBlank(submission.studyCode)) {
				Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, submission.studyCode);
				// Recuperer object study pour mettre historique des state traceInformation à jour:
				if (! submission.state.code.equalsIgnoreCase("F-SUB-R")){
					// Recopier etat de soumission dans etat du study
					study.state = updateHistoricalNextState(study.state, submission.state);
					study.traceInformation = updateTraceInformation(study.traceInformation, submission.state);				
				} else {
					// Mettre etat du study à F-SUB si etat de submission est à F-SUB-R :
					State state_replacement = new State();
					state_replacement.code = "F-SUB";
					state_replacement.user = submission.state.user;
					state_replacement.date = submission.state.date;
					study.state = updateHistoricalNextState(study.state, state_replacement);
					study.traceInformation = updateTraceInformation(study.traceInformation, state_replacement);				
				}
				// Mettre à jour study pour le state, traceInformation 
				MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
						DBQuery.is("code", submission.studyCode),
						DBUpdate.set("state", study.state).set("traceInformation", study.traceInformation));

				System.out.println("mise à jour du study avec state.code=" + study.state.code);
			}
			//Normalement une soumission pour release doit concerner uniquement un study, donc pas de test pour status F-SUB-R		
			if (submission.sampleCodes != null){
				for (int i = 0; i < submission.sampleCodes.size() ; i++) {
					Sample sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, submission.sampleCodes.get(i));
					sample.state = updateHistoricalNextState(sample.state, submission.state);
					sample.traceInformation = updateTraceInformation(sample.traceInformation, submission.state);

					MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class,
							DBQuery.is("code", submission.sampleCodes.get(i)),
							DBUpdate.set("state", sample.state).set("traceInformation", sample.traceInformation));
					System.out.println("mise à jour du sample "+ sample.code + " avec state.code=" + sample.state.code);

				}
			}

			if (submission.experimentCodes != null){
				for (int i = 0; i < submission.experimentCodes.size() ; i++) {

					Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, submission.experimentCodes.get(i));
					experiment.state = updateHistoricalNextState(experiment.state, submission.state);
					experiment.traceInformation = updateTraceInformation(experiment.traceInformation, submission.state);

					// Updater objet experiment :
					MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
							DBQuery.is("code", submission.experimentCodes.get(i)),
							DBUpdate.set("state", experiment.state).set("traceInformation", experiment.traceInformation));
					System.out.println("mise à jour de exp "+ experiment.code + " avec state.code=" + experiment.state.code);

					// Updater objet readSet :
					ReadSet readset = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, experiment.readSetCode);
					readset.submissionState = updateHistoricalNextState(readset.submissionState, submission.state);
					readset.traceInformation = updateTraceInformation(readset.traceInformation, submission.state);

					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,
							DBQuery.is("code", experiment.readSetCode),
							DBUpdate.set("submissionState", readset.submissionState).set("traceInformation", readset.traceInformation));
					System.out.println("mise à jour de readset "+ readset.code + " avec readset.code=" + readset.submissionState.code);
				}
			}
		}
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation,
			Submission submission, State nextState) {
		if("IP-SUB-R".equals(submission.code) && "F-SUB".equals(nextState)){
			submissionWorkflowHelper.updateSubmissionRelease(submission);
		}
	}

	@Override
	public void setState(ContextValidation contextValidation, Submission submission, State nextState) {
		System.out.println("dans setState avec submission" + submission.code +" et et submission.state="+submission.state.code+ " et nextState="+nextState.code);

		contextValidation.setUpdateMode();
		// verifier que le state à installer est valide avant de mettre à jour base de données : 
		// verification qui ne passe pas par VariableSRA [SraValidationHelper.requiredAndConstraint(contextValidation, nextState.code , VariableSRA.mapStatus, "state.code")]		
		// mais par CommonValidationHelper.validateState(ObjectType.CODE.SRASubmission, nextState, contextValidation); 
		// pour uniformiser avec reste du code ngl
		System.out.println("dans setState");
		System.out.println("contextValidation.error avant validateState " + contextValidation.errors);

		CommonValidationHelper.validateState(ObjectType.CODE.SRASubmission, nextState, contextValidation); 	
		System.out.println("contextValidation.error apres validateState " + contextValidation.errors);

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
			System.out.println("ATTENTION ERROR :"+contextValidation.errors);
		}
	}


	@Override
	public void nextState(ContextValidation contextValidation, Submission object) {
		// TODO Auto-generated method stub

	}




}
