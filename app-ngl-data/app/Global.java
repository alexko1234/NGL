
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import services.instance.ImportDataFactory;


public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("NGL has started");

		Akka.system().scheduler().schedule(
				Duration.create(nextExecutionInSeconds(1, 0), TimeUnit.SECONDS),
                Duration.create(24, TimeUnit.HOURS)
                , (new ImportDataFactory()).getImportData(), Akka.system().dispatcher()
		); 
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