package models.laboratory.processes.description.dao;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.processes.description.ProcessCategory;
import models.utils.dao.AbstractDAODefault;
import models.utils.dao.DAOException;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import play.Logger;

@Repository
public class ProcessCategoryDAO extends AbstractDAODefault<ProcessCategory>{

	public ProcessCategoryDAO() {
		super("process_category",ProcessCategory.class,true);
	}

	//overload to delete "order by" in sql query
	public List<ProcessCategory> findAll() throws DAOException
	{
		try {
			String sql = getSqlCommon();
			//Logger.debug(sql);
			BeanPropertyRowMapper<ProcessCategory> mapper = new BeanPropertyRowMapper<ProcessCategory>(entityClass);
			return this.jdbcTemplate.query(sql, mapper);
		} catch (DataAccessException e) {
			return new ArrayList<ProcessCategory>();
		}
	}
}
