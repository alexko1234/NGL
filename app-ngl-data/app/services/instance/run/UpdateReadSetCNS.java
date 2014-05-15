package services.instance.run;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import models.LimsCNSDAO;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.Logger;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;

public class UpdateReadSetCNS extends AbstractImportDataCNS{

	public UpdateReadSetCNS( FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateReadSetCNS", durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException,
	RulesException {
		updateReadSetArchive(contextError);
	}

	
	public void updateReadSetArchive(ContextValidation contextError) {
		List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,  
				DBQuery.and(DBQuery.is("dispatch", true), DBQuery.is("archiveId", null))).toList();
		
		logger.info("nb ReadSet ="+readSets.size());
		for(ReadSet rs : readSets){
			ReadSet updateRS;
			try {
				updateRS = limsServices.findReadSetToUpdate(rs, contextError);
				logger.info("Update ReadSet ="+rs.getCode());
				if(updateRS.archiveDate != null && updateRS.archiveId != null){
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class
							, DBQuery.is("code", rs.code)
							, DBUpdate.set("archiveDate",updateRS.archiveDate)
										.set("archiveId", updateRS.archiveId)
										.set("traceInformation.modifyDate", new Date())
										.set("traceInformation.modifyUser", "lims"));					
				}else if(updateRS.archiveDate == null && updateRS.archiveId != null){
					logger.error("Probleme archivage date null / id not null : "+rs.getCode());
				}else if(updateRS.archiveDate != null && updateRS.archiveId == null){
					logger.error("Probleme archivage date not null / id null : "+rs.getCode());
				}
			} catch (SQLException e) {
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
