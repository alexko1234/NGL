package models.laboratory.run.description;

import java.util.List;

import models.laboratory.run.description.dao.TreatmentTypeContextDAO;
import models.utils.dao.DAOException;

public class TreatmentTypeContext extends TreatmentContext{
	
	public TreatmentTypeContext() {
		super();		
	}

	public TreatmentTypeContext(TreatmentContext tc, Boolean required) {
		super(tc.id, tc.code,tc.name);
		this.required = required;
	}

	public Boolean required = Boolean.FALSE;
	
	public static TreatmentTypeContextFinder find = new TreatmentTypeContextFinder();
	
	
	public static class TreatmentTypeContextFinder extends Finder<TreatmentTypeContext> {

		public TreatmentTypeContextFinder() {
			super(TreatmentTypeContextDAO.class.getName());
		}

		public List<TreatmentTypeContext> findByTreatmentTypeId(Long id) throws DAOException {
			return ((TreatmentTypeContextDAO) getInstance()).findByTreatmentTypeId(id);
		}
		
		public TreatmentTypeContext findByTreatmentTypeId(String code, Long id) throws DAOException {
			return ((TreatmentTypeContextDAO) getInstance()).findByTreatmentTypeId(code, id);
		}

	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((required == null) ? 0 : required.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreatmentTypeContext other = (TreatmentTypeContext) obj;
		if (required == null) {
			if (other.required != null)
				return false;
		} else if (!required.equals(other.required))
			return false;
		return true;
	}
	
	
}