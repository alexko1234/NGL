package controllers.sra.configurations.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import models.sra.submit.sra.instance.*;
import models.sra.submit.util.SraCodeHelper;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.DocumentController;
import controllers.sra.submissions.api.SubmissionsSearchForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import validation.ContextValidation;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.State;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import models.sra.submit.common.instance.Submission;
import models.sra.submit.util.SraException;
import views.components.datatable.DatatableResponse;


public class Configurations extends DocumentController<Configuration>{

	final static Form<Configuration> configurationForm = form(Configuration.class);
	// declaration d'une instance configurationSearchForm qui permet de recuperer la liste des configurations => utilisee dans list()
	final static Form<ConfigurationsSearchForm> configurationsSearchForm = form(ConfigurationsSearchForm.class);

	final static Form<SubmissionsSearchForm> submissionsSearchForm = form(SubmissionsSearchForm.class);


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
			userConfiguration.state = new State("new", getCurrentUser());
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
	
	
	// methode list appelee avec url suivante :
	//localhost:9000/api/sra/configurations?datatable=true&paginationMode=local&projCode=BCZ
	// url construite dans services.js 
	//search : function(){
	//	this.datatable.search({projCode:this.form.projCode, state:'new'});
	//},
	public Result list(){	
		Form<ConfigurationsSearchForm> configurationsSearchFilledForm = filledFormQueryString(configurationsSearchForm, ConfigurationsSearchForm.class);
		ConfigurationsSearchForm configurationsSearchForm = configurationsSearchFilledForm.get();
		//Logger.debug(submissionsSearchForm.state);
		Query query = getQuery(configurationsSearchForm);
		MongoDBResult<Configuration> results = mongoDBFinder(configurationsSearchForm, query);				
		List<Configuration> configurationsList = results.toList();
		if(configurationsSearchForm.datatable){
			return ok(Json.toJson(new DatatableResponse<Configuration>(configurationsList, configurationsList.size())));
		}else{
			return ok(Json.toJson(configurationsList));
		}
	}

	
	private Query getQuery(ConfigurationsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.projCode)) { 
			queries.add(DBQuery.in("projectCode", form.projCode));
		}	
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
	
	private Configuration getConfiguration(String code) {
		Configuration configuration = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, code);
		return configuration;
	}

	public Result update(String code) {
		//Get Submission from DB 
		Configuration configuration = getConfiguration(code);
		Form<Configuration> filledForm = getFilledForm(configurationForm, Configuration.class);
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	

		if (configuration == null) {
			//return badRequest("Configuration with code "+code+" not exist");
			ctxVal.addErrors("configuration ", " not exist");
			return badRequest(filledForm.errorsAsJson());
		}
		Configuration configurationInput = filledForm.get();
		if (code.equals(configurationInput.code)) {	
			ctxVal.setUpdateMode();
			ctxVal.getContextObjects().put("type","sra");
			configurationInput.traceInformation.setTraceInformation(getCurrentUser());
			configurationInput.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				Logger.info("Update configuration state "+configurationInput.state.code);
				MongoDBDAO.update(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, configurationInput);
				return ok(Json.toJson(configurationInput));
			}else {
				return badRequest(filledForm.errorsAsJson());
			}
		}else{
			//return badRequest("configuration code are not the same");
			ctxVal.addErrors("configuration " + code, "configuration code  " + code + " and configurationInput.code "+ configurationInput.code + "are not the same");
			return badRequest(filledForm.errorsAsJson());
		}	
	}

}
