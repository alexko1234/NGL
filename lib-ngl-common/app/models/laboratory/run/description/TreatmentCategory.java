package models.laboratory.run.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.dao.TreatmentCategoryDAO;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;

public class TreatmentCategory extends AbstractCategory<TreatmentCategory>{
	
//	public static Finder<TreatmentCategory> find = new Finder<TreatmentCategory>(TreatmentCategoryDAO.class.getName());
	public static final Finder<TreatmentCategory,TreatmentCategoryDAO> find = new Finder<>(TreatmentCategoryDAO.class);
	
	public static enum CODE {
		ngsrg, 
		global, 
		sequencing, 
		quality, 
		ba
	};

	public TreatmentCategory() {
		super(TreatmentCategoryDAO.class.getName());
	}

}
