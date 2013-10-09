package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Value;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ValueDAO extends AbstractDAO<Value>{

	protected ValueDAO() {
		super("value", Value.class,true);
	}

	public List<Value> findByPropertyDefinition(long idPropertyDefinition)
	{
		String sql = "SELECT id, value, default_value FROM value WHERE fk_property_definition=?";
		BeanPropertyRowMapper<Value> mapper = new BeanPropertyRowMapper<Value>(Value.class);
		return this.jdbcTemplate.query(sql, mapper, idPropertyDefinition);
	}

	public Value save(Value value, long idPropertyDefinition)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("value", value.value);
        parameters.put("default_value", value.defaultValue);
        parameters.put("fk_property_definition", idPropertyDefinition);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        value.id = newId;
		return value;
	}

	public void update(Value value, long idPropertyDefinition)
	{
		String sql = "UPDATE value SET value=?, default_value=? WHERE id=? AND fk_property_definition=?";
		jdbcTemplate.update(sql,value.code, value.value, value.defaultValue, value.id, idPropertyDefinition);
	}

	@Override
	public Value findByCode(String code) throws DAOException {
		throw new UnsupportedOperationException("Value does not have a code");
		
	}


}
