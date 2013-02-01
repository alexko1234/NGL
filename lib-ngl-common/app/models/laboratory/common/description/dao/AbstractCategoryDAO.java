package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.AbstractCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Generic DAO for entity category
 * Defining the basic operations for simple category ie which have no relation with another object
 * @author ejacoby
 *
 */
public abstract class AbstractCategoryDAO<P extends AbstractCategory> {

	public SimpleJdbcTemplate jdbcTemplate;
	protected SimpleJdbcInsert jdbcInsert;
	protected String tableName;
	protected Class<P> categoryClass;
	
	public AbstractCategoryDAO(String tableName,Class<P> classCategory) {
		super();
		this.tableName=tableName;
		this.categoryClass=classCategory;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);       
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName).usingGeneratedKeyColumns("id");
	}
	
	public P findById(long id)
	{
		String sql = "SELECT id,name,code " +
				"FROM "+tableName+" "+
				"WHERE id=?";
		BeanPropertyRowMapper<P> mapper = new BeanPropertyRowMapper<P>(categoryClass);
		P category = this.jdbcTemplate.queryForObject(sql, mapper, id);
		return category;
	}
	
	public P findByCode(String code)
	{
		String sql = "SELECT id,name,code " +
				"FROM "+tableName+" "+
				"WHERE code=?";
		BeanPropertyRowMapper<P> mapper = new BeanPropertyRowMapper<P>(categoryClass);
		P category = this.jdbcTemplate.queryForObject(sql, mapper, code);
		return category;
	}
	
	public P add(P category)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", category.name);
        parameters.put("code", category.code);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        category.id = newId;
		return category;
	}
	
	
	public void update(P category)
	{
		String sql = "UPDATE "+tableName+" SET name=? WHERE id=?";
		jdbcTemplate.update(sql, category.name, category.id);
	}
}
