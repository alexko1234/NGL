package workflows.sra.submission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.util.SraException;
import models.utils.InstanceConstants;
//import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;

/**
 * 
 * @author sgas
 *
 * @param <K1>   cle1
 * @param <K2>   cle2
 * @param <V>    valeur de type transition associée à la paire de cles
 */
class DoubleKeyMap < K1, K2, V > {
	private Map < K1, Map<K2, V> > map = new HashMap<>();
	
	public void put(K1 k1, K2 k2 , V v){
		Map<K2, V> tmp = map.get(k1);
		if (tmp == null) {
			tmp = new HashMap<>();
			map.put(k1, tmp);
		}
		tmp.put(k2, v);
	}
	public V get(K1 k1, K2 k2) {
		Map<K2, V> tmp = map.get(k1);
		if (tmp == null) {
			return null;
		}
		return tmp.get(k2);
	}
}

//@Service
@Singleton
public class OtherSubmissionWorkflows extends TransitionWorkflows<Submission> {
	
	private static final play.Logger.ALogger logger = play.Logger.of(SubmissionWorkflows.class);

	public static final String IW_SUB_R = "IW-SUB-R";
	
//	enum StateCode {
//		ReleaseEnAttenteBirds       ("IW-SUB-R"),
//		ReleaseEnCoursBirds         ("IP-SUB-R"),
//		SoumissionEnAttenteBirds    ("IW-SUB"), 
//		SoumissionEnCoursBirds      ("IP-SUB"),
//		SoumissionOk                ("F-SUB");
//		
//		private final String code;
//		
//		StateCode (String code) {
//			this.code = code;
//		}
//		
//		public static StateCode fromString(String code) {
//			for (StateCode s : values()) {
//				if (s.code.equals(code)) {
//					return s;
//				}
//			}
//			throw new RuntimeException("stateCode non trouvé " + code);
//		}
//		public String getCode() {
//			return this.code;
//		}	
//	}
//	
	//@Autowired
	private SubmissionWorkflowsHelper submissionWorkflowsHelper;
	
	@Inject
	public OtherSubmissionWorkflows(SubmissionWorkflowsHelper submissionWorkflowsHelper) {
		super();
		this.submissionWorkflowsHelper = submissionWorkflowsHelper;
	}
	
	public interface APSR {
		void run (SubmissionWorkflows self, ContextValidation validation, Submission submission);
	}
	
	public interface Fonction <A,B> {
		B applique(A x);
	}
	
	public static class UpperCase implements Fonction <String, String> {
		public String applique(String x) {
			return x.toUpperCase();
		}
	}
	
//	public static class Tools {
//		
//		public static List<String> xxx(List<String> l) {
//			return map(l,new UpperCase());
//		}
//		
//		public static List<String> anonimous(List<String> l) {
//			return map(l,new Fonction<String, String>(){
//				public String applique(String x) {
//					return x.toLowerCase();
//				}
//			});
//		}
//
//
//		public static <A,B>List<B> map(List<A> l, Fonction <A,B> f) {
//			List<B> bs = new ArrayList<>();
//			for (A i : l) {
//				bs.add(f.applique(i));
//			}
//			return bs;
//		}
//		
//	}
	
//	static class IP_SUB_R__F_SUB implements SubmissionTransition {
//
//		@Override
//		public void execute(SubmissionWorkflows self, ContextValidation validation, Submission submission,
//				 State nextState) {
//			logger.debug("call update submission Release");
//			self.submissionWorkflowsHelper.updateSubmissionRelease(submission);	
//		}
//		@Override
//		public void success(SubmissionWorkflows self, ContextValidation validation, Submission submission,State nextState) {
//			self.submissionWorkflowsHelper.updateSubmissionChildObject(submission, validation);
//		}
//		@Override
//		public void error(SubmissionWorkflows self, ContextValidation validation, Submission submission,State nextState) {
//			logger.error("Problem on SubmissionWorkflow.applyErrorPostStateRules : " + validation.errors.toString());
//		}
//	}
//	
//	static class F_SUB__IW_SUB_R implements SubmissionTransition {
//
//		
//	}
//	
//	static class V_SUB__IW_SUB implements SubmissionTransition {
//
//	
//	}
	
//	static final IP_SUB__F_SUB IP_SUB__F_SUB = new IP_SUB__F_SUB();
//	
//	
//	static class IP_SUB__F_SUB implements SubmissionTransition {
//
//		@Override
//		public void execute(SubmissionWorkflows self, ContextValidation validation, Submission submission,
//				 State nextState) {
//			logger.debug("call update submission Release");
//			self.submissionWorkflowsHelper.updateSubmissionForDates(submission);
//		}
//		
//	}
//	
	//private static final DoubleKeyMap<String, String, APSR> dkm;
	private /*static final*/ DoubleKeyMap<String, String, Transition<Submission>> trs;
	public static final String 
		IP_SUB_R = "IP-SUB-R",
		F_SUB    = "F-SUB";
	
	OtherSubmissionWorkflows() {
//		dkm = new DoubleKeyMap<>();
//		//dkm.put(StateCode.ReleaseEnAttente,StateCode.ReleaseEnCoursBirds, new IP_SUB_R__F_SUB());
//		dkm.put("IP-SUB-R", "F-SUB", new IP_SUB_R__F_SUB());
//		dkm.put("F-SUB", IW_SUB_R, new F_SUB__IW_SUB_R());
//		dkm.put("V-SUB","IW-SUB", new V_SUB__IW_SUB());
//		dkm.put("IP-SUB", "F-SUB", IP_SUB__F_SUB);
		trs = new DoubleKeyMap<>();
//		trs.put("IP-SUB-R", "F-SUB", new IP_SUB_R__F_SUB());
//		trs.put("F-SUB", IW_SUB_R, new F_SUB__IW_SUB_R());
//		trs.put("V-SUB","IW-SUB", new V_SUB__IW_SUB());
//		trs.put("IP-SUB", "F-SUB", IP_SUB__F_SUB);
//		trs.put("IP-SUB-R", "F-SUB", new Transition3<Submission>(
//				(self, validation, submission, nextState)->{
//					self.submissionWorkflowsHelper.createDirSubmission(submission, validation);
//				},
//				(self, validation, submission, nextState)->{
//					self.submissionWorkflowsHelper.updateSubmissionChildObject(submission, validation);
//				},
//				(self, validation, submission, nextState)->{
//					logger.error("Problem on SubmissionWorkflow.applyErrorPostStateRules : "+validation.errors.toString());
//				}));
		
		
		// sous-classe de SubmissionTransition anonyme et interne => classe interne qui existera au sein de l'instance environnemental SubmissionWorkflows
		trs.put(IP_SUB_R, F_SUB, new SubmissionTransition() {
				public void execute(ContextValidation validation, Submission submission, State nextState) {
					logger.debug("call update submission Release");
					updateTraceInformation(submission.traceInformation, nextState);
					submissionWorkflowsHelper.updateSubmissionRelease(submission); 
					// methode submissionWorkflowsHelper de la classe englobante SubmissionWorkflows est accessible 
					// directement si on est dans 
					//SubmissionWorkflows.this.submissionWorkflowsHelper.updateSubmissionRelease(submission);
				}
				public void success(ContextValidation validation, Submission submission,State nextState) {
					submissionWorkflowsHelper.updateSubmissionChildObject(submission, validation);
				}
				public void error(ContextValidation validation, Submission submission,State nextState) {
					logger.error("Problem on SubmissionWorkflow.applyErrorPostStateRules : "+validation.errors.toString());
				}
		});
		trs.put(F_SUB, IW_SUB_R, new SubmissionTransition() {

			@Override
			public void execute(ContextValidation contextValidation, Submission submission, State nextState) {
				updateTraceInformation(submission.traceInformation, nextState);
				submissionWorkflowsHelper.createDirSubmission(submission, contextValidation);
			}
			@Override
			public void success(ContextValidation contextValidation, Submission object, State nextState) {}
			@Override
			public void error( ContextValidation contextValidation, Submission object, State nextState) {}
			
		});

	}

	
	
	
	
	
	@Override
	public void applyPreStateRules(ContextValidation validation,
			Submission submission, State nextState) {
//		if("IP-SUB-R".equals(submission.state.code) && "F-SUB".equals(nextState.code)){
//			logger.debug("call update submission Release");
//			submissionWorkflowsHelper.updateSubmissionRelease(submission);
//		}
//		
//		if ("F-SUB".equals(submission.state.code) && "IW-SUB-R".equals(nextState.code)){
//			submissionWorkflowsHelper.createDirSubmission(submission, validation);
//		}
//
//		if ("V-SUB".equals(submission.state.code) && "IW-SUB".equals(nextState.code)){
//			submissionWorkflowsHelper.activationPrimarySubmission(validation, submission);
//		}
//		if("IP-SUB".equals(submission.state.code) && "F-SUB".equals(nextState.code)){
//			logger.debug("call update submission Release");
//			submissionWorkflowsHelper.updateSubmissionForDates(submission);
//		}
//		String currentStateCode = submission.state.code;
//		String nextStateCode = nextState.code;
//		
//		APSR transition = dkm.get(currentStateCode, nextStateCode);
//		if (transition == null) {
//			String message = "dans apply pre state rules, pb dans les states fournis" + currentStateCode + nextStateCode;
//			logger.error(message);
//			validation.addError("applyPreStateRules", message);
//		} else {
//			transition.run(this, validation, submission);
//			logger.debug("dans apply pre state rules avec nextState = '" + nextState.code + "'");
//			
//			updateTraceInformation(submission.traceInformation, nextState); 
//		}
//		
//		
//		//String currentStateCode = submission.state.code;
//		//String nextStateCode = nextState.code;
//		
//		SubmissionTransition tr = trs.get(currentStateCode, nextStateCode);
//		if (tr == null) {
//			String message = "dans apply pre state rules, pb dans les states fournis" + currentStateCode + nextStateCode;
//			logger.error(message);
//			validation.addError("applyPreStateRules", message);
//		} else {
//			tr.applyPreStateRules(validation, submission, nextState);
//			logger.debug("dans apply pre state rules avec nextState = '" + nextState.code + "'");
//			updateTraceInformation(submission.traceInformation, nextState); 
//		}

	}
	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, Submission object) {}
/*	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, Submission object) {
		SubmissionTransition tr = trs.get(currentStateCode, nextStateCode);
		if (tr == null) {
			String message = "dans apply pre state rules, pb dans les states fournis" + currentStateCode + nextStateCode;
			logger.error(message);
			validation.addError("applyPreStateRules", message);
		} else {
			tr.applyPreStateRules(validation, submission, nextState);
			logger.debug("dans apply pre state rules avec nextState = '" + nextState.code + "'");
			updateTraceInformation(submission.traceInformation, nextState); 
		}
	}
*/
	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, Submission object) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void applySuccessPostStateRules(ContextValidation validation, Submission submission) {
		if (! submission.state.code.equalsIgnoreCase("N") && ! submission.state.code.equalsIgnoreCase("N-R") && !submission.state.code.equalsIgnoreCase("IW-SUB-R")){
			submissionWorkflowsHelper.updateSubmissionChildObject(submission, validation);
		}
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation,
			Submission submission, State nextState) {
		if("IW-SUB-R".equals(submission.code)){
			submissionWorkflowsHelper.rollbackSubmission(submission, validation);
		}
		if(validation.hasErrors()){
			logger.error("Problem on SubmissionWorkflow.applyErrorPostStateRules : "+validation.errors.toString());
		}
	}

	public Transition<Submission> get(String currentStateCode, String nextStateCode) {
		return trs.get(currentStateCode, nextStateCode);
	}
	public ObjectType.CODE getObjectType() {
		return ObjectType.CODE.SRASubmission;
	}
	
//	@Override
//	public void setState(ContextValidation contextValidation, Submission submission, State nextState) {
//		logger.debug("dans setState avec submission" + submission.code +" et et submission.state="+submission.state.code+ " et nextState="+nextState.code);
//
//		contextValidation.setUpdateMode();
//		
//		String currentStateCode = submission.state.code;
//		String nextStateCode = nextState.code;
//		Transition<Submission> tr = get(currentStateCode, nextStateCode);  //trs.get(currentStateCode, nextStateCode);
//		if (tr == null) {
//			throw new RuntimeException();
//		}	
//		//CommonValidationHelper.validateState(ObjectType.CODE.SRASubmission, nextState, contextValidation); 
//		CommonValidationHelper.validateState(getObjectType(), nextState, contextValidation); 
//		if (contextValidation.hasErrors()) { 
//			throw new RuntimeException();
//		}
//		tr.execute(this, contextValidation, submission, nextState);
//		
//		// --------------------------
//		
//		
//		if (!contextValidation.hasErrors()) {
//			submission.state = updateHistoricalNextState(submission.state, nextState);	
//			// sauver le state dans la base avec traceInformation
//			MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME,  Submission.class, 
//					DBQuery.is("code", submission.code),
//					DBUpdate.set("state", submission.state).set("traceInformation", submission.traceInformation));
//			tr.success(this, contextValidation, submission, nextState);
//			if (contextValidation.hasErrors()) {
//				
//			}
//		} else {
//			tr.error(this, contextValidation, submission, nextState);
//		}
//		// verifier que le state à installer est valide avant de mettre à jour base de données : 
//		// verification qui ne passe pas par VariableSRA [SraValidationHelper.requiredAndConstraint(contextValidation, nextState.code , VariableSRA.mapStatus, "state.code")]		
//		// mais par CommonValidationHelper.validateState(ObjectType.CODE.SRASubmission, nextState, contextValidation); 
//		// pour uniformiser avec reste du code ngl
//		logger.debug("dans setState");
//		logger.debug("contextValidation.error avant validateState " + contextValidation.errors);
//
//		CommonValidationHelper.validateState(ObjectType.CODE.SRASubmission, nextState, contextValidation); 	
//		logger.debug("contextValidation.error apres validateState " + contextValidation.errors);
//
//		if (contextValidation.hasErrors()) { 
//			logger.error("ATTENTION ERROR :"+contextValidation.errors);
//		} else if (nextState.code.equals(submission.state.code)) {
//			logger.error("ATTENTION ERROR :submissionStateCode == {} et nextStateCode == {}", 
//						 submission.state.code, nextState.code);
//		} else {
//			applyPreStateRules(contextValidation, submission, nextState);
//			//submission.validate(contextValidation);
//			if (!contextValidation.hasErrors()) {
//				// Gerer l'historique des states :
//				submission.state = updateHistoricalNextState(submission.state, nextState);	
//				// sauver le state dans la base avec traceInformation
//				MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME,  Submission.class, 
//						DBQuery.is("code", submission.code),
//						DBUpdate.set("state", submission.state).set("traceInformation", submission.traceInformation));
//				applySuccessPostStateRules(contextValidation, submission);
//				nextState(contextValidation, submission);		
//			} else {
//				applyErrorPostStateRules(contextValidation, submission, nextState);	
//			}
//		}
//	}

	@Override
	public String getCollectionName() {
		return InstanceConstants.SRA_SUBMISSION_COLL_NAME;
	}
	@Override
	public Class<Submission> getElementClass() {
		return Submission.class;
	}


}
