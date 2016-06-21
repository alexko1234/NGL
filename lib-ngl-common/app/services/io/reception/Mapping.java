package services.io.reception;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import play.Logger;
import validation.ContextValidation;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

/**
 * Classe to map a line of Excel or CVS file to an DBObject : sample, support, container, etc.
 * @author galbini
 *
 * @param <T>
 */
public abstract class Mapping<T extends DBObject> {

	protected Map<String, Map<String, DBObject>> objects;
	protected Map<String, ? extends AbstractFieldConfiguration> configuration;
	protected Action action;
	protected ContextValidation contextValidation;
	protected String collectionName;
	protected Class<T> type;
	
	protected Mapping(Map<String, Map<String, DBObject>> objects, Map<String, ? extends AbstractFieldConfiguration> configuration, Action action,
			String collectionName, Class<T> type, ContextValidation contextValidation) {
		super();
		this.objects = objects;
		this.configuration = configuration;
		this.action = action;
		this.contextValidation = contextValidation;
		this.collectionName =collectionName;
		this.type = type;
	}
	
	/**
	 * convert a file line in DBObject
	 * @param rowMap
	 * @return
	 */
	public T convertToDBObject(Map<Integer, String> rowMap) throws Exception{
		T object = type.newInstance();
		if(Action.update.equals(action)){
			object = get(object, rowMap);
		}		
		Field[] fields = type.getFields();
		for(Field field : fields){
			populateField(field, object, rowMap);			
		}
		update(object);
		return object;
	}
	
	/**
	 * Update the current object alone without any information from other object
	 * @param object
	 */
	protected abstract void update(T object) ;

	/**
	 * Add missing property from otehr objectType
	 * @param c
	 */
	public abstract void consolidate(T c);
	
	protected void populateField(Field field, DBObject dbObject, Map<Integer, String> rowMap) {
		if(configuration.containsKey(field.getName())){
			AbstractFieldConfiguration fieldConfiguration = configuration.get(field.getName());
			try {
				fieldConfiguration.populateField(field, dbObject, rowMap, contextValidation);
			} catch (Exception e) {
				Logger.error("Error", e.getMessage(), e);
				contextValidation.addErrors("Error", e.getMessage());
				throw new RuntimeException(e);
			}
		}			
	}
	
	protected T get(T object, Map<Integer, String> rowMap) {
		try {
			AbstractFieldConfiguration codeConfig = configuration.get("code");
			codeConfig.populateField(object.getClass().getField("code"), object, rowMap, contextValidation);
			if(null != object.code){
				object = MongoDBDAO.findByCode(collectionName, type, object.code);						
			}else{
				contextValidation.addErrors("Error", "not found "+type.getName()+" code !!!");
			}
		} catch (Exception e) {
			Logger.error("Error", e.getMessage(), e);
			contextValidation.addErrors("Error", e.getMessage());
			throw new RuntimeException(e);
		}
		
		return object;
	}

	protected ContainerSupport getContainerSupport(String code) {
		if(objects.containsKey("support")){
			ContainerSupport cs = (ContainerSupport)objects.get("support").get(code);
			return cs;
		}else{
			throw new RuntimeException("Support must be load form Excel file, check configuration");
		}
	}
	
	protected Sample getSample(String code) {
		Sample sample = null;
		if(objects.containsKey("sample")){
			sample = (Sample)objects.get("sample").get(code);			
		}else{
			objects.put("sample", new TreeMap<String, DBObject>());
		}
		if(null == sample){
			sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, code);
			objects.get("sample").put(code, sample);
		}
		return sample;
	}
	
}
