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
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.SampleOnContainer;

import org.mongojack.DBQuery;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;

import play.Logger;
import play.mvc.Http;
import validation.ContextValidation;
import validation.IValidation;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

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

	@Deprecated
	public static String getUser(){
		String user;
		try{
			user=Http.Context.current().session().get("CAS_FILTER_USER");
			if(user==null){
				user="ngl";
			}
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
		newComment.createUser=CommonController.getCurrentUser();

		comments.add(newComment);
		return comments;
	}


	@Deprecated
	public static void updateTraceInformation(TraceInformation traceInformation){
		
		if (traceInformation.createUser==null){
			traceInformation.createUser=CommonController.getCurrentUser();
		}else {
			traceInformation.modifyUser=CommonController.getCurrentUser();
		}

		if(traceInformation.creationDate==null){
			traceInformation.creationDate=new Date();
		}else {
			traceInformation.modifyDate=new Date();
		}

	}
	
	public static TraceInformation updateTraceInformation(
			TraceInformation traceInformation, State nextState) {		
		traceInformation.modifyDate = nextState.date;
		traceInformation.modifyUser = nextState.user;		
		return traceInformation;
	}
	
	public static TraceInformation getUpdateTraceInformation(TraceInformation traceInformation) {
		TraceInformation ti=null;
		if(traceInformation==null){
			ti=new TraceInformation();
		}else {
			ti = traceInformation;
		}
		ti.setTraceInformation(CommonController.getCurrentUser());
		return ti;
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
		localContextError.setMode(contextError.getMode());
		if(keepRootKeyName){
			localContextError.addKeyToRootKeyName(contextError.getRootKeyName());
		}
		localContextError.setContextObjects(contextError.getContextObjects());
		
		if (obj != null) {
			obj.validate(localContextError);
		} else {
			throw new IllegalArgumentException("missing object to validate");
		}
		
		if(localContextError.errors.size()==0){
			return MongoDBDAO.save(collectionName,(DBObject) obj);
		}
		else {
			contextError.errors.putAll(localContextError.errors);
			Logger.info("error(s) on output :: "+contextError.errors.toString());
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
	
	
	public static  <T extends DBObject> List<T> save(String collectionName,List<T> objects, ContextValidation contextErrors, Boolean keepRootKeyName) {

		List<T> dbObjects=new ArrayList<T>();

		for(DBObject object:objects){
			@SuppressWarnings("unchecked")
			T result=(T) InstanceHelpers.save(collectionName,(IValidation) object, contextErrors, keepRootKeyName);
			if(result!=null){
				dbObjects.add(result);
			}
		}

		return (List<T>) dbObjects;
	}


	public static SampleOnContainer getSampleOnContainer(ReadSet readSet) {
		//1 retrieve containerSupportCode from Run
		String containerSupportCode = getContainerSupportCode(readSet);
		Container container = getContainer(readSet, containerSupportCode);
		if(null != container){
			Content content = getContent(container, readSet);
			SampleOnContainer sampleContainer = convertToSampleOnContainer(readSet, containerSupportCode, container, content);
		//Logger.info(sampleContainer.toString());
			return sampleContainer;
		}
		return null;
	}


	private static SampleOnContainer convertToSampleOnContainer(ReadSet readSet, String containerSupportCode, Container container, Content content) {
		SampleOnContainer sc = new SampleOnContainer();
		sc.lastUpdateDate = new Date();
		sc.containerSupportCode = containerSupportCode;
		sc.containerCode = container.code;
		sc.sampleCode = readSet.sampleCode;
		sc.sampleTypeCode = content.sampleTypeCode;
		sc.sampleCategoryCode = content.sampleCategoryCode;
		sc.properties = content.properties;
		return sc;
	}



	private static Content getContent(Container container, ReadSet readSet) {
		for(Content sampleUsed : container.contents){
			if(sampleUsed.sampleCode.equals(readSet.sampleCode)){
				return sampleUsed;
			}
		}
		Logger.warn("Not found Content for "+readSet.code+" / "+readSet.sampleCode);
		return null;
	}



	private static Container getContainer(ReadSet readSet, String containerSupportCode) {
		MongoDBResult<Container> cl = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
				DBQuery.and(DBQuery.is("support.code", containerSupportCode),DBQuery.is("support.line", readSet.laneNumber.toString()),
						DBQuery.is("contents.sampleCode", readSet.sampleCode)));
		
		if(cl.size() == 0){
			Logger.warn("Not found Container for "+readSet.code+" with : '"+containerSupportCode+", "+readSet.laneNumber.toString()+", "+readSet.sampleCode+"'");
			return null;
		}
		
		return cl.toList().get(0);
	}


	private static String getContainerSupportCode(ReadSet readSet) {
		BasicDBObject keys = new BasicDBObject();
		keys.put("containerSupportCode", 1);
		Run r = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.is("code",readSet.runCode),
				keys).toList().get(0);
		return r.containerSupportCode;
	}
	


}