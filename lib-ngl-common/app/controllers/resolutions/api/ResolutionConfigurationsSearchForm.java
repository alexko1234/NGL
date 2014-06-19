package controllers.resolutions.api;

import java.util.List;

import models.laboratory.common.description.ObjectType.CODE;
import models.laboratory.resolutions.instance.Resolution;
import controllers.ListForm;

public class ResolutionConfigurationsSearchForm extends ListForm{
	
	public String typeCode;
	public List<String> typeCodes;
	
	public String objectTypeCode;
	public List<String> objectTypeCodes;
	
}