package workflows.readset;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.WriteResult;
// import org.springframework.stereotype.Service;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.SampleOnContainer;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
// import play.Logger;
// import play.Play;
import rules.services.RulesServices6;
import validation.ContextValidation;
//import workflows.WorkflowsCatalog;

// @Service
@Singleton
public class ReadSetWorkflowsHelper {

	private static final play.Logger.ALogger logger = play.Logger.of(ReadSetWorkflowsHelper.class);
	
	// private final String rulesKey;
	private final NGLContext ctx;
	
	@Inject
	public ReadSetWorkflowsHelper(NGLContext ctx) {
		this.ctx = ctx;
		// rulesKey = ctx.getRulesKey();
	}
	
	/*private final String rulesKey;
	
	// Not an injection constructor on purpose
	public ReadSetWorkflowsHelper(WorkflowsCatalog wc) {
		rulesKey = wc.getNGLContext().getRulesKey();
	}*/
	
	public void updateContainer(ReadSet readSet) {
		//insert sample container properties at the end of the ngsrg
		SampleOnContainer sampleOnContainer = InstanceHelpers.getSampleOnContainer(readSet);
		if(null != sampleOnContainer){
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
					DBQuery.is("code", readSet.code), DBUpdate.set("sampleOnContainer", sampleOnContainer));
		} else {
			// Logger.error("sampleOnContainer null for "+readSet.code);
			logger.error("sampleOnContainer null for {}", readSet.code);
		}			
	}
	
	public void updateDispatch(ReadSet readSet)	{
		//update dispatch
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
				DBQuery.is("code", readSet.code), DBUpdate.set("dispatch", Boolean.TRUE));	
	}
	
	public void updateBioinformaticValuation(ReadSet readSet, TBoolean valid, String user, Date date) {
			readSet.bioinformaticValuation.valid = valid;
			readSet.bioinformaticValuation.user = user;
			readSet.bioinformaticValuation.date = date;

			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
					DBQuery.is("code", readSet.code), DBUpdate.set("bioinformaticValuation", readSet.bioinformaticValuation));
	}
	
	public void updateFiles(ReadSet readSet, ContextValidation contextValidation) {
		//met les fichiers dipo ou non d
		State state = cloneState(readSet.state, contextValidation.getUser());
		if (null != readSet.files) {
			for(File f : readSet.files){
				WriteResult<ReadSet, String> r = MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
						DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.is("files.fullname", f.fullname)),
						DBUpdate.set("files.$.state", state));					
			}
		} else {
			// Logger.error("No files for "+readSet.code);
			logger.error("No files for {}", readSet.code);
		}
	}
	
	public void createSampleReadSetExternal(ReadSet readSet, ContextValidation contextValidation, String rules)	{
		//Create sample if doesn't exist (for external data)
		Sample sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, readSet.sampleCode);
		if (sample == null && contextValidation.getObject("external") != null && (Boolean)contextValidation.getObject("external")) {
			//Call rules
			ArrayList<Object> facts = new ArrayList<Object>();
			facts.add(readSet);
			facts.add(contextValidation);
			// RulesServices6.getInstance().callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), rules, facts);
			// RulesServices6.getInstance().callRulesWithGettingFacts(rulesKey, rules, facts);
			ctx.callRulesWithGettingFacts(rules, facts);
		}
	}
	
	/**
	 * Clone State without historical
	 * @param state
	 * @return
	 */
	private static State cloneState(State state, String user) {
		State nextState = new State();
		nextState.code = state.code;
		nextState.date = new Date();
		nextState.user = user;
		return nextState;
	}

	public boolean isHasBA(ReadSet readSet) {
		Project p = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, readSet.projectCode);
		if (p.bioinformaticParameters.biologicalAnalysis) {  //"^.+_.+F_.+_.+$" pour BFY
			return (StringUtils.isNotBlank(p.bioinformaticParameters.regexBiologicalAnalysis))?readSet.code.matches(p.bioinformaticParameters.regexBiologicalAnalysis):p.bioinformaticParameters.biologicalAnalysis; //TODO matche PE of type F
		}
		return false;
	}
	
}
