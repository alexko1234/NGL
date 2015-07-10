package models.laboratory.run.description.dao;

import org.springframework.stereotype.Repository;
import models.laboratory.run.description.RunCategory;
import models.utils.dao.AbstractDAODefault;

@Repository
public class RunCategoryDAO extends AbstractDAODefault<RunCategory>{

	public RunCategoryDAO() {
		super("run_category",RunCategory.class,true);
	}
}



