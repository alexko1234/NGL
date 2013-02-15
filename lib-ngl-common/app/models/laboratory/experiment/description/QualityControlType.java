package models.laboratory.experiment.description;

import models.laboratory.experiment.description.dao.QualityControlTypeDAO;


public class QualityControlType extends AbstractExperiment{

	public QualityControlType() {
		super.classNameDAO=QualityControlTypeDAO.class.getName();
	}
	
	public static Finder<QualityControlType> find = new Finder<QualityControlType>(QualityControlTypeDAO.class.getName()); 
}
