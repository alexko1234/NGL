package models.laboratory.experiment.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.instrument.description.InstrumentUsedType;

public class QualityControlType{

	public Long id;

	public List<Protocol> protocols; 
	
	public List<InstrumentUsedType> instrumentTypes;
	
	public CommonInfoType commonInfoType;

	
}
