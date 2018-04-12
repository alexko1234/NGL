package models.laboratory.run.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.run.description.dao.TreatmentCategoryDAO;
import models.utils.dao.AbstractDAO;

public class TreatmentCategory extends AbstractCategory<TreatmentCategory> {
	
//	public static Finder<TreatmentCategory> find = new Finder<TreatmentCategory>(TreatmentCategoryDAO.class.getName());
	public static final Finder<TreatmentCategory,TreatmentCategoryDAO> find = new Finder<>(TreatmentCategoryDAO.class);
	
	public static enum CODE {
		ngsrg, 
		global, 
		sequencing, 
		quality, 
		ba
	}

	public TreatmentCategory() {
		super(TreatmentCategoryDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<TreatmentCategory>> daoClass() {
		return TreatmentCategoryDAO.class;
	}

}
