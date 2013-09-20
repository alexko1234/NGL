package models.laboratory.run.description;

import org.codehaus.jackson.annotate.JsonIgnore;

import models.laboratory.run.description.dao.TreatmentContextDAO;
import models.utils.Model;

public class TreatmentContext extends Model<TreatmentContext> {
	
	public TreatmentContext() {
		super(TreatmentContextDAO.class.getName());
	}
	
	public String name;

    
	public static Finder<TreatmentContext> find = new Finder<TreatmentContext>(TreatmentContextDAO.class.getName());
	

}