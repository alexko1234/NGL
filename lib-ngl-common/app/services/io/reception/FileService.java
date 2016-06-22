package services.io.reception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


import play.Logger;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ExcelFieldConfiguration;
import models.laboratory.reception.instance.ObjectFieldConfiguration;
import models.laboratory.reception.instance.PropertiesFieldConfiguration;
import models.laboratory.reception.instance.PropertyValueFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.instance.Sample;
import services.io.reception.mapping.ContainerMapping;
import services.io.reception.mapping.SampleMapping;
import services.io.reception.mapping.SupportMapping;
import validation.ContextValidation;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public abstract class FileService {
	
	protected Map<Integer, String> headerByIndex = new HashMap<Integer,String>();
	
	protected ReceptionConfiguration configuration;
	protected PropertyFileValue fileValue;
	protected ContextValidation contextValidation;
	
	private Map<String, Mapping<? extends DBObject>> mappings = new HashMap<String,Mapping<? extends DBObject>>();
	
	private Map<String, Map<String, DBObject>> objects = new HashMap<String,Map<String, DBObject>>();
	
	public Map<String, Map<String, DBObject>> getObjects() {
		return objects;
	}

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
		
		if(Mapping.Keys.sample.toString().equals(objectType)){
			return new SampleMapping(objects, configuration.configs.get(objectType), configuration.action, contextValidation);
		}else if(Mapping.Keys.support.toString().equals(objectType)){
			return new SupportMapping(objects, configuration.configs.get(objectType), configuration.action, contextValidation);
		}else if("container".equals(objectType)){
			return new ContainerMapping(objects, configuration.configs.get(objectType), configuration.action, contextValidation);
		}
		else{
			contextValidation.addErrors("Error", "Mapping : "+objectType);
			throw new UnsupportedOperationException("Mapping : "+objectType);
		}
	}
	 
	/**
	 * analyse one line of the file
	 * @param rowMap
	 */
	protected void treatLine(Map<Integer, String> rowMap) {
		
		Set<String> objectTypes = configuration.configs.keySet();
		
		objectTypes.stream().forEach(s -> {
			try {
				DBObject dbObject = mappings.get(s).convertToDBObject(rowMap);
				if (null != dbObject.code && !objects.get(s).containsKey(dbObject.code)){
					objects.get(s).put(dbObject.code, dbObject);
				}else{
					Logger.warn(s+" already load from another line");
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	/**
	 * Consolidate the object obtain after file parsing
	 */
	protected void consolidateObjects() {
		//First consolidate container
		if(configuration.configs.containsKey(Mapping.Keys.container.toString())){
			Map<String, DBObject> containers = objects.get(Mapping.Keys.container.toString());
			containers.values().forEach(c -> {
				((ContainerMapping)mappings.get(Mapping.Keys.container.toString())).consolidate((Container)c);
				
			});
		}
		//Second consolodate support
		if(configuration.configs.containsKey(Mapping.Keys.support.toString())){
			Map<String, DBObject> supports = objects.get(Mapping.Keys.support.toString());
			supports.values().forEach(c -> {
				((SupportMapping)mappings.get(Mapping.Keys.support.toString())).consolidate((ContainerSupport)c);
				
			});
		}
		
		//normally not necessary to consolidate sample
		
	}
	/**
	 * Save or update objects in mongodb
	 */
	protected void saveObjects() {
		//First sampe if needed
		if(Action.save.equals(configuration.action)){
			contextValidation.setCreationMode();
		}else{
			contextValidation.setUpdateMode();
		}
		if(saveObjectsForKey(Mapping.Keys.sample.toString())){
			if(saveObjectsForKey(Mapping.Keys.support.toString())){
				saveObjectsForKey(Mapping.Keys.container.toString());		
				rollbackObjectIFNeeded(Mapping.Keys.sample.toString(),Mapping.Keys.support.toString());
			}else{
				rollbackObjectIFNeeded(Mapping.Keys.sample.toString()); //??? Good idea ???
			}			
		}
		
	}

	private void rollbackObjectIFNeeded(String...keys) {
		Arrays.asList(keys).forEach(key ->{
			if(contextValidation.hasErrors() && configuration.configs.containsKey(key)){
				Map<String, DBObject> dbobjects = objects.get(key);
				Mapping<? extends DBObject> mapping = mappings.get(key);
				dbobjects.values().forEach(o -> {
					mapping.rollbackInMongoDB(o);				
				});
			}
		});
		
	}

	/**
	 * Save objects only it not error
	 * @param key
	 */
	private boolean saveObjectsForKey(String key) {
		if(!contextValidation.hasErrors() && configuration.configs.containsKey(key)){
			Map<String, DBObject> dbobjects = objects.get(key);
			Mapping<? extends DBObject> mapping = mappings.get(key);
			
			contextValidation.addKeyToRootKeyName(key);
			dbobjects.values().forEach(o -> {
				mapping.validate(o);				
			});
			contextValidation.removeKeyFromRootKeyName(key);
			
			if(!contextValidation.hasErrors()){
				dbobjects.values().forEach(c -> {
					mapping.synchronizeMongoDB(c);					
				});
			}
		}
		return !contextValidation.hasErrors();
	}
	
	/**
	 * Update HeaderLabel in ExcelFieldConfiguration to have a good error message
	 */
	protected void updateHeaderConfiguration() {
		Set<String> objectTypes = configuration.configs.keySet();
		objectTypes.stream().forEach(s -> {
			Map<String, ? extends AbstractFieldConfiguration> fieldConfigurations = configuration.configs.get(s);
			Set<String> propertyNames = configuration.configs.get(s).keySet();
			propertyNames.stream().forEach(pName ->{
				updateAbstractFieldConfigurationHeader(fieldConfigurations.get(pName));
			});
		});
	}

	private void updateAbstractFieldConfigurationHeader(AbstractFieldConfiguration afc) {
		if(ExcelFieldConfiguration.class.isAssignableFrom(afc.getClass())){
			updateExcelConfigurationHeader(afc);
		}else if(PropertiesFieldConfiguration.class.isAssignableFrom(afc.getClass())){
			PropertiesFieldConfiguration pfc = (PropertiesFieldConfiguration)afc;
			Set<String> propertyNames = pfc.configs.keySet();
			propertyNames.stream().forEach(_pName ->{
				updateAbstractFieldConfigurationHeader(pfc.configs.get(_pName));
			});
		}else if(PropertyValueFieldConfiguration.class.isAssignableFrom(afc.getClass())){
			PropertyValueFieldConfiguration pvfc = (PropertyValueFieldConfiguration)afc;
			updateAbstractFieldConfigurationHeader(pvfc.value);
			if(null != pvfc.unit)
				updateAbstractFieldConfigurationHeader(pvfc.unit);
		}else if(ObjectFieldConfiguration.class.isAssignableFrom(afc.getClass())){
			@SuppressWarnings("rawtypes")
			ObjectFieldConfiguration ofc = (ObjectFieldConfiguration)afc;
			Set<String> propertyNames = ofc.configs.keySet();
			propertyNames.stream().forEach(_pName ->{
				updateAbstractFieldConfigurationHeader((AbstractFieldConfiguration) ofc.configs.get(_pName));
			});
		}
	}

	private void updateExcelConfigurationHeader(AbstractFieldConfiguration afc) {
		ExcelFieldConfiguration efc = (ExcelFieldConfiguration)afc;
		if(this.headerByIndex.containsKey(efc.cellPosition)){
			efc.headerValue = this.headerByIndex.get(efc.cellPosition);
		}else{
			contextValidation.addErrors("Headers","not found header for cell position "+efc.cellPosition);
		}
	}
	
	
	public abstract void analyse();
	
}
