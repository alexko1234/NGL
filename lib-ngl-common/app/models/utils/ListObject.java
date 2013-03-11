package models.utils;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.project.instance.Project;

public class ListObject{
	public String name;
	public String code;
	
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
}
