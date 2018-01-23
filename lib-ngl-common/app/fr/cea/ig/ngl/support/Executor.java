package fr.cea.ig.ngl.support;

import java.util.concurrent.Callable;

import fr.cea.ig.lfw.support.LoggerHolder;
import fr.cea.ig.ngl.dao.DAOEntityNotFoundException;
import play.mvc.Result;
import play.mvc.Results;

public interface Executor extends LoggerHolder {
	
	/*default Result result(APIExecution toRun, String msg) {
		try {
			return toRun.run();
		} catch (DAOEntityNotFoundException e) {
			return Results.notFound();
		} catch (Exception e) {
			getLogger().error(msg,e);
			throw new RuntimeException(msg,e);
		}
	}*/
	
	default Result result(Callable<Result> callable, String msg) {
		try {
			return callable.call();
		} catch (DAOEntityNotFoundException e) {
			return Results.notFound();
		} catch (Exception e) {
			getLogger().error(msg,e);
			throw new RuntimeException(msg,e);
		}
	}

}
