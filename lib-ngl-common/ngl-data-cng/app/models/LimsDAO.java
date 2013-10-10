package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
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
import models.utils.instance.ContainerHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
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
public class LimsDAO {

	private JdbcTemplate jdbcTemplate;

	private static final String CONTAINER_CATEGORY_CODE= "tube";
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
	
	protected static final String SAMPLE_TYPE_CODE_DEFAULT = "default-sample";
	protected static final String SAMPLE_CATEGORY_CODE = "default";
	
	protected static final String FLOWCELL_CATEGORY_CODE = "default-flowcell";
	
	
	@Autowired
	@Qualifier("lims")
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}




	// ok
	public Sample findSampleToCreate(final ContextValidation contextError, String sampleCode) throws SQLException, DAOException {

		List<Sample> results = this.jdbcTemplate.query("fn_sampletongl",new Object[]{sampleCode} 
		,new RowMapper<Sample>() {

			@SuppressWarnings("rawtypes")
			public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {

				Sample sample = new Sample();
				sample.traceInformation.setTraceInformation(InstanceHelpers.getUser());

				sample.code=rs.getString("code"); //barcode
				
				String sampleTypeCode=getSampleTypeFromLims();
				//Logger.debug("Sample Type :"+sampleTypeCode);

				if(sampleTypeCode==null){
					contextError.addErrors( "typeCode", "limsdao.error.emptymapping", sample.code);
					return null;
				}
				/*
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
				*/

				sample.typeCode=sampleTypeCode;
			
				sample.projectCodes=new ArrayList<String>();
				sample.projectCodes.add(rs.getString("project_code")); // jointure t_project.name

				sample.name=rs.getString("name1"); // barcode
				sample.referenceCollab= null; 
				sample.taxonCode=rs.getString("taxon_code"); // t_org.ncbi_taxon_id

				sample.comments=new ArrayList<Comment>(); // comments
				sample.comments.add(new Comment(rs.getString("comments")));
				
				//sample.categoryCode=sampleType.category.code;
				sample.categoryCode=SAMPLE_CATEGORY_CODE; 

				/*
				for(PropertyDefinition propertyDefinition :sampleType.propertiesDefinitions) {
					String code=null;
					try{
						code=rs.getString(propertyDefinition.code);

						if(sample.properties==null){ sample.properties=new HashMap<String, PropertyValue>();}
						sample.properties.put(propertyDefinition.code, new PropertySingleValue(code));

					}catch (SQLException e) {
						Logger.info("Property "+propertyDefinition.code+" not exist in pl_MaterielToNGL");
					}
				}
				*/
				sample.properties=new HashMap<String, PropertyValue>();
				
				sample.importTypeCode=getImportTypeCode();
				//Logger.debug("Import Type "+sample.importTypeCode);
				return sample;
			}



		});        

		if(results.size()==1)
		{
			Logger.debug("One sample");
			return results.get(0);
		}
		else return null;

	}

	
	// OK
	public List<Project> findProjectToCreate(final ContextValidation contextError) throws SQLException, DAOException {
		List<Project> results = this.jdbcTemplate.query("fn_projetongl",new Object[]{} 
		,new RowMapper<Project>() {

			@SuppressWarnings("rawtypes")
			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {

				
				Project project = new Project(rs.getString(1).trim(), rs.getString(2).trim());
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
				return project;
			}
		});
		
		return results;
	}
	
	
	
	
	// ok
	public List<ContainerSupport> findFlowcellToCreate(final ContextValidation contextError) throws SQLException, DAOException {
		List<ContainerSupport> results = this.jdbcTemplate.query("fn_FlowcellToNGL",new Object[]{} 
		,new RowMapper<ContainerSupport>() {

			@SuppressWarnings("rawtypes")
			public ContainerSupport mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				// String name, String barCode, String categoryCode, String stockCode /* code frigo */, String x, String y

				ContainerSupport cs = new ContainerSupport();

				cs.name= rs.getString("name1").trim();
				
				cs.barCode = rs.getString("barcode");
				
				cs.categoryCode = FLOWCELL_CATEGORY_CODE;
				
				cs.stockCode = null;
				
				cs.x = "1";
				
				cs.y = rs.getString("y");

				return cs;
			}
		});
		
		return results;
	}
	
	
	
	private String getImportTypeCode() {
			 return "default-import";
	}
	

		
	private String getSampleTypeFromLims() {

	/*	
		if(tadnco.equals("15")) return "fosmid";
		else
		if(tadnco.equals("8")) return "plasmid";
		else
		if(tadnco.equals("2")) return "BAC";
		else
		if(tadnco.equals("1") && !tprco.equals("11")) return "gDNA";
		else
		if(tadnco.equals("1") && tprco.equals("11")) return "MeTa-DNA";
		else
		if(tadnco.equals("19") || tadnco.equals("6")) return "amplicon";
		else
		if(tadnco.equals("12")) return "cDNA";
		else
		if( tadnco.equals("11")) return "total-RNA";
		else 
		if(tadnco.equals("18")) return "sRNA";
		else
		if(tadnco.equals("10")) return "mRNA";
		else
		if(tadnco.equals("17")) return "chIP";
		//Logger.debug("Erreur mapping Type materiel ("+tadnco+")/Type projet ("+tprco+") et Sample Type");
		return null;
	*/
		return SAMPLE_TYPE_CODE_DEFAULT; 
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

