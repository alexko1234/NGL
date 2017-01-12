package controllers.instruments.io.cng.cbotAlone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory; 
import javax.xml.xpath.XPathConstants;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.parameter.index.Index;
import play.Logger;
import validation.ContextValidation;
import validation.utils.ValidationHelper;
import controllers.instruments.io.utils.AbstractInput;
import controllers.instruments.io.utils.InputHelper;

import models.laboratory.reagent.instance.ReagentUsed;;

public class CbotAloneInput extends AbstractInput {
	
   /* FDS 06/01/207 Description du fichier a traiter: XML généré par Cbot II:
    <?xml version="1.0" encoding="utf-16"?>
    <RunData xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    ....
    <ExperimentType>PairedEnd</ExperimentType>
    <FlowCellID>FCBARCODE</FlowCellID>
    <RunFolderName>161004_CBOT-C_0003</RunFolderName>
    <TemplateID>STRIPBARCODE</TemplateID>
    <ReagentID>XXXXXXXXXX3</ReagentID>
    ....
    <UsedOnBoardScanner>false</UsedOnBoardScanner>
	*/
	
	@Override
	public Experiment importFile(Experiment experiment,PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
			
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder builder = factory.newDocumentBuilder();
	     
	      try {
	    	 InputStream inputStream = new ByteArrayInputStream(pfv.value);
	    	 
	    	 // le fichier produit par Illumina n'est PAS en UTF-16 malgré l'entete <?xml version="1.0" encoding="utf-16"?>
	    	 // mais en UTF-8 !!! il faut donc remettre la valeur correcte
	    	 InputSource is = new InputSource(inputStream);
	    	 is.setEncoding("UTF-8");
	 		 
	         Document xml = builder.parse(is);
	         
	     	 //optional, but recommended
	     	 //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	     	 xml.getDocumentElement().normalize();

	         Element root = xml.getDocumentElement();
	         XPathFactory xpf = XPathFactory.newInstance();
	         XPath xpath = xpf.newXPath();
	         
	         // barcode de Flowcell
	         String expression="FlowCellID";
	         String flowcellId = (String)xpath.evaluate(expression, root);  
	         checkMandatoryXMLTag (contextValidation, expression, flowcellId );
 
	         // barcode de la plaque de reactifs
	         expression="ReagentID";
	         String reagentId = (String)xpath.evaluate(expression, root);
	         checkMandatoryXMLTag (contextValidation, expression, reagentId );
	       
	         // barcode du strip
	         expression="TemplateID";
	         String stripId = (String)xpath.evaluate(expression, root);
	         checkMandatoryXMLTag (contextValidation, expression, stripId);
	         
	         // expression="ExperimentType"
	         // peut contenir "rehyb" => ne peut pas etre utilisé pour fixer "Types lectures" !!!!      
	         
	         // nom du rum
	         expression="RunFolderName";
	         String runFolder = (String)xpath.evaluate(expression, root);
	         checkMandatoryXMLTag (contextValidation, expression, runFolder );
	         
	         // vérifier s'il s'agit d'un fichier produit par la cbot choisie
		     // runFolder est de la forme :  DATE_CBOT_NUM
	         if ( runFolder.length() > 1 ) {
	        	 String[] runf = runFolder.split("_");  
	        	 String cbot = null;
		      
	        	 // le nom de la cBot est le 2eme element de runFolder
	        	 // dans NGL les noms de cbot n'ont pas "-",=> le supprimer
	        	 if ( (runf.length == 3 ) && ( runf[1].charAt(4) == '-' ) ){
	        		 //System.out.println("(1) cbot:" +runf[1]);
	        		 
	        		 StringBuilder sb = new StringBuilder(runf[1]);
	        		 sb.deleteCharAt(4);
	        		 cbot = sb.toString();
	        		 //System.out.println("(2) cbot:" + cbot);
	        		 
	        		 // !! a l'etat NEW experiment.instrument.code n'est pas encore definie !!??? A TESTER
	        		 
	        		 if (experiment.instrument.typeCode.equals("cBot"))
	        		 {
	        			 // l'instrument est une cbot seule
	        			 if ( ! cbot.toUpperCase().equals(experiment.instrument.code.toUpperCase()) ) {
	        				 contextValidation.addErrors("Erreurs fichier", "Le fichier ne correspond pas a la cBot sélectionnée");
	        			 }
	        		 } else if (experiment.instrument.typeCode.equals("janus-and-cBot")) {
	        			 
	        			 //l'instrument code  est 'janus-and-cbotX' 
	        			 String[] janusAndCbot=experiment.instrument.code.split("-");
	        			 String realCbot=janusAndCbot[2];
	        			 System.out.println("realcbot:" + realCbot);
	        			 if ( ! cbot.toUpperCase().equals(realCbot.toUpperCase()) ) {
	        				 contextValidation.addErrors("Erreurs fichier", "Le fichier ne correspond pas a la cBot sélectionnée");
	        			 }
	        		 }
	        		 
	        	 } else {
	        		 contextValidation.addErrors("Erreurs fichier", "'RunFolderName' incorrect");
	        	 }   
	         }
		      
		     if (contextValidation.hasErrors()){
		    	  return experiment;
		     }
		      
		     //System.out.println("setting containerSupportCode to:"+ flowcellId);
		     experiment.instrumentProperties.put("containerSupportCode", new PropertySingleValue(flowcellId));
		      
		     //System.out.println("setting containerSupportCode to:"+ stripId);
		     experiment.instrumentProperties.put("stripCode", new PropertySingleValue(stripId));
		      
		     //System.out.println("setting Reagent ?? to:"+ reagentId);
		     ReagentUsed reagent=new ReagentUsed();
		     
		     /* 12/01  HARDCODED   
		      * kitCatalogCode     OB4B1ILET=TruSeq PE Cluster Kit V3 cBot Box 2 of 2
		      * boxCode            0B4B1Q3LW=TruSeq PE Cluster Kit V3 cBot Box 2 of 2
		      * reagentCatalogCode 0B4B1Q3N8= PE Cluster Plate V3
		      */
		    
		     reagent.kitCatalogCode="0B4B1ILET"; 
		     reagent.boxCatalogCode="0B4B1Q3LW";
		     reagent.boxCode="???";
		     reagent.reagentCatalogCode="0B4B1Q3N8";
		     reagent.code=reagentId;  
		     reagent.description="Imported from Cbot-II XML file";
		     experiment.reagents.add(reagent);
		     
	      } catch (SAXException e) {
	    	  contextValidation.addErrors("Erreurs fichier", "filchier XML incorrect (structure,encodage,...)");
	      } catch (IOException e) {
	    	  contextValidation.addErrors("Erreurs fichier", "IOException");
	      } catch (XPathExpressionException e) {
	    	  // erreur de (String)xpath.evaluate("XX", root);=> erreur du programmeur
	    	  contextValidation.addErrors("Erreurs fichier", "Probleme XPathExpressionException !!!");
	      }      
  
		  return experiment;
	}
	
	private void checkMandatoryXMLTag ( ContextValidation contextValidation, String tagName, String tagValue){
		 //if (null == tagValue )  {
         //	 contextValidation.addErrors("Erreurs fichier","Balise <"+ tagName+"> manquante");
         // } else 
        	 if ( tagValue.equals("NULL") ) {
        	 contextValidation.addErrors("Erreurs fichier","Balise <"+ tagName+"> incorrecte (NULL)");
         } else if ( tagValue.equals("") ) {
        	 contextValidation.addErrors("Erreurs fichier","Balise <"+ tagName+"> manquante ou non renseignée (vide)");
         }
	}
}
