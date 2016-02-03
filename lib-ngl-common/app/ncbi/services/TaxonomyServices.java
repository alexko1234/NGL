package ncbi.services;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import play.Logger;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

public class TaxonomyServices {

	private static String URLNCBI = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&retmote=xml";

	public static String getTaxonomyInfo(String taxonCode, String expression) throws XPathExpressionException
	{
		if(taxonCode!=null && expression!=null){
			Promise<WSResponse> homePage = WS.url(URLNCBI+"&id="+taxonCode).get();
			Promise<Document> xml = homePage.map(response -> {
				DocumentBuilderFactory dbf =
						DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(new InputSource(new StringReader(response.getBody())));
				return doc;
			});
			//By default wait 5000ms
			Document doc = xml.get(5000);
			XPath xPath =  XPathFactory.newInstance().newXPath();
			//String expression = "/TaxaSet/Taxon/ScientificName";

			//read a string value
			String value = xPath.compile(expression).evaluate(doc);
			if(StringUtils.isBlank(value))
				return null;
			else
				return value;
		}else
			return null;
	}

	public static String getScientificName(String taxonCode)
	{
		try {
			return getTaxonomyInfo(taxonCode, "/TaxaSet/Taxon/ScientificName");
		} catch (XPathExpressionException e) {
			Logger.error("Error Xpath /TaxaSet/Taxon/ScientificName "+e.getMessage());
		}
		return null;
	}

	public static String getLineage(String taxonCode)
	{
		try {
			return getTaxonomyInfo(taxonCode, "/TaxaSet/Taxon/Lineage");
		} catch (XPathExpressionException e) {
			Logger.error("Error Xpath /TaxaSet/Taxon/Lineage"+e.getMessage());
		}
		return null;
	}
}
