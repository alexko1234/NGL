package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.laboratory.processes.instance.Process;

public class CodeHelper {

	private static SimpleDateFormat getSimpleDateFormat(){
		return new SimpleDateFormat("yyyyMMDDHHmmss");
	}
	
	//ProcessusTypeCode/ProjectCode/SampeCode/YYYYMMDDHHMMSS
	public static String generateProcessCode(Process process) {		
		return (process.typeCode+"/"+process.projectCode+"/"+process.sampleCode+"/"+getSimpleDateFormat().format(new Date())).toUpperCase();		
	}

}
