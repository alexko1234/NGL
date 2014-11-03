package models;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.processes.instance.Process;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.data.validation.ValidationError;
import utils.AbstractTests;
import utils.Constants;
import controllers.CommonController;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class InstanceTest extends AbstractTests{

	static final Class<DBObject>[] classTest= new  Class[]{Process.class,Sample.class,Experiment.class,Project.class,Container.class};

	private static final Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();

	static String id;
	static ProjectType sProjectType;
	static SampleType sSampleType;
	static ExperimentType sexpExperimentType;
	static InstrumentUsedType sIntrumentUsedType;
	static ContainerCategory sContainerCategory;
	static models.laboratory.common.description.State sState;
	static Resolution sResolution;

	@AfterClass
	public static  void deleteData() {
		
	}

	@BeforeClass
	public static  void initData() throws DAOException {
			//ExperimentType
			sexpExperimentType=ExperimentType.find.findAll().get(0);
			
	}	


	//@Test 
	public void saveInstanceMongo() throws InstantiationException, IllegalAccessException, ClassNotFoundException{

		for(Class t:classTest){
			saveDBOject(t);
		}
	}


	//@Test
	public void updateProject() throws DAOException{

		Project project=findObject(Project.class);

		project.name="projectName";
		project.comments=new ArrayList<Comment>();
		project.comments.add(new Comment("comment"));
		project.traceInformation=new TraceInformation();
		project.traceInformation.setTraceInformation("test");
		project.typeCode="projectType";
		project.categoryCode="categoryProject";
		
		project.state = new State(); 
		project.state.code="EtatprojectType";
		project.state.user = "test";
		project.state.date = new Date();
				

		MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, project);

		project=findObject(Project.class);

		assertThat(project.code).isEqualTo("ProjectCode");
		assertThat(project.name).isEqualTo("projectName");

		ProjectCategory projectCategory=ProjectCategory.find.findByCode(project.categoryCode);
		assertThat(projectCategory).isNotNull();

		ProjectType projectType=ProjectType.find.findByCode(project.typeCode);
		assertThat(projectType).isNotNull();

		assertThat(project.state).isNotNull();
		assertThat(project.state.code).isEqualTo("EtatprojectType");
		assertThat(project.state.user).isEqualTo("test");
	}

	//@Test
	public void updateSample(){
		Sample sample=findObject(Sample.class);

		sample.name="sampleName";
		sample.referenceCollab="Ref collab";
		
		sample.valuation= new Valuation(); 
		sample.valuation.valid = TBoolean.UNSET;
		

		sample.categoryCode="sampleCategory";
		sample.typeCode="sampleType";
		sample.projectCodes=	InstanceHelpers.addCode("ProjectCode", sample.projectCodes);

		/*sample.comments=InstanceHelpers.addComment("comment", sample.comments);
		InstanceHelpers.updateTraceInformation(sample.traceInformation); */

		MongoDBDAO.save(Sample.class.getSimpleName(), sample);

		sample=findObject(Sample.class);

		assertThat(sample.code).isEqualTo("SampleCode");
		assertThat(sample.name).isEqualTo("sampleName");
		
		/*assertThat(sample.comments).isNotNull();
		assertThat(sample.comments.get(0).comment).isEqualTo("comment");
		assertThat(sample.traceInformation.modifyUser).isNotNull();
		assertThat(sample.valid).isEqualTo(TBoolean.UNSET);*/

		assertThat(sample.categoryCode).isEqualTo("sampleCategory");
		assertThat(sample.typeCode).isEqualTo("sampleType");
		assertThat(sample.projectCodes.size()).isEqualTo(1);
		


	}

	//@Test
	public void updateContainer(){
		Container container=findObject(Container.class);

		container.support=new LocationOnContainerSupport();
		container.support.code="containerName";
		container.categoryCode="containerCategory";

		container.projectCodes= new ArrayList<String>();
		container.projectCodes.add("ProjectCode");

		container.sampleCodes=new ArrayList<String>();
		container.sampleCodes.add("SampleCode");

		container.state = new State(); 
		container.state.code="Etatcontainer";
		container.state.user = Constants.TEST_USER;
		container.state.date = new Date();

		
		
		container.valuation = new Valuation();
		container.valuation.valid = TBoolean.FALSE;
		container.valuation.user = Constants.TEST_USER;
		container.valuation.date = new Date(); 
		
		container.contents.add(new Content("SampleCode", "sampleType", "sampleCategory"));

		container.fromExperimentTypeCodes=new ArrayList<String>();
		container.fromExperimentTypeCodes.add("experimentType");
		 
			
		//public List<QualityControlResult> qualityControlResults; 
		//public List<Proper> mesuredVolume;
		//public List<Volume> calculedVolume;		
		//public String fromPurifingCode;

		container.comments=new ArrayList<Comment>();
		container.comments.add(new Comment("comment"));
		container.traceInformation.setTraceInformation("test"); 

		MongoDBDAO.save(Container.class.getSimpleName(), container);
		container=findObject(Container.class);

		assertThat(container.code).isEqualTo("ContainerCode");
		assertThat(container.support.code).isEqualTo("containerName");
		assertThat(container.comments.get(0).comment).isEqualTo("comment");
		assertThat(container.traceInformation.createUser).isEqualTo("test");
		assertThat(container.categoryCode).isEqualTo("containerCategory");
		assertThat(container.projectCodes).isNotEmpty();
		assertThat(container.projectCodes.get(0)).isEqualTo("ProjectCode");

		assertThat(container.state.code).isEqualTo("Etatcontainer");

		assertThat(container.sampleCodes).isNotEmpty();
		assertThat(container.sampleCodes.get(0)).isEqualTo("SampleCode");

		assertThat(container.contents).isNotEmpty();
		assertThat(container.contents.get(0).sampleCode).isEqualTo("SampleCode");
		assertThat(container.contents.get(0).sampleCategoryCode).isEqualTo("sampleCategory");
		assertThat(container.contents.get(0).sampleTypeCode).isEqualTo("sampleType");

		assertThat(container.fromExperimentTypeCodes).isNotEmpty();
		assertThat(container.fromExperimentTypeCodes.size()).isEqualTo(1);
		assertThat(container.fromExperimentTypeCodes.get(0)).isEqualTo("experimentType");

	}


	@Test
	public void updateExperience(){
		MongoDBDAO.delete(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", "ExperimentCode"));

		Experiment experiment=new Experiment();
		
		experiment.code="ExperimentCode";
		experiment.typeCode="experimentType";
		experiment.categoryCode="experimentCategory";

		experiment.projectCodes= new ArrayList<String>();
		experiment.projectCodes.add("ProjectCode");

		experiment.sampleCodes=new ArrayList<String>();
		experiment.sampleCodes.add("SampleCode");

		experiment.instrument=new InstrumentUsed();
		experiment.instrument.categoryCode="instrumentCategory";
		experiment.instrument.code="instrumentCode";

		State state=new State("N","test");
		experiment.state=state;
		experiment.state.resolutionCodes=new ArrayList<String>();
		experiment.state.resolutionCodes.add("ResolutionCode");

		//TODO
		//public Map<String,PropertyValue> experimentProperties;
		//public Map<String, PropertyValue> instrumentProperties;
		//		public String protocolCode;


		experiment.comments=new ArrayList<Comment>();
		experiment.comments.add(new Comment("comment"));
		experiment.traceInformation.setTraceInformation("test"); 

		experiment.atomicTransfertMethods= new HashMap<Integer,AtomicTransfertMethod>();
		for(int i=0; i<10; i++){
			OneToOneContainer oneToOneContainer =new OneToOneContainer();
			oneToOneContainer.inputContainerUsed=new ContainerUsed("containerInput"+i);
			oneToOneContainer.outputContainerUsed=new ContainerUsed("containerOutput"+i);
			experiment.atomicTransfertMethods.put(i,oneToOneContainer);
		}
		
		Experiment newExperiment=MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, experiment);
		
		assertThat(newExperiment.code).isEqualTo(experiment.code);
		//assertThat(newExperiment.state.code).isEqualTo(state.code);
		assertThat(experiment.state.resolutionCodes.get(0)).isEqualTo(experiment.state.resolutionCodes.get(0));
		
		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code",experiment.code),DBUpdate.set("state",state));
		newExperiment=MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME,Experiment.class,experiment.code);
		
		assertThat(newExperiment.code).isEqualTo(experiment.code);
		assertThat(newExperiment.state.code).isEqualTo(state.code);
	}

	////@Test
	public void removeInstanceMongo(){
		for(Class t:classTest){
			removeDBOject(t);
		}
	}


	public <T extends DBObject> T saveDBOject(Class<T> type) throws InstantiationException, IllegalAccessException, ClassNotFoundException{

		String collection=type.getSimpleName();
		String code=type.getSimpleName()+"Code";
		T object = (T) Class.forName (type.getName()).newInstance ();
		object.code=code;
		object=MongoDBDAO.save(collection, object);
		id=object._id;
		assertThat(object._id).isNotNull();
		return object;
	}

	public <T extends DBObject> void removeDBOject(Class<T> type){
		String collection=type.getSimpleName();
		String code=type.getSimpleName()+"Code";
		T object=MongoDBDAO.findByCode(collection, type, code);
		MongoDBDAO.delete(collection, object);
		object=MongoDBDAO.findByCode(collection, type, code);
		assertThat(object).isNull();

	}

	public <T extends DBObject> T findObject(Class<T> type){
		String collection=type.getSimpleName();
		String code=type.getSimpleName()+"Code";
		return MongoDBDAO.findByCode(collection, type, code);		
	}
}
