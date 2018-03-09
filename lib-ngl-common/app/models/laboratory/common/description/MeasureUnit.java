package models.laboratory.common.description;

import models.laboratory.common.description.dao.MeasureUnitDAO;
import models.utils.Model;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;

public class MeasureUnit extends Model<MeasureUnit> {

	public static MeasureUnitFinder find = new MeasureUnitFinder();

	public String value;   	
	public Boolean defaultUnit = Boolean.FALSE;
	//multiple par rapport à une référence ex L et µL 10-6
	public MeasureCategory category;
	
	public MeasureUnit() {
		super(MeasureUnitDAO.class.getName());
	}
	
	@Override
	protected Class<? extends AbstractDAO<MeasureUnit>> daoClass() {
		return MeasureUnitDAO.class;
	}

	public static class MeasureUnitFinder extends Finder<MeasureUnit,MeasureUnitDAO> {

//		public MeasureUnitFinder() {
//		    super(MeasureUnitDAO.class.getName());
//		}
		public MeasureUnitFinder() { super(MeasureUnitDAO.class); }
		
		public MeasureUnit findByValue(String value) throws DAOException {
//			return ((MeasureUnitDAO) getInstance()).findByValue(value);
			return getInstance().findByValue(value);
		}
		
	}

}
