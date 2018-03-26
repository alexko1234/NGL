package controllers;

import views.components.datatable.DatatableForm;



public class ListForm extends DatatableForm {
	public Boolean list = Boolean.FALSE;
	public Boolean count = Boolean.FALSE;
	
	public Integer limit = 5000; //limit the number or element in the result

	public boolean reporting = Boolean.FALSE;
	public String reportingQuery;
	public boolean aggregate = Boolean.FALSE;
	
	
}
