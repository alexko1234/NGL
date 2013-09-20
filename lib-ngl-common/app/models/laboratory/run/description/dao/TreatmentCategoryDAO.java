package models.laboratory.run.description.dao;

import org.springframework.stereotype.Repository;
import models.laboratory.run.description.TreatmentCategory;
import models.utils.dao.AbstractDAO;

@Repository
public class TreatmentCategoryDAO extends AbstractDAO<TreatmentCategory>{

	public TreatmentCategoryDAO() {
		super("treatment_category",TreatmentCategory.class,true);
	}
}