package controllers.migration.models;

import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.run.instance.ReadSet;


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
