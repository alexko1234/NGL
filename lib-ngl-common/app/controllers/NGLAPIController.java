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
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.NGLController;
import fr.cea.ig.ngl.dao.GenericMongoDAO;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APISemanticException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.support.NGLForms;
import fr.cea.ig.play.IGBodyParsers;
import play.data.validation.ValidationError;
import play.mvc.BodyParser;
import play.mvc.Result;

/**
 * Contains Generic Methods of API controllers 
 * like head()
 * @author ajosso
 *
 * @param <T> GenericAPI
 * @param <U> GenericMongoDAO
 * @param <V> DBObject
 */
@Historized
public abstract class NGLAPIController<T extends GenericAPI<U,V>, U extends GenericMongoDAO<V>, V extends DBObject> 
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
	 * If object exists returns Status 200 OK <br>
	 * else returns Status 404 NOT FOUND.
	 * @param code String 
	 * @return     Result HTTP result (200 or 404)
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
	
	/**
	 * These method defines the specific creation behavior for each resource. 
	 * {@link NGLAPIController#save()} wraps the call of this method. <br>
	 * We do not check here if form has errors because the API validates data. 
	 * 
	 * @return V the DBObject created
	 * @throws APIException exceptions during creation
	 */
	public abstract V saveImpl() throws APIException ;
	
	@Authenticated
	@Authorized.Write
	public Result save() {
		try {
			V object = saveImpl();
			return okAsJson(object);
		} catch (APIValidationException e) {
			getLogger().error(e.getMessage());
			if (e.getErrors() != null) {
				return badRequestAsJson(errorsAsJson(e.getErrors()));
			} else {
				return badRequestAsJson(e.getMessage());
			}
		} catch (APISemanticException e) {
			getLogger().error(e.getMessage());
			return badRequestAsJson("use PUT method to update");
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}
	
	/**
	 * These method defines the specific update behavior for each resource. 
	 * {@link #update(String)} wraps the call of this method. <br>
	 * We do not check here if form has errors because the API validates data. 
	 * 
	 * @param code String
	 * @return     V the DBObject created
	 * @throws Exception              global exception
	 * @throws APIException           exception from API
	 * @throws APIValidationException exception from API
	 */
	public abstract V updateImpl(String code) throws Exception, APIException, APIValidationException;
	
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	@Authenticated
	@Authorized.Write
	public Result update(String code) {
		try {
			V object = updateImpl(code);
			return okAsJson(object);
		} catch (APIValidationException e) {
			getLogger().error(e.getMessage());
			if(e.getErrors() != null) {
				return badRequestAsJson(errorsAsJson(e.getErrors()));
			} else {
				return badRequestAsJson(e.getMessage());
			}
		} catch (APIException e) {
			getLogger().error(e.getMessage());
			return badRequestAsJson(e.getMessage());
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}

	public Map<String, List<ValidationError>> mapErrors(List<ValidationError> formErrors) {
		Map<String, List<ValidationError>> map = new TreeMap<>(); 
		formErrors.forEach(ve -> {
			if(map.containsKey(ve.key())) {
				map.get(ve.key()).add(ve);
			} else {
				map.put(ve.key(), Arrays.asList(ve));
			}
		});
		return map;
	}

	/**
	 * @return badRequest Result with standard message
	 */
	protected Result nglGlobalBadRequest() {
		return badRequestAsJson("Error on server: contact support for more details");
	}

}
