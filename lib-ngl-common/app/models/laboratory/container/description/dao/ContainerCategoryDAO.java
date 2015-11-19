package models.laboratory.container.description.dao;

import models.laboratory.container.description.ContainerCategory;
import models.utils.dao.AbstractDAODefault;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ContainerCategoryDAO extends AbstractDAODefault<ContainerCategory>{

	protected ContainerCategoryDAO() {
		super("container_category", ContainerCategory.class, true);
	}
	
	public ContainerCategory findByContainerSupportCategoryCode(String containerSupportCategoryCode) {
		if(null == containerSupportCategoryCode){
			throw new DAOException("containerSupportCategoryCode is mandatory");
		}
		String sql = sqlCommon+" inner join container_support_category as c ON c.fk_container_category=t.id" +
				" WHERE c.code=? order by t.name";
		BeanPropertyRowMapper<ContainerCategory> mapper = new BeanPropertyRowMapper<ContainerCategory>(entityClass);
		return this.jdbcTemplate.queryForObject(sql, mapper, containerSupportCategoryCode);
	}
	
}
