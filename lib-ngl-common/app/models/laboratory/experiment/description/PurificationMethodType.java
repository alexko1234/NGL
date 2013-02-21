package models.laboratory.experiment.description;

import models.laboratory.experiment.description.dao.PurificationMethodTypeDAO;

public class PurificationMethodType extends AbstractExperiment{

	public PurificationMethodType() {
		super(PurificationMethodTypeDAO.class.getName());
	}
	
	public static Finder<PurificationMethodType> find = new Finder<PurificationMethodType>(PurificationMethodTypeDAO.class.getName());
	
	
}
