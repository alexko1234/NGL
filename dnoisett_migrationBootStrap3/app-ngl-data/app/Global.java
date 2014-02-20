
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import services.instance.ImportDataCNG;
import services.instance.ImportDataCNS;


public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("NGL has started");
		
		importData();
	 
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
		  
			if(play.Play.application().configuration().getBoolean("import.data")){
				
		 		Logger.info("NGL import data has started");
			try {
				
				String institute=play.Play.application().configuration().getString("import.institute");
				Logger.info("Import institute "+ institute);
			
				if(institute.equals("CNG")){
					new ImportDataCNG();
				}else if (institute.equals("CNS")){
					 new ImportDataCNS();
				} else {
					throw new RuntimeException("La valeur de l'attribut institute dans application.conf n'a pas d'implementation");
				}
				
			}catch(Exception e){
				throw new RuntimeException("L'attribut institute dans application.conf n'est pas renseigné");
			}
			}else { Logger.info("No import data"); }
	  }
}