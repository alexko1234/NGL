package controllers.studies.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.DocumentController;

import models.sra.submit.sra.instance.*;
import models.sra.submit.util.SraCodeHelper;
//import models.sra.submit.util.VariableSRA;
import fr.cea.ig.MongoDBDAO;
import validation.ContextValidation;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.State;
import models.utils.CodeHelper;


public class Studies extends DocumentController<Study>{

	final static Form<Study> studyForm = form(Study.class);
	
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
				userStudy.code = SraCodeHelper.getInstance().generateConfigurationCode(userStudy.projectCode);
				userStudy.validate(contextValidation);
				
				if(contextValidation.errors.size()==0) {
					MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, userStudy);
				} else {
					return badRequest("study no valid : " + contextValidation.errors);
				}
			} else {
				return badRequest("study with id "+userStudy._id +" already exist");
			}
		
		return ok("Successful save study " + userStudy.code);
	}
	
}
