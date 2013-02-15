package models.laboratory.experiment.description;

import models.laboratory.experiment.description.dao.TransferMethodTypeDAO;

public class TransferMethodType extends AbstractExperiment{

	public static Finder<TransferMethodType> find = new Finder<TransferMethodType>(TransferMethodTypeDAO.class.getName());
	
	public TransferMethodType() {
		super.classNameDAO=TransferMethodTypeDAO.class.getName();
	}

	
}
