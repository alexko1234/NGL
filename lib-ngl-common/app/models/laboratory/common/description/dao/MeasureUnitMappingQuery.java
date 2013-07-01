package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class MeasureUnitMappingQuery extends MappingSqlQuery<MeasureUnit>{

	public MeasureUnitMappingQuery()
	{
		super();
	}
	public MeasureUnitMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected MeasureUnit mapRow(ResultSet rs, int rowNum) throws SQLException {
		MeasureUnit measureValue = new MeasureUnit();
		measureValue.id=rs.getLong("id");
		measureValue.value=rs.getString("value");
		measureValue.defaultUnit=rs.getBoolean("default_unit");
		measureValue.code=rs.getString("code");
		long idCategory = rs.getLong("fk_measure_category");
		
		MeasureCategoryDAO measureCategoryDAO = Spring.getBeanOfType(MeasureCategoryDAO.class);
		MeasureCategory measureCategory=null;
		try {
			measureCategory = measureCategoryDAO.findById(idCategory);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		measureValue.category=measureCategory;
		return measureValue;
	}

}
