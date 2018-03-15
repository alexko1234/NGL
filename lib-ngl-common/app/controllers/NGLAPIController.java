package controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.cea.ig.DBObject;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.mongo.DBObjectConvertor;
import fr.cea.ig.ngl.APINGLController;
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.NGLController;
import fr.cea.ig.ngl.dao.GenericMongoDAO;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.support.NGLForms;
import play.data.validation.ValidationError;
import play.mvc.Result;

/**
 * Contains Generic Methods of API controllers 
 * like head()
 * @author ajosso
 *
 * @param <T> GenericAPI
 * @param <R> GenericMongoDAO
 * @param <S> DBObject
 */
@Historized
public abstract class NGLAPIController<T extends GenericAPI<R,S>, R extends GenericMongoDAO<S>, S extends DBObject> 
				extends NGLController implements NGLForms, DBObjectConvertor {

	private final T api;
	
	public T api() {
		return api;
	}

	public NGLAPIController(NGLApplication app, T api) {
		super(app);
		this.api = api;
	}
	
	/**
	 * if object exists returns Status 200 OK <br>
	 * else returns Status 404 NOT FOUND
	 * @param code
	 * @return 
	 */
	@Authenticated
	@Authorized.Read
	public Result head(String code) {
		if(! api().isObjectExist(code)) {
			return notFound();
		} else {
			return ok();
		}
	}
	
	// Mandatory methods
	public abstract Result list();
	public abstract Result get(String code);
	public abstract Result save();
	public abstract Result update(String code) ;

	public Map<String, List<ValidationError>> mapErrors(List<ValidationError> formErrors) {
		Map<String, List<ValidationError>> map = new TreeMap<String, List<ValidationError>>(); 
		formErrors.forEach(ve -> {
			if(map.containsKey(ve.key())) {
				map.get(ve.key()).add(ve);
			} else {
				map.put(ve.key(), Arrays.asList(ve));
			}
		});
		return map;
	}
}
