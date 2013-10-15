package models;

import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceHelpers;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerSupportHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import play.Logger;
import services.description.common.LevelService;
import services.description.common.MeasureService;
import validation.ContextValidation;


/**
 * @author dnoisett
 *
 */
@Repository
public class LimsDAO {

	private JdbcTemplate jdbcTemplate;

	private static final String CONTAINER_CATEGORY_CODE= "lane";
	private static final String CONTAINER_STATE_CODE="IWP";
	private static final String CONTAINER_PROPERTIES_BQ="tag";
	private static final String LIMS_CODE="limsCode";
	private static final String SAMPLE_ADPATER="isAdapters";
	private static final String RECEPTION_DATE ="receptionDate";
	
	protected static final String PROJECT_CATEGORY_CODE = "default";
	//protected static final String PROJECT_TYPE_CODE_FG = "france-genomique";
	protected static final String PROJECT_TYPE_CODE_DEFAULT = "default-project";
	protected static final String PROJECT_PROPERTIES_FG_GROUP="fgGroup";

	protected static final String IMPORT_CATEGORY_CODE="sample-import";
	
	protected static final String SAMPLE_TYPE_CODE_DEFAULT = "unknown";
	protected static final String SAMPLE_CATEGORY_CODE = "default";
	
	protected static final String FLOWCELL_CATEGORY_CODE = "flowcell 1";
	
	
	@Autowired
	@Qualifier("lims")
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}


	public List<Project> findProjectsToCreate(final ContextValidation contextError) throws SQLException, DAOException {
		
		//use view v_projectstongl
		List<Project> results = this.jdbcTemplate.query("select * from v_projectstongl;",new Object[]{} ,new RowMapper<Project>() {

			@SuppressWarnings("rawtypes")
			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {

				Project project = new Project(rs.getString("code"), rs.getString("name1").trim());
				//String fgGroupe=rs.getString("groupefg");
				//if(fgGroupe==null){
					project.typeCode=PROJECT_TYPE_CODE_DEFAULT;
				//}
				//else {
				//	project.typeCode=PROJECT_TYPE_CODE_FG;
					project.properties= new HashMap<String, PropertyValue>();
				//	project.properties.put(PROJECT_PROPERTIES_FG_GROUP, new PropertySingleValue(fgGroupe));
				//}
			
				project.categoryCode=PROJECT_CATEGORY_CODE;
				project.stateCode="IP";
				InstanceHelpers.updateTraceInformation(project.traceInformation);
				
				project.comments = new ArrayList<Comment>(); 
				// just one comment for one project
				InstanceHelpers.addComment(rs.getString("comments"), project.comments);
				
				return project;
			}
		});
		
		return results;
	}
	

	
	
	
	public List<Sample> findSamplesToCreate(final ContextValidation contextError) throws SQLException, DAOException {
		
		List<Sample> results = this.jdbcTemplate.query("select * from v_sampletongl;",new Object[]{} 
		,new RowMapper<Sample>() {

			@SuppressWarnings("rawtypes")
			public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Sample sample = new Sample();
				sample.traceInformation.setTraceInformation(InstanceHelpers.getUser());

				sample.code=rs.getString("code"); //barcode
				//Logger.debug("Sample code :"+sample.code);
				
				String sampleTypeCode=SAMPLE_TYPE_CODE_DEFAULT;
				//Logger.debug("Sample Type :"+sampleTypeCode);

				if(sampleTypeCode==null){
					contextError.addErrors( "typeCode", "limsdao.error.emptymapping", sample.code);
					return null;
				}
				
				SampleType sampleType=null;
				try {
					sampleType = SampleType.find.findByCode(sampleTypeCode);
				} catch (DAOException e) {
					Logger.debug("",e);
					return null;
				}
				if( sampleType==null ){
					contextError.addErrors("code", "error.codeNotExist", sampleTypeCode, sample.code);
					return null;
				}
				
				sample.typeCode=sampleTypeCode;
				sample.categoryCode=sampleType.category.code;
			
				sample.projectCodes=new ArrayList<String>();
				sample.projectCodes.add(rs.getString("project_code")); // jointure t_project.name

				sample.name=rs.getString("name1"); // barcode
				sample.referenceCollab= null; // stockbarcode ?
				sample.taxonCode=rs.getString("taxoncode"); // t_org.ncbi_taxon_id

				sample.comments=new ArrayList<Comment>(); // comments
				sample.comments.add(new Comment(rs.getString("comments")));

				//pb : column with different types (not only varchar) !
				//for(PropertyDefinition propertyDefinition :sampleType.propertiesDefinitions) {
				//	String code=null;
				//	try{
				//		value=rs.getString(propertyDefinition.code.toLowerCase());
				//		Logger.info("code of property : " + value);
						
						if(sample.properties==null){ 
							sample.properties=new HashMap<String, PropertyValue>();
						}
					    sample.properties.put("taxonSize", new PropertySingleValue(rs.getDouble("taxonsize")));
					    sample.properties.put("isFragmented", new PropertySingleValue(rs.getBoolean("isfragmented")));
					    sample.properties.put("isAdapters", new PropertySingleValue(rs.getBoolean("isadapters")));
					    sample.properties.put("limsCode", new PropertySingleValue(rs.getInt("limscode")));
						
				//	}catch (SQLException e) {
				//		Logger.info("Property "+propertyDefinition.code+" not exists in v_sampletongl");
				//	}
				//}
				

				sample.importTypeCode="default-import";
				//Logger.debug("Import Type "+sample.importTypeCode);
				return sample;
			}

		});     
		
		//affect all the project codes to a same sample 
		/// required to have an ordered list (see ORDER BY clause in the sql of the view)
		int pos = 0;
		int x=1;
		int listSize  =  results.size(); 
		while (pos < listSize-1    )   {
			while ( (pos < listSize-1) && (results.get(pos).code.equals( results.get(pos+x).code ))   ) {
				// difference between the two project codes
				if (! results.get(pos).projectCodes.get(0).equals(results.get(pos+x).projectCodes.get(0))) {
					results.get(pos).projectCodes.add( results.get(pos+x).projectCodes.get(0) ); 
				}
				// difference between the two comments
				if (! results.get(pos).comments.get(0).equals(results.get(pos+x).comments.get(0))) {
					results.get(pos).comments.add( results.get(pos+x).comments.get(0) ); 
				}
				// all the difference have been reported on the first sample found (at the position pos)
				// so we can delete the sample at the position (posNext)
				results.remove(pos+x);
				listSize--;
			}
			pos++;
		}
		return results;
	}
	
	
	
	/**
	 * TODO :  find lanes who have flag 'available=0' ( this flag is update to 1 when lane exists in NGL database)
	 * 
	 * @param contextError
	 * @return
	 */
	public List<Container> findContainersToCreate(ContextValidation contextError){

		List<Container> results = this.jdbcTemplate.query("select distinct * from v_flowcelltongl;",new Object[]{} 
		,new RowMapper<Container>() {

			@SuppressWarnings("rawtypes")
			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				/* flowcell_id, lane_id, name1, code, barcode, y, code_sample, idx*/ 

				Container container = new Container();
				
				container.code=String.valueOf(rs.getInt("lane_id"));
				Logger.debug("Container :"+rs.getString("code"));
				
				container.categoryCode=CONTAINER_CATEGORY_CODE;
				
				container.processTypeCode = null;
				
				container.projectCodes=new ArrayList<String>();
				// TODO : change test value 
				//container.projectCodes.add(rs.getString("project_code"));
				container.projectCodes.add("UNKNOWN"); 
				
				container.sampleCodes=new ArrayList<String>();
				container.sampleCodes.add(rs.getString("code_sample"));
				
				//container.fromExperimentTypeCodes = new ArrayList<String>(); //not required
				
				container.fromPurifingCode = null; // not required
				
				
				container.resolutionCode = null; // not required
				
				container.stateCode=CONTAINER_STATE_CODE; // required
				
				//define content
				Content content = new Content();
				content.sampleUsed=new SampleUsed();
				content.sampleUsed.sampleCode=rs.getString("code_sample");
				
				//TODO : change default value
				content.sampleUsed.categoryCode = "unknown"; // required
				content.sampleUsed.typeCode = "unknown"; // required
				
				container.contents=new ArrayList<Content>();
				container.contents.add(content);
				
				content.properties = new HashMap<String, PropertyValue>();
				content.properties.put("index",new PropertySingleValue(rs.getString("idx")));
				
				// define container support attributes
				try {
					container.support=ContainerSupportHelper.getContainerSupport("lane", rs.getInt("nb_lanes"),rs.getString("barcode"),"1",rs.getString("y")); 
				}
				catch(DAOException e) {
					Logger.info("Can't get container support !"); 
				}
				
				container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
				

				container.comments=new ArrayList<Comment>();	
				//just one comment for one lane (container)
				container.comments.add(new Comment(rs.getString("comments")));
				
				
				
				// TODO : to verify
				/*
				switch (rs.getInt("control")) {
				case 0: 
					container.valid= TBoolean.FALSE;
					break; 
				case 1: 
					container.valid= TBoolean.TRUE;
					break; 
				case 2: 
					container.valid= TBoolean.UNSET;
					break; 
				default: 
					container.valid= TBoolean.UNSET;
					break; 
				}
				*/

				container.properties= new HashMap<String, PropertyValue>();
				container.properties.put(LIMS_CODE,new PropertySingleValue(rs.getInt("lane_id")));

				//TODO: get measures;
				//container.mesuredConcentration=new PropertySingleValue((float) 0);
				//container.mesuredVolume=new PropertySingleValue((float) 0);
				//container.mesuredQuantity=new PropertySingleValue((float) 0); 
				//container.calculedVolume =new PropertySingleValue((float) 0);  


			
				return container;
			}

		});       
		
		//affect all the project codes /samples to the first container 
		/// required to have an ordered list (see ORDER BY clause in the sql of the view)
		int pos = 0;
		int x=1;
		int listSize  =  results.size(); 
		while (pos < listSize-1    )   {
			while ( (pos < listSize-1) && (results.get(pos).code.equals( results.get(pos+x).code ))   ) {
				// difference between the two project codes
				if (! results.get(pos).projectCodes.get(0).equals(results.get(pos+x).projectCodes.get(0))) {
					results.get(pos).projectCodes.add( results.get(pos+x).projectCodes.get(0) ); 
				}
				// difference between the two comments
				if (! results.get(pos).sampleCodes.get(0).equals(results.get(pos+x).sampleCodes.get(0))) {
					results.get(pos).sampleCodes.add( results.get(pos+x).sampleCodes.get(0) ); 
				}
				// all the difference have been reported on the first sample found (at the position pos)
				// so we can delete the sample at the position (posNext)
				results.remove(pos+x);
				listSize--;
			}
			pos++;
		}
		return results;
	}


	public List<ListObject> getListObjectFromProcedureLims(String procedure) {
		List<ListObject> listObjects = this.jdbcTemplate.query(procedure,
				new RowMapper<ListObject>() {
			public ListObject mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ListObject value = new ListObject();
				value.name = rs.getString(1);
				value.code = rs.getString(2);
				return value;
			}
		});
		return listObjects;
	}

		

}

