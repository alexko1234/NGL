package services.reporting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

import org.mongojack.DBQuery;
import com.mongodb.MongoException;
import fr.cea.ig.MongoDBDAO;

import com.typesafe.config.ConfigFactory;

import mail.MailServiceException;
import mail.MailServices;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import play.Logger;
import scala.concurrent.duration.FiniteDuration;

import services.reporting.txt.*;


public class ReportingCNS extends AbstractReporting {

	public ReportingCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ReportingCNS", durationFromStart, durationFromNextIteration);
	}
	
	
	@Override
	public void runReporting() throws UnsupportedEncodingException, MessagingException {
		
		try {
			
			//Get global parameters for email
			String expediteur = ConfigFactory.load().getString("reporting.email.from"); 
			String dest = ConfigFactory.load().getString("reporting.email.to");   
			String subject = ConfigFactory.load().getString("reporting.email.subject") + " " + ConfigFactory.load().getString("institute") + " " + ConfigFactory.load().getString("ngl.env");
		    Set<String> destinataires = new HashSet<String>();
		    destinataires.addAll(Arrays.asList(dest.split(",")));
		    
		    MailServices mailService = new MailServices();
		    
		    //Get data 
		    int nbQueries = 5;
		    Integer[] nbResults = new Integer[nbQueries];  
		    ArrayList<ArrayList<String>> listResults = new ArrayList<ArrayList<String>>();
		    ArrayList<String> results = new ArrayList<String>();
		    String[] subHeaders2 = new String[nbQueries];
		    for (int i=0; i<nbQueries; i++) {
		    	nbResults[i] = getQueryResults(i+1).size();
		    	if (nbResults[i] > 0)  
		    		subHeaders2[i] = getColumnHeaders(i+1);
		    	else
		    		subHeaders2[i] = "";
		    	results = getQueryResults(i+1);
		    	listResults.add(results);
		    }
		    
		    String content = reportingCNS.render(subHeaders2, nbResults, listResults).body();
		    		    
		    //Send mail using global parameters and content
		    mailService.sendMail(expediteur, destinataires, subject, new String(content.getBytes(), "iso-8859-1"));
		    
		} catch (MailServiceException e) {
			Logger.error("MailService error: "+e.getMessage(),e);
		}
		
	}
	
	public static String getColumnHeaders(int queryId) {
		if (queryId == 4) 
			return "code,runCode,stateCode,sampleTypeCode";
		else
			return "code,runCode,stateCode";
	}

	
	public static ArrayList<String> getQueryResults(int queryId) throws MongoException {
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
			buffer.append(readSet.code).append(",").append(readSet.runCode).append(",").append(readSet.state.code);
			if (queryId==4) {
				buffer.append(",").append(readSet.sampleOnContainer.sampleTypeCode);
			}
			lines.add(buffer.toString());
		}
		return lines;
	}
	
}
