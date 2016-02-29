package controllers.migration;		

import java.text.SimpleDateFormat;
import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import ncbi.services.TaxonomyServices;
import play.Logger;
import play.mvc.Result;

/**
 * Update SampleOnContainer on ReadSet
 * @author galbini
 *
 */
public class MigrationUpdateNCBITaxonSample extends CommonController {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");

	public static Result migration(String code, Boolean onlyNull){

		Logger.info("Migration sample start");
		//backupSample(code);
		List<Sample> samples = null;
		if(!"all".equals(code)){
			samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",code)).toList();						
		}else if(onlyNull.booleanValue()){
			samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.or(DBQuery.notExists("ncbiScientificName"),DBQuery.notExists("ncbiLineage"))).toList();						
		}else {
			samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.exists("code")).toList();
		}
		Logger.debug("migre "+samples.size()+" samples");
		int size = samples.size();
		int nb = 1;
		for(Sample sample : samples){
			Logger.debug("Sample "+nb+"/"+size);
			migreSample(sample);
			nb++;
		}
		Logger.info("Migration sample finish");
		return ok("Migration Finish");

	}



	private static void migreSample(Sample sample) {
		String ncbiScientificName=TaxonomyServices.getScientificName(sample.taxonCode);
		String ncbiLineage=TaxonomyServices.getLineage(sample.taxonCode);
		MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,  Sample.class, 
				DBQuery.is("code", sample.code), DBUpdate.set("ncbiScientificName", ncbiScientificName).set("ncbiLineage", ncbiLineage));
		if(ncbiScientificName==null)
			Logger.error("no scientific name "+ncbiScientificName);
	}

	private static void backupSample(String code) {
		String backupName = InstanceConstants.SAMPLE_COLL_NAME+"_BCK_NCBI_"+sdf.format(new java.util.Date());
		Logger.info("\tCopie "+InstanceConstants.SAMPLE_COLL_NAME+" to "+backupName+" start");
		List<Sample> samples = null;
		if(!"all".equals(code)){
			samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",code)).toList();						
		}else{
			samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.exists("code")).toList();						
		}

		MongoDBDAO.save(backupName, samples);
		Logger.info("\tCopie "+InstanceConstants.SAMPLE_COLL_NAME+" to "+backupName+" end");

	}



}
