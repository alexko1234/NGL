package services.instance.run;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import models.Constants;
import models.LimsCNSDAO;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.utils.ValidationHelper;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.NGLContext;

public class UpdateReadSetCNS extends AbstractImportDataCNS{

	@Inject
	public UpdateReadSetCNS( FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("UpdateReadSetCNS", durationFromStart, durationFromNextIteration, ctx);
	}
				
	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
	RulesException {
		//updateReadSetArchive(contextError);
		
		updateLSRunProjMissingData(contextError);
		updateLSRunProjUpdateData(contextError);
	}

	private BasicDBObject getReadSetKeys() {
		BasicDBObject keys = new BasicDBObject();
		keys.put("treatments", 0);
		return keys;
	}
	
	private void updateLSRunProjMissingData(ContextValidation contextError) {
		MongoDBResult<ReadSet> results = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,  
				DBQuery.or(DBQuery.is("sampleOnContainer.properties.insertSizeGoal",null),
						DBQuery.and(DBQuery.in("sampleOnContainer.sampleCategoryCode", "RNA", "cDNA"), DBQuery.is("sampleOnContainer.properties.strandOrientation",null))),getReadSetKeys());
		
		Logger.info("Start synchro LSRunProjMissingData  : nb ReadSet ="+results.count());
		logger.info("Start synchro LSRunProjMissingData  : nb ReadSet ="+results.count());
		
		while(results.cursor.hasNext()){
			ReadSet readset = results.cursor.next();
			contextError.addKeyToRootKeyName(readset.code);
			ReadSet newReadset = limsServices.findLSRunProjData(readset);
			if(null != newReadset && null != readset.sampleOnContainer){
				updateReadSet(contextError, newReadset, readset.sampleOnContainer.sampleCategoryCode);
			}else if(null == newReadset){
				if("A".equals(readset.state.code) && !readset.typeCode.equals("rsnanopore")){
					Logger.warn("not found ReadSet on LIMS : "+readset.code);
				}
			}
			contextError.removeKeyFromRootKeyName(readset.code);
		}
	}

	private void updateReadSet(ContextValidation contextError,
			ReadSet readset, String sampleCategoryCode) {
		ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
		validateReadSet(readset, contextValidation);
		if(!contextValidation.hasErrors()){
			if("RNA".equals(sampleCategoryCode) || "cDNA".equals(sampleCategoryCode)){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class
						, DBQuery.is("code", readset.code)
						, DBUpdate.set("sampleOnContainer.properties.insertSizeGoal", readset.properties.get("insertSizeGoal"))
						.set("sampleOnContainer.properties.strandOrientation", readset.properties.get("strandOrientation"))
						.set("traceInformation.modifyDate", new Date())
						.set("traceInformation.modifyUser", Constants.NGL_DATA_USER));
						//.set("path", readset.path)
						//			.set("location", readset.location)
									
			}else{
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class
						, DBQuery.is("code", readset.code)
						, DBUpdate.set("sampleOnContainer.properties.insertSizeGoal", readset.properties.get("insertSizeGoal"))
									.set("traceInformation.modifyDate", new Date())
									.set("traceInformation.modifyUser", Constants.NGL_DATA_USER));
				//.set("path", readset.path)
				//.set("location", readset.location)
			}
		}else{
			contextError.addErrors(contextValidation.errors);
		}
	}

	private void validateReadSet(ReadSet readset, ContextValidation contextValidation) {
		
		//ValidationHelper.required(contextValidation, readset.path, "path");
		//ValidationHelper.required(contextValidation, readset.location, "location");
		ValidationHelper.required(contextValidation, readset.properties.get("insertSizeGoal"), "properties.insertSizeGoal");
		ValidationHelper.required(contextValidation, readset.properties.get("strandOrientation"), "properties.strandOrientation");
		
	}

	
	
	private void updateLSRunProjUpdateData(ContextValidation contextError) {
		List<ReadSet> readsets = limsServices.findLSRunProjData();
		Logger.info("Start synchro updateLSRunProjUpdateData  : nb ReadSet ="+readsets.size());
		logger.info("Start synchro updateLSRunProjUpdateData  : nb ReadSet ="+readsets.size());
		
		for(ReadSet readset : readsets){
			ReadSet currentRS =  MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readset.code, getReadSetKeys());
			contextError.addKeyToRootKeyName(readset.code);
			if(null != currentRS && null != currentRS.sampleOnContainer){
				updateReadSet(contextError, readset, currentRS.sampleOnContainer.sampleCategoryCode);
			}else{
				contextError.addErrors("readset", "not found in ngl");
			}
			contextError.removeKeyFromRootKeyName(readset.code);
		}
		
	}
	
	
}
