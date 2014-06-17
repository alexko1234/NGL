package controllers.resolutions.api;

import models.laboratory.common.description.ObjectType.CODE;
import controllers.ListForm;

public class ResolutionsSearchForm extends ListForm{
	public String typeCode;
	
	public CODE objectTypeCode;
}
