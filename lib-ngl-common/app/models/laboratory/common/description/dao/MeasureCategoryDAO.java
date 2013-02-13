package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureValue;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class MeasureCategoryDAO extends AbstractDAOMapping<MeasureCategory>{

	

	protected MeasureCategoryDAO() {
		super("measure_category", MeasureCategory.class, MeasureCategoryMappingQuery.class, 
				"SELECT t.id, name, code "+
				"FROM measure_category as t ", true);
	}
	
	public long add(MeasureCategory measureCategory)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", measureCategory.name);
		parameters.put("code", measureCategory.code);
		
		measureCategory.id = (Long) jdbcInsert.executeAndReturnKey(parameters);
		//measureCategory = (MeasureCategory) super.add(measureCategory);
		//Add measureValue
		if(measureCategory.measurePossibleValues!=null && measureCategory.measurePossibleValues.size()>0){
			MeasureValueDAO measureValueDAO = Spring.getBeanOfType(MeasureValueDAO.class);
			for(MeasureValue measureValue : measureCategory.measurePossibleValues){
				measureValue.measureCategory=measureCategory;
				measureValueDAO.add(measureValue);
			}
		}
        return measureCategory.id;
	}
	
	public void remove(MeasureCategory measureCategory)
	{
		//remove measure values
		MeasureValueDAO measureValueDAO = Spring.getBeanOfType(MeasureValueDAO.class);
		for(MeasureValue measureValue : measureCategory.measurePossibleValues){
			measureValueDAO.remove(measureValue);
		}
		//remove measure category
		super.remove(measureCategory);
				
	}

	@Override
	public void update(MeasureCategory measureCategory) throws DAOException {
		String sql = "UPDATE measure_category SET name=?, code=? WHERE id=?";
		jdbcTemplate.update(sql, measureCategory.name, measureCategory.code, measureCategory.id);
	}
}
