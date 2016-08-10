package models.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import models.laboratory.common.instance.Comment;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.Process;
import models.laboratory.project.instance.Project;
import play.Logger;
import fr.cea.ig.MongoDBDAO;

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

	private SimpleDateFormat getSimpleDateFormat(String format) {
		return new SimpleDateFormat(format);
	}

	protected synchronized String generateBarCode(){
		try {
			Thread.sleep(1);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			Logger.error("Interruption error: "+e1.getMessage(),e1);
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
			//Logger.debug("Container code generated "+code);
			return code.toUpperCase();
		} else {
			try {
				Logger.error("Error matches of the date fail"+date);
				throw new Exception("matches fail " + date);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.error("Matches error: "+e.getMessage(),e);;
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

	public synchronized String generateExperimentCode(Experiment exp) {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			Logger.error("Interruption error: "+e1.getMessage(),e1);
		}
		
		String date = getSimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
		Pattern p = Pattern.compile("([0-9]{8})_([0-9]{6})([0-9]{1})([0-9]{1})([0-9]{1})");
		// Matcher m = p.matcher("151231235959999");//worst situation
		Matcher m = p.matcher(date);
		if (m.matches()) {
			String code = m.group(1)+"_"+m.group(2)+""
					+Integer.toString(Integer.valueOf(m.group(3)) +10,36)
					+Integer.toString(Integer.valueOf(m.group(4)) +10,36)
					+Integer.toString(Integer.valueOf(m.group(5)) +10,36);
			return (exp.typeCode + "-" + code).toUpperCase();
		} else {
			try {
				Logger.error("Error matches of the date fail"+date);
				throw new Exception("matches fail " + date);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.error("Matches error: "+e.getMessage(),e);;
				return null;
			}
		}
	}

	public synchronized String generateExperimentCommentCode(Comment com) {
		return (com.createUser + getSimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + Math
				.random()).toUpperCase();
	}
	
	public synchronized String generateSampleCode(String projectCode){
		Project project =MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, projectCode);
		String newCode=nextCode(project.lastSampleCode);			
		MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code", project.code),DBUpdate.set("lastSampleCode",newCode));
		return projectCode+"_"+newCode; //TODO !!! Genoscope rules !!!!
	}
	
	
	private static String nextCode(String oldCode){
		String newCode=null;
		String beginCode=null;
		char lastLetter='Z';

		if(oldCode==null){
			newCode="AAAA";
		}else {
			
			int nbCharacter=oldCode.length();
			int lastCharacter=nbCharacter;

			//Recupère la position à partir de laquelle il faut changer de lettre
			while (lastCharacter!=0 && oldCode.substring(lastCharacter-1, lastCharacter).equals(Character.toString(lastLetter))) {
				lastCharacter--;
			}
			
			if( lastCharacter>1 || (lastCharacter==1 && !oldCode.substring(lastCharacter-1, lastCharacter).equals(String.valueOf(lastLetter))))
			{
				beginCode=oldCode.substring(0, lastCharacter-1); // debut du code sample a conserver
				newCode=beginCode+Character.toString((char) (oldCode.charAt(lastCharacter-1)+1)); // Concatenation debut code sample + lettre suivante
			}
			else {
				newCode="AA";
				lastCharacter=1;
			}
			
			while (lastCharacter<nbCharacter){
				newCode=newCode+'A';
				lastCharacter++;
			}
		}
		
		return newCode;
	}
	
	 public static void main (String[] args){
		 System.out.println("New code A : "+CodeHelper.nextCode(null));
		 System.out.println("New code B : "+CodeHelper.nextCode("A"));
		 System.out.println("New code AA : "+CodeHelper.nextCode("Z"));
		 System.out.println("New code BA : "+CodeHelper.nextCode("AZ"));
		 System.out.println("New code AAAA : "+CodeHelper.nextCode("ZZZ"));
		 System.out.println("New code AABA : "+CodeHelper.nextCode("AAAZ"));
		 System.out.println("New code ACAA : "+CodeHelper.nextCode("ABZZ"));
	 }
	
}
