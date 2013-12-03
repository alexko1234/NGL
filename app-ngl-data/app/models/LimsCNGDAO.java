package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerSupportHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import play.Logger;
import validation.ContextValidation;

/**
 * @author dnoisett
 *
 */
@Repository
public class LimsCNGDAO {

	private JdbcTemplate jdbcTemplate;

	private static final String CONTAINER_CATEGORY_CODE= "lane";
	private static final String CONTAINER_STATE_CODE="A";
	protected static final String PROJECT_TYPE_CODE_DEFAULT = "default-project";
	protected static final String IMPORT_CATEGORY_CODE="sample-import";
	protected static final String SAMPLE_TYPE_CODE_DEFAULT = "default-sample-cng";
	protected static final String SAMPLE_USED_TYPE_CODE = "default-sample-cng";	
	protected static final String IMPORT_TYPE_CODE_DEFAULT = "default-import";
	
	
	@Autowired
	@Qualifier("lims")
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}


	public List<Project> findProjectToCreate(final ContextValidation contextError) throws SQLException, DAOException {
		
		List<Project> results = this.jdbcTemplate.query("select * from v_project_tongl", new Object[]{}, new RowMapper<Project>() {

			@SuppressWarnings("rawtypes")
			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {

				Project project = new Project(rs.getString("code"), rs.getString("name").trim());
				project.typeCode=PROJECT_TYPE_CODE_DEFAULT;
				
				ProjectType projectType=null;
				try {
					projectType = ProjectType.find.findByCode(project.typeCode);
				} catch (DAOException e) {
					Logger.debug("",e);
					return null;
				}
				if( projectType==null ){
					contextError.addErrors("code", "error.codeNotExist", project.typeCode, project.code);
					return null;
				}
				
				project.categoryCode=projectType.category.code;
				
				project.stateCode="IP";
				InstanceHelpers.updateTraceInformation(project.traceInformation);

				// just one comment for one project
				if (rs.getString("comments") != null ) {
					project.comments = new ArrayList<Comment>(); 
					InstanceHelpers.addComment(rs.getString("comments"), project.comments);
				}
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
			
			SampleType sampleType=null;
			try {
				sampleType = SampleType.find.findByCode(sampleTypeCode);
			} catch (DAOException e) {
				Logger.debug("",e);
				return null;
			}
			if ( sampleType==null ) {
				contextError.addErrors("code", "error.codeNotExist", sampleTypeCode, sample.code);
				return null;
			}
			
			sample.typeCode=sampleType.code;
			sample.categoryCode=sampleType.category.code;
		
			sample.projectCodes=new ArrayList<String>();
			if (rs.getString("project") != null) {
				sample.projectCodes.add(rs.getString("project")); // t_project.name
			}
			else {
				sample.projectCodes.add(" "); 
			}

			sample.name=rs.getString("name"); // t_sample.barcode
			
			sample.referenceCollab= rs.getString("ref_collab"); // t_individual_id.name
			
			sample.taxonCode=rs.getString("taxon_code"); // t_org.ncbi_taxon_id
			
			sample.comments=new ArrayList<Comment>(); // comments
			
			if (rs.getString("comments") != null) {
				sample.comments.add(new Comment(rs.getString("comments")));
			}
			else {
				sample.comments.add(new Comment(" ")); 
			}
					
			sample.properties=new HashMap<String, PropertyValue>();
			sample.properties.put("limsCode", new PropertySingleValue(rs.getInt("lims_code")));
	
			sample.importTypeCode=IMPORT_TYPE_CODE_DEFAULT;
			return sample;
	}
	
	public List<Sample> findSampleToCreate(final ContextValidation contextError) throws SQLException, DAOException {
		return findSampleToCreate(contextError, null);
		
	}
	
	public List<Sample> findSampleToCreate(final ContextValidation contextError, String sampleCode) throws SQLException, DAOException {
		
		List<Sample> results = null;
		
		if (sampleCode != null) { 
			results = this.jdbcTemplate.query("select * from v_sample_tongl where code=? order by code, project, comments", new Object[]{sampleCode}
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
			results = this.jdbcTemplate.query("select * from v_sample_tongl order by code, project, comments",new Object[]{}
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
					if (! results.get(pos).projectCodes.contains(results.get(pos+x).projectCodes.get(0))) {
						results.get(pos).projectCodes.add( results.get(pos+x).projectCodes.get(0) ); 
					}
				}
				// difference between the two comments
				if (! results.get(pos).comments.get(0).equals(results.get(pos+x).comments.get(0))) {
					if (! results.get(pos).comments.contains(results.get(pos+x).comments.get(0))) {
						results.get(pos).comments.add( results.get(pos+x).comments.get(0) ); 
					}
				}
				// all the difference have been reported on the first sample found (at the position pos)
				// so we can delete the sample at the position (posNext)
				results.remove(pos+x);
				listSize--;
			}
			pos++;
		}
		
		//for remove null comment or project
		for (Sample s : results) {
			for (int i=0; i<s.comments.size(); i++) {
				if (s.comments.get(i).equals(" ")) {
					s.comments.remove(i);
				}
			}
			for (int i=0; i<s.projectCodes.size(); i++) {
				if (s.projectCodes.get(i).equals(" ")) {
					s.projectCodes.remove(i);
				}
			}
		}
		
		return results;
	}
	
	
	
	/**
	 * 
	 * method for mass loading
	 * @param contextError
	 * @return
	 * @throws DAOException 
	 */
	public List<Container> findContainerToCreate(final ContextValidation contextError) throws DAOException{

		//verification OK for codes in ('C01BBACXX_1','D0358ACXX_3') 
		List<Container> results = this.jdbcTemplate.query("select * from v_flowcell_tongl where isavailable = true order by code, project, code_sample, tag",new Object[]{} 
		,new RowMapper<Container>() {

			@SuppressWarnings("rawtypes")
			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {

				Container container = new Container();
				
				container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
				
				container.code=rs.getString("code");
				Logger.debug("Container :"+rs.getString("code"));
				
				container.categoryCode=CONTAINER_CATEGORY_CODE;
				
				if (rs.getString("comment") != null) {
					container.comments=new ArrayList<Comment>();	
					//just one comment for one lane (container)
					container.comments.add(new Comment(rs.getString("comment")));
				}
				
				container.stateCode=CONTAINER_STATE_CODE; // required
				
				container.valid=null;
				
				// define container support attributes
				try {
					container.support=ContainerSupportHelper.getContainerSupport("lane", rs.getInt("nb_container"),rs.getString("code_support"),"1",rs.getString("column")); 
				}
				catch(DAOException e) {
					Logger.info("Can't get container support !"); 
				}
				
				container.properties= new HashMap<String, PropertyValue>();
				container.properties.put("limsCode",new PropertySingleValue(rs.getInt("lims_code")));
				
				
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
						Logger.debug("",e);
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
					}
					else {
						content.properties.put("tag",new PropertySingleValue("-1")); // specific value for making comparaison, suppress it at the end of the function...
					}
					
					if(rs.getString("percent_per_lane")!=null) { 
						content.properties.put("percentPerLane",new PropertySingleValue(rs.getString("percent_per_lane")));
					}
					else {
						content.properties.put("percentPerLane",new PropertySingleValue("-1")); 
					}
					
					container.contents=new ArrayList<Content>();
					container.contents.add(content);					
				}
				
				container.sampleCodes=new ArrayList<String>();
				container.sampleCodes.add(rs.getString("code_sample"));
			
				container.fromPurifingCode = null; // not required				
				container.resolutionCode = null; // not required

				return container;
			}
		});       
		
		//affect all the project codes /samples /tags to the same container (for having unique codes of containers) 
		/// required to have an ordered list (see ORDER BY clause in the sql of the view)
		int pos = 0;
		int x=1;
		int listSize  =  results.size(); 
		Boolean insertContent = false;
		
		while (pos < listSize-1    )   {
			
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
				
				// difference between the two tag (and same sampleCode)
				if (!  results.get(pos).contents.get(0).properties.get("tag").value.equals(  results.get(pos+x).contents.get(0).properties.get("tag").value  ) ) {
					if (!insertContent) {
						
						createContent(results, pos, pos+x);
						
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
				if (r.contents.get(i).properties.get("percentPerLane").value.equals("-1")) {
					r.contents.get(i).properties.remove("percentPerLane");
				}
			}
		}
		
		return results;
	}
	
	
	private List<Container>  createContent(List<Container> results, int posCurrent, int posNext) throws DAOException{
		Content content = new Content();
		content.sampleUsed=new SampleUsed();
		content.sampleUsed.sampleCode= results.get(posNext).sampleCodes.get(0);
		
		SampleType sampleType=null;
		sampleType = SampleType.find.findByCode(SAMPLE_USED_TYPE_CODE);	
		content.sampleUsed.typeCode = sampleType.code;
		content.sampleUsed.categoryCode = sampleType.category.code;
		
		
		content.properties = new HashMap<String, PropertyValue>();
		content.properties.put("tag",new PropertySingleValue( results.get(posNext).contents.get(0).properties.get("tag").value  ));
		content.properties.put("percentPerLane",new PropertySingleValue( results.get(posNext).contents.get(0).properties.get("percentPerLane").value ));
		
		results.get(posCurrent).contents.add(content); 
		
		return results;
	}
	
	

	
	
	
	public void updateLimsProjects(List<Project> projects, ContextValidation contextError) throws DAOException {
		contextError.addKeyToRootKeyName("updateImportDate");
		
		String sql = "UPDATE t_project SET nglimport_date = ? WHERE name = ?";
		
		List<Object[]> parameters = new ArrayList<Object[]>();
		for (Project project : projects) {
	        parameters.add(new Object[] {new Date(), project.code}); 
		}
		
		this.jdbcTemplate.batchUpdate(sql, parameters);  
		
		contextError.removeKeyFromRootKeyName("updateImportDate");
	}
	
	
	public void updateLimsSamples(List<Sample> samples, ContextValidation contextError) throws DAOException {
		contextError.addKeyToRootKeyName("updateImportDate");
		
		String sql = "UPDATE t_sample SET nglimport_date = ? WHERE stock_barcode = ?";
		
		List<Object[]> parameters = new ArrayList<Object[]>();
		for (Sample sample : samples) {
	        parameters.add(new Object[] {new Date(), sample.code}); 
		}
		
		this.jdbcTemplate.batchUpdate(sql, parameters);  
		
		contextError.removeKeyFromRootKeyName("updateImportDate");
	}
	
	public void updateLimsContainers(List<Container> containers, ContextValidation contextError) throws DAOException {
		contextError.addKeyToRootKeyName("updateImportDate");
		
		String sql = "UPDATE t_lane SET nglimport_date = ? WHERE id = ?";
		
		List<Object[]> parameters = new ArrayList<Object[]>();
		for (Container container : containers) {
	        parameters.add(new Object[] {new Date(), container.properties.get("limsCode").value}); 
		}
		
		this.jdbcTemplate.batchUpdate(sql, parameters);  
		
		contextError.removeKeyFromRootKeyName("updateImportDate");
	}
	
		

}
