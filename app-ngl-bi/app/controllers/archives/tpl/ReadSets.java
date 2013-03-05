package controllers.archives.tpl;

import java.util.ArrayList;
import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;
import views.common.datatable.DatatableColumn;
import views.common.datatable.DatatableConfig;
import views.common.datatable.DatatableHelpers;
import views.html.archive.*;
/**
 * Controller around archive readset object
 * @author galbini
 *
 */
public class ReadSets extends Controller {
	
	public static Result home() {
		return ok(home.render());
	}
	
	public static Result search() {
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("runCode", "Run Code"));
		columns.add(DatatableHelpers.getColumn("projectCode", "Project Code"));
		columns.add(DatatableHelpers.getColumn("readSetCode", "Read Set Code"));
		columns.add(DatatableHelpers.getColumn("path", "Path"));
		columns.add(DatatableHelpers.getDateColumn("date", "Date"));
		columns.add(DatatableHelpers.getColumn("id", "Id"));
		DatatableConfig config = new DatatableConfig(columns);
		return ok(search.render(config));
	}
	
}
