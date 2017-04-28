package services.ncbi;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import play.Logger;

public class NCBITaxon {
	private Document doc;
	public  String code;
	public Boolean error; 
	public Boolean exists; 
	
	public NCBITaxon(){
		
	}
	
	public NCBITaxon(String code, Document doc) {
		this.code = code;
		this.doc = doc;
		this.error = getIsError();
		this.exists = getIsTaxon();
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
	
	
	private Boolean getIsTaxon() {
		try {
			String value = getValue("/TaxaSet/Taxon");
			if(StringUtils.isNotBlank(value)){
				return Boolean.TRUE;
			}
		} catch (XPathExpressionException e) {
			Logger.error("Error Xpath /TaxaSet/Taxon "+e.getMessage());
		}
		return Boolean.FALSE;
		
	}

	private Boolean getIsError() {
		try {
			String value = getValue("/eFetchResult/ERROR");
			if(StringUtils.isNotBlank(value)){
				return Boolean.TRUE;
			}
		} catch (XPathExpressionException e) {
			Logger.error("Error Xpath /eFetchResult/ERROR "+e.getMessage());
		}
		return Boolean.FALSE;
		
	}

	public String getScientificName(){
		return getResult("/TaxaSet/Taxon/ScientificName");
		
	}

	public String getLineage(){
		return getResult("/TaxaSet/Taxon/Lineage");
		
	}


	private String getResult(String xpath)  {
		try {
			if (!this.error && this.exists) {
				return getValue(xpath);
			}else if(!this.error && !this.exists){
				return "Taxon code " + this.code + " is not exists";
			}else if(this.error){
				return "Taxon code " + this.code + " is on error";
			}else{
				return null;
			}
		} catch (XPathExpressionException e) {
			Logger.error("Error Xpath"+xpath+" "+e.getMessage());
		}
		return null;
	}
}
