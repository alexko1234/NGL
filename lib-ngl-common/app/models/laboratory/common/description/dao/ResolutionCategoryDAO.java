package models.laboratory.common.description.dao;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.ResolutionCategory;
import models.utils.dao.AbstractDAODefault;
import models.utils.dao.DAOException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ResolutionCategoryDAO extends AbstractDAODefault<ResolutionCategory>{

	protected ResolutionCategoryDAO() {
		super("resolution_category", ResolutionCategory.class, true);
	}
	
	//override method for the order by clause
	public List<ResolutionCategory> findAll() throws DAOException {
		try {
			String sql = getSqlCommon()+" ORDER by t.display_order";
			BeanPropertyRowMapper<ResolutionCategory> mapper = new BeanPropertyRowMapper<ResolutionCategory>(entityClass);
			return this.jdbcTemplate.query(sql, mapper);
		} catch (DataAccessException e) {
			return new ArrayList<ResolutionCategory>();
		}
	}

	

	
}
