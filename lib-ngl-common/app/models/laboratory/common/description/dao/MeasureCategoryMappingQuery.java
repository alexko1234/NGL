package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureValue;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

public class MeasureCategoryMappingQuery extends MappingSqlQuery<MeasureCategory>{

	public MeasureCategoryMappingQuery()
	{
		super();
	}
	public MeasureCategoryMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	@Override
	protected MeasureCategory mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		MeasureCategory measureCategory = new MeasureCategory();
		measureCategory.id=rs.getLong("id");
		measureCategory.name=rs.getString("name");
		measureCategory.code=rs.getString("code");
		MeasureValueDAO measureValueDAO = Spring.getBeanOfType(MeasureValueDAO.class);
		List<MeasureValue> measureValues = measureValueDAO.findByMeasureCategory(measureCategory.id);
		measureCategory.measurePossibleValues=measureValues;		
		return measureCategory;
	}
}
