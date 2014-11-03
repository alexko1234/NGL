package services.instance.run;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Constants;
import models.laboratory.common.description.Level;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.SampleOnContainer;
import models.laboratory.run.instance.Treatment;
import models.laboratory.sample.instance.Sample;
import models.util.Workflows;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import play.Logger;
import play.Play;
import rules.services.RulesException;
import rules.services.RulesServices;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.run.instance.LaneValidationHelper;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;

public class RunImportCNS extends AbstractImportDataCNS{

	public RunImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("RunCNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		createRuns("pl_RunToNGL",contextError);

	}


	public static void createRuns(String sql,ContextValidation contextError) throws SQLException, DAOException{
		Logger.debug("Create Run From Lims CNS");
		List<Run> runs=limsServices.findRunsToCreate(sql, contextError);
		//Create Lane
		List<Run> newRuns=new ArrayList<Run>();
		String rootKeyName=null;

		for(Run run:runs){
			if(run!=null) {
				rootKeyName="run["+run.code+"]";
				ContextValidation ctx=new ContextValidation(Constants.NGL_DATA_USER);
				ctx.addKeyToRootKeyName(rootKeyName);

				//Save Run du Lims si n'existe pas ou n'est pas transféré dans NGL
				Run newRun=MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class, run.code);
				if(newRun==null){
					Logger.debug("Save Run "+run.code + " mode "+contextError.getMode());
					newRun=(Run) InstanceHelpers.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run, ctx, true);
				} else {

					Logger.debug("Update Run "+run.code + " mode "+contextError.getMode());	
					ctx.setCreationMode();
					ctx.putObject("level", Level.CODE.Run);
					ctx.putObject("run", run);
					run.treatments.get("ngsrg").validate(ctx);

					if(!ctx.hasErrors()){

						MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
								DBQuery.is("code", run.code),
								DBUpdate.set("treatments.ngsrg",run.treatments.get("ngsrg"))
								.set("state",run.state)
								.set("dispatch",run.dispatch)
								.set("traceInformation.modifyUser","lims")
								.set("traceInformation.modifyDate",new Date()));
					}
				}

				//Si Run non null creation des lanes ou traitement ngsrg
				if(newRun!=null && !ctx.hasErrors()){
					Run runLanes=createLaneFromRun(newRun, ctx);
					if(runLanes!=null && !ctx.hasErrors()){
						newRuns.add(runLanes);
					}
				}

				if(ctx.hasErrors()){
					contextError.errors.putAll(ctx.errors);
				}

				ctx.removeKeyFromRootKeyName(rootKeyName);

			}
		}

		List<Run> updateRuns=new ArrayList<Run>();

		for(Run run:newRuns){
			ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
			Logger.debug("Create ReadSet from Run "+run.code);
			createReadSetFromRun(run, contextValidation);

			Run newRun=MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, run.code);
			if(!contextValidation.hasErrors()){
				List<Object> list=new ArrayList<Object>();
				list.add(newRun);
				try{
					Logger.debug("Run Rules from Run "+run.code);
					new RulesServices().callRules(Play.application().configuration().getString("rules.key"),"rg_1",list);
				}catch (Exception e) {
					contextValidation.addErrors("rules", e.toString()+ "runCode :"+run.code, run.code);
				}
				Workflows.nextRunState(contextValidation, newRun);
			}

			if(!contextValidation.hasErrors()){
				updateRuns.add(run);
			}else {
				contextError.errors.putAll(contextValidation.errors);
			}			

		}		

		//Update Run if lane and readSet are created
		limsServices.updateRunLims(updateRuns,contextError);
	}


	public static Run createLaneFromRun(Run newRun,ContextValidation contextError) throws SQLException{
		Logger.debug("Create Lanes from Run "+newRun.code);
		List<Lane> lanes=limsServices.findLanesToCreateFromRun(newRun, contextError);
		//Save TreatmentLane
		ContextValidation contextErrorValidation = new ContextValidation(Constants.NGL_DATA_USER);
		contextErrorValidation.addKeyToRootKeyName(contextError.getRootKeyName());
		contextErrorValidation.putObject("run",newRun);

		//Pas de lanes dans le run alors creation
		if(newRun.lanes==null){

			contextErrorValidation.setCreationMode();
			LaneValidationHelper.validationLanes(lanes, contextErrorValidation);


			if(!contextErrorValidation.hasErrors()){
				for(Lane lane : lanes){

					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
							DBQuery.is("code", newRun.code),
							DBUpdate.push("lanes", lane));

					Logger.debug("Save new Lanes "+lane.number+"from Run "+newRun.code);

				}
			}



			//creation traitements ngsrg
		}else {
			for(Lane lane:lanes){

				for(String treatmentKey:lane.treatments.keySet()){
					if(treatmentKey.equals("ngsrg")){
						Treatment treatment=lane.treatments.get("ngsrg");
						contextErrorValidation.setCreationMode();
						contextErrorValidation.putObject("level", Level.CODE.Lane);
						contextErrorValidation.putObject("lane", lane);
						treatment.validate(contextErrorValidation);
						if(!contextErrorValidation.hasErrors()){
							MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
									DBQuery.and(DBQuery.is("code", newRun.code), DBQuery.is("lanes.number", lane.number)),
									DBUpdate.set("lanes.$.treatments."+treatment.code, treatment));
							Logger.debug("Save new treatment ngsrg lanes "+lane.number+"from run "+newRun.code);
						}
					}
				}
			}
		}

		if(contextErrorValidation.hasErrors()){
			contextError.errors.putAll(contextErrorValidation.errors);
			return null;
		}else {
			return newRun;
		}
	}

	public static void createFileFromReadSet(ReadSet readSet,ContextValidation ctxVal) throws SQLException{
		List<File> files = limsServices.findFileToCreateFromReadSet(readSet,ctxVal);
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

	public static List<ReadSet> createReadSetFromRun(Run run,ContextValidation contextValidation)throws SQLException, DAOException {

		List<ReadSet> newReadSets=new ArrayList<ReadSet>();

		//Delete old readSet from run
		if(MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", run.code))){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", run.code));
		}

		List<ReadSet> readSets=limsServices.findReadSetToCreateFromRun(run,contextValidation);

		if(!contextValidation.hasErrors() && readSets.size()!=0){
			for(ReadSet readSet:readSets){
				String rootKeyName="readSet["+readSet.code+"]";
				contextValidation.addKeyToRootKeyName(rootKeyName);

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

					SampleOnContainer sampleOnContainer = InstanceHelpers.getSampleOnContainer(readSet);
					if(null != sampleOnContainer){
						MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
								DBQuery.is("code", readSet.code), DBUpdate.set("sampleOnContainer", sampleOnContainer));
					}else{
						contextValidation.addErrors( "sampleOneContainer", "error.codeNotExist");
					}

					if(!contextValidation.hasErrors()){
						newReadSets.add(readSet);
					}
				}


				contextValidation.removeKeyFromRootKeyName(rootKeyName);

			}
		}
		return newReadSets;
	}

}
