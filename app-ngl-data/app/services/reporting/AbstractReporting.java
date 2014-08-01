package services.reporting;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.slf4j.MDC;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;

public abstract class AbstractReporting implements Runnable{

	final String name;
	protected ALogger logger;

	public abstract void runReporting() throws UnsupportedEncodingException, MessagingException;

	public AbstractReporting(String name,FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration){
		this.name=name;
		logger=Logger.of(this.getClass().getName());
		Akka.system().scheduler().schedule(durationFromStart,durationFromNextIteration
				, this, Akka.system().dispatcher()
				); 
	}

	public void run() {
		MDC.put("name", name);
		logger.info("Reporting execution :"+name);
		try{
			runReporting();
		}catch (Throwable e) {
			logger.error("",e);
		}
		finally{
			MDC.remove("name");
		}
	};



}
