package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;


public class ReportingCNSDAO {
	
	public static int getCountOfQuery(int queryId) throws MongoException {
		int nb = 0;
		switch(queryId) {
			case 1:
				nb = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("state.code", "IP-QC"), 
						DBQuery.notExists("treatments.readQualityRaw"))).count();
				break;
			case 2:
				nb = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("state.code", "IW-VQC"), 
						DBQuery.notExists("treatments.readQualityRaw"))).count();
				break;
			case 3:
				nb = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("state.code", "IW-VQC"), 
						DBQuery.notExists("treatments.readQualityClean"))).count();
				break;
			case 4:
				nb = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("state.code", "IW-VQC"), 
						DBQuery.notExists("treatments.sortingRibo"), DBQuery.in("sampleOnContainer.sampleTypeCode", Arrays.asList("depletedRNA","mRNA","total-RNA","sRNA","cDNA")) )).count();
				break;
			case 5:
				nb = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("state.code", "IW-VQC"), 
						DBQuery.notExists("treatments.taxonomy"))).count();
				break;
		}
		return nb;
	}
	
	public static List<String> getResultsOfQuery(int queryId) throws MongoException {
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
