package workflows.container;


import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.utils.InstanceConstants;


public class ContentHelper {
	
	
	/*
	 * Give MongoDB query to find a specific content inside containers
	 * @param container
	 * @param content
	 * @return
	 */
	public static Query getContentQuery(Container container, Content content) {
		Query query = DBQuery.is("code",container.code);
		
		Query contentQuery =  DBQuery.is("projectCode", content.projectCode).is("sampleCode", content.sampleCode);
		
		if(content.properties.containsKey(InstanceConstants.TAG_PROPERTY_NAME)){
			contentQuery.is("properties.tag.value", content.properties.get(InstanceConstants.TAG_PROPERTY_NAME).value);
		}
		query.elemMatch("contents", contentQuery);
		
		return query;
	}

}
