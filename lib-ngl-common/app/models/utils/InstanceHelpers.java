package models.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;

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
		if(Http.Context.current().session().get("CAS_FILTER_USER")==null){
			user="admin";
		}
		else {
			user=Http.Context.current().session().get("CAS_FILTER_USER");
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

}
