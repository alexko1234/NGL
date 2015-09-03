package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.Play;

import com.typesafe.config.ConfigFactory;

import java.util.Date;

import javax.mail.MessagingException;

import mail.MailServiceException;
import mail.MailServices;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.util.SraException;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import fr.cea.ig.MongoDBDAO;

public class FileAcServices  {

	public static void updateStateSubmission(Submission submission, String status) {
		submission.state.code=status;
		// Mettre à jour objets submission et sous-objet study, sample et experiment pour status.
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", submission.code).notExists("accession"),
				DBUpdate.set("state.code", status).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));	

		if (StringUtils.isNotBlank(submission.studyCode)) {
			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
					DBQuery.is("code", submission.code).notExists("accession"),
					DBUpdate.set("state.code", status).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));	
		}
		if (submission.sampleCodes != null){
			for (int i = 0; i < submission.sampleCodes.size() ; i++) {
				MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class,
						DBQuery.is("code", submission.sampleCodes.get(i)).notExists("accession"),
						DBUpdate.set("state.code", "submitted").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 			
			}
		}
		if (submission.experimentCodes != null){
			for (int i = 0; i < submission.experimentCodes.size() ; i++) {
				MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
						DBQuery.is("code", submission.experimentCodes.get(i)).notExists("accession"),
						DBUpdate.set("state.code", "submitted").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 			
			}
		}
	}
	

	public static Submission traitementFileAC(String submissionCode, File ebiFileAc) throws IOException, SraException, MailServiceException {
		if (StringUtils.isBlank(submissionCode) || (ebiFileAc == null)) {
			throw new SraException("traitementFileAC :: parametres d'entree à null" );
		}
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		
		if (! ebiFileAc.exists()){
			throw new SraException("Fichier des AC de l'Ebi non present sur disque : "+ ebiFileAc.getAbsolutePath());
		}

		BufferedReader inputBuffer = null;
		try {
			inputBuffer = new BufferedReader(new FileReader(ebiFileAc));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String lg = null;
		String ligne;

		// Get global parameters for email => utiliser Play.application().configuration().getString plutot que
		// ConfigFactory.load().getString pour recuperer les parametres pour avoir les surcharges de AbstractTest si 
		// test unitaires 
		MailServices mailService = new MailServices();
//		String expediteur = ConfigFactory.load().getString("accessionReporting.email.from"); 
		String expediteur = Play.application().configuration().getString("accessionReporting.email.from"); 

		String dest = Play.application().configuration().getString("accessionReporting.email.to");   
		String subjectSuccess = Play.application().configuration().getString("accessionReporting.email.subject.success");
		
		//Logger.debug("subjectSuccess = "+Play.application().configuration().getString("accessionReporting.email.subject.success"));
		
		String subjectError = Play.application().configuration().getString("accessionReporting.email.subject.error");
		Set<String> destinataires = new HashSet<String>();
		destinataires.addAll(Arrays.asList(dest.split(",")));    		    

		String sujet = null;
		
		
		
		
		Map<String, String> mapSamples = new HashMap<String, String>(); 
		Map<String, String> mapExperiments = new HashMap<String, String>(); 
		Map<String, String> mapRuns = new HashMap<String, String>(); 
		String submissionAc = null;
		String studyAc = null;
		String message = null;
		String ebiSubmissionCode = null;
		String ebiStudyCode = null;
		String errorStatus = "errorResultSendXml";
		String okStatus = "submitted";
		
		while ((lg = inputBuffer.readLine()) != null) {
			if (lg.startsWith("<?")){
				// ignorer
			} else if (lg.matches("^\\s*$")) {
				// ignorer
			} else {
				Boolean resultAC = false;
				//System.out.println("ligne = '"+ lg+"'");
				String pattern_string = "<RECEIPT\\s+receiptDate=\"(\\S+)\"\\s+submissionFile=\"(\\S+)\"\\s+success=\"true\"";
				java.util.regex.Pattern pattern = Pattern.compile(pattern_string);
				Matcher m = pattern.matcher(lg);
				if ( ! m.find() ) {
					//System.out.println("ligne RECEIPT absente dans '"+lg+"'");
					// mettre status à jour
					updateStateSubmission(submission, errorStatus); 
					message = "Absence de la ligne RECEIPT ... pour  " + submissionCode + " dans fichier "+ ebiFileAc.getPath();
					sujet = "Probleme parsing fichier des AC : ";
					//mailService.sendMail("william@genoscope.cns.fr", destinataires, sujet, message);
				    mailService.sendMail(expediteur, destinataires, subjectError, new String(message.getBytes(), "iso-8859-1"));
					return submission;
				} 
				//System.out.println("Traitement des AC :");
				String [] tab = lg.split(">");
				String patternAc = "<(\\S+)\\s+accession=\"(\\S+)\"\\s+alias=\"(\\S+)\"";
				java.util.regex.Pattern pAc = Pattern.compile(patternAc);


				for(String info : tab) {
					//System.out.println(info);
					Matcher mAc = pAc.matcher(info);
					// Appel de find obligatoire pour pouvoir récupérer $1 ...$n
					if ( ! mAc.find() ) {
						// autre ligne que AC.
					} else {
						//System.out.println("type='"+mAc.group(1)+"', accession='"+mAc.group(2)+"', alias='"+ mAc.group(3)+"'" );
						if (mAc.group(1).equalsIgnoreCase("RUN")){
							//System.out.println("insertion dans mapRun de "+ mAc.group(3) + " et "+ mAc.group(2));
							mapRuns.put(mAc.group(3), mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("EXPERIMENT")){
							//System.out.println("insertion dans mapExperiment de " + mAc.group(3) + " et "+ mAc.group(2));
							mapExperiments.put(mAc.group(3), mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("SAMPLE")){
							//System.out.println("insertion dans mapSample de " + mAc.group(3) + " et "+ mAc.group(2));
							mapSamples.put(mAc.group(3), mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("STUDY")){
							ebiStudyCode = mAc.group(3);
							studyAc = mAc.group(2);
							//System.out.println("insertion dans mapStudy de " + mAc.group(3) + " et "+ mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("SUBMISSION")){
							ebiSubmissionCode = mAc.group(3);
							submissionAc =  mAc.group(2);
							//System.out.println("insertion dans mapSubmission de "  + mAc.group(3) + " et "+ mAc.group(2));
						} else {

						}
					}
				}
			}
		}
		
		// Mise à jour des objets :
		Boolean error = false;
		sujet = "Probleme parsing fichier des AC : ";
		message = "Pour la soumission " + submissionCode + ", le fichier des AC "+ ebiFileAc.getPath() + "</br>";
		destinataires.add(submission.state.user);
		
		if (StringUtils.isBlank(ebiSubmissionCode)) {
			//System.out.println("Pas de Recuperation de ebiSubmissionCode");
		    message += "- ne contient pas ebiSubmissionCode \n";
			error = true;
		} 
		if (! ebiSubmissionCode.equals(submissionCode)) {
			//System.out.println("ebiSubmissionCode != submissionCode");
			message += "- contient un ebiSubmissionCode ("  + ebiSubmissionCode + ") different du submissionCode passé en parametre "+ submissionCode + "</br>"; 
			error = true;
		}
		// Verifier que le nombre d'ac recuperés dans le fichier est bien celui attendu pour l'objet submission:
		if (StringUtils.isNotBlank(submission.studyCode)) {
			if (StringUtils.isBlank(studyAc)) {
				//System.out.println("studyAc attendu non trouvé pour " + submission.studyCode);
			    message += "- ne contient pas de valeur pour le studyCode " + submission.studyCode+"</br>";
			}
		}
		if (submission.sampleCodes != null){
			for (int i = 0; i < submission.sampleCodes.size() ; i++) {
				if (!mapSamples.containsKey(submission.sampleCodes.get(i))){
					//System.out.println("sampleAc attendu non trouvé pour " + submission.sampleCodes.get(i));
					message += "- ne contient pas d'AC pour le sampleCode " + submission.sampleCodes.get(i)+"</br>";
					error = true;
					
				}	
			}
		}
		if (submission.experimentCodes != null){
			for (int i = 0; i < submission.experimentCodes.size() ; i++) {
				if (!mapExperiments.containsKey(submission.experimentCodes.get(i))){
					//System.out.println("experimentAc attendu non trouvé pour " + submission.experimentCodes.get(i));
					message += "- ne contient pas d'AC pour l'experimentCode " + submission.experimentCodes.get(i) + "</br>";
					error = true;
				}	
			}
		}
		if (submission.runCodes != null){
			for (int i = 0; i < submission.runCodes.size() ; i++) {
				//System.out.println("runCode========="+ submission.runCodes.get(i));
				if (!mapRuns.containsKey(submission.runCodes.get(i))){
					//System.out.println("runAc attendu non trouvé pour " + submission.runCodes.get(i));
					message += "- ne contient pas d'AC pour le runCode " + submission.runCodes.get(i) + "</br>";
					error = true;
				}	
			}
		}

		if (error){
			//System.out.println("ERROR" + message);
			//mailService.sendMail("william@genoscope.cns.fr", destinataires, sujet, message);
		    mailService.sendMail(expediteur, destinataires, subjectError, new String(message.getBytes(), "iso-8859-1"));
			updateStateSubmission(submission, errorStatus);
			return submission;
		} else {
			//System.out.println("OK");
		}
		
		// Mise à jour de la soumission et de ses objets pour les AC et pour le status :
		
		sujet = "Liste des AC attribues pour la soumission "  + submissionCode ;
		message = "Liste des AC attribues pour la soumission "  + submissionCode + " : </br></br>";
		String retourChariot = "</br>";
		
		message += "submissionCode = " + submissionCode + ",   AC = "+ submissionAc + "</br>";  
		submission.state.code=okStatus;
		submission.accession=submissionAc;
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", submissionCode).notExists("accession"),
				DBUpdate.set("accession", submissionAc).set("state.code", okStatus).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));	

		if (StringUtils.isNotBlank(ebiStudyCode)) {
			message += "studyCode = " + ebiStudyCode + ",   AC = "+ studyAc + "</br>";  
			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
					DBQuery.is("code", ebiStudyCode).notExists("accession"),
					DBUpdate.set("accession", studyAc).set("state.code", okStatus).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));
		}
		for(Entry<String, String> entry : mapSamples.entrySet()) {
			String code = entry.getKey();
			String ac = entry.getValue();
			message += "sampleCode = " + code + ",   AC = "+ ac + "</br>";  
			MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class,
					DBQuery.is("code", code).notExists("accession"),
					DBUpdate.set("accession", ac).set("state.code", okStatus).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 		
		}
		for(Entry<String, String> entry : mapExperiments.entrySet()) {
			String code = entry.getKey();
			String ac = entry.getValue();
			message += "experimentCode = " + code + ",   AC = "+ ac + "</br>";  
			MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
					DBQuery.is("code", code).notExists("accession"),
					DBUpdate.set("accession", ac).set("state.code", okStatus).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 
		}
		for(Entry<String, String> entry : mapRuns.entrySet()) {
			String code = entry.getKey();
			String ac = entry.getValue();
			message += "runCode = " + code + ",   AC = "+ ac  + "</br>";  
			MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
					DBQuery.is("run.code", code).notExists("accession"),
					DBUpdate.set("run.accession", ac)); 			
		}
		mailService.sendMail(expediteur, destinataires, subjectSuccess, new String(message.getBytes(), "iso-8859-1"));
		return submission;
	}

}
