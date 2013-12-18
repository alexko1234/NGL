package services.instance.run;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import rules.services.RulesException;
import rules.services.RulesServices;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.run.instance.LaneValidationHelper;

import com.mongodb.MongoException;
import com.typesafe.config.ConfigFactory;

import fr.cea.ig.MongoDBDAO;

public class RunImportCNS extends AbstractImportDataCNS{

	public RunImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("RunCNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {

		//Create Run
		List<Run> runs=limsServices.findRunsToCreate("pl_RunToNGL", contextError);

		//Create Lane
		List<Run> newRuns=new ArrayList<Run>();
		String rootKeyName=null;

		for(Run run:runs){
			if(run!=null) {
				rootKeyName="run["+run.code+"]";
				contextError.addKeyToRootKeyName(rootKeyName);

				//Save Run
				Run newRun=MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class, run.code);
				if(newRun==null){
					newRun=(Run) InstanceHelpers.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run, contextError, true);
				}

				//Si Run non null
				if(newRun!=null){
					//Si lanes n'existe pas
					if(newRun.lanes==null){
						List<Lane> lanes=limsServices.findLanesToCreateFromRun(run, contextError);

						//Save TreatmentLane
						ContextValidation contextErrorValidation = new ContextValidation();
						contextErrorValidation.addKeyToRootKeyName(contextError.getRootKeyName());
						contextErrorValidation.putObject("run",newRun);
						contextErrorValidation.setCreationMode();

						LaneValidationHelper.validationLanes(lanes, contextErrorValidation);

						if(contextErrorValidation.hasErrors()){
							contextError.errors.putAll(contextErrorValidation.errors);
						}else {
							for(Lane lane : lanes){
								MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
										DBQuery.is("code", newRun.code),
										DBUpdate.push("lanes", lane));
							}
							//Creation readSet
							newRuns.add(newRun);
						}

					}else {
						//Lane existent => creation readSet 
						newRuns.add(newRun);
					}
				}
				contextError.removeKeyFromRootKeyName(rootKeyName);
			}
		}

		List<Run> updateRuns=new ArrayList<Run>();

		for(Run run:newRuns){
			ContextValidation contextValidation=new ContextValidation();

			createReadSetFromRun(run, contextValidation);

			if(!contextValidation.hasErrors()){
				List<Object> list=new ArrayList<Object>();
				list.add(run);
				try{
					new RulesServices().callRules(ConfigFactory.load().getString("rules.key"),"rg_1",list);
				}catch (Exception e) {
					contextValidation.addErrors("rules", e.toString(), run.code);
				}
			}
			
			if(!contextValidation.hasErrors()){
				updateRuns.add(run);
			}else {
				contextError.errors.putAll(contextValidation.errors);
			}			
			
		}		

		//CallRules rg_1
		new RulesServices().callRules(ConfigFactory.load().getString("rules.key"),"rg_1",new ArrayList<Object>(updateRuns));
		
		//Update Run if lane and readSet are created
		limsServices.updateRunLims(updateRuns,contextError);


	}


	public void createFileFromReadSet(ReadSet readSet,ContextValidation ctxVal) throws SQLException{
		List<File> files = limsServices.findFileToCreateFromReadSet(readSet,contextError);
		String rootKeyName=null;
		for(File file:files){
			rootKeyName="file["+file.fullname+"]";

			ctxVal.addKeyToRootKeyName(rootKeyName);
			ctxVal.putObject("readSet", readSet);
			ctxVal.setCreationMode();
			file.validate(ctxVal);

			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
						DBQuery.is("code", readSet.code),
						DBUpdate.push("files", file)); 
			} 
			ctxVal.removeKeyFromRootKeyName(rootKeyName);

		}
	}

	public void createReadSetFromRun(Run run,ContextValidation contextValidation)throws SQLException, DAOException {
		String rootKeyName="readSet["+run.code+"]";
		contextValidation.addKeyToRootKeyName(rootKeyName);

		//Delete old readSet from run
		if(MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", run.code))){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", run.code));
		}
		
		List<ReadSet> readSets=limsServices.findReadSetToCreateFromRun(run,contextValidation);

		if(!contextValidation.hasErrors() && readSets.size()!=0){
			for(ReadSet readSet:readSets){

				if (!MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,readSet.sampleCode)){
					
					Sample sample =limsServices.findSampleToCreate(contextValidation, readSet.sampleCode);
					if(sample!=null){
						InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME,sample,contextValidation,true);
					}
				}
				
				InstanceHelpers.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet, contextValidation,true);

				if(!contextValidation.hasErrors()){

					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class
							,DBQuery.is("code", run.code).and(DBQuery.is("lanes.number",readSet.laneNumber))
							,DBUpdate.addToSet("lanes.$.readSetCodes", readSet.code));
					
					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class
							,DBQuery.is("code", run.code)
							,DBUpdate.addToSet("projectCodes", readSet.projectCode).addToSet("sampleCodes", readSet.sampleCode));
					
					createFileFromReadSet(readSet,contextValidation);
				}

			}
		}
		contextValidation.removeKeyFromRootKeyName(rootKeyName);

	}

}
