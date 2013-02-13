package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.MeasureValue;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class MeasureValueDAO extends AbstractDAOMapping<MeasureValue>{

	protected MeasureValueDAO() {
		super("measure_value", MeasureValue.class, MeasureValueMappingQuery.class,
				"SELECT t.id, value, default_value, measure_category_id "+
						"FROM measure_value as t ",true);
	}


	public List<MeasureValue> findByMeasureCategory(long idMeasureCategory)
	{
		String sql = "SELECT id,value,default_value " +
				"FROM measure_value "+
				"WHERE measure_category_id=?";
		BeanPropertyRowMapper<MeasureValue> mapper = new BeanPropertyRowMapper<MeasureValue>(MeasureValue.class);
		return this.jdbcTemplate.query(sql, mapper, idMeasureCategory);
	}

	public MeasureValue findByValue(String value)
	{
		String sql = "SELECT id,value,default_value " +
				"FROM measure_value "+
				"WHERE value=?";
		BeanPropertyRowMapper<MeasureValue> mapper = new BeanPropertyRowMapper<MeasureValue>(MeasureValue.class);
		return this.jdbcTemplate.queryForObject(sql, mapper, value);
	}
	
	public MeasureValue findByCode(String code) throws DAOException
	{
		throw new DAOException("No code field");
	}

	public long add(MeasureValue measureValue)
	{
		//Check if category exist
		if(measureValue.measureCategory!=null && measureValue.measureCategory.id==null)
		{
			MeasureCategoryDAO measureCategoryDAO = Spring.getBeanOfType(MeasureCategoryDAO.class);
			measureValue.measureCategory.id = measureCategoryDAO.add(measureValue.measureCategory);
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("value", measureValue.value);
		parameters.put("default_value", measureValue.defaultValue);
		parameters.put("measure_category_id", measureValue.measureCategory.id);

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		measureValue.id = newId;
		return measureValue.id;
	}


	@Override
	public void update(MeasureValue measureValue) throws DAOException {
		String sql = "UPDATE measure_value SET value=?, default_value=? WHERE id=?";
		jdbcTemplate.update(sql, measureValue.value, measureValue.defaultValue, measureValue.id);
	}

}
