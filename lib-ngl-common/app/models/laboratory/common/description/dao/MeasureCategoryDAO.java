package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.MeasureCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MeasureCategoryDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
		
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);       
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("measure_category").usingGeneratedKeyColumns("id");
	}
	
	public MeasureCategory add(MeasureCategory measureCategory)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", measureCategory.name);
        parameters.put("code", measureCategory.code);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        measureCategory.id = newId;
		return measureCategory;
	}
	
	
	public void update(MeasureCategory measureCategory)
	{
		String sql = "UPDATE measure_category SET name=? WHERE id=?";
		jdbcTemplate.update(sql, measureCategory.name, measureCategory.id);
	}
}
