package models.laboratory.common.description;

import play.modules.spring.Spring;
import models.laboratory.common.description.dao.MeasureValueDAO;
import models.utils.Model;

public class MeasureValue extends Model<MeasureValue>{

	public String value;   
	
	public Boolean defaultValue = Boolean.FALSE;
  	
	public MeasureCategory measureCategory;
	
	public MeasureValue() {
		super(MeasureValueDAO.class.getName());
	}
	
	public static Finder<MeasureValue> find = new Finder<MeasureValue>(MeasureValueDAO.class.getName()); 
	
	public static MeasureValue findByValue(String value)
	{
		MeasureValueDAO measureValueDAO = Spring.getBeanOfType(MeasureValueDAO.class);
		return measureValueDAO.findByValue(value);
	}
}
