package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Value;
import models.utils.dao.AbstractDAO;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ValueDAO extends AbstractDAO<Value>{

	protected ValueDAO() {
		super("value", Value.class,true);
	}
	
	public List<Value> findByPropertyDefinition(long idPropertyDefinition)
	{
		String sql = "SELECT id, value, default_value FROM value WHERE property_definition_id=?";
		BeanPropertyRowMapper<Value> mapper = new BeanPropertyRowMapper<Value>(Value.class);
		return this.jdbcTemplate.query(sql, mapper, idPropertyDefinition);
	}
	
	public Value add(Value value, long idPropertyDefinition)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("value", value.value);
        parameters.put("default_value", value.defaultValue);
        parameters.put("property_definition_id", idPropertyDefinition);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        value.id = newId;
		return value;
	}
	
	public void update(Value value, long idPropertyDefinition)
	{
		String sql = "UPDATE value SET value=?, default_value=? WHERE id=? AND property_definition_id=?";
		jdbcTemplate.update(sql,value.value, value.defaultValue, value.id, idPropertyDefinition);
	}
}
