package views.components.datatable;

import java.util.List;

import org.codehaus.jackson.JsonNode;


import play.libs.Json;
import play.libs.Scala;

import scala.collection.Seq;

public class DatatableConfig {
	
	public static final String DEFAULT_NB_ELEMENT = "50";
	public static final String DEFAULT_PAGE_NUMBER = "0";
	public static final String DEFAULT_ORDER_SENSE = "1";
	
	public Seq<DatatableColumn> columns;
	public List<DatatableColumn> columnList;
	public Boolean button = Boolean.FALSE;
	public Boolean edit = Boolean.FALSE;
	public Boolean hidding = Boolean.FALSE;
	public Boolean show = Boolean.FALSE;
	public String name = "datatable"; //default js name
	
	
	public DatatableConfig(List<DatatableColumn> columns) {
		this(columns, Boolean.FALSE);
	}
	
	public DatatableConfig(List<DatatableColumn> columns, Boolean show) {
		this.columnList = columns;
		this.columns = Scala.toSeq(columns);
		this.show = show;
		int count = 0;		
		for(DatatableColumn column:  columns){
			if(column.edit.booleanValue()){
				this.edit = Boolean.TRUE;
			}
			if(column.hide.booleanValue()){
				this.hidding = Boolean.TRUE;
			}
			column.id = "p"+count++;
		}
		button = (edit.booleanValue() || hidding.booleanValue() || show.booleanValue())?Boolean.TRUE:Boolean.FALSE;
	}	
	
	
	public JsonNode toJson(){
		return Json.toJson(columnList);		
	}
}
