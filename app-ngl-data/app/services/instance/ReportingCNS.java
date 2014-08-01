package services.instance;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import com.typesafe.config.ConfigFactory;

import mail.MailServiceException;
import mail.MailServices;
import models.ReportingCNSDAO;
import scala.concurrent.duration.FiniteDuration;


public class ReportingCNS extends AbstractReporting {

	public ReportingCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ReportingCNS", durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runReporting() throws UnsupportedEncodingException, MessagingException {
		try {
			
			//get parameters for email
			String expediteur = ConfigFactory.load().getString("reporting.email.from"); 
			String dest = ConfigFactory.load().getString("reporting.email.to");   
			String subject = ConfigFactory.load().getString("reporting.email.subject") + " " + ConfigFactory.load().getString("reporting.institute");
		    Set<String> destinataires = new HashSet<String>();
		    destinataires.addAll(Arrays.asList(dest.split(",")));
		    
		    MailServices mailService = new MailServices();

		    //getContentType : expected text/html found text/plain. Why ? :
		    //How set contentType ?
		    /*
			Properties properties = System.getProperties();
			properties.put("mail.smtp.host", "smtp.genoscope.cns.fr");
			Session session = Session.getInstance(properties, null);
		    Message msg = new MimeMessage(session);
		    System.out.println("msg.getContentType()=" + msg.getContentType()); 
		    //msg.getContentType() = text/plain ! 
		    */ 
		    
		    //Generate stringBuffer
		    //1. Define constants;
		    int nbQueryTypes = 5;
		    StringBuffer buffer = new StringBuffer();
		    String[] headers = {"1) QC en cours bloqué", 
		    					"2) Readsets à évaluer : Read Quality (RAW) manquant", 
		    					"3) Readsets à évaluer : Read Quality (CLEAN) manquant",
		    					"4) Readsets à évaluer : SortingRibo manquant",
		    					"5) Readsets à évaluer : Taxonomy manquant"};
		    String[] comments = {"Liste des readsets à l\'état IP-QC, pour lesquels le traitement readQualityRaw n\'existe pas.",
		    		"Liste des readsets à l\'état IW-VQC, pour lesquels le traitement readQualityRaw n\'existe pas.",
		    		"Liste des readsets à l\'état IW-VQC, pour lesquels le traitement readQualityClean n\'existe pas.",
		    		"Liste des readsets à l\'état IW-VQC, pour lesquels le traitement sortingRibo n\'existe pas et dont le sampleType est dans la liste : depletedRNA ;  mRNA ; total-RNA ; sRNA ; cDNA",
		    		"Liste des readsets à l\'état IW-VQC, pour lesquels le traitement taxonomy n\'existe pas."};
		    String subHeader1 = "Nombre de résultats : ";
		    String subHeader2 = "Détails (Code readset, Code run, Etat readset) : ";
		    String lineReturn = "<br>";
		    String separatorLine = "--------------------------------------------";
		    
		    int[] nb = new int[nbQueryTypes];
		    for (int i=0; i<nbQueryTypes; i++) {
		    	nb[i] = ReportingCNSDAO.getCountOfQuery(i+1);
		    }
		    

		    //2. Concatenation
		    for (int i=0; i<nbQueryTypes; i++) {
		    	
		    	buffer.append(headers[i]).append(lineReturn).append(comments[i]).append(lineReturn).append(lineReturn);
		    	
		    	buffer.append(subHeader1).append(nb[i]).append(lineReturn).append(lineReturn);
		    	
		    	List<String> results = null;
		    	results = ReportingCNSDAO.getResultsOfQuery(i+1);
		    	
		    	if ((nb[i] > 0) && (results != null)) {
		    		buffer.append(subHeader2).append(lineReturn);
		    		
			    	for (String result : results) {	
			    		buffer.append(result).append(lineReturn);
			    	}			    	
			    	buffer.append(lineReturn);
		    	}
		    	
		    	buffer.append(separatorLine).append(lineReturn);
		    }
		    
		    //Send mail using parameters and string buffer
		    mailService.sendMail(expediteur, destinataires, subject, new String(buffer.toString().getBytes(), "iso-8859-1"));
		    
		} catch (MailServiceException e) {
			e.printStackTrace();
		}
	}
	
}
