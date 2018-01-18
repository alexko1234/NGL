package sra.scripts;

import play.mvc.Result;
import play.mvc.Results;//.ok; // pour utiliser les methodes de controller
//import static controllers.sra.scripts.ScriptController.LILI;

public  abstract class AbstractScript {
	private final play.Logger.ALogger logger;
	
	StringBuilder sb;
	
	public AbstractScript() {
		logger = play.Logger.of(getClass());
		sb = new StringBuilder();
	}
	
	public Result run() {
		try {
			execute();
			return Results.ok(sb.toString());
		} catch (Exception e) {
			sb.append("***************** ERROR ***********************\n");
			//sb.append(e.getMessage());// println(e);
			sb.append(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
			sb.append("***************** ERROR ***********************\n");
			logger.error(e.getMessage(), e);
			return Results.notFound(sb.toString());			
		}
	}
	

	public enum LogLevel {
		Debug, Info
	}
	
	public void println(String arg) {
		sb.append(arg);
		sb.append('\n');
		switch (logLevel()) {
		case Debug : 
			logger.debug(arg); 
			break;
		case Info : 
			logger.info(arg); 
			break;	
		}
	}	
	
	public void printfln(String format, Object... args ) {
		String arg = String.format(format, args);
		sb.append(arg);
		sb.append('\n');
		switch (logLevel()) {
		case Debug : 
			logger.debug(arg); 
			break;
		case Info : 
			logger.info(arg); 
			break;	
		}
	}	
		
	public LogLevel logLevel() {
		return LogLevel.Debug;
	}	
	
	// methode abstraite qui sera impementée dans les != script
	public abstract void execute() throws Exception;
	

}

