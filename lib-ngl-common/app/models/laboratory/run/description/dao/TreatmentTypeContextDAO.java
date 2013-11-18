package models.laboratory.run.description.dao;

import java.util.List;

import models.laboratory.run.description.TreatmentContext;
import models.laboratory.run.description.TreatmentType;
import models.laboratory.run.description.TreatmentTypeContext;
import models.utils.ListObject;
import models.utils.dao.AbstractDAODefault;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import com.avaje.ebean.enhance.asm.Type;

@Repository
public class TreatmentTypeContextDAO extends AbstractDAODefault<TreatmentTypeContext>{

	protected TreatmentTypeContextDAO() {
		super("treatment_context", TreatmentTypeContext.class, true);	
	}
	
	public List<TreatmentTypeContext> findByTreatmentTypeId(Long id) throws DAOException {
		
		String sql ="SELECT t.id, t.code, t.name, ttc.required "+
				"FROM treatment_context as t "+
				"JOIN treatment_type_context as ttc ON ttc.fk_treatment_context=t.id "+
				"WHERE ttc.fk_treatment_type = ? ";
		BeanPropertyRowMapper<TreatmentTypeContext> mapper = new BeanPropertyRowMapper<TreatmentTypeContext>(TreatmentTypeContext.class);
		return this.jdbcTemplate.query(sql, mapper, id);
		
	}
	
	public TreatmentTypeContext findByTreatmentTypeId(String code, Long id) {
		
		String sql = "SELECT t.id, t.code, t.name, ttc.required "+
				"FROM treatment_context as t "+
				"JOIN treatment_type_context as ttc ON ttc.fk_treatment_context=t.id "+
				"WHERE ttc.fk_treatment_type = ? and t.code = ?";
		
		BeanPropertyRowMapper<TreatmentTypeContext> mapper = new BeanPropertyRowMapper<TreatmentTypeContext>(TreatmentTypeContext.class);
		List<TreatmentTypeContext> result = this.jdbcTemplate.query(sql, mapper, id, code);
		if(result != null && result.size() == 1){
			return result.get(0);
		}else{
			return null;
		}
		
		
	}
	
	@Override
	public void remove(TreatmentTypeContext treatmentContext) throws DAOException {
		throw new RuntimeException("Pas implémenter cat table de liaison");
	}

	@Override
	public long save(TreatmentTypeContext value) throws DAOException {
		throw new RuntimeException("Pas implémenter cat table de liaison");
	}

	@Override
	public void update(TreatmentTypeContext value) throws DAOException {
		throw new RuntimeException("Pas implémenter cat table de liaison");		
	}

	@Override
	public List<TreatmentTypeContext> findAll() throws DAOException {
		throw new RuntimeException("Pas implémenter cat table de liaison");	
	}

	@Override
	public TreatmentTypeContext findById(Long id) throws DAOException {
		throw new RuntimeException("Pas implémenter cat table de liaison");
	}

	@Override
	public TreatmentTypeContext findByCode(String code) throws DAOException {
		throw new RuntimeException("Pas implémenter cat table de liaison");
	}
	
	@Override
	public List<ListObject> findAllForList(){
		throw new RuntimeException("Pas implémenter cat table de liaison");
	}
	
}

