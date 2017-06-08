package services.instance.sample;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.mongojack.DBQuery;

import com.mongodb.MongoException;

import controllers.migration.OneToVoidContainer;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult.Sort;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.sample.instance.Sample;
import models.laboratory.sample.instance.reporting.SampleExperiment;
import models.laboratory.sample.instance.reporting.SampleProcess;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import play.Logger;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportData;

public class UpdateReportingData extends AbstractImportData {

	public UpdateReportingData(FiniteDuration durationFromStart,FiniteDuration durationFromNextIteration) {
			super("UpdateReportingData", durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		Logger.debug("Start reporting synchro");
		
		MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).sort("code", Sort.DESC).limit(5000)
			.cursor.forEach(sample -> {
				updateProcesses(sample);				
			});
		
	}

	private void updateProcesses(Sample sample) {
		List<Process> processes = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("sampleOnInputContainer.sampleCode", sample.code))
				.toList();
		
		sample.processes = processes.stream()
					.map(process -> convertToSampleProcess(sample, process))
					.collect(Collectors.toList());		
	}

	private SampleProcess convertToSampleProcess(Sample sample, Process process) {
		SampleProcess sampleProcess = new SampleProcess();
		sampleProcess.code= process.code;
		sampleProcess.typeCode= process.typeCode;
		sampleProcess.categoryCode= process.categoryCode;
		sampleProcess.state= process.state;
		sampleProcess.traceInformation= process.traceInformation;
		sampleProcess.properties= process.properties;
		
		sampleProcess.experiments = updateExperiments(sample, process);
		
		return sampleProcess;
	}

	private List<SampleExperiment> updateExperiments(Sample sample, Process process) {
		List<Experiment> experiments = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.in("code", process.experimentCodes)).toList();
		experiments.parallelStream().map(exp -> convertToSampleExperiments(process, exp));
		return null;
	}

	
	//map key = expCode-processCode
	private Map<String, List<SampleExperiment>> convertToSampleExperiments(Process process, Experiment experiment) {
		Set<String> containerCodes = new TreeSet(process.outputContainerCodes);
		containerCodes.add(process.inputContainerCode);
		
		experiment.atomicTransfertMethods.forEach(atm -> {
			if(OneToVoidContainer.class.isInstance(atm)){
				atm.inputContainerUseds.forEach(icu -> {
					if(containerCodes.contains(icu.code)){
						SampleExperiment sampleExperiment = new SampleExperiment();
					}					
				});
			}else{
				atm.inputContainerUseds.forEach(icu -> {
					atm.outputContainerUseds.forEach(ocu ->{
						if(containerCodes.containsAll(Arrays.asList(icu.code, ocu.code))){
							SampleExperiment sampleExperiment = new SampleExperiment();
							
						}
					});
					
				});	
			}
		});
		//one-to-void
		//one-to-one
		//many-to-one
		//one-to-many ???
		
		return null;
	}
}
