package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.Resolution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ResolutionDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);       
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("resolution").usingGeneratedKeyColumns("id");
	}
	
	public List<Resolution> findByCommonInfoType(long idCommonInfoType)
	{
		String sql = "SELECT id,name,code "+
					"FROM resolution "+
					"JOIN common_info_type_resolution ON fk_resolution=id "+
					"WHERE fk_common_info_type=?";
		BeanPropertyRowMapper<Resolution> mapper = new BeanPropertyRowMapper<Resolution>(Resolution.class);
		return this.jdbcTemplate.query(sql, mapper, idCommonInfoType);
	}
	
	public Resolution add(Resolution resolution)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", resolution.getName());
        parameters.put("code", resolution.getCode());
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        resolution.setId(newId);
        return resolution;
	}
	
	public void update(Resolution resolution)
	{
		String sql = "UPDATE resolution SET name=? WHERE id=?";
		jdbcTemplate.update(sql, resolution.getName(), resolution.getId());
	}
}
