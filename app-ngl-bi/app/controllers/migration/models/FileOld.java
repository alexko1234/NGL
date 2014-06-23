package controllers.migration.models;


import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.mongojack.DBQuery;

import models.laboratory.run.instance.File;

public class FileOld extends File {

	public String stateCode;
	
}
