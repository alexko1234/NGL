package models.laboratory.processes.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.dao.ExperimentTypeNodeDAO;
import models.utils.Model;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;
import models.utils.dao.AbstractDAO;
import play.api.modules.spring.Spring;

public class ExperimentTypeNode extends Model<ExperimentTypeNode>{

//	public static final Finder<ExperimentTypeNode> find = new Finder<ExperimentTypeNode>(ExperimentTypeNodeDAO.class.getName());
	public static final Finder<ExperimentTypeNode> find = new Finder<>(ExperimentTypeNodeDAO.class);

	//Possibility to do purification
	public boolean doPurification = false; // Boolean.FALSE;
	public boolean mandatoryPurification = false; // Boolean.FALSE;
	
	//Possibility to do quality control
	public boolean doQualityControl = false; // Boolean.FALSE;
	public boolean mandatoryQualityControl = false; // Boolean.FALSE;

	//Possibility to do transfert
	public boolean doTransfert = false; // Boolean.FALSE;
	public boolean mandatoryTransfert = false; // Boolean.FALSE;

	public ExperimentType experimentType;
	public List<ExperimentTypeNode> previousExperimentTypeNodes=new ArrayList<ExperimentTypeNode>();;
	public List<ExperimentType> previousExperimentTypes;
	public List<ExperimentType> possibleQualityControlTypes=new ArrayList<ExperimentType>();
	public List<ExperimentType> possiblePurificationTypes=new ArrayList<ExperimentType>();
	public List<ExperimentType> possibleTransferts=new ArrayList<ExperimentType>();
	
	
	public ExperimentTypeNode() {
		super(ExperimentTypeNodeDAO.class.getName());
	}

}
