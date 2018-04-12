package controllers.sra.configurations.api;

// import static play.data.Form.form;
// import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import models.sra.submit.sra.instance.*;
import models.sra.submit.util.SraCodeHelper;
import models.utils.InstanceConstants;
//import play.Logger;
// import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.DocumentController;
// import controllers.sra.experiments.api.Experiments;
// import controllers.sra.submissions.api.SubmissionsSearchForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.mongo.DBQueryBuilder;
import fr.cea.ig.play.NGLContext;
import validation.ContextValidation;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.State;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

// import models.sra.submit.common.instance.Submission;
import models.sra.submit.util.SraException;
import views.components.datatable.DatatableResponse;
//import workflows.sra.submission.ConfigurationWorkflows;

public class Configurations extends DocumentController<Configuration> {
	
	private static final play.Logger.ALogger logger = play.Logger.of(Configurations.class);

	// final static Form<Configuration> configurationForm = form(Configuration.class);
	// declaration d'une instance configurationSearchForm qui permet de recuperer la liste des configurations => utilisee dans list()
	// final static Form<ConfigurationsSearchForm> configurationsSearchForm = form(ConfigurationsSearchForm.class);
	// final static Form<SubmissionsSearchForm> submissionsSearchForm = form(SubmissionsSearchForm.class);
	// final ConfigurationWorkflows configWorkflows = Spring.get BeanOfType(ConfigurationWorkflows.class);

	private final Form<Configuration>            configurationForm;
	private final Form<ConfigurationsSearchForm> configurationsSearchForm;
	// private final Form<SubmissionsSearchForm> submissionsSearchForm;
	// private final ConfigurationWorkflows configWorkflows;
	
	@Inject
	public Configurations(NGLContext ctx/*, ConfigurationWorkflows configWorkflows*/) {
		super(ctx,InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class);
		configurationForm        = ctx.form(Configuration.class);
		configurationsSearchForm = ctx.form(ConfigurationsSearchForm.class);
		// submissionsSearchForm    = ctx.form(SubmissionsSearchForm.class);
		// this.configWorkflows     = configWorkflows;
	}

	public Result save() {
		Form<Configuration> filledForm = getFilledForm(configurationForm, Configuration.class);
		Configuration userConfiguration = filledForm.get();
		
//		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm);
		contextValidation.setCreationMode();	
		if (userConfiguration._id == null) {
			userConfiguration.traceInformation = new TraceInformation(); 
			userConfiguration.traceInformation.setTraceInformation(getCurrentUser());
			State state = new State("NONE", getCurrentUser());
			// Ne pas passer par configWorkflows ici car setState possible si mode update si object existe deja
			//configWorkflows.setState(contextValidation, userConfiguration, state);
			userConfiguration.state = state;
			try {
				userConfiguration.code = SraCodeHelper.getInstance().generateConfigurationCode(userConfiguration.projectCodes);
			} catch (SraException e) {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(contextValidation.getErrors()));
			}
//			System.out.println (" !!!!!!!!!!! userConf.code = " + userConfiguration.code);
			logger.debug(" !!!!!!!!!!! userConf.code = " + userConfiguration.code);
			userConfiguration.validate(contextValidation);
			// if(contextValidation.errors.size()==0) {
			if (!contextValidation.hasErrors()) {
				MongoDBDAO.save(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, userConfiguration);
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(contextValidation.getErrors()));
			}
		} else {
//			filledForm.reject("configuration with id "+userConfiguration._id ," already exist");
//			return badRequest(filledForm.errorsAsJson( )); // legit, at least does seem
			contextValidation.addError("configuration with id "+userConfiguration._id ," already exist");
			return badRequest(errorsAsJson(contextValidation.getErrors()));
		}
		return ok(Json.toJson(userConfiguration.code));
	}
		
	// methode list appelee avec url suivante :
	//localhost:9000/api/sra/configurations?datatable=true&paginationMode=local&projCode=BCZ
	// url construite dans services.js 
	//search : function(){
	//	this.datatable.search({projCode:this.form.projCode, state:'N'});
	//},
	public Result list(){	
		Form<ConfigurationsSearchForm> configurationsSearchFilledForm = filledFormQueryString(configurationsSearchForm, ConfigurationsSearchForm.class);
		ConfigurationsSearchForm configurationsSearchForm = configurationsSearchFilledForm.get();
		//Logger.debug(submissionsSearchForm.state);
		Query query = getQuery(configurationsSearchForm);
		MongoDBResult<Configuration> results = mongoDBFinder(configurationsSearchForm, query);				
		List<Configuration> configurationsList = results.toList();
		if (configurationsSearchForm.datatable)
			return ok(Json.toJson(new DatatableResponse<>(configurationsList, configurationsList.size())));
		return ok(Json.toJson(configurationsList));
	}

	private Query getQuery(ConfigurationsSearchForm form) {
		List<Query> queries = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(form.projCodes)) { //
			queries.add(DBQuery.in("projectCodes", form.projCodes)); // doit pas marcher car pour state.code
			// C'est une valeur qui peut prendre une valeur autorisee dans le formulaire. Ici on veut que 
			// l'ensemble des valeurs correspondent à l'ensemble des valeurs du formulaire independamment de l'ordre.
		}
		
		if (CollectionUtils.isNotEmpty(form.stateCodes)) { //all
			queries.add(DBQuery.in("state.code", form.stateCodes));
		}
		
		if (StringUtils.isNotBlank(form.stateCode)) { //all
			queries.add(DBQuery.in("state.code", form.stateCode));
		}
		
		if (CollectionUtils.isNotEmpty(form.codes)) { //all
			queries.add(DBQuery.in("code", form.codes));
		}
		
		if (CollectionUtils.isNotEmpty(form.codes)) {
			queries.add(DBQuery.in("code", form.codes));
		} else if(StringUtils.isNotBlank(form.codeRegex)) {
			queries.add(DBQuery.regex("code", Pattern.compile(form.codeRegex)));
		}
		
//		Query query = null;
//		if (queries.size() > 0)
//			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
//		return query;
		return DBQueryBuilder.query(DBQueryBuilder.and(queries));
	}
		
	private Configuration getConfiguration(String code) {
		Configuration configuration = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, code);
		return configuration;
	}

	public Result update(String code) {
		//Get Submission from DB 
		Configuration configuration = getConfiguration(code);
		Form<Configuration> filledForm = getFilledForm(configurationForm, Configuration.class);
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 	
		if (configuration == null) {
			//return badRequest("Configuration with code "+code+" not exist");
			ctxVal.addErrors("configuration ", " not exist");
			// return badRequest(filledForm.errors-AsJson( ));
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}
		Configuration configurationInput = filledForm.get();
		if (code.equals(configurationInput.code)) {	
			ctxVal.setUpdateMode();
			ctxVal.getContextObjects().put("type","sra");
			configurationInput.traceInformation.setTraceInformation(getCurrentUser());
			configurationInput.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				logger.info("Update configuration state " + configurationInput.state.code);
				MongoDBDAO.update(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, configurationInput);
				return ok(Json.toJson(configurationInput));
			} else {
				//return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(ctxVal.getErrors()));
			}
		} else {
			//return badRequest("configuration code are not the same");
			ctxVal.addErrors("configuration " + code, "configuration code  " + code + " and configurationInput.code "+ configurationInput.code + "are not the same");
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}	
	}

}
