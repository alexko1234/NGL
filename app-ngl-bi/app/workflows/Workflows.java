package workflows;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import akka.actor.ActorRef;
import akka.actor.Props;

import com.typesafe.config.ConfigFactory;

import play.Logger;
import play.libs.Akka;
import rules.services.RulesActor;
import rules.services.RulesMessage;

import org.mongojack.DBQuery;

import org.mongojack.DBUpdate;
import fr.cea.ig.MongoDBDAO;


import lims.services.ILimsRunServices;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.TransientState;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.SampleOnContainer;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.WriteResult;
import play.Logger;
import play.api.modules.spring.Spring;
import play.libs.Akka;
import rules.services.RulesActor;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.run.instance.AnalysisValidationHelper;
import validation.run.instance.RunValidationHelper;
import akka.actor.ActorRef;
import akka.actor.Props;




import com.mongodb.BasicDBObject;
import com.typesafe.config.ConfigFactory;

import controllers.CommonController;

import fr.cea.ig.MongoDBDAO;

public class Workflows {
	
	private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor.class));
	private static final String ruleStatRG="rg_1";
			
	
	public static void setRunState(ContextValidation contextValidation, Run run, State nextState) {
		
		//on valide l'état			
		contextValidation.setUpdateMode();
		RunValidationHelper.validateState(run.typeCode, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(run.state.code)){
			boolean goBack = goBack(run.state, nextState);
			if(goBack)Logger.debug(run.code+" : back to the workflow. "+run.state.code +" -> "+nextState.code);		
			
			run.traceInformation = updateTraceInformation(run.traceInformation, nextState); 
			run.state = updateHistoricalNextState(run.state, nextState);
			
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
					DBQuery.is("code", run.code),
					DBUpdate.set("state", run.state).set("traceInformation",run.traceInformation));
			
			applyRunRules(contextValidation, run);
			nextRunState(contextValidation, run);
		}		
	}

	
	public static void nextRunState(ContextValidation contextValidation, Run run) {
		State nextStep = cloneState(run.state);
		if("F-RG".equals(run.state.code)){
			nextStep.code = "IW-V";
		}else if("F-S".equals(run.state.code)){
			nextStep.code = "IW-RG";
		}else if("IW-V".equals(run.state.code) && atLeastOneValuation(run)){
			nextStep.code = "IP-V";
		}else if("IP-V".equals(run.state.code) && isRunValuationComplete(run)){
			nextStep.code = "F-V";
		}else if("F-V".equals(run.state.code) && !isRunValuationComplete(run)){
			nextStep.code = "IP-V";
		}
		setRunState(contextValidation, run, nextStep);
	}

	public static boolean isRunValuationComplete(Run run) {

		if(run.valuation.valid.equals(TBoolean.UNSET)){
			return false;
		}
		for(Lane lane : run.lanes){
			if(lane.valuation.valid.equals(TBoolean.UNSET)){
				return false;
			}
		}
		return true;
	}
	
	public static boolean atLeastOneValuation(Run run) {

		if(!run.valuation.valid.equals(TBoolean.UNSET)){
			return true;
		}
		for(Lane lane : run.lanes){
			if(!lane.valuation.valid.equals(TBoolean.UNSET)){
				return true;
			}
		}
		return false;
	}
	
	private static void applyRunRules(ContextValidation contextValidation, Run run) {
		if("F-RG".equals(run.state.code)){
			//update dispatch
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
					DBQuery.is("code", run.code), DBUpdate.set("dispatch", Boolean.TRUE));
			
			List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", run.code), getReadSetKeys()).toList();
			for(ReadSet readSet: readSets){
				State nextReadSetState = cloneState(run.state);
				setReadSetState(contextValidation, readSet, nextReadSetState);
			}
			
			
			ArrayList<Object> facts = new ArrayList<Object>();
			facts.add(run);		
			rulesActor.tell(new RulesMessage(facts,ConfigFactory.load().getString("rules.key"),ruleStatRG),null);
		}else if("F-V".equals(run.state.code)){
			Spring.getBeanOfType(ILimsRunServices.class).valuationRun(run);
			//For all lane with VALID = FALSE so we put VALID=FALSE on each read set
			for (Lane lane : run.lanes) {
				if (lane.valuation.valid.equals(TBoolean.FALSE)) {
					List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
							DBQuery.and(DBQuery.is("runCode", run.code), DBQuery.is("laneNumber", lane.number)), getReadSetKeys()).toList();
					if(readSets.size() != lane.readSetCodes.size())Logger.error("Problem with number of readsets for run = "+run.code+" and lane = "+lane.number+". Nb RS in lane = "+lane.readSetCodes.size()+", nb RS by query = "+readSets.size());
					for(ReadSet readSet: readSets){
						if(TBoolean.UNSET.equals(readSet.productionValuation.valid)){
							readSet.productionValuation.valid = TBoolean.FALSE;
							readSet.productionValuation.date = new Date();
							readSet.productionValuation.user = CommonController.getCurrentUser();
							if(null == readSet.productionValuation.resolutionCodes)readSet.productionValuation.resolutionCodes = new ArrayList<String>(1);
							readSet.productionValuation.resolutionCodes.add("Run-abandonLane");
							
							readSet.traceInformation.modifyDate = new Date();
							readSet.traceInformation.modifyUser = CommonController.getCurrentUser();
								
							MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
									DBQuery.is("code", readSet.code), DBUpdate.set("productionValuation", readSet.productionValuation).set("traceInformation", readSet.traceInformation));
							nextReadSetState(contextValidation, readSet);					
						}
					}					
				}
			}	
		}
	}

	private static BasicDBObject getReadSetKeys() {
		BasicDBObject keys = new BasicDBObject();
		keys.put("treatments", 0);
		return keys;
	}


	public static void setReadSetState(ContextValidation contextValidation, ReadSet readSet, State nextState) {
		
		//on valide l'état			
		contextValidation.setUpdateMode();
		RunValidationHelper.validateState(readSet.typeCode, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(readSet.state.code)){
			boolean goBack = goBack(readSet.state, nextState);
			if(goBack)Logger.debug(readSet.code+" : back to the workflow. "+readSet.state.code +" -> "+nextState.code);		
			
			readSet.traceInformation = updateTraceInformation(readSet.traceInformation, nextState); 
			readSet.state = updateHistoricalNextState(readSet.state, nextState);
			
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  Run.class, 
					DBQuery.is("code", readSet.code),
					DBUpdate.set("state", readSet.state).set("traceInformation",readSet.traceInformation));
			applyReadSetRules(contextValidation, readSet);
			nextReadSetState(contextValidation, readSet);
		}	
	}
	
	private static void applyReadSetRules(ContextValidation contextValidation, ReadSet readSet) {
		if("F-RG".equals(readSet.state.code)){
			//update dispatch
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
					DBQuery.is("code", readSet.code), DBUpdate.set("dispatch", Boolean.TRUE));	
			
			//insert sample container properties at the en of the ngsrg
			SampleOnContainer sampleOnContainer = InstanceHelpers.getSampleOnContainer(readSet);
			if(null != sampleOnContainer){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
						DBQuery.is("code", readSet.code), DBUpdate.set("sampleOnContainer", sampleOnContainer));
			}else{
				Logger.error("sampleOnContainer null for "+readSet.code);
			}
			
		}else if("F-VQC".equals(readSet.state.code)){
			Spring.getBeanOfType(ILimsRunServices.class).valuationReadSet(readSet, true);	
			if(TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
				readSet.bioinformaticValuation.valid = readSet.productionValuation.valid;
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
						DBQuery.is("code", readSet.code), DBUpdate.set("bioinformaticValuation.valid", readSet.bioinformaticValuation.valid));
			}
		} else if("IW-BA".equals(readSet.state.code)){
			readSet.bioinformaticValuation.valid = TBoolean.UNSET;
			readSet.bioinformaticValuation.date = null;
			readSet.bioinformaticValuation.user = null;
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
					DBQuery.is("code", readSet.code), DBUpdate.set("bioinformaticValuation.valid", readSet.bioinformaticValuation.valid));
		} else if("A".equals(readSet.state.code) || "UA".equals(readSet.state.code))	{
			//met les fichier dipo ou non dès que le read set est valider
			State state = cloneState(readSet.state);
			if (null != readSet.files) {
				for(File f : readSet.files){
					WriteResult<ReadSet, String> r = MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
							DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.is("files.fullname", f.fullname)),
							DBUpdate.set("files.$.state", state));					
				}
			}
			else {
				Logger.error("No files for "+readSet.code);
			}
			
		}
	}

	


	public static void nextReadSetState(ContextValidation contextValidation, ReadSet readSet) {
		State nextStep = cloneState(readSet.state);
		if("F-RG".equals(readSet.state.code)){
			nextStep.code = "IW-QC";
		}else if("F-QC".equals(readSet.state.code)){
			nextStep.code = "IW-VQC";
		}else if("IW-VQC".equals(readSet.state.code)){
			if(!TBoolean.UNSET.equals(readSet.productionValuation.valid)){
				nextStep.code = "F-VQC";
			}
		}else if("IP-VQC".equals(readSet.state.code)){
			if(!TBoolean.UNSET.equals(readSet.productionValuation.valid)){
					nextStep.code = "F-VQC";
				}		
		}else if("F-VQC".equals(readSet.state.code)){
			if(isHasBA(readSet) && TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "IW-BA";
			}else{
				if(TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
					nextStep.code = "A";
				}else if(TBoolean.FALSE.equals(readSet.bioinformaticValuation.valid)){
					nextStep.code = "UA";
				}
			}
		}else if("F-BA".equals(readSet.state.code)){
			nextStep.code = "IW-VBA";
		}else if("IW-VBA".equals(readSet.state.code)){
			if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "F-VBA";
			}		
		}else if("F-VBA".equals(readSet.state.code)){
			if(TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
					nextStep.code = "A";
			}else if(TBoolean.FALSE.equals(readSet.bioinformaticValuation.valid)){
					nextStep.code = "UA";
			}					
		}else if("A".equals(readSet.state.code) || "UA".equals(readSet.state.code)){			
			if(TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "A";
			}else { //FALSE or UNSET
				nextStep.code = "UA";
			}
			//if change valuation when final step
			Spring.getBeanOfType(ILimsRunServices.class).valuationReadSet(readSet, false);	
		}
		setReadSetState(contextValidation, readSet, nextStep);
	}
	
	
	private static boolean isHasBA(ReadSet readSet){
		Project p = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, readSet.projectCode);
		if(p.bioinformaticAnalysis){
			return readSet.code.matches("^.+_.+F_.+_.+$"); //TODO matche PE of type F
		}
		return false;
	}
	
	private static State updateHistoricalNextState(State previousState, State nextState) {
		if (null == previousState.historical) {
			nextState.historical = new ArrayList<TransientState>(0);
			nextState.historical.add(new TransientState(previousState, nextState.historical.size()));
		} else {
			nextState.historical = previousState.historical;
		}
		nextState.historical.add(new TransientState(nextState, nextState.historical.size()));		
		return nextState;
	}

	private static TraceInformation updateTraceInformation(
			TraceInformation traceInformation, State nextState) {		
		traceInformation.modifyDate = nextState.date;
		traceInformation.modifyUser = nextState.user;		
		return traceInformation;
	}

	private static boolean goBack(State previousState, State nextState) {
		models.laboratory.common.description.State nextStateDesc = getStateDescription(nextState);
		models.laboratory.common.description.State previousStateDesc = getStateDescription(previousState);
		boolean goBack = false;
		if(nextStateDesc.position < previousStateDesc.position){
			goBack=true;
			
		}
		return goBack;
	}

	private static models.laboratory.common.description.State getStateDescription(
			State state) {
		try {
			return models.laboratory.common.description.State.find.findByCode(state.code);

		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Clone State without historical
	 * @param state
	 * @return
	 */
	private static State cloneState(State state) {
		State nextState = new State();
		nextState.code = state.code;
		nextState.date = new Date();
		nextState.user = CommonController.getCurrentUser();
		return nextState;
	}


	public static void setAnalysisState(ContextValidation contextValidation, Analysis analysis, State nextState) {
		//on valide l'état			
		contextValidation.setUpdateMode();
		AnalysisValidationHelper.validateState(analysis.typeCode, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(analysis.state.code)){
			boolean goBack = goBack(analysis.state, nextState);
			if(goBack)Logger.debug(analysis.code+" : back to the workflow. "+analysis.state.code +" -> "+nextState.code);		
			
			analysis.traceInformation = updateTraceInformation(analysis.traceInformation, nextState); 
			analysis.state = updateHistoricalNextState(analysis.state, nextState);
			
			MongoDBDAO.update(InstanceConstants.ANALYSIS_COLL_NAME,  Analysis.class, 
					DBQuery.is("code", analysis.code),
					DBUpdate.set("state", analysis.state).set("traceInformation",analysis.traceInformation));
			
			applyAnalysisRules(contextValidation, analysis);
			nextAnalysisState(contextValidation, analysis);
		}		
		
	}


	private static void applyAnalysisRules(ContextValidation contextValidation, Analysis analysis) {
		if("IP-BA".equals(analysis.state.code)){
			for(String rsCode : analysis.masterReadSetCodes){
				ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, rsCode, getReadSetKeys());
				State nextStep = cloneState(readSet.state);
				nextStep.code = "IP-BA";
				setReadSetState(contextValidation, readSet, nextStep);
			}
		}else if("F-BA".equals(analysis.state.code)){
			//update readset if necessary
			for(String rsCode : analysis.masterReadSetCodes){
				ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, rsCode, getReadSetKeys());
				State nextStep = cloneState(readSet.state);
				nextStep.code = "F-BA";
				setReadSetState(contextValidation, readSet, nextStep);				
			}							
		}else if("IW-V".equals(analysis.state.code)){
			for(String rsCode : analysis.masterReadSetCodes){
				ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, rsCode, getReadSetKeys());
				//if different of state IW-VBA
				if(!"IW-VBA".equals(readSet.state.code)){
					readSet.bioinformaticValuation.valid = TBoolean.UNSET;
					readSet.bioinformaticValuation.date = null;
					readSet.bioinformaticValuation.user = null;
					
					readSet.traceInformation.modifyDate = new Date();
					readSet.traceInformation.modifyUser = CommonController.getCurrentUser();
						
						
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
							DBQuery.is("code", rsCode), DBUpdate.set("bioinformaticValuation", readSet.bioinformaticValuation).set("traceInformation", readSet.traceInformation));
					
					State nextStep = cloneState(readSet.state);
					nextStep.code = "IW-VBA";
					setReadSetState(contextValidation, readSet, nextStep);
				}
			}		
		}else if("F-V".equals(analysis.state.code)){
			for(String rsCode : analysis.masterReadSetCodes){
				ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, rsCode, getReadSetKeys());
				if(TBoolean.TRUE.equals(analysis.valuation.valid)){
					readSet.bioinformaticValuation.valid = TBoolean.TRUE;
					readSet.bioinformaticValuation.date = new Date();
					readSet.bioinformaticValuation.user = CommonController.getCurrentUser();
					
					readSet.traceInformation.modifyDate = new Date();
					readSet.traceInformation.modifyUser = CommonController.getCurrentUser();
					
					
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
							DBQuery.is("code", rsCode), DBUpdate.set("bioinformaticValuation", readSet.bioinformaticValuation).set("traceInformation", readSet.traceInformation));
					
					State nextStep = cloneState(readSet.state);
					nextStep.code = "F-VBA";
					setReadSetState(contextValidation, readSet, nextStep);
				}								
			}		
		}
	}


	public static void nextAnalysisState(ContextValidation contextValidation, Analysis analysis) {
		State nextStep = cloneState(analysis.state);
		
		if("F-BA".equals(analysis.state.code)){
			nextStep.code = "IW-V";
		}else if("IW-V".equals(analysis.state.code)){
			if(!TBoolean.UNSET.equals(analysis.valuation.valid)){
				nextStep.code = "F-V";
			}
		}else if("F-V".equals(analysis.state.code)){
			if(TBoolean.UNSET.equals(analysis.valuation.valid)){
				nextStep.code = "IW-V";
			}
		}
		
		setAnalysisState(contextValidation, analysis, nextStep);
		
	}

}
