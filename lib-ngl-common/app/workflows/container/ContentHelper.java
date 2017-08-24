package workflows.container;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.springframework.stereotype.Service;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;

@Service
public class ContentHelper {
	public static final String TAG_PROPERTY_NAME = "tag";
	/**
	 * Give MongoDB query to find a specific content inside containers
	 * @param container
	 * @param content
	 * @return
	 */
	public Query getContentQuery(Container container, Content content) {
		Query query = DBQuery.is("code",container.code);
		
		Query contentQuery =  DBQuery.is("projectCode", content.projectCode).is("sampleCode", content.sampleCode);
		
		if(content.properties.containsKey(TAG_PROPERTY_NAME)){
			contentQuery.is("properties.tag.value", content.properties.get(TAG_PROPERTY_NAME).value);
		}
		query.elemMatch("contents", contentQuery);
		
		return query;
	}
	

}
