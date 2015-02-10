package models.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import models.laboratory.common.instance.Comment;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.Process;

import org.apache.commons.lang3.StringUtils;

public class CodeHelper {

	private static SimpleDateFormat getSimpleDateFormat(){
		return new SimpleDateFormat("yyyyMMdd_HHmmss");
	}
	
	public static String generateKitCatalogCode(String kitCatalogName){
		return StringUtils.stripAccents(kitCatalogName.toUpperCase().replaceAll("\\s", ""));
	}
	
	public static String generateBoxCatalogCode(String kitCatalogCode, String boxCatalogName){
		return StringUtils.stripAccents(kitCatalogCode+"-"+boxCatalogName.toUpperCase().replaceAll("\\s", ""));
	}
	
	public static String generateReagentCatalogCode(String reagentCatalogName){
		return StringUtils.stripAccents(reagentCatalogName.toUpperCase().replaceAll("\\s", ""));
	}
	
	//ProcessusTypeCode-ProjectCode-SampeCode-YYYYMMDDHHMMSSSS
	public static String generateProcessCode(Process process) {		
		 Random randomGenerator = new Random();
		return ("P-"+process.typeCode+"-"+process.sampleCode+"-"+new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(new Date())+randomGenerator.nextInt(100)).toUpperCase();		
	}
	
	public static String generateExperiementCode(Experiment exp) {		
		return (exp.typeCode+"-"+getSimpleDateFormat().format(new Date())).toUpperCase();		
	}
	
	public static String generateExperimentCommentCode(Comment com){
		return (com.createUser+getSimpleDateFormat().format(new Date())+Math.random()).toUpperCase();
	}
}
