package controllers.sra.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.ReadSpec;
import models.utils.InstanceConstants;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;

public class ExperimentsReadSpecs extends DocumentController<Experiment> {

	final static Form<ExperimentsSearchForm> experimentsSearchForm = form(ExperimentsSearchForm.class);
	
	public ExperimentsReadSpecs() {
		super(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class);
	}

	public Result list()
	{
		List<ReadSpec> allReadSpecs = new ArrayList<ReadSpec>();
		Form<ExperimentsSearchForm> form = filledFormQueryString(experimentsSearchForm, ExperimentsSearchForm.class);
		ExperimentsSearchForm formExp = form.get();
		Query query = getQuery(formExp);
		MongoDBResult<Experiment> results = mongoDBFinder(formExp, query);		
		List<Experiment> list = results.toList();
		for(Experiment experiment : list){
			if(experiment.readSpecs!=null)
				allReadSpecs.addAll(experiment.readSpecs);
		}
		return ok(Json.toJson(allReadSpecs));
	}
	
	private Query getQuery(ExperimentsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if (StringUtils.isNotBlank(form.submissionCode)) {
			//Get submissionFromCode
			Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, form.submissionCode);
			if(submission!=null && CollectionUtils.isNotEmpty(submission.experimentCodes)){
				queries.add(DBQuery.in("code", submission.experimentCodes));
			}
		}
		
		if(StringUtils.isNotBlank(form.experimentCode)){
			queries.add(DBQuery.is("code", form.experimentCode));
		}else if (CollectionUtils.isNotEmpty(form.listExperimentCodes)) { //all
			queries.add(DBQuery.in("code", form.listExperimentCodes));
		}
		
		// ajout pour interface release study :
		if (StringUtils.isNotBlank(form.studyCode)) {
			queries.add(DBQuery.in("studyCode", form.studyCode));
		}
		// end ajout
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
	
}