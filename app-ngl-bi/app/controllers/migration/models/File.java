package controllers.migration.models;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

import java.util.HashMap;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;

import models.utils.InstanceConstants;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class File {

	//concatenation de flotseqname + flotseqext
	public String fullname;
	public String extension;
	public Boolean usable = Boolean.FALSE;
	public String typeCode; //id du type de fichier
	public Map<String, PropertyValueOld> properties = new HashMap<String, PropertyValueOld>();

	/*
	asciiEncoding	encodage ascii du fichier
	label			id du label du fichier READ1 / READ2 / SINGLETON
	*/

	
}
