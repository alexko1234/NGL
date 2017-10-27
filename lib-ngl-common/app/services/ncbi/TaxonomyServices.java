package services.ncbi;

import static fr.cea.ig.play.IGGlobals.ws;
import static fr.cea.ig.play.IGGlobals.cache;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import models.utils.dao.DAOException;
// import play.Logger;
// import play.cache.Cache;
// import play.libs.ws.WS;
import play.libs.ws.WSResponse;
// import scala.concurrent.Future;
import validation.ContextValidation;

// import play.libs.F.Promise;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

// TODO: cleanup
public class TaxonomyServices {

	private static final play.Logger.ALogger logger = play.Logger.of(TaxonomyServices.class);
	
	private static String URLNCBI = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&retmote=xml";

	// public static Promise<NCBITaxon> getNCBITaxon(String taxonCode) {
	public static CompletionStage<NCBITaxon> getNCBITaxon(String taxonCode) {
		if (taxonCode != null) {
			logger.debug("Get taxo info for code : "+taxonCode);
			NCBITaxon taxon = getObjectInCache(taxonCode);
			if (null == taxon) {
				// Promise<WSResponse> homePage = WS.url(URLNCBI+"&id="+taxonCode).get();
				// CompletionStage<WSResponse> homePage = WS.url(URLNCBI+"&id="+taxonCode).get();
				CompletionStage<WSResponse> homePage = ws().url(URLNCBI+"&id="+taxonCode).get();
				// Promise<NCBITaxon> xml = homePage.map(response -> {
				CompletionStage<NCBITaxon> xml = homePage.thenApplyAsync(response -> {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					try {
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document doc = db.parse(new InputSource(new StringReader(response.getBody())));
						NCBITaxon newTaxon = new NCBITaxon(taxonCode, doc);
						setObjectInCache(newTaxon, taxonCode);
						return newTaxon;
					} catch (ParserConfigurationException e) {
						throw new RuntimeException("wrapped",e);
					} catch (IOException e) {
						throw new RuntimeException("wrapped",e);
					} catch (SAXException e) {
						throw new RuntimeException("wrapped",e);
					}
				});
				return xml;
			} else {
				logger.debug("found taxon in cache "+taxonCode);
				// return Promise.pure(taxon);
				return CompletableFuture.completedFuture(taxon);
			}
			
		}
		// return Promise.pure(new NCBITaxon());
		return CompletableFuture.completedFuture(new NCBITaxon());
	}
	
	@SuppressWarnings("unchecked")
	private static NCBITaxon getObjectInCache(String code){
		if (null != code) {
			try {
				String key = NCBITaxon.class.toString()+"."+code;
				// return (NCBITaxon) Cache.get(key);
				return (NCBITaxon) cache().get(key);
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}		
	}
	
	private static void setObjectInCache(NCBITaxon o, String code) {
		if (null != o && null != code) {
			// Cache.set(NCBITaxon.class.toString()+"."+code, o, 60 * 60 * 24);
			cache().set(NCBITaxon.class.toString()+"."+code, o, 60 * 60 * 24);
		}		
	}
	
	@Deprecated
	public static String getTaxonomyInfo(String taxonCode, String expression) throws XPathExpressionException {
		if (taxonCode!=null && expression!=null) {
			logger.debug("Get taxo info for "+expression+" for taxon "+taxonCode);
			/*Promise<WSResponse> homePage = WS.url(URLNCBI+"&id="+taxonCode).get();
			Promise<Document> xml = homePage.map(response -> {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(new InputSource(new StringReader(response.getBody())));
				return doc;
			});*/
			// CompletionStage<WSResponse> homePage = WS.url(URLNCBI+"&id="+taxonCode).get();
			CompletionStage<WSResponse> homePage = ws().url(URLNCBI+"&id="+taxonCode).get();
			CompletionStage<Document> xml = homePage.thenApplyAsync(response -> {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				try {
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(new InputSource(new StringReader(response.getBody())));
					return doc;
				} catch (ParserConfigurationException e) {
					throw new RuntimeException("wrapped",e);
				} catch (IOException e) {
					throw new RuntimeException("wrapped",e);
				} catch (SAXException e) {
					throw new RuntimeException("wrapped",e);
				}
			});
			
			String value = null;
			int nbTry=0;
			while(nbTry<3 && value==null){
				try {
					value = getValue(xml, expression);
					logger.debug("Value "+value);
				} catch (TimeoutException e) {
					//Retry connect
					logger.debug("Retry connect NCBI");
					nbTry++;
				}
			}
			if(nbTry==3)
				logger.error("NCBI Timeout for taxonId "+taxonCode);
			
			if(StringUtils.isBlank(value))
				return null;
			else
				return value;
		} else
			return null;
	}
	
	@Deprecated
	// public static String getValue(Promise<Document> xml, String expression) throws XPathExpressionException, RuntimeException, TimeoutException	{
	public static String getValue(CompletionStage<Document> xml, String expression) throws XPathExpressionException, RuntimeException, TimeoutException	{
		// TODO: possibly fix time unit original is get(10000), assumed to be milliseconds.
		try {
		Document doc = xml.toCompletableFuture().get(10000,TimeUnit.MILLISECONDS);
		XPath xPath =  XPathFactory.newInstance().newXPath();
		//String expression = "/TaxaSet/Taxon/ScientificName";
		//read a string value
		return xPath.compile(expression).evaluate(doc);
		} catch (ExecutionException e) {
			throw new RuntimeException("wrapped exception",e);
		} catch (InterruptedException e) {
			throw new RuntimeException("wrapped exception",e);
		}
	}
	
	@Deprecated
	public static String getScientificName(String taxonCode) {
		try {
			return getTaxonomyInfo(taxonCode, "/TaxaSet/Taxon/ScientificName");
		} catch (XPathExpressionException e) {
			logger.error("Error Xpath /TaxaSet/Taxon/ScientificName "+e.getMessage());
		}
		return null;
	}
	
	@Deprecated
	public static String getLineage(String taxonCode) {
		try {
			return getTaxonomyInfo(taxonCode, "/TaxaSet/Taxon/Lineage");
		} catch (XPathExpressionException e) {
			logger.error("Error Xpath /TaxaSet/Taxon/Lineage"+e.getMessage());
		}
		return null;
	}
	
}
