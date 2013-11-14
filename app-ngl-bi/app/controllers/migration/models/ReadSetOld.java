package controllers.migration.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.codehaus.jackson.annotate.JsonIgnore;


import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Validation;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import validation.IValidation;
import play.data.validation.Constraints.Required;
import validation.ContextValidation;


public class ReadSetOld extends ReadSet{

	public String stateCode;
	
	public TBoolean validProduction = TBoolean.UNSET;
	public Date validProductionDate;
	public TBoolean validBioinformatic = TBoolean.UNSET;
	public Date validBioinformaticDate;
	public List<FileOld> files;
	
	//new field to remove
	public String sampleContainerCode;
}
