package fr.cea.ig.auto.submission;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.genoscope.lis.devsi.birds.api.entity.ResourceProperties;
import fr.genoscope.lis.devsi.birds.api.exception.BirdsException;
import fr.genoscope.lis.devsi.birds.api.exception.FatalException;
import fr.genoscope.lis.devsi.birds.extension.api.exception.MailServiceException;
import fr.genoscope.lis.devsi.birds.extension.api.service.IMailService;
import fr.genoscope.lis.devsi.birds.extension.impl.factory.MailServiceFactory;
import fr.genoscope.lis.devsi.birds.impl.properties.ProjectProperties;

public class SubmissionServices implements ISubmissionServices{

	private static Logger log = Logger.getLogger(SubmissionServices.class);
	
	//TODO
	@Override
	public Set<ResourceProperties> getRawDataResources(String submissionCode)
	{
		//TODO
		//Call NGL SUB Services to get rawData resources from submission
		//Convert JSON to JobResource
		return null;
	}
	
	@Override
	public void createXMLRelease(String submissionCode, String submissionDirectory, String studyCode) throws BirdsException, IOException
	{
		File submissionFile = new File(submissionDirectory + File.separator +"submission.xml");
		
		if(studyCode==null || (studyCode!=null && studyCode.equals(""))){
			throw new BirdsException("Impossible de faire la soumission pour release " + submissionCode + " sans studyCode");

		}
		
		// ouvrir fichier en ecriture
		log.debug("Creation du fichier " + submissionFile);
		BufferedWriter output_buffer = new BufferedWriter(new FileWriter(submissionFile));
		String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
		chaine = chaine + "<SUBMISSION_SET>\n";
		
		log.debug("Ecriture du submission " + submissionCode);
		chaine = chaine + "  <SUBMISSION alias=\""+ submissionCode + "\" ";
		chaine = chaine + ">\n";	
		chaine = chaine + "    <CONTACTS>\n";
		chaine = chaine + "      <CONTACT  name=\"william\" inform_on_status=\"william@genoscope.cns.fr\" inform_on_error=\"william@genoscope.cns.fr\"/>\n";
		chaine = chaine + "    </CONTACTS>\n";
			
		chaine = chaine + "    <ACTIONS>\n";
		
		chaine = chaine + "      <ACTION>\n        <RELEASE target=\"" + studyCode + "\"/>\n      </ACTION>\n";
		
		chaine = chaine + "    </ACTIONS>\n";
		
		
		
		chaine = chaine + "  </SUBMISSION>\n";
		chaine = chaine + "</SUBMISSION_SET>\n";
		
		output_buffer.write(chaine);
		output_buffer.close();	
	}
	
	@Override
	public boolean treatmentFileRelease(String ebiFileName, String submissionCode, String accessionStudy, String studyCode, String creationUser) throws FatalException, BirdsException, UnsupportedEncodingException
	{
		if(ebiFileName==null)
			throw new FatalException("Pas de fichier retour ebi");
		File retourEbiRelease = new File(ebiFileName);
		
		if (! retourEbiRelease.exists()){
			throw new BirdsException("Fichier resultat de l'ebi pour la release absent des disques : "+ retourEbiRelease.getAbsolutePath());
		}
		
		log.debug("Parse ebi file "+ebiFileName);
		boolean ebiSuccess = false;
		String message = null;
		String infos = null;
		String studyAccession = null;
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
			
			final Document document= builder.parse(retourEbiRelease);
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
			
			log.debug("Nombre de racine noeud = "+ nbRacineNoeuds);
			
			if( racine.getAttribute("success").equalsIgnoreCase ("true")){
				ebiSuccess = true;
			} else {
				ebiSuccess = false;
				message = "Absence de la ligne RECEIPT ... pour  " + submissionCode + " dans fichier "+ retourEbiRelease.getPath();
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
								log.debug("studyAccession="+ studyAccession);
							}
						}
					}
				}			
			}  // end for  
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} 
		
		if (studyAccession!=null && !studyAccession.equals("")){
			if(studyAccession.equals(accessionStudy)) {
				log.debug("studyAccession :'"+ studyAccession + "' ==  study.accession :'" +  accessionStudy +"'");
				ebiSuccess = true;
				message = "Objets lies au studyAccession = " + studyAccession + " mis dans le domaine public via la soumission "+ submissionCode + "</br>"; 
			} else {
				ebiSuccess = false;
				log.debug("studyAccession :'"+ studyAccession + "' !=  study.accession :'" +  accessionStudy +"' pour study.code = '"+ studyCode +"'");
				message = "La soumission ."+ submissionCode + " indique un study à releaser "+ accessionStudy + " different du studyAccession indiqué dans " + retourEbiRelease.getName();
			}
		} else {
			log.debug("Pas de recuperation du studyAccession");
			message = "La soumission ."+ submissionCode + " a un retour incorrect " + retourEbiRelease.getName();
		}
		
		//Send mail
		if (! ebiSuccess ) {
			// mettre status à jour
			sendMail(creationUser, "ngl-sub : Ebi Accession Reporting error", new String(message.getBytes(), "iso-8859-1"));
		} else {
			message = "Objets lies au studyAccession = " + studyAccession + " mis dans le domaine public via la soumission "+ submissionCode + "</br>"; 
			sendMail(creationUser, "ngl-sub : Ebi Accession Reporting success", new String(message.getBytes(), "iso-8859-1"));
		}
		return ebiSuccess;
	}
	
	public void sendMail(String creationUser, String subject, String message) throws FatalException, MailServiceException, UnsupportedEncodingException
	{
		IMailService mailService = MailServiceFactory.getInstance();
		
		String from = ProjectProperties.getProperty(ProjectProperties.ADMIN_EMAIL);
		String[] emailTo = ProjectProperties.getProperty(ProjectProperties.ADDRESSES_EMAIL).split(",");
		Set<String> to = new HashSet<String>(Arrays.asList(emailTo));
		
		if(!creationUser.equals("ngsrg") && !creationUser.equals("")){
			to.add(creationUser);
		}
		
		mailService.sendMail(from, to, subject, new String(message.getBytes(), "iso-8859-1"), null);
		
	}

	
}
