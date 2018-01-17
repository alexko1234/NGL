package fr.cea.ig.ngl.support;

import fr.cea.ig.ngl.daoapi.APIException;
import models.utils.dao.DAOException;
import play.mvc.Result;
import play.mvc.Results;

public interface APIResultProcessor {

	default public Result apiResult(APIExecution toRun) {
		try {
			return toRun.run();
		} catch (DAOException e) {
			return Results.internalServerError();
		} catch (APIException e) {
			return Results.internalServerError();
		} catch (Exception e) {
			return Results.internalServerError();
		}
	}
	
}
