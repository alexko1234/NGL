package controllers.sra.studies.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.submit.common.instance.AbstractStudy;
import models.sra.submit.common.instance.Study;
//import models.sra.submit.common.instance.Study;
import models.sra.submit.util.SraCodeHelper;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import controllers.DocumentController;
//import models.sra.submit.util.VariableSRA;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;


public class Studies extends DocumentController<AbstractStudy>{

	final static Form<AbstractStudy> studyForm = form(AbstractStudy.class);
	final static Form<StudiesSearchForm> studiesSearchForm = form(StudiesSearchForm.class);
	
	public Studies() {
		super(InstanceConstants.SRA_STUDY_COLL_NAME, AbstractStudy.class);
	}


	public Result save() {
		Form<AbstractStudy> filledForm = getFilledForm(studyForm, AbstractStudy.class);
		AbstractStudy userStudy = filledForm.get();
		
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
			contextValidation.setCreationMode();	
			if (userStudy._id == null) {
				userStudy.traceInformation = new TraceInformation(); 
				userStudy.traceInformation.setTraceInformation(getCurrentUser());
				userStudy.state = new State("N", getCurrentUser());
				if (userStudy instanceof Study){
					((Study)userStudy).centerName=VariableSRA.centerName;
					((Study)userStudy).centerProjectName = ((Study)userStudy).projectCode;
					((Study)userStudy).code = SraCodeHelper.getInstance().generateStudyCode(((Study)userStudy).projectCode);	
				}
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

	// methode list appelee avec url suivante :
	//localhost:9000/api/sra/studies?datatable=true&paginationMode=local&projCode=BCZ
	// url construite dans services.js 
	//search : function(){
	//	this.datatable.search({projCode:this.form.projCode, state:'N'});
	//},
	// Renvoie le Json correspondant à la liste des study ayant le projectCode indique dans la variable du formulaire projectCode et stockee dans
	// l'instance studiesSearchForm	
	public Result list(){	
		Form<StudiesSearchForm> studiesSearchFilledForm = filledFormQueryString(studiesSearchForm, StudiesSearchForm.class);
		StudiesSearchForm studiesSearchForm = studiesSearchFilledForm.get();
		//Logger.debug(studiesSearchForm.state);
		Query query = getQuery(studiesSearchForm);
		MongoDBResult<AbstractStudy> results = mongoDBFinder(studiesSearchForm, query);				
		List<AbstractStudy> studiesList = results.toList();
		if(studiesSearchForm.datatable){
			return ok(Json.toJson(new DatatableResponse<AbstractStudy>(studiesList, studiesList.size())));
		}else{
			return ok(Json.toJson(studiesList));
		}
	}	
	
	private Query getQuery(StudiesSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.projCode)) { //all
			queries.add(DBQuery.in("projectCode", form.projCode));
		}	
		if (CollectionUtils.isNotEmpty(form.stateCodes)) { //all
			queries.add(DBQuery.in("state.code", form.stateCodes));
		}
		
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
	
	private AbstractStudy getStudy(String code) {
		AbstractStudy study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, AbstractStudy.class, code);
		return study;
	}
	
	public Result update(String code) {
		//Get Submission from DB 
		AbstractStudy study = getStudy(code);
		Form<AbstractStudy> filledForm = getFilledForm(studyForm, AbstractStudy.class);
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	

		if (study == null) {
			//return badRequest("Study with code "+code+" not exist");
			ctxVal.addErrors("study ", " not exist");
			return badRequest(filledForm.errorsAsJson());
		}
		AbstractStudy studyInput = filledForm.get();
		if (code.equals(studyInput.code)) {	
			ctxVal.setUpdateMode();
			ctxVal.getContextObjects().put("type","sra");
			studyInput.traceInformation.setTraceInformation(getCurrentUser());
			studyInput.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				Logger.info("Update study state "+studyInput.state.code);
				MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, studyInput);
				return ok(Json.toJson(studyInput));
			}else {
				return badRequest(filledForm.errorsAsJson());
			}
		} else {
			//return badRequest("study code are not the same");
			ctxVal.addErrors("study " + code, "study code  " + code + " and studyInput.code "+ studyInput.code + "are not the same");
			return badRequest(filledForm.errorsAsJson());
		}	
	}


}
