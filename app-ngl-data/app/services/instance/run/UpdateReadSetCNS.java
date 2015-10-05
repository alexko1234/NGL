package services.instance.run;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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

public class UpdateReadSetCNS extends AbstractImportDataCNS{

	public UpdateReadSetCNS( FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateReadSetCNS", durationFromStart, durationFromNextIteration);
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
				DBQuery.or(DBQuery.is("location",null), DBQuery.is("sampleOnContainer.properties.insertSizeGoal",null),
						DBQuery.and(DBQuery.is("sampleOnContainer.sampleCategoryCode", "RNA"), DBQuery.is("sampleOnContainer.properties.strandOrientation",null))),getReadSetKeys());
		
		Logger.info("Start synchro LSRunProjMissingData  : nb ReadSet ="+results.count());
		logger.info("Start synchro LSRunProjMissingData  : nb ReadSet ="+results.count());
		
		while(results.cursor.hasNext()){
			ReadSet readset = results.cursor.next();
			contextError.addKeyToRootKeyName(readset.code);
			ReadSet newReadset = limsServices.findLSRunProjData(readset);
			if(null != newReadset && null != readset.sampleOnContainer){
				updateReadSet(contextError, newReadset, readset.sampleOnContainer.sampleCategoryCode);
			}else if(null == newReadset){
				if("A".equals(readset.state.code)){
					contextError.addErrors("readset", "not found in db lims");
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
			if("RNA".equals(sampleCategoryCode)){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class
						, DBQuery.is("code", readset.code)
						, DBUpdate.set("path", readset.path)
									.set("location", readset.location)
									.set("sampleOnContainer.properties.insertSizeGoal", readset.properties.get("insertSizeGoal"))
									.set("sampleOnContainer.properties.strandOrientation", readset.properties.get("strandOrientation"))
									.set("traceInformation.modifyDate", new Date())
									.set("traceInformation.modifyUser", Constants.NGL_DATA_USER));
			}else{
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class
						, DBQuery.is("code", readset.code)
						, DBUpdate.set("path", readset.path)
									.set("location", readset.location)
									.set("sampleOnContainer.properties.insertSizeGoal", readset.properties.get("insertSizeGoal"))
									.set("traceInformation.modifyDate", new Date())
									.set("traceInformation.modifyUser", Constants.NGL_DATA_USER));
			}
		}else{
			contextError.addErrors(contextValidation.errors);
		}
	}

	private void validateReadSet(ReadSet readset, ContextValidation contextValidation) {
		
		ValidationHelper.required(contextValidation, readset.path, "path");
		ValidationHelper.required(contextValidation, readset.location, "path");
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
	
	
	
	
	
	
	
	
	public void updateReadSetArchive(ContextValidation contextError) {
		List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,  
				DBQuery.and(DBQuery.is("dispatch", true), DBQuery.is("archiveId", null), DBQuery.notEquals("state.code", "UA"))).toList();
		Logger.info("Start synchro archive  : nb ReadSet ="+readSets.size());
		logger.info("nb ReadSet ="+readSets.size());
		for(ReadSet rs : readSets){
			ReadSet updateRS;
			try {
				updateRS = limsServices.findReadSetToUpdate(rs, contextError);
				logger.info("Update ReadSet ="+rs.code);
				if(updateRS.archiveDate != null && updateRS.archiveId != null){
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class
							, DBQuery.is("code", rs.code)
							, DBUpdate.set("archiveDate",updateRS.archiveDate)
										.set("archiveId", updateRS.archiveId)
										.set("traceInformation.modifyDate", new Date())
										.set("traceInformation.modifyUser", "lims"));					
				}else if(updateRS.archiveDate == null && updateRS.archiveId != null){
					contextError.addErrors("archiveDate", "Probleme archivage date null / id not null : "+rs.code);
					logger.error("Probleme archivage date null / id not null : "+rs.code);
				}else if(updateRS.archiveDate != null && updateRS.archiveId == null){
					contextError.addErrors("archiveId", "Probleme archivage date not null / id null : "+rs.code);
					logger.error("Probleme archivage date not null / id null : "+rs.code);
				}
			} catch (SQLException e) {
				contextError.addErrors("database", e.getMessage());
				logger.error(e.getMessage());
			}
		}
	}
	

	public static void updateReadSet(Run run,ContextValidation contextError) {
		List<ReadSet> readSets=null;
		try {
			readSets =limsServices.findReadSetToCreateFromRun(run, contextError);
		
		for(ReadSet newReadSet:readSets){
			
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class, newReadSet.code)) {
				updateReadSet(newReadSet, contextError);
				updateFiles(newReadSet,contextError);		
			}else {
				contextError.addErrors( "code", "error.codeNotExist",newReadSet.code);
			}
			
			//limsServices.updateReadSetLims(newReadSet, true, contextError);

		}
		
		} catch (SQLException e) {
			Logger.error("Erreur sql");
		}		

	}



	private static void updateReadSet(ReadSet newReadSet,
			ContextValidation contextError) {
		
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class
				, DBQuery.is("code", newReadSet.code)
				, DBUpdate.set("productionValuation", newReadSet.productionValuation)
							.set("bioinformaticValuation", newReadSet.bioinformaticValuation)
							.set("archiveDate",newReadSet.archiveDate)
							.set("archiveId", newReadSet.archiveId)
							.set("state",newReadSet.state)
							.set("traceInformation.modifyDate", new Date())
							.set("traceInformation.modifyUser", "lims"));
		
	}

	public static void updateFiles(ReadSet readSet,ContextValidation ctxVal) {

		List<File> files;
		try {
			files = limsServices.findFileToCreateFromReadSet(readSet,ctxVal);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
		/*String rootKeyName=null;
		ctxVal.putObject("readSet", readSet);
		ctxVal.setDeleteMode();
		for(File file:files){
			rootKeyName="file["+file.fullname+"]";
			ctxVal.addKeyToRootKeyName(rootKeyName);
			file.validate(ctxVal);
			ctxVal.removeKeyFromRootKeyName(rootKeyName);

		}*/
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.is("code", readSet.code),
					DBUpdate.unset("files"));
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.is("code", readSet.code),
					DBUpdate.pushAll("files", files));  
	}

}
