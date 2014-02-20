package models.laboratory.common.description;

import play.api.modules.spring.Spring;
import models.laboratory.common.description.dao.MeasureUnitDAO;
import models.utils.Model;

public class MeasureUnit extends Model<MeasureUnit>{

	public String value;   
	
	public Boolean defaultUnit = Boolean.FALSE;
  	
	//multiple par rapport à une référence ex L et µL 10-6
	
	public MeasureCategory category;
	
	public MeasureUnit() {
		super(MeasureUnitDAO.class.getName());
	}
	
	public static Finder<MeasureUnit> find = new Finder<MeasureUnit>(MeasureUnitDAO.class.getName()); 
	
	public static MeasureUnit findByValue(String value)
	{
		MeasureUnitDAO measureValueDAO = Spring.getBeanOfType(MeasureUnitDAO.class);
		return measureValueDAO.findByValue(value);
	}
}
