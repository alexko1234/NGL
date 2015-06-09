 package models.laboratory.run.description.dao;

import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.run.description.TreatmentType;
import models.laboratory.run.description.TreatmentTypeContext;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import com.avaje.ebean.enhance.asm.Type;

import play.api.modules.spring.Spring;

@Repository
public class TreatmentTypeDAO extends AbstractDAOCommonInfoType<TreatmentType>{

	protected TreatmentTypeDAO() {
		super("treatment_type", TreatmentType.class, TreatmentTypeMappingQuery.class, 
				"SELECT distinct c.id, c.names, c.fk_common_info_type, c.fk_treatment_category, c.display_orders ",
						"FROM treatment_type as c "+sqlCommonInfoType, false);
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
		parameters.put("display_orders", treatmentType.displayOrders);
		jdbcInsert.execute(parameters);
		//Add contexts
		insertTreatmentContexts(treatmentType.contexts, treatmentType.id, false);
		
		return treatmentType.id;
	}
	
	
	private void insertTreatmentContexts(List<TreatmentTypeContext> contexts, Long id, boolean deleteBefore) throws DAOException {
		if (deleteBefore) {
			removeTreatmentContexts(id);
		}
		//Add contexts list		
		if (contexts!=null && contexts.size()>0) {
			String sql = "INSERT INTO treatment_type_context (fk_treatment_type, fk_treatment_context, required) VALUES(?,?,?)";
			for(TreatmentTypeContext context : contexts){
				if(context == null || context.id == null ){
					throw new DAOException("context is mandatory");
				}
				jdbcTemplate.update(sql, id, context.id, context.required);
			}
		}
		else {
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
		String sql = "UPDATE treatment_type SET names=?, display_order=? WHERE id=?";
		jdbcTemplate.update(sql, treatmentType.names, treatmentType.displayOrders, treatmentType.id);
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
	
	public List<TreatmentType> findByTreatmentContextId(long id) {
		String sql = sqlCommon+
				" inner join treatment_type_context as ttc ON ttc.fk_treatment_type=c.id "+
				"WHERE fk_treatment_context = ? ";
		TreatmentTypeMappingQuery treatmentTypeMappingQuery=new TreatmentTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return treatmentTypeMappingQuery.execute(id);
	}
	
	
	public List<TreatmentType> findByTreatmentCategoryNames(String...categoryNames) throws DataAccessException, DAOException {
		String sql = sqlCommon+
				" inner join treatment_category cat on cat.id = c.fk_treatment_category where cat.name in ("+listToParameters(Arrays.asList(categoryNames))+")";;				
		
		return initializeMapping(sql, listToSqlParameters(Arrays.asList(categoryNames),"cat.name", Types.VARCHAR)).execute((Object[])categoryNames);
	}
	
	public List<TreatmentType> findByLevels(Level.CODE...levels) throws DAOException{
		Object[] parameters = new Object[0];
		Object[] sqlParameters = new SqlParameter[0];
		
		String sql = sqlCommon
				+" inner join property_definition pd on pd.fk_common_info_type = t.id"
				+" inner join property_definition_level pdl on pdl.fk_property_definition = pd.id"
				+" inner join level l on l.id = pdl.fk_level"
				+" where 1=1 ";
		
		if(null != levels && levels.length > 0){
			parameters = ArrayUtils.addAll(parameters,levels);
			sqlParameters = ArrayUtils.addAll(sqlParameters, listToSqlParameters(Arrays.asList(levels),"l.code", Types.VARCHAR));
			sql += "and l.code in ("+listToParameters(Arrays.asList(levels))+")";
		}
		
		return initializeMapping(sql, (SqlParameter[])sqlParameters).execute(parameters);
	}
	

}