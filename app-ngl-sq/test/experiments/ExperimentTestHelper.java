package experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.laboratory.experiment.instance.OneToManyContainer;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.utils.dao.DAOException;
import play.Logger;

public class ExperimentTestHelper {
	public final static String EXP_CODE = "TESTYANNEXP";
	
	public static Experiment getFakeExperiment(){
		Experiment exp = new Experiment(EXP_CODE);
		exp.state = new State("N","ngsrg");
		exp.atomicTransfertMethods = new ArrayList<AtomicTransfertMethod>();
		exp.instrument = new InstrumentUsed();
		exp.instrument.outContainerSupportCategoryCode="tube";
		exp.experimentProperties = new HashMap<String, PropertyValue>();
		exp.instrumentProperties = new HashMap<String, PropertyValue>();
		
		return exp;
		
	}
	
	public static ManytoOneContainer getManytoOneContainer(){
		ManytoOneContainer atomicTransfertMethod = new ManytoOneContainer();
		atomicTransfertMethod.inputContainerUseds = new ArrayList<ContainerUsed>();
		atomicTransfertMethod.outputContainerUseds = new ArrayList<ContainerUsed>();
		return atomicTransfertMethod;
	}
	
	public static OneToOneContainer getOnetoOneContainer(){
		OneToOneContainer atomicTransfertMethod = new OneToOneContainer();
		atomicTransfertMethod.inputContainerUseds = new ArrayList<ContainerUsed>();
		atomicTransfertMethod.outputContainerUseds = new ArrayList<ContainerUsed>();
		
		return atomicTransfertMethod;
	}
	
	public static OneToManyContainer getOnetoManyContainer(){
		OneToManyContainer atomicTransfertMethod = new OneToManyContainer();
		atomicTransfertMethod.inputContainerUseds = new ArrayList<ContainerUsed>();
		atomicTransfertMethod.outputContainerUseds = new ArrayList<ContainerUsed>();
		return atomicTransfertMethod;
	}
	
	public static ContainerUsed getContainerUsed(String code){
		ContainerUsed containerUsed = new ContainerUsed(code);
		containerUsed.state = new State();
		containerUsed.state.code = "IW-E";
		containerUsed.experimentProperties =  new HashMap<String, PropertyValue>();
		containerUsed.instrumentProperties =  new HashMap<String, PropertyValue>();
		containerUsed.locationOnContainerSupport=new LocationOnContainerSupport();
		containerUsed.locationOnContainerSupport.code=code;
		return containerUsed;
	}
	
	public static InstrumentUsed getInstrumentPrepFlowcell(){
		Instrument instrument = new Instrument();
		InstrumentUsed instrumentUsed = new InstrumentUsed();
		try {
			instrument = instrument.find.findByCode("cBot Fluor A");
			instrumentUsed.code = instrument.code;
			instrumentUsed.categoryCode = instrument.categoryCode;
			instrumentUsed.typeCode = instrument.typeCode;
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			Logger.error("DAO error: "+e.getMessage(),e);;
		}
		
		return instrumentUsed;
	};
	
	public static Experiment getFakePrepFlowcell(){
		Random randomGenerator=new Random();
		String code = "TEST-PREPFLOWCELL"+randomGenerator.nextInt(1000);
		Experiment exp = getFakeExperimentWithAtomicExperimentManyToOne("prepa-flowcell");
		exp.code=code;
		exp.categoryCode = "transformation";
		exp.instrument = getInstrumentPrepFlowcell();
		
		return exp;
	}
	
	public static Experiment getFakeExperimentWithAtomicExperiment(String typeCode){
		Experiment exp = ExperimentTestHelper.getFakeExperiment();
		exp.typeCode=typeCode;		
		ManytoOneContainer atomicTransfert1 = ExperimentTestHelper.getManytoOneContainer();
		atomicTransfert1.position=1;
		ManytoOneContainer atomicTransfert2 = ExperimentTestHelper.getManytoOneContainer();
		atomicTransfert2.position=2;
		
		exp.atomicTransfertMethods.add(0,atomicTransfert1);
		exp.atomicTransfertMethods.add(1, atomicTransfert2);
		
		ContainerUsed container1_1=ExperimentTestHelper.getContainerUsed("CONTAINER1_1");
		container1_1.percentage=20.0;
		Content content1_1=new Content("CONTENT1_1","TYPE","CATEGORIE");
		container1_1.contents=new ArrayList<Content>();
		content1_1.properties=new HashMap<String, PropertyValue>();
		content1_1.properties.put("tag", new PropertySingleValue("IND1"));
		content1_1.properties.put("tagCategory", new PropertySingleValue("TAGCATEGORIE"));
		content1_1.properties.put("tag", new PropertySingleValue("IND2"));
		content1_1.properties.put("tagCategory", new PropertySingleValue("TAGCATEGORIE"));
		container1_1.contents.add(content1_1);
		
		ContainerUsed container1_2=ExperimentTestHelper.getContainerUsed("CONTAINER1_2");
		container1_2.percentage= 80.0;
		Content content1_2=new Content("CONTENT1_2","TYPE","CATEGORIE");
		container1_2.contents=new ArrayList<Content>();
		content1_2.properties=new HashMap<String, PropertyValue>();
		content1_2.properties.put("tag", new PropertySingleValue("IND1"));
		content1_2.properties.put("tagCategory", new PropertySingleValue("TAGCATEGORIE"));
		container1_2.contents.add(content1_2);
		
		atomicTransfert1.inputContainerUseds.add(container1_1);
		atomicTransfert1.inputContainerUseds.add(container1_2);
		
		ContainerUsed container2_2=ExperimentTestHelper.getContainerUsed("CONTAINER2_2");
		container2_2.percentage= 100.0;
		Content content2_2=new Content("CONTENT2_2","TYPE","CATEGORIE");
		container2_2.contents=new ArrayList<Content>();
		content2_2.properties=new HashMap<String, PropertyValue>();
		container2_2.contents.add(content2_2);
		atomicTransfert2.inputContainerUseds.add(container2_2);
		return exp;
	}
	
	public static Experiment getFakeExperimentWithAtomicExperimentManyToOne(String typeCode){
		Experiment exp = getFakeExperiment();
		exp.typeCode=typeCode;		
 		ManytoOneContainer atomicTransfert1 = ExperimentTestHelper.getManytoOneContainer();
		atomicTransfert1.position=1;
		ManytoOneContainer atomicTransfert2 = ExperimentTestHelper.getManytoOneContainer();
		atomicTransfert2.position=2;
		
		exp.atomicTransfertMethods.add(0,atomicTransfert1);
		exp.atomicTransfertMethods.add(1, atomicTransfert2);
		
		ContainerUsed container1_1=ExperimentTestHelper.getContainerUsed("ADI_RD1");
		container1_1.percentage=20.0;
		Content content1_1=new Content("ADI_RD","MeTa-DNA","DNA");
		container1_1.contents=new ArrayList<Content>();
		content1_1.properties=new HashMap<String, PropertyValue>();
/*		content1_1.properties.put("tag", new PropertySingleValue("IND1"));
		content1_1.properties.put("tagCategory", new PropertySingleValue("TAGCATEGORIE"));*/
		content1_1.properties.put("tag", new PropertySingleValue("IND2"));
		content1_1.properties.put("tagCategory", new PropertySingleValue("TAGCATEGORIE"));
		container1_1.contents.add(content1_1);
		
		ContainerUsed container1_2=ExperimentTestHelper.getContainerUsed("C2EV3ACXX_3");
		container1_2.percentage= 80.0;
		Content content1_2=new Content("BFB_AABA","amplicon","amplicon");
		container1_2.contents=new ArrayList<Content>();
		content1_2.properties=new HashMap<String, PropertyValue>();
		content1_2.properties.put("tag", new PropertySingleValue("IND1"));
		content1_2.properties.put("tagCategory", new PropertySingleValue("TAGCATEGORIE"));
		container1_2.contents.add(content1_2);
		
		atomicTransfert1.inputContainerUseds.add(container1_1);
		atomicTransfert1.inputContainerUseds.add(container1_2);
		
		ContainerUsed container2_2=ExperimentTestHelper.getContainerUsed("C2EV3ACXX_5");
		container2_2.percentage= 100.0;
		Content content2_2=new Content("ADI_RD","MeTa-DNA","DNA");
		container2_2.contents=new ArrayList<Content>();
		content2_2.properties=new HashMap<String, PropertyValue>();
		container2_2.contents.add(content2_2);
		atomicTransfert2.inputContainerUseds.add(container2_2);
		return exp;
	}
	
	public static InstrumentUsed getInstrumentSolutionStock(){
		Instrument instrument = new Instrument();
		InstrumentUsed instrumentUsed = new InstrumentUsed();
		try {
			instrument = instrument.find.findByCode("hand");
			instrumentUsed.code = instrument.code;
			instrumentUsed.categoryCode = instrument.categoryCode;
			instrumentUsed.typeCode = instrument.typeCode;
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			Logger.error("DAO error: "+e.getMessage(),e);;
		}
		
		return instrumentUsed;
	}
	
	public static Experiment getFakeSolutionStock(){
		Random randomGenerator=new Random();
		String code = "TEST-SOLUTIONSTOCK"+randomGenerator.nextInt(1000);
		Experiment exp = getFakeExperimentWithAtomicExperimentManyToOne("solution-stock");
		exp.code=code;
		exp.categoryCode = "transformation";
		exp.instrument = getInstrumentSolutionStock();
		
		return exp;
	}
	
	public static Experiment getFakeExperimentWithAtomicExperimentOneToOne(String typeCode){
		Experiment exp = getFakeExperiment();
		exp.typeCode=typeCode;
		OneToOneContainer atomicTransfert1 = ExperimentTestHelper.getOnetoOneContainer();
		OneToOneContainer atomicTransfert2 = ExperimentTestHelper.getOnetoOneContainer();
		
		exp.atomicTransfertMethods.add(0,atomicTransfert1);
		exp.atomicTransfertMethods.add(0,atomicTransfert2);
		
		
		
		return exp;
		
	}
}
