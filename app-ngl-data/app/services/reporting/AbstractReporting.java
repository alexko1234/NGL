package services.reporting;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.slf4j.MDC;

import fr.cea.ig.play.NGLContext;
import play.Logger;
import play.Logger.ALogger;
// import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;
//import static fr.cea.ig.play.IGGlobals.akkaSystem;

public abstract class AbstractReporting implements Runnable{

	final String name;
	protected ALogger logger;

	public abstract void runReporting() throws UnsupportedEncodingException, MessagingException;

	@Inject
	public AbstractReporting(String name,FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration, NGLContext ctx){
		this.name=name;
		logger=Logger.of(this.getClass().getName());
		// Akka.system()
		ctx.akkaSystem()
		.scheduler().schedule(durationFromStart,durationFromNextIteration
				, this, //Akka.system().dispatcher()
				ctx.akkaSystem().dispatcher()
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
