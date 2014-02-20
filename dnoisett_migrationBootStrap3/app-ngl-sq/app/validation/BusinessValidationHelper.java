package validation;

import static validation.utils.ValidationHelper.addErrors;
import static validation.utils.ValidationHelper.getKey;
import static validation.utils.ValidationHelper.required;
import static validation.utils.ValidationHelper.validateProperties;
import java.util.List;
import java.util.Map;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.validation.ValidationError;

import com.mongodb.MongoException;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

/**
 * Helper to validate MongoDB Object Used before insert or update a MongoDB
 * object
 * 
 * @author ydeshayes
 * 
 */
public class BusinessValidationHelper {
	private static final String ERROR_NOTUNIQUE = "error.codenotunique";
	public static final String FIELD_CODE = "code";
	
	private static final String CONTAINER_COLL_NAME = "Container";

}
