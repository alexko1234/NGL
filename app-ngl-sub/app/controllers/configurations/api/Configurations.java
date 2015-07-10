package controllers.configurations.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import models.sra.submit.sra.instance.*;
import models.sra.submit.util.SraCodeHelper;
import models.utils.InstanceConstants;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import validation.ContextValidation;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.State;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class Configurations extends DocumentController<Configuration>{

	final static Form<Configuration> configurationForm = form(Configuration.class);
	final static Form<ConfigurationsSearchForm> configurationsSearchForm = form(ConfigurationsSearchForm.class);

	public Configurations() {
		super(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class);
	}

	
	public Result save() {
		Form<Configuration> filledForm = getFilledForm(configurationForm, Configuration.class);
		Configuration userConfiguration = filledForm.get();
		
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
		contextValidation.setCreationMode();	
		if (userConfiguration._id == null) {
			userConfiguration.traceInformation = new TraceInformation(); 
			userConfiguration.traceInformation.setTraceInformation(getCurrentUser());
			userConfiguration.state = new State("userValidate", getCurrentUser());
			userConfiguration.code = SraCodeHelper.getInstance().generateConfigurationCode(userConfiguration.projectCode);
			userConfiguration.validate(contextValidation);
			
			if(contextValidation.errors.size()==0) {
				MongoDBDAO.save(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, userConfiguration);
			} else {
				return badRequest(filledForm.errorsAsJson());
			}
		} else {
			filledForm.reject("configuration with id "+userConfiguration._id ," already exist");
			return badRequest(filledForm.errorsAsJson());
		}
		return ok(Json.toJson(userConfiguration.code));
	}

	
	/*public Result list() {	
		Form<ConfigurationsSearchForm> configurationsFilledForm = filledFormQueryString(configurationsSearchForm, ConfigurationsSearchForm.class);
		ConfigurationsSearchForm configurationsSearchForm = configurationsFilledForm.get();
		MongoDBResult<Configuration> results = MongoDBDAO.find(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, DBQuery.is("projectCode", configurationsSearchForm.projCode));
		List<Configuration> configurations = results.toList();
		return ok(Json.toJson(configurations));
	}
	*/
	public Result list() {	
		// On recupere un formulaire :
		//Form<ConfigurationsSearchForm> configurationsFilledForm = filledFormQueryString(configurationsSearchForm, ConfigurationsSearchForm.class);
		//On recupere un objet :
		ConfigurationsSearchForm configurationsFilledForm = filledFormQueryString(ConfigurationsSearchForm.class);
		Query query = getQuery(configurationsFilledForm);
		MongoDBResult<Configuration> results = mongoDBFinder(configurationsFilledForm, query);	
		List<Configuration> configurationsList = results.toList();
		return ok(Json.toJson(configurationsList));
	}
	
	private Query getQuery(ConfigurationsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.projCode)) { //all
			queries.add(DBQuery.in("projectCode", form.projCode));
		}	
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}

}
