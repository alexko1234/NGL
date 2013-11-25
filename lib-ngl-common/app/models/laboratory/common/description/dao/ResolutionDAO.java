package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.utils.DescriptionHelper;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.Logger;

@Repository
public class ResolutionDAO extends AbstractDAOMapping<Resolution>{

	protected ResolutionDAO() {
		super("resolution", Resolution.class, ResolutionMappingQuery.class,
				"SELECT t.id, t.name, t.code, t.fk_resolution_category FROM resolution as t ",
				true);
	}
	
	@Override
	public void remove(Resolution resolution) throws DAOException
	{
		//Remove list resolution for common_info_type
		String sqlState = "DELETE FROM common_info_type_resolution WHERE fk_resolution=?";
		jdbcTemplate.update(sqlState, resolution.id);
		//remove resolution
		super.remove(resolution);
	}

	@Override
	public long save(Resolution resolution) throws DAOException {
		//Check if category exist
		if(resolution.category == null || resolution.category.id == null){
			throw new IllegalArgumentException("ResolutionCategory is not present ");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", resolution.code);
		parameters.put("name", resolution.name);
		parameters.put("fk_resolution_category", resolution.category.id);

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		resolution.id = newId;
		return resolution.id;
	}

	@Override
	public void update(Resolution resolution) throws DAOException {
		String sql = "UPDATE resolution SET code=?, name=? WHERE id=?";
		jdbcTemplate.update(sql, resolution.code, resolution.name);
		
	}
	
	public List<Resolution> findByTypeCode(String typeCode)  throws DAOException {
		
		String sql = sqlCommon +
				"JOIN common_info_type_resolution cr ON cr.fk_resolution=t.id "+
				"JOIN common_info_type c on c.id =cr.fk_common_info_type "+
				  DAOHelpers.getSQLForInstitute("c")+
				" where c.code=?";
		return initializeMapping(sql, new SqlParameter("c.code", Types.VARCHAR)).execute(typeCode);	
	}
	
	
	public List<Resolution> findByCommonInfoType(long idCommonInfoType)
	{
		String sql = sqlCommon+
				"JOIN common_info_type_resolution ON fk_resolution=id "+
				"WHERE fk_common_info_type=?";
		BeanPropertyRowMapper<Resolution> mapper = new BeanPropertyRowMapper<Resolution>(Resolution.class);
		return this.jdbcTemplate.query(sql, mapper, idCommonInfoType);
	}

	public List<Resolution> findByCategoryCode(String code) throws DAOException {
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		String sql = sqlCommon+" inner join resolution_category r on r.id = t.fk_resolution_category WHERE r.code = ? ";
		return initializeMapping(sql, new SqlParameter("code", Types.VARCHAR)).execute(code);		
	}

	public boolean isCodeExistForTypeCode(String code, String typeCode) throws DAOException {		
		String sql = sqlCommon +
				"JOIN common_info_type_resolution cr ON cr.fk_resolution=t.id "+
				"JOIN common_info_type c on c.id =cr.fk_common_info_type "+
				  DAOHelpers.getSQLForInstitute("c")+
				" where t.code=? and c.code=?";
		Logger.debug(sql);

		return( initializeMapping(sql, new SqlParameter("t.code", Types.VARCHAR),
				 new SqlParameter("c.code", Types.VARCHAR)).findObject(code, typeCode) != null )? true : false;	
	}


}
