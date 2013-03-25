package models.laboratory.processus.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processus.description.dao.ProcessTypeDAO;

public class ProcessType extends CommonInfoType{

	public ProcessCategory processCategory;
	
	public List<ExperimentType> experimentTypes;
	
	public ExperimentType voidExperimentType;
	
	public ExperimentType firstExperimentType;
	
	public ExperimentType lastExperimentType;
	

	public static Finder<ProcessType> find = new Finder<ProcessType>(ProcessTypeDAO.class.getName()); 
	
	public ProcessType() {
		super(ProcessTypeDAO.class.getName());
	}
	
	
}
