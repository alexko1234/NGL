package views.components.datatable;

import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DatatableForm implements IDatatableForm {
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
	
	public Set<String> excludes = new TreeSet<String>();
	public Set<String> includes = new TreeSet<String>();

	@Override
	@JsonIgnore
	public Set<String> excludes() {
		return this.excludes;
	}
	@Override
	@JsonIgnore
	public Set<String> includes() {
		return this.includes;
	}
	}
