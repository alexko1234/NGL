package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Valuation;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.Logger;

@Repository
public class ValuationDAO extends AbstractDAOMapping<Valuation>{
	
	protected ValuationDAO() {
		super("valuation", Valuation.class, ValuationMappingQuery.class,
				"SELECT t.id, t.name, t.code FROM valuation as t ",
				true);
	}
	
	
	@Override
	public void remove(Valuation valuation) throws DAOException {
		String sql = "DELETE FROM valuation_common_info_type WHERE fk_valuation=?";
		jdbcTemplate.update(sql, valuation.id);
		//remove Valuation itself
		super.remove(valuation);
	}
	
	@Override
	public long save(Valuation valuation) throws DAOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", valuation.code);
		parameters.put("name", valuation.name);
		parameters.put("path", valuation.path);

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		valuation.id = newId;
		return valuation.id;
	}

	@Override
	public void update(Valuation valuation) throws DAOException {
		String sql = "UPDATE valuation SET code=?, name=?, path=? WHERE id=?";
		jdbcTemplate.update(sql, valuation.code, valuation.name, valuation.path);
		
	}
	
	public List<Valuation> findByTypeCode(String code) {
		//TODO : DAOHelpers.getSQLForInstitute("c") ?
		String sql = "SELECT v.id, v.code, v.name, v.path "+
				"FROM valuation  as v "+
				"INNER JOIN valuation_common_info_type as vcc ON vcc.fk_valuation=v.id "+
				"INNER JOIN common_info_type as c ON c.id=vcc.fk_common_info_type " +
				"WHERE c.code = ?";
		
		BeanPropertyRowMapper<Valuation> mapper = new BeanPropertyRowMapper<Valuation>(Valuation.class);
		return this.jdbcTemplate.query(sql, mapper, code);
		
	}
	
	
	public List<Valuation> findByCommonInfoType(long idCommonInfoType) {
		String sql = "SELECT v.id, v.name, v.code, v.path "+
				"FROM valuation v "+
				"JOIN valuation_common_info_type vcc ON vcc.fk_valuation= v.id "+
				"WHERE vcc.fk_common_info_type=?";
		BeanPropertyRowMapper<Valuation> mapper = new BeanPropertyRowMapper<Valuation>(Valuation.class);
		return this.jdbcTemplate.query(sql, mapper, idCommonInfoType);
	}
	
	public boolean isCodeExistForTypeCode(String code, String typeCode) throws DAOException {		
		//TODO : DAOHelpers.getSQLForInstitute("c") ?
		String sql =  "SELECT v.id, v.name, v.code, v.path "+
				"FROM valuation v "+
				"INNER JOIN valuation_common_info_type as vcc ON vcc.fk_valuation=v.id "+
				"INNER JOIN common_info_type as c ON c.id=vcc.fk_common_info_type " + 
				"WHERE v.code=? and c.code=?";
		
		return( initializeMapping(sql, new SqlParameter("v.code", Types.VARCHAR),
				 new SqlParameter("c.code", Types.VARCHAR)).findObject(code, typeCode) != null )? true : false;	
	}
	
}