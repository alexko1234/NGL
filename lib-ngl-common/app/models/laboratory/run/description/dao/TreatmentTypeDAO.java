 package models.laboratory.run.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.run.description.TreatmentContext;
import models.laboratory.run.description.TreatmentType;
import models.laboratory.run.description.TreatmentTypeContext;
import models.utils.DescriptionHelper;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.avaje.ebean.enhance.asm.Type;

import play.api.modules.spring.Spring;

@Repository
public class TreatmentTypeDAO extends AbstractDAOMapping<TreatmentType>{

	protected TreatmentTypeDAO() {
		super("treatment_type", TreatmentType.class, TreatmentTypeMappingQuery.class, 
				"SELECT t.id, t.names, t.fk_common_info_type, t.fk_treatment_category "+
						"FROM treatment_type as t "+
						"JOIN common_info_type as c ON c.id=t.fk_common_info_type ", false);
	}
	
	public List<TreatmentType> findByTreatmentContextId(long id)
	{
		String sql = sqlCommon+
				" JOIN treatment_type_context as ttc ON ttc.fk_treatment_type=t.id "+
				"WHERE fk_treatment_context = ? ";
		TreatmentTypeMappingQuery treatmentTypeMappingQuery=new TreatmentTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return treatmentTypeMappingQuery.execute(id);
	}
	

	@Override
	public long save(TreatmentType treatmentType) throws DAOException {	
		if (null == treatmentType) {
			throw new DAOException("ProjectType is mandatory");
		}
		//Check if category exist
		if (treatmentType.category == null || treatmentType.category.id == null) {
			throw new DAOException("TreatmentCategory is not present !!");
		}
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		treatmentType.id = commonInfoTypeDAO.save(treatmentType);
		//Create new treatmentType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", treatmentType.id);
		parameters.put("names", treatmentType.names);
		parameters.put("fk_common_info_type", treatmentType.id);
		parameters.put("fk_treatment_category", treatmentType.category.id);
		jdbcInsert.execute(parameters);
		//Add contexts
		insertTreatmentContexts(treatmentType.contexts, treatmentType.id, false);
		
		return treatmentType.id;
	}
	
	
	private void insertTreatmentContexts(List<TreatmentTypeContext> contexts, Long id, boolean deleteBefore) throws DAOException {
		if (deleteBefore) {
			removeTreatmentContexts(id);
		}
		//Add resolutions list		
		if (contexts!=null && contexts.size()>0) {
			//String sql = null;
			Map<String, Object> parameters = null;
			
			for (TreatmentTypeContext context : contexts) {
				if (context == null) {
					throw new DAOException("context is mandatory");
				} else {
					parameters = new HashMap<String, Object>();
					parameters.put("fk_treatment_type", id);
					parameters.put("fk_treatment_context", context.id);
					parameters.put("required", context.required);
					// set the table name to the name of the link table
					 SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
			         .withTableName("treatment_type_context");
					jdbcInsert.execute(parameters);
				}				
			}
		} else {
			throw new DAOException("contexts null or empty");
		}
		
	}
	
	private void removeTreatmentContexts(Long id)  throws DAOException {
		String sql = "DELETE FROM treatment_type_context WHERE fk_treatment_type=?";
		jdbcTemplate.update(sql, id);
	}
	

	@Override
	public void update(TreatmentType treatmentType) throws DAOException {
		//Update contexts
		insertTreatmentContexts(treatmentType.contexts, treatmentType.id, true);
		String sql = "UPDATE treatment_type SET names=? WHERE id=?";
		jdbcTemplate.update(sql, treatmentType.names, treatmentType.id);
	}

	@Override
	public void remove(TreatmentType treatmentType) throws DAOException {
		//Remove contexts for this treatmentType
		removeTreatmentContexts(treatmentType.id);
		//Remove treatmentType
		super.remove(treatmentType);
		//Remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(treatmentType);
	}

}