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
	public Boolean button = Boolean.FALSE; //est ce qu'il y a des bouttons à afficher
	public Boolean edit = Boolean.FALSE; //mode edition
	public Boolean editColumn = Boolean.FALSE; //mode edition column
	public Boolean save = Boolean.FALSE; //mode save
	public Boolean order = Boolean.FALSE; //mode save
	public Boolean remove = Boolean.FALSE; //mode suppression
	public Boolean hidding = Boolean.FALSE; //mode cacher. attention très consomateur en resource
	public Boolean show = Boolean.FALSE; //mode details
	public String name = "datatable"; //default js name
	public Boolean compact = Boolean.TRUE; //mode compact pour le nom des bouttons
	
		
	public DatatableConfig(List<DatatableColumn> columns) {
		this.columnList = columns;
		this.columns = Scala.toSeq(columns);		
		int count = 0;		
		for(DatatableColumn column:  columns){
			if(column.edit.booleanValue()){
				this.edit = Boolean.TRUE;
				this.editColumn = Boolean.TRUE;
			}
			if(column.hide.booleanValue()){
				this.hidding = Boolean.TRUE;
			}
			if(column.order.booleanValue()){
				this.order = Boolean.TRUE;
			}
			column.id = "p"+count++;
		}
		if(this.edit)this.save = Boolean.TRUE; //if edition so save
		this.button = (this.edit.booleanValue() || this.hidding.booleanValue() || this.show.booleanValue() || this.remove.booleanValue())?Boolean.TRUE:Boolean.FALSE;
	}	
	
	
	public JsonNode toJson(){
		return Json.toJson(columnList);		
	}
}
