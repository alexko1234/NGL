package models.description.experiment.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.description.experiment.ProcessCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProcessCategoryDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);      
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("process_category").usingGeneratedKeyColumns("id");
	}
	
	public ProcessCategory findById(long id)
	{
		String sql = "SELECT id,name,code " +
				"FROM process_category "+
				"WHERE id=?";
		BeanPropertyRowMapper<ProcessCategory> mapper = new BeanPropertyRowMapper<ProcessCategory>(ProcessCategory.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, id);
	}
	
	public ProcessCategory add(ProcessCategory processCategory)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", processCategory.getName());
        parameters.put("code", processCategory.getCode());
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        processCategory.setId(newId);
        return processCategory;
	}
}
