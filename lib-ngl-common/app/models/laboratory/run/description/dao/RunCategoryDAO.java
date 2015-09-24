package models.laboratory.run.description.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.run.description.RunCategory;
import models.utils.dao.AbstractDAODefault;
import models.utils.dao.DAOException;

@Repository
public class RunCategoryDAO extends AbstractDAODefault<RunCategory>{

	public RunCategoryDAO() {
		super("run_category",RunCategory.class,true);
	}

	public RunCategory findByTypeCode(String typeCode) throws DAOException {		
		String sql = getSqlCommon()+" inner join run_type r on r.fk_run_category = t.id where r.code = ?";
		BeanPropertyRowMapper<RunCategory> mapper = new BeanPropertyRowMapper<RunCategory>(entityClass);
		return this.jdbcTemplate.queryForObject(sql, mapper, typeCode);		
	}
}



