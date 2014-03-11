package controllers.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import models.LimsCNGDAO;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.sample.description.SampleType;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerSupportHelper;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;
import play.Logger;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

/**
 * Update tag and add tagCategory in the Container & Support collections
 * @author dnoisett
 * 
 */
@Repository
public class MigrationTag extends CommonController {
	
	private static final String CONTAINER_COLL_NAME_BCK = InstanceConstants.CONTAINER_COLL_NAME + "_BCKmigrationTag";	
	private static final String SUPPORT_COLL_NAME_BCK = InstanceConstants.SUPPORT_COLL_NAME + "_BCKmigrationTag";
	
	private static JdbcTemplate jdbcTemplate;
	private static DataSource dataSource;

	protected static final String PROJECT_TYPE_CODE_DEFAULT = "default-project";
	protected static final String PROJECT_STATE_CODE_DEFAULT = "IP";
	protected static final String IMPORT_CATEGORY_CODE="sample-import";
	protected static final String SAMPLE_TYPE_CODE_DEFAULT = "default-sample-cng";
	protected static final String SAMPLE_USED_TYPE_CODE = "default-sample-cng";	
	protected static final String IMPORT_TYPE_CODE_DEFAULT = "default-import";
	
	
	@Autowired
	@Qualifier("lims")
	public void setDataSource(DataSource dataSource) {
		//this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}
	
	
	
	
	public static List<Container> findContainer(final ContextValidation contextError) throws DAOException{

		//use v_flowcell_tongl_reprise view (specific to this migration)
		if (jdbcTemplate == null) {
			Logger.debug("jdbcTemplate is null !!!!!!!!!");
		}
		
		List<Container> results = jdbcTemplate.query("select * from v_flowcell_tongl_reprise where isavailable = true order by code, project, code_sample, tag",new Object[]{} 
		,new RowMapper<Container>() {

			@SuppressWarnings("rawtypes")
			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {

				Container container = new Container();
				
				container.code=rs.getString("code");
				//Logger.debug("Container code :"+container.code);
				
				if (rs.getString("project")!=null) {
					container.projectCodes=new ArrayList<String>();
					container.projectCodes.add(rs.getString("project"));
				}
				
				if (rs.getString("code_sample")!=null) {
					Content content = new Content();
					content.sampleUsed=new SampleUsed();
					content.sampleUsed.sampleCode=rs.getString("code_sample");
					
					String sampleTypeCode = SAMPLE_USED_TYPE_CODE;
					SampleType sampleType=null;
					try {
						sampleType = SampleType.find.findByCode(sampleTypeCode);
					} catch (DAOException e) {
						Logger.error("",e);
						return null;
					}
					if( sampleType==null ){
						contextError.addErrors("code", "error.codeNotExist", sampleTypeCode, content.sampleUsed.sampleCode);
						return null;
					}		
					
					content.sampleUsed.typeCode = sampleType.code;
					content.sampleUsed.categoryCode = sampleType.category.code;
					
					content.properties = new HashMap<String, PropertyValue>();
					
					if(rs.getString("tag")!=null) { 
						content.properties.put("tag",new PropertySingleValue(rs.getString("tag")));
						content.properties.put("tagCategory",new PropertySingleValue(rs.getString("tagcategory")));
					}
					else {
						content.properties.put("tag",new PropertySingleValue("-1")); // specific value for making comparison, suppress it at the end of the function...
						content.properties.put("tagCategory",new PropertySingleValue("-1"));
					}
					
					if(rs.getString("percent_per_lane")!=null) { 
						content.properties.put("percentPerLane",new PropertySingleValue(rs.getString("percent_per_lane")));
					}
					else {
						content.properties.put("percentPerLane",new PropertySingleValue("-1")); 
					}
					
					container.contents=new ArrayList<Content>();
					container.contents.add(content);			
					
					container.sampleCodes=new ArrayList<String>();
					container.sampleCodes.add(rs.getString("code_sample"));
				}
				
				return container;
			}
		});       		
		
		//affect all the project codes /samples /tags to the same container (for having unique codes of containers) 
		/// required to have an ordered list (see ORDER BY clause in the sql of the view)
		int pos = 0;
		int x=1;
		int listSize  =  results.size(); 
		Boolean insertContent = false;
		
		while (pos < listSize-1)   {
			
			while ( (pos < listSize-1) && (results.get(pos).code.equals( results.get(pos+x).code))   ) {
				
				insertContent = false;
				// difference between the two projectCode
				if (! results.get(pos).projectCodes.get(0).equals(results.get(pos+x).projectCodes.get(0))) {
					if (! results.get(pos).projectCodes.contains(results.get(pos+x).projectCodes.get(0))) {
						
						results.get(pos).projectCodes.add( results.get(pos+x).projectCodes.get(0) ); 
					}
				}
				// difference between the two sampleCode
				if (! results.get(pos).sampleCodes.get(0).equals(results.get(pos+x).sampleCodes.get(0))) {
					if (! results.get(pos).sampleCodes.contains(results.get(pos+x).sampleCodes.get(0))) {
							
						results.get(pos).sampleCodes.add( results.get(pos+x).sampleCodes.get(0) );
						
						createContent(results, pos, pos+x); 
						insertContent = true;
					}
				}
								
				// all the difference have been reported on the first sample found (at the position pos)
				// so we can delete the sample at the position (posNext)
				results.remove(pos+x);
				//ajust list size
				listSize--;
			}
			pos++;
		}	
		
		//for remove null tags
		for (Container r : results) {
			for (int i=0; i<r.contents.size(); i++) {
				if (r.contents.get(i).properties.get("tag").value.equals("-1")) {
					r.contents.get(i).properties.remove("tag");
				}
				if (r.contents.get(i).properties.get("tagCategory").value.equals("-1")) {
					r.contents.get(i).properties.remove("tagCategory");
				}
				if (r.contents.get(i).properties.get("percentPerLane").value.equals("-1")) {
					r.contents.get(i).properties.remove("percentPerLane");
				}
			}
		}
		
		return results;
	}
	
	
	private static List<Container>  createContent(List<Container> results, int posCurrent, int posNext) throws DAOException{
		Content content = new Content();
		content.sampleUsed=new SampleUsed();
		content.sampleUsed.sampleCode= results.get(posNext).sampleCodes.get(0);
		
		SampleType sampleType=null;
		sampleType = SampleType.find.findByCode(SAMPLE_USED_TYPE_CODE);	
		content.sampleUsed.typeCode = sampleType.code;
		content.sampleUsed.categoryCode = sampleType.category.code;
		
		content.properties = new HashMap<String, PropertyValue>();
		content.properties.put("tag",new PropertySingleValue( results.get(posNext).contents.get(0).properties.get("tag").value  ));
		content.properties.put("tagCategory",new PropertySingleValue( results.get(posNext).contents.get(0).properties.get("tagCategory").value ));
		
		content.properties.put("percentPerLane",new PropertySingleValue( results.get(posNext).contents.get(0).properties.get("percentPerLane").value ));
		
		results.get(posCurrent).contents.add(content); 
		
		return results;
	}

	
	
	public static Result migration() {
		
		JacksonDBCollection<Container, String> containersCollBck = MongoDBDAO.getCollection(CONTAINER_COLL_NAME_BCK, Container.class);
		if (containersCollBck.count() == 0) {
	
			//backup currents collections
			backUpContainer();
			backUpSupport();
			
			Logger.info("Migration container starts");
		
			//find collection up to date
			ContextValidation contextError = new ContextValidation();
			List<Container> newContainers = null;
			try {
				newContainers = findContainer(contextError);
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
			ContainerSupport support;
			Boolean bChangeTag;
			
			//find current collection
			List<Container> oldContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();

			//iteration over current collection
			for (Container oldContainer : oldContainers) {
				
			 //if (oldContainer.code.equals("62VBAAAXX_4")) {
				 
				support = null;
				bChangeTag = false;
				
				for (int i=0; i<oldContainer.contents.size(); i++) {
					
					if (m2.get(oldContainer.code) != null) {
						
						strValue = (String) (m2.get(oldContainer.code)).get(oldContainer.contents.get(i).sampleUsed.sampleCode);
						if (strValue != null && strValue.contains("_")) {
							
							newTag = strValue.substring(0, strValue.indexOf("_")); 
							tagCategory =  strValue.substring(strValue.indexOf("_")+1);
							
							if (newTag != "") {
								
								if (oldContainer.contents.get(i).properties != null) {
								
									PropertySingleValue pTag = new PropertySingleValue();
									pTag.value = newTag;
									PropertySingleValue pTagCategory = new PropertySingleValue();
									pTagCategory.value = tagCategory;
									
									//update tag
									if (oldContainer.contents.get(i).properties.get("tag") != null) {
										oldContainer.contents.get(i).properties.get("tag").value = newTag;
									}
									else {
										Logger.debug("Insert tag for this container, container.code=" + oldContainer.code + ", i=" + i );
										
										oldContainer.contents.get(i).properties.put("tag", pTag);
									}
									
									//update tagCategory
									oldContainer.contents.get(i).properties.put("tagCategory", pTagCategory);
									
									bChangeTag = true;
									
									//find support
									support = MongoDBDAO.findOne(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", oldContainer.support.supportCode));
									//oldSupport = support;
									
									support.properties = new HashMap<String, PropertyValue>();
									
									support.properties.put("tagCategory", pTagCategory);
										
									
								
								}
								else {
									Logger.debug("No properties for this container, container.code=" + oldContainer.code + ", i=" + i );
								}
							}
							else {
								Logger.debug("newTag=" + newTag + ", container.code=" + oldContainer.code + ", i=" + i ); 
							}
																					
						}
					}

				}

				if (bChangeTag) {
					Logger.info("update container !");
					WriteResult r = (WriteResult)MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code", oldContainer.code),   
						DBUpdate.set("contents", oldContainer.contents));
					
					if(StringUtils.isNotEmpty(r.getError())){
						Logger.error("Update CONT : "+oldContainer.code+" / "+r.getError());
					}
	
					//update tagCategory in support
					r = (WriteResult)MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", oldContainer.support.supportCode),
							DBUpdate.set("properties", support.properties)); 
					
					if(StringUtils.isNotEmpty(r.getError())){
						Logger.error("Update CONT : "+r.getError());
					}
				}
				//break;
			 //} //fin test	
			}	//end for containers
			
			
		} else {
			Logger.info("Migration containers already executed !");
		}
		
		Logger.info("Migration container (tag) Finish");
		return ok("Migration container (tag) Finish");
	
	}
	

	/*
	public static Result migrationTest() {
		
		Logger.info("Migration TEST container (tag) 	START");
		
		PropertySingleValue pTagCategory = new PropertySingleValue();
		pTagCategory.value = "TEST01";
		
		String containerCode = "62VBAAAXX_4";
		
		Container c = MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code", containerCode));
		
		c.contents.get(0).properties.put("tagCategory", pTagCategory);
		
		Logger.debug("value of tagCategory = " + c.contents.get(0).properties.get("tagCategory").value); 
			
		
		WriteResult r = (WriteResult) MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code", c.code),   
				DBUpdate.set("contents", c.contents));
			
		if(StringUtils.isNotEmpty(r.getError())){
				Logger.error("Update CONT : "+c.code+" / "+r.getError());
		}
		
		Logger.info("Migration TEST container (tag) END");
		
		
		return ok("FIN");
		
	}
	*/

	
	private static void backUpContainer() {
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" starts");
		MongoDBDAO.save(CONTAINER_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" ended");
	}
	
	private static void backUpSupport() {
		Logger.info("\tCopie "+InstanceConstants.SUPPORT_COLL_NAME+" starts");
		MongoDBDAO.save(SUPPORT_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class).toList());
		Logger.info("\tCopie "+InstanceConstants.SUPPORT_COLL_NAME+" ended");
		
	}

}
