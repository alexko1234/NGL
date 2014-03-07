package controllers.migration;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import models.LimsCNGDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.Logger;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

/**
 * Update tag and add tagCategory in the Container collection
 * @author dnoisett
 * 
 */

public class MigrationTag extends CommonController {
	
	private static final String CONTAINER_COLL_NAME_BCK = InstanceConstants.CONTAINER_COLL_NAME + "_BCK";
	private static final String CONTAINER_COLL_NAME_UPDATED = InstanceConstants.CONTAINER_COLL_NAME + "_UPDATED";
	protected static LimsCNGDAO limsServices= Spring.getBeanOfType(LimsCNGDAO.class);


	public static Result migration(){
		
		JacksonDBCollection<Container, String> containersCollBck = MongoDBDAO.getCollection(CONTAINER_COLL_NAME_BCK, Container.class);
		if (containersCollBck.count() == 0) {
	
			//backup current collection
			backUpContainer();
			
			Logger.info("Migration container starts");
		
			//find collection up to date
			ContextValidation contextError = new ContextValidation();
			List<Container> newContainers = null;
			try {
				newContainers = limsServices.findContainer(contextError, false);
			} catch (DAOException e) {
				Logger.debug("ERROR in findContainer()");
			}
			
			//set a map with the new values indexed by codes
			Map<String, String> m1 = new HashMap<String, String>();
			Map<String, Map> m2 = new HashMap<String, Map>();
			String m1Value = "";
			
			for (Container newContainer : newContainers) {
				for (Content newContent : newContainer.contents) {
					if (newContent.properties.get("tag") != null) {
						m1Value = (String) newContent.properties.get("tag").value + '_' + (String) newContent.properties.get("tagCategory").value;
						m1.put(newContent.sampleUsed.sampleCode, m1Value);
					}
				}
				m2.put(newContainer.code, m1);
			}
			//end of setting map 
			
			String newTag = "";
			String tagCategory = "";
			String strValue = "";
			
			//find current collection
			List<Container> oldContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();

			//iteration over current collection
			for (Container oldContainer : oldContainers) {
				
				for (int i=0; i<oldContainer.contents.size(); i++) {
										
					if (m2.get(oldContainer.code) != null) {
						
						strValue = (String) (m2.get(oldContainer.code)).get(oldContainer.contents.get(i).sampleUsed.sampleCode);
						if (strValue != null && strValue.contains("_")) {
							
							newTag = strValue.substring(0, strValue.indexOf("_")); 
							tagCategory =  strValue.substring(strValue.indexOf("_")+1);
							
							if (newTag != "") {
								
								//HashMap<String, PropertySingleValue> mapProperties = new HashMap<String, PropertySingleValue>();
								
								//List<PropertySingleValue> l = new ArrayList<PropertySingleValue>(); 
								
								if (oldContainer.contents.get(i).properties != null) {
								
									//create propertySingleValues from values
									if (oldContainer.contents.get(i).properties.get("percentperLane") != null) {
										PropertySingleValue pPercentPerLane = new PropertySingleValue(oldContainer.contents.get(i).properties.get("percentperLane").value);
										
										//mapProperties.put("percentPerLane", pPercentPerLane);
										
										//l.add(pPercentPerLane); 
									}

									PropertySingleValue pTag = new PropertySingleValue();
									pTag.value = newTag;
									PropertySingleValue pTagCategory = new PropertySingleValue();
									pTagCategory.value = tagCategory;
									
									//l.add(pTag);
									//l.add(pTagCategory);
									
									//mapProperties.put("tag", pTag);
									//mapProperties.put("tagCategory", pTagCategory);
									
									if (oldContainer.contents.get(i).properties.get("tag") != null) {
										oldContainer.contents.get(i).properties.get("tag").value = newTag;
									}
									else {
										oldContainer.contents.get(i).properties.put("tag", pTag);
									}
									
									oldContainer.contents.get(i).properties.put("tagCategory", pTagCategory); 
									
									/*
									//bug : don't insert tagCategory !
									MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
											DBQuery.and(DBQuery.is("code", oldContainer.code), 
													DBQuery.is("contents.sampleUsed.sampleCode", oldContainer.contents.get(i).sampleUsed.sampleCode)),  
											DBUpdate.unset("contents."+ i+ ".properties")
											//.set("contents." + i + ".properties", l)
											.pushAll("contents." + i + ".properties", l)
											); 									
									*/

								
								}
							}
							
														
						}
					}
					
				}

				//save instead of update !
				MongoDBDAO.save(CONTAINER_COLL_NAME_UPDATED, oldContainer); 
			}	//end for
			
			JacksonDBCollection<Container, String> containersColl = MongoDBDAO.getCollection(InstanceConstants.CONTAINER_COLL_NAME, Container.class);
			containersColl.drop();
			
			JacksonDBCollection<Container, String> containersCollUpd = MongoDBDAO.getCollection(CONTAINER_COLL_NAME_UPDATED, Container.class);
			containersCollUpd.rename(InstanceConstants.CONTAINER_COLL_NAME); 
			
			
			//limsServices.updateLimsContainers(containersUpdated, contextError);
			
		} else {
			Logger.info("Migration containers already executed !");
		}
		
		Logger.info("Migration container (tag) Finish");
		return ok("Migration container (tag) Finish");
	
	}

	private static void backUpContainer() {
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" starts");
		MongoDBDAO.save(CONTAINER_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" ended");
		
	}

}
