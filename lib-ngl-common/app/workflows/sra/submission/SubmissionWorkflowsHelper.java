package workflows.sra.submission;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.AbstractStudy;
import models.sra.submit.common.instance.ExternalStudy;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Configuration;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import play.Logger;
import validation.ContextValidation;
import workflows.run.Workflows;
import workflows.sra.experiment.ExperimentWorkflows;
import workflows.sra.sample.SampleWorkflows;
import workflows.sra.study.StudyWorkflows;

@Service
public class SubmissionWorkflowsHelper {

	@Autowired
	StudyWorkflows studyWorkflows;
	@Autowired
	SampleWorkflows sampleWorkflows;
	@Autowired
	ExperimentWorkflows experimentWorkflows;
	

	public void updateSubmissionRelease(Submission submission)
	{
		Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, submission.studyCode);
		Calendar calendar = Calendar.getInstance();
		Date date  = calendar.getTime();		
		Date release_date  = calendar.getTime();
		Logger.debug("update submission date study "+study.code+" with date "+release_date);
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", submission.code),
				DBUpdate.set("submissionDate", date).set("xmlSubmission", "submission.xml"));	

		MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
				DBQuery.is("accession", study.accession),
				DBUpdate.set("releaseDate", release_date));
	}

	public void createDirSubmission(Submission submission,ContextValidation validation){
		// Determiner le repertoire de soumission:
		
		submission.submissionDirectory = VariableSRA.submissionRootDirectory + File.separator + submission.code; 
		if (submission.release) {
			submission.submissionDirectory = submission.submissionDirectory + "_release"; 
		}
		File dataRep = new File(submission.submissionDirectory);
		Logger.debug("Creation du repertoire de soumission : " + submission.submissionDirectory);
		Logger.of("SRA").info("Creation du repertoire de soumission" + submission.submissionDirectory);
		if (dataRep.exists()){
			validation.addErrors("submission", "error.directory.release.exist",dataRep,submission.code);
		} else {
			if(!dataRep.mkdirs()){	
				validation.addErrors("submission", "error.directory.release.create",dataRep,submission.code);
			}
		}
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME,  Submission.class, 
				DBQuery.is("code", submission.code),
				DBUpdate.set("submissionDirectory", submission.submissionDirectory));
	}
	
	public void rollbackSubmissionRelease(Submission submission,ContextValidation validation){

		// Si la soumission est connu de l'EBI on ne pourra pas l'enlever de la base :
		if (StringUtils.isNotBlank(submission.accession)){
			Logger.debug("objet submission avec AC : submissionCode = "+ submission.code + " et submissionAC = "+ submission.accession);
			return;
		} 
		// Si la soumission concerne une release avec status "N-R" ou IW-SUB-R:
		if (submission.release && (submission.state.code.equalsIgnoreCase("N-R")||(submission.state.code.equalsIgnoreCase("IW-SUB-R")))) {
			// detruire la soumission :
			MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submission.code);
			// remettre le status du study avec un status F-SUB
			// La date de release du study est modifié seulement si retour posifit de l'EBI pour release donc si status F-SUB-R
			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class,
					DBQuery.is("code", submission.studyCode),
					DBUpdate.set("state.code", "F-SUB").set("traceInformation.modifyUser", validation.getUser()).set("traceInformation.modifyDate", new Date()));
			return;
		} 


		if (! submission.experimentCodes.isEmpty()) {
			for (String experimentCode : submission.experimentCodes) {
				// verifier que l'experiment n'est pas utilisé par autre objet submission avant destruction
				Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, experimentCode);
				// mettre le status pour la soumission des readSet à NONE si possible: 
				if (experiment != null){
					// remettre les readSet dans la base avec submissionState à "NONE":
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,
							DBQuery.is("code", experiment.readSetCode),
							DBUpdate.set("submissionState.code", "NONE").set("traceInformation.modifyUser", validation.getUser()).set("traceInformation.modifyDate", new Date()));
					//System.out.println("submissionState.code remis à 'N' pour le readSet "+experiment.readSetCode);

					List <Submission> submissionList = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("experimentCodes", experimentCode)).toList();
					if (submissionList.size() > 1) {
						for (Submission sub: submissionList) {
							System.out.println(experimentCode + " utilise par objet Submission " + sub.code);
						}
						validation.addErrors("experiment", "error.experiment.mulitple.submission",experimentCode);
					} else {
						// todo : verifier qu'on ne detruit que des experiments en new ou uservalidate
						if ("N".equals(experiment.state.code) ||"V-SUB".equals(experiment.state.code)){
							MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.submit.sra.instance.Experiment.class, experimentCode);
						} else {
							Logger.debug(experimentCode + " non delété dans base car status = " + experiment.state.code);
						}
					}
				}
			}
		}

		if (! submission.refSampleCodes.isEmpty()) {	
			for (String sampleCode : submission.refSampleCodes){
				// verifier que sample n'est pas utilisé par autre objet submission avant destruction
				// normalement sample crees dans init de type external avec state=F-SUB ou sample avec state='N'
				List <Submission> submissionList = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("refSampleCodes", sampleCode)).toList();
				if (submissionList.size() > 1) {
					for (Submission sub: submissionList) {
						Logger.debug(sampleCode + " utilise par objet Submission " + sub.code);
					}
				} else {
					MongoDBDAO.deleteByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.submit.common.instance.Sample.class, sampleCode);		
					Logger.debug("deletion dans base pour sample "+sampleCode);
				}
			}
		}

		// verifier que la config à l'etat used n'est pas utilisé par une autre soumission avant de remettre son etat à 'N'
		// update la configuration pour le statut en remettant le statut new si pas utilisé par ailleurs :
		List <Submission> submissionList = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("configCode", submission.configCode)).toList();
		if (submissionList.size() <= 1) {
			MongoDBDAO.update(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, 
					DBQuery.is("code", submission.configCode),
					DBUpdate.set("state.code", "N").set("traceInformation.modifyUser", validation.getUser()).set("traceInformation.modifyDate", new Date()));	
			Logger.debug("state.code remis à 'N' pour configuration "+submission.configCode);
		}

		// verifier que le study à l'etat userValidate n'est pas utilisé par une autre soumission avant de remettre son etat à 'N'
		if (StringUtils.isNotBlank(submission.studyCode)){
			List <Submission> submissionList2 = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("studyCode", submission.studyCode)).toList();
			if (submissionList2.size() == 1) {
				MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
						DBQuery.is("code", submission.studyCode),
						DBUpdate.set("state.code", "N").set("traceInformation.modifyUser", validation.getUser()).set("traceInformation.modifyDate", new Date()));	
				Logger.debug("state.code remis à 'N' pour study "+submission.studyCode);
			}	
		}

		if (! submission.refStudyCodes.isEmpty()) {	
			// On ne peut detruire que des ExternalStudy crées et utilisés seulement par la soumission courante.
			for (String studyCode : submission.refStudyCodes){
				// verifier que study n'est pas utilisé par autre objet submission avant destruction
				// normalement study crees dans init de type external avec state=F-SUB ou study avec state='N'
				List <Submission> submissionList2 = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("refStudyCodes", studyCode)).toList();
				if (submissionList2.size() > 1) {
					for (Submission sub: submissionList2) {
						Logger.debug(studyCode + " utilise par objet Submission " + sub.code);
					}
				} else {
					AbstractStudy study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.AbstractStudy.class, studyCode);
					if (study != null){
						if ( ExternalStudy.class.isInstance(study)) {
							// on ne veut enlever que les external_study cree par cette soumission, si internalStudy cree, on veut juste le remettre avec bon state.
							//System.out.println("Recuperation dans base du study avec Ac = " + studyAc +" qui est du type externalStudy ");
							if("F-SUB".equalsIgnoreCase(study.state.code) ){
								MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.Study.class, studyCode);		
								Logger.debug("deletion dans base pour study "+studyCode);
							}
						}
					}
				}
			}
		}

		Logger.debug("deletion dans base pour submission "+submission.code);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submission.code);
	}
	
	public void updateSubmissionChildObject(Submission submission, ContextValidation validation)
	{
		Logger.debug("dans applySuccessPostStateRules submission=" + submission.code + " avec state.code='"+submission.state.code+"'");
		
		if (StringUtils.isNotBlank(submission.studyCode)) {
			Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, submission.studyCode);
			// Recuperer object study pour mettre historique des state traceInformation à jour:
			if (! submission.state.code.equalsIgnoreCase("F-SUB-R")){
				studyWorkflows.setState(validation, study, submission.state);
			} else {
				// Mettre etat du study à F-SUB si etat de submission est à F-SUB-R :
				State state_replacement = new State();
				state_replacement.code = "F-SUB";
				state_replacement.user = submission.state.user;
				state_replacement.date = submission.state.date;
				studyWorkflows.setState(validation, study, state_replacement);
			}
			Logger.debug("mise à jour du study avec state.code=" + study.state.code);
		}
		//Normalement une soumission pour release doit concerner uniquement un study, donc pas de test pour status F-SUB-R		
		if (submission.sampleCodes != null){
			for (int i = 0; i < submission.sampleCodes.size() ; i++) {
				Sample sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, submission.sampleCodes.get(i));
				sampleWorkflows.setState(validation, sample, submission.state);
			}
		}

		if (submission.experimentCodes != null){
			for (int i = 0; i < submission.experimentCodes.size() ; i++) {

				Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, submission.experimentCodes.get(i));
				experimentWorkflows.setState(validation, experiment, submission.state);

				// Updater objet readSet :
				ReadSet readset = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, experiment.readSetCode);
				Workflows.setReadSetState(validation, readset, submission.state);
			}
		}
	}

}