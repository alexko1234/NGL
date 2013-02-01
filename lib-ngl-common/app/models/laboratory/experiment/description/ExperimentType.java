package models.laboratory.experiment.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import play.modules.spring.Spring;

public class ExperimentType extends AbstractExperiment{

	//Possibility to do purification
	public boolean doPurification=Boolean.FALSE;
	public boolean mandatoryPurification=Boolean.FALSE;
	//List of possible purificationMethodType
	public List<PurificationMethodType> possiblePurificationMethodTypes=new ArrayList<PurificationMethodType>();
	
	//Possibility to do quality control
	public boolean doQualityControl=Boolean.FALSE;
	public boolean mandatoryQualityControl=Boolean.FALSE;
	//List of possible quality control type
	public List<QualityControlType> possibleQualityControlTypes=new ArrayList<QualityControlType>();
	
	public List<ExperimentType> previousExperimentTypes=new ArrayList<ExperimentType>();
	
	public ExperimentCategory experimentCategory;
	
	
	

	public static ExperimentType findByCode(String code)
	{
		ExperimentTypeDAO expTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		return expTypeDAO.findByCode(code);
	}
	
	public static ExperimentType findExpTypeById(long id)
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		return experimentTypeDAO.findById(id);
	}
	
	public ExperimentType add()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		return experimentTypeDAO.add(this);
	}
	
	public void update()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		experimentTypeDAO.update(this);
	}
	
}
