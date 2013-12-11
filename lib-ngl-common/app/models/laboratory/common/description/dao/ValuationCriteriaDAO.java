package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.ValuationCriteria;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.Logger;

@Repository
public class ValuationCriteriaDAO extends AbstractDAOMapping<ValuationCriteria>{
	
	protected ValuationCriteriaDAO() {
		super("valuation_criteria", ValuationCriteria.class, ValuationCriteriaMappingQuery.class,
				"SELECT t.id, t.name, t.code FROM valuation_criteria as t ",
				true);
	}
	
	
	@Override
	public void remove(ValuationCriteria valuationCriteria) throws DAOException {
		String sql = "DELETE FROM valuation_criteria_common_info_type WHERE fk_valuation_criteria=?";
		jdbcTemplate.update(sql, valuationCriteria.id);
		//remove ValuationCriteria itself
		super.remove(valuationCriteria);
	}
	
	@Override
	public long save(ValuationCriteria valuationCriteria) throws DAOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", valuationCriteria.code);
		parameters.put("name", valuationCriteria.name);
		parameters.put("path", valuationCriteria.path);

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		valuationCriteria.id = newId;
		return valuationCriteria.id;
	}

	@Override
	public void update(ValuationCriteria valuationCriteria) throws DAOException {
		String sql = "UPDATE valuation_criteria SET code=?, name=?, path=? WHERE id=?";
		jdbcTemplate.update(sql, valuationCriteria.code, valuationCriteria.name, valuationCriteria.path);
		
	}
	
	public List<ValuationCriteria> findByTypeCode(String code) {
		//TODO : DAOHelpers.getSQLForInstitute("c") ?
		String sql = "SELECT v.id, v.code, v.name, v.path "+
				"FROM valuation_criteria  as v "+
				"INNER JOIN valuation_criteria_common_info_type as vcc ON vcc.fk_valuation_criteria=v.id "+
				"INNER JOIN common_info_type as c ON c.id=vcc.fk_common_info_type " +
				"WHERE c.code = ?";
		
		BeanPropertyRowMapper<ValuationCriteria> mapper = new BeanPropertyRowMapper<ValuationCriteria>(ValuationCriteria.class);
		return this.jdbcTemplate.query(sql, mapper, code);
		
	}
	
	
	public List<ValuationCriteria> findByCommonInfoType(long idCommonInfoType) {
		String sql = "SELECT v.id, v.name, v.code, v.path "+
				"FROM valuation_criteria v "+
				"JOIN valuation_criteria_common_info_type vcc ON vcc.fk_valuation_criteria= v.id "+
				"WHERE vcc.fk_common_info_type=?";
		BeanPropertyRowMapper<ValuationCriteria> mapper = new BeanPropertyRowMapper<ValuationCriteria>(ValuationCriteria.class);
		return this.jdbcTemplate.query(sql, mapper, idCommonInfoType);
	}
	
	public boolean isCodeExistForTypeCode(String code, String typeCode) throws DAOException {		
		//TODO : DAOHelpers.getSQLForInstitute("c") ?
		String sql =  "SELECT v.id, v.name, v.code, v.path "+
				"FROM valuation_criteria v "+
				"INNER JOIN valuation_criteria_common_info_type as vcc ON vcc.fk_valuation_criteria=v.id "+
				"INNER JOIN common_info_type as c ON c.id=vcc.fk_common_info_type " + 
				"WHERE v.code=? and c.code=?";
		
		return( initializeMapping(sql, new SqlParameter("v.code", Types.VARCHAR),
				 new SqlParameter("c.code", Types.VARCHAR)).findObject(code, typeCode) != null )? true : false;	
	}
	
}