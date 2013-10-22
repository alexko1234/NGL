package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.PropertyDefinition;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import play.Logger;
import play.api.modules.spring.Spring;
import play.data.validation.ValidationError;
import validation.ContextValidation;


/**
 * @author mhaquell
 *
 */
@Repository
public class LimsDAO {

	private JdbcTemplate jdbcTemplate;

	private static final String CONTAINER_CATEGORY_CODE= "tube";
	private static final String CONTAINER_STATE_CODE="IWP";
	private static final String CONTAINER_PROPERTIES_BQ="tag";
	public static final String LIMS_CODE="limsCode";
	private static final String SAMPLE_ADPATER="isAdapters";
	private static final String RECEPTION_DATE ="receptionDate";
	protected static final String PROJECT_CATEGORY_CODE = "default";
	protected static final String PROJECT_TYPE_CODE_FG = "france-genomique";
	protected static final String PROJECT_TYPE_CODE_DEFAULT = "default-project";
	protected static final String PROJECT_PROPERTIES_FG_GROUP="fgGroup";

	protected static final String IMPORT_CATEGORY_CODE="sample-import";
	
	
	@Autowired
	@Qualifier("lims")
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}


	/**
	 * Find Tube Lims who have flag 'tubinNGL=0' ( this flag is update to 1 when Tube exists in NGL database)
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


	public Sample findSampleToCreate(final ContextValidation contextError, String sampleCode) throws SQLException, DAOException {

		List<Sample> results = this.jdbcTemplate.query("pl_MaterielToNGLUn @nom_materiel=?",new Object[]{sampleCode} 
		,new RowMapper<Sample>() {

			@SuppressWarnings("rawtypes")
			public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {

				Sample sample = new Sample();
				InstanceHelpers.updateTraceInformation(sample.traceInformation);
				String tadco = rs.getString("tadco");
				String tprco = rs.getString("tprco");
				sample.code=rs.getString("code");
				
				Logger.debug("Code Materiel (adnco) :"+rs.getString(LIMS_CODE)+" , Type Materiel (tadco) :"+tadco +", Type Projet (tprco) :"+tprco);

				String sampleTypeCode=getSampleTypeFromLims(tadco,tprco);

				if(sampleTypeCode==null){
					contextError.addErrors( "typeCode", "limsdao.error.emptymapping", tadco, sample.code);
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

				Logger.debug("Sample Type :"+sampleTypeCode);

				sample.typeCode=sampleTypeCode;
			

				sample.projectCodes=new ArrayList<String>();
				sample.projectCodes.add(rs.getString("project"));

				sample.name=rs.getString("name");
				sample.referenceCollab=rs.getString("referenceCollab");
				sample.taxonCode=rs.getString("taxonCode");

				sample.comments=new ArrayList<Comment>();
				sample.comments.add(new Comment(rs.getString("comment")));
				sample.categoryCode=sampleType.category.code;


				for(PropertyDefinition propertyDefinition :sampleType.propertiesDefinitions)
				{
					String code=null;
					try{
						code=rs.getString(propertyDefinition.code);

						if(sample.properties==null){ sample.properties=new HashMap<String, PropertyValue>();}
						if(code!=null){
							sample.properties.put(propertyDefinition.code, new PropertySingleValue(code));
						}

					}catch (SQLException e) {
						Logger.info("Property "+propertyDefinition.code+" not exist in pl_MaterielToNGL");
					}

				}

				boolean tara=false;

				if(rs.getInt("tara")==1){
					tara=true;
				}

				if(tara){

					Logger.debug("Tara sample "+sample.code);
					
					TaraDAO  taraServices = Spring.getBeanOfType(TaraDAO.class);
					if(sample.properties==null){ sample.properties=new HashMap<String, PropertyValue>();}

					Map<String, PropertyValue> map=taraServices.findTaraSampleFromLimsCode(rs.getInt(LIMS_CODE),contextError);

					if(map!=null){
						sample.properties.putAll(map);
					} else {
						tara=false;
					}

				}
				//Logger.debug("Adpatateur :"+sample.properties.get("adaptateur").value.toString());

				boolean adapter=false;
				if(sample.properties.get(SAMPLE_ADPATER)!=null){
					adapter= Boolean.parseBoolean(sample.properties.get(SAMPLE_ADPATER).value.toString());
				}
				
				sample.importTypeCode=getImportTypeCode(tara,adapter);
				Logger.debug("Import Type "+sample.importTypeCode);
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

	
	
	public List<Project> findProjectToCreate(final ContextValidation contextError) throws SQLException, DAOException {
		List<Project> results = this.jdbcTemplate.query("pl_ProjetToNGL ",new Object[]{} 
		,new RowMapper<Project>() {

			@SuppressWarnings("rawtypes")
			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {

				
				Project project = new Project(rs.getString(2).trim(),rs.getString(1));
				String fgGroupe=rs.getString("groupefg");
				if(fgGroupe==null){
					project.typeCode=PROJECT_TYPE_CODE_DEFAULT;
				}
				else {
					project.typeCode=PROJECT_TYPE_CODE_FG;
					project.properties= new HashMap<String, PropertyValue>();
					project.properties.put(PROJECT_PROPERTIES_FG_GROUP, new PropertySingleValue(fgGroupe));
				}
			
				project.categoryCode=PROJECT_CATEGORY_CODE;
				project.stateCode="IP";
				InstanceHelpers.updateTraceInformation(project.traceInformation);
				return project;
			}
		});
		
		return results;
	}
	
	public static String getImportTypeCode(boolean tara, boolean adapter) {
		
		//Logger.debug("Adaptateur "+adapter);
		//Logger.debug("Tara "+tara);
		if(adapter){
			if(tara){
				return "tara-library";
			}
			else { return "library"; }
		}
		else if(tara){
			return "tara-default";
		}
		else {
			 return "default-import";
		}
	}

	private String getSampleTypeFromLims(String tadnco,String tprco) {

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
		if(tadnco.equals("16")) return "gDNA";
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
		else
		if(tadnco.equals("20")) return "depletedRNA";

		//Logger.debug("Erreur mapping Type materiel ("+tadnco+")/Type projet ("+tprco+") et Sample Type");
		return null;
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


	public void updateMaterielmanipLims(List<Container> containers,ContextValidation contextError) {

		String limsCode=null;
		String rootKeyName=null;
		
		contextError.addKeyToRootKeyName("updateMaterielmanipLims");
		
		for(Container container:containers){

			rootKeyName="container["+container.code+"]";
			contextError.addKeyToRootKeyName(rootKeyName);
			limsCode=container.properties.get(LIMS_CODE).value.toString();

			if(container.properties==null || limsCode==null)
			{
				contextError.addErrors("limsCode","error.PropertyNotExist",LIMS_CODE,container.support.barCode);

			}else {
				try{
			
					String sql="pm_MaterielmanipInNGL @matmaco=?";
					Logger.debug(sql+limsCode);
					this.jdbcTemplate.update(sql, Integer.parseInt(limsCode));

				} catch(DataAccessException e){

					contextError.addErrors("",e.getMessage(), container.support.barCode);
				}
			}
			
			contextError.removeKeyFromRootKeyName(rootKeyName);


		}
		
		contextError.removeKeyFromRootKeyName("updateMaterielmanipLims");
	}

	//TODO
	public List<Container> findContainersToUpdate(ContextValidation contexValidation){

		List<Container> results = this.jdbcTemplate.query("pl_TubeUpdateToNGL ",new Object[]{} 
		,new RowMapper<Container>() {

			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {

				Container container = new Container();
				
				return container;
			}

		});        

		return results;
	}


	/**
	 *  Find contents from a container code 
	 *  
	 *  */
	public List<Content> findContentsFromContainer(String sqlContent, String code) {

		List<Content> results = this.jdbcTemplate.query(sqlContent,new Object[]{code} 
		,new RowMapper<Content>() {

			@SuppressWarnings("rawtypes")
			public Content mapRow(ResultSet rs, int rowNum) throws SQLException {

				SampleUsed sampleUsed = new SampleUsed(rs.getString("sampleCode"),null,null);
				Content content= new Content(sampleUsed);
				// Todo add properties from ExperimentType
				content.properties=new HashMap<String, PropertyValue>();
				content.properties.put("percentPerLane", new PropertySingleValue(rs.getFloat("percentPerLane")));
				content.properties.put("tag",new PropertySingleValue(rs.getString("tag")));
				return content;
			}

		});        

		return results;
		
	}

}

