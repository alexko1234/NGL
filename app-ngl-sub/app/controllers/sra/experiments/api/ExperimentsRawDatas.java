package controllers.sra.experiments.api;

//import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import models.laboratory.run.instance.Treatment;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.RawData;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.NGLContext;

public class ExperimentsRawDatas extends DocumentController<Experiment> {

	final /*static*/ Form<ExperimentsSearchForm> experimentsSearchForm;// = form(ExperimentsSearchForm.class);
	final /*static*/ Form<RawData> rawDataForm;// = form(RawData.class);
	
	@Inject
	public ExperimentsRawDatas(NGLContext ctx) {
		super(ctx,InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class);
		experimentsSearchForm = ctx.form(ExperimentsSearchForm.class);
		rawDataForm = ctx.form(RawData.class);
	}

	public Result list()
	{
		List<RawData> allRawDatas = new ArrayList<RawData>();
		Form<ExperimentsSearchForm> form = filledFormQueryString(experimentsSearchForm, ExperimentsSearchForm.class);
		ExperimentsSearchForm formExp = form.get();
		Query query = getQuery(formExp);
		MongoDBResult<Experiment> results = mongoDBFinder(formExp, query);		
		List<Experiment> list = results.toList();
		for(Experiment experiment : list){
			if(experiment.run!=null && experiment.run.listRawData!=null)
				allRawDatas.addAll(experiment.run.listRawData);
		}
		return ok(Json.toJson(allRawDatas));
	}
	
	public Result get(String code, String relatifName)
	{
		Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, code);
		for(RawData rawData : experiment.run.listRawData){
			if(rawData.relatifName.equals(relatifName))
				return ok(Json.toJson(rawData));
		}
		return badRequest("No rawData for experiment "+code+" file "+relatifName);
	}
	
	public Result update(String code, String relatifName)
	{
		Form<RawData> filledForm = getFilledForm(rawDataForm, RawData.class);
		RawData rawData = filledForm.get();
		Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, code);
		List<RawData> newRawDatas = new ArrayList<RawData>();
		for(RawData rawDataDB : experiment.run.listRawData){
			if(rawDataDB.relatifName.equals(relatifName))
				newRawDatas.add(rawData);
			else
				newRawDatas.add(rawDataDB);
		}
		experiment.run.listRawData=newRawDatas;
		MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment);
		return ok(Json.toJson(experiment));
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
		
		if(StringUtils.isNotBlank(form.code)) {
			queries.add(DBQuery.is("code", form.code));
		} else if (CollectionUtils.isNotEmpty(form.codes)) { //all
			queries.add(DBQuery.in("code", form.codes));
		}
		
		// ajout pour interface release study :
		if (StringUtils.isNotBlank(form.studyCode)) {
			queries.add(DBQuery.in("studyCode", form.studyCode));
		}
		
		if(StringUtils.isNotBlank(form.runCode)){
			queries.add(DBQuery.is("run.code", form.runCode));
		}
		// end ajout
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
}
