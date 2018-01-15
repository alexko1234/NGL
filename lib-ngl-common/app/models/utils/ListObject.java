package models.utils;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;


public class ListObject {
	
	public String name;
	public String code;
	
	public ListObject(){
		this.name = "";
		this.code = "";
	}
	
	public ListObject(String code, String label) {
		this.name = label;
		this.code = code;
	}
	
	public static List<ListObject> from(List<CommonInfoType> values) {
		List<ListObject> l = new ArrayList<ListObject>(values.size());
		for(CommonInfoType value : values){
			l.add(new ListObject(value.code, value.name));
		}
		return l;
	}
	
}
