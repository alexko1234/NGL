package services.io.reception;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

//import play.Logger;
import validation.ContextValidation;
import validation.IValidation;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

/**
 * Class to map a line of Excel or CVS file to a DBObject : sample, support, container, etc.
 * 
 * @author galbini
 *
 * @param <T> DBOject subclass to map 
 */
public abstract class Mapping<T extends DBObject> {

	private static final play.Logger.ALogger logger = play.Logger.of(Mapping.class);
	
	public enum Keys {
		sample,
		support,
		container
	};
	
	// Seems that it's supposed to be 3 maps and not 1.
	protected Map<String, Map<String, DBObject>> objects;
	protected Map<String, ? extends AbstractFieldConfiguration> configuration;
	protected Action action;
	protected ContextValidation contextValidation;
	protected String collectionName;
	protected Class<T> type;
	private Keys key;
	
	// TODO: fix doc generation error for unqualified parameter type (Keys -> Mapping.Keys)
	protected Mapping(Map<String, Map<String, DBObject>> objects, 
			          Map<String, ? extends AbstractFieldConfiguration> configuration, 
			          Action action,
			          String collectionName, 
			          Class<T> type, 
			          Mapping.Keys key, 
			          ContextValidation contextValidation) {
//		super();
		this.objects           = objects;
		this.configuration     = configuration;
		this.action            = action;
		this.contextValidation = contextValidation;
		this.collectionName    = collectionName;
		this.type              = type;
		this.key               = key;
	}
	
	/*
	 * convert a file line in DBObject
	 * @param rowMap
	 * @return
	 */
	public T convertToDBObject(Map<Integer, String> rowMap) throws Exception {
		T object = type.newInstance();
		if (Action.update.equals(action)) {
			object = get(object, rowMap, true);
		} else if(Action.save.equals(action)) {
			T objectInDB = get(object, rowMap, false);
			if (objectInDB != null) {
				contextValidation.addErrors("Error", "error.objectexist", type.getSimpleName(), objectInDB.code);
			} else if (object.code != null) {
				// TODO: use properly typed separate object collections so the cast is not needed
				T objectInObjects = (T)objects.get(key.toString()).get(object.code);
				if (objectInObjects != null) {
					object = objectInObjects;
				}
			}
		}
		if (object != null) {
			Field[] fields = type.getFields();
			for (Field field : fields) {
				populateField(field, object, rowMap);			
			}
			update(object);
		}
		return object;
	}
	
	/*
	 * Update the current object alone without any information from other object
	 * @param object
	 */
	protected abstract void update(T object) ;

	/*
	 * Add missing property from otehr objectType
	 * @param c
	 */
	public abstract void consolidate(T object);
	
	public void synchronizeMongoDB(DBObject c){
		if (Action.save.equals(action)) {
			MongoDBDAO.save(collectionName, c);
		} else if(Action.update.equals(action)) {
			MongoDBDAO.update(collectionName, c);
		}		
	}
	
	public void rollbackInMongoDB(DBObject c){
		if (Action.save.equals(action) && c._id == null) { //Delete sample and support if already exist !!!!
			MongoDBDAO.deleteByCode(collectionName, c.getClass(), c.code);
		} else if(Action.update.equals(action)) {
			//replace by old version of the object
		}		
	}
	
	public void validate(DBObject c) {
		ContextValidation cv = new ContextValidation(contextValidation.getUser());
		cv.setRootKeyName(contextValidation.getRootKeyName());
		cv.addKeyToRootKeyName(c.code);
		cv.setMode(cv.getMode());
		((IValidation)c).validate(cv);
		if (cv.hasErrors()) {
			contextValidation.addErrors(cv.errors);
		}
		cv.removeKeyFromRootKeyName(c.code);	
	}
	
	protected void populateField(Field field, DBObject dbObject, Map<Integer, String> rowMap) {
		if (configuration.containsKey(field.getName())) {
			AbstractFieldConfiguration fieldConfiguration = configuration.get(field.getName());
			try {
				fieldConfiguration.populateField(field, dbObject, rowMap, contextValidation, action);
			} catch (Exception e) {
				logger.error("Error", e.getMessage(), e);
				contextValidation.addErrors("Error", e.getMessage());
				throw new RuntimeException(e);
			}
		}			
	}
	
	protected T get(T object, Map<Integer, String> rowMap, boolean errorIsNotFound) {
		try {
			AbstractFieldConfiguration codeConfig = configuration.get("code");
			if (codeConfig != null) {
				codeConfig.populateField(object.getClass().getField("code"), object, rowMap, contextValidation, action);
				if (object.code != null) {
					String code = object.code;
					object = MongoDBDAO.findByCode(collectionName, type, object.code);	
					if (errorIsNotFound && object == null) {
						contextValidation.addErrors("Error", "not found "+type.getSimpleName()+" for code "+code);
					}
				} else if (codeConfig.required) {
					contextValidation.addErrors("Error", "not found "+type.getSimpleName()+" code !!!");
				} else {
					object = null;
				}
			} else {
				object = null;
			}
		} catch (Exception e) {
			logger.error("Error", e.getMessage(), e);
			contextValidation.addErrors("Error", e.getMessage());
			throw new RuntimeException(e);
		}
		return object;
	}

	protected ContainerSupport getContainerSupport(String code) {
		if (objects.containsKey("support")) {
			ContainerSupport cs = (ContainerSupport)objects.get("support").get(code);
			return cs;
		} else {
			throw new RuntimeException("Support must be load from Excel file, check configuration");
		}
	}
	
	protected Sample getSample(String code) {
		Sample sample = null;
		if (objects.containsKey("sample")) {
			sample = (Sample)objects.get("sample").get(code);			
		} else {
			objects.put("sample", new TreeMap<String, DBObject>());
		}
		if (sample == null) {
			sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, code);
			objects.get("sample").put(code, sample);
		}
		return sample;
	}
	
}
