package models.laboratory.run.description;


import models.laboratory.run.description.dao.TreatmentContextDAO;
import models.utils.Model;

public class TreatmentContext extends Model<TreatmentContext> {
	
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
	

	public static Finder<TreatmentContext> find = new Finder<TreatmentContext>(TreatmentContextDAO.class.getName());


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		TreatmentContext other = (TreatmentContext) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	
	
	
}