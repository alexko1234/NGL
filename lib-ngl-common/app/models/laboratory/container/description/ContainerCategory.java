package models.laboratory.container.description;

import java.util.List;

import play.api.modules.spring.Spring;
import models.laboratory.common.description.AbstractCategory;
import models.laboratory.container.description.dao.ContainerCategoryDAO;
import models.laboratory.processes.description.dao.ProcessTypeDAO;
import models.utils.ListObject;
import models.utils.dao.DAOException;

public class ContainerCategory extends AbstractCategory{

	public static Finder<ContainerCategory> find = new Finder<ContainerCategory>(ContainerCategoryDAO.class.getName()); 
	
	public ContainerCategory() {
		super(ContainerCategoryDAO.class.getName());
	}

	
	/**
	 * Return a list of ListObject that help populating the <select> input
	 * @return List<ListObject>
	 */
	public static List<ListObject> findAllForList() throws DAOException{
		ContainerCategoryDAO containerCategoryDAO = Spring.getBeanOfType(ContainerCategoryDAO.class);
		return containerCategoryDAO.findAllForList();
	}
}
