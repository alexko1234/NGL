package workflows.sra.submission;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import models.sra.submit.common.instance.Submission;
import models.sra.submit.util.SraException;
import validation.ContextValidation;

public class SubmissionWorkflowsHelper {

	public File checkFileEbi(String fileName, ContextValidation validation)
	{
		if(fileName!=null){
			File ebiFile = new File(fileName);
			if(ebiFile.exists())
				return ebiFile;
			else
				validation.addErrors("file","error.file.not.found",(String)validation.getObject("fileEbi"));
		}else{
			validation.addErrors("file","error.file.null",(String)validation.getObject("fileEbi"));
		}
		return null;
	}
	
	public void checkRelease(Submission submission, ContextValidation validation)
	{
		if (!submission.release){
			validation.addErrors("code", "error.validationsub.release.false", submission.code);
		}
	}
	
	public String parseEbiFileRelease(Submission submission, File ebiFile, ContextValidation validation)
	{
		String studyAccession = null;
		Boolean ebiSuccess = false;
		String infos = null;
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
			
			final Document document= builder.parse(ebiFile);
			//Affiche du prologue
			/*System.out.println("*************PROLOGUE************");
			System.out.println("version : " + document.getXmlVersion());
			System.out.println("encodage : " + document.getXmlEncoding());      
			System.out.println("standalone : " + document.getXmlStandalone());
            */
			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();
			//Affichage de l'élément racine
			/*System.out.println("\n*************RACINE************");
			System.out.println(racine.getNodeName());
			System.out.println("success = " + racine.getAttribute("success"));
			*/
			final NodeList racineNoeuds = racine.getChildNodes();
			final int nbRacineNoeuds = racineNoeuds.getLength();
			
			System.out.println("Nombre de racine noeud = "+ nbRacineNoeuds);
			
			if( racine.getAttribute("success").equalsIgnoreCase ("true")){
				ebiSuccess = true;
			} else {
				ebiSuccess = false;
				validation.addErrors("ebi","error.parse.ebi.release.receipt",submission.code, ebiFile.getAbsolutePath());
			}
			for (int i = 0; i<nbRacineNoeuds; i++) {
				if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element elt = (Element) racineNoeuds.item(i);
					//Affichage d'un elt :
					/*System.out.println("\n*************Elt************");
					System.out.println("localName="+elt.getLocalName());
					System.out.println("nodeName="+ elt.getNodeName());
					System.out.println("nodeValue="+elt.getNodeValue());
					System.out.println("nodeType="+elt.getNodeType());
					System.out.println("textContent="+elt.getTextContent());	
					*/
					if (elt.getNodeName().equals("MESSAGES")){
						if (elt.getElementsByTagName("INFO").item(0) != null){
							infos = elt.getElementsByTagName("INFO").item(0).getTextContent();
							//System.out.println("infos="+ infos);
							String pattern = "study accession \"([^\"]+)\" is set to public status";
							java.util.regex.Pattern p = Pattern.compile(pattern);
							Matcher m = p.matcher(infos);
							if ( m.find() ) { 
								studyAccession = m.group(1);
								System.out.println("studyAccession="+ studyAccession);
							}
						}
					}
				}			
			}  // end for  
		} catch (final ParserConfigurationException e) {
			validation.addErrors("ebi","error.parse.ebi.release.exception",e.getMessage());
		} catch (final SAXException e) {
			validation.addErrors("ebi","error.parse.ebi.release.exception",e.getMessage());
		} catch (final IOException e) {
			validation.addErrors("ebi","error.parse.ebi.release.exception",e.getMessage());
		} 
		return studyAccession;
	}
}
