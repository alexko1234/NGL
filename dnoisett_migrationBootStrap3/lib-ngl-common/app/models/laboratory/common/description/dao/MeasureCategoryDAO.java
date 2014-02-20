package models.laboratory.common.description.dao;



import models.laboratory.common.description.MeasureCategory;
import models.utils.dao.AbstractDAODefault;

import org.springframework.stereotype.Repository;



@Repository
public class MeasureCategoryDAO  extends AbstractDAODefault<MeasureCategory>{

	protected MeasureCategoryDAO() {
		super("measure_category", MeasureCategory.class, true);
	}
	

}
