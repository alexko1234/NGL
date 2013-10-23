package models.laboratory.common.description.dao;


import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.description.StateCategory;
import models.laboratory.experiment.description.Protocol;
import models.utils.ListObject;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import com.avaje.ebean.enhance.asm.Type;

@Repository
public class StateDAO extends AbstractDAOMapping<State>{

	protected StateDAO() {
		super("state", State.class,StateMappingQuery.class, 
				"SELECT t.id,t.name,t.code,t.active,t.position " +
				"FROM state as t ", true);
	}

	
	@Override
	public void remove(State state) throws DAOException	{
		removeCategories(state.id);
		//Remove list state for object_type
		String sqlState = "DELETE FROM state_object_type WHERE fk_state=?";
		jdbcTemplate.update(sqlState, state.id);
		//remove resolution
		super.remove(state);
	}

	@Override
	public long save(State state) throws DAOException {
		//Check if category exist
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", state.code);
		parameters.put("name", state.name);
		parameters.put("active", state.active);
		parameters.put("position", state.position);
		
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		state.id = newId;
		
		insertCategories(state.categories, state.id, false);
		
		return state.id;
	}

	private void insertCategories(List<StateCategory> categories, Long id,
			boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeCategories(id);
		}
		//Add resolutions list		
		if(categories!=null && categories.size()>0){
			String sql = "INSERT INTO state_category_state (fk_state, fk_state_category) VALUES(?,?)";
			for(StateCategory category:categories){
				if(category == null || category.id == null ){
					throw new DAOException("category is mandatory");
				}
				jdbcTemplate.update(sql, id,category.id);
			}
		}				
	}
	
	
	
	private void insertObjectTypes(List<ObjectType> objectTypes, Long id,
			boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeObjectTypes(id);
		}
		//Add resolutions list		
		if(objectTypes!=null && objectTypes.size()>0){
			String sql = "INSERT INTO state_object_type (fk_state, fk_object_type) VALUES(?,?)";
			for(ObjectType objectType:objectTypes){
				if(objectType == null || objectType.id == null ){
					throw new DAOException("objectType is mandatory");
				}
				jdbcTemplate.update(sql, id, objectType.id);
			}
		}				
	}

	private void removeCategories(Long id) {
		String sql = "DELETE FROM state_category_state WHERE fk_state=?";
		jdbcTemplate.update(sql, id);
	}
	
	private void removeObjectTypes(Long id) {
		String sql = "DELETE FROM state_object_type WHERE fk_state=?";
		jdbcTemplate.update(sql, id);
	}
	
	@Override
	public void update(State state) throws DAOException {
		String sql = "UPDATE state SET code=?, name=?, active=?, position=? WHERE id=?";
		jdbcTemplate.update(sql, state.code, state.name, state.active, state.position, state.id);
	}
	
	public List<ListObject> findAllForContainerList(){
		//find data with object_type instead of state_category...
		//String sql = "SELECT t.code, t.name FROM state t inner join state_category_state scs on scs.fk_state = t.id" +
		//		" inner join state_category s on s.id = scs.fk_state_category WHERE s.code = ? ";
		String sql = "SELECT t.code, t.name FROM state t inner join state_object_state sos on sos.fk_state = t.id" +
				" inner join object_type o on o.id = sos.fk_object_type WHERE o.code = ? ";
		
		BeanPropertyRowMapper<ListObject> mapper = new BeanPropertyRowMapper<ListObject>(ListObject.class);
		return this.jdbcTemplate.query(sql, mapper, ObjectType.CODE.Container.name());
	}

	public List<State> findByCategoryCode(String code) throws DAOException {
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		String sql = sqlCommon+" inner join state_category_state scs on scs.fk_state = t.id" +
				" inner join state_category s on s.id = scs.fk_state_category WHERE s.code = ? ";
		return initializeMapping(sql, new SqlParameter("code", Types.VARCHAR)).execute(code);		
	}
	
	public List<State> findByObjectType(long idObjectType) throws DAOException {
		String sql = sqlCommon+
				"JOIN state_object_type ON fk_state=id "+
				"WHERE fk_object_type=?";		
		return initializeMapping(sql, new SqlParameter("fk_object_type", Type.LONG)).execute(idObjectType);		
	}


	public List<State> findByObjectTypeCode(String objectCode)  throws DAOException {
		String sql = sqlCommon+
				"JOIN state_object_type so ON so.fk_state=id "+
				"JOIN object_type o on o.id =so.fk_object_type "+
				"WHERE o.code = ?";
		return initializeMapping(sql, new SqlParameter("code", Types.VARCHAR)).execute(objectCode);	
	}
	
	public boolean isCodeExistForObjectTypeCode(String code, String objectCode)  throws DAOException {
		String sql = sqlCommon+
				"JOIN state_object_type so ON so.fk_state=id "+
				"JOIN object_type o on c.id =so.fk_object_type "+
				"WHERE o.code = ? and t.code = ?";
		return( initializeMapping(sql, new SqlParameter("o.code", Types.VARCHAR),
				 new SqlParameter("t.code", Types.VARCHAR)).findObject(objectCode, code) != null ) ? true : false;	
	}
	
	
	public List<State> findByCommonInfoType(long idCommonInfoType) {
		String sql = "SELECT st.id, st.name, st.code "+
				"FROM state st,  state_object_type sot,  common_info_type cit "+
				"WHERE sot.fk_state=st.id AND sot.fk_object_type = cit.fk_object_type "+
				"AND cit.id=?";
		BeanPropertyRowMapper<State> mapper = new BeanPropertyRowMapper<State>(State.class);
		return this.jdbcTemplate.query(sql, mapper, idCommonInfoType);
	}

}
