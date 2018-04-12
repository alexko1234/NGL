package models.laboratory.run.description;

import static fr.cea.ig.lfw.utils.Equality.objectEquals;
import static fr.cea.ig.lfw.utils.Equality.typedEquals;
import static fr.cea.ig.lfw.utils.Hashing.hash;

import java.util.List;

import models.laboratory.run.description.dao.TreatmentTypeContextDAO;
import models.utils.dao.DAOException;

public class TreatmentTypeContext extends TreatmentContext {

	@SuppressWarnings("hiding")
	public static final TreatmentTypeContextFinder find = new TreatmentTypeContextFinder();

	public Boolean required = false; // Boolean.FALSE;
	
	public TreatmentTypeContext() {
//		super();		
	}

	public TreatmentTypeContext(TreatmentContext tc, Boolean required) {
		super(tc.id, tc.code,tc.name);
		this.required = required;
	}

	
	public static class TreatmentTypeContextFinder extends Finder<TreatmentTypeContext,TreatmentTypeContextDAO> {

//		public TreatmentTypeContextFinder() {
//			super(TreatmentTypeContextDAO.class.getName());
//		}
		public TreatmentTypeContextFinder() { super(TreatmentTypeContextDAO.class); }

		public List<TreatmentTypeContext> findByTreatmentTypeId(Long id) throws DAOException {
//			return ((TreatmentTypeContextDAO) getInstance()).findByTreatmentTypeId(id);
			return getInstance().findByTreatmentTypeId(id);
		}
		
		public TreatmentTypeContext findByTreatmentTypeId(String code, Long id) throws DAOException {
//			return ((TreatmentTypeContextDAO) getInstance()).findByTreatmentTypeId(code, id);
			return getInstance().findByTreatmentTypeId(code, id);
		}

	}

	// TODO: provide some helper method to compute hash(this.hashCode,super.hashCode);
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result
//				+ ((required == null) ? 0 : required.hashCode());
//		return result;
//	}
	@Override
	public int hashCode() {
		return hash(super.hashCode(),required);
	}

	@Override
	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		TreatmentTypeContext other = (TreatmentTypeContext) obj;
//		if (required == null) {
//			if (other.required != null)
//				return false;
//		} else if (!required.equals(other.required))
//			return false;
//		return true;
		return typedEquals(TreatmentTypeContext.class, this, obj,
				           (a,b) -> super.equals(obj) && objectEquals(a.required,b.required));
	}
		
}
