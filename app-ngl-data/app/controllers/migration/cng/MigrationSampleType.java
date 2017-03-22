package controllers.migration.cng;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Constants;
import models.LimsCNGDAO;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.parameter.Parameter;
import models.laboratory.parameter.index.IlluminaIndex;
import models.laboratory.parameter.index.Index;
/// PAS CELUI LA.... import models.sra.submit.common.instance.Sample;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;

import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;


import play.api.modules.spring.Spring;




import org.springframework.jdbc.core.RowMapper;

import play.Logger;
import play.mvc.Result;
///import play.api.modules.spring.Spring;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

/**
 * correction des samples importés avec typeCode='default-sample-cng' alors qu'il est connu dans la base source Solexa...
 * @author fdsantos
 * 21/03/2017
 */

public class MigrationSampleType extends  CommonController {
	
	private static final String SAMPLE_COLL_NAME_BCK = InstanceConstants.SAMPLE_COLL_NAME + "_BCK_032117";
	protected static LimsCNGDAO limsServices= Spring.getBeanOfType(LimsCNGDAO.class);
	
	public static Result migration() {	
		JacksonDBCollection<Sample, String> samplesCollBck = MongoDBDAO.getCollection(SAMPLE_COLL_NAME_BCK, Sample.class);
		if (samplesCollBck.count() == 0) {
			// collection backup vide (inexistante ??)=> faire le backup
			//backUpSampleColl();
			
			Logger.info("Migration sample TypeCode starts");
			try {
				migrationSampleTypeCode();
			}
			catch(Exception e) {
				Logger.error(e.getMessage());
			}
									
		} else {
			Logger.info("Migration sample TypeCode  already executed !");
		}		
		Logger.info("Migration sample TypeCode  ended");
		
		// est affiché dans le naviguateur
		return ok("Finish");
	}

	private static void backUpSampleColl() {
		Logger.info("\tCopie "+InstanceConstants.SAMPLE_COLL_NAME+" starts");
		MongoDBDAO.save(SAMPLE_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList());
		Logger.info("\tCopie "+InstanceConstants.SAMPLE_COLL_NAME+" ended");
	}
	

	private static void migrationSampleTypeCode() throws DAOException{
		
		ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
		
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,DBQuery.is("typeCode","default-sample-cng")).toList();
		Logger.debug("Nb de containers to update : "+samples.size());
		
		Map<String, String> results=limsServices.findOldSampleTypes();
		Logger.debug("results="+ results);
		
		for ( Sample samp:samples){
			 
			 if (results.containsKey( samp.code )) {
				 Logger.debug("sample barcode "+ samp.code + "trouvé. son type est:"+ results.get(samp.code) +" !");
			 } else {
				 Logger.debug("sample barcode "+ samp.code + "pas trouvé...");
			 }
		}

	}
	
	
	
	/* modifier aussi et les containers qui ont ces samples en contents ?????????
	for(Container container:containers){
		
		for(Content content:container.contents){
		
		/*	content.referenceCollab=sampleRefCollab.get(content.sampleCode);
			
			if(content.sampleTypeCode.equals("default-sample-cns")){
	
   */
}