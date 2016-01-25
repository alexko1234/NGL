package models.utils;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;

public class ListObjectNumber {

	public String name;
	public Number code;
	
	public ListObjectNumber(){
		this.name = "";
		this.code = 0;
	}
	
	public ListObjectNumber(Number code, String label){
		this.name = label;
		this.code = code;
	}
	
}
