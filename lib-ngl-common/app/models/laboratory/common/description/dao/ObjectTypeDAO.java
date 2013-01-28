package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.ObjectType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

@Repository
public class ObjectTypeDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);   
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("object_type").usingGeneratedKeyColumns("id");
	}

	public ObjectType find(String type)
	{
		String sql = "SELECT id,generic,type FROM object_type WHERE type=?";
		BeanPropertyRowMapper<ObjectType> mapper = ParameterizedBeanPropertyRowMapper.newInstance(ObjectType.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, type);
	}
	
	public List<ObjectType> findAll()
	{
		String sql = "SELECT id, generic, type FROM object_type ORDER by type";
		BeanPropertyRowMapper<ObjectType> mapper = ParameterizedBeanPropertyRowMapper.newInstance(ObjectType.class);
		return this.jdbcTemplate.query(sql, mapper);
	}
	
	public ObjectType findById(long id)
	{
		String sql = "SELECT id, generic, type FROM object_type WHERE id=?";
		BeanPropertyRowMapper<ObjectType> mapper =  ParameterizedBeanPropertyRowMapper.newInstance(ObjectType.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, id);
	}
	
	public ObjectType add(ObjectType objectType)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("type", objectType.type);
        parameters.put("generic", objectType.generic);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        objectType.id = newId;
        return objectType;
	}
	
	public void update(ObjectType objectType)
	{
		String sql = "UPDATE object_type SET type=?, generic=? WHERE id=?";
		jdbcTemplate.update(sql, objectType.type, objectType.generic, objectType.id);
	}

}
