package models.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.property.PropertySingleValue;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;

import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class InstanceHelpers {

	@SuppressWarnings("unchecked")
	public static Map<String, PropertyValue> getLazyMapPropertyValue() {
		return MapUtils.lazyMap(new HashMap<String, PropertyValue>(), new Transformer() {
			public PropertyValue transform(Object mapKey) {
				//todo comment je sais quel est le type on doit mettre
				return new PropertySingleValue();
			}
		});
	}

	public static String getUser(){
		String user;
		try{
			user=Http.Context.current().session().get("CAS_FILTER_USER");
		} catch(RuntimeException e){
			user="ngl";
		}
		return user;

	}

	public static List<Comment> addComment(String comment,List<Comment> comments){
		if(comments==null){
			comments=new ArrayList<Comment>();
		}

		Comment newComment=new Comment(comment);
		newComment.createUser=InstanceHelpers.getUser();

		comments.add(newComment);
		return comments;
	}


	public static void updateTraceInformation(TraceInformation traceInformation){
		
		if (traceInformation.createUser==null){
			traceInformation.createUser=InstanceHelpers.getUser();
		}else {
			traceInformation.modifyUser=InstanceHelpers.getUser();
		}

		if(traceInformation.creationDate==null){
			traceInformation.creationDate=new Date();
		}else {
			traceInformation.modifyDate=new Date();
		}

	}

	// Add unique code to list
	public static List<String> addCode(String code,List<String> listCodes){
	
		if(listCodes==null){
			listCodes=new ArrayList<String>();
		}
		
		if(code!= null && !listCodes.contains(code)){
				listCodes.add(code);
		}
		return listCodes;
	}


	// Add unique codes from list to list
	public static List<String> addCodesList(List<String> codes, List<String> listCodes){
		if(listCodes==null){
			listCodes=new ArrayList<String>();
		}

		for(int i=0;i<codes.size();i++){
			if(!listCodes.contains(codes.get(i))){
				listCodes.add(codes.get(i));
			}

		}
		return listCodes;
	}



	public static void copyPropertyValueFromPropertiesDefinition(List<PropertyDefinition> propertyDefinitions,
			Map<String,PropertyValue> propertiesInput,Map<String,PropertyValue> propertiesOutPut){

		for(PropertyDefinition propertyDefinition : propertyDefinitions){

			PropertyValue propertyValue= propertiesInput.get(propertyDefinition.code);

			if(propertyValue!=null) {

				if(propertiesOutPut==null){
					propertiesOutPut=new HashMap<String, PropertyValue>();
				}
				propertiesOutPut.put(propertyDefinition.code,propertyValue);
			}
		}

	}

	public static void copyPropertyValueFromLevel(
			Map<String, PropertyDefinition> propertyDefinitions,
			Level.CODE level, Map<String, PropertyValue> propertyValues,
			Map<String, PropertyValue> properties) {


	}

	public static DBObject save(String collectionName, IValidation obj, ContextValidation contextError,Boolean keepRootKeyName) {
		ContextValidation localContextError=new ContextValidation();
		if(keepRootKeyName){
			localContextError.addKeyToRootKeyName(contextError.getRootKeyName());
		}
		localContextError.setContextObjects(contextError.getContextObjects());
		
		if (obj != null) {
			obj.validate(localContextError);
		} else {
			throw new IllegalArgumentException("missing obj");
		}

		if(localContextError.errors.size()==0){
			return MongoDBDAO.save(collectionName,(DBObject) obj);
		}
		else {
			contextError.errors.putAll(localContextError.errors);
			Logger.info("erreur output :: "+contextError.errors.toString());
			return null;
		}
	}
	
	public static DBObject save(String collectionName, IValidation obj, ContextValidation contextError) {
		return save(collectionName, obj, contextError, false);
	}


	public static  <T extends DBObject> List<T> save(String collectionName,List<T> objects, ContextValidation contextErrors) {

		List<T> dbObjects=new ArrayList<T>();

		for(DBObject object:objects){
			@SuppressWarnings("unchecked")
			T result=(T) InstanceHelpers.save(collectionName,(IValidation) object, contextErrors);
			if(result!=null){
				dbObjects.add(result);
			}
		}

		return (List<T>) dbObjects;
	}




}