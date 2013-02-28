package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.State;
import models.laboratory.common.description.StateCategory;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import com.avaje.ebean.enhance.asm.Type;

@Repository
public class StateDAO extends AbstractDAOMapping<State>{

	protected StateDAO() {
		super("state", State.class,StateMappingQuery.class, 
				"SELECT t.id,name,code,active,priority,level,fk_state_category " +
				"FROM state as t ", true);
	}

	public List<State> findByCommonInfoType(long idCommonInfoType)
	{
		String sql = sqlCommon+
				"JOIN common_info_type_state ON fk_state=id "+
				"WHERE fk_common_info_type=?";
		StateMappingQuery stateMappingQuery = new StateMappingQuery(dataSource, sql,new SqlParameter("fk_common_info_type", Type.LONG));
		return stateMappingQuery.execute(idCommonInfoType);
	}

	@Override
	public void remove(State state) throws DAOException
	{
		//Remove list state for common_info_type
		String sqlState = "DELETE FROM common_info_type_state WHERE fk_state=?";
		jdbcTemplate.update(sqlState, state.id);
		//remove resolution
		super.remove(state);
	}

	@Override
	public long save(State state) throws DAOException {
		//Check if category exist
		if(state.stateCategory!=null){
			StateCategory stateCategoryDB = StateCategory.find.findByCode(state.stateCategory.code);
			if(stateCategoryDB==null){
				state.stateCategory.id=state.stateCategory.save();
			}else
				state.stateCategory=stateCategoryDB;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", state.code);
		parameters.put("name", state.name);
		parameters.put("active", state.active);
		parameters.put("priority", state.priority);
		parameters.put("level", state.level);
		parameters.put("fk_state_category", state.stateCategory.id);

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		state.id = newId;
		return state.id;
	}

	@Override
	public void update(State state) throws DAOException {
		String sql = "UPDATE state SET code=?, name=?, active=?, priority=?, level=? WHERE id=?";
		jdbcTemplate.update(sql, state.code, state.name, state.active, state.priority, state.level, state.id);
	}

}
