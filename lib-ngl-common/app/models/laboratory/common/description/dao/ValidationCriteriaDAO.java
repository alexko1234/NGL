package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.ValidationCriteria;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.Logger;

@Repository
public class ValidationCriteriaDAO extends AbstractDAOMapping<ValidationCriteria>{
	
	protected ValidationCriteriaDAO() {
		super("validation_criteria", ValidationCriteria.class, ValidationCriteriaMappingQuery.class,
				"SELECT t.id, t.name, t.code FROM validation_criteria as t ",
				true);
	}
	
	
	@Override
	public void remove(ValidationCriteria validationCriteria) throws DAOException {
		//Remove list institute for common_info_type
		String sql = "DELETE FROM validation_criteria_common_info_type WHERE fk_validation_criteria=?";
		jdbcTemplate.update(sql, validationCriteria.id);
		//remove validationCriteria itself
		super.remove(validationCriteria);
	}
	
	@Override
	public long save(ValidationCriteria validationCriteria) throws DAOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", validationCriteria.code);
		parameters.put("name", validationCriteria.name);
		parameters.put("path", validationCriteria.path);

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		validationCriteria.id = newId;
		return validationCriteria.id;
	}

	@Override
	public void update(ValidationCriteria validationCriteria) throws DAOException {
		String sql = "UPDATE validation_criteria SET code=?, name=?, path=? WHERE id=?";
		jdbcTemplate.update(sql, validationCriteria.code, validationCriteria.name, validationCriteria.path);
		
	}
	
	public List<ValidationCriteria> findByRunCode(String code) {
		
		String sql = "SELECT vc.id, vc.code, vc.name, vc.path "+
				"FROM validation_criteria  as vc "+
				"INNER JOIN validation_criteria_common_info_type as vcc ON vcc.fk_validation_criteria=vc.id " +
				"INNER JOIN common_info_type as c ON c.id=vcc.fk_common_info_type " +
				"INNER JOIN run_type as r ON r.fk_common_info_type=c.id " +
				"WHERE c.code = ?";
		
		BeanPropertyRowMapper<ValidationCriteria> mapper = new BeanPropertyRowMapper<ValidationCriteria>(ValidationCriteria.class);
		return this.jdbcTemplate.query(sql, mapper, code);
		
	}
	
	public List<ValidationCriteria> findByReadSetCode(String code) {
		
		String sql = "SELECT vc.id, vc.code, vc.name, vc.path "+
				"FROM validation_criteria  as vc "+
				"INNER JOIN validation_criteria_common_info_type as vcc ON vcc.fk_validation_criteria=vc.id " +
				"INNER JOIN common_info_type as c ON c.id=vcc.fk_common_info_type " +
				"INNER JOIN readset_type as r ON r.fk_common_info_type=c.id " +
				"WHERE c.code = ?";
		
		BeanPropertyRowMapper<ValidationCriteria> mapper = new BeanPropertyRowMapper<ValidationCriteria>(ValidationCriteria.class);
		return this.jdbcTemplate.query(sql, mapper, code);
		
	}

	public List<ValidationCriteria> findByTypeCode(String code) {
		
		String sql = "SELECT vc.id, vc.code, vc.name, vc.path "+
				"FROM validation_criteria  as vc "+
				"INNER JOIN validation_criteria_common_info_type as vcc ON vcc.fk_validation_criteria=vc.id "+
				"INNER JOIN common_info_type as c ON c.id=vcc.fk_common_info_type " +
				"WHERE c.code = ?";
		
		BeanPropertyRowMapper<ValidationCriteria> mapper = new BeanPropertyRowMapper<ValidationCriteria>(ValidationCriteria.class);
		return this.jdbcTemplate.query(sql, mapper, code, code);
		
	}
	
	
	public List<ValidationCriteria> findByCommonInfoType(long idCommonInfoType) {
		String sql = "SELECT vc.id, vc.name, vc.code, vc.path "+
				"FROM validation_criteria vc "+
				"JOIN validation_criteria_common_info_type vcc ON vcc.fk_validation_criteria= vc.id "+
				"WHERE vcc.fk_common_info_type=?";
		BeanPropertyRowMapper<ValidationCriteria> mapper = new BeanPropertyRowMapper<ValidationCriteria>(ValidationCriteria.class);
		return this.jdbcTemplate.query(sql, mapper, idCommonInfoType);
	}
	
	public boolean isCodeExistForTypeCode(String code, String typeCode) throws DAOException {		
		//TODO : DAOHelpers.getSQLForInstitute("c") ?
		String sql =  "SELECT vc.id, vc.name, vc.code, vc.path "+
				"FROM validation_criteria vc "+
				"INNER JOIN validation_criteria_common_info_type as vcc ON vcc.fk_validation_criteria=vc.id "+
				"INNER JOIN common_info_type as c ON c.id=vcc.fk_common_info_type " + 
				"WHERE vc.code=? and c.code=?";
		
		return( initializeMapping(sql, new SqlParameter("vc.code", Types.VARCHAR),
				 new SqlParameter("c.code", Types.VARCHAR)).findObject(code, typeCode) != null )? true : false;	
	}
	
}