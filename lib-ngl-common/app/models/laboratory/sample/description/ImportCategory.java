package models.laboratory.sample.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.sample.description.dao.ImportCategoryDAO;

/**
 * Category of collaborator informations
 * @author ejacoby
 *
 */
public class ImportCategory extends AbstractCategory{

	public static Finder<ImportCategory> find = new Finder<ImportCategory>(ImportCategoryDAO.class.getName());
	
	public ImportCategory() {
		super(ImportCategoryDAO.class.getName());
	}

}
