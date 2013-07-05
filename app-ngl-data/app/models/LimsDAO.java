package models;

import static validation.utils.ConstraintsHelper.addErrors;

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

	
	@Autowired
	@Qualifier("lims")
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}


	public List<Container> findContainersToCreate(Map<String, List<ValidationError>> errors){

		List<Container> results = this.jdbcTemplate.query("pl_TubeToNGL ",new Object[]{} 
		,new RowMapper<Container>() {

			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {

				Container container = new Container();
				container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
				//Logger.debug("Container :"+rs.getString("code"));
				container.code=rs.getString("code");
				container.categoryCode=CONTAINER_CATEGORY_CODE;
				container.projectCodes=new ArrayList<String>();				
				container.projectCodes.add(rs.getString("prsco"));

				container.sampleCodes=new ArrayList<String>();
				container.sampleCodes.add(rs.getString("sampleCode"));

				container.comments=new ArrayList<Comment>();				
				container.comments.add(new Comment(rs.getString("comment")));
				container.stateCode=CONTAINER_STATE_CODE;
				container.valid=null;

				container.support=ContainerHelper.getContainerSupportTube(rs.getString("code"));

				container.properties= new HashMap<String, PropertyValue>();
				container.properties.put(LIMS_CODE,new PropertyValue(rs.getInt("tubco")));
				container.properties.put(RECEPTION_DATE,new PropertyValue(rs.getString(RECEPTION_DATE)));

				
				container.mesuredConcentration=new PropertyValue(rs.getFloat("tubconcr"), "ng/µl");
				container.mesuredVolume=new PropertyValue(rs.getFloat("tubvolr"), "µl");
				container.mesuredQuantity=new PropertyValue(rs.getFloat("tubqtr"), "ng");

				Content content = new Content();
				content.sampleUsed=new SampleUsed();
				content.sampleUsed.sampleCode=rs.getString("sampleCode");
				container.contents=new ArrayList<Content>();
				container.contents.add(content);

				if(rs.getString("indexBq")!=null){
					content.properties = new HashMap<String, PropertyValue>();
					content.properties.put(CONTAINER_PROPERTIES_BQ,new PropertyValue(rs.getString("indexBq")));
				}

				return container;
			}

		});        

		return results;
	}


	public Sample findSampleToCreate(final Map<String, List<ValidationError>> errors, String sampleCode) throws SQLException, DAOException {

		List<Sample> results = this.jdbcTemplate.query("pl_MaterielToNGLUn @nom_materiel=?",new Object[]{sampleCode} 
		,new RowMapper<Sample>() {

			public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {

				Sample sample = new Sample();
				sample.traceInformation.setTraceInformation(InstanceHelpers.getUser());
				String tadco = rs.getString("tadco");
				String tprco = rs.getString("tprco");
				//String codeLims = rs.getString(LIMS_CODE);
				//	Logger.debug("Code Materiel (adnco) :"+codeLims" , Type Materiel (tadco) :"+tadco +", Type Projet (tprco) :"+tprco);

				String sampleTypeCode=getSampleTypeFromLims(tadco,tprco);

				if(sampleTypeCode==null){
					addErrors(errors, "import.sample.typeCode", "limsdao.error.emptymapping",tadco);
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
					addErrors(errors, "sampleType.code", "error.codeNotExist", sampleTypeCode);
					return null;
				}

				//Logger.debug("Sample Type :"+sampleTypeCode);

				sample.typeCode=sampleTypeCode;
				sample.code=rs.getString("code");

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
						sample.properties.put(propertyDefinition.code, new PropertyValue(code));

					}catch (SQLException e) {
						Logger.debug("Property "+propertyDefinition.code+" not exist in pl_MaterielToNGL");
					}

				}

				boolean tara=false;

				if(rs.getInt("tara")==1){
					tara=true;
				}

				if(tara){

					Logger.debug("Tara sample");
					TaraDAO  taraServices = Spring.getBeanOfType(TaraDAO.class);
					if(sample.properties==null){ sample.properties=new HashMap<String, PropertyValue>();}

					Map<String, PropertyValue> map=taraServices.findTaraSample(rs.getInt(LIMS_CODE),errors);

					if(map!=null){
						Logger.debug("Nb properties :"+map.size());
						sample.properties.putAll(map);
					}else { Logger.debug("Map tara null");}

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

	
	
	public List<Project> findProjectToCreate(final Map<String, List<ValidationError>> errors) throws SQLException, DAOException {
		List<Project> results = this.jdbcTemplate.query("pl_ProjetToNGL ",new Object[]{} 
		,new RowMapper<Project>() {

			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {

				Project project = new Project(rs.getString(2).trim(),rs.getString(1));
				String fgGroupe=rs.getString("groupefg");
				if(fgGroupe==null){
					project.typeCode="default-project";
				}
				else {
					project.typeCode="france-genomique";
					project.properties= new HashMap<String, PropertyValue>();
					project.properties.put("fgGroup", new PropertyValue(fgGroupe));
				}
			
				project.categoryCode=PROJECT_CATEGORY_CODE;
				InstanceHelpers.updateTraceInformation(project.traceInformation);
				return project;
			}
		});
		
		return results;
	}
	
	private String getImportTypeCode(boolean tara, boolean adapter) {
		
		Logger.debug("Adaptateur "+adapter);
		Logger.debug("Tara "+tara);
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


	public void updateTubeLims(List<Container> containers,Map<String, List<ValidationError>> errors) {

		String limsCode=null;
		
		for(Container container:containers){

			limsCode=container.properties.get(LIMS_CODE).value.toString();

			if(container.properties==null || limsCode==null)
			{
				addErrors(errors, "container.properties.limsCode","error.PropertyNotExist",LIMS_CODE,container.support.barCode);

			}else {
				try{
			
					Logger.debug("pm_TubeidentInNGL @tubco="+limsCode);
					this.jdbcTemplate.update("pm_TubeidentInNGL @tubco=?", Integer.parseInt(limsCode));

				} catch(DataAccessException e){

					addErrors(errors, "limsdao.updateTubeLims",e.getMessage(), container.support.barCode);
				}
			}

		}
	}

	public List<Container> findContainersToUpdate(Map<String, List<ValidationError>> errors){

		List<Container> results = this.jdbcTemplate.query("pl_TubeUpdateToNGL ",new Object[]{} 
		,new RowMapper<Container>() {

			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {

				Container container = new Container();
				
				return container;
			}

		});        

		return results;
	}


}

