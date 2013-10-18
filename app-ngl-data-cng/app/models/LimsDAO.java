package models;


import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceHelpers;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import models.utils.instance.ContainerSupportHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import play.Logger;
import validation.ContextValidation;


/**
 * @author dnoisett, 16-10-2013
 *
 */
@Repository
public class LimsDAO {

	private JdbcTemplate jdbcTemplate;

	private static final String CONTAINER_CATEGORY_CODE= "lane";
	private static final String CONTAINER_STATE_CODE="F";
	private static final String LIMS_CODE="limsCode";
	protected static final String PROJECT_CATEGORY_CODE = "default";
	protected static final String PROJECT_TYPE_CODE_DEFAULT = "default-project";
	protected static final String IMPORT_CATEGORY_CODE="sample-import";
	protected static final String SAMPLE_TYPE_CODE_DEFAULT = "unknown";
	protected static final String SAMPLE_CATEGORY_CODE = "default";
	
	
	@Autowired
	@Qualifier("lims")
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}


	public List<Project> findProjectsToCreate(final ContextValidation contextError) throws SQLException, DAOException {
		
		//use view v_project_tongl
		List<Project> results = this.jdbcTemplate.query("select * from v_project_tongl;",new Object[]{} ,new RowMapper<Project>() {

			@SuppressWarnings("rawtypes")
			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {

				Project project = new Project(rs.getString("code"), rs.getString("name").trim());
				project.typeCode=PROJECT_TYPE_CODE_DEFAULT;
				project.properties= new HashMap<String, PropertyValue>();
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
	

	
	
	public Sample commonSampleMapRow(ResultSet rs, int rowNum, ContextValidation contextError) throws SQLException {
			
			Sample sample = new Sample();
			sample.traceInformation.setTraceInformation(InstanceHelpers.getUser());

			sample.code=rs.getString("code"); //barcode
			Logger.debug("Sample code :"+sample.code);
			
			String sampleTypeCode=SAMPLE_TYPE_CODE_DEFAULT;
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
			sample.projectCodes.add(rs.getString("project")); // t_project.name

			sample.name=rs.getString("name"); // barcode
			sample.referenceCollab= null; // stockbarcode ?
			sample.taxonCode=rs.getString("taxon_code"); // t_org.ncbi_taxon_id

			sample.comments=new ArrayList<Comment>(); // comments
			sample.comments.add(new Comment(rs.getString("comments")));
					
			if(sample.properties==null){ 
				sample.properties=new HashMap<String, PropertyValue>();
			}
		    sample.properties.put("taxonSize", new PropertySingleValue(rs.getDouble("taxonsize")));
		    sample.properties.put("isFragmented", new PropertySingleValue(rs.getBoolean("isfragmented")));
		    sample.properties.put("isAdapters", new PropertySingleValue(rs.getBoolean("isadapters")));
		    sample.properties.put("limsCode", new PropertySingleValue(rs.getInt("lims_code")));
					
			sample.importTypeCode="default-import";
			return sample;
	}
	
	
	public List<Sample> findSamplesToCreate(final ContextValidation contextError, String sampleCode) throws SQLException, DAOException {
		
		List<Sample> results = null;
		
		if (sampleCode != null) { 
			results = this.jdbcTemplate.query("select * from v_sample_tongl;",new Object[]{sampleCode}
			,new RowMapper<Sample>() {
				@SuppressWarnings("rawtypes")
				public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					@SuppressWarnings("rawtypes")
					Sample s=  commonSampleMapRow(rs0, rowNum0, ctxErr); 
					return s;
				}
			});
		}
		else { // mass loading
			results = this.jdbcTemplate.query("select * from v_sample_tongl;",new Object[]{}
			,new RowMapper<Sample>() {
				@SuppressWarnings("rawtypes")
				public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					@SuppressWarnings("rawtypes")
					Sample s=  commonSampleMapRow(rs0, rowNum0, ctxErr); 
					return s;
				}
			});			
		}
		
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
	 * TODO :  find lanes who have flag 'available=true'
	 * 
	 * method for mass loading
	 * @param contextError
	 * @return
	 */
	public List<Container> findContainersToCreate(ContextValidation contextError){

		List<Container> results = this.jdbcTemplate.query("select * from v_flowcell_tongl where available = true;",new Object[]{} 
		,new RowMapper<Container>() {

			@SuppressWarnings("rawtypes")
			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {

				Container container = new Container();
				
				container.code=rs.getString("code");
				Logger.debug("Container :"+rs.getString("code"));
				
				container.categoryCode=CONTAINER_CATEGORY_CODE;
				
				container.processTypeCode = null;
				
				container.projectCodes=new ArrayList<String>();
				// TODO : change test value 
				container.projectCodes.add(rs.getString("project"));
				
				container.sampleCodes=new ArrayList<String>();
				container.sampleCodes.add(rs.getString("code_sample"));
				
				container.fromExperimentTypeCodes = new ArrayList<String>(); //not required
				
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
				content.properties.put("indexBq",new PropertySingleValue(rs.getString("tag")));
				content.properties.put("percentPerLane",new PropertySingleValue(rs.getString("percent_per_lane")));
				
				// define container support attributes
				try {
					container.support=ContainerSupportHelper.getContainerSupport("lane", rs.getInt("nb_container"),rs.getString("code_support"),"1",rs.getString("column")); 
				}
				catch(DAOException e) {
					Logger.info("Can't get container support !"); 
				}
				
				container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
				
				container.comments=new ArrayList<Comment>();	
				//just one comment for one lane (container)
				container.comments.add(new Comment(rs.getString("comment")));
				
				container.valid=null;

				container.properties= new HashMap<String, PropertyValue>();
				container.properties.put(LIMS_CODE,new PropertySingleValue(rs.getInt("lims_code")));

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
	
	
	
	/**
	 * 
	 * @param projects
	 * @param contextError
	 */
	public void updateImportDate(String tableName, String keyColumn, String[] keyCodes, ContextValidation contextError) {
		
		contextError.addKeyToRootKeyName("updateImportDateForSamples");
		
		String sql="UPDATE " + tableName + " SET nglimport_date = ? WHERE " + keyColumn + " = ANY (?)";
		//alternative sql
		//String sql="UPDATE " + tableName + " SET nglimport_date = ? WHERE name IN (SELECT UNNEST(?::VARCHAR[]))" ; 
		
		Logger.debug("sql = "+ sql);
		
		try {
			Connection conn = this.jdbcTemplate.getDataSource().getConnection();
			PreparedStatement pst = conn.prepareStatement(sql);
			
			pst.setObject(1, new Date(), Types.TIMESTAMP);
			pst.setArray(2, conn.createArrayOf("text", keyCodes));
			//arrayLiteral = "{A,\"B \", C,D}"
			//pst.setString(2, arrayLiteral)
            pst.execute();
            pst.close();
		}
		catch(Exception e) {
			contextError.addErrors("",e.getMessage(), sql, new Date(), keyCodes);
		}
		contextError.removeKeyFromRootKeyName("updateImportDateForSamples");
	}
	
	
	/**
	 * 
	 * @param contextError
	 * @return
	 */
	public List<Container> findContainersToCreate(String procedure,ContextValidation contextError, final String containerCategoryCode, final String containerStateCode, final String experimentTypeCode){

		List<Container> results = this.jdbcTemplate.query(procedure,new Object[]{} 
		,new RowMapper<Container>() {

			@SuppressWarnings("rawtypes")
			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {

				Container container = null;
				try {
					//TODO : modify createContainerFromResultSet for running with PostgresSql
					container = ContainerHelper.createContainerFromResultSet(rs, containerCategoryCode,containerStateCode,experimentTypeCode);
				} catch (DAOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return container;
			}

		});        

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

