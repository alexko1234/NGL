package models.laboratory.run.description.dao;

import models.laboratory.run.description.TreatmentContext;
import models.utils.dao.AbstractDAODefault;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

@Repository
public class TreatmentContextDAO extends AbstractDAODefault<TreatmentContext>{

	protected TreatmentContextDAO() {
		super("treatment_context",TreatmentContext.class,true);
	}
	
		
	private void removeTreatmentTypesContexts(Long id) {
		String sql = "DELETE FROM treatment_type_context WHERE fk_treatment_context=?";
		jdbcTemplate.update(sql, id);
		
	}
	
	@Override
	public void remove(TreatmentContext treatmentContext) throws DAOException {
		if(null == treatmentContext){
			throw new IllegalArgumentException("treatmentContext is null");
		}
		removeTreatmentTypesContexts(treatmentContext.id);
		//Remove treatmentContext
		super.remove(treatmentContext);
	}

}

