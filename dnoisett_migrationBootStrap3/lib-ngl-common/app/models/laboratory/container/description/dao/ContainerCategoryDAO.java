package models.laboratory.container.description.dao;

import models.laboratory.container.description.ContainerCategory;
import models.utils.dao.AbstractDAODefault;

import org.springframework.stereotype.Repository;

@Repository
public class ContainerCategoryDAO extends AbstractDAODefault<ContainerCategory>{

	protected ContainerCategoryDAO() {
		super("container_category", ContainerCategory.class, true);
	}
	
}
