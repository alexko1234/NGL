package models.laboratory.processes.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.dao.ExperimentTypeNodeDAO;
import models.utils.Model;

public class ExperimentTypeNode extends Model<ExperimentTypeNode>{

	//Possibility to do purification
	public boolean doPurification=Boolean.FALSE;
	public boolean mandatoryPurification=Boolean.FALSE;
	
	//Possibility to do quality control
	public boolean doQualityControl=Boolean.FALSE;
	public boolean mandatoryQualityControl=Boolean.FALSE;
	
	public ExperimentType experimentType;
	public List<ExperimentTypeNode> previousExperimentType=new ArrayList<ExperimentTypeNode>();;
	public List<ExperimentType> possibleQualityControlTypes=new ArrayList<ExperimentType>();
	public List<ExperimentType> possiblePurificationTypes=new ArrayList<ExperimentType>();
	
	
	public static Finder<ExperimentTypeNode> find = new Finder<ExperimentTypeNode>(ExperimentTypeNodeDAO.class.getName());
	
	public ExperimentTypeNode() {
		super(ExperimentTypeNodeDAO.class.getName());
	}

	
}
