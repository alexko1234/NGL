package services.ncbi;

import java.util.concurrent.TimeoutException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import play.Logger;
import play.libs.F.Promise;

public class NCBITaxon {
	private Document doc;
	public  String code;
	
	public NCBITaxon(String code, Document doc) {
		this.code = code;
		this.doc = doc;
	}
	
	public NCBITaxon() {		
	}

	private String getValue(String expression) throws XPathExpressionException 
	{
		XPath xPath =  XPathFactory.newInstance().newXPath();
		//String expression = "/TaxaSet/Taxon/ScientificName";
		//read a string value
		if(null != doc){
			String value = xPath.compile(expression).evaluate(doc);
			if(StringUtils.isNotBlank(value)){
				return value;
			}
		}
		return null;				
	}
	
	public String getScientificName()
	{
		try {
			return getValue("/TaxaSet/Taxon/ScientificName");
		} catch (XPathExpressionException e) {
			Logger.error("Error Xpath /TaxaSet/Taxon/ScientificName "+e.getMessage());
		}
		return null;
	}

	public String getLineage()
	{
		try {
			return getValue("/TaxaSet/Taxon/Lineage");
		} catch (XPathExpressionException e) {
			Logger.error("Error Xpath /TaxaSet/Taxon/Lineage"+e.getMessage());
		}
		return null;
	}
}
