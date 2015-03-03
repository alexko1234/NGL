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

//Singleton
public class CodeHelper {

	protected CodeHelper()
	{}

	private static class SingletonHolder
	{
		private final static CodeHelper instance = new CodeHelper();
	}

	public static CodeHelper getInstance()
	{			
		return SingletonHolder.instance;
	}

	private SimpleDateFormat getSimpleDateFormat() {
		return new SimpleDateFormat("yyyyMMdd_HHmmss");
	}

	public synchronized String generateKitCatalogCode(String kitCatalogName) {
		return generateBarCode();
	}

	public synchronized String generateBoxCatalogCode(String kitCatalogCode) {
		return StringUtils.stripAccents(kitCatalogCode + "-"
				+ generateBarCode());
	}

	public synchronized String generateReagentCatalogCode(String boxCatalogCode) {
		return boxCatalogCode + "-" + generateBarCode();
	}

	protected synchronized String generateBarCode(){
		try {
			Thread.sleep(1);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String date = new SimpleDateFormat("yyMMddHHmmssSS").format(new Date());
		Pattern p = Pattern
				.compile("([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{3}|[0-9]{2})");
		// Matcher m = p.matcher("151231235959999");//worst situation
		Matcher m = p.matcher(date);
		if (m.matches()) {
			String code = Integer.toString(Integer.valueOf(Integer.valueOf(m.group(1))-15),//Years 0 is 2015
					36);// year
			code += Integer.toString(Integer.valueOf(m.group(2)), 36);// month
			code += Integer.toString(Integer.valueOf(m.group(3)), 36);// day
			code += Integer.toString(Integer.valueOf(m.group(4)), 36);// hours

			int second = Integer.valueOf(m.group(6)) + 10;// +10 because we can have duplicated like 11 0 and 1 10
			int minsec = Integer.valueOf(m.group(5) + String.valueOf(second)) + 1296;// +1296 because we want always 3 char
			code += Integer.toString(minsec, 36);// minute

			code += Integer.toString(Integer.valueOf(m.group(7)) + 36, 36);// millisecond
			Logger.debug("Container code generated "+code);
			return code.toUpperCase();
		} else {
			try {
				Logger.error("Error matches of the date fail"+date);
				throw new Exception("matches fail " + date);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public synchronized String generateContainerSupportCode() {
		 return generateBarCode();
	}

	// ProcessusTypeCode-ProjectCode-SampeCode-YYYYMMDDHHMMSSSS
	public synchronized String generateProcessCode(Process process) {
		return (process.sampleCode + "_" + process.typeCode + "_" + generateBarCode()).toUpperCase();
	}

	public synchronized String generateExperiementCode(Experiment exp) {
		return (exp.typeCode + "-" + getSimpleDateFormat().format(new Date()))
				.toUpperCase();
	}

	public synchronized String generateExperimentCommentCode(Comment com) {
		return (com.createUser + getSimpleDateFormat().format(new Date()) + Math
				.random()).toUpperCase();
	}
}
