package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureValue;

import org.springframework.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class MeasureValueDAO{

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	private DataSource dataSource;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);      
		jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("measure_value").usingGeneratedKeyColumns("id");
	}
	
	public List<MeasureValue> findByMeasureCategory(long idMeasureCategory)
	{
		String sql = "SELECT id, value, default_value, measure_category_id "+
					"FROM measure_value "+
					"WHERE measure_category_id=?";
		MeasureValueMappingQuery measureValueMappingQuery = new MeasureValueMappingQuery(dataSource, sql,new SqlParameter("measure_category_id", Type.LONG));
		return measureValueMappingQuery.execute(idMeasureCategory);
	}
	
	public MeasureValue add(MeasureValue measureValue)
	{
		//Check if category exist
		if(measureValue.measureCaterory!=null && measureValue.measureCaterory.id==null)
		{
			MeasureCategoryDAO measureCategoryDAO = Spring.getBeanOfType(MeasureCategoryDAO.class);
			MeasureCategory mc = (MeasureCategory) measureCategoryDAO.add(measureValue.measureCaterory);
			measureValue.measureCaterory = mc;
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("value", measureValue.value);
		parameters.put("default_value", measureValue.defaultValue);
		parameters.put("measure_category_id", measureValue.measureCaterory.id);

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


}
