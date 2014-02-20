package controllers.runs.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.instance.TransientState;
import models.laboratory.run.instance.Run;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import workflows.Workflows;

public class State extends RunsController {
    final static Form<models.laboratory.common.instance.State> stateForm = form(models.laboratory.common.instance.State.class);
    final static Form<HistoricalStateSearchForm> historicalForm = form(HistoricalStateSearchForm.class);

    public static Result get(String code) {
	Run runValue = getRun(code, "state");
	if (runValue != null) {
	    return ok(Json.toJson(runValue.state));
	} else {
	    return notFound();
	}
    }

    // @Permission(value={"workflow_run_lane"})
    public static Result update(String code) {
	Run run = getRun(code);
	if (run == null) {
	    return badRequest();
	}
	Form<models.laboratory.common.instance.State> filledForm = getFilledForm(
		stateForm, models.laboratory.common.instance.State.class);
	models.laboratory.common.instance.State state = filledForm.get();
	state.date = new Date();
	state.user = getCurrentUser();
	ContextValidation ctxVal = new ContextValidation(filledForm.errors());
	Workflows.setRunState(ctxVal, run, state);
	if (!ctxVal.hasErrors()) {
	    return ok(Json.toJson(getRun(code)));
	} else {
	    return badRequest(filledForm.errorsAsJson());
	}
    }

    public static Result historical(String code) {
	Run runValue = getRun(code, "state");
	if (runValue != null) {
	    Form<HistoricalStateSearchForm> inputForm = filledFormQueryString(historicalForm, HistoricalStateSearchForm.class);
	    List<TransientState> historical = getHistorical(runValue.state.historical, inputForm.get());
	    return ok(Json.toJson(historical));
	} else {
	    return notFound();
	}
    }

    private static List<TransientState> getHistorical(
	    List<TransientState> historical, HistoricalStateSearchForm form) {
	List<TransientState> values = new ArrayList<TransientState>();
	if (StringUtils.isNotBlank(form.stateCode)) {
	    for (int i = historical.size() - 1; i >= 0; i--) {
		TransientState ts = historical.get(i);
		if(form.stateCode.equals(ts.code)){
		    values.add(ts);
		    if(form.last)break;
		}
	    }
	    Collections.reverse(values);
	    return values;
	} else {
	    return historical;
	}
    }
    
}
