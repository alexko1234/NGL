package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.Value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ValueDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
		
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);       
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("value").usingGeneratedKeyColumns("id");
	}
	
	public List<Value> findByPropertyDefinition(long idPropertyDefinition)
	{
		String sql = "SELECT id, value, default_value FROM value WHERE property_definition_id=?";
		BeanPropertyRowMapper<Value> mapper = new BeanPropertyRowMapper<Value>(Value.class);
		return this.jdbcTemplate.query(sql, mapper, idPropertyDefinition);
	}
	public Value add(Value value)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("value", value.value);
        parameters.put("default_value", value.defaultValue);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        value.id = newId;
		return value;
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
	
	public void update(Value value)
	{
		String sql = "UPDATE value SET value=?, default_value=? WHERE id=?";
		jdbcTemplate.update(sql,value.value, value.defaultValue, value.id);
	}
}
