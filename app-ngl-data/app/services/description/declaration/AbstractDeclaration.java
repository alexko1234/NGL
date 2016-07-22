package services.description.declaration;

import java.util.List;

import com.typesafe.config.ConfigFactory;

import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessExperimentType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.sample.description.SampleType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;

public abstract class AbstractDeclaration {

	protected abstract List<ExperimentType> getExperimentTypeDEV();
	protected abstract List<ExperimentType> getExperimentTypePROD();
	protected abstract List<ExperimentType> getExperimentTypeUAT();

	public List<ExperimentType> getExperimentType(){

		if(ConfigFactory.load().getString("ngl.env").equals("DEV")){
			return getExperimentTypeDEV();
		}else if(ConfigFactory.load().getString("ngl.env").equals("UAT")){
			return getExperimentTypeUAT();
		}else if(ConfigFactory.load().getString("ngl.env").equals("PROD")) {
			return getExperimentTypePROD();
		}else {
			throw new RuntimeException("ngl.env value not implemented");
		}
	}


	protected abstract List<ProcessType> getProcessTypeDEV();
	protected abstract List<ProcessType> getProcessTypePROD();
	protected abstract List<ProcessType> getProcessTypeUAT();

	public List<ProcessType> getProcessType(){

		if(ConfigFactory.load().getString("ngl.env").equals("DEV")){
			return getProcessTypeDEV();
		}else if(ConfigFactory.load().getString("ngl.env").equals("UAT")){
			return getProcessTypeUAT();
		}else if(ConfigFactory.load().getString("ngl.env").equals("PROD")) {
			return getProcessTypePROD();
		}else {
			throw new RuntimeException("ngl.env value not implemented");
		}
	}
	
	
	protected abstract void getExperimentTypeNodeDEV();
	protected abstract void getExperimentTypeNodePROD();
	protected abstract void getExperimentTypeNodeUAT();

	public void getExperimentTypeNode(){

		if(ConfigFactory.load().getString("ngl.env").equals("DEV")){
			getExperimentTypeNodeDEV();
		}else if(ConfigFactory.load().getString("ngl.env").equals("UAT")){
			 getExperimentTypeNodeUAT();
		}else if(ConfigFactory.load().getString("ngl.env").equals("PROD")) {
			 getExperimentTypeNodePROD();
		}else {
			throw new RuntimeException("ngl.env value not implemented");
		}
	}
	
	
	protected static ProcessExperimentType getPET(String expCode, Integer index) {
		return new ProcessExperimentType(getExperimentType(expCode), index);
	}
	
	protected static List<ExperimentType> getExperimentTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentType.class,ExperimentType.find, codes);
	}
	
	protected static ExperimentType getExperimentType(String code) throws DAOException {
		return DAOHelpers.getModelByCode(ExperimentType.class,ExperimentType.find, code);
	}
	
	public static List<InstrumentUsedType> getInstrumentUsedTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(InstrumentUsedType.class,InstrumentUsedType.find, codes);
	}

	public static List<SampleType> getSampleTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(SampleType.class,SampleType.find, codes);
	}
	
	public static List<ExperimentTypeNode> getExperimentTypeNodes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(ExperimentTypeNode.class,ExperimentTypeNode.find, codes);
	}

}
