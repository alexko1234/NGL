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
				container.categoryCode="TUBE";
				container.projectCodes=new ArrayList<String>();				
				container.projectCodes.add(rs.getString("prsco"));

				container.sampleCodes=new ArrayList<String>();
				container.sampleCodes.add(rs.getString("sampleCode"));

				container.comments=new ArrayList<Comment>();				
				container.comments.add(new Comment(rs.getString("comment")));
				container.stateCode="IWP";
				container.valid=null;

				container.support=ContainerHelper.getContainerSupportTube(rs.getString("code"));

				container.properties= new HashMap<String, PropertyValue>();
				container.properties.put("codeLims",new PropertyValue(rs.getInt("tubco")));

				container.mesuredConcentration=new PropertyValue(rs.getFloat("tubconcr"), "ng/ul");
				container.mesuredVolume=new PropertyValue(rs.getFloat("tubvolr"), "ul");
				container.mesuredQuantity=new PropertyValue(rs.getFloat("tubqtr"), "ng");

				Content content = new Content();
				content.sampleUsed=new SampleUsed();
				content.sampleUsed.sampleCode=rs.getString("sampleCode");
				container.contents=new ArrayList<Content>();
				container.contents.add(content);

				if(rs.getString("indexBq")!=null){
					content.properties = new HashMap<String, PropertyValue>();
					content.properties.put("index",new PropertyValue(rs.getString("indexBq")));
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
				String codeLims = rs.getString("codeLims");
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
				sample.categoryCode=sampleType.sampleCategory.code;


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

					Map<String, PropertyValue> map=taraServices.findTaraSample(rs.getInt("codeLims"),errors);

					if(map!=null){
						sample.properties.putAll(map);
					}

				}
				//Logger.debug("Adpatateur :"+sample.properties.get("adaptateur").value.toString());

				sample.importTypeCode=getImportTypeCode(tara, Boolean.parseBoolean(sample.properties.get("adaptateur").value.toString()));
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

	private String getImportTypeCode(boolean tara, boolean adaptateur) {
		
		Logger.debug("Adaptateur "+adaptateur);
		Logger.debug("Tara "+tara);
		if(adaptateur){
			if(tara){
				return "importTaraBanqueSolexa";
			}
			else { return "importBanqueSolexa"; }
		}
		else if(tara){
			return "importTara";
		}
		else {
			 return "importNormal";
		}
	}

	private String getSampleTypeFromLims(String tadnco,String tprco) {

		if(tadnco.equals("15")) return "fosmide";
		else
			if(tadnco.equals("1") && !tprco.equals("11")) return "ADNGenomique";
			else
				if(tadnco.equals("1") && tprco.equals("11")) return "ADNMetagenomique";
				else
					if(tadnco.equals("19") || tadnco.equals("6")) return "amplicon";
					else
						if(tadnco.equals("12")) return "cDNA";
						else
							if( tadnco.equals("11")) return "ARNTotal";
							else 
								if(tadnco.equals("18")) return "sRNA";
								else
									if(tadnco.equals("10")) return "ARNm";
									else
										if(tadnco.equals("17")) return "ChiP";
										
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

		for(Container container:containers){

			if(container.properties==null || container.properties.get("codeLims")==null)
			{
				addErrors(errors, "container.properties.codeLims","error.PropertyNotExist","codeLims",container.support.barCode);

			}else {
				try{

					String value=container.properties.get("codeLims").value.toString();
					Logger.debug("pm_TubeidentInNGL @tubco="+value);
					this.jdbcTemplate.update("pm_TubeidentInNGL @tubco=?", Integer.parseInt(value));

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
				container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
				//Logger.debug("Container :"+rs.getString("code"));
				container.code=rs.getString("code");
				container.categoryCode="TUBE";
				container.projectCodes=new ArrayList<String>();				
				container.projectCodes.add(rs.getString("prsco"));

				container.sampleCodes=new ArrayList<String>();
				container.sampleCodes.add(rs.getString("sampleCode"));

				container.comments=new ArrayList<Comment>();				
				container.comments.add(new Comment(rs.getString("comment")));
				container.stateCode="IWP";
				container.valid=null;

				container.support=ContainerHelper.getContainerSupportTube(rs.getString("code"));

				container.properties= new HashMap<String, PropertyValue>();
				container.properties.put("codeLims",new PropertyValue(rs.getInt("tubco")));

				container.mesuredConcentration=new PropertyValue(rs.getFloat("tubconcr"), "ng/ul");
				container.mesuredVolume=new PropertyValue(rs.getFloat("tubvolr"), "ul");
				container.mesuredQuantity=new PropertyValue(rs.getFloat("tubqtr"), "ng");

				Content content = new Content();
				content.sampleUsed=new SampleUsed();
				content.sampleUsed.sampleCode=rs.getString("sampleCode");
				container.contents=new ArrayList<Content>();
				container.contents.add(content);

				if(rs.getString("indexBq")!=null){
					content.properties = new HashMap<String, PropertyValue>();
					content.properties.put("index",new PropertyValue(rs.getString("indexBq")));
				}

				return container;
			}

		});        

		return results;
	}


}

