package models.laboratory.run.description.dao;

import org.springframework.stereotype.Repository;
import models.laboratory.run.description.TreatmentContext;
import models.utils.dao.AbstractDAO;

@Repository
public class TreatmentContextDAO extends AbstractDAO<TreatmentContext>{

	public TreatmentContextDAO() {
		super("treatment_context",TreatmentContext.class,true);
	}
}
