package models.instance.run;

import java.util.Date;
import java.util.List;
import java.util.Map;

import models.instance.Utils;
import models.instance.common.PropertyValue;
import models.instance.common.TBoolean;
import models.instance.common.TraceInformation;
import models.instance.instrument.InstrumentUsed;
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
	
	public Map<String, PropertyValue> properties = Utils.getLazyMapPropertyValue();

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
