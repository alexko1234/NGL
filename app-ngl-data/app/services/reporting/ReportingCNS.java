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
		    
		    //Define constants;
		    Integer nbQueries = 5;
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
		    String separatorLine = "--------------------------------------------";
		    String lineReturn = "<br>";
		    
		    //Get data
		    Integer[] nb = new Integer[nbQueries.intValue()];  
		    ArrayList<ArrayList<String>> listResults = new ArrayList<ArrayList<String>>();
		    ArrayList<String> results = new ArrayList<String>();
		    String[] subHeaders2 = new String[nbQueries.intValue()];
		    for (int i=0; i<nbQueries.intValue(); i++) {
		    	nb[i] = getQueryResults(i+1).size();
		    	if (nb[i] > 0)  
		    		subHeaders2[i] = subHeader2 + getColumnHeaders(i+1);
		    	else
		    		subHeaders2[i] = "";
		    	results = getQueryResults(i+1);
		    	listResults.add(results);
		    }
		    
		    String content = reportingCNS.render(nbQueries, headers, comments, subHeader1, subHeaders2, nb, listResults, lineReturn, separatorLine).body();
		    		    
		    //Send mail using global parameters and content
		    mailService.sendMail(expediteur, destinataires, subject, content);
		    
		} catch (MailServiceException e) {
			e.printStackTrace();
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
