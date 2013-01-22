package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.MeasureValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MeasureValueDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
		
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);       
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("measure_value").usingGeneratedKeyColumns("id");
	}
	
	public MeasureValue add(MeasureValue measureValue, long idMeasureCategory)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("value", measureValue.getValue());
        parameters.put("default_value", measureValue.getDefaultValue());
        parameters.put("measure_category_id", idMeasureCategory);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        measureValue.setId(newId);
		return measureValue;
	}
	
	public void update(MeasureValue measureValue)
	{
		String sql = "UPDATE measure_value SET value=?, default_value=? WHERE id=?";
		jdbcTemplate.update(sql, measureValue.getValue(), measureValue.getDefaultValue(), measureValue.getId());
	}
}
