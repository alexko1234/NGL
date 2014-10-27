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
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.instance.BioinformaticParameters;
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

import controllers.CommonController;

import play.Logger;
import validation.ContextValidation;

/**
 * @author dnoisett
 * Import data from CNG's LIMS to NGL 
 */
@Repository
public class LimsCNGDAO {

	private JdbcTemplate jdbcTemplate;

	private static final String CONTAINER_STATE_CODE="A";
	protected static final String PROJECT_TYPE_CODE_DEFAULT = "default-project";
	protected static final String PROJECT_STATE_CODE_DEFAULT = "IP";
	protected static final String IMPORT_CATEGORY_CODE="sample-import";
	protected static final String SAMPLE_TYPE_CODE_DEFAULT = "default-sample-cng";
	protected static final String SAMPLE_USED_TYPE_CODE = "default-sample-cng";	
	protected static final String IMPORT_TYPE_CODE_DEFAULT = "default-import";
	
	
	@Autowired
	@Qualifier("lims")
	private void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}
	
	
	/**
	 * Common mapping for Project
	 * @param rs
	 * @param rowNum
	 * @param ctxErr
	 * @return
	 * @throws SQLException
	 */
	private Project commonProjectMapRow(ResultSet rs, int rowNum, ContextValidation ctxErr) throws SQLException { 
		Project project = new Project();
		project.code = rs.getString("code");
		Logger.debug("project.code=" + project.code);
		project.name = rs.getString("name").trim();
		
		project.typeCode=PROJECT_TYPE_CODE_DEFAULT;
		
		ProjectType projectType=null;
		try {
			projectType = ProjectType.find.findByCode(project.typeCode);
		} catch (DAOException e) {
			Logger.error("",e);
			return null;
		}
		if( projectType==null ){
			ctxErr.addErrors("code", "error.codeNotExist", project.typeCode, project.code);
			return null;
		}
		
		project.categoryCode=projectType.category.code;
		
		project.state = new State(); 
		project.state.code=PROJECT_STATE_CODE_DEFAULT;
		project.state.user = InstanceHelpers.getUser();
		project.state.date = new Date();
		
		project.traceInformation = new TraceInformation(); 
		project.traceInformation.setTraceInformation(InstanceHelpers.getUser());
	
		// just one comment for one project
		if (rs.getString("comments") != null ) {
			project.comments = new ArrayList<Comment>(); 
			InstanceHelpers.addComment(rs.getString("comments"), project.comments);
		}
		
		//specific to CNG
		project.bioinformaticParameters = new BioinformaticParameters();
		project.bioinformaticParameters.biologicalAnalysis = Boolean.TRUE; 
		
		return project;
	}

	
	/**
	 * To get new projects
	 * @param contextError
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Project> findProjectToCreate(final ContextValidation contextError) throws SQLException, DAOException {		
		List<Project> results = this.jdbcTemplate.query("select * from v_project_tongl", new Object[]{}, 
			new RowMapper<Project>() {
				public Project mapRow(ResultSet rs, int rowNum) throws SQLException {								
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					Project p =  commonProjectMapRow(rs0, rowNum0, ctxErr); 
					return p;
				}	
		});
		return results;
	}
	

	/**
	 * To get projects that have been updated in Soxela
	 * @param contextError
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Project> findProjectToModify(final ContextValidation contextError) throws SQLException, DAOException {	
		List<Project> results = this.jdbcTemplate.query("select * from v_project_updated_tongl", new Object[]{}, 
			new RowMapper<Project>() {
				public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					Project p =  commonProjectMapRow(rs0, rowNum0, ctxErr); 
					return p;
				}	
		});
		return results;
	}
	
	/*************************************************************************************************************************************************/
	
	/**
	 * Common mapping for Sample
	 * @param rs
	 * @param rowNum
	 * @param contextError
	 * @return
	 * @throws SQLException
	 */
	private Sample commonSampleMapRow(ResultSet rs, int rowNum, ContextValidation ctxErr) throws SQLException {
			
			Sample sample = new Sample();
			
			sample.traceInformation = new TraceInformation();
			sample.traceInformation.setTraceInformation(InstanceHelpers.getUser());

			sample.code=rs.getString("code");
			Logger.debug("Sample code :"+sample.code);
			
			String sampleTypeCode=SAMPLE_TYPE_CODE_DEFAULT;
			
			SampleType sampleType=null;
			try {
				sampleType = SampleType.find.findByCode(sampleTypeCode);
			} catch (DAOException e) {
				Logger.error("",e);
				return null;
			}
			if ( sampleType==null ) {
				ctxErr.addErrors("code", "error.codeNotExist", sampleTypeCode, sample.code);
				return null;
			}
			
			sample.typeCode=sampleType.code;
			sample.categoryCode=sampleType.category.code;
			sample.name=rs.getString("name");
			sample.referenceCollab= rs.getString("ref_collab");
			sample.taxonCode=rs.getString("taxon_code");

			sample.importTypeCode=IMPORT_TYPE_CODE_DEFAULT;
		
			sample.projectCodes=new ArrayList<String>();
			if (rs.getString("project") != null) {
				sample.projectCodes.add(rs.getString("project"));
			}
			else {
				sample.projectCodes.add(" "); 
			}

			sample.comments=new ArrayList<Comment>();
			
			if (rs.getString("comments") != null) {
				sample.comments.add(new Comment(rs.getString("comments")));
			}
			else {
				sample.comments.add(new Comment(" ")); 
			}
					
			sample.properties=new HashMap<String, PropertyValue>();
			sample.properties.put("limsCode", new PropertySingleValue(rs.getInt("lims_code")));
	
			return sample;
	}

	
	/**
	 * To set projectCodes and comments to samples
	 * @param results
	 * @return
	 */
	public List<Sample> demultiplexSample(List<Sample> results) {
		//affect all the project codes to a same sample 
		/// required to have an ordered list (see ORDER BY clause in the sql of the view)
		int pos = 0;
		int x = 1;
		int listSize = results.size(); 
		while (pos < listSize-1) {
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
	 * To get samples updated in the CNG's LIMS (Solexa)
	 * @param contextError
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Sample> findSampleToModify(final ContextValidation contextError) throws SQLException, DAOException {
		return findSampleToModify(contextError, null);
	}
	
	
	/**
	 * To get a particular sample updated in the CNG's LIMS (Solexa)
	 * @param contextError
	 * @param sampleCode
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Sample> findSampleToModify(final ContextValidation contextError, String sampleCode) throws SQLException, DAOException {		
		List<Sample> results = null;
		
		if (sampleCode != null) { 
			results = this.jdbcTemplate.query("select * from v_sample_updated_tongl where code=? order by code, project, comments", new Object[]{sampleCode}
			,new RowMapper<Sample>() {
				public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					Sample s=  commonSampleMapRow(rs0, rowNum0, ctxErr); 
					return s;
				}
			});
		}
		else { // mass loading
			Logger.debug("call v_sample_updated_tongl");
			results = this.jdbcTemplate.query("select * from v_sample_updated_tongl order by code, project, comments",new Object[]{}
			,new RowMapper<Sample>() {
				public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					Sample s=  commonSampleMapRow(rs0, rowNum0, ctxErr); 
					return s;
				}
			});			
		}		
		return demultiplexSample(results);	
	}
	
	

	
	/**
	 * To get new samples
	 * @param contextError
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Sample> findSampleToCreate(final ContextValidation contextError) throws SQLException, DAOException {
		return findSampleToCreate(contextError, null);
	}
	
	/**
	 * To get a new particular sample
	 * @param contextError
	 * @param sampleCode
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Sample> findSampleToCreate(final ContextValidation contextError, String sampleCode) throws SQLException, DAOException {		
		List<Sample> results = null;
		
		if (sampleCode != null) { 
			results = this.jdbcTemplate.query("select * from v_sample_tongl where code=? order by code, project, comments", new Object[]{sampleCode}
			,new RowMapper<Sample>() {
				public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					Sample s=  commonSampleMapRow(rs0, rowNum0, ctxErr); 
					return s;
				}
			});
		}
		else { // mass loading
			Logger.debug("call v_sample_tongl");
			results = this.jdbcTemplate.query("select * from v_sample_tongl order by code, project, comments",new Object[]{}
			,new RowMapper<Sample>() {
				public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					Sample s=  commonSampleMapRow(rs0, rowNum0, ctxErr); 
					return s;
				}
			});			
		}		
		return demultiplexSample(results);	
	}
	
	/****************************************************************************************************************************************/
	
	/**
	 * Common mapping for container
	 * @param rs
	 * @param rowNum
	 * @param ctxErr
	 * @return
	 * @throws SQLException
	 */
	private Container commonContainerMapRow(ResultSet rs, int rowNum, ContextValidation ctxErr, String containerCategoryCode) throws SQLException {
		Container container = new Container();
		
		container.traceInformation = new TraceInformation();
		container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
		
		container.code=rs.getString("code");
		Logger.debug("Container code :"+container.code);
		
		container.categoryCode=containerCategoryCode; //lane or tube
		
		if (rs.getString("comment") != null) {
			container.comments=new ArrayList<Comment>();	
			//just one comment for one lane (container)
			container.comments.add(new Comment(rs.getString("comment")));
		}
		
		container.state = new State(); 
		container.state.code=CONTAINER_STATE_CODE; 
		container.state.user = InstanceHelpers.getUser();
		container.state.date = new Date(); 
		
		container.valuation = new Valuation(); 
		container.valuation.valid= TBoolean.UNSET;
		
		// define container support attributes
		try {
			container.support=ContainerSupportHelper.getContainerSupport(containerCategoryCode, rs.getInt("nb_container"),rs.getString("code_support"),"1",rs.getString("column")); 
		}
		catch(DAOException e) {
			Logger.error("Can't get container support !"); 
		}
		
		container.properties= new HashMap<String, PropertyValue>();
		container.properties.put("limsCode",new PropertySingleValue(rs.getInt("lims_code")));
		
		if (rs.getString("project")!=null) {
			container.projectCodes=new ArrayList<String>();
			container.projectCodes.add(rs.getString("project"));
		}
		
		if (rs.getString("code_sample")!=null) {
			Content content=new Content();
			content.sampleCode=rs.getString("code_sample");
			
			String sampleTypeCode = SAMPLE_USED_TYPE_CODE;  //TODO : to manage with Julie !
			SampleType sampleType=null;
			try {
				sampleType = SampleType.find.findByCode(sampleTypeCode);
			} catch (DAOException e) {
				Logger.error("",e);
				return null;
			}
			if( sampleType==null ){
				ctxErr.addErrors("code", "error.codeNotExist", sampleTypeCode, content.sampleCode);
				return null;
			}		
			
			//TODO : manage fromExperimentTypeCodes for import lib_b* & lane/flowcell --> mapping Julie
			
			content.sampleTypeCode = sampleType.code;
			content.sampleCategoryCode = sampleType.category.code;
			
			content.properties = new HashMap<String, PropertyValue>();
			
			if(rs.getString("tag")!=null) { 
				content.properties.put("tag", new PropertySingleValue(rs.getString("tag")));
				content.properties.put("tagCategory", new PropertySingleValue(rs.getString("tagcategory")));
			}
			else {
				content.properties.put("tag",new PropertySingleValue("-1")); // specific value for making comparison, suppress it at the end of the function...
				content.properties.put("tagCategory",new PropertySingleValue("-1"));
			}						

			if(rs.getString("exp_short_name")!=null) {
				content.properties.put("libProcessTypeCode", new PropertySingleValue(rs.getString("exp_short_name")));
			}
			else {
				content.properties.put("libProcessTypeCode", new PropertySingleValue("-1"));
			}
			container.contents.add(content);			
			
			container.sampleCodes=new ArrayList<String>();
			container.sampleCodes.add(rs.getString("code_sample"));
		}

		container.fromPurifingCode = null;				

		return container;
	}
	
	
	/**
	 * 
	 * @param results
	 * @return
	 * @throws DAOException
	 */
	public static List<Container> demultiplexContainer(List<Container> results) throws DAOException {
		//affect all the project codes /samples /tags to the same container (for having unique codes of containers) 
		/// required to have an ordered list (see ORDER BY clause in the sql of the view)
		int pos = 0;
		int x = 1;
		int listSize = results.size();
		Boolean findContent;
		
		while (pos < listSize-1) {
			
			while ( (pos < listSize-1) && (results.get(pos).code.equals(results.get(pos+x).code)) ) {
				
				// difference between two consecutive projectCodes
				if (! results.get(pos).projectCodes.get(0).equals(results.get(pos+x).projectCodes.get(0))) {
					if (! results.get(pos).projectCodes.contains(results.get(pos+x).projectCodes.get(0))) {
						
						results.get(pos).projectCodes.add( results.get(pos+x).projectCodes.get(0) ); 
					}
				}
				// difference between two consecutive sampleCodes
				if (! results.get(pos).sampleCodes.get(0).equals(results.get(pos+x).sampleCodes.get(0))) {
					if (! results.get(pos).sampleCodes.contains(results.get(pos+x).sampleCodes.get(0))) {
							
						results.get(pos).sampleCodes.add( results.get(pos+x).sampleCodes.get(0) );
					}
				}
								
				findContent = false;
				//just to be sure that we don't create content in double
				for (Content content : results.get(pos).contents) {
					if ( (content.sampleCode.equals(results.get(pos+x).contents.get(0).sampleCode))  
								&& (content.properties.get("tag").value.equals(results.get(pos+x).contents.get(0).properties.get("tag").value)) 
								&& (content.properties.get("libProcessTypeCode").value.equals(results.get(pos+x).contents.get(0).properties.get("libProcessTypeCode").value))  ) {
						findContent = true;
						//Logger.debug("content already created !");
						break;
					}
				}				
				
				if (!findContent) createContent(results, pos, pos+x);
								
				// all the difference have been reported on the first sample found (at the position pos)
				// so we can delete the sample at the position (posNext)
				results.remove(pos+x);
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
				if (r.contents.get(i).properties.get("libProcessTypeCode").value.equals("-1")) {
					r.contents.get(i).properties.remove("libProcessTypeCode");
				}
			}
		}		
		return results;
	}
	
	
	/**
	 * Create a content and attach it to the contents of a container  
	 * @param results
	 * @param posCurrent
	 * @param posNext
	 * @return
	 * @throws DAOException
	 */
	public static List<Container>  createContent(List<Container> results, int posCurrent, int posNext) throws DAOException{
		Content content=new Content();
		content.sampleCode= results.get(posNext).sampleCodes.get(0);
		
		SampleType sampleType=null;
		sampleType = SampleType.find.findByCode(SAMPLE_USED_TYPE_CODE);	
		content.sampleTypeCode = sampleType.code;
		content.sampleCategoryCode = sampleType.category.code;
		
		content.properties = new HashMap<String, PropertyValue>();
		content.properties.put("tag", new PropertySingleValue(results.get(posNext).contents.get(0).properties.get("tag").value));
		content.properties.put("tagCategory", new PropertySingleValue(results.get(posNext).contents.get(0).properties.get("tagCategory").value));
		content.properties.put("libProcessTypeCode", new PropertySingleValue(results.get(posNext).contents.get(0).properties.get("libProcessTypeCode").value));
		
		results.get(posCurrent).contents.add(content); 
		
		return results;
	}
	
	/**
	 * common mapping for containerSupport
	 * @param rs
	 * @param rowNum
	 * @param ctxErr
	 * @return
	 * @throws SQLException
	 */
	private ContainerSupport commonContainerSupportMapRow(ResultSet rs, int rowNum, ContextValidation ctxErr) throws SQLException {
		ContainerSupport containerSupport = new ContainerSupport();		
		containerSupport.code = rs.getString("code_support");
		
		if (rs.getString("seq_program_type").equals("PE") || rs.getString("seq_program_type").equals("SR")) {
			containerSupport.properties= new HashMap<String, PropertyValue>();
			containerSupport.properties.put("sequencingProgramType", new PropertySingleValue(rs.getString("seq_program_type")));
		}
		else {
			Logger.error("Wrong value of seq_program_type : " + rs.getString("seq_program_type") + "! (expected SE ou PR) for code : " + rs.getString("code_support")); 
		}
		return containerSupport;
	}	

	
	
	/**
	 * To get new containers
	 * @param contextError
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Container> findContainerToCreate(final ContextValidation contextError, String containerCategoryCode) throws SQLException, DAOException {
		return findContainerToCreate(contextError, null, containerCategoryCode);
	}
	
	/**
	 * To get a new container
	 * method for mass loading
	 * @param contextError
	 * @return
	 * @throws DAOException 
	 */
	public List<Container> findContainerToCreate(final ContextValidation contextError, String containerCode, String containerCategoryCode) throws DAOException {
		final String _containerCategoryCode = containerCategoryCode;
		String sqlView;
		
		if (containerCategoryCode.equals("lane")) {
			sqlView = "v_flowcell_tongl";
		}
		else {
			sqlView = "v_tube_tongl";
		}
		
		List<Container> results = null;
		if (containerCode != null) {
			results = this.jdbcTemplate.query("select * from " + sqlView + " where code = ? and isavailable = true order by code, project, code_sample, tag", new Object[]{containerCode} 
			,new RowMapper<Container>() {
				public Container mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					Container c=  commonContainerMapRow(rs0, rowNum0, ctxErr, _containerCategoryCode); 
					return c;
				}
			});
		}
		else {
			Logger.debug("call v_flowcell_tongl ");
			results = this.jdbcTemplate.query("select * from " + sqlView + " where isavailable = true order by code, project, code_sample, tag", new Object[]{} 
			,new RowMapper<Container>() {
				public Container mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					Container c=  commonContainerMapRow(rs0, rowNum0, ctxErr, _containerCategoryCode); 
					return c;
				}
			});
		}
		return demultiplexContainer(results);			
	}
	
	
	
	//reprise
	public List<Container> findContainerToCreateForFirstTime(final ContextValidation contextError, String containerCode, String containerCategoryCode) throws DAOException {
		final String _containerCategoryCode = containerCategoryCode;
		String sqlView = "v_tube_tongl_reprise";
		
		Logger.debug("call v_tube_tongl_reprise !!!!!!!!!!!!!");
		List<Container> results = this.jdbcTemplate.query("select * from " + sqlView + " where isavailable = true order by code, project, code_sample, tag", new Object[]{} 
		,new RowMapper<Container>() {
			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultSet rs0 = rs;
				int rowNum0 = rowNum;
				ContextValidation ctxErr = contextError; 
				Container c=  commonContainerMapRow(rs0, rowNum0, ctxErr, _containerCategoryCode); 
				return c;
			}
		});
		
		return demultiplexContainer(results);			
	}
	
	

	/**
	 * To get containers updated
	 * @param contextError
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Container> findContainerToModify(final ContextValidation contextError, String containerCategoryCode) throws SQLException, DAOException {
		return findContainerToModify(contextError, null, containerCategoryCode);
	}

	
	/**
	 * To get a particular container updated (with its code)
	 * method for mass loading
	 * @param contextError
	 * @return
	 * @throws DAOException 
	 */
	public List<Container> findContainerToModify(final ContextValidation contextError, String containerCode, String containerCategoryCode) throws DAOException {		
		final String _containerCategoryCode = containerCategoryCode;
		String sqlView;
		
		if (containerCategoryCode.equals("lane")) {
			sqlView = "v_flowcell_updated_tongl";
		}
		else {
			sqlView = "v_tube_updated_tongl";
		}
		
		List<Container> results = null;		
		if (containerCode != null) {
			results = this.jdbcTemplate.query("select * from " + sqlView + " where code = ? and isavailable = true order by code, project, code_sample, tag", new Object[]{containerCode} 
			,new RowMapper<Container>() {
				public Container mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError;
					Container c=  commonContainerMapRow(rs0, rowNum0, ctxErr, _containerCategoryCode); 
					return c;
				}
			});
		}
		else {
			Logger.debug("call v_flowcell_updated_tongl ");
			results = this.jdbcTemplate.query("select * from " + sqlView + " where isavailable = true order by code, project, code_sample, tag", new Object[]{} 
			,new RowMapper<Container>() {
				public Container mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					Container c=  commonContainerMapRow(rs0, rowNum0, ctxErr, _containerCategoryCode); 
					return c;
				}
			});
		}
		return demultiplexContainer(results);			
	}
	
	/**
	 * Set sequencingProgramType
	 * @param contextError
	 * @param mode
	 * @return
	 * @throws DAOException
	 */
	public HashMap<String, PropertyValue<String>>  setSequencingProgramTypeToContainerSupport(final ContextValidation contextError, String mode)  throws DAOException {
		String sqlView;
		
		if (mode.equals("creation")) {
			sqlView = "v_flowcell_tongl"; 
		}
		else {
			sqlView = "v_flowcell_updated_tongl";
		}
		
		List<ContainerSupport> results = null;
		results = this.jdbcTemplate.query("select code_support, seq_program_type from " + sqlView + " where isavailable = true order by code, project, code_sample, tag", new Object[]{} 
		,new RowMapper<ContainerSupport>() {
			public ContainerSupport mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultSet rs0 = rs;
				int rowNum0 = rowNum;
				ContextValidation ctxErr = contextError; 
				ContainerSupport c=  commonContainerSupportMapRow(rs0, rowNum0, ctxErr); 
				return c;
			}
		});
		//map data
		HashMap<String,PropertyValue<String>> mapCodeSupportSequencing = new HashMap<String,PropertyValue<String>>();
		for (ContainerSupport result : results) {
			if (!mapCodeSupportSequencing.containsKey(result.code)) {
				mapCodeSupportSequencing.put(result.code, result.properties.get("sequencingProgramType"));
			}
		}	
		return mapCodeSupportSequencing;
	}

	
	/*************************************************************************************************************************************/
	
	/**
	 * UPDATE projects import/update dates
	 * @param projects
	 * @param contextError
	 * @throws DAOException
	 */
	public void updateLimsProjects(List<Project> projects, ContextValidation contextError, String mode) throws DAOException {
		
		String key, column;
		if (mode.equals("creation")) {
			key = "update_ImportDate";
			column = "nglimport_date";
		}
		else {
			key = "update_UpdateDate";
			column = "ngl_update_date";			
		}
		contextError.addKeyToRootKeyName(key);
		
		String sql = "UPDATE t_project SET " + column + " = ? WHERE name = ?";
		List<Object[]> parameters = new ArrayList<Object[]>();
		for (Project project : projects) {
	        parameters.add(new Object[] {new Date(), project.code}); 
		}
		this.jdbcTemplate.batchUpdate(sql, parameters);  
		
		contextError.removeKeyFromRootKeyName(key);
	}

	
	/**
	 * UPDATE samples import/update dates 
	 * @param samples
	 * @param contextError
	 * @throws DAOException
	 */
	public void updateLimsSamples(List<Sample> samples, ContextValidation contextError, String mode) throws DAOException {
		
		String key, column;
		if (mode.equals("creation")) {
			key = "update_ImportDate";
			column = "nglimport_date";
		}
		else {
			key = "update_UpdateDate";
			column = "ngl_update_date";			
		}
		
		contextError.addKeyToRootKeyName(key);
		
		String sql = "UPDATE t_sample SET " + column + " = ? WHERE stock_barcode = ?";
		List<Object[]> parameters = new ArrayList<Object[]>();
		for (Sample sample : samples) {
	        parameters.add(new Object[] {new Date(), sample.code}); 
		}
		this.jdbcTemplate.batchUpdate(sql, parameters);  
		
		//new
		sql = "UPDATE t_individual SET " + column + " = ? WHERE id in (select individual_id from t_sample where stock_barcode = ?)";
		parameters = new ArrayList<Object[]>();
		for (Sample sample : samples) {
	        parameters.add(new Object[] {new Date(), sample.code}); 
		}
		this.jdbcTemplate.batchUpdate(sql, parameters);  
		
		contextError.removeKeyFromRootKeyName(key);
	}

	
	/**
	 * UPDATE lane containers import/update dates 
	 * @param containers
	 * @param contextError
	 * @throws DAOException
	 */
	public void updateLimsLanes(List<Container> containers, ContextValidation contextError, String mode) throws DAOException {

		String key, column;
		if (mode.equals("creation")) {
			key = "update_ImportDate";
			column = "nglimport_date";
		}
		else {
			key = "update_UpdateDate";
			column = "ngl_update_date";			
		}
		
		contextError.addKeyToRootKeyName(key);
		
		String sql = "UPDATE t_lane SET " + column + " = ? WHERE id = ?";
		List<Object[]> parameters = new ArrayList<Object[]>();
		for (Container container : containers) {
	        parameters.add(new Object[] {new Date(), container.properties.get("limsCode").value}); 
		}
		this.jdbcTemplate.batchUpdate(sql, parameters);  
		
		sql = "UPDATE t_sample_lane SET " + column + " = ? WHERE lane_id = ?";
		parameters = new ArrayList<Object[]>();
		for (Container container : containers) {
	        parameters.add(new Object[] {new Date(), container.properties.get("limsCode").value}); 
		}
		this.jdbcTemplate.batchUpdate(sql, parameters);   
				
		contextError.removeKeyFromRootKeyName(key);
	}
	
	/**
	 * UPDATE tube containers import/update dates 
	 * @param containers
	 * @param contextError
	 * @param mode
	 * @throws DAOException
	 */
	public void updateLimsTubes(List<Container> containers, ContextValidation contextError, String mode) throws DAOException {

		String key, column;
		if (mode.equals("creation")) {
			key = "update_ImportDate";
			column = "nglimport_date";
		}
		else {
			key = "update_UpdateDate";
			column = "ngl_update_date";			
		}
		
		contextError.addKeyToRootKeyName(key);
		
		String sql = "UPDATE t_tube SET " + column + " = ? WHERE id = ?";
		List<Object[]> parameters = new ArrayList<Object[]>();
		for (Container container : containers) {
	        parameters.add(new Object[] {new Date(), container.properties.get("limsCode").value}); 
		}
		this.jdbcTemplate.batchUpdate(sql, parameters);  		
		
		contextError.removeKeyFromRootKeyName(key);
	}
	

}
