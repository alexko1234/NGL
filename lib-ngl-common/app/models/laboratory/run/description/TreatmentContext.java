package models.laboratory.run.description;

import static fr.cea.ig.lfw.utils.Hashing.hash;
import static fr.cea.ig.lfw.utils.Equality.objectEquals;
import static fr.cea.ig.lfw.utils.Equality.typedEquals;

import models.laboratory.run.description.dao.TreatmentContextDAO;
import models.utils.Model;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;
import models.utils.dao.AbstractDAO;

public class TreatmentContext extends Model<TreatmentContext> {
	
//	public static Finder<TreatmentContext> find = new Finder<TreatmentContext>(TreatmentContextDAO.class.getName());
	public static final Finder<TreatmentContext,TreatmentContextDAO> find = new Finder<>(TreatmentContextDAO.class);
	
	public String name;
	
	public TreatmentContext() {
		super(TreatmentContextDAO.class.getName());
	}
	
	public TreatmentContext(String name) {
		super(TreatmentContextDAO.class.getName());
		this.name = name;
		this.code = name;
	}
	
	protected TreatmentContext(Long id, String code, String name) {
		super(TreatmentContextDAO.class.getName());
		this.id = id;
		this.name = name;
		this.code = code;
	}
	
	@Override
	protected Class<? extends AbstractDAO<TreatmentContext>> daoClass() {
		return TreatmentContextDAO.class;
	}
	
	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		return result;
		return hash(super.hashCode(),name);
	}

	@Override
	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		TreatmentContext other = (TreatmentContext) obj;
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} else if (!name.equals(other.name))
//			return false;
//		return true;
		return typedEquals(TreatmentContext.class, this, obj,
				           (a,b) -> super.equals(obj) && objectEquals(a.name,b.name));
	}

}
