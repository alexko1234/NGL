package models.laboratory.container.description.dao;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import models.laboratory.container.description.ContainerCategory;
import models.utils.ListObject;
import models.utils.dao.AbstractDAO;

@Repository
public class ContainerCategoryDAO extends AbstractDAO<ContainerCategory>{

	protected ContainerCategoryDAO() {
		super("container_category", ContainerCategory.class, true);
	}
	
	/**
	 * Return a list of ListObject that help populating the <select> input
	 * @return List<ListObject>
	 */
	public List<ListObject> findAllForList(){
		String sql = "SELECT code, name "+
				"FROM container_category ";
		
		BeanPropertyRowMapper<ListObject> mapper = new BeanPropertyRowMapper<ListObject>(ListObject.class);
		return this.jdbcTemplate.query(sql, mapper);
	}
}
