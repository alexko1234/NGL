package controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.commons.api.StateBatchElement;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.support.LoggerHolder;
import fr.cea.ig.ngl.NGLApplicationHolder;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.support.NGLForms;
import models.laboratory.common.instance.State;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Lang;
import play.mvc.Http;
import play.mvc.Result;
import views.components.datatable.DatatableBatchResponseElement;

//import play.Logger;
//import play.libs.Json;

public interface StateController extends NGLApplicationHolder, NGLForms, LoggerHolder {

	/**
	 * Update the state of a resource (retrieved by its code).
	 * @param code the code of the object to update
	 * @return 	   HTTP result
	 */
	@Authenticated
	@Authorized.Write
	default Result updateState(String code) {
		Form<State> stateForm = getNGLApplication().formFactory().form(State.class);
		try {
			Form<State> filledForm =  getFilledForm(stateForm, State.class);
			State state = filledForm.get();
			state.date = new Date();
			state.user = getCurrentUser();
			return okAsJson(updateStateImpl(code, state));
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
	
	@Authenticated
	@Authorized.Write
	default Result updateStateBatch() {
		try {
//			Logger.debug("StateController updateStateBatch");
			Form<StateBatchElement> batchElementForm =  getNGLApplication().formFactory().form(StateBatchElement.class);
			List<Form<StateBatchElement>> filledForms =  getFilledFormList(batchElementForm, StateBatchElement.class);
//			Logger.debug("StateController updateStateBatch filledForms 0 " + Json.toJson(filledForms.get(0).get()));
//			Logger.debug("StateController updateStateBatch filledForms 1 " + Json.toJson(filledForms.get(1).get()));
//			Logger.debug("StateController updateStateBatch batchElementForm " + Json.toJson(batchElementForm));
			final Lang lang = Http.Context.Implicit.lang();
			
//			for (int n = 0; n< filledForms.size(); n++){
//				StateBatchElement element = filledForms.get(n).get();
//				Logger.debug("StateController updateStateBatch " + element.data.code + " " + element.data.state.code + ", " + element.index);
//
//				State state = element.data.state;
//				state.date = new Date();
//				state.user = getCurrentUser();
//				
//				Object o = updateStateImpl(element.data.code, state);
//				Logger.debug("StateController updateStateBatch o : " + o.toString());
//			}
			
//			List<DatatableBatchResponseElement> response = filledForms.parallelStream()
			List<DatatableBatchResponseElement> response = filledForms.stream()
					.map(filledForm -> {
//						Logger.debug("StateController updateStateBatch in");
						StateBatchElement element = filledForm.get();
						State state = element.data.state;
						state.date = new Date();
						state.user = getCurrentUser();
						try {
//							Logger.debug("StateController updateStateBatch befor updateStateImpl");
							Object o = updateStateImpl(element.data.code, state);
//							Logger.debug("StateController updateStateBatch after updateStateImpl" + element.data.code + " " + state.code + ", " + element.index);
							return new DatatableBatchResponseElement(play.mvc.Http.Status.OK, o, element.index);
//							DatatableBatchResponseElement dbre = new DatatableBatchResponseElement(play.mvc.Http.Status.OK, o, element.index);
//							Logger.debug("StateController updateStateBatch " + Json.toJson(dbre.data));
//							return dbre;
						} catch (APIValidationException e) {
//							Logger.debug("StateController updateStateBatch APIValidationException");
							return new DatatableBatchResponseElement(play.mvc.Http.Status.BAD_REQUEST, errorsAsJson(lang, e.getErrors()), element.index);
						} catch (APIException e) {
//							Logger.debug("StateController updateStateBatch APIValidationException");
							return new DatatableBatchResponseElement(play.mvc.Http.Status.BAD_REQUEST, element.index);
//						} catch (Exception e) {
//							Logger.debug("StateController updateStateBatch Exception");
//							return new DatatableBatchResponseElement(play.mvc.Http.Status.BAD_REQUEST, element.index);
						}
					}).collect(Collectors.toList());
//			Logger.debug("StateController updateStateBatch okAsJson");
			return okAsJson(response);
		} catch (Exception e) {
//			Logger.error("StateController updateStateBatch error " + e.getMessage());
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}	
	
	/**
	 * These method defines the specific updateState behavior for each resource. 
	 * @param code 	code of resource object to update
	 * @param state new state of resource object
	 * @return
	 * @throws APIException
	 */
	public abstract Object updateStateImpl(String code, State state) throws APIException;
	
	public abstract JsonNode errorsAsJson(Map<String, List<ValidationError>> errors);
	public abstract JsonNode errorsAsJson(Lang lang, Map<String, List<ValidationError>> errors);
	public abstract Result nglGlobalBadRequest();
	public abstract String getCurrentUser();
	public abstract Result okAsJson(Object o);
	public abstract Result badRequestAsJson(Object o);

}
