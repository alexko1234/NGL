package models.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.laboratory.common.instance.Comment;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.Process;

import org.apache.commons.lang3.StringUtils;

import play.Logger;

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
	
	
	public static String generateContainerSupportCode(){
		String date = new SimpleDateFormat("yyMMddHHmmssSS").format(new Date());
		Logger.info(date);
		Pattern p = Pattern.compile("([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{3}|[0-9]{2})");
		//Matcher m = p.matcher("151231235959999");//worst situation
		Matcher m = p.matcher(date);
		if(m.matches()){
			String code = Integer.toString(Integer.valueOf(m.group(1)),36);//year
			code += Integer.toString(Integer.valueOf(m.group(2)),36);//month
			code += Integer.toString(Integer.valueOf(m.group(3)),36);//day
			code += Integer.toString(Integer.valueOf(m.group(4)),36);//hours
			
			int second = Integer.valueOf(m.group(6))+10;//+10 because we can have duplicated like 11 0 and 1 10
			int minsec = Integer.valueOf(m.group(5)+String.valueOf(second))+1296;//+1296 because we want always 3 char
			code += Integer.toString(minsec,36);//minute
			
			code += Integer.toString(Integer.valueOf(m.group(7))+36,36);//millisecond
			
			Logger.info(code);
			return code;
		}
		
		return null;
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
