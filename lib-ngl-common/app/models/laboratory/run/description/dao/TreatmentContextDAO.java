package models.laboratory.run.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.avaje.ebean.enhance.asm.Type;

import models.laboratory.run.description.TreatmentContext;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

@Repository
public class TreatmentContextDAO extends AbstractDAOMapping<TreatmentContext>{

	protected TreatmentContextDAO() {
		super("treatment_context", TreatmentContext.class, TreatmentContextMappingQuery.class, 
				"SELECT t.id, t.code, t.name "+
						"FROM treatment_context as t", false);
	}
	
	public List<TreatmentContext> findByTreatmentTypeId(long id) {
		String sql = sqlCommon+
				" JOIN treatment_type_context as ttc ON ttc.fk_treatment_context=t.id "+
				"WHERE ttc.fk_treatment_type = ? ";
		TreatmentContextMappingQuery treatmentContextMappingQuery=new TreatmentContextMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return treatmentContextMappingQuery.execute(id);
	}

	
	

	@Override
	public long save(TreatmentContext treatmentContext) throws DAOException {	
		if (null == treatmentContext) {
			throw new DAOException("TreatmentContext is mandatory");
		}
		//Create new treatmentContext
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", treatmentContext.code);
		parameters.put("name", treatmentContext.name);
		// create a new jdbcInsert with the generatedKeyColumns() for the id (auto_increment)
		 SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
         .withTableName("treatment_context").usingGeneratedKeyColumns("id");
		// call the executeAndReturnKey method on this new object 
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		treatmentContext.id = newId;
		return treatmentContext.id;
	}
	
	
	private void removeTreatmentTypes(Long id) {
		//old
		String sql = "DELETE FROM treatment_type_context WHERE fk_treatment_context=?";
		jdbcTemplate.update(sql, id);
		
	}
	

	@Override
	public void update(TreatmentContext treatmentContext) throws DAOException {

		String sql = "UPDATE treatment_context SET code=?, name=? WHERE id=?";
		jdbcTemplate.update(sql, treatmentContext.code, treatmentContext.name, treatmentContext.id);
	}

	@Override
	public void remove(TreatmentContext treatmentContext) throws DAOException {
		//Remove contexts for this treatmentType
		if (treatmentContext != null) {
			System.out.println("treatmentContext" + treatmentContext.toString());
		}
		else {
			System.out.println("treatmentContext is null !!!!!!!!!!"); 
		}
		
		removeTreatmentTypes(treatmentContext.id);
		//Remove treatmentType
		super.remove(treatmentContext);
	}

}
