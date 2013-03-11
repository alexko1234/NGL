package models.laboratory.experiment.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.api.modules.spring.Spring;

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
	
	public static Finder<ExperimentType> find = new Finder<ExperimentType>(ExperimentTypeDAO.class.getName());
	
	public ExperimentType() {
		super(ExperimentTypeDAO.class.getName());
	}

	public static ExperimentType findExpTypeById(long id) throws DAOException
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		return experimentTypeDAO.findById(id);
	}
	
	public static List<ListObject> findAllForList() throws DAOException{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		return experimentTypeDAO.findAllForList();
	}
	
	
}
