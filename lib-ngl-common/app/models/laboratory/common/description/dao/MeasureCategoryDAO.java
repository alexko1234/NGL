package models.laboratory.common.description.dao;

import java.util.List;

import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureValue;

import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class MeasureCategoryDAO extends AbstractCategoryDAO<MeasureCategory>{

	public MeasureCategoryDAO() {
		super("measure_category",MeasureCategory.class);
	}

	public MeasureCategory findById(long id)
	{
		MeasureCategory measureCategory = (MeasureCategory) super.findById(id);
		MeasureValueDAO measureValueDAO = Spring.getBeanOfType(MeasureValueDAO.class);
		List<MeasureValue> measureValues = measureValueDAO.findByMeasureCategory(id);
		measureCategory.measurePossibleValues=measureValues;
		return measureCategory;
	}
	
	
	
	public MeasureCategory add(MeasureCategory measureCategory)
	{
		measureCategory = (MeasureCategory) super.add(measureCategory);
		//Add measureValue
		if(measureCategory.measurePossibleValues!=null && measureCategory.measurePossibleValues.size()>0){
			MeasureValueDAO measureValueDAO = Spring.getBeanOfType(MeasureValueDAO.class);
			for(MeasureValue measureValue : measureCategory.measurePossibleValues){
				measureValue.measureCaterory=measureCategory;
				measureValueDAO.add(measureValue);
			}
		}
        return measureCategory;
	}
}
