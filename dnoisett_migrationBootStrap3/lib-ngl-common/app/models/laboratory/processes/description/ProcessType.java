package models.laboratory.processes.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.CommonInfoType.AbstractCommonInfoTypeFinder;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.dao.StateDAO;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.dao.ExperimentCategoryDAO;
import models.laboratory.processes.description.dao.ProcessTypeDAO;
import models.utils.ListObject;
import models.utils.Model.Finder;
import models.utils.dao.DAOException;
import play.api.modules.spring.Spring;

public class ProcessType extends CommonInfoType{

	public ProcessCategory category;
	
	public List<ExperimentType> experimentTypes;
	
	public ExperimentType voidExperimentType;
	
	public ExperimentType firstExperimentType;
	
	public ExperimentType lastExperimentType;
	

	public static ProcessTypeFinder find = new ProcessTypeFinder(); 
	
	public ProcessType() {
		super(ProcessTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Process);
	}
	

	public static class ProcessTypeFinder extends Finder<ProcessType>{

		public ProcessTypeFinder() {
			super(ProcessTypeDAO.class.getName());			
		}
		
		public List<ProcessType> findByProcessCategoryCode(String processCategoryCode) throws DAOException{
			return ((ProcessTypeDAO)getInstance()).findByProcessCategoryCode(processCategoryCode);
		}
	}
	
}
