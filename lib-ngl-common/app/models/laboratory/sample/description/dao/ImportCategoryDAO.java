package models.laboratory.sample.description.dao;

import models.laboratory.sample.description.ImportCategory;
import models.utils.dao.AbstractDAODefault;

import org.springframework.stereotype.Repository;

@Repository
public class ImportCategoryDAO extends AbstractDAODefault<ImportCategory>{

	public ImportCategoryDAO() {
		super("import_category",ImportCategory.class,true);
	}

}
