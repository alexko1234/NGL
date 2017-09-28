package workflows.sra.study;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.util.SraCodeHelper;
import models.sra.submit.util.SraException;
import models.utils.InstanceConstants;
import play.Logger;
import validation.ContextValidation;
import workflows.sra.submission.SubmissionWorkflows;

@Service
public class StudyWorkflowsHelper {

	@Autowired
	SubmissionWorkflows submissionWorkflows;

	public void createSubmissionEntityforRelease(Study study, ContextValidation validation)
	{
		try {
			Submission submission = null;
			Date courantDate = new java.util.Date();
			submission = new Submission(validation.getUser(), study.projectCodes);
			submission.code = SraCodeHelper.getInstance().generateSubmissionCode(study.projectCodes);
			submission.creationDate = courantDate;
			Logger.debug("submissionCode="+ submission.code);
			submission.release = true;

			// mettre à jour l'objet submission pour le study  :
			if (! submission.refStudyCodes.contains("study.code")){
				submission.refStudyCodes.add(study.code);
				Logger.debug("ajout de studyCode dans submission.refStudyCode");

			}
			submission.studyCode = study.code;

			submission.state = new State("N-R", validation.getUser());

			if (StringUtils.isNotBlank(submission.studyCode)){
				study.state.code = "N-R";
				study.traceInformation.modifyDate = new Date();
				study.traceInformation.modifyUser = validation.getUser();
				MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
						DBQuery.is("code", study.code),
						DBUpdate.set("state.code", "N-R").set("traceInformation.modifyUser", validation.getUser()).set("traceInformation.modifyDate", new Date()));	
			}

			// puis valider et sauver submission
			validation.setCreationMode();
			validation.getContextObjects().put("type", "sra");

			Logger.debug("AVANT submission.validate="+validation.errors);

			submission.validate(validation);
			Logger.debug("APRES submission.validate="+validation.errors);

			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code",submission.code)){	
				MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
				Logger.debug("sauvegarde dans la base du submission " + submission.code);
			}
			validation.setUpdateMode();
			validation.getContextObjects().remove("type");

			// Avancer le status de submission et study à IW-SUB-R
			State state = new State("IW-SUB-R", validation.getUser());
			submissionWorkflows.setState(validation, submission, state);
		} catch (SraException e) {
			validation.addErrors("study", e.getMessage(), study.code);
		}

	}
}