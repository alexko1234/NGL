package models.utils;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

@Repository
public class ExecuteSQLDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		
	}
	
	public void executeScript(Resource resource)
	{
		SimpleJdbcTestUtils.executeSqlScript(jdbcTemplate, resource, true);
	}
}
