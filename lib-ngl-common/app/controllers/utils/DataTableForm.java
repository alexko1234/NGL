package controllers.utils;

import java.util.List;

public class DataTableForm {
	public String sEcho;
	public Integer iColumns;
	public String sColumns;
	public Integer iDisplayStart;
	public Integer iDisplayLength;
	//public String sSearch;
	//public Boolean bRegex = false;
	public Integer iSortingCols;
	
	
	public List<String> mDataProp;
	public List<String> sSearch;
	public List<Boolean> bRegex;
	public List<Boolean> bSearchable;
	public List<Boolean> bSortable;
	public List<Integer> iSortCol;
	public List<String> sSortDir;
	
}
