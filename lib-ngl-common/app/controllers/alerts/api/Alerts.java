package controllers.alerts.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import models.laboratory.alert.instance.Alert;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.readsets.api.ReadSetsSearchForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;



public class Alerts extends CommonController{

	final static Form<AlertsSearchForm> searchForm = form(AlertsSearchForm.class);
	//@Permission(value={"reading"})
	public static Result list() {
		Form<AlertsSearchForm> filledForm = filledFormQueryString(searchForm, AlertsSearchForm.class);
		AlertsSearchForm form = filledForm.get();
		
		Query q = getQuery(form);
		if(form.datatable){
			MongoDBResult<Alert> results = mongoDBFinder(InstanceConstants.ALERT_COLL_NAME, form, Alert.class, q);				
			List<Alert> alerts = results.toList();
			return ok(Json.toJson(new DatatableResponse<Alert>(alerts, results.count())));
		}else{
			MongoDBResult<Alert> results = mongoDBFinder(InstanceConstants.ALERT_COLL_NAME, form, Alert.class, q);							
			List<Alert> alerts = results.toList();
			return ok(Json.toJson(alerts));
		}
	}
	
	
	private static Query getQuery(AlertsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.regexCode)) { //all
			queries.add(DBQuery.regex("code", Pattern.compile(form.regexCode)));
		}
		return query;
	}
	
	//@Permission(value={"reading"})
	public static Result get(String code) {
		Alert alert = getAlert(code);
		if (alert != null) {		
			return ok(Json.toJson(alert));					
		} else {
			return notFound();
		}
	}
	
	//@Permission(value={"reading"})
	public static Result head(String code) {
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.ALERT_COLL_NAME, Alert.class, code)){			
			return ok();					
		}else{
			return notFound();
		}
	}
	
	private static Alert getAlert(String code) {
		Alert alert = MongoDBDAO.findByCode(InstanceConstants.ALERT_COLL_NAME, Alert.class, code);
		return alert;
	}
	
}
