package models.laboratory.common.description.dao;



import models.laboratory.common.description.MeasureCategory;
import models.utils.dao.AbstractDAO;

import org.springframework.stereotype.Repository;



@Repository
public class MeasureCategoryDAO  extends AbstractDAO<MeasureCategory>{

	protected MeasureCategoryDAO() {
		super("measure_category", MeasureCategory.class, true);
	}
	

}
