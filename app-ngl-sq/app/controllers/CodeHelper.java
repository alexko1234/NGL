package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.Process;

public class CodeHelper {

	private static SimpleDateFormat getSimpleDateFormat(){
		return new SimpleDateFormat("yyyyMMddHHmmss");
	}
	
	//ProcessusTypeCode-ProjectCode-SampeCode-YYYYMMDDHHMMSSSS
	public static String generateProcessCode(Process process) {		
		return (process.typeCode+"-"+process.projectCode+"-"+process.sampleCode+"-"+new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date())).toUpperCase();		
	}
	
	public static String generateExperiementCode(Experiment exp) {		
		return (exp.typeCode+"-"+getSimpleDateFormat().format(new Date())).toUpperCase();		
	}
}
