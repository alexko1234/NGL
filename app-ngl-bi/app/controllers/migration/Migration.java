package controllers.migration;		


import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TransientState;
import models.laboratory.common.instance.Valuation;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;

import play.Logger;
import play.Play;
import play.libs.Akka;
import play.mvc.Result;
import rules.services.RulesActor;
import rules.services.RulesMessage;
import akka.actor.ActorRef;
import akka.actor.Props;
import controllers.CommonController;
import controllers.migration.models.FileOld;
import controllers.migration.models.LaneOld;
import controllers.migration.models.ReadSetOld;
import controllers.migration.models.RunOld;
import fr.cea.ig.MongoDBDAO;

public class Migration extends CommonController {
	
	private static final String RUN_ILLUMINA_BCK = InstanceConstants.RUN_ILLUMINA_COLL_NAME+"_BCK";
	private static final String READSET_ILLUMINA_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK";
	
	private static final String RUN_ILLUMINA_BEFORE_RECALCUL = InstanceConstants.RUN_ILLUMINA_COLL_NAME+"_BEFORE_RECALCUL";
	
	private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor.class));
	private static final String ruleStatRG="F_RG_1";
	
	
	public static Result migration(){
		
		Logger.info("Start point of Migration");
		
		JacksonDBCollection<RunOld, String> runsCollBck = MongoDBDAO.getCollection(RUN_ILLUMINA_BCK, RunOld.class);
		if(runsCollBck.count() == 0){
			Logger.info("Migration run start");
			backupRun();
			List<RunOld> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, RunOld.class).toList();
			Logger.debug("migre "+runs.size()+" runs");
			for(RunOld run : runs){
				migreRun(run);
			}
			Logger.info("Migration run end");
		
		}else{
			Logger.info("Migration run already execute !");
		}
		
		
		JacksonDBCollection<ReadSetOld, String> readSetsCollBck = MongoDBDAO.getCollection(READSET_ILLUMINA_BCK, ReadSetOld.class);
		if(readSetsCollBck.count() == 0){
			Logger.info("Migration readset start");
			backupReadSet();
			List<ReadSetOld> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class).toList();
			Logger.debug("migre "+readSets.size()+" readSets");
			for(ReadSetOld readSet : readSets){
				migreReadSet(readSet);				
			}
			Logger.info("Migration readset end");
						
		}else{
			Logger.info("Migration readset already execute !");
		}
		
		//backupRunBeforeRecalcul(); 
		List<Run> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class).toList();
		Logger.debug("recalcul "+runs.size()+" runs");
		for(Run run : runs){
			if("F".equals(run.state.code)){
				statsRecalcul(run);
			}
		}
		
		check();
		
		Logger.info("Migration finish");
		return ok("Migration Finish");
	}

	
	private static void migreReadSet(ReadSetOld readSet) {
		
		Valuation valuation = new Valuation();
		
		State state = new State();
		state.code = readSet.stateCode;
		state.user = (null == readSet.traceInformation.modifyUser) ? readSet.traceInformation.createUser : readSet.traceInformation.modifyUser;
		state.date = (null == readSet.traceInformation.modifyUser) ? readSet.traceInformation.creationDate : readSet.traceInformation.modifyDate;
		
		state = addHistorical(state);
		
		String runTypeCode = null;
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, readSet.runCode);
		if (run != null) {
			runTypeCode = run.typeCode; 
		}
		
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class, 
				DBQuery.is("code", readSet.code), 
				DBUpdate.unset("stateCode").unset("validProduction").unset("validProductionDate").unset("validBioinformatic").unset("validBioinformaticDate")
				.unset("sampleContainerCode").set("productionValuation", valuation).set("bioinformaticValuation", valuation).set("state", state).set("sampleCode", readSet.sampleContainerCode)
				.set("runSequencingStartDate", getDate(readSet.runCode))
				.set("runTypeCode", runTypeCode) );
		
		if(null != readSet.files){
			for(FileOld fileOld : readSet.files){
				
				if (fileOld.stateCode != null) {
					if(!fileOld.stateCode.equals(state.code)){
						throw new RuntimeException("file state are different from run state");
					}
					
					
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class, 
							DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.is("files.fullname", fileOld.fullname)),
							DBUpdate.unset("files.$.stateCode").set("files.$.state", state));	
				}
			}
		}
		
	}
	

	private static void migreRun(RunOld run) {
		
		Valuation valuation = new Valuation();
		
		State state = new State();
		state.code = run.stateCode;
		state.user = (null == run.traceInformation.modifyUser) ? run.traceInformation.createUser : run.traceInformation.modifyUser;
		state.date = (null == run.traceInformation.modifyUser) ? run.traceInformation.creationDate : run.traceInformation.modifyDate;
		
		state = addHistorical(state);
		
		InstrumentUsed instrumentUsed = new InstrumentUsed();
		instrumentUsed.code = run.instrumentUsed.code;
		instrumentUsed.typeCode = run.instrumentUsed.categoryCode;
		
		Set<String> projectCodes = new TreeSet<String>();
		Set<String> sampleCodes = new TreeSet<String>();
		
		List<ReadSetOld> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class, DBQuery.is("runCode", run.code)).toList();
		for (ReadSetOld readSetOld : readSets) {
			projectCodes.add(readSetOld.projectCode);
			sampleCodes.add(readSetOld.sampleContainerCode);
		}
		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.is("code", run.code), 
				DBUpdate.unset("stateCode").unset("valid").unset("validDate") 
				.set("valuation", valuation).set("state", state)
				.set("projectCodes", projectCodes).set("sampleCodes", sampleCodes)
				.set("instrumentUsed", instrumentUsed)
				.set("sequencingStartDate", getDate(run.code)) );
		
		if (run.lanes != null) {
			for (LaneOld laneOld : run.lanes) {

				MongoDBDAO.update(
						InstanceConstants.RUN_ILLUMINA_COLL_NAME,
						Run.class,
						DBQuery.and(DBQuery.is("code", run.code),
								DBQuery.is("lanes.number", laneOld.number)),
						DBUpdate.unset("lanes.$.stateCode")
								.unset("lanes.$.valid")
								.unset("lanes.$.validDate")
								.set("lanes.$.valuation", valuation));
			}
		}
	}

	private static void check() {
		
	}
	
	private static void statsRecalcul(Run run) {
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.add(run);
		// Outside of an actor and if no reply is needed the second argument can be null
		rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),ruleStatRG,facts),null);
	}

	

	private static void backupRun() {
		Logger.info("\tCopie "+InstanceConstants.RUN_ILLUMINA_COLL_NAME+" start");
		MongoDBDAO.save(RUN_ILLUMINA_BCK, MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, RunOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.RUN_ILLUMINA_COLL_NAME+" end");
		
	}
	
	
	private static void backupRunBeforeRecalcul() {
		Logger.info("\tCopie "+InstanceConstants.RUN_ILLUMINA_COLL_NAME+" before recalculation start");
		MongoDBDAO.save(RUN_ILLUMINA_BEFORE_RECALCUL, MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class).toList());
		Logger.info("\tCopie "+RUN_ILLUMINA_BEFORE_RECALCUL+" end");
		
	}
	
	
	private static void backupReadSet() {
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" start");		
		MongoDBDAO.save(READSET_ILLUMINA_BCK, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" end");
		
	}
	
	
	private static Date getDate(String runCode) {
		String sStartDate = runCode.substring(0, runCode.indexOf("_"));
		int year = Integer.parseInt(sStartDate.substring(0, 2)) + 2000;
		int month = Integer.parseInt(sStartDate.substring(2, 4))-1;
		int day = Integer.parseInt(sStartDate.substring(4, 6));
				
		java.util.Calendar calendar = new GregorianCalendar(year,month,day,0,0,0); 
		TimeZone tz = TimeZone.getTimeZone("GMT");
		calendar.setTimeZone(tz);

		return calendar.getTime(); 
	}
	
	
	private static State addHistorical(State state) {
			if (state.code.equals("F")) {				
				List<TransientState> lts = new ArrayList<TransientState>();
				
				TransientState ts1 = new TransientState();
				ts1.code = "F-RG";
				ts1.date = state.date;
				ts1.index = 0;
				ts1.user = state.user;
				
				TransientState ts2 = new TransientState(state, 1);
				
				lts.add(ts1);
				lts.add(ts2);
				
				state.historical = lts;
			}
			return state;
	}
	
	


}
