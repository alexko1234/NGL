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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import models.sra.submit.util.SraParameter;
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

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
public class ParametersTest extends AbstractTestsSRA {
		

	//@Test
	public void genere_sraParameter_libProcessTypeCodeValue_orientation() {
		List <String> libProcessTypeCodeValues_1 = new ArrayList<String>();
		libProcessTypeCodeValues_1.add("A");
		libProcessTypeCodeValues_1.add("C");
		List <String> libProcessTypeCodeValues_2 = new ArrayList<String>();
		libProcessTypeCodeValues_2.add("F");
		libProcessTypeCodeValues_2.add("H");
		libProcessTypeCodeValues_2.add("K");
		libProcessTypeCodeValues_2.add("L");
		libProcessTypeCodeValues_2.add("U");
		libProcessTypeCodeValues_2.add("W");
		libProcessTypeCodeValues_2.add("Z");
		libProcessTypeCodeValues_2.add("DB");
		libProcessTypeCodeValues_2.add("DC");
		libProcessTypeCodeValues_2.add("DD");
		libProcessTypeCodeValues_2.add("DE");
		libProcessTypeCodeValues_2.add("RA");
		libProcessTypeCodeValues_2.add("RB");
		libProcessTypeCodeValues_2.add("TA");
		libProcessTypeCodeValues_2.add("TB");
			

		for (String libProcessTypeCodeValue : libProcessTypeCodeValues_1){
			SraParameter param = new SraParameter();
			param.type = "libProcessTypeCodeValue_orientation";
			param.code=libProcessTypeCodeValue;
			param.value = "reverse-forward";
			MongoDBDAO.save(InstanceConstants.SRA_PARAMETER_COLL_NAME, param);
		}

		for (String libProcessTypeCodeValue : libProcessTypeCodeValues_2){
			SraParameter param = new SraParameter();
			param.type = "libProcessTypeCodeValue_orientation";
			param.code=libProcessTypeCodeValue;
			param.value = "forward-reverse";
			MongoDBDAO.save(InstanceConstants.SRA_PARAMETER_COLL_NAME, param);
		}	
	}
	
	
	//@Test
	public void test_map_libProcessTypeCodeValue_orientation() {
		SraParameter sraParam = new SraParameter();
		Map<String, String> map = sraParam.getParameter("libProcessTypeCodeValue_orientation");

		for (Iterator<Entry<String, String>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
		  Entry<String, String> entry = iterator.next();
		  
		}

		if (map.get("Toco")!= null){
			System.out.println("ok pour TA voici sa valeur "+ map.get("TA"));
		}
		
	}
		
}
