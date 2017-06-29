package workflows.sra.submission;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.stereotype.Service;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Configuration;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.sra.SraValidationHelper;
import workflows.Workflows;
@Service
public class SubmissionWorkflows extends Workflows<Submission>{

	//public static SubmissionWorkflows instance= new SubmissionWorkflows();


	@Override
	public void applyPreStateRules(ContextValidation validation,
			Submission object, State nextState) {
		// TODO Auto-generated method stub
		
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
	public void applySuccessPostStateRules(ContextValidation validation,
			Submission object) {	
		
		if (StringUtils.isNotBlank(object.studyCode)) {
			if ((! object.state.code.equalsIgnoreCase("N")) && (! object.state.code.equalsIgnoreCase("N-R"))) {
				Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, object.studyCode);
				// Recuperer object study pour mettre historique des state traceInformation à jour:
				if (! object.state.code.equalsIgnoreCase("F-SUB-R")){
					// Recopier etat de soumission dans etat du study
					study.state = updateHistoricalNextState(study.state, object.state);
					study.traceInformation = updateTraceInformation(study.traceInformation, object.state);				
				} else {
					// Mettre etat du study à F-SUB si etat de submission est à F-SUB-R :
					State state_replacement = new State();
					state_replacement.code = "F-SUB";
					state_replacement.user = object.state.user;
					state_replacement.date = object.state.date;
					study.state = updateHistoricalNextState(study.state, state_replacement);
					study.traceInformation = updateTraceInformation(study.traceInformation, state_replacement);				
				}
				// Mettre à jour study pour le state, traceInformation 
				MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
				DBQuery.is("code", object.code).notExists("accession"),
				DBUpdate.set("state", study.state).set("traceInformation", study.traceInformation));
		   }
		}
	
		if (object.sampleCodes != null){
			for (int i = 0; i < object.sampleCodes.size() ; i++) {
				Sample sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, object.sampleCodes.get(i));
				sample.state = updateHistoricalNextState(sample.state, object.state);
				sample.traceInformation = updateTraceInformation(sample.traceInformation, object.state);

				MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class,
						DBQuery.is("code", object.sampleCodes.get(i)).notExists("accession"),
						DBUpdate.set("state", sample.state).set("traceInformation", sample.traceInformation));
			}
		}
		
		if (object.experimentCodes != null){
			for (int i = 0; i < object.experimentCodes.size() ; i++) {
				
				Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, object.experimentCodes.get(i));
				experiment.state = updateHistoricalNextState(experiment.state, object.state);
				experiment.traceInformation = updateTraceInformation(experiment.traceInformation, object.state);
				
				// Updater objet experiment :
				MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
						DBQuery.is("code", object.experimentCodes.get(i)).notExists("accession"),
						DBUpdate.set("state", experiment.state).set("traceInformation", experiment.traceInformation));

				// Updater objet readSet :
				ReadSet readset = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, experiment.readSetCode);
				readset.submissionState = updateHistoricalNextState(readset.submissionState, object.state);
				readset.traceInformation = updateTraceInformation(readset.traceInformation, object.state);

				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,
						DBQuery.is("code", experiment.readSetCode),
						DBUpdate.set("submissionState", readset.submissionState).set("traceInformation", readset.traceInformation));
			}
		}
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation,
			Submission object, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setState(ContextValidation contextValidation, Submission object, State nextState) {
		contextValidation.setUpdateMode();

		// Gerer historique de la modification
		object.traceInformation = updateTraceInformation(object.traceInformation, nextState); 			

		// verifier que le state à installer est valide avant de mettre à jour base de données : 
		// verification qui ne passe pas par VariableSRA [SraValidationHelper.requiredAndConstraint(contextValidation, nextState.code , VariableSRA.mapStatus, "state.code")]		
        // mais par CommonValidationHelper.validateState(ObjectType.CODE.SRASubmission, nextState, contextValidation); 
		// pour uniformiser avec reste du code ngl
		
		CommonValidationHelper.validateState(ObjectType.CODE.SRASubmission, nextState, contextValidation); 		
		if(!contextValidation.hasErrors()){
			// Gerer l'historique des states :
			object.state = updateHistoricalNextState(object.state, nextState);	
			// sauver le state dans la base avec traceInformation
			MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME,  Submission.class, 
				DBQuery.is("code", object.code),
				DBUpdate.set("state", object.state).set("traceInformation", object.traceInformation));
			if (! object.state.code.equalsIgnoreCase("N") && !object.state.code.equalsIgnoreCase("N-R")){
				applySuccessPostStateRules(contextValidation, object);
			}
		}
	}


	@Override
	public void nextState(ContextValidation contextValidation, Submission object) {
		// TODO Auto-generated method stub
		
	}

	
	

}
