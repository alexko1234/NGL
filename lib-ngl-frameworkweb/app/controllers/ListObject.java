package controllers;

public class ListObject {
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
	
}
