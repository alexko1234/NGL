package controllers.commons.api;

import java.util.List;

import models.laboratory.common.description.ObjectType.CODE;
import models.laboratory.common.instance.Resolution;
import controllers.ListForm;

public class ResolutionConfigsSearchForm extends ListForm{
	
	public String typeCode;
	public List<String> typeCodes;
	
	public String objectTypeCode;
	public List<String> objectTypeCodes;
	
}