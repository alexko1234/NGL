package controllers.run.tpl;

import java.util.ArrayList;
import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;
import views.common.datatable.DatatableColumn;
import views.common.datatable.DatatableConfig;
import views.common.datatable.DatatableHelpers;
import views.html.run.*;

/**
 * Controller around Run object
 * @author galbini
 *
 */
public class Runs extends Controller {
	
	public static Result home(String code) {
		return ok(home.render());
	}
	
	public static Result search() {
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", "Code"));
		columns.add(DatatableHelpers.getDateColumn("traceInformation.creationDate", "Creation date"));
		columns.add(DatatableHelpers.getColumn("dispatch", "Dispatch"));
		DatatableConfig config = new DatatableConfig(columns, Boolean.TRUE);
		return ok(search.render(config));
	}
	
	public static Result details() {
		return ok(details.render());
	}	
	
}
