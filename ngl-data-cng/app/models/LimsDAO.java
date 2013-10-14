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
				
				String sampleTypeCode=getSampleTypeFromLims();
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
			
				sample.projectCodes=new ArrayList<String>();
				sample.projectCodes.add(rs.getString("project_code")); // jointure t_project.name

				sample.name=rs.getString("name1"); // barcode
				sample.referenceCollab= null; // stockbarcode ?
				sample.taxonCode=rs.getString("taxoncode"); // t_org.ncbi_taxon_id

				sample.comments=new ArrayList<Comment>(); // comments
				sample.comments.add(new Comment(rs.getString("comments")));
				
				sample.categoryCode=sampleType.category.code;

				//pb : column with different types (not only varchar) !
				
				//for(PropertyDefinition propertyDefinition :sampleType.propertiesDefinitions) {
					
				//	String code=null;
				//	try{
				//		value=rs.getString(propertyDefinition.code.toLowerCase());
						
				//		Logger.info("code of property : " + code);
						/*
						propertyDefinitions.add(newPropertiesDefinition("Taille associée au taxon", "taxonSize", LevelService.getLevels(Level.CODE.Content,Level.CODE.Sample),Double.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("Mb"), MeasureUnit.find.findByCode("Mb")));
						propertyDefinitions.add(newPropertiesDefinition("Fragmenté", "isFragmented", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true));
						propertyDefinitions.add(newPropertiesDefinition("Adaptateurs", "isAdapters", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true));
						propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, false));
						*/
						
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
		int posNext = 0;
		int listSize  =  results.size(); 
		while (pos < listSize-1    )   {
			posNext = pos+x;
			while (  results.get(pos).code.equals( results.get(posNext).code ) ) {
				// difference between the two project codes
				if (! results.get(pos).projectCodes.get(0).equals(results.get(posNext).projectCodes.get(0))) {
					results.get(pos).projectCodes.add( results.get(posNext).projectCodes.get(0) ); 
				}
				// difference between the two comments
				if (! results.get(pos).comments.get(0).equals(results.get(posNext).comments.get(0))) {
					results.get(pos).comments.add( results.get(posNext).comments.get(0) ); 
				}
				// all the difference have been reported on the first sample found (at the position pos)
				// so we can delete the sample at the position (posNext)
				results.remove(posNext);
				listSize--;
			}
			pos++;
		}
		
		return results;
	}
	
	
	
	public List<Sample> testTri() { 
		List<Sample> results = new ArrayList<Sample>();
		
		Sample s0 = new Sample();
		s0.code = "c0";
		s0.projectCodes = null;
		Sample s1 = new Sample();
		s1.code = "c1";
		s1.projectCodes = null;
		Sample s2 = new Sample();
		s2.code = "c2";
		s2.projectCodes=new ArrayList<String>();
		s2.projectCodes.add("p0");
		Sample s3 = new Sample();
		s3.code = "c2";
		s3.projectCodes=new ArrayList<String>();
		s3.projectCodes.add("p1");
		Sample s4 = new Sample();
		s4.code = "c2";
		s4.projectCodes=new ArrayList<String>();
		s4.projectCodes.add("p2");
		Sample s5 = new Sample();
		s5.code = "c5";
		
		results.add(s0);
		results.add(s1);
		results.add(s2);
		results.add(s3);
		results.add(s4);
		results.add(s5);
	
		//affect all the project codes to a same sample 
		/// required to have an ordered list (see ORDER BY clause in the sql of the view)
		int pos = 0;
		int x=1;
		int posNext = 0;
		int listSize  =  results.size(); 
		while (pos < listSize-1    )   {
			System.out.println("pos = "+ pos); 
			posNext = pos+x;
			System.out.println("posNext = "+ posNext); 
			
			while (  results.get(pos).code.equals( results.get(posNext).code ) ) {
				// difference between the two project codes
				if (! results.get(pos).projectCodes.get(0).equals(results.get(posNext).projectCodes.get(0))) {
					results.get(pos).projectCodes.add( results.get(posNext).projectCodes.get(0) ); 
				}
				// difference between the two comments
				//if (! results.get(pos).comments.get(0).equals(results.get(posNext).comments.get(0))) {
				//	results.get(pos).comments.add( results.get(posNext).comments.get(0) ); 
				//}
				// all the difference have been reported on the first sample found (at the position pos)
				// so we can delete the sample at the position (posNext)
				results.remove(posNext);
				listSize--;
				System.out.println("listSize = " + listSize); 
				//x++;
			}
			//x=1;
			pos++;
			System.out.println("pos = "+ pos); 
		}
		return results;
	}
	
	
	
	
	
	public List<ContainerSupport> findFlowcellsToCreate(final ContextValidation contextError) throws SQLException, DAOException {
		List<ContainerSupport> results = this.jdbcTemplate.query("select * from v_flowcelltongl;", new Object[]{} 
		, new RowMapper<ContainerSupport>() {

			@SuppressWarnings("rawtypes")
			public ContainerSupport mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				// String name, String barCode, String categoryCode, String stockCode /* code frigo */, String x, String y

				ContainerSupport cs = new ContainerSupport();

				cs.name= rs.getString("name1").trim();
				
				cs.barCode = rs.getString("barcode");
				
				cs.categoryCode = "flowcell " + rs.getString("y");
				
				cs.stockCode = null;
				
				cs.x = "1";
				
				cs.y = rs.getString("y");
				
				
				// rs contains "idx" column (index) && "code_sample" (code du sample)
				
				return cs;
			}
		});
		
		return results;
	}




	
	
	
	
	/**
	 * TODO :  find lanes who have flag 'available=0' ( this flag is update to 1 when lane exists in NGL database)
	 * 
	 * @param contextError
	 * @return
	 */
	public List<Container> findContainersToCreate(ContextValidation contextError){

		List<Container> results = this.jdbcTemplate.query("select * from v_flowcelltongl;",new Object[]{} 
		,new RowMapper<Container>() {

			@SuppressWarnings("rawtypes")
			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {

				Container container = new Container();
				container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
				//Logger.debug("Container :"+rs.getString("code"));
				container.code=String.valueOf(rs.getInt("lane_id"));
				
				container.categoryCode=CONTAINER_CATEGORY_CODE;
				container.projectCodes=new ArrayList<String>();				
				// a voir
				//container.projectCodes.add(rs.getString("project_code"));

				container.sampleCodes=new ArrayList<String>();
				container.sampleCodes.add(rs.getString("code_sample"));

				container.comments=new ArrayList<Comment>();				
				//container.comments.add(new Comment(rs.getString("comments")));
				container.stateCode=CONTAINER_STATE_CODE;
				container.valid=null;

				container.support=ContainerHelper.getContainerSupportLane(rs.getString("barcode"));
				
				container.support.y = rs.getString("y");

				container.properties= new HashMap<String, PropertyValue>();
				container.properties.put(LIMS_CODE,new PropertySingleValue(rs.getInt("lane_id")));

				container.mesuredConcentration=new PropertySingleValue((float) 0); //new PropertySingleValue(rs.getFloat("tubconcr"), "ng/µl");
				container.mesuredVolume=new PropertySingleValue((float) 0); //new PropertySingleValue(rs.getFloat("tubvolr"), "µl");
				container.mesuredQuantity=new PropertySingleValue((float) 0); // new PropertySingleValue(rs.getFloat("tubqtr"), "ng");

				Content content = new Content();
				content.sampleUsed=new SampleUsed();
				content.sampleUsed.sampleCode=rs.getString("code_sample");
				container.contents=new ArrayList<Content>();
				container.contents.add(content);

				content.properties = new HashMap<String, PropertyValue>();
				content.properties.put("index",new PropertySingleValue(rs.getString("idx")));
			

				return container;
			}

		});        

		return results;
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

