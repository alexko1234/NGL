package controllers.instruments.io.common.novaseq;

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
//import models.laboratory.parameter.index.Index;
import models.laboratory.reagent.instance.ReagentUsed;

import play.Logger;
import validation.ContextValidation;
import validation.utils.ValidationHelper;
import controllers.instruments.io.utils.AbstractInput;
import controllers.instruments.io.utils.InputHelper;



public class NovaSeqInput extends AbstractInput {
	
	/* NGL-1769: Dépôt NovaSeq : import fichier xml
      Description du fichier a traiter: XML:
      
<?xml version="1.0"?>
<RunParameters xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <Surface>Both</Surface>
  <ReadType>PairedEnd</ReadType>
  <Side>A</Side>
  <Read1NumberOfCycles>151</Read1NumberOfCycles>
  <Read2NumberOfCycles>151</Read2NumberOfCycles>
  <IndexRead1NumberOfCycles>8</IndexRead1NumberOfCycles>
  <IndexRead2NumberOfCycles>8</IndexRead2NumberOfCycles>
  <PlannedRead1Cycles>151</PlannedRead1Cycles>
  <PlannedRead2Cycles>151</PlannedRead2Cycles>
  <PlannedIndex1ReadCycles>8</PlannedIndex1ReadCycles>
  <PlannedIndex2ReadCycles>8</PlannedIndex2ReadCycles>
  <RunNumber>7</RunNumber>
  <RtaVersion>v3.3.3</RtaVersion>
  <RecipeVersion>1.2.0</RecipeVersion>
  <ExperimentName>Test_Formation_S2</ExperimentName>
  <RfidsInfo>
    <FlowCellSerialBarcode>H5VJ2DMXX</FlowCellSerialBarcode>
    <FlowCellPartNumber>A</FlowCellPartNumber>
    <FlowCellLotNumber>20209948</FlowCellLotNumber>
    ...
    ...
    
	*/	
	
	@Override
	public Experiment importFile(Experiment experiment,PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
			
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder builder = factory.newDocumentBuilder();
	     
	     try {
	    	 InputStream inputStream = new ByteArrayInputStream(pfv.value);
	    	 InputSource is = new InputSource(inputStream);
	    	 is.setEncoding("UTF-8");
	    	 System.out.println("nom du fichier >>>" + pfv.fullname );
	 		 
	         Document xml = builder.parse(is);
	         
	     	 //optional, but recommended
	     	 //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	     	 xml.getDocumentElement().normalize();

	         Element root = xml.getDocumentElement();
	         XPathFactory xpf = XPathFactory.newInstance();
	         XPath xpath = xpf.newXPath();
	         
	         
	         /* -1- verifier identique entre fichhier XML et infos NGL

	         <FlowCellSerialBarcode>
	         <LibraryTubeSerialBarcode>
	         <Side>
	         <FlowCellMode>
	         */
	         
	         /* -2- reagents a inserer ???

	         <FlowCellLotNumber>20209948
	         <LibraryTubeSerialBarcode>NV0017846-LIB
	         <LibraryTubeLotNumber>1000001046
	         <SbsSerialBarcode>NV2061137-RGSBS
	         <SbsLotNumber>20200578
	         <ClusterSerialBarcode>NV2049937-RGCPE
	         <ClusterLotNumber>20191106
	         <BufferSerialBarcode>NV2065139-BUFFR
	         <BufferLotNumber>20207676
	         */
	         
	         String expression=null;
	         
	         // position sur sequenceur: NIVEAU 1
	         expression="Side";
	         String side = (String)xpath.evaluate(expression, root);
	         checkMandatoryXMLTag (contextValidation, expression, side);     
	         
	         // node RfidsInfo
	         expression="RfidsInfo";
	         String info = (String)xpath.evaluate(expression, root);
	         checkMandatoryXMLTag (contextValidation, expression, info);  
	         
	         // NIVEAU 2
	         // barcode de Flowcell  NIVEAU 2=><RfidsInfo>
	         expression="RfidsInfo/FlowCellSerialBarcode";
	         String flowcellId = (String)xpath.evaluate(expression, root);  
	         checkMandatoryXMLTag (contextValidation, expression, flowcellId );
 
	         // barcode du tube NIVEAU 2=><RfidsInfo>
	         expression="RfidsInfo/LibraryTubeSerialBarcode";
	         String tubeId = (String)xpath.evaluate(expression, root);
	         checkMandatoryXMLTag (contextValidation, expression, tubeId );
	       
	      
	         // flowcell mode NIVEAU 2=><RfidsInfo>
	         expression="RfidsInfo/FlowCellMode";
	         String fcMode = (String)xpath.evaluate(expression, root);
	         checkMandatoryXMLTag (contextValidation, expression, fcMode );

	         //--------------------verifications   ----------------------
	         
	         //-1- s'il existe, verifier le barcode Flowcell 
	         if ( (flowcellId.length() > 0) && (! experiment.inputContainerSupportCodes.contains(flowcellId))) {
	        		 contextValidation.addErrors("Erreurs fichier", "Le barcode flowcell du fichier '"+flowcellId+"' ne correspond pas à celui de l'expérience.");
			 }
	         
	         //-2- si elle existe, verifier la position
	         if ( (side.length() > 0) && (! experiment.instrumentProperties.get("position").value.equals(side))) {
	        		 contextValidation.addErrors("Erreurs fichier", "La position '"+side+"' du fichier ne correspond pas à celle de l'expérience.");
			 }

	         // n'est visible qu'une fois sauvegardé !!!!
	         //contextValidation.addErrors("TEST","novaseqLoadingTube="+  experiment.instrumentProperties.get("novaseqLoadingTube").value.toString() );
	         
	         //-3- s'il existe, vérifier le tubeId
	         // pas obligatoire et peut etre manquant  
	         if ( null == experiment.instrumentProperties.get("novaseqLoadingTube")) {
        		 contextValidation.addErrors("Erreurs expérience", "Veuillez renseigner 'Tube chargement (RFID)' avant d'importer le fichier.");
        		 
	         } else if ( (tubeId.length() > 0 ) && ( ! experiment.instrumentProperties.get("novaseqLoadingTube").value.equals(tubeId)) ){
				 contextValidation.addErrors("Erreurs fichier", "Le tube de chargement '"+tubeId+"' du fichier ne correspond pas à celui de l'expérience.");
	         }
	         
	         //-4- s'il existe, vérifier FlowCellMode
	         // pas obligatoire et peut etre manquant
	         if ( null == experiment.instrumentProperties.get("novaseqFlowcellMode")) {
        		 contextValidation.addErrors("Erreurs expérience", "Veuillez renseigner 'type de flowcell' avant d'importer le fichier.");
        		 
	         } else if ( (fcMode.length() > 0 ) && ( ! experiment.instrumentProperties.get("novaseqFlowcellMode").value.equals(fcMode)) ){
				 contextValidation.addErrors("Erreurs fichier", "Le type de flowcell '"+ fcMode+"' du fichier ne correspond pas à celui de l'expérience."+ experiment.instrumentProperties.get("novaseqFlowcellMode").value);
	         }
	        
		      
		     if (contextValidation.hasErrors()){
		    	  return experiment;
		     }      

		     // récupérer le nom du fichier importé..... faut-il le faire ???
		     ////experiment.instrumentProperties.put("cbotFile", new PropertySingleValue(pfv.fullname)); 
		      
	         /* code  a reutiliser ??????
	        
		     ReagentUsed reagent=new ReagentUsed();    
		     String reag[] = reagentId.split("-");
		     if ( reag.length != 2 ){
		    	 contextValidation.addErrors("Erreurs fichier","Barcode réactif '"+reagentId+ "' incorrect!!");
		     } else {
		    	 reagent.code=reagentId;  
		     
		    	 // TESTS !!!! modifier quand le catalogue sera correct !!
		    	 if  (reag[1].equals("PC6")) {
		    		 reagent.kitCatalogCode="TEST:HiSeq4000"; 
		    		 reagent.boxCatalogCode="TEST:HiSeq4000";
		    		 reagent.boxCode="";
		     
		    		 reagent.reagentCatalogCode="0B4B1Q3N8";// ????  HARDCODED reagentCatalogCode 0B4B1Q3N8= PE Cluster Plate V3

		    	 } else if (reag[1].equals("PC2")) {	
		    		 reagent.kitCatalogCode="TEST:HiSeqX"; 
		    		 reagent.boxCatalogCode="TEST:HiSeqX";
		    		 reagent.boxCode="";
		     
		    		 reagent.reagentCatalogCode="0B4B1Q3N8";// ???????  HARDCODED reagentCatalogCode 0B4B1Q3N8= PE Cluster Plate V3
		    	 
		    	 } else if (reag[1].equals("RH6")) {	
		    		 reagent.kitCatalogCode="TEST:RehybHiSeq4000"; 
		    		 reagent.boxCatalogCode="TEST:RehybHiSeq4000";
		    		 reagent.boxCode="";
		     
		    		 reagent.reagentCatalogCode="0B4B1Q3N8";// ???????  HARDCODED reagentCatalogCode 0B4B1Q3N8= PE Cluster Plate V3
		    	 
		    	 } else if (reag[1].equals("RH6")) {	
		    		 reagent.kitCatalogCode="TEST:RehybHiSeqX"; 
		    		 reagent.boxCatalogCode="TEST:RehybHiSeqX";
		    		 reagent.boxCode="";
		     
		    		 reagent.reagentCatalogCode="0B4B1Q3N8";// ???????  HARDCODED reagentCatalogCode 0B4B1Q3N8= PE Cluster Plate V3 
		    	 
		    	 } else {
		    		 // on fait quoi ???
		    		 contextValidation.addErrors("Erreurs fichier","Réactif '-"+ reag[1]+ "' non géré !!");
		    	 }
		     }
		     
		     reagent.description="Recipe version: "+ recipeVersion+"; Reagent version: " + reagentVersion;
		     experiment.reagents.add(reagent);
		     */
		     
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
