package controllers.projects.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBUpdate.Builder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;

import com.mongodb.BasicDBObject;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.Props;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Result;
import rules.services.RulesActor;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.run.instance.RunValidationHelper;
//import views.components.datatable.DatatableHelpers;
//import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;
/**
 * Controller around Run object
 *
 */
public class Projects extends ProjectsController {

	
	
	//@Permission(value={"reading"})
	public static Result get(String code) {
		Project projectValue = getProject(code);
		if (projectValue != null) {		
			return ok(Json.toJson(projectValue));					
		} else {
			return notFound();
		}
	}


}
