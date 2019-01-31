//<<<<<<< HEAD
//
//import org.joda.time.DateTime;
//import org.joda.time.Seconds;
//
//import play.Application;
//import play.GlobalSettings;
//import play.Logger;
//import play.api.Play;
//import rules.services.RulesServices6;
//import services.instance.ImportDataCNG;
//import services.instance.ImportDataCNS;
////import services.instance.ImportDataCNG;
////import services.instance.ImportDataCNS;
//import services.instance.ImportDataGET;
//
//
//
//public class Global extends GlobalSettings {
//
//	@Override
//	public void onStart(Application app) {
//		Logger.info("NGL has started");
//		
//		try {
//			RulesServices6.getInstance();
//		} catch (Throwable e) {
//			Logger.error("Error Load knowledge base");
//			Logger.error("Drools Singleton error: "+e.getMessage(),e);;
//			//Shutdown application
//			Play.stop();
//		}
//		
//		importData();
//	}
//
//	@Override
//	public void onStop(Application app) {
//		Logger.info("NGL shutdown...");
//	}
//	
//	 public static int nextExecutionInSeconds(int hour, int minute){
//	        return Seconds.secondsBetween(
//	                new DateTime(),
//	                nextExecution(hour, minute)
//	        ).getSeconds();
//	    }
//
//	  public static DateTime nextExecution(int hour, int minute){
//	        DateTime next = new DateTime()
//	                .withHourOfDay(hour)
//	                .withMinuteOfHour(minute)
//	                .withSecondOfMinute(0)
//	                .withMillisOfSecond(0);
//
//	        return (next.isBeforeNow())
//	                ? next.plusHours(24)
//	                : next;
//	    }
//
//	  public static void importData(){
//		  
//			if (play.Play.application().configuration().getBoolean("import.data")) {
//				
//		 		Logger.info("NGL import data has started");
//				try {
//					
//					String institute=play.Play.application().configuration().getString("import.institute");
//					Logger.info("Import institute "+ institute);
//
//					if ("GET".equals(institute)){
//						new ImportDataGET();
////					}else if("CNG".equals(institute)){
////						new ImportDataCNG();
////					}else if ("CNS".equals(institute)){
////						 new ImportDataCNS();
//					} else {
//						throw new RuntimeException("La valeur de l'attribut import.institute dans application.conf n'a pas d'implementation");
//					}
//
//				}catch(Exception e){
//					throw new RuntimeException("L'attribut import.institute dans application.conf n'est pas renseigné",e);
//				}
//				
//			} else { Logger.info("No import data"); }
//	  }
//	  
////	  public static void generateReporting(){
////		  
////			if (play.Play.application().configuration().getBoolean("reporting.active")) {
////				
////		 		Logger.info("NGL reporting has started");
////				try {
////					
////					String institute=configuration().getString("institute");
////					Logger.info("institute for the reporting : "+ institute);
////				
////					if (institute.equals("CNS")) {
////						 new RunReportingCNS();
////					} else {
////						throw new RuntimeException("La valeur de l'attribut institute dans application.conf n'a pas d'implementation");
////					}
////					
////				}catch(Exception e){
////					throw new RuntimeException("L'attribut institute dans application.conf n'est pas renseigné");
////				}
////				
////			} else { Logger.info("No reporting"); }
////	  }
//	  
//	  
// à modifier aprés merge =======

import javax.inject.Inject;

import static fr.cea.ig.play.IGGlobals.configuration;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import fr.cea.ig.play.migration.NGLContext;
import rules.services.RulesServices6;
import services.instance.ImportDataCNG;
import services.instance.ImportDataCNS;
import services.instance.ImportDataGET;
import services.reporting.RunReportingCNS;

// TODO: use proper polymorphism instead of key test
public class Global { // extends GlobalSettings {
	
	private static final play.Logger.ALogger logger = play.Logger.of("application");
	
	private NGLContext ctx;
	
	@Inject
	public Global (NGLContext ctx) {
		this.ctx = ctx;
		logger.info("Global NGL has started");
	}
	// @Override
	public void onStart(play.Application app) {
		logger.info("NGL has started");

		try {
			RulesServices6.getInstance();
		} catch (Throwable e) {
			logger.error("Error Load knowledge base");
			logger.error("Drools Singleton error: "+e.getMessage(),e);
			//Shutdown application
			// Play.stop();
		}

		importData();

//		generateReporting();

	}

	// @Override
	public void onStop(play.Application app) {
		logger.info("NGL shutdown...");
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

	public /*static*/ void importData(){

		// if (play.Play.application().configuration().getBoolean("import.data")) {
//		if (IGGlobals.configuration().getBoolean("import.data",false)) {
		if (ctx.config().getBoolean("import.data",false)) {
			logger.info("NGL import data has started");
			try {

				// String institute=play.Play.application().configuration().getString("import.institute");
				String institute = ctx.config().getString("import.institute");
				logger.info("Import institute "+ institute);

				if ("CNG".equals(institute)) {
					new ImportDataCNG(ctx);
				} else if ("CNS".equals(institute)) {
					new ImportDataCNS(ctx);
				}else 
				if ("GET".equals(institute)) {
//					logger.info("Import institute ImportDataGET");
					new ImportDataGET(ctx);
				} else {
					throw new RuntimeException("La valeur de l'attribut import.institute dans application.conf n'a pas d'implementation");
				}

			} catch(Exception e){
				logger.error("importData error " + e.getMessage());
				throw new RuntimeException("Erreur rencontrée lors de l'import des données",e);
			}

		} else { 
			logger.info("No import data"); 
		}
	}

	public /*static*/ void generateReporting(){

		// if (play.Play.application().configuration().getBoolean("reporting.active")) {
//		if (IGGlobals.configuration().getBoolean("reporting.active",false)) {

		if (ctx.config().getBoolean("reporting.active",false)) {
			logger.info("NGL reporting has started");
			try {

				// String institute=configuration().getString("institute");
				String institute = ctx.config().getString("institute");
				logger.info("institute for the reporting : "+ institute);

				if (institute.equals("CNS")) {
					new RunReportingCNS(ctx);
				} else {
					throw new RuntimeException("La valeur de l'attribut institute dans application.conf n'a pas d'implementation");
				}

			}catch(Exception e){
				throw new RuntimeException("L'attribut institute dans application.conf n'est pas renseigné");
			}

		} else { 
			logger.info("No reporting"); 
		}
	}

//>>>>>>> V2.0.2
}