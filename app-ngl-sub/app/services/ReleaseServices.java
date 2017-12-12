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

//import play.Logger;
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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

public class ReleaseServices  {
	private static final play.Logger.ALogger logger = play.Logger.of(ReleaseServices.class);

	final static SubmissionWorkflows submissionWorkflows = Spring.getBeanOfType(SubmissionWorkflows.class);

	public static Submission traitementRetourRelease(ContextValidation ctxVal, String submissionCode, File retourEbiRelease) throws IOException, SraException, MailServiceException {
		if (StringUtils.isBlank(submissionCode) || (retourEbiRelease == null)) {
			throw new SraException("traitementRelease :: parametres d'entree à null" );
		}
		System.out.println("submissionCode=" + submissionCode);
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, submission.studyCode);

		if (submission == null){
			throw new SraException("soumission " + submission.code + " impossible à recuperer dans base");
		}
		if (! retourEbiRelease.exists()){
			throw new SraException("Fichier resultat de l'ebi pour la release absent des disques : "+ retourEbiRelease.getAbsolutePath());
		}
		if (!submission.release){
			throw new SraException("soumission "+submission.code+" ne correspond pas a une soumission pour release");
		}
		BufferedReader inputBuffer = null;
		try {
			inputBuffer = new BufferedReader(new FileReader(retourEbiRelease));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		

		// Get global parameters for email => utiliser Play.application().configuration().getString plutot que
		// ConfigFactory.load().getString pour recuperer les parametres pour avoir les surcharges de AbstractTest si 
		// test unitaires 
		MailServices mailService = new MailServices();
//		String expediteur = ConfigFactory.load().getString("releaseReporting.email.from"); 
		String expediteur = Play.application().configuration().getString("releaseReporting.email.from"); 
		System.out.println("expediteur="+expediteur);
		String dest = Play.application().configuration().getString("releaseReporting.email.to");   
		System.out.println("destinataires = "+ dest);
		String subjectSuccess = Play.application().configuration().getString("releaseReporting.email.subject.success");
		
		//l.debug("subjectSuccess = "+Play.application().configuration().getString("releaseReporting.email.subject.success"));
		
		String subjectError = Play.application().configuration().getString("releaseReporting.email.subject.error");
		Set<String> destinataires = new HashSet<String>();
		
		destinataires.addAll(Arrays.asList(dest.split(",")));    		    

		String message = null;
		String errorStatus = "FE-SUB-R";
		String okStatus = "F-SUB";
		Boolean ebiSuccess = false;	
		String studyAccession = null;
		String infos = null;
		// On ne prend pas ctxVal.getUser, car methode lancé par birds,pas très informatif
		String user;
		if (StringUtils.isNotBlank(submission.creationUser)) {
			user = submission.creationUser;
		} else {
			user = ctxVal.getUser();
		}

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
			
			final Document document= builder.parse(retourEbiRelease);
			//Affiche du prologue
			/*System.out.println("*************PROLOGUE************");
			System.out.println("version : " + document.getXmlVersion());
			System.out.println("encodage : " + document.getXmlEncoding());      
			System.out.println("standalone : " + document.getXmlStandalone());
            */
			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();
			//Affichage de l'élément racine
			/*System.out.println("\n*************RACINE************");
			System.out.println(racine.getNodeName());
			System.out.println("success = " + racine.getAttribute("success"));
			*/
			final NodeList racineNoeuds = racine.getChildNodes();
			final int nbRacineNoeuds = racineNoeuds.getLength();
			
			System.out.println("Nombre de racine noeud = "+ nbRacineNoeuds);
			
			if( racine.getAttribute("success").equalsIgnoreCase ("true")){
				ebiSuccess = true;
			} else {
				ebiSuccess = false;
				message = "Absence de la ligne RECEIPT ... pour  " + submissionCode + " dans fichier "+ retourEbiRelease.getPath();
			}
			for (int i = 0; i<nbRacineNoeuds; i++) {
				if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element elt = (Element) racineNoeuds.item(i);
					//Affichage d'un elt :
					/*System.out.println("\n*************Elt************");
					System.out.println("localName="+elt.getLocalName());
					System.out.println("nodeName="+ elt.getNodeName());
					System.out.println("nodeValue="+elt.getNodeValue());
					System.out.println("nodeType="+elt.getNodeType());
					System.out.println("textContent="+elt.getTextContent());	
					*/
					if (elt.getNodeName().equals("MESSAGES")){
						if (elt.getElementsByTagName("INFO").item(0) != null){
							infos = elt.getElementsByTagName("INFO").item(0).getTextContent();
							//System.out.println("infos="+ infos);
							String pattern = "study accession \"([^\"]+)\" is set to public status";
							java.util.regex.Pattern p = Pattern.compile(pattern);
							Matcher m = p.matcher(infos);
							if ( m.find() ) { 
								studyAccession = m.group(1);
								System.out.println("studyAccession="+ studyAccession);
							}
						}
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
		if (submissionCode.equals(submission.code)){
			System.out.println("ok submissionCode = submission.code");
		}
		if (StringUtils.isNotBlank(studyAccession)){
			if(studyAccession.equals(study.accession)) {
				System.out.println("studyAccession :'"+ studyAccession + "' ==  study.accession :'" +  study.accession +"'");
				ebiSuccess = true;
				message = "Objets lies au studyAccession = " + studyAccession + " mis dans le domaine public via la soumission "+ submissionCode + "</br>"; 
			} else {
				ebiSuccess = false;
				System.out.println("studyAccession :'"+ studyAccession + "' !=  study.accession :'" +  study.accession +"' pour study.code = '"+ study.code +"'");
				message = "La soumission ."+ submission.code + " indique un study à releaser "+ submission.studyCode + " different du studyAccession indiqué dans " + retourEbiRelease.getName();
			}
		} else {
			System.out.println("Pas de recuperation du studyAccession");
			message = "La soumission ."+ submission.code + " a un retour incorrect " + retourEbiRelease.getName();
		}
		String destinataire = submission.creationUser;
		System.out.println("destinataire="+destinataire);
		// ne pas envoyer de mail à ngsrg:
		if (destinataire.equals("ngsrg")) {
			destinataire = "";
		}
		if (StringUtils.isNotBlank(destinataire)) {
			if(!destinataire.endsWith("@genoscope.cns.fr")) {
				destinataire = destinataire + "@genoscope.cns.fr";
			}
			destinataires.add(destinataire);
		}
		
		// Mise à jour dans la base de la soumission du status et de la date de release :
		ctxVal.setUpdateMode();
		Calendar calendar = Calendar.getInstance();
		Date date  = calendar.getTime();		
		Date release_date  = calendar.getTime();
		
		if (! ebiSuccess ) {
			// mettre status à jour
			State errorState = new State(errorStatus, user);
			submissionWorkflows.setState(ctxVal, submission, errorState);
			mailService.sendMail(expediteur, destinataires, subjectError, new String(message.getBytes(), "iso-8859-1"));
		} else {
			State okState = new State(okStatus, user);
			submissionWorkflows.setState(ctxVal, submission, okState);
			message = "Objets lies au studyAccession = " + studyAccession + " mis dans le domaine public via la soumission "+ submissionCode + "</br>"; 
			mailService.sendMail(expediteur, destinataires, subjectSuccess, new String(message.getBytes(), "iso-8859-1"));
			MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", submissionCode),
				DBUpdate.set("submissionDate", date).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", date));	

			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
				DBQuery.is("accession", studyAccession),
				DBUpdate.set("releaseDate", release_date).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", date));
		}
		// Recuperer l'objet soumission mis à jour pour le status :
		submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		return submission;
	}
	

}
