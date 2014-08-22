package controllers.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.valuation.instance.ValuationCriteria;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import play.mvc.Result;

/**
 * Replace old codes of ValuationCriteria
 * @author dnoisett
 * 21-08-2014
 */


public class ValuationCriteriaCodes  extends CommonController {

	private static final String VALUATION_CRITERIA_COLL_NAME_BCK = InstanceConstants.VALUATION_CRITERIA_COLL_NAME+"_BCK_20140821";
	private static final String RUN_ILLUMINA_COLL_NAME_BCK = InstanceConstants.RUN_ILLUMINA_COLL_NAME+"_BCK_20140821";
	private static final String READSET_ILLUMINA_COLL_NAME_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK_20140821";
	private static final String ANALYSIS_COLL_NAME_BCK = InstanceConstants.ANALYSIS_COLL_NAME+"_BCK_20140821";

	//main
	public static Result migration() {
		
		HashMap<String, String> mappingVCCodes = defineMapping();
		
		//Migrate Ref Collection
		List<ValuationCriteria> vcCollBck = MongoDBDAO.find(VALUATION_CRITERIA_COLL_NAME_BCK, ValuationCriteria.class).toList();
		if(vcCollBck.size() == 0){
			
			Logger.info(">>>>>>>>>>> 1/4 Migration ValuationCriteria starts");

			backupVCCollection();
			migrateVCCodes(mappingVCCodes);
			
			Logger.info(">>>>>>>>>>> Migration ValuationCriteria end");
		} else {
			Logger.info(">>>>>>>>>>> Migration ValuationCriteria already execute !");
		}
		
		
		//Migrate Runs
		List<Run> runsCollBck = MongoDBDAO.find(RUN_ILLUMINA_COLL_NAME_BCK, Run.class).toList();
		if(runsCollBck.size() == 0){
			
			Logger.info(">>>>>>>>>>> 2/4 Migration Run starts");

			backupRunCollection();
			migrateRunVCCodes(mappingVCCodes);
			
			Logger.info(">>>>>>>>>>> Migration Run end");
		} else {
			Logger.info(">>>>>>>>>>> Migration Run already execute !");
		}
		
		
		//Migrate Analysis...
		List<Analysis> analysisCollBck = MongoDBDAO.find(ANALYSIS_COLL_NAME_BCK, Analysis.class).toList();
		if(analysisCollBck.size() == 0){
			
			Logger.info(">>>>>>>>>>> 4/4 Migration Analysis starts");

			backupAnalysisCollection();
			migrateAnalysisVCCodes(mappingVCCodes);
			
			Logger.info(">>>>>>>>>>> Migration Analysis end");
		} else {
			Logger.info(">>>>>>>>>>> Migration Analysis already execute !");
		}
		
		
		//Migrate ReadSets...
		List<ReadSet> readSetsCollBck = MongoDBDAO.find(READSET_ILLUMINA_COLL_NAME_BCK, ReadSet.class).toList();
		if(readSetsCollBck.size() == 0){
			
			Logger.info(">>>>>>>>>>> 3/4 Migration ReadSet starts");
			
			ArrayList<String> lStringPattern = new ArrayList<String>();
			lStringPattern.add("^A[A-D].*");
			lStringPattern.add("^A[E-H].*");
			lStringPattern.add("^A[I-L].*");
			lStringPattern.add("^A[M-P].*");
			lStringPattern.add("^A[Q-T].*");
			lStringPattern.add("^A[U-Z].*");
			lStringPattern.add("^B[A-B].*");
			lStringPattern.add("^B[C-D].*");
			lStringPattern.add("^B[E-Z].*");
			lStringPattern.add("^[C-Z].*");

			backupReadSetCollection(lStringPattern);
			migrateReadSetVCCodes(mappingVCCodes, lStringPattern);
			
			Logger.info(">>>>>>>>>>> Migration ReadSet end");
		} else {
			Logger.info(">>>>>>>>>>> Migration ReadSet already execute !");
		}
				
		return ok(">>>>>>>>>>> Migrations finish");
	}


	
	private static void migrateVCCodes(HashMap<String, String> mappingVCCodes) {
		List<ValuationCriteria> vcs = MongoDBDAO.find(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, ValuationCriteria.class).toList();	
		Logger.info("Expected to migrate "+vcs.size()+" VALUATION CRITERIA");		
		
		for (ValuationCriteria vc : vcs) {
			MongoDBDAO.update(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, ValuationCriteria.class, DBQuery.is("code", vc.code),   
					DBUpdate.set("code", mappingVCCodes.get(vc.code)));			
		}
	}
	
	
	private static void migrateRunVCCodes(HashMap<String, String> mappingVCCodes) {
		List<Run> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.exists("valuation.criteriaCode")).toList();
		Logger.info("Expected to migrate "+runs.size()+" RUNS");		
		
		for (Run run : runs) {
			if (run.valuation.criteriaCode != null) {
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", run.code),   
						DBUpdate.set("valuation.criteriaCode", mappingVCCodes.get(run.valuation.criteriaCode)));
			}
		}
	}

	
	private static void migrateReadSetVCCodes(HashMap<String, String> mappingVCCodes, ArrayList<String> lStringPattern) {

		for (String stringPattern : lStringPattern) {
			List<ReadSet> readsets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.or(DBQuery.exists("bioinformaticValuation.criteriaCode"),DBQuery.exists("productionValuation.criteriaCode")), 
								DBQuery.regex("code",Pattern.compile(stringPattern)))).toList();
			
			Logger.info("Expected to migrate "+readsets.size()+" READSETS");		
			
			for (ReadSet readset : readsets) {
				if (readset.bioinformaticValuation.criteriaCode != null) {
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", readset.code),   
							DBUpdate.set("bioinformaticValuation.criteriaCode", mappingVCCodes.get(readset.bioinformaticValuation.criteriaCode)));
				}
				if (readset.productionValuation.criteriaCode != null) {
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", readset.code),   
							DBUpdate.set("productionValuation.criteriaCode", mappingVCCodes.get(readset.productionValuation.criteriaCode)));
				}
			}
		}
	}

	
	private static void migrateAnalysisVCCodes(HashMap<String, String> mappingVCCodes) {
		List<Analysis> analysis = MongoDBDAO.find(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, DBQuery.exists("valuation.criteriaCode")).toList();		
		Logger.info("Expected to migrate "+analysis.size()+" ANALYSIS");		
		
		for (Analysis analyse : analysis) {
			if (analyse.valuation != null && analyse.valuation.criteriaCode != null) {
				MongoDBDAO.update(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, DBQuery.is("code", analyse.code),   
						DBUpdate.set("valuation.criteriaCode", mappingVCCodes.get(analyse.valuation.criteriaCode)));
			}
		}
	}
	
	
	
	
	private static HashMap<String, String> defineMapping() {	
		HashMap<String, String> mappingVCCodes = new HashMap<String, String>();
		
		mappingVCCodes.put("VC-20140604170100", "VC-RMISEQ-PE-101-v1");
		mappingVCCodes.put("VC-20140604170101","VC-RMISEQ-PE-151-v1");
		mappingVCCodes.put("VC-20140604170200","VC-RMISEQ-PE-251-v1");
		mappingVCCodes.put("VC-20140604170201","VC-RMISEQ-PE-301-v1");
		mappingVCCodes.put("VC-20140604165527","VC-RHS2500R-PE-101-v1");
		mappingVCCodes.put("VC-20140521183026","VC-RHS2500R-PE-151-v1");
		mappingVCCodes.put("VC-20140604164026","VC-RHS2500-PE-101-v1");
		mappingVCCodes.put("VC-20140604170000","VC-RHS2000-PE-101-v1");
		mappingVCCodes.put("VC-20140428152037","VC-ReadsetBlePE-v1");
		mappingVCCodes.put("VC-20140428152226","VC-ReadsetBleMP-v1");
		mappingVCCodes.put("VC-20140709175326","VC-AnalysisBPA-v1");
		mappingVCCodes.put("VC-20140709172226","VC-RHS2000-PE-101-v1");

		return mappingVCCodes;
	}
	
	
	private static void backupVCCollection() {
		Logger.info("\tCopie "+InstanceConstants.VALUATION_CRITERIA_COLL_NAME+" start");
		MongoDBDAO.save(VALUATION_CRITERIA_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, ValuationCriteria.class).toList());
		Logger.info("\tCopie "+InstanceConstants.VALUATION_CRITERIA_COLL_NAME+" end");
	}
	
	
	private static void backupRunCollection() {
		Logger.info("\tCopie "+InstanceConstants.RUN_ILLUMINA_COLL_NAME+" start");
		MongoDBDAO.save(RUN_ILLUMINA_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class).toList());
		Logger.info("\tCopie "+InstanceConstants.RUN_ILLUMINA_COLL_NAME+" end");
	}
	
	
	private static void backupReadSetCollection(ArrayList<String> lStringPattern) {
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" start");
		
		int i = 1;
		
		for (String stringPattern : lStringPattern) {			
			MongoDBDAO.save(READSET_ILLUMINA_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.regex("code", Pattern.compile(stringPattern))).toList());
	
			Logger.debug("step" + i +"/" + lStringPattern.size());
			i++;
		}
				
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" end");
	}
	
	
	private static void backupAnalysisCollection() {
		Logger.info("\tCopie "+InstanceConstants.ANALYSIS_COLL_NAME+" start");
		MongoDBDAO.save(ANALYSIS_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class).toList());
		Logger.info("\tCopie "+InstanceConstants.ANALYSIS_COLL_NAME+" end");
	}
	
}