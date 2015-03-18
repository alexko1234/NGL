package controllers.studies.api;

import static play.data.Form.form;

import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.submit.common.instance.Study;
import models.sra.submit.util.SraCodeHelper;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;

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
					return badRequest("study no valid : " + contextValidation.errors);
				}
			} else {
				return badRequest("study with id " + userStudy._id + " already exist");
			}
		
		return ok("Successful save study " + userStudy.code);
	}
	// Renvoie le Json correspondant à la liste des study ayant le projectCode indique dans la variable du formulaire projectCode et stockee dans
	// l'instance studiesSearchForm
	public Result list() {	
		Form<StudiesSearchForm> studiesFilledForm = filledFormQueryString(studiesSearchForm, StudiesSearchForm.class);
		StudiesSearchForm studiesSearchForm = studiesFilledForm.get();
		MongoDBResult<Study> results = MongoDBDAO.find(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, DBQuery.is("projectCode", studiesSearchForm.projCode));
		List<Study> studys = results.toList();
		return ok(Json.toJson(studys));
	}	
}
