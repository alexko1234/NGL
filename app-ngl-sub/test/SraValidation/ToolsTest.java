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

import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.util.SraException;
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

import services.XmlServices;
import utils.AbstractTestsSRA;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.commons.lang3.StringUtils;

public class ToolsTest extends AbstractTestsSRA {
	
	public  List<Sample> xmlToSample(File xmlFile) {
		List<Sample> listSamples = new ArrayList<Sample>();

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
			
			final Document document= builder.parse(xmlFile);
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
		return listSamples;
	}		
	
	//@Test
	public void testfileAc()throws IOException {
		File xmlFile = new File("/env/cns/home/sgas/test/listAc_CNS_BCU_BLK_266H23OI3.txt");
		List<Sample> listSamples = new ArrayList<Sample>();
		listSamples = xmlToSample(xmlFile);
	}

	@Test
	public void testRelease()throws IOException {
		Submission submission = MongoDBDAO.findOne(InstanceConstants.SRA_SUBMISSION_COLL_NAME,
				Submission.class, DBQuery.and(DBQuery.is("code", "CNS_ARH_27B9362NT")));
		XmlServices xmlServices = new XmlServices();
		try {
			xmlServices.writeAllXml(submission.code);
		} catch (SraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//@Test
	public void testDates()throws IOException {
		Calendar calendar = Calendar.getInstance();
		Date date  = calendar.getTime();
		int year = calendar.get(Calendar.YEAR);
		System.out.println("annee = " + year);
		System.out.println("date = " + date);

		System.out.println("getDate =" + new Date());
		calendar.add(Calendar.YEAR, 2);
		Date release_date  = calendar.getTime();
		int year_release = calendar.get(Calendar.YEAR);
		System.out.println("annee_release = " + year_release);
		System.out.println("date_release = " + release_date);
		String user = "william";
		
		MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
				DBQuery.is("code", "STUDY_BCU_25TG2F0LD"),
				DBUpdate.set("accession", "toto").set("firstSubmissionDate", date).set("releaseDate", release_date).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", date));
		
		Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "STUDY_BCU_25TG2F0LD");	
		
		System.out.println("apres requete");
		
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "code", "STUDY_BCU_25TG2F0LD")){
			System.out.println("le study avec  : code=" +study.code + " existe bien dans base");
		}
		System.out.println("dans study : code=" +study.code);
		System.out.println("dans study : accession=" +study.accession);

		System.out.println("dans study : firstSubmissionDate=" +study.firstSubmissionDate);
		System.out.println("dans study : release_date=" +study.releaseDate);

		/* deprecated :
		Date release = calendar.get
		//Date date = new Date();
		System.out.println("date = " + date);
		int annee_release = date.getYear() + 2;
		date.setYear(annee_release);
		System.out.println("date release = " + date);
		*/
	}
	//@Test
	public void testRegExp()throws IOException  {
		String name = "titi/toto/tutu/lili";
		
		String relatifName = "";
		relatifName = name;
		if (name.contains("/")){
			relatifName = name.substring(name.lastIndexOf("/")+1);
		}
		System.out.println("name :" + name +" et relatifName="+ relatifName);
	}
	
	//@Test
	public void SymbolicLinkSuccess() throws IOException  {
		String nameDirectory = "/env/cns/submit_traces/SRA/NGL_test/tests_liens/linkTest4";
		
		File dir = new File(nameDirectory);
		if (!dir.exists()) {
			if(!dir.mkdirs()){
				System.out.println("impossible de creer repertoire nameDirectory");
			} 
			
		}
		File fileCible = new File(nameDirectory + File.separator + "cible");
		if (fileCible.exists()) {
			fileCible.delete();
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileCible));
			writer.write("maCible");
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		File fileLien = new File(nameDirectory + File.separator + "lien_3");
		if (fileLien.exists()){
			fileLien.delete();
		}
		try {
		Path lien = Paths.get(fileLien.getPath());
		Path cible = Paths.get(fileCible.getPath());
		Files.createSymbolicLink(lien, cible);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("ok verifier lien :" + fileLien.getPath());

		Assert.assertTrue(fileLien.exists());
		
	}
	//@Test
	public void testhttp() throws IOException, XPathExpressionException  {
		Promise<WSResponse> homePage = WS.url("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&id=1735743&retmote=xml").get();
		Promise<Document> xml = homePage.map(response -> {
			System.out.println("response "+response.getBody());
			//Document d = XML.fromString(response.getBody());
			//Node n = scala.xml.XML.loadString(response.getBody());
			//System.out.println("J'ai une reponse ?"+ n.toString());
			DocumentBuilderFactory dbf =
		            DocumentBuilderFactory.newInstance();
			//dbf.setValidating(false);
			//dbf.setSchema(null);
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(response.getBody())));
			return doc;
		});
		
		
		System.out.println("J'ai une reponse xml ?"+xml.get(1000) );
		Document doc = xml.get(1000);
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String expression = "/TaxaSet/Taxon/ScientificName";

		//read a string value
		String scientifiqueName = xPath.compile(expression).evaluate(doc);
		System.out.println("Scientifique name "+scientifiqueName);
		


	   
		//Promise<WSResponse> result = WS.url("http://example.com").post("content");	
	
	}
	public String getNcbiScientificName(Integer taxonId) throws IOException, XPathExpressionException  {
		Promise<WSResponse> homePage = WS.url("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&id="+taxonId+"&retmote=xml").get();
		Promise<Document> xml = homePage.map(response -> {
			System.out.println("response "+response.getBody());
			//Document d = XML.fromString(response.getBody());
			//Node n = scala.xml.XML.loadString(response.getBody());
			//System.out.println("J'ai une reponse ?"+ n.toString());
			DocumentBuilderFactory dbf =
		            DocumentBuilderFactory.newInstance();
			//dbf.setValidating(false);
			//dbf.setSchema(null);
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(response.getBody())));
			return doc;
		});
		
		Document doc = xml.get(1000);
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String expression = "/TaxaSet/Taxon/ScientificName";

		//read a string value
		String scientificName = xPath.compile(expression).evaluate(doc);
		return scientificName;	
	}

	//@Test
	public void getNcbiScientificName() throws XPathExpressionException, IOException  {	
		Integer taxonId = new Integer(1735743);
		String scientificName = getNcbiScientificName(taxonId);
		System.out.println("getNcbiScientificName::Scientific name = '"+ scientificName + "'");
	}	
	
	
	
}
