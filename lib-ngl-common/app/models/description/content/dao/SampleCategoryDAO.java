package models.description.content.dao;

import javax.sql.DataSource;

import models.description.content.SampleCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SampleCategoryDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
	public SampleCategory findById(long id)
	{
		String sql = "SELECT id,name,code " +
				"FROM sample_category "+
				"WHERE id=?";
		BeanPropertyRowMapper<SampleCategory> mapper = new BeanPropertyRowMapper<SampleCategory>(SampleCategory.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, id);
	}
}
