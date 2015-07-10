package models.utils;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;

public class ListObjectValue<T> {

	public String name;
	public T code;
	
	public ListObjectValue(){
		this.name= "";
		this.code= null;
	}
	
	public ListObjectValue(T code, String label){
		this.name = label;
		this.code = code;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<ListObjectValue> projectToJsonObject(List<Project> projects){
		List<ListObjectValue> jo = new ArrayList<ListObjectValue>();
		
		for(Project p: projects){
			jo.add(new ListObjectValue(p.code, p.name));
		}
		
		return jo;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<ListObjectValue> sampleToJsonObject(List<Sample> samples){
		List<ListObjectValue> jo = new ArrayList<ListObjectValue>();
		
		for(Sample p: samples){
			jo.add(new ListObjectValue(p.code, p.name));
		}
		
		return jo;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<ListObjectValue> from(List<CommonInfoType> values) {
		List<ListObjectValue> l = new ArrayList<ListObjectValue>(values.size());
		for(CommonInfoType value : values){
			l.add(new ListObjectValue(value.code, value.name));
		}
		return l;
	}
	
}
