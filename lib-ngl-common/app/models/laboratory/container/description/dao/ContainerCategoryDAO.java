package models.laboratory.container.description.dao;

import org.springframework.stereotype.Repository;

import models.laboratory.container.description.ContainerCategory;
import models.utils.dao.AbstractDAO;

@Repository
public class ContainerCategoryDAO extends AbstractDAO<ContainerCategory>{

	protected ContainerCategoryDAO() {
		super("container_category", ContainerCategory.class, true);
	}

}
