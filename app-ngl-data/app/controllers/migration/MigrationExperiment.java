package controllers.migration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.laboratory.experiment.instance.OneToManyContainer;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.laboratory.experiment.instance.OneToVoidContainer;
import models.utils.InstanceConstants;
import play.mvc.Result;
import controllers.CommonController;
import controllers.migration.models.ExperimentOld;
import controllers.migration.models.experiment.AtomicTransfertMethodOld;
import controllers.migration.models.experiment.ManytoOneContainerOld;
import controllers.migration.models.experiment.OneToManyContainerOld;
import controllers.migration.models.experiment.OneToOneContainerOld;
import controllers.migration.models.experiment.OneToVoidContainerOld;
import fr.cea.ig.MongoDBDAO;

public class MigrationExperiment extends CommonController{

	static String collection=InstanceConstants.EXPERIMENT_COLL_NAME;
	
	public static Result migrationExperiment(){
		
		MigrationNGLSEQ.backupOneCollection(collection, ExperimentOld.class);
		updateExperiment();
		return ok("End migration experiment");
	}

	public static void updateExperiment() {
		List<ExperimentOld> experimentOlds = MongoDBDAO.find(collection, ExperimentOld.class).toList();
		for(ExperimentOld experimentOld:experimentOlds){
			MongoDBDAO.delete(collection, experimentOld);
			MongoDBDAO.save(collection,newExperiment(experimentOld));
		}
	}
	
	
	static Experiment newExperiment(ExperimentOld experimentOld){
		Experiment exp=new Experiment();
		exp._id=experimentOld._id;
		exp.categoryCode=experimentOld.categoryCode;
		exp.code=experimentOld.code;
		exp.comments=experimentOld.comments;
		exp.experimentProperties=experimentOld.experimentProperties;
		exp.inputContainerSupportCodes=experimentOld.inputContainerSupportCodes;
		exp.instrument=experimentOld.instrument;
		exp.instrumentProperties=experimentOld.instrumentProperties;
		exp.outputContainerSupportCodes=experimentOld.outputContainerSupportCodes;
		exp.projectCodes=experimentOld.projectCodes;
		exp.protocolCode=experimentOld.protocolCode;
		exp.reagents=experimentOld.reagents;
		exp.sampleCodes=experimentOld.sampleCodes;
		exp.state=experimentOld.state;
		exp.traceInformation=experimentOld.traceInformation;
		exp.typeCode=experimentOld.typeCode;
		exp.atomicTransfertMethods=new ArrayList<AtomicTransfertMethod>();
		for(Entry<Integer, AtomicTransfertMethodOld> atomics :experimentOld.atomicTransfertMethods.entrySet()){
			AtomicTransfertMethod atomicTransfertMethod = null;
			if(atomics.getValue() instanceof OneToOneContainerOld){
				atomicTransfertMethod=new OneToOneContainer();
			}else if(atomics.getValue() instanceof ManytoOneContainerOld){
				atomicTransfertMethod=new ManytoOneContainer();
			}else if(atomics.getValue() instanceof OneToManyContainerOld){
				atomicTransfertMethod=new OneToManyContainer();
			}else if (atomics.getValue() instanceof OneToVoidContainerOld){
				atomicTransfertMethod=new OneToVoidContainer();
			}

			atomicTransfertMethod.line=String.valueOf(Integer.sum(atomics.getKey(),1));
			if(atomicTransfertMethod.line.equals("0")){
				throw new RuntimeException("Erreur line = 0");
			}
			atomicTransfertMethod.column="1";
			
			atomicTransfertMethod.inputContainerUseds=atomics.getValue().getInputContainers();
			atomicTransfertMethod.outputContainerUseds=atomics.getValue().getOutputContainers();
			exp.atomicTransfertMethods.add(atomicTransfertMethod);
		}
		//Validation des atomicstransfertmethod
		return exp;
	}
	
}
