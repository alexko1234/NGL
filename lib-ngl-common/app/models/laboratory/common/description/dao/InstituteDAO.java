package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class InstituteDAO extends AbstractDAOMapping<Institute>{

	protected InstituteDAO() {
		super("institute", Institute.class, InstituteMappingQuery.class,
				"SELECT t.id, t.name, t.code FROM institute as t ",
				true);
	}
	
	@Override
	public void remove(Institute institute) throws DAOException {
		//Remove list institute for common_info_type
		String sql = "DELETE FROM common_info_type_institute WHERE fk_institute=?";
		jdbcTemplate.update(sql, institute.id);
		
		sql = "DELETE FROM instrument_institute WHERE fk_institute=?";
		jdbcTemplate.update(sql, institute.id);
		
		sql = "DELETE FROM resolution_institute WHERE fk_institute=?";
		jdbcTemplate.update(sql, institute.id);
		
		sql = "DELETE FROM valuation_criteria_institute WHERE fk_institute=?";
		jdbcTemplate.update(sql, institute.id);
			
		//remove institute itself
		super.remove(institute);
	}

	@Override
	public long save(Institute institute) throws DAOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", institute.code);
		parameters.put("name", institute.name);

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		institute.id = newId;
		return institute.id;
	}

	@Override
	public void update(Institute institute) throws DAOException {
		String sql = "UPDATE institute SET code=?, name=? WHERE id=?";
		jdbcTemplate.update(sql, institute.code, institute.name);
		
	}
	
	public List<Institute> findByCommonInfoType(long idCommonInfoType) {
		String sql = "SELECT i.id, i.name, i.code "+
				"FROM institute i "+
				"JOIN common_info_type_institute ci ON ci.fk_institute= i.id "+
				"WHERE ci.fk_common_info_type=?";
		BeanPropertyRowMapper<Institute> mapper = new BeanPropertyRowMapper<Institute>(Institute.class);
		return this.jdbcTemplate.query(sql, mapper, idCommonInfoType);
	}


}

