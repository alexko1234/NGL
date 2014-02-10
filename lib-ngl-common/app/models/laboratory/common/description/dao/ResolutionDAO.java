package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.ObjectType;
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

import com.avaje.ebean.enhance.asm.Type;

import play.Logger;

@Repository
public class ResolutionDAO extends AbstractDAOMapping<Resolution>{

	protected ResolutionDAO() {
		super("resolution", Resolution.class, ResolutionMappingQuery.class,
				"SELECT distinct t.id, t.name, t.code, t.fk_resolution_category FROM resolution as t "+DAOHelpers.getResolutionSQLForInstitute("t"),
				true);
	}
	
	@Override
	public void remove(Resolution resolution) throws DAOException {
		if(null == resolution){
			throw new IllegalArgumentException("resolution is null");
		}
		//Remove list resolution for common_info_type
		String sqlResolution = "DELETE FROM common_info_type_resolution WHERE fk_resolution=?";
		jdbcTemplate.update(sqlResolution, resolution.id);
		//Remove list resolution for object_type
		sqlResolution = "DELETE FROM resolution_object_type WHERE fk_resolution=?";
		jdbcTemplate.update(sqlResolution, resolution.id);
		
		//remove institute
		removeInstitutes(resolution.id);
		
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
		       
        insertInstitutes(resolution.institutes, resolution.id, false);
        
		return resolution.id;
	}

	@Override
	public void update(Resolution resolution) throws DAOException {
		String sql = "UPDATE resolution SET code=?, name=? WHERE id=?";
		jdbcTemplate.update(sql, resolution.code, resolution.name, resolution.id);
	}
	
	
	private void insertInstitutes(List<Institute> institutes, Long resolutionId, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeInstitutes(resolutionId);
		}
		//Add institutes list		
		if(institutes!=null && institutes.size()>0){
			String sql = "INSERT INTO resolution_institute (fk_resolution, fk_institute) VALUES (?,?)";
			for (Institute institute : institutes) {
				if (institute == null || institute.id == null ) {
					throw new DAOException("institute is mandatory");
				}
				jdbcTemplate.update(sql, resolutionId, institute.id);
			}
		}
	}
	
	private void removeInstitutes( long resolutionId) {
		String sql = "DELETE FROM resolution_institute WHERE fk_resolution=?";
		jdbcTemplate.update(sql, resolutionId);
	}
	
	
	
	
	private void insertObjectTypes(List<ObjectType> objectTypes, Long id,
			boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeObjectTypes(id);
		}
		//Add resolutions list		
		if(objectTypes!=null && objectTypes.size()>0){
			String sql = "INSERT INTO resolution_object_type (fk_resolution, fk_object_type) VALUES(?,?)";
			for(ObjectType objectType:objectTypes){
				if(objectType == null || objectType.id == null ){
					throw new DAOException("objectType is mandatory");
				}
				jdbcTemplate.update(sql, id, objectType.id);
			}
		}				
	}
		
	private void removeObjectTypes(Long id) {
		String sql = "DELETE FROM resolution_object_type WHERE fk_resolution=?";
		jdbcTemplate.update(sql, id);
	}

	
	
	
	public List<Resolution> findByTypeCode(String typeCode)  throws DAOException {	
		String sql = sqlCommon +
				"inner join common_info_type_resolution cr ON cr.fk_resolution=t.id "+
				"inner join common_info_type c on c.id = cr.fk_common_info_type "+
				  DAOHelpers.getCommonInfoTypeSQLForInstitute("c")+
				" where c.code=?";
		return initializeMapping(sql, new SqlParameter("c.code", Types.VARCHAR)).execute(typeCode);	
	}
	
	public List<Resolution> findByCommonInfoTypeId(long idCommonInfoType) {
		String sql = sqlCommon+
				"inner join common_info_type_resolution cr ON cr.fk_resolution=t.id "+
				"WHERE cr.fk_common_info_type=?";
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
				"inner join common_info_type_resolution cr ON cr.fk_resolution=t.id "+
				"inner join common_info_type c on c.id =cr.fk_common_info_type "+
				  DAOHelpers.getCommonInfoTypeSQLForInstitute("c")+
				" where t.code=? and c.code=?";
		Logger.debug(sql);

		return( initializeMapping(sql, new SqlParameter("t.code", Types.VARCHAR),
				 new SqlParameter("c.code", Types.VARCHAR)).findObject(code, typeCode) != null )? true : false;	
	}
		
	public List<Resolution> findByObjectTypeCode(ObjectType.CODE objectTypeCode) throws DAOException {
		if(null == objectTypeCode){
			throw new DAOException("code is mandatory");
		}
		String sql = sqlCommon+
				"inner join resolution_object_type ro ON ro.fk_resolution=t.id "+
				"inner join object_type o ON ro.fk_object_type=o.id "+
				"WHERE o.code=?";		
		return initializeMapping(sql, new SqlParameter("o.code", Types.VARCHAR)).execute(objectTypeCode.name());		
	}
	
	public List<Resolution> findByObjectTypeId(Long id) throws DAOException {
		if(null == id){
			throw new DAOException("id is mandatory");
		}
		String sql = sqlCommon+
				"inner join resolution_object_type ro ON ro.fk_resolution= t.id "+
				"WHERE ro.fk_object_type=?";		
		return initializeMapping(sql, new SqlParameter("fk_object_type", Type.LONG)).execute(id);		
	}


}
