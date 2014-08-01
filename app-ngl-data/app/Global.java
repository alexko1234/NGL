
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import services.instance.ImportDataCNG;
import services.instance.ImportDataCNS;
import services.instance.RunReportingCNS;


public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("NGL has started");
		
		importData();
		
		generateReporting();
	 
	}

	@Override
	public void onStop(Application app) {
		Logger.info("NGL shutdown...");
	}
	
	 public static int nextExecutionInSeconds(int hour, int minute){
	        return Seconds.secondsBetween(
	                new DateTime(),
	                nextExecution(hour, minute)
	        ).getSeconds();
	    }

	  public static DateTime nextExecution(int hour, int minute){
	        DateTime next = new DateTime()
	                .withHourOfDay(hour)
	                .withMinuteOfHour(minute)
	                .withSecondOfMinute(0)
	                .withMillisOfSecond(0);

	        return (next.isBeforeNow())
	                ? next.plusHours(24)
	                : next;
	    }

	  public static void importData(){
		  
			if (play.Play.application().configuration().getBoolean("import.data")) {
				
		 		Logger.info("NGL import data has started");
				try {
					
					String institute=play.Play.application().configuration().getString("import.institute");
					Logger.info("Import institute "+ institute);
				
					if(institute.equals("CNG")){
						new ImportDataCNG();
					}else if (institute.equals("CNS")){
						 new ImportDataCNS();
					} else {
						throw new RuntimeException("La valeur de l'attribut import.institute dans application.conf n'a pas d'implementation");
					}
					
				}catch(Exception e){
					throw new RuntimeException("L'attribut import.institute dans application.conf n'est pas renseigné");
				}
				
			} else { Logger.info("No import data"); }
	  }
	  
	  public static void generateReporting(){
		  
			if (play.Play.application().configuration().getBoolean("reporting.active")) {
				
		 		Logger.info("NGL reporting has started");
				try {
					
					String institute=play.Play.application().configuration().getString("reporting.institute");
					Logger.info("Reporting institute "+ institute);
				
					if(institute.equals("CNG")){
						//new ReportingCNG();
					}else if (institute.equals("CNS")){
						 new RunReportingCNS();
					} else {
						throw new RuntimeException("La valeur de l'attribut reporting.institute dans application.conf n'a pas d'implementation");
					}
					
				}catch(Exception e){
					throw new RuntimeException("L'attribut reporting.institute dans application.conf n'est pas renseigné");
				}
				
			} else { Logger.info("No reporting"); }
	  }
	  
	  
}