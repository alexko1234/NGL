package SraValidation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import mail.MailServiceException;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.ReadSpec;
import models.sra.submit.util.SraException;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;

import org.junit.Assert;
import org.junit.Test;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import fr.cea.ig.MongoDBDAO;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import java.util.Calendar;

import services.FileAcServices;
import services.ReleaseServices;
import services.XmlServices;
import utils.AbstractTestsSRA;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.commons.lang3.StringUtils;

import validation.ContextValidation;
import play.Logger;

public class ToolsTest2 extends AbstractTestsSRA {
		
	//@Test
	public void testfileAc()throws IOException, SraException, MailServiceException {
		File ebiFileAc = new File("/env/cns/home/sgas/test/listAC_CNS_BIL_274F26OJP.txt");
		String user = "william";
		ContextValidation ctxVal = new ContextValidation(user);

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
						final Element eltExtId = (Element) elt.getElementsByTagName("EXT_ID").item(0);
						String studyExtId = eltExtId.getAttribute("accession");
						System.out.println("accession project: " + studyExtId);

				    } else if(elt.getTagName().equalsIgnoreCase("SAMPLE")) {
				    	mapSamples.put(elt.getAttribute("alias"), elt.getAttribute("accession"));	
				    	final Element eltExtId = (Element) elt.getElementsByTagName("EXT_ID").item(0);
						String sampleExtId = eltExtId.getAttribute("accession");
						System.out.println("SAME accession sample: " + sampleExtId);
						
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
				
		ctxVal.displayErrors(Logger.of("SRA"));

	}

	
}
