package models.laboratory.experiment.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.sample.description.SampleType;
import models.utils.dao.DAOException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Parent class categories not represented by a table in the database
 * Database relationship for experiment with instrumentUsedType and protocol are represented in CommonInfoType table
 * @author ejacoby
 *
 */
public class ExperimentType extends CommonInfoType {
 
	public ExperimentCategory category;
	//Relationship accessible by the parent table in the database
	public List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
	//public List<Protocol> protocols = new ArrayList<Protocol>();
	public String atomicTransfertMethod;
	
	public String shortCode;
	
	public Boolean newSample=Boolean.FALSE;
	
	public  List<SampleType> sampleTypes=new ArrayList<SampleType>();
	
	public ExperimentType() {
		super(ExperimentTypeDAO.class.getName());		
	}
	
	@JsonIgnore
	public static ExperimentTypeFinder find = new ExperimentTypeFinder();
	
	@JsonIgnore
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(models.laboratory.common.description.Level.CODE.Experiment);
	}
	
	public static class ExperimentTypeFinder extends CommonInfoType.AbstractCommonInfoTypeFinder<ExperimentType,ExperimentTypeDAO> {

		public ExperimentTypeFinder() {
			super(ExperimentTypeDAO.class);
		}
		
		public List<String> findVoidProcessExperimentTypeCode(String processTypeCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findVoidProcessExperimentTypeCode(processTypeCode);
			return getInstance().findVoidProcessExperimentTypeCode(processTypeCode);
		}
		
		public List<ExperimentType> findPreviousExperimentTypeForAnExperimentTypeCode(String code) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findPreviousExperimentTypeForAnExperimentTypeCode(code);
			return getInstance().findPreviousExperimentTypeForAnExperimentTypeCode(code);
		}
		
		public List<ExperimentType> findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(String code, String processTypeCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(code, processTypeCode);
			return getInstance().findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(code, processTypeCode);
		}
		
		public List<ExperimentType> findNextExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(String code, String processTypeCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findNextExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(code, processTypeCode);
			return getInstance().findNextExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(code, processTypeCode);
		}
		
		public List<ExperimentType> findByCategoryCode(String categoryCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findByCategoryCode(categoryCode);
			return getInstance().findByCategoryCode(categoryCode);
		}
		
		public List<ExperimentType> findActiveByCategoryCode(String categoryCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findActiveByCategoryCode(categoryCode);
			return getInstance().findActiveByCategoryCode(categoryCode);
		}
		
		public List<ExperimentType> findByCategoryCodes(List<String> categoryCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findByCategoryCodes(categoryCode);
			return getInstance().findByCategoryCodes(categoryCode);
		}
		
		public List<ExperimentType> findByCategoryCodeAndProcessTypeCode(String categoryCode, String processTypeCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findByCategoryCodeAndProcessTypeCode(categoryCode, processTypeCode);
			return getInstance().findByCategoryCodeAndProcessTypeCode(categoryCode, processTypeCode);
		}
		
		public List<ExperimentType> findByCategoryCodeWithoutOneToVoid(String categoryCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findByCategoryCodeWithoutOneToVoid(categoryCode);
			return getInstance().findByCategoryCodeWithoutOneToVoid(categoryCode);
		}	
		
		public List<ExperimentType> findByCategoryCodesWithoutOneToVoid(List<String> categoryCodes) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findByCategoryCodesWithoutOneToVoid(categoryCodes);
			return findByCategoryCodesWithoutOneToVoid(categoryCodes);
		}	

		public List<ExperimentType> findNextExperimentTypeCode(String previousExperimentTypeCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findNextExperimentTypeCode(previousExperimentTypeCode);
			return getInstance().findNextExperimentTypeCode(previousExperimentTypeCode);
		}
		
		public List<ExperimentType> findByPreviousExperimentTypeCodeInProcessTypeContext(String previousExperimentTypeCode, String processTypeCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findByPreviousExperimentTypeCodeInProcessTypeContext(previousExperimentTypeCode, processTypeCode);
			return getInstance().findByPreviousExperimentTypeCodeInProcessTypeContext(previousExperimentTypeCode, processTypeCode);
		}
		
		public List<ExperimentType> findByProcessTypeCode(String processTypeCode, boolean onlyPositivePosition) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).findByProcessTypeCode(processTypeCode, onlyPositivePosition);
			return getInstance().findByProcessTypeCode(processTypeCode, onlyPositivePosition);
		}
		
		public Integer countDistinctExperimentPositionInProcessType(String processTypeCode) throws DAOException{
//			return ((ExperimentTypeDAO)getInstance()).countDistinctExperimentPositionInProcessType(processTypeCode);
			return getInstance().countDistinctExperimentPositionInProcessType(processTypeCode);
		}
		
	}
	
}
