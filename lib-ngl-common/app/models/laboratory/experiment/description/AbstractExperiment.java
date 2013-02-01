package models.laboratory.experiment.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.instrument.description.InstrumentUsedType;

public abstract class AbstractExperiment extends CommonInfoType{

	//Relationship accessible by the parent table in the database
		public List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		public List<Protocol> protocols = new ArrayList<Protocol>();
}
