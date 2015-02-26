package views.components.datatable;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DatatableForm {
	public Boolean datatable = Boolean.FALSE;
	public Integer orderSense = DatatableConfig.DEFAULT_ORDER_SENSE;
	public Integer numberRecordsPerPage = DatatableConfig.DEFAULT_NB_ELEMENT;
	public Integer pageNumber = DatatableConfig.DEFAULT_PAGE_NUMBER;
	public String orderBy;
	
	public String paginationMode = "REMOTE";
	
	@JsonIgnore
	public boolean isServerPagination() {
		return "REMOTE".equalsIgnoreCase(paginationMode);
	}
	
	public List<String> excludes = new ArrayList<String>(0);
	public List<String> includes = new ArrayList<String>(0);
	
}
