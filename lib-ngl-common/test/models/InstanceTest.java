package models;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputOutputContainer;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.processes.instance.Process;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.DescriptionHelper;
import models.utils.InstanceHelpers;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.Helpers;
import utils.AbstractTests;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class InstanceTest extends AbstractTests{

	static final Class<DBObject>[] classTest= new  Class[]{Process.class,Sample.class,Experiment.class,Project.class,Container.class};

	static String id;
	static ProjectType sProjectType;
	static SampleType sSampleType;
	static ExperimentType sexpExperimentType;
	static InstrumentUsedType sIntrumentUsedType;
	static ContainerCategory sContainerCategory;
	static State sState;
	static Resolution sResolution;


	@BeforeClass
	public static void startTest(){
		app = getFakeApplication();
		Helpers.start(app);
		initData();
		Helpers.stop(app);
	}

	@AfterClass
	public static void endTest(){
		app = getFakeApplication();
		Helpers.start(app);
		deleteData();
		Helpers.stop(app);
	}
	
	
	@Before
	public void start(){
		 app = getFakeApplication();
		 Helpers.start(app);
	}
	
	@After
	public void stop(){
		Helpers.stop(app);
	}	


	private static void deleteData(){
		try {

			for(Class t:classTest){
				MongoDBDAO.getCollection(t.getSimpleName(), t).drop();
			}
			ProjectCategory projectCategory= sProjectType.category;
			sProjectType.remove();
			projectCategory.remove();

			SampleCategory sampleCategory=sSampleType.category;
			sSampleType.remove();
			sampleCategory.remove();

			ExperimentCategory experimentCategory=sexpExperimentType.category;
			sexpExperimentType.remove();
			experimentCategory.remove();

			for (Instrument i :sIntrumentUsedType.instruments){
				i.remove();
			}
			sIntrumentUsedType.remove();

			sState.remove();
			sResolution.remove();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private static void initData() {
		try {

			//ProjectType
			sProjectType=ProjectType.find.findByCode("projectType");
			if(sProjectType==null){
				sProjectType=DescriptionHelper.getProjectType("projectType","projectType", "categoryProject",null);
				sProjectType.save();
			}		
			sProjectType=ProjectType.find.findByCode(sProjectType.code);


			//SampleType
			sSampleType=SampleType.find.findByCode("sampleType");
			if(sSampleType==null){
				sSampleType=DescriptionHelper.getSampleType("sampleType","sampleType", "sampleCategory",null);
				sSampleType.save();
			}		
			sSampleType=SampleType.find.findByCode(sSampleType.code);

			//ExperimentType
			sexpExperimentType=ExperimentType.find.findByCode("experimentType");
			if(sexpExperimentType==null){
				sexpExperimentType=DescriptionHelper.getExperimentType("experimentType", "experimentName", "experimentCategory", null);
				sexpExperimentType.save();
			}
			sexpExperimentType=ExperimentType.find.findByCode("experimentType");

			//Instrument
			sIntrumentUsedType=InstrumentUsedType.find.findByCode("instrumentUsedType");
			if(sIntrumentUsedType==null){
				sIntrumentUsedType=DescriptionHelper.getInstrumentUsedType("instrumentUsedType","instrumentCode","instrumentCategory",null);
				sIntrumentUsedType.save();
			}
			sIntrumentUsedType=InstrumentUsedType.find.findByCode("instrumentUsedType");

			//Container category
			sContainerCategory=ContainerCategory.find.findByCode("containerCategory");
			if(sContainerCategory==null){
				sContainerCategory=DescriptionHelper.getCategory(ContainerCategory.class, "containerCategory");
				sContainerCategory.save();
			}
			sContainerCategory=ContainerCategory.find.findByCode("containerCategory");

			sState =State.find.findByCode("Etatcontainer");
			if(sState==null){
				sState=DescriptionHelper.getState("Etatcontainer");
				sState.save();
				sState =State.find.findByCode("Etatcontainer");
			}

			sResolution=Resolution.find.findByCode("Resolutioncontainer");
			if(sResolution==null){
				sResolution=DescriptionHelper.getResolution("Resolutioncontainer");
				sResolution.save();
				Resolution.find.findByCode("Resolutioncontainer");
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}	


	@Test 
	public void saveInstanceMongo() throws InstantiationException, IllegalAccessException, ClassNotFoundException{

		for(Class t:classTest){
			saveDBOject(t);
		}
	}


	@Test
	public void updateProject(){

		Project project=findObject(Project.class);

		project.name="projectName";
		project.comments=new ArrayList<Comment>();
		project.comments.add(new Comment("comment"));
		project.traceInformation=new TraceInformation();
		project.traceInformation.setTraceInformation("test");
		project.typeCode="projectType";
		project.categoryCode="categoryProject";
		project.stateCode="EtatprojectType";

		MongoDBDAO.save(Project.class.getSimpleName(), project);

		project=findObject(Project.class);

		assertThat(project.code).isEqualTo("ProjectCode");
		assertThat(project.name).isEqualTo("projectName");

		ProjectCategory projectCategory=project.getProjectCategory();
		assertThat(projectCategory).isNotNull();

		ProjectType projectType=project.getProjectType();
		assertThat(projectType).isNotNull();

		assertThat(project.getState()).isNotNull();
		assertThat(project.getState().code).isEqualTo("EtatprojectType");

	}

	@Test
	public void updateSample(){
		Sample sample=findObject(Sample.class);

		sample.name="sampleName";
		sample.referenceCollab="Ref collab";
		sample.valid=TBoolean.UNSET;

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

		assertThat(sample.getSampleCategory().code).isEqualTo("sampleCategory");
		assertThat(sample.getSampleType().code).isEqualTo("sampleType");
		assertThat(sample.getProjects().size()).isEqualTo(1);
		


	}

	@Test
	public void updateContainer(){
		Container container=findObject(Container.class);

		container.support=new ContainerSupport();
		container.support.barCode="containerName";
		container.categoryCode="containerCategory";

		container.projectCodes= new ArrayList<String>();
		container.projectCodes.add("ProjectCode");

		container.sampleCodes=new ArrayList<String>();
		container.sampleCodes.add("SampleCode");

		container.stateCode="Etatcontainer";
		container.valid=TBoolean.FALSE;
		container.resolutionCode="Resolutioncontainer";

		container.contents=new ArrayList<Content>();
		container.contents.add(new Content(new SampleUsed("SampleCode", "sampleType", "sampleCategory")));

		container.fromExperimentTypeCodes=new ArrayList<String>();
		container.fromExperimentTypeCodes.add("experimentType");
		//TODO 
		//public Map<String, PropertyValue> properties;		
		//public List<QualityControlResult> qualityControlResults; 
		//public List<Volume> mesuredVolume;
		//public List<Volume> calculedVolume;		
		//public String fromPurifingCode;

		container.comments=new ArrayList<Comment>();
		container.comments.add(new Comment("comment"));
		container.traceInformation.setTraceInformation("test"); 

		MongoDBDAO.save(Container.class.getSimpleName(), container);
		container=findObject(Container.class);

		assertThat(container.code).isEqualTo("ContainerCode");
		assertThat(container.support.barCode).isEqualTo("containerName");
		assertThat(container.comments.get(0).comment).isEqualTo("comment");
		assertThat(container.traceInformation.createUser).isEqualTo("test");
		assertThat(container.getContainerCategory().code).isEqualTo("containerCategory");
		assertThat(container.getProjects()).isNotEmpty();
		assertThat(container.getProjects().get(0).code).isEqualTo("ProjectCode");

		assertThat(container.getState().code).isEqualTo("Etatcontainer");
		assertThat(container.getResolution().code).isEqualTo("Resolutioncontainer");

		assertThat(container.getSamples()).isNotEmpty();
		assertThat(container.getSamples().get(0).code).isEqualTo("SampleCode");

		assertThat(container.contents).isNotEmpty();
		assertThat(container.contents.get(0).sampleUsed.getSample().code).isEqualTo("SampleCode");
		assertThat(container.contents.get(0).sampleUsed.getSampleCategory().code).isEqualTo("sampleCategory");
		assertThat(container.contents.get(0).sampleUsed.getSampleType().code).isEqualTo("sampleType");

		assertThat(container.getFromExperimentTypes()).isNotEmpty();
		assertThat(container.getFromExperimentTypes().size()).isEqualTo(1);
		assertThat(container.getFromExperimentTypes().get(0).code).isEqualTo("experimentType");

	}


	@Test
	public void updateExperience(){
		Experiment experiment=findObject(Experiment.class);

		experiment.typeCode="experimentType";
		experiment.categoryCode="experimentCategory";

		experiment.projectCodes= new ArrayList<String>();
		experiment.projectCodes.add("ProjectCode");

		experiment.sampleCodes=new ArrayList<String>();
		experiment.sampleCodes.add("SampleCode");

		experiment.instrument=new InstrumentUsed();
		experiment.instrument.categoryCode="instrumentCategory";
		experiment.instrument.code="instrumentCode";

		experiment.stateCode="New";
		experiment.resolutionCode="ResolutionexperimentType";

		//TODO
		//public Map<String,PropertyValue> experimentProperties;
		//public Map<String, PropertyValue> instrumentProperties;
		//		public String protocolCode;

		experiment.listInputOutputContainers=new ArrayList<InputOutputContainer>();
		InputOutputContainer inputOutputContainer=new InputOutputContainer();
		experiment.listInputOutputContainers.add(inputOutputContainer);

		inputOutputContainer.comment=new Comment();
		inputOutputContainer.inputContainers=new ArrayList<ContainerUsed>();
		ContainerUsed containerUsedIn=new ContainerUsed();
		containerUsedIn.containerCode="ContainerCode";
		//TODO 
		//containerUsedIn.experimentProperties
		//containerUsedIn.instrumentProperties
		//containerUsedIn.volume
		inputOutputContainer.inputContainers.add(containerUsedIn);

		inputOutputContainer.outputContainers=new ArrayList<ContainerUsed>();
		ContainerUsed containerUsedOut=new ContainerUsed();
		containerUsedOut.containerCode="ContainerCode";
		inputOutputContainer.outputContainers.add(containerUsedOut);



		experiment.comments=new ArrayList<Comment>();
		experiment.comments.add(new Comment("comment"));
		experiment.traceInformation.setTraceInformation("test"); 

		MongoDBDAO.save(Experiment.class.getSimpleName(), experiment);
		experiment=findObject(Experiment.class);

		assertThat(experiment.code).isEqualTo("ExperimentCode");
		assertThat(experiment.comments.get(0).comment).isEqualTo("comment");
		assertThat(experiment.traceInformation.createUser).isEqualTo("test");
		assertThat(experiment.getExperimentCategory()).isNotNull();
		assertThat(experiment.getExperimentCategory().code).isEqualTo("experimentCategory");
		assertThat(experiment.getExperimentType()).isNotNull();
		assertThat(experiment.getExperimentType().code).isEqualTo("experimentType");
		assertThat(experiment.getProjects()).isNotEmpty();
		assertThat(experiment.getProjects().get(0).code).isEqualTo("ProjectCode");

		assertThat(experiment.instrument.code).isEqualTo("instrumentCode");
		assertThat(experiment.instrument.categoryCode).isEqualTo("instrumentCategory");
		assertThat(experiment.getState().code).isEqualTo("New");
		assertThat(experiment.getResolution()).isNotNull();
		assertThat(experiment.getResolution().code).isEqualTo("ResolutionexperimentType");

		assertThat(experiment.getSamples()).isNotEmpty();
		assertThat(experiment.getSamples().get(0).code).isEqualTo("SampleCode");

		assertThat(experiment.listInputOutputContainers).isNotEmpty();
		assertThat(experiment.listInputOutputContainers.get(0).inputContainers).isNotEmpty();
		assertThat(experiment.listInputOutputContainers.get(0).outputContainers).isNotEmpty();
		assertThat(experiment.listInputOutputContainers.get(0).inputContainers.get(0).containerCode).isEqualTo("ContainerCode");
		assertThat(experiment.listInputOutputContainers.get(0).outputContainers.get(0).containerCode).isEqualTo("ContainerCode");

	}




	@Test
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
