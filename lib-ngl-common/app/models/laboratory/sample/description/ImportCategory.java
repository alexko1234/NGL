package models.laboratory.sample.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.sample.description.dao.ImportCategoryDAO;
import models.utils.dao.AbstractDAO;

/**
 * Category of collaborator informations
 * 
 * @author ejacoby
 *
 */
public class ImportCategory extends AbstractCategory<ImportCategory> {

//	public static Finder<ImportCategory> find = new Finder<ImportCategory>(ImportCategoryDAO.class.getName());
	public static final Finder<ImportCategory,ImportCategoryDAO> find = new Finder<>(ImportCategoryDAO.class);
	
	public ImportCategory() {
		super(ImportCategoryDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<ImportCategory>> daoClass() {
		return ImportCategoryDAO.class;
	}

}
