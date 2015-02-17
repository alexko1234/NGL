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

public class Studies extends DocumentController<Study>{

	final static Form<Study> studyForm = form(Study.class);
	
	public Studies() {
		super(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class);
	}

	public Result listTypes()
	{
		return ok(Json.toJson(toListObjects(VariableSRA.mapExistingStudyType)));
	}
	
	public Result save()
	{
		Form<Study> filledForm = getFilledForm(studyForm, Study.class);
		Study studyToSave = filledForm.get();
		Logger.debug("Study to save title : "+studyToSave.title+", projectCode "+studyToSave.projectCode+", study type "+studyToSave.existingStudyType+", abstract "+studyToSave.studyAbstract+", description "+studyToSave.description);
		return ok("Successful save study");
	}
	
	private List<ListObject> toListObjects(Map<String, String> map){
		List<ListObject> lo = new ArrayList<ListObject>();
		for(String key : map.keySet()){
			lo.add(new ListObject(key, map.get(key)));
		}
		return lo;
	}
}
