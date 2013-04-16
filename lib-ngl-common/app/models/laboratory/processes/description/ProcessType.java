package models.laboratory.processes.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.StateDAO;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.dao.ProcessTypeDAO;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.api.modules.spring.Spring;

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
	
	/**
	 * Return a list of ListObject that help populating the <select> input
	 * @return List<ListObject>
	 */
	public static List<ListObject> findAllForList() throws DAOException{
		ProcessTypeDAO processTypeDAO = Spring.getBeanOfType(ProcessTypeDAO.class);
		return processTypeDAO.findAllForList();
	}
	
}
