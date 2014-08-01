package services.reporting;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.mongojack.DBQuery;

import com.mongodb.MongoException;
import com.typesafe.config.ConfigFactory;

import fr.cea.ig.MongoDBDAO;

import mail.MailServiceException;
import mail.MailServices;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
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
			String subject = ConfigFactory.load().getString("reporting.email.subject") + " " + ConfigFactory.load().getString("institute");
		    Set<String> destinataires = new HashSet<String>();
		    destinataires.addAll(Arrays.asList(dest.split(",")));
		    
		    MailServices mailService = new MailServices();

		    //dnoisett, rem : getContentType : expected text/html found text/plain. Why ? :
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
		    String subHeader2 = "Détails : ";
		    String lineReturn = "<br>";
		    String separatorLine = "--------------------------------------------";
		    
		    int[] nb = new int[nbQueryTypes];
		    for (int i=0; i<nbQueryTypes; i++) {
		    	nb[i] = getQuery(i+1).size();
		    }
		    

		    //2. Concatenation
		    for (int i=0; i<nbQueryTypes; i++) {
		    	
		    	buffer.append(headers[i]).append(lineReturn).append(comments[i]).append(lineReturn).append(lineReturn);
		    	
		    	buffer.append(subHeader1).append(nb[i]).append(lineReturn).append(lineReturn);
		    	
		    	List<String> results = null;
		    	results = getQuery(i+1);
		    	
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
	
	
	public static List<String> getQuery(int queryId) throws MongoException {
		List<ReadSet> readSets = null;
		switch(queryId) {
			case 1:
				readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("state.code", "IP-QC"), 
						DBQuery.notExists("treatments.readQualityRaw"))).toList();
				break;
			case 2:
				readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("state.code", "IW-VQC"), 
						DBQuery.notExists("treatments.readQualityRaw"))).toList();
				break;
			case 3:
				readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("state.code", "IW-VQC"), 
						DBQuery.notExists("treatments.readQualityClean"))).toList();
				break;
			case 4:
				readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and( DBQuery.is("state.code", "IW-VQC"), 
						DBQuery.notExists("treatments.sortingRibo"), DBQuery.in("sampleOnContainer.sampleTypeCode", Arrays.asList("depletedRNA","mRNA","total-RNA","sRNA","cDNA")) )).toList();
				break;
			case 5:
				readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("state.code", "IW-VQC"), 
						DBQuery.notExists("treatments.taxonomy"))).toList();
				break;
		}
		ArrayList<String> lines = new ArrayList<String>(); 
		StringBuffer buffer;
		for (ReadSet readSet : readSets) {
			buffer = new StringBuffer();
			buffer.append("code : ").append(readSet.code);
			buffer.append(", runCode : ").append(readSet.runCode);
			buffer.append(", stateCode : ").append(readSet.state.code);
			if (queryId==4) {
				buffer.append(", sampleTypeCode : ").append(readSet.sampleOnContainer.sampleTypeCode);
			}
			lines.add(buffer.toString());
		}
		return lines;
	}
	
}
