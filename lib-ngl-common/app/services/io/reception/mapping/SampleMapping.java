package services.io.reception.mapping;

import java.lang.reflect.Field;
import java.util.Map;

import play.Logger;
import fr.cea.ig.DBObject;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.instance.Sample;
import services.io.reception.Mapping;
import validation.ContextValidation;

public class SampleMapping extends Mapping<Sample> {

	public SampleMapping(Map<String, Map<String, DBObject>> objects, Map<String, ? extends AbstractFieldConfiguration> configuration, Action action, ContextValidation contextValidation) {
		super(objects, configuration, action, contextValidation);
	}

	@Override
	public Sample convertToDBObject(Map<Integer, String> rowMap) {
		
		//TODO if update or create different
		Sample sample = new Sample();
		
		Field[] fields = Sample.class.getFields();
		
		for(Field field : fields){
			populateField(field, sample, rowMap);
		}
		
		
		return null;
	}

	private void populateField(Field field, DBObject dbObject,	Map<Integer, String> rowMap) {
		if(configuration.containsKey(field.getName())){
			AbstractFieldConfiguration fieldConfiguration = configuration.get(field.getName());
			try {
				fieldConfiguration.populateField(field, dbObject, fieldConfiguration, rowMap, contextValidation);
			} catch (Exception e) {
				Logger.error("Error", e.getMessage(), e);
				contextValidation.addErrors("Error", e.getMessage());
				throw new RuntimeException(e);
			}
		}			
	}

}
