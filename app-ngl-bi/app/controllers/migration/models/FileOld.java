package controllers.migration.models;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import net.vz.mongodb.jackson.DBQuery;

import models.laboratory.run.instance.File;
import models.utils.InstanceConstants;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class FileOld extends File {

	public String stateCode;
	
}
