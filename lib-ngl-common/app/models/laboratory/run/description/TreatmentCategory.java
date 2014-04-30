package models.laboratory.run.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.dao.TreatmentCategoryDAO;


public class TreatmentCategory extends AbstractCategory<TreatmentCategory>{
	
	public static enum CODE {ngsrg, global, sequencing, quality, bpa};

	public static Finder<TreatmentCategory> find = new Finder<TreatmentCategory>(TreatmentCategoryDAO.class.getName());
	
	public TreatmentCategory() {
		super(TreatmentCategoryDAO.class.getName());
	}

}
