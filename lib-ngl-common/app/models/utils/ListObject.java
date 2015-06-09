package models.utils;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;

public class ListObject{
	public String name;
	public String code;
	
	public ListObject(){
		this.name = "";
		this.code = "";
	}
	
	public ListObject(String code, String label){
		this.name = label;
		this.code = code;
	}
	
	
	
	public static List<ListObject> projectToJsonObject(List<Project> projects){
		List<ListObject> jo = new ArrayList<ListObject>();
		
		for(Project p: projects){
			jo.add(new ListObject(p.code, p.name));
		}
		
		return jo;
	}
	
	public static List<ListObject> sampleToJsonObject(List<Sample> samples){
		List<ListObject> jo = new ArrayList<ListObject>();
		
		for(Sample p: samples){
			jo.add(new ListObject(p.code, p.name));
		}
		
		return jo;
	}

	public static List<ListObject> from(List<CommonInfoType> values) {
		List<ListObject> l = new ArrayList<ListObject>(values.size());
		for(CommonInfoType value : values){
			l.add(new ListObject(value.code, value.name));
		}
		return l;
	}
}
