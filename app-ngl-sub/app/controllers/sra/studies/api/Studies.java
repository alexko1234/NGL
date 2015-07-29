package controllers.sra.studies.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.submit.common.instance.Study;
import models.sra.submit.util.SraCodeHelper;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.DocumentController;
//import models.sra.submit.util.VariableSRA;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;


public class Studies extends DocumentController<Study>{

	final static Form<Study> studyForm = form(Study.class);
	final static Form<StudiesSearchForm> studiesSearchForm = form(StudiesSearchForm.class);
	
	public Studies() {
		super(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class);
	}


	public Result save() {
		Form<Study> filledForm = getFilledForm(studyForm, Study.class);
		Study userStudy = filledForm.get();
		
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
			contextValidation.setCreationMode();	
			if (userStudy._id == null) {
				userStudy.traceInformation = new TraceInformation(); 
				userStudy.traceInformation.setTraceInformation(getCurrentUser());
				userStudy.state = new State("userValidate", getCurrentUser());
				userStudy.centerName=VariableSRA.centerName;
				userStudy.centerProjectName = userStudy.projectCode;
				userStudy.code = SraCodeHelper.getInstance().generateStudyCode(userStudy.projectCode);	
				contextValidation.getContextObjects().put("type", "sra");
				userStudy.validate(contextValidation);
				//Logger.info("utilisateur = "+getCurrentUser());	
				if(contextValidation.errors.size()==0) {
					MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, userStudy);
				} else {
					return badRequest(filledForm.errorsAsJson());
				}
			} else {
				//return badRequest("study with id " + userStudy._id + " already exist");
				filledForm.reject("Study_id "+userStudy._id, "study with id " + userStudy._id + " already exist");  // si solution filledForm.reject
				return badRequest(filledForm.errorsAsJson());
			}
		
		return ok(Json.toJson(userStudy.code));
	}
	// Renvoie le Json correspondant à la liste des study ayant le projectCode indique dans la variable du formulaire projectCode et stockee dans
	// l'instance studiesSearchForm
	/*public Result list() {	
		Form<StudiesSearchForm> studiesFilledForm = filledFormQueryString(studiesSearchForm, StudiesSearchForm.class);
		StudiesSearchForm studiesSearchForm = studiesFilledForm.get();
		MongoDBResult<Study> results = MongoDBDAO.find(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, DBQuery.is("projectCode", studiesSearchForm.projCode));
		List<Study> studys = results.toList();
		return ok(Json.toJson(studys));
	}*/
	public Result list() {	
		StudiesSearchForm studiesFilledForm = filledFormQueryString(StudiesSearchForm.class);
		Query query = getQuery(studiesFilledForm);
		MongoDBResult<Study> results = mongoDBFinder(studiesFilledForm, query);		
		List<Study> studysList = results.toList();
		return ok(Json.toJson(studysList));
	}
	
	private Query getQuery(StudiesSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.projCode)) { //all
			queries.add(DBQuery.in("projectCode", form.projCode));
		}	
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
}
