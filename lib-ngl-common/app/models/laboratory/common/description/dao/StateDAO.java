package models.laboratory.common.description.dao;


import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;
import models.laboratory.common.description.StateCategory;
import models.utils.DescriptionHelper;
import models.utils.ListObject;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.Logger;

import com.avaje.ebean.enhance.asm.Type;

@Repository
public class StateDAO extends AbstractDAOMapping<State>{

	protected StateDAO() {
		super("state", State.class,StateMappingQuery.class, 
				"SELECT t.id,t.name,t.code,t.active,t.position, c.code as codeCit, i.code as codeIns, o.code as codeObj " +
				"FROM state as t "+
				"JOIN common_info_type_state cs ON cs.fk_state=t.id "+		
				"JOIN common_info_type as c ON c.id=cs.fk_common_info_type "+
				"JOIN common_info_type_institute ci ON c.id=ci.fk_common_info_type "+
				"JOIN institute i ON i.id = ci.fk_institute "+
				"JOIN state_object_type so ON so.fk_state=t.id "+
				"JOIN object_type o ON c.fk_object_type=o.id  and o.id=so.fk_object_type WHERE i.code=" + DescriptionHelper.getInstitute(), true);
	}

	
	@Override
	public void remove(State state) throws DAOException	{
		removeCategories(state.id);
	 	//Remove list state for common_info_type
		String sqlState = "DELETE FROM common_info_type_state WHERE fk_state=?";
		jdbcTemplate.update(sqlState, state.id);
		//Remove list state for object_type
		sqlState = "DELETE FROM state_object_type WHERE fk_state=?";
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
		//String sql = "SELECT t.code, t.name FROM state t inner join state_object_state sos on sos.fk_state = t.id" +
		//		" inner join object_type o on o.id = sos.fk_object_type WHERE o.code = ? ";
		String sql = "SELECT code, name FROM ("+ sqlCommon + ") WHERE codeObj =?"; 
		BeanPropertyRowMapper<ListObject> mapper = new BeanPropertyRowMapper<ListObject>(ListObject.class);
		return this.jdbcTemplate.query(sql, mapper, ObjectType.CODE.Container.name());
	}

	public List<State> findByCategoryCode(String code) throws DAOException {
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		String sql = "select * from (" + sqlCommon + ") a, (select s.code, scs.fk_state from state_category_state scs, state_category s where s.id = scs.fk_state_category) b where b.fk_state = a.id" +
				"WHERE b.code = ? ";
		return initializeMapping(sql, new SqlParameter("code", Types.VARCHAR)).execute(code);		
	}
	
	public List<State> findByCommonInfoType(long idCommonInfoType) throws DAOException {
		String sql = sqlCommon + " AND cs.fk_common_info_type=?";		
		return initializeMapping(sql, new SqlParameter("cs.fk_common_info_type", Type.LONG)).execute(idCommonInfoType);		
	}
	
	public List<State> findByObjectTypeCode(String objectTypeCode) throws DAOException {
		if(null == objectTypeCode){
			throw new DAOException("code is mandatory");
		}
		String sql = sqlCommon + " AND o.code=?";		
		return initializeMapping(sql, new SqlParameter("o.code", Types.VARCHAR)).execute(objectTypeCode);		
	}
	
	public List<State> findByObjectTypeId(Long id) throws DAOException {
		if(null == id){
			throw new DAOException("id is mandatory");
		}
		String sql = sqlCommon + " AND so.fk_object_type=?";		
		return initializeMapping(sql, new SqlParameter("so.fk_object_type", Type.LONG)).execute(id);		
	}


	public List<State> findByTypeCode(String typeCode)  throws DAOException {
		String sql = sqlCommon + " AND c.code = ?";
		return initializeMapping(sql, new SqlParameter("c.code", Types.VARCHAR)).execute(typeCode);	
	}
	
	public boolean isCodeExistForTypeCode(String code, String typeCode)  throws DAOException {
		String sql = sqlCommon + " AND t.code = ? and c.code = ?";
		return( initializeMapping(sql, new SqlParameter("t.code", Types.VARCHAR),
				 new SqlParameter("c.code", Types.VARCHAR)).findObject(code, typeCode) != null )? true : false;	
	}

}
