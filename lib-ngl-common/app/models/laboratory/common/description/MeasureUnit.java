package models.laboratory.common.description;

import models.laboratory.common.description.dao.MeasureUnitDAO;
import models.utils.Model;
import models.utils.dao.DAOException;

public class MeasureUnit extends Model<MeasureUnit>{

	public String value;   
	
	public Boolean defaultUnit = Boolean.FALSE;
  	
	//multiple par rapport à une référence ex L et µL 10-6
	
	public MeasureCategory category;
	
	public MeasureUnit() {
		super(MeasureUnitDAO.class.getName());
	}
	
	
	public static MeasureUnitFinder find = new MeasureUnitFinder();
	
		
	
	public static class MeasureUnitFinder extends Finder<MeasureUnit> {

		public MeasureUnitFinder() {
		    super(MeasureUnitDAO.class.getName());
		}
		
		public MeasureUnit findByValue(String value) throws DAOException {
			return ((MeasureUnitDAO) getInstance()).findByValue(value);
		}
		
	}
	
	
	
}
