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

import org.mongojack.DBQuery;

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
import models.laboratory.reagent.instance.ReagentUsed;
import models.laboratory.reagent.description.BoxCatalog;
import models.laboratory.reagent.description.KitCatalog;
import models.laboratory.reagent.description.ReagentCatalog;

import models.utils.InstanceConstants;
import play.Logger;
import validation.ContextValidation;
import validation.utils.ValidationHelper;
import controllers.instruments.io.utils.AbstractInput;
import controllers.instruments.io.utils.InputHelper;

import fr.cea.ig.MongoDBDAO;



public class NovaSeqInput extends AbstractInput {
	
	/* NGL-1769: Dépôt NovaSeq : import fichier XML
       Description du fichier à traiter:
      
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
		  </RfidsInfo>
		    ...
		</RunParameters
	*/	
	
	@Override
	public Experiment importFile(Experiment experiment,PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
			
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder builder = factory.newDocumentBuilder();
	     
	     try {
	    	 InputStream inputStream = new ByteArrayInputStream(pfv.value);
	    	 InputSource is = new InputSource(inputStream);
	    	 is.setEncoding("UTF-8");
	    	 Logger.debug("import fichier >>>" + pfv.fullname );
	 		 
	         Document xml = builder.parse(is);
	         
	     	 //optional, but recommended
	     	 //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	     	 xml.getDocumentElement().normalize();

	         Element root = xml.getDocumentElement();
	         XPathFactory xpf = XPathFactory.newInstance();
	         XPath xpath = xpf.newXPath();
	              
	         checkConsistancy(root, xpath, experiment, contextValidation);
	         importReagents(root, xpath, experiment, contextValidation);
  
	      } catch (SAXException e) {
	    	  contextValidation.addErrors("Erreurs fichier", "filchier XML incorrect (structure,encodage,...)");
	      } catch (IOException e) {
	    	  contextValidation.addErrors("Erreurs fichier", "IOException");
	      }      
 
		  return experiment;
	}
	        
     private void checkConsistancy (Element root, XPath xpath, Experiment experiment, ContextValidation contextValidation) {
         // verifier donnees identiques entre fichier XML et infos experience 
    	 

    	 try {
		     String expression=null;
		     
		     //-1- position sur sequenceur: NIVEAU 1
		     expression="Side";
		     String side = (String)xpath.evaluate(expression, root);
		     checkMandatoryXMLTag (contextValidation, expression, side); 
		     // si la balise OK comparer avec  position
		     if ((side.length() > 0) && (! experiment.instrumentProperties.get("position").value.equals(side))) {
		    		 contextValidation.addErrors("Erreurs fichier", "La position '"+side+"' du fichier ne correspond pas à celle de l'expérience.");
			 }
		     
		     // node RfidsInfo
		     expression="RfidsInfo";
		     String info = (String)xpath.evaluate(expression, root);
		     checkMandatoryXMLTag (contextValidation, expression, info);  
		     if (contextValidation.hasErrors()){
		    	 // si le noeud RfidsInfo est manquant inutile de continuer !!!
		    	  return ;
		     }      
		     
		     //-2- barcode de Flowcell  NIVEAU 2=><RfidsInfo>
		     expression="RfidsInfo/FlowCellSerialBarcode";
		     String flowcellId = (String)xpath.evaluate(expression, root);  
		     checkMandatoryXMLTag (contextValidation, expression, flowcellId );
		     // si la balise OK comparer avec inputContainerSupportCodes
		     if ((flowcellId.length() > 0) && (! experiment.inputContainerSupportCodes.contains(flowcellId))) {
		    		 contextValidation.addErrors("Erreurs fichier", "Le barcode flowcell du fichier '"+flowcellId+"' ne correspond pas à celui de l'expérience.");
			 }
		 
			 // -3- barcode du tube de chargement  NIVEAU 2=><RfidsInfo>
		     // !!! pour l'instant il n'y a qu'un seule tube... mais ca va changer avec l'utilisation d'un support de chargement multi-lanes....
		     expression="RfidsInfo/LibraryTubeSerialBarcode";
		     String tubeId = (String)xpath.evaluate(expression, root);
		     checkMandatoryXMLTag (contextValidation, expression, tubeId );
		     // si la balise OK comparer avec novaseqLoadingTube
		     // !! pas obligatoire dans l'experience...et peut etre manquant  

		     if (null == experiment.instrumentProperties.get("novaseqLoadingTube") || null == experiment.instrumentProperties.get("novaseqLoadingTube").value) {
		    	 Logger.debug("novaseqLoadingTube= null !!");
		    	 // si on importe le fichier a l'etat terminé on ne peut plus renseigner 'Tube chargement (RFID)' !!!
				 ///contextValidation.addErrors("Erreurs expérience", "Veuillez renseigner 'Tube chargement (RFID)' avant d'importer le fichier.");
				 
		     } else if ((tubeId.length() > 0 ) && ( ! experiment.instrumentProperties.get("novaseqLoadingTube").value.equals(tubeId)) ){
				 contextValidation.addErrors("Erreurs fichier", "Le tube de chargement '"+tubeId+"' du fichier ne correspond pas à celui de l'expérience.");
		     }
		     
		     //-4- flowcell mode    NIVEAU 2=><RfidsInfo>
		     expression="RfidsInfo/FlowCellMode";
		     String fcMode = (String)xpath.evaluate(expression, root);
		     checkMandatoryXMLTag (contextValidation, expression, fcMode );
		     // si la balise OK comparer avec novaseqFlowcellMode
		     // !! pas obligatoire dans l'experience...et peut etre manquant

		     if (null == experiment.instrumentProperties.get("novaseqFlowcellMode") || null == experiment.instrumentProperties.get("novaseqFlowcellMode").value ) {
		    	 Logger.debug("novaseqFlowcellMode= null !!");
		    	 // si on importe le fichier a l'etat terminé on ne peut plus renseigner 'type de flowcell' !!!
				 ///contextValidation.addErrors("Erreurs expérience", "Veuillez renseigner 'type de flowcell' avant d'importer le fichier.");
				 
		     } else if ((fcMode.length() > 0 ) && ( ! experiment.instrumentProperties.get("novaseqFlowcellMode").value.equals(fcMode)) ){
				 contextValidation.addErrors("Erreurs fichier", "Le type de flowcell '"+ fcMode+"' du fichier ne correspond pas à celui de l'expérience.");
		     }
		     
		     //-5-  On pourrait aussi vérifier le nom de la machine ???  >> voir balise <InstrumentName> ???? TODO ?????
		     // il faut que le sequenceur soit paramétré pour que la balise contienne le nom du PC et pas l'ID du sequenceur
		     expression="InstrumentName";
		     String instrument = (String)xpath.evaluate(expression, root);
		     checkMandatoryXMLTag (contextValidation, expression, instrument);
		     if ((instrument.length() > 0) && (! experiment.instrument.code.equals(instrument))) {
	    		 contextValidation.addErrors("Erreurs fichier", "L'instrument du fichier '"+instrument+"' ne correspond pas à celui de l'expérience.");
		     }
		     
	     } catch (XPathExpressionException e) {
	   	  	// erreur de (String)xpath.evaluate("XX", root);=> erreur du programmeur
	    	 contextValidation.addErrors("Erreurs fichier", "Probleme XPathExpressionException !!!");
	     }           
     }
	      
     private void importReagents (Element root, XPath xpath,  Experiment experiment, ContextValidation contextValidation) {
         /*  barcodes reagents a inserer ??? on n'a pas le nom des reagents !!!! harcoder relation balise<-->nom reactif
		 <FlowCellSerialBarcode>1234DMXX
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
    	 
    	 Map<String,String> reagentTagMap = new HashMap<String,String>(0); 
    	 
    	 // A MODIFIER QUAND LE KIT SERA DEFINI PAR LA PROD
    	 reagentTagMap.put("FlowCell","FLOWCELL");
    	 reagentTagMap.put("LibraryTube","LIBRARY TUBE");
    	 reagentTagMap.put("Sbs","SBS");
    	 reagentTagMap.put("Cluster","CLUSTER");
    	 reagentTagMap.put("Buffer","BUFFER");
    	 String kitName="KIT NOVASEQ 6000"; 
    	 String boxName="BOX NOVASEQ 6000";
    	 
    	 //REM: l'asociation a l'expérience de dépot n'est pas vérifiée...elle est implicite
    	 KitCatalog kit= MongoDBDAO.findOne(InstanceConstants.REAGENT_CATALOG_COLL_NAME, KitCatalog.class, DBQuery.is("category","Kit").and(DBQuery.is("name",kitName ).and(DBQuery.is("active",true))));
    	 if ( null == kit ){
    		 contextValidation.addErrors("Erreurs catalogue", kitName+": pas de kit actif de ce nom dans le catalogue");
    	 }
    	 BoxCatalog box= MongoDBDAO.findOne(InstanceConstants.REAGENT_CATALOG_COLL_NAME, BoxCatalog.class, DBQuery.is("category","Box").and(DBQuery.is("name",boxName ).and(DBQuery.is("active",true))));
    	 if ( null == box ){
    		 contextValidation.addErrors("Erreurs catalogue", boxName+": pas de boîte active de ce nom dans le catalogue");
    	 }
    	 
    	 if (contextValidation.hasErrors()){
    		 return;
    	 }
    	 
    	 try {
	    	 for (Map.Entry<String, String> pair : reagentTagMap.entrySet() ) {
		         //Logger.debug(">>>>"+ pair.getKey() + "|"+ pair.getValue() ); 
		    	 ReagentUsed reagent=new ReagentUsed(); 
	    		 
	    		 String XMLtag1="RfidsInfo/"+pair.getKey()+"SerialBarcode";
	    		 //Logger.debug("XMLtag:"+XMLtag1); 
	    		 String serialBarcode = (String)xpath.evaluate(XMLtag1, root);
	    		 checkMandatoryXMLTag (contextValidation, XMLtag1, serialBarcode );
	    		 
	    		 String XMLtag2="RfidsInfo/"+pair.getKey()+"LotNumber";
	      		 //Logger.debug("XMLtag:"+XMLtag2);
	    		 String lotNumber = (String)xpath.evaluate(XMLtag2, root);
	    		 checkMandatoryXMLTag (contextValidation, XMLtag2, lotNumber );
	    		 
	    		 Logger.debug("création reagent: "+ pair.getValue());
	    		 ReagentCatalog reag= MongoDBDAO.findOne(InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class, DBQuery.is("category", "Reagent").and(DBQuery.is("name", pair.getValue())));
	    		 //Logger.debug("code="+ reag.code);
	    		 
	    		 reagent.kitCatalogCode=kit.code;       // code NGL du kit parent
	    		 reagent.boxCatalogCode=box.code;       // code NGL de la boîte parent
	    		 reagent.reagentCatalogCode=reag.code;  // code NGL du reactif
	    		 //reagent.boxCode="XXX";               // barcode de la boîte parent... info non disponible dans le fichier
	    		 reagent.code=serialBarcode+"_"+lotNumber+"_"; // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite
	    		 //reagent.description="TEST....";      // rien de pertinent a mettre ?????

				 experiment.reagents.add(reagent); 
	    	 }
	     } catch (XPathExpressionException e) {
		    	 contextValidation.addErrors("Erreurs fichier", "Probleme XPathExpressionException !!!");
		 }      
     }
	        

	private void checkMandatoryXMLTag ( ContextValidation contextValidation, String tagName, String tagValue){
        if ( tagValue.equals("NULL") ) {
        	 contextValidation.addErrors("Erreurs fichier","Balise <"+ tagName+"> incorrecte (NULL)");
         } else if ( tagValue.equals("") ) {
        	 contextValidation.addErrors("Erreurs fichier","Balise <"+ tagName+"> manquante ou non renseignée (vide)");
         }
	}
	
	
}
