package models.laboratory.run.instance;

import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.utils.InstanceHelpers;
import fr.cea.ig.DBObject;
public class Run extends DBObject {
		
	public TraceInformation traceInformation;
	public String typeCode;
	
	public Date transfertStartDate;
	public Date transfertEndDate;
	public Boolean dispatch = Boolean.FALSE;
	
	public String containerSupportCode; //id flowcell
	
	public TBoolean abort = TBoolean.UNSET;
	public Date abortDate;
	
	public Map<String, PropertyValue> properties = InstanceHelpers.getLazyMapPropertyValue();

	public InstrumentUsed instrumentUsed;
	public List<Lane> lanes;

	/*
	 	nbClusterIlluminaFilter
	 	nbCycle
	 	nbClusterTotal
	 	nbBase
	 	flowcellPosition
	 	rtaVersion
	 	flowcellVersion
	 	controlLane
	 	mismatch
	 */
	
	/*
	id du depot flowcell ???
	id du type de sequençage ???
	*/

}
