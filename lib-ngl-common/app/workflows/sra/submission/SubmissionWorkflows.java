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
public class SubmissionWorkflows extends TransitionWorkflows<Submission> {
	
//	private static final play.Logger.ALogger logger = play.Logger.of(SubmissionWorkflows.class);

	public static final String 
		N        = "N",
		N_R      = "N-R",
		V_SUB    = "V-SUB",
		IW_SUB	 = "IW-SUB",
		IP_SUB   = "IP-SUB",
		FE_SUB   = "FE-SUB",
		F_SUB    = "F-SUB",
		IW_SUB_R = "IW-SUB-R",
		IP_SUB_R = "IP-SUB-R",
		FE_SUB_R = "FE_SUB-R";
	
	private SubmissionWorkflowsHelper submissionWorkflowsHelper;

	private DoubleKeyMap<String, String, Transition<Submission>> trs;
	
	public class BasicTransition implements SubmissionTransition {
		public void execute(ContextValidation contextValidation, Submission submission, State nextState) {}
		public void success(ContextValidation contextValidation, Submission submission, State nextState) {}
		public void error(ContextValidation contextValidation, Submission object, State nextState) {}		
	}

	@Inject
	public SubmissionWorkflows(SubmissionWorkflowsHelper submissionWorkflowsHelper) {
		super();
		this.submissionWorkflowsHelper = submissionWorkflowsHelper;
		initSubmissionWorkflows();
	}

	void initSubmissionWorkflows() {
		trs = new DoubleKeyMap<>();
		//--------------------------------------------------
		// workflow d'une premiere soumission des données :
		//--------------------------------------------------
		// implementation d'une sous-classe de SubmissionTransition anonyme et interne 
		// classe interne qui existera au sein de l'instance environnemental SubmissionWorkflows
		trs.put(N     , V_SUB   , new BasicTransition() {		
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
			}
		});				
		trs.put(V_SUB , IW_SUB  , new BasicTransition() {
			@Override public void execute(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.activationPrimarySubmission(contextValidation, submission);
			}			
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
			}
		});
		trs.put(IW_SUB, IP_SUB  , new BasicTransition() {
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
			}
		});			
		trs.put(IP_SUB, F_SUB   , new BasicTransition() {	
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
				submissionWorkflowsHelper.updateSubmissionForDates(submission);

			}
		});	
		trs.put(IP_SUB, FE_SUB  , new BasicTransition() {
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
			}
		});
		trs.put(FE_SUB, IW_SUB  , new BasicTransition() {
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
			}			
		});
		
		
		//-------------------------------------------------------
		// workflow d'une soumission  pour release des données :
		//-------------------------------------------------------
		trs.put(N_R   , IW_SUB_R, new BasicTransition() {
			@Override public void execute(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.createDirSubmission(submission, contextValidation);
			}
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
			}
		});	
		trs.put(IW_SUB_R, IP_SUB_R, new BasicTransition() {
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
			}
			@Override public void error(ContextValidation contextValidation, Submission submission, State nextState) {		
				submissionWorkflowsHelper.rollbackSubmission(submission, contextValidation);
			}
		});			
		trs.put(IP_SUB_R, F_SUB, new BasicTransition() {
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionRelease(submission);
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
			}
		});	
		trs.put(IP_SUB_R, FE_SUB_R, new BasicTransition() {
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionRelease(submission);
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
			}
		});	
		trs.put(FE_SUB_R, IW_SUB_R  , new BasicTransition() {
			@Override public void success(ContextValidation contextValidation, Submission submission, State nextState) {
				submissionWorkflowsHelper.updateSubmissionChildObject(submission, contextValidation);
			}			
		});
		
	}


	public Transition<Submission> get(String currentStateCode, String nextStateCode) {
		return trs.get(currentStateCode, nextStateCode);
	}
	
	public ObjectType.CODE getObjectType() {
		return ObjectType.CODE.SRASubmission;
	}
	
	@Override
	public String getCollectionName() {
		return InstanceConstants.SRA_SUBMISSION_COLL_NAME;
	}
	@Override
	public Class<Submission> getElementClass() {
		return Submission.class;
	}
	
	public void activateSubmissionRelease(ContextValidation contextValidation, Submission submission) {
		//submission.setState(new State(SubmissionWorkflows.IW_SUB_R, contextValidation.getUser()));
		setState(contextValidation, submission, new State(SubmissionWorkflows.IW_SUB_R, contextValidation.getUser()));
	}
}
