package services.io.reception;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import play.Logger;
import fr.cea.ig.DBObject;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.reception.instance.ReceptionConfiguration;
import services.io.reception.mapping.SampleMapping;
import validation.ContextValidation;

public abstract class FileService {
	
	protected Map<Integer, String> headerByIndex = new HashMap<Integer,String>();
	
	protected ReceptionConfiguration configuration;
	protected PropertyFileValue fileValue;
	protected ContextValidation contextValidation;
	
	private Map<String, Mapping<? extends DBObject>> mappings = new HashMap<String,Mapping<? extends DBObject>>();
	
	private Map<String, Map<String, DBObject>> objects = new HashMap<String,Map<String, DBObject>>();
	
	protected FileService(ReceptionConfiguration configuration,
			PropertyFileValue fileValue, ContextValidation contextValidation) {
		this.configuration = configuration;
		this.fileValue = fileValue;
		this.contextValidation = contextValidation;
		Set<String> objectTypes = configuration.configs.keySet();
		objectTypes.stream().forEach(s -> {
			objects.put(s, new TreeMap<String, DBObject>());
			mappings.put(s, mappingFactory(s));						
		});
				
	}

	private Mapping<? extends DBObject> mappingFactory(String objectType) {
		
		if("sample".equals(objectType)){
			return new SampleMapping(objects, configuration.configs.get(objectType), configuration.action, contextValidation);
		}else{
			contextValidation.addErrors("Error", "Mapping : "+objectType);
			throw new UnsupportedOperationException("Mapping : "+objectType);
		}
	}
	 
	protected void treatLine(Map<Integer, String> rowMap) {
		
		Set<String> objectTypes = configuration.configs.keySet();
		
		objectTypes.stream().forEach(s -> {
			mappings.get(s).convertToDBObject(rowMap);			
		});
	}
	
	public abstract void analyse();
	
}
