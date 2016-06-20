package services.io.reception;

import java.util.Map;

import validation.ContextValidation;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import fr.cea.ig.DBObject;

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
	
	protected Mapping(Map<String, Map<String, DBObject>> objects, Map<String, ? extends AbstractFieldConfiguration> configuration, Action action,
			ContextValidation contextValidation) {
		super();
		this.objects = objects;
		this.configuration = configuration;
		this.action = action;
		this.contextValidation = contextValidation;
	}
	
	public abstract DBObject convertToDBObject(Map<Integer, String> rowMap);

}
