package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import models.laboratory.common.description.MeasureValue;
import models.utils.AbstractDAO;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MeasureValueDAO extends AbstractDAO<Long, MeasureValue>{

	public MeasureValueDAO() {
		super("measure_value");
	}

	public MeasureValue add(MeasureValue measureValue)
	{
		/*
		Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("value", measureValue.value);
        parameters.put("default_value", measureValue.defaultValue);
        parameters.put("measure_category_id", idMeasureCategory);
        */
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(measureValue);
        Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
        measureValue.id = newId;
		return measureValue;
	}
	
	public MeasureValue update(MeasureValue measureValue)
	{
		String sql = "UPDATE measure_value SET value=?, default_value=? WHERE id=?";
		jdbcTemplate.update(sql, measureValue.value, measureValue.defaultValue, measureValue.id);
		return measureValue;
	}


	@Override
	public List<MeasureValue> findAll() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public MeasureValue findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}


	
	@Override @Transactional
	public void remove(MeasureValue value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MeasureValue findByCode(String code) {
		// TODO Auto-generated method stub
		return null;
	}
}
