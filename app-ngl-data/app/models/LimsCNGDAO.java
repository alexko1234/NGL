package models;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.parameter.Index;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.instance.BioinformaticParameters;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import models.utils.instance.ContainerSupportHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.typesafe.config.ConfigFactory;

import play.Logger;
import services.instance.experiment.ExperimentImport;
import validation.ContextValidation;

/**
 * @author dnoisett
 * Import data from CNG's LIMS to NGL 
 * Functions to get projects, samples,  containers ( lanes, tubes), indexes
 * Sub-functions to map data (between Solexa and NGL)
 * Sub-functions to update dates (in order to know what data has been imported)
 */
@Repository
public class LimsCNGDAO {

	private JdbcTemplate jdbcTemplate;

	// FDS 14/10/2015  il faut plusieurs state codes...
	private static final String CONTAINER_STATE_CODE_IW_P="IW-P";
	private static final String CONTAINER_STATE_CODE_IS="IS";
	
	protected static final String PROJECT_TYPE_CODE_DEFAULT = "default-project";
	protected static final String PROJECT_STATE_CODE_DEFAULT = "IP";
	
	protected static final String IMPORT_CATEGORY_CODE="sample-import";  // inutilisé...
	protected static final String IMPORT_TYPE_CODE_DEFAULT = "default-import";
	
	protected static final String SAMPLE_TYPE_CODE_DEFAULT = "default-sample-cng"; // inutilisé...
	protected static final String SAMPLE_USED_TYPE_CODE = "default-sample-cng";	
	
	@Autowired
	@Qualifier("lims")
	private void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}
	
	/*************************************************************************************************************************************************
	 * 1 - Common mapping for Project
	 * @param rs
	 * @param rowNum
	 * @param ctxErr
	 * @return
	 * @throws SQLException
	 */
	private Project commonProjectMapRow(ResultSet rs, int rowNum, ContextValidation ctxErr) throws SQLException { 
		Project project = new Project();
		
		project.code = rs.getString("code");
		project.name = rs.getString("name").trim();
		//Logger.debug("project code=" + project.code);		
		
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
			InstanceHelpers.addComment(rs.getString("comments"), project.comments, "ngl-data");
		}
		
		//specific to CNG
		project.bioinformaticParameters = new BioinformaticParameters();
		project.bioinformaticParameters.biologicalAnalysis = Boolean.TRUE; 
		
		return project;
	}
	
	/*************************************************************************************************************************************************
	 * 2 - Common mapping for Sample
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
			
			String sampleTypeCode=rs.getString("sample_type");
			//Logger.debug("Sample code :"+sample.code+ " Sample type code :"+sampleTypeCode);
			
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
		
			//FDS: plus necessaire car un sample n'appartient plus qu'a un seul projet ??
			sample.projectCodes=new HashSet<String>();
			if (rs.getString("project") != null) {
				sample.projectCodes.add(rs.getString("project"));
			}
			else {
				sample.projectCodes.add(" "); 
			}

			sample.comments=new ArrayList<Comment>();
			
			if (rs.getString("comments") != null) {
				sample.comments.add(new Comment(rs.getString("comments"), "ngl-data"));
			}
			
			sample.properties=new HashMap<String, PropertyValue>();
			sample.properties.put("limsCode", new PropertySingleValue(rs.getInt("lims_code")));
	
			/* FDS et GA 16/06/2015: le sampleType gDNA est commun CNG/CNG. les 3 propriétés  isAdapters, isFragmented, taxonSize 
			    sont obligatoires au CNS. Comme elles n'existent pas au CNG, il faut leur fournir une valeur par defaut pour que
			    la validation fonctionne
			*/
			if (sample.typeCode.equals("gDNA")) {	
				sample.properties.put("isAdapters", new PropertySingleValue(false)); 
				sample.properties.put("isFragmented", new PropertySingleValue(false)); 
				sample.properties.put("taxonSize", new PropertySingleValue(0)); 		   
			}			
			
			return sample;
	}

	/*************************************************************************************************************************************************
	 * 3 - Common mapping for container
	 * @param rs
	 * @param rowNum
	 * @param ctxErr
	 * @param experimentTypeCode 
	 * @param importState   22/10/2015 ajouté pour la reprise avec status differencié 
	 * @return
	 * @throws SQLException
	 */
	private Container commonContainerMapRow(ResultSet rs, int rowNum, ContextValidation ctxErr, String containerCategoryCode, String experimentTypeCode, String importState) throws SQLException {
		Container container = new Container();
		
		container.traceInformation = new TraceInformation();
		container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
		container.code = rs.getString("container_code");
		//Logger.debug("[commonContainerMapRow] Container code :"+container.code);
		
		container.categoryCode = containerCategoryCode; //lane or tube or plate-well
		
		if (rs.getString("comment") != null) {
			container.comments = new ArrayList<Comment>();	
			container.comments.add(new Comment(rs.getString("comment"), "ngl-data"));
		}
		
		container.fromExperimentTypeCodes=new HashSet<String>();
		container.fromExperimentTypeCodes.add(experimentTypeCode);
		
		container.state = new State(); 
		
		if ( (importState == null)  || (importState.equals("is")) ) {
			container.state.code = CONTAINER_STATE_CODE_IS; 
		}
		else if (importState.equals("iw-p")) {
			container.state.code = CONTAINER_STATE_CODE_IW_P;
		}
		
		container.state.user = InstanceHelpers.getUser();
		container.state.date = new Date(); 
		
		container.valuation = new Valuation(); 
		container.valuation.valid = TBoolean.UNSET;
		
		// define container support attributes
		try {
			// 23/09/2015 bug!!! inversion entre x/y  x==> column; y==>line
			// 14/10/2015 FDS Ajout de storageCode; 28/10/2015 renommé storage_code
			container.support = ContainerSupportHelper.getContainerSupport(containerCategoryCode, 
					                                                       rs.getInt("nb_usable_container"),
					                                                       rs.getString("support_code"),
					                                                       rs.getString("column"),
					                                                       rs.getString("row"),
					                                                       rs.getString("storage_code"));  
		}
		catch(DAOException e) {
			Logger.error("[commonContainerMapRow] Can't get container support !"); 
		}
		
		container.properties = new HashMap<String, PropertyValue>();
		container.properties.put("limsCode",new PropertySingleValue(rs.getInt("lims_code")));
		
		//TODO idem  plate-well
		if (containerCategoryCode.equals("tube")) {
			//round concentration to 2 decimals using BigDecimal
			Double concentration = null;
			BigDecimal d = null;
			if ((Float) rs.getFloat("concentration") != null) {
				 d = new BigDecimal(rs.getFloat("concentration"));
				 BigDecimal d2 = d.setScale(2, BigDecimal.ROUND_HALF_UP); 
				 concentration = d2.doubleValue();
			}
			container.mesuredConcentration = new PropertySingleValue(concentration, "nM");
			

		}
		
		// List plus nécessaire, une library n'est plus attribuée qu'a un seul projet ??
		if (rs.getString("project")!=null) {
			container.projectCodes = new HashSet<String>();
			container.projectCodes.add(rs.getString("project"));
		}		
		
		// creation du content d'un container
		if (rs.getString("sample_code")!=null) {
			Content content=new Content();
			content.sampleCode = rs.getString("sample_code");
			content.projectCode = rs.getString("project");
						
			String sampleTypeCode=rs.getString("sample_type");
			//Logger.debug("[commonContainerMapRow] content.Sample type code :"+sampleTypeCode);
			
			SampleType sampleType=null;
			try {
				sampleType = SampleType.find.findByCode(sampleTypeCode);
			} catch (DAOException e) {
				Logger.error("",e);
				return null;
			}
			if ( sampleType==null ) {
				ctxErr.addErrors("sample code", "error.codeNotExist", sampleTypeCode, content.sampleCode);
				return null;
			}	
			
			content.sampleTypeCode = sampleType.code;
			content.sampleCategoryCode = sampleType.category.code;
			
			content.properties = new HashMap<String, PropertyValue>();
			
			if (rs.getString("tag")!=null) { 
				content.properties.put("tag", new PropertySingleValue(rs.getString("tag")));
				content.properties.put("tagCategory", new PropertySingleValue(rs.getString("tagcategory")));
			}
			else {
				content.properties.put("tag",new PropertySingleValue("-1")); // specific value for making comparison, suppress it at the end of the function...
				content.properties.put("tagCategory",new PropertySingleValue("-1"));
			}				

			if (rs.getString("exp_short_name")!=null) {
				content.properties.put("libProcessTypeCode", new PropertySingleValue(rs.getString("exp_short_name")));
			}
			else {
				content.properties.put("libProcessTypeCode", new PropertySingleValue("-1"));
			}
			
			// FDS 15/06/2015 JIRA NGL-673 Ajout du barcode de la librairie solexa initiale ( aliquot )=> nouvelle propriété de content 
			if (rs.getString("aliquote_code")!=null) { 
				//Logger.debug("[commonContainerMapRow] content aliquote code :"+ rs.getString("aliquote_code"));
				content.properties.put("sampleAliquoteCode", new PropertySingleValue(rs.getString("aliquote_code")));
			}
			else {
				Logger.warn("[commonContainerMapRow] content aliquot code : null !!!!!!");
				content.properties.put("sampleAliquoteCode", new PropertySingleValue("-1"));
			}
			
			container.contents.add(content);			
			
			container.sampleCodes=new HashSet<String>();
			container.sampleCodes.add(rs.getString("sample_code"));
				
		}

		container.fromPurifingCode = null;				

		return container;
	}
	
	/*************************************************************************************************************************************************
	 * 4- common mapping for containerSupport
	 * @param rs
	 * @param rowNum
	 * @param ctxErr
	 * @return
	 * @throws SQLException
	 *  FDS uniquement  appellé dans setSequencingProgramTypeToContainerSupport ?????????????????
	 */
	private ContainerSupport commonContainerSupportMapRow(ResultSet rs, int rowNum, ContextValidation ctxErr) throws SQLException {
		ContainerSupport containerSupport = new ContainerSupport();	
		containerSupport.code = rs.getString("support_code");
		
		if (rs.getString("seq_program_type").equals("PE") || rs.getString("seq_program_type").equals("SR")) {
			containerSupport.properties= new HashMap<String, PropertyValue>();
			containerSupport.properties.put("sequencingProgramType", new PropertySingleValue(rs.getString("seq_program_type")));
		}
		else {
			Logger.error("Wrong value of seq_program_type : " + rs.getString("seq_program_type") + "! (expected SE ou PR) for code : " + rs.getString("support_code")); 
		}
		return containerSupport;
	}	
	
	
	/** FDS: no IndexMapRow
	 *  mapping is done in findIndexToCreate
	 */
	
	/*************************************************************************************************************************************************
	 ** FDS 29/04/2015 remise dans l'etat initial.. garder findProjectToCreate et findProjectToModify....
	 **
	 * 1a - To get new projects
	 * @param contextError
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Project> findProjectToCreate(final ContextValidation contextError) throws SQLException, DAOException {		
		List<Project> results = this.jdbcTemplate.query("select code, name, comments from v_project_tongl", new Object[]{},  
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
	

	/*************************************************************************************************************************************************
	 * 1b - To get projects that have been updated in Soxela
	 * @param contextError
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Project> findProjectToModify(final ContextValidation contextError) throws SQLException, DAOException {	
		List<Project> results = this.jdbcTemplate.query("select  code, name, comments from v_project_updated_tongl", new Object[]{}, 
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
	
	/************************************************************************************************************************************************* 
	 * UPDATE Solexa t_project import/update dates
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
	
	/*************************************************************************************************************************************************
	 ** FDS 17/06/2015 inutile puisqu'il n'y a plus qu'un seul projet pour un sample ???
	 * 2a -To set projectCodes to samples
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
			 // meme recodage a faire que pour les containers...TODO
             while ( (pos < listSize-1) && (results.get(pos).code.equals( results.get(pos+x).code ))   ) {
                     // difference between the two project codes
                     if (! results.get(pos).projectCodes.toArray(new String[0])[0].equals(results.get(pos+x).projectCodes.toArray(new String[0])[0])) {
                             if (! results.get(pos).projectCodes.contains(results.get(pos+x).projectCodes.toArray(new String[0])[0])) {
                                     results.get(pos).projectCodes.add( results.get(pos+x).projectCodes.toArray(new String[0])[0] );
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
             for (String projectCode :s.projectCodes) {
                     if (projectCode.equals(" ")) {
                             s.projectCodes.remove(projectCode);
                     }
             }
     }
	
		return results;
	}

	/************************************************************************************************************************************************* 
	 * 2b - To get all the samples (first loading, migration) 
	 * @param contextError
	 * @return
	 * @throws DAOException
	 */
	public List<Sample> findAllSample(final ContextValidation contextError) throws DAOException {
		
		List<Sample> results = this.jdbcTemplate.query("select * from v_sample_tongl_reprise order by code, project desc, comments", new Object[]{} 
		,new RowMapper<Sample>() {
			public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultSet rs0 = rs;
				int rowNum0 = rowNum;
				ContextValidation ctxErr = contextError; 
				Sample s=  commonSampleMapRow(rs0, rowNum0, ctxErr); 
				return s;
			}
		});
		
		//demultiplexSample toujours necessaire car le code est le SOLEXA stock_barcode=> plusieurs samples peuvent avoir le meme code
		return demultiplexSample(results);			
	}
	
	/*************************************************************************************************************************************************
	 * 2c - To get samples updated in the CNG's LIMS (Solexa database)
	 * @param contextError
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Sample> findSampleToModify(final ContextValidation contextError) throws SQLException, DAOException {
		return findSampleToModify(contextError, null);
	}
	
	
	/*************************************************************************************************************************************************
	 * 2d To get a particular sample updated in the CNG's LIMS (Solexa database)
	 * @param contextError
	 * @param sampleCode
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public List<Sample> findSampleToModify(final ContextValidation contextError, String sampleCode) throws SQLException, DAOException {		
		List<Sample> results = null;
		
		if (sampleCode != null) { 
			results = this.jdbcTemplate.query("select * from v_sample_updated_tongl where code=? order by code, project desc, comments", new Object[]{sampleCode}
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
			results = this.jdbcTemplate.query("select * from v_sample_updated_tongl order by code, project desc, comments",new Object[]{}
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
		
		//demultiplexSample toujours necessaire car le code est le SOLEXA stock_barcode=> plusieurs samples peuvent avoir le meme code
		return demultiplexSample(results);	
	}
	
	
	/*************************************************************************************************************************************************
	 * 2e To get new samples
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
			results = this.jdbcTemplate.query("select * from v_sample_tongl where code=? order by code, project desc, comments", new Object[]{sampleCode}
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
			results = this.jdbcTemplate.query("select * from v_sample_tongl order by code, project desc, comments",new Object[]{}
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
		
		//demultiplexSample toujours necessaire car le code est le SOLEXA stock_barcode=> plusieurs samples peuvent avoir le meme code
		return demultiplexSample(results);	
	}
	
	/*************************************************************************************************************************************************
	 * To set projectCodes & sampleCodes
	 * @param results
	 * @return
	 * @throws DAOException
	 */
	public static List<Container> demultiplexContainer(List<Container> results) throws DAOException {
		//affect all the project codes /samples /tags to the same container (for having unique codes of containers) 
		/// required to have an ordered list (see ORDER BY clause in the sql of the view)
		Logger.debug("start demultiplexing containers");
		
		int pos = 0;
		int x = 1;
		int listSize = results.size();
		Boolean findContent;
		Content[] tmpArray = new Content[1];
		
		while (pos < listSize-1) {
			
			while ( (pos < listSize-1) && (results.get(pos).code.equals(results.get(pos+x).code)) ) {
				// 10-07-15 refactored by NW
				//Logger.debug("demultiplex "+ results.get(pos).code);
				assert results.get(pos+x).sampleCodes.size() <= 1;
				
				// difference between two consecutive sampleCodes
				java.util.Iterator<String> iter = results.get(pos+x).sampleCodes.iterator();
				if (iter.hasNext()) {
					String oneSampleCode = iter.next();
					if (! results.get(pos).sampleCodes.contains(oneSampleCode)) {
						results.get(pos).sampleCodes.add(oneSampleCode);
					}
				}

				findContent = false;
				//just to be sure that we don't create content in double
				// FDS 16/06/2015 get("sampleAliquoteCode") ajouté pour JIRA NGL-273
				for (Content content : results.get(pos).contents) {
					// Content nextContent = results.get(pos+x).contents.iterator().next();
					Content nextContent = results.get(pos+x).contents.toArray(tmpArray)[0];
					if ( (content.sampleCode.equals(nextContent.sampleCode))  
								&& (content.properties.get("tag").value.equals(nextContent.properties.get("tag").value)) 
								&& (content.properties.get("libProcessTypeCode").value.equals(nextContent.properties.get("libProcessTypeCode").value)) 
								&& (content.properties.get("sampleAliquoteCode").value.equals(nextContent.properties.get("sampleAliquoteCode").value)) ) {
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
		
		for (Container r : results) {
			// 10-07-15 refactoredF by NW
			
			//For now we have not the % of each content=> assume equimolarity !
			//Logger.debug("Nb contents in container=" + r.contents.size());
			//calcul identique pour les contents d'un meme container=>ne pas mettre dans le for!!
			Double equiPercent = ContainerHelper.getEquiPercentValue(r.contents.size());
			//Logger.debug("equiPercent="+equiPercent);
			
			for (Content content : r.contents) {		
				//remove bad properties;  FDS comments 04/05/2015 : valeurs -1 positionnées dans commonContainerMapRow 

				// FDS 17/06/2015 ajout sampleAliquoteCode pour JIRA NGL-673
				for (String propName : new String[]{"tag", "tagCategory", "libProcessTypeCode", "sampleAliquoteCode"}) {
					PropertyValue<?> propVal = content.properties.get(propName);
					if (propVal != null && (propVal.value == null || propVal.value.equals("-1"))) {
						content.properties.remove(propName);
					}
				}
				
				//set percentage
				content.percentage = equiPercent; 
			}
		}	
		
		//define container projects from projects contents
		defineContainerProjectCodes(results); 
		
		Logger.debug("end demultiplexing containers");
		return results;
	}
	
	/*************************************************************************************************************************************************
	 * @param results
	 * @return
	 * @throws DAOException
	 */
	public static List<Container> defineContainerProjectCodes(List<Container> results) throws DAOException {
		for (Container r : results) {
			Set<String> projectCodes = new HashSet<String>();
			for (Content c : r.contents) {
				projectCodes.add(c.projectCode);
			}
			r.projectCodes = projectCodes; 
		}
		return results;
	}
	
	
	/*************************************************************************************************************************************************
	 * Create a content and attach it to the contents of a container 
	 * FDS comment : only if the container is multiplexed... othrerwise the container Content no1 has been created in common ContainerMapRow
	 * @param results
	 * @param posCurrent
	 * @param posNext
	 * @return
	 * @throws DAOException
	 */
	public static List<Container>  createContent(List<Container> results, int posCurrent, int posNext) throws DAOException{
		Content content = new Content();
		
		//FDS refactor todo ???    toArray(new String[0])[0]; !!!!
		
		content.sampleCode = results.get(posNext).sampleCodes.toArray(new String[0])[0];
		content.projectCode = results.get(posNext).projectCodes.toArray(new String[0])[0];
		
		content.sampleTypeCode =results.get(posNext).contents.toArray(new Content[0])[0].sampleTypeCode;
		content.sampleCategoryCode =results.get(posNext).contents.toArray(new Content[0])[0].sampleCategoryCode;
		
		// peut ne pas y avoir d'index et pourtant pas de pb de null pointer exception ici ???
		content.properties = new HashMap<String, PropertyValue>();
		content.properties.put("tag", new PropertySingleValue(results.get(posNext).contents.toArray(new Content[0])[0].properties.get("tag").value));
		content.properties.put("tagCategory", new PropertySingleValue(results.get(posNext).contents.toArray(new Content[0])[0].properties.get("tagCategory").value));
	
		if (results.get(posNext).contents.toArray(new Content[0])[0].properties.get("libProcessTypeCode") == null) {	
			Logger.debug("[createContent] content.sampleCode =" + content.sampleCode + " pas de lib process type code (exp_type_code) !!!!!");
		}
		else {
			content.properties.put("libProcessTypeCode", new PropertySingleValue(results.get(posNext).contents.toArray(new Content[0])[0].properties.get("libProcessTypeCode").value));
			//Logger.debug("[createContent] content.sampleCode =" + content.sampleCode + "; content.libProcessTypeCode ="+ content.properties.get("libProcessTypeCode").value);
		}
		
		//FDS 16/06/2015 JIRA NGL-673: ajouter aliquote code (peut pas etre null)???
		if (results.get(posNext).contents.toArray(new Content[0])[0].properties.get("sampleAliquoteCode") == null) {
			Logger.debug("[createContent] content.sampleCode =" + content.sampleCode + " pas de aliquot code !!!!!");
		}
		else {
			content.properties.put("sampleAliquoteCode", new PropertySingleValue(results.get(posNext).contents.toArray(new Content[0])[0].properties.get("sampleAliquoteCode").value));
			//Logger.debug("[createContent] content.sampleCode =" + content.sampleCode + "; content.sampleAliquoteCode ="+ content.properties.get("sampleAliquoteCode").value);
		}
		
		results.get(posCurrent).contents.add(content); 
		
		return results;
	}
	

	/*************************************************************************************************************************************************
	 * To get new containers
	 * @param contextError
	 * @param containerCategoryCode
	 * @param experimentTypeCode
	 * @param importState 22/10/2015 parametre pour reprise avec creation a des states differents
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */

	public List<Container> findContainerToCreate(final ContextValidation contextError, String containerCategoryCode, String experimentTypeCode, String importState) throws SQLException, DAOException {
		return findContainerToCreate(contextError, null, containerCategoryCode, experimentTypeCode, importState);
	}
	
	/**
	 * To get a new container !! 2 categories: lane / tube
	 * method for mass loading
	 * @param containerCategoryCode
	 * @param experimentTypeCode
	 * @param importState 22/10/2015 parametre pour reprise avec creation a des states differents
	 * @param contextError
	 * @return
	 * @throws DAOException 
	 */

	public List<Container> findContainerToCreate(final ContextValidation contextError, String containerCode, String containerCategoryCode, String experimentTypeCode, String importState) throws DAOException {
		String sqlView=null;
		String sqlQuery=null;
		String sqlClause=null;
		//13/03/2015 le order by est TRES IMPORTANT: demultiplexContainer en depend !! 
		String sqlOrder=" order by container_code, project desc, sample_code, tag, exp_short_name";
		
		if (containerCategoryCode.equals("lane")) {
			sqlView = "v_flowcell_tongl";
			sqlClause="";
		}
		// 27/10/2015 separaration 'tube'/'plate' en vue de prochaine version
		//    les vues "tubes" actuelles importent les puits de plaque comme des tubes !!!
		else if (containerCategoryCode.equals("tube")) {
			if (experimentTypeCode.equals("lib-normalization")) {		
					sqlView = "v_libnorm_tube_new_tongl"; 
			}
			else if (experimentTypeCode.equals("denat-dil-lib")) {
					sqlView = "v_libdenatdil_tube_new_tongl";
			}
			else {
					//autres experimentTypeCode a venir ??
					sqlView = "TODO ??";
			}
			
			
			if (importState == null ) {
				sqlClause="";
			}
			else if (importState.equals("is")){
				sqlClause=" and ngl_status='done' ";
			}
			else if (importState.equals("iw-p")){
				sqlClause=" and ngl_status='ready' ";
			}
			else {
				sqlClause="NOT SUPPORTED";
			}
		}
		/* PAS ENCORE EN PROD
		else if (containerCategoryCode.equals("plate-well")) {
			if (experimentTypeCode.equals("lib-normalization")) {		
					sqlView = "v_libnorm_plate_new_tongl"; 
			}
			else if (experimentTypeCode.equals("denat-dil-lib")) {
					sqlView = "v_libdenatdil_plate_new_tongl";
			}
			else {
					//autres experimentTypeCode a venir ??
					sqlView = "TODO ??";
			}
			
			if (importState == null ) {
				sqlClause="";
			}
			else if (importState.equals("is")){
				sqlClause=" and ngl_status='done' ";
			}
			else if (importState.equals("iw-p")){
				sqlClause=" and ngl_status='ready' ";
			}
			else {
				sqlClause="NOT SUPPORTED";
			}
		}
		*/
		
		List<Container> results = null;
		if (containerCode != null) {
			Logger.debug("Import container " + containerCategoryCode +"("+ containerCode+ ") with SOLEXA sql: "+ sqlView + sqlClause);
			// 22/10/2015 utilisation sqlQuery, sqlClause, sqlOrder
			sqlQuery="select * from " + sqlView + " where container_code = ? " + sqlClause + sqlOrder;
		}
		else {
			Logger.debug("Import containers " + containerCategoryCode + " with SOLEXA sql: "+ sqlView + sqlClause);
			// 22/10/2015 utilisation sqlQuery, sqlClause, sqlOrder
			sqlQuery="select * from " + sqlView + " where 1=1 " + sqlClause + sqlOrder;
		}
		
		// fusion des 2 appels a jdbcTemplate.query en passant par sqlQuery !! A FAIRE AILLEURS AUSSI
		//results = this.jdbcTemplate.query("select * from " + sqlView + " order by container_code, project desc, sample_code, tag, exp_short_name", new Object[]{} 
		results = this.jdbcTemplate.query(sqlQuery, new Object[]{} ,new RowMapper<Container>() {
		public Container mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultSet rs0 = rs;
				int rowNum0 = rowNum;
				ContextValidation ctxErr = contextError; 
				Container c=  commonContainerMapRow(rs0, rowNum0, ctxErr, containerCategoryCode, experimentTypeCode, importState);
				return c;
			}
		});
		
		
		return demultiplexContainer(results);			
	}
	
	
	/*************************************************************************************************************************************************
	 * To get all containers (for mass loading the first time or for migration)
	 * @param contextError
	 * @param containerCategoryCode
	 * @param experimentTypeCode
	 * @param 
	 * @return
	 * @throws DAOException
	 *  DEPRECATED.. pas mise a jour ( voir findContainerToCreate...)
	 */
	public List<Container> findAllContainer(final ContextValidation contextError, String containerCategoryCode, String experimentTypeCode) throws DAOException {
		final String _containerCategoryCode = containerCategoryCode;
		String sqlView=null;
		
		if (containerCategoryCode.equals("lane")) {
			sqlView = "v_flowcell_tongl_reprise";
		}
		// 27/10/2015 separaration 'tube'/'plate-well' en vue de prochaine version
		//    les vues "tubes" actuelles importent les puits de plaque comme des tubes !!!
		else if (containerCategoryCode.equals("tube")) {
			if (experimentTypeCode.equals("lib-normalization")) {
				sqlView = "v_libnorm_tube_tongl_reprise";
			}
			else if (experimentTypeCode.equals("denat-dil-lib")) {
				sqlView = "v_libdenatdil_tube_tongl_reprise";
			}
			else {
				//autres experimentTypeCode a venir ??
				sqlView = "TODO??";
			}
		}
		/* PAS ENCORE EN PROD
		else if (containerCategoryCode.equals("plate-well")) {
			if (experimentTypeCode.equals("lib-normalization")) {
				sqlView = "v_libnorm_plate_tongl_reprise";
			}
			else if (experimentTypeCode.equals("denat-dil-lib")) {
				sqlView = "v_libdenatdil_plate_tongl_reprise";
			}
			else {
				//autres experimentTypeCode a venir ??
				sqlView = "TODO??";
			}
		}
		*/
		
		List<Container> results = this.jdbcTemplate.query("select * from " + sqlView + " order by container_code, project desc, sample_code, tag, exp_short_name", new Object[]{} 
		,new RowMapper<Container>() {
			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultSet rs0 = rs;
				int rowNum0 = rowNum;
				ContextValidation ctxErr = contextError; 
				Container c=  commonContainerMapRow(rs0, rowNum0, ctxErr, _containerCategoryCode,experimentTypeCode, null); 
				return c;
			}
		});
		
		return demultiplexContainer(results);			
	}
	

	/*************************************************************************************************************************************************
	 * To get containers updated in CNG database (Solexa database)
	 * @param contextError
	 * @param containerCategoryCode
	 * @param experimentTypeCode
	 * @return
	 * @    throws SQLException  NON dit Nicolas...car jdbcTemplate.query l'enrobe lui meme dans une DataAccessException
	 * @throws DAOException
	 */
	
	// enlever throws SQLException  
	public List<Container> findContainerToModify(final ContextValidation contextError, String containerCategoryCode, String experimentTypeCode) 
			throws DAOException {
		return findContainerToModify(contextError, null, containerCategoryCode,experimentTypeCode);
	}

	
	/**
	 * To get a particular container updated (with its code) !! 2 categories: lane / tube
	 * method for mass loading
	 * @param contextError
	 * @param containerCategoryCode
	 * @param experimentTypeCode
	 *  pas de importState ???
	 * @return
	 * @throws DAOException 
	 */
	public List<Container> findContainerToModify(final ContextValidation contextError, String containerCode, String containerCategoryCode, String experimentTypeCode) 
			throws DAOException {		
		String sqlView=null;
		
		if (containerCategoryCode.equals("lane")) {
			sqlView = "v_flowcell_updated_tongl";
		}
		// 27/10/2015 separaration 'tube'/'plate-well' en vue de prochaine version
		//    les vues "tubes" actuelles importent les puits de plaque comme des tubes !!!
		else if (containerCategoryCode.equals("tube")) {
			if (experimentTypeCode.equals("lib-normalization")) {
				sqlView = "v_libnorm_tube_updated_tongl";
			}
			else if (experimentTypeCode.equals("denat-dil-lib")) {
				sqlView = "v_libdenatdil_tube_updated_tongl";
			}
			else {
				//autres experimentTypeCode a venir ??
				sqlView = "TODO ??";
			}
		}
		/* PAS ENCORE EN PROD
		else if (containerCategoryCode.equals("plate-well")) {
			if (experimentTypeCode.equals("lib-normalization")) {
				sqlView = "v_libnorm_plate_updated_tongl";
			}
			else if (experimentTypeCode.equals("denat-dil-lib")) {
				sqlView = "v_libdenatdil_plate_updated_tongl";
			}
			else {
				//autres experimentTypeCode a venir ??
				sqlView = "TODO ??";
			}
		}
		*/

	
		List<Container> results = null;		
		if (containerCode != null) {
			Logger.debug("Modify container " + containerCategoryCode +"("+ containerCode+ ") with SOLEXA sql: "+ sqlView );
			results = this.jdbcTemplate.query("select * from " + sqlView + " where container_code = ? order by container_code, project desc, sample_code, tag, exp_short_name", new Object[]{containerCode} 
			,new RowMapper<Container>() {
				public Container mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError;
					// en modification passer importState=null
					Container c=  commonContainerMapRow(rs0, rowNum0, ctxErr, containerCategoryCode, experimentTypeCode, null); 
					return c;
				}
			});
		}
		else {
			Logger.debug("Modify containers " + containerCategoryCode + " with SOLEXA sql: "+ sqlView );
			results = this.jdbcTemplate.query("select * from " + sqlView + " order by container_code, project desc, sample_code, tag, exp_short_name", new Object[]{} 
			,new RowMapper<Container>() {
				public Container mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSet rs0 = rs;
					int rowNum0 = rowNum;
					ContextValidation ctxErr = contextError; 
					// en modification passer importState=null
					Container c=  commonContainerMapRow(rs0, rowNum0, ctxErr, containerCategoryCode, experimentTypeCode, null); 
					return c;
				}
			});
		}
		return demultiplexContainer(results);			
	}
	
	/*************************************************************************************************************************************************
	 * Sub-method to set the sequencingProgramType of a flowcell
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
		results = this.jdbcTemplate.query("select support_code, seq_program_type from " + sqlView + " order by container_code, project desc, sample_code, tag, exp_short_name", new Object[]{} 
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
	
	
	/*************************************************************************************************************************************************
	 * for eventually find all the "depot" (in case of a migration) 
	 */
	public List<Experiment> findAllIlluminaDepotExperimentToCreate(final ContextValidation contextError, final String protocoleCode) throws DAOException {
		List<Experiment> results = this.jdbcTemplate.query("SELECT * FROM v_depotfc_tongl_reprise ORDER BY 1", new Object[]{} 
		,new RowMapper<Experiment>() {
			public Experiment mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultSet rs0 = rs;
				int rowNum0 = rowNum;
				ContextValidation ctxErr = contextError; 
				Experiment e = ExperimentImport.experimentDepotIlluminaMapRow(rs0, rowNum0, ctxErr, protocoleCode); 
				return e;
			}
		}); 
		return results;
	}
	
	/*************************************************************************************************************************************************
	 * for normal use
	 */
	public List<Experiment> findIlluminaDepotExperiment(final ContextValidation contextError, final String protocoleCode) throws DAOException {
		List<Experiment> results = this.jdbcTemplate.query("SELECT * FROM v_depotfc_tongl ORDER BY 1", new Object[]{} 
		,new RowMapper<Experiment>() {
			public Experiment mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultSet rs0 = rs;
				int rowNum0 = rowNum;
				ContextValidation ctxErr = contextError; 
				Experiment e = ExperimentImport.experimentDepotIlluminaMapRow(rs0, rowNum0, ctxErr, protocoleCode); 
				return e;
			}
		}); 
		return results;
	}
	
	
	/*************************************************************************************************************************************************
	 * To get the indexes and update the "Parameter" collection
	 * FDS 30/04/2015: nglbi_code=>code, short_name=>shortName (et non plus code), cng_name=>name
	 * FDS 24/09/2015 Migration des lanes deja importees donc passer sur nouvelle solution !
	 */
	public List<Index> findIndexIlluminaToCreate(final ContextValidation contextError)throws SQLException {
		List<Index> results = this.jdbcTemplate.query("select nglbi_code, short_name, cng_name,(CASE WHEN type = 1 THEN 'SINGLE-INDEX'::text WHEN type = 2 THEN 'DUAL-INDEX'::text WHEN type = 3 THEN 'MID'::text ELSE NULL::text END) AS code_category,sequence from t_index order by 1" 
				,new RowMapper<Index>() {
					@SuppressWarnings("rawtypes")
					public Index mapRow(ResultSet rs, int rowNum) throws SQLException {
						Index index=new Index();

						index.code=rs.getString("nglbi_code");
						index.shortName=rs.getString("short_name");
						index.name=rs.getString("cng_name");
						
						index.categoryCode=rs.getString("code_category");
						index.sequence=rs.getString("sequence");
						index.traceInformation=new TraceInformation();
						InstanceHelpers.updateTraceInformation(index.traceInformation, "ngl-data");
						
						Logger.info("index code:"+index.code);
						return index;
					}
				});
		return results;
	}

	
	/*************************************************************************************************************************************************
	 * UPDATE Solexa tables t_sample & t_individual tables (import/update dates) 
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
		
		sql = "UPDATE t_individual SET " + column + " = ? WHERE id in (select individual_id from t_sample where stock_barcode = ?)";
		parameters = new ArrayList<Object[]>();
		for (Sample sample : samples) {
	        parameters.add(new Object[] {new Date(), sample.code}); 
		}
		this.jdbcTemplate.batchUpdate(sql, parameters);  
		
		contextError.removeKeyFromRootKeyName(key);
	}

	
	/*************************************************************************************************************************************************
	 * UPDATE Solexa table t_lane (import/update dates) 
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
	
	/*************************************************************************************************************************************************
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
		try {
			this.jdbcTemplate.batchUpdate(sql, parameters);
		}
		catch(Exception e) {
			Logger.debug(e.getMessage());
		}
		contextError.removeKeyFromRootKeyName(key);
	}
	
	
	/*************************************************************************************************************************************************
	 * UPDATE main table witch contains experiments of type "depots" in Solexa to keep trace of the imports
	 * 
	 * @param experiments
	 * @param contextError
	 * @param mode
	 * @throws DAOException
	 */
	public void updateLimsDepotExperiment(List<Experiment> experiments, ContextValidation contextError, String mode) throws DAOException {
		String key, column;
		key = "update_synchroDate";
		column = "ngl_synchro_date";			
		
		contextError.addKeyToRootKeyName(key);
		
		String sql = "UPDATE t_workflow SET " + column + " = ? WHERE id = ?";
		List<Object[]> parameters = new ArrayList<Object[]>();
		for (Experiment experiment : experiments) {
	        parameters.add(new Object[] {new Date(), experiment.experimentProperties.get("limsCode").value}); 
		}
		try {
			this.jdbcTemplate.batchUpdate(sql, parameters);
		}
		catch(Exception e) {
			Logger.debug(e.getMessage());
		}
		contextError.removeKeyFromRootKeyName(key);
	}
	
}
