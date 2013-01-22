package models.laboratory.experiment.description.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.experiment.description.ProtocolCategory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProtocolCategoryDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);      
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("protocol_category").usingGeneratedKeyColumns("id");
	}
	
	public ProtocolCategory findById(long id)
	{
		String sql = "SELECT id,name,code " +
				"FROM protocol_category "+
				"WHERE id=?";
		BeanPropertyRowMapper<ProtocolCategory> mapper = new BeanPropertyRowMapper<ProtocolCategory>(ProtocolCategory.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, id);
	}
	
	public ProtocolCategory add(ProtocolCategory protocolCategory)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", protocolCategory.getName());
        parameters.put("code", protocolCategory.getCode());
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        protocolCategory.setId(newId);
        return protocolCategory;
	}
}
