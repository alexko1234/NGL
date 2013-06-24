package models.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import net.vz.mongodb.jackson.MongoCollection;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

import play.data.validation.ValidationError;
import play.mvc.Http;

public class InstanceHelpers {

	@SuppressWarnings("unchecked")
	public static Map<String, PropertyValue> getLazyMapPropertyValue() {
		return MapUtils.lazyMap(new HashMap<String, PropertyValue>(), new Transformer() {
			public PropertyValue transform(Object mapKey) {
				//todo comment je sais quel est le type on doit mettre
				return new PropertyValue();
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
		traceInformation.modifyUser=InstanceHelpers.getUser();
		traceInformation.modifyDate=new Date();

	}

	// Add unique code to list
	public static List<String> addCode(String code,List<String> listCodes){
		if(listCodes==null){
			listCodes=new ArrayList<String>();
		}
		if(!listCodes.contains(code)){
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


	public static ContainerSupport getContainerSupportTube(String barCode){
		ContainerSupport containerSupport=new ContainerSupport();
		containerSupport.barCode=barCode;	
		containerSupport.categoryCode="TUBE";
		containerSupport.x="1";
		containerSupport.y="1";
		return containerSupport;
	}


	public static Map<String,PropertyValue> copyPropertyValueFromLevel(Map<String,PropertyDefinition> propertyDefinitions,String level, Map<String,PropertyValue> properties){

		Set<Entry<String, PropertyDefinition>> entries=propertyDefinitions.entrySet();
		Map<String,PropertyValue> propertyResults = null;

		for(Entry<String, PropertyDefinition> entry :entries){

			if(entry.getValue().level.equals(level)){

				PropertyValue propertyValue= properties.get(entry.getKey());

				if(propertyValue!=null) {

					if(propertyResults==null){
						propertyResults=new HashMap<String, PropertyValue>();
					}
					propertyResults.put(entry.getKey(),propertyValue);
				}
			}
		}

		return propertyResults;
	}

	public static DBObject save(IValidation obj, Map<String,List<ValidationError>> errors) {
		Map<String, List<ValidationError>> localErrors=new HashMap<String, List<ValidationError>>();

		obj.validate(localErrors);

		if(localErrors.size()==0){
			return MongoDBDAO.save(obj.getClass().getAnnotation(MongoCollection.class).name(),(DBObject) obj);
		}
		else {
			errors.putAll(localErrors);
			return null;
		}
	}



	public static  <T extends DBObject> List<T> save(List<T> objects, Map<String,List<ValidationError>> errors) {

		List<T> dbObjects=new ArrayList<T>();

		for(DBObject object:objects){
			@SuppressWarnings("unchecked")
			T result=(T) InstanceHelpers.save((IValidation) object, errors);
			if(result!=null){
				dbObjects.add(result);
			}
		}

		return (List<T>) dbObjects;
	}

	
}