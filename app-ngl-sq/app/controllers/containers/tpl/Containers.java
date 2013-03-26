package controllers.containers.tpl;

import java.util.ArrayList;
import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;
import views.html.container.*;
import play.i18n.Messages;

public class Containers extends Controller {

	public static Result home(){
		return ok(home.render());
	}
	
	public static Result search(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", Messages.get("container.table.code")));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("container.table.categoryCode")));
		columns.add(DatatableHelpers.getColumn("sampleCode", Messages.get("container.table.sampleCode")));
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("container.table.fromExperimentTypeCodes")));
		columns.add(DatatableHelpers.getColumn("valid", Messages.get("container.table.valid")));
		columns.add(DatatableHelpers.getColumn("support.name", Messages.get("container.table.support.name")));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("container.table.stateCode")));
		DatatableConfig config = new DatatableConfig(columns);
		return ok(search.render(config));
	}
}
