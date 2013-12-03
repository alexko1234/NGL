package services.instance.run;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.run.instance.LaneValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class RunImportCNS extends AbstractImportDataCNS{

	public RunImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("RunCNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {

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
				updateRuns.add(run);
			}else {
				contextError.errors.putAll(contextValidation.errors);
			}			
		}		

		//Update Run if lane and readSet are created
		limsServices.updateRunLims(updateRuns,contextError);


	}


	public void createFileFromReadSet(ReadSet readSet,ContextValidation ctxVal){
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

	public void createReadSetFromRun(Run run,ContextValidation contextValidation){
		String rootKeyName="readSet["+run.code+"]";
		contextValidation.addKeyToRootKeyName(rootKeyName);

		List<ReadSet> readSetsOld=MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("runCode", run.code)).toList();
		//Si readSets du run existe alors supprime les 
		if(readSetsOld!=null){
			for(ReadSet read:readSetsOld){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, read);
			}
		}

		List<ReadSet> readSets=limsServices.findReadSetToCreateFromRun(run,contextValidation);

		if(!contextValidation.hasErrors() && readSets.size()!=0){
			for(ReadSet readSet:readSets){

				InstanceHelpers.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet, contextValidation,true);

				if(!contextValidation.hasErrors()){

					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class
							,DBQuery.is("code", run.code).and(DBQuery.is("lanes.number",readSet.laneNumber))
							,DBUpdate.push("lanes.$.readSetCodes", readSet.code));

					createFileFromReadSet(readSet,contextValidation);
				}

			}
		}
		contextValidation.removeKeyFromRootKeyName(rootKeyName);

	}

}
