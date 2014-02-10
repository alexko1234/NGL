package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

@Repository
public class ObjectTypeDAO extends AbstractDAOMapping<ObjectType>{

	protected ObjectTypeDAO() {
		super("object_type", ObjectType.class, ObjectTypeMappingQuery.class, 
				"SELECT t.id as oId, t.code as codeObject, t.generic "+
				"FROM object_type as t ", true);
	}
	
	
	public long save(ObjectType ot) throws DAOException {
		//Check if objectType exist
		if(null == ot){
			throw new DAOException("ObjectType is mandatory");
		}
		//Create new ot
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", ot.code);
		//field generic can not be null
		parameters.put("generic", ot.generic);
		
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		ot.id = newId;

		insertStates(ot.states, ot.id, false);

		return ot.id;
	}

	
	public void update(ObjectType ot) throws DAOException {
		if (null == ot) {
			throw new DAOException("ObjectType is mandatory (case 1)");
		}
		if (ot.id == null) {
			throw new DAOException("ObjectType is mandatory (case 2)");
		}
		
		ObjectType otDB = findById(ot.id);
		if(null == otDB){
			throw new DAOException("ObjectType doesn't exist");
		}		
		String sql = "UPDATE object_type SET code=?, generic=? WHERE id=?";
		jdbcTemplate.update(sql, ot.code, ot.generic, ot.id);
		
		insertStates(ot.states, ot.id, true);
	}

	@Override
	public void remove(ObjectType objectType) throws DAOException {
		//Delete state common_info_type_state
		removeStates(objectType.id);
		
		super.remove(objectType);
	}

	private void removeStates(Long otId) {
		String sqlState = "DELETE FROM state_object_type WHERE fk_object_type=?";
		jdbcTemplate.update(sqlState, otId);
	}

	
	private void insertStates(List<State> states, Long otId, boolean deleteBefore)
			throws DAOException {
		//Add states list
		if(deleteBefore){
			removeStates(otId);
		}
		if(states!=null && states.size()>0){
			String sql = "INSERT INTO state_object_type (fk_object_type, fk_state) VALUES (?,?)";
			for(State state : states){
				if(state == null || state.id == null ){
					throw new DAOException("state is mandatory");
				}
				jdbcTemplate.update(sql, otId, state.id);
			}
		}
	}

	
	/**
	 * Particular sql with two code must be implemented
	 */
	public ObjectType findByCode(String code) throws DAOException {
		String sql = sqlCommon+
				"WHERE t.code = ? ";
		ObjectTypeMappingQuery objectTypeMappingQuery = new ObjectTypeMappingQuery(dataSource, sql, new SqlParameter("code",Types.VARCHAR));
		return objectTypeMappingQuery.findObject(code);
	}

}
