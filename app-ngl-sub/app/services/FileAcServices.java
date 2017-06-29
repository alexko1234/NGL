package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
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
import play.api.modules.spring.Spring;
import validation.ContextValidation;
import workflows.sra.submission.SubmissionWorkflows;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import com.typesafe.config.ConfigFactory;

import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.commons.lang3.StringUtils;


import mail.MailServiceException;
import mail.MailServices;
import models.laboratory.common.instance.State;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.util.SraException;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import fr.cea.ig.MongoDBDAO;

public class FileAcServices  {
	final static SubmissionWorkflows submissionWorkflows = Spring.getBeanOfType(SubmissionWorkflows.class);

	public static void updateStateSubmission(ContextValidation ctxVal, Submission submission, String status) {
		submission.state.code = status;
		String user = ctxVal.getUser();
		// Mettre à jour objets submission et sous-objet study, sample et experiment pour status.
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", submission.code).notExists("accession"),
				DBUpdate.set("state.code", status).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", new Date()));	

		if (StringUtils.isNotBlank(submission.studyCode)) {
			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
					DBQuery.is("code", submission.code).notExists("accession"),
					DBUpdate.set("state.code", status).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", new Date()));	
		}
		if (submission.sampleCodes != null){
			for (int i = 0; i < submission.sampleCodes.size() ; i++) {
				MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class,
						DBQuery.is("code", submission.sampleCodes.get(i)).notExists("accession"),
						DBUpdate.set("state.code", status).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", new Date())); 			
			}
		}
		if (submission.experimentCodes != null){
			for (int i = 0; i < submission.experimentCodes.size() ; i++) {
				Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, submission.experimentCodes.get(i));
				// Updater objet experiment :
				MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
						DBQuery.is("code", submission.experimentCodes.get(i)).notExists("accession"),
						DBUpdate.set("state.code", status).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", new Date())); 			
				// Updater objet readSet :
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,
						DBQuery.is("code", experiment.readSetCode),
						DBUpdate.set("state.code", status).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", new Date())); 			
			}
				
		}
	}
	

	public static Submission traitementFileAC(ContextValidation ctxVal, String submissionCode, File ebiFileAc) throws IOException, SraException, MailServiceException {
		System.out.println("coucou");
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
		System.out.println("destinataires = "+ dest);
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
		String errorStatus = "FE-SUB";
		String okStatus = "F-SUB";
		Boolean ebiSuccess = false;	
		
		/*
		 * Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();
			/*
			 * Etape 3 : création d'un Document
			 */
			
			final Document document= builder.parse(ebiFileAc);
			//Affiche du prologue
			System.out.println("*************PROLOGUE************");
			System.out.println("version : " + document.getXmlVersion());
			System.out.println("encodage : " + document.getXmlEncoding());      
			System.out.println("standalone : " + document.getXmlStandalone());
			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();
			//Affichage de l'élément racine
			System.out.println("\n*************RACINE************");
			System.out.println(racine.getNodeName());
			System.out.println("success = " + racine.getAttribute("success"));
			//System.out.println(((Node) racine).getNodeName());
			//System.out.println("success = " + ((DocumentBuilderFactory) racine).getAttribute("success"));
			/*
			 * Etape 5 : récupération des samples
			 */
			

			
			final NodeList racineNoeuds = racine.getChildNodes();
			final int nbRacineNoeuds = racineNoeuds.getLength();
			
			
			
			if( racine.getAttribute("success").equalsIgnoreCase ("true")){
				ebiSuccess = true;
			}
			for (int i = 0; i<nbRacineNoeuds; i++) {
				
				if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					
					final Element elt = (Element) racineNoeuds.item(i);
					//Affichage d'un elt :
					System.out.println("\n*************Elt************");
					
					String alias = elt.getAttribute("alias");
					String accession = elt.getAttribute("accession");
					
					System.out.println("alias : " + alias);
					System.out.println("accession : " + accession);
					if(elt.getTagName().equalsIgnoreCase("SUBMISSION")) {
						ebiSubmissionCode = elt.getAttribute("alias");	
						submissionAc = elt.getAttribute("accession");
					} else if(elt.getTagName().equalsIgnoreCase("STUDY")) {
						ebiStudyCode = elt.getAttribute("alias");	
						studyAc = elt.getAttribute("accession");
				    } else if(elt.getTagName().equalsIgnoreCase("SAMPLE")) {
				    	mapSamples.put(elt.getAttribute("alias"), elt.getAttribute("accession"));	
					} else if(elt.getTagName().equalsIgnoreCase("EXPERIMENT")) {
				    	mapExperiments.put(elt.getAttribute("alias"), elt.getAttribute("accession"));	
					} else if(elt.getTagName().equalsIgnoreCase("RUN")) {
						mapRuns.put(elt.getAttribute("alias"), elt.getAttribute("accession"));
					} else {
						
					}
				}
				
				
			}  // end for  
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} 
		
		if (! ebiSuccess ) {
			System.out.println("ligne RECEIPT absente dans '"+lg+"'");
			// mettre status à jour
			updateStateSubmission(ctxVal, submission, errorStatus); 
			message = "Absence de la ligne RECEIPT ... pour  " + submissionCode + " dans fichier "+ ebiFileAc.getPath();
			sujet = "Probleme parsing fichier des AC : ";
			//mailService.sendMail("william@genoscope.cns.fr", destinataires, sujet, message);
			mailService.sendMail(expediteur, destinataires, subjectError, new String(message.getBytes(), "iso-8859-1"));
			return submission;
		}
		
		
		// Mise à jour des objets :
		Boolean error = false;
		sujet = "Probleme parsing fichier des AC : ";
		message = "Pour la soumission " + submissionCode + ", le fichier des AC "+ ebiFileAc.getPath() + "</br>";
		String destinataire = submission.creationUser;
		
		if (StringUtils.isNotBlank(destinataire)) {
			if(!destinataire.endsWith("@genoscope.cns.fr")) {
				destinataire = destinataire + "@genoscope.cns.fr";
			}
			destinataires.add(destinataire);
		}
	
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
			updateStateSubmission(ctxVal, submission, "FE-SUB");
			return submission;
		} else {
			//System.out.println("OK");
		}
		
		// Mise à jour dans la base de la soumission et de ses objets pour les AC :
		ctxVal.setUpdateMode();
		sujet = "Liste des AC attribues pour la soumission "  + submissionCode ;
		message = "Liste des AC attribues pour la soumission "  + submissionCode + " : </br></br>";
		String retourChariot = "</br>";
		
		message += "submissionCode = " + submissionCode + ",   AC = "+ submissionAc + "</br>";  
		submission.accession=submissionAc;
		String user = ctxVal.getUser();
				
		Calendar calendar = Calendar.getInstance();
		Date date  = calendar.getTime();		
		calendar.add(Calendar.YEAR, 2);
		Date release_date  = calendar.getTime();
				
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", submissionCode).notExists("accession"),
				DBUpdate.set("accession", submissionAc).set("submissionDate", date).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", date));	

		if (StringUtils.isNotBlank(ebiStudyCode)) {	
			message += "studyCode = " + ebiStudyCode + ",   AC = "+ studyAc + "</br>";  
			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
					DBQuery.is("code", ebiStudyCode).notExists("accession"),
					DBUpdate.set("accession", studyAc).set("firstSubmissionDate", date).set("releaseDate", release_date).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", date));
		}
		for(Entry<String, String> entry : mapSamples.entrySet()) {
			String code = entry.getKey();
			String ac = entry.getValue();
			message += "sampleCode = " + code + ",   AC = "+ ac + "</br>";  
			MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class,
					DBQuery.is("code", code).notExists("accession"),
					DBUpdate.set("accession", ac).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", date)); 		
		}
		for(Entry<String, String> entry : mapExperiments.entrySet()) {
			String code = entry.getKey();
			String ac = entry.getValue();
			message += "experimentCode = " + code + ",   AC = "+ ac + "</br>";  

			MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
					DBQuery.is("code", code).notExists("accession"),
					DBUpdate.set("accession", ac).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", date)); 	
		}
		for(Entry<String, String> entry : mapRuns.entrySet()) {
			String code = entry.getKey();
			String ac = entry.getValue();
			message += "runCode = " + code + ",   AC = "+ ac  + "</br>";  
			MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
					DBQuery.is("run.code", code).notExists("run.accession"),
					DBUpdate.set("run.accession", ac)); 			
		}
		State state = new State(okStatus, user);
		
		submissionWorkflows.setState(ctxVal, submission, state);
		System.out.println("expediteur =" + expediteur);
		System.out.println("destinataires =" + destinataires);
		System.out.println("subjectSuccess =" + subjectSuccess);
		mailService.sendMail(expediteur, destinataires, subjectSuccess, new String(message.getBytes(), "iso-8859-1"));
		return submission;
	}

}
