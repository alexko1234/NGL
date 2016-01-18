package SraValidation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import utils.AbstractTestsSRA;

public class ToolsTest extends AbstractTestsSRA {
		
	@Test
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
		Path lien = Paths.get(fileLien.getPath());
		Path cible = Paths.get(fileCible.getPath());
		Files.createSymbolicLink(lien, cible);
		Assert.assertTrue(fileLien.exists());
		
	}
	@Test
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

	@Test
	public void getNcbiScientificName() throws XPathExpressionException, IOException  {	
		Integer taxonId = new Integer(1735743);
		String scientificName = getNcbiScientificName(taxonId);
		System.out.println("getNcbiScientificName::Scientific name = '"+ scientificName + "'");
	}	
	
	
	
}
