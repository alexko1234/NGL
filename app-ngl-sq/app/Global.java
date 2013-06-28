
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Request;
import scala.concurrent.duration.Duration;
import data.ImportDataRun;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("NGL has started");
		
		Akka.system().scheduler().schedule(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.SECONDS)
                , new ImportDataRun(), Akka.system().dispatcher()
				); 
		
		Akka.system().scheduler().schedule(
				Duration.create(nextExecutionInSeconds(22,13), TimeUnit.SECONDS),
                Duration.create(24, TimeUnit.HOURS)
                , new ImportDataRun(), Akka.system().dispatcher()
				); 
	}

	@Override
	public Action onRequest(Request request, Method actionMethod) {

		//call CAS module
		return new play.modules.cas.CasAuthentication();
	}

	@Override
	public  play.api.mvc.Handler	onRouteRequest(Http.RequestHeader request) {
		return super.onRouteRequest(request);
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
}