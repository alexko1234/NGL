package models.description.project.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.description.project.ProjectCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectCategoryDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);      
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("project_category").usingGeneratedKeyColumns("id");
	}
	
	public ProjectCategory findById(long id)
	{
		String sql = "SELECT id,name,code " +
				"FROM project_category "+
				"WHERE id=?";
		BeanPropertyRowMapper<ProjectCategory> mapper = new BeanPropertyRowMapper<ProjectCategory>(ProjectCategory.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, id);
	}
	
	public ProjectCategory add(ProjectCategory projectCategory)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", projectCategory.getName());
        parameters.put("code", projectCategory.getCode());
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        projectCategory.setId(newId);
        return projectCategory;
	}
}
