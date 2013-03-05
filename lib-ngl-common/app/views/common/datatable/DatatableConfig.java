package views.common.datatable;

import java.util.List;

import play.libs.Scala;

import scala.collection.Seq;

public class DatatableConfig {
	
	public Seq<DatatableColumn> columns;

	public Boolean toolbar = Boolean.FALSE;
	public Boolean edit = Boolean.FALSE;
	public Boolean hidding = Boolean.FALSE;
	public Boolean show = Boolean.FALSE;
	public String name = "datatable"; //default js name
		
	public DatatableConfig(List<DatatableColumn> columns) {
		this(columns, Boolean.FALSE);
	}
	
	public DatatableConfig(List<DatatableColumn> columns, Boolean show) {
		this.columns = Scala.toSeq(columns);
		this.show = show;
		for(DatatableColumn column:  columns){
			if(column.editable.booleanValue()){
				this.edit = Boolean.TRUE;
			}
			if(column.hidding.booleanValue()){
				this.hidding = Boolean.TRUE;
			}
		}
		toolbar = (edit.booleanValue() || hidding.booleanValue() || show.booleanValue())?Boolean.TRUE:Boolean.FALSE;
	}
	
}
