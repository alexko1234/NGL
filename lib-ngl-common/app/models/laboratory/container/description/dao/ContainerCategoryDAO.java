package models.laboratory.container.description.dao;

import models.laboratory.container.description.ContainerCategory;
import models.utils.dao.AbstractDAO;

public class ContainerCategoryDAO extends AbstractDAO<ContainerCategory>{

	protected ContainerCategoryDAO() {
		super("container_category", ContainerCategory.class, true);
	}

}
