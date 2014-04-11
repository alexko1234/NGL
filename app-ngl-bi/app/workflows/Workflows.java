package workflows;

import java.util.ArrayList;
import java.util.List;


import lims.services.ILimsRunServices;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.TransientState;
import models.laboratory.container.instance.Container;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.SampleOnContainer;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.WriteResult;
import play.Logger;
import play.api.modules.spring.Spring;
import play.libs.Akka;
import rules.services.RulesActor;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.run.instance.RunValidationHelper;
import akka.actor.ActorRef;
import akka.actor.Props;


import com.mongodb.BasicDBObject;
import com.typesafe.config.ConfigFactory;

import fr.cea.ig.MongoDBDAO;

public class Workflows {

	private static ActorRef rulesActor = Akka.system().actorOf(new Props(RulesActor.class));
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

			List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", run.code)).toList();
			for(ReadSet readSet: readSets){
				State nextReadSetState = cloneState(run.state);
				setReadSetState(contextValidation, readSet, nextReadSetState);
			}


			ArrayList<Object> facts = new ArrayList<Object>();
			facts.add(run);
			rulesActor.tell(new RulesMessage(facts,ConfigFactory.load().getString("rules.key"),ruleStatRG),null);
		}else if("F-V".equals(run.state.code)){
			Spring.getBeanOfType(ILimsRunServices.class).valuationRun(run);
		}
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

		}else if("F-V".equals(readSet.state.code)){
			Spring.getBeanOfType(ILimsRunServices.class).valuationReadSet(readSet);
		} else if("A".equals(readSet.state.code) || "UA".equals(readSet.state.code))	{
			//met les fichier dipo ou non dès que le read set est valider
			State state = cloneState(readSet.state);
			if (null != readSet.files) {
				for(File f : readSet.files){
					WriteResult<ReadSet, String> r = MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,
							DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.is("files.fullname", f.fullname)),
							DBUpdate.set("files.$.state", state));
					Logger.debug(r.getError());
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
			nextStep.code = "IW-V";
		}else if("IW-V".equals(readSet.state.code)){
			if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)
				|| !TBoolean.UNSET.equals(readSet.productionValuation.valid)){
				nextStep.code = "IP-V";
			}
		}else if("IP-V".equals(readSet.state.code)){
			if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)
				&& !TBoolean.UNSET.equals(readSet.productionValuation.valid)){
				nextStep.code = "F-V";
			}
		}else if("F-V".equals(readSet.state.code) || "A".equals(readSet.state.code) || "UA".equals(readSet.state.code)){
			if(TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)
					|| TBoolean.UNSET.equals(readSet.productionValuation.valid)){
				nextStep.code = "IP-V";
			}else if(TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "A";
			}else if(TBoolean.FALSE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "UA";
			}
		}
		setReadSetState(contextValidation, readSet, nextStep);
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
		nextState.date = state.date;
		nextState.user = state.user;
		return nextState;
	}





}
