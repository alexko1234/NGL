package workflows.sra.submission;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

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

public class SubmissionWorkflows extends Workflows<Submission>{

	public static SubmissionWorkflows instance= new SubmissionWorkflows();


	@Override
	public void applyPreStateRules(ContextValidation validation,
			Submission object, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyCurrentStateRules(ContextValidation validation,
			Submission object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applySuccessPostStateRules(ContextValidation validation,
			Submission object) {	
		
		if (StringUtils.isNotBlank(object.studyCode)) {
			// Recuperer object study pour mettre historique des state traceInformation à jour:
			Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, object.studyCode);
			study.state = updateHistoricalNextState(study.state, object.state);
			study.traceInformation = updateTraceInformation(study.traceInformation, object.state);
			// Mettre à jour study pour le state, traceInformation 
			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
					DBQuery.is("code", object.code).notExists("accession"),
					DBUpdate.set("state", study.state).set("traceInformation", study.traceInformation));
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
			
		}
		applySuccessPostStateRules(contextValidation, object);
		// mettre à jour etat des samples, experiments, study, readset et conf
		/*if (StringUtils.isNotBlank(object.configCode)) {
			// Recuperer object conf pour mettre historique des state traceInformation à jour:
			Configuration conf = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, object.configCode);
			conf.state = updateHistoricalNextState(conf.state, nextState);
			conf.traceInformation = updateTraceInformation(conf.traceInformation, nextState);
			// Mettre à jour study pour le state, traceInformation 
			MongoDBDAO.update(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, 
					DBQuery.is("code", object.code),
					DBUpdate.set("state", conf.state).set("traceInformation", conf.traceInformation));
		}*/
		
	}


	@Override
	public void nextState(ContextValidation contextValidation, Submission object) {
		// TODO Auto-generated method stub
		
	}

	
	

}
