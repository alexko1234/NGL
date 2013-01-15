package models.description.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.description.common.ObjectType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;


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
		BeanPropertyRowMapper<ObjectType> mapper = new BeanPropertyRowMapper<ObjectType>(ObjectType.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, type);
	}
	
	public List<ObjectType> findAll()
	{
		String sql = "SELECT id, generic, type FROM object_type ORDER by type";
		BeanPropertyRowMapper<ObjectType> mapper = new BeanPropertyRowMapper<ObjectType>(ObjectType.class);
		return this.jdbcTemplate.query(sql, mapper);
	}
	
	public ObjectType findById(long id)
	{
		String sql = "SELECT id, generic, type FROM object_type WHERE id=?";
		BeanPropertyRowMapper<ObjectType> mapper = new BeanPropertyRowMapper<ObjectType>(ObjectType.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, id);
	}
	
	public ObjectType add(ObjectType objectType)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("type", objectType.getType());
        parameters.put("generic", objectType.getGeneric());
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        objectType.setId(newId);
        return objectType;
	}
	
	public void update(ObjectType objectType)
	{
		String sql = "UPDATE object_type SET type=?, generic=? WHERE id=?";
		jdbcTemplate.update(sql, objectType.getType(), objectType.getGeneric(), objectType.getId());
	}

}
