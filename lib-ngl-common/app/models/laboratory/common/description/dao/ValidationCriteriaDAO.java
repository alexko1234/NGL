package models.laboratory.common.description.dao;

import java.util.List;

import models.laboratory.common.description.ValidationCriteria;
import models.utils.dao.AbstractDAODefault;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ValidationCriteriaDAO extends AbstractDAODefault<ValidationCriteria>{

	protected ValidationCriteriaDAO() {
		super("validation_criteria", ValidationCriteria.class, true);
	}
	
	
	private void removeValidationCriteriaReadSet(Long id) {
		String sql = "DELETE FROM validation_criteria_readset WHERE fk_validation_criteria=?";
		jdbcTemplate.update(sql, id);
	}
	
	private void removeValidationCriteriaRun(Long id) {
		String sql = "DELETE FROM validation_criteria_run WHERE fk_validation_criteria=?";
		jdbcTemplate.update(sql, id);
	}
	
	@Override
	public void remove(ValidationCriteria validationCriteria) throws DAOException {
		if(null == validationCriteria){
			throw new IllegalArgumentException("validationCriteria is null");
		}
		//to group in an unique method ?
		removeValidationCriteriaReadSet(validationCriteria.id);
		removeValidationCriteriaRun(validationCriteria.id);
		//Remove validationCriteria itself
		super.remove(validationCriteria);
	}
	
	public List<ValidationCriteria> findByRunCode(String code) {
		
		String sql = "SELECT vc.id, vc.code, vc.name, vc.path "+
				"FROM validation_criteria  as vc "+
				"INNER JOIN validation_criteria_run as vcr ON vcr.fk_validation_criteria=vc.id " +
				"INNER JOIN common_info_type as c ON c.id=vcr.fk_run " +
				"WHERE c.code = ?";
		
		BeanPropertyRowMapper<ValidationCriteria> mapper = new BeanPropertyRowMapper<ValidationCriteria>(ValidationCriteria.class);
		return this.jdbcTemplate.query(sql, mapper, code);
		
	}
	
	public List<ValidationCriteria> findByReadSetCode(String code) {
		
		String sql = "SELECT vc.id, vc.code, vc.name, vc.path "+
				"FROM validation_criteria  as vc "+
				"INNER JOIN validation_criteria_readset as vcr ON vcr.fk_validation_criteria=vc.id " +
				"INNER JOIN common_info_type as c ON c.id=vcr.fk_run " +
				"WHERE c.code = ?";
		
		BeanPropertyRowMapper<ValidationCriteria> mapper = new BeanPropertyRowMapper<ValidationCriteria>(ValidationCriteria.class);
		return this.jdbcTemplate.query(sql, mapper, code);
		
	}

	public List<ValidationCriteria> findByTypeCode(String code) {
		
		String sql = "SELECT vc.id, vc.code, vc.name, vc.path "+
				"FROM validation_criteria  as vc "+
				"INNER JOIN validation_criteria_run as vcr ON vcr.fk_validation_criteria=vc.id "+
				"INNER JOIN common_info_type as c ON c.id=vcr.fk_run_type "+
				"WHERE c.code = ? "+
				"UNION ALL "+
				"SELECT vc.id, vc.code, vc.name, vc.path "+
				"FROM validation_criteria  as vc "+
				"INNER JOIN validation_criteria_readset as vcr ON vcr.fk_validation_criteria=vc.id "+
				"INNER JOIN common_info_type as c ON c.id=vcr.fk_readset_type "+
				"WHERE c.code = ?";
		
		BeanPropertyRowMapper<ValidationCriteria> mapper = new BeanPropertyRowMapper<ValidationCriteria>(ValidationCriteria.class);
		return this.jdbcTemplate.query(sql, mapper, code, code);
		
	}
	
	
	
	
	
	
	
}