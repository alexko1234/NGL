package models.laboratory.experiment.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.instrument.description.InstrumentUsedType;

/**
 * Parent class categories not represented by a table in the database
 * Database relationship for experiment with instrumentUsedType and protocol are represented in CommonInfoType table
 * @author ejacoby
 *
 */
public abstract class AbstractExperiment extends CommonInfoType{

	//Relationship accessible by the parent table in the database
		public List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		public List<Protocol> protocols = new ArrayList<Protocol>();
}
