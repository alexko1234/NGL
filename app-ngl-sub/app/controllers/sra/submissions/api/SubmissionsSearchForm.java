package controllers.sra.submissions.api;

import java.util.List;

import models.laboratory.common.instance.State;
import controllers.ListForm;

public class SubmissionsSearchForm extends ListForm{
	public List<String> projCodes; // meme nom que dans la vue et les services .js
	public String stateCode;
	
}
