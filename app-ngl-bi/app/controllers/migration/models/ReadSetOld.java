package controllers.migration.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;


import models.laboratory.common.instance.TBoolean;
import validation.IValidation;
import play.data.validation.Constraints.Required;
import validation.ContextValidation;


public class ReadSetOld implements IValidation{


	@Required
	public String code;
	@Required
	public String sampleContainerCode; //code bar de la banque ou est l'echantillon
	@Required
	public String sampleCode; //nom de l'ind / ech
	@Required
	public String projectCode;
	public TBoolean abort = TBoolean.UNSET;
	public Date abortDate;	
	@Required
	public String path;	
	public String archiveId;
	public Date archiveDate;
	@Valid
	public List<File> files;
	
	public Map<String, PropertyValueOld> properties= new HashMap<String, PropertyValueOld>();
	
	
	
	@Override
	public void validate(ContextValidation contextValidation) {
		
		
	}
}
