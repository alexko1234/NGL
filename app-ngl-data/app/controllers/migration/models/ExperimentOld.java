package controllers.migration.models;

import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.reagent.instance.ReagentUsed;
import controllers.migration.models.experiment.AtomicTransfertMethodOld;
import fr.cea.ig.DBObject;

public class ExperimentOld extends DBObject{
		

		public String typeCode;
		public String categoryCode;
		
		public TraceInformation traceInformation;
		public Map<String,PropertyValue> experimentProperties;
		
		public Map<String, PropertyValue> instrumentProperties;
		
		public InstrumentUsed instrument;
		public String protocolCode;

		public State state;
		
		public Map<Integer,AtomicTransfertMethodOld> atomicTransfertMethods; 
		
		public List<ReagentUsed> reagents;
		
		public List<Comment> comments;
		
		public Set<String> projectCodes;
		
		public Set<String> sampleCodes;
		
		public Set<String> inputContainerSupportCodes;
		
		public Set<String> outputContainerSupportCodes;

	
}
