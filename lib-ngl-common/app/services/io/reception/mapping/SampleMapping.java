package services.io.reception.mapping;

import java.util.Map;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import services.io.reception.Mapping;
import validation.ContextValidation;
import fr.cea.ig.DBObject;

public class SampleMapping extends Mapping<Sample> {
	/**
	 * 
	 * @param objects : list of all db objects need by type samples, supports, containers
	 * @param configuration : the filed configuration for the current type
	 * @param action
	 * @param contextValidation
	 */
	public SampleMapping(Map<String, Map<String, DBObject>> objects, Map<String, ? extends AbstractFieldConfiguration> configuration, Action action, ContextValidation contextValidation) {
		super(objects, configuration, action, InstanceConstants.SAMPLE_COLL_NAME, Sample.class, Mapping.Keys.sample, contextValidation);
	}
	
	
	protected void update(Sample sample) {
		
		if(Action.update.equals(action)){
			sample.traceInformation.setTraceInformation(contextValidation.getUser());
		}else{
			sample.traceInformation = new TraceInformation(contextValidation.getUser());
		}
		//TODO update categoryCode if not a code but a label.
		if(sample.categoryCode == null){
			sample.categoryCode = SampleType.find.findByCode(sample.typeCode).category.code;
		}
	}


	@Override
	public void consolidate(Sample sample) {
		// TODO Auto-generated method stub
		
	}	
	
	@Override
	public void synchronizeMongoDB(DBObject c){
		if(Action.save.equals(action)){
			Sample sample = (Sample)c;
			CodeHelper.getInstance().updateProjectSampleCodeIfNeeded(sample.projectCodes.iterator().next(), sample.code);
		}
		super.synchronizeMongoDB(c);
	}
}
