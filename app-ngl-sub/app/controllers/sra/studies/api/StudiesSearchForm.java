package controllers.sra.studies.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import controllers.ListForm;

public class StudiesSearchForm extends ListForm{

	//public String projCode;
	public List<String> projCodes = new ArrayList<String>();
	public List<String> stateCodes = new ArrayList<String>();
	public Boolean confidential = null;
	public String stateCode = null;
}
