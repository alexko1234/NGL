package models.laboratory.sample.description.dao;

import models.laboratory.sample.description.ImportCategory;
import models.utils.dao.AbstractDAO;

import org.springframework.stereotype.Repository;

@Repository
public class ImportCategoryDAO extends AbstractDAO<ImportCategory>{

	public ImportCategoryDAO() {
		super("import_category",ImportCategory.class,true);
	}

}
