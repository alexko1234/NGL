package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.laboratory.common.instance.Comment;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.Process;

public class CodeHelper {

	private static SimpleDateFormat getSimpleDateFormat(){
		return new SimpleDateFormat("yyyyMMdd_HHmmss");
	}
	
	//ProcessusTypeCode-ProjectCode-SampeCode-YYYYMMDDHHMMSSSS
	public static String generateProcessCode(Process process) {		
		return ("P-"+process.typeCode+"-"+process.projectCode+"-"+process.sampleCode+"-"+new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(new Date())).toUpperCase();		
	}
	
	public static String generateExperiementCode(Experiment exp) {		
		return (exp.typeCode+"-"+getSimpleDateFormat().format(new Date())).toUpperCase();		
	}
	
	public static String generateExperimentCommentCode(Comment com){
		return (com.createUser+getSimpleDateFormat().format(new Date())+Math.random()).toUpperCase();
	}
}
