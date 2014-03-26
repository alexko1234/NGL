package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Institute;
import models.laboratory.common.description.ObjectType.CODE;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.description.ValuationCriteria;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class CommonInfoTypeDAO extends AbstractDAOMapping<CommonInfoType>{

	public CommonInfoTypeDAO() {
		super("common_info_type", CommonInfoType.class, CommonInfoTypeMappingQuery.class, 
				"SELECT distinct t.id as cId, t.name, t.code as codeSearch, o.id as oId, o.code as codeObject, o.generic "+
				"FROM common_info_type as t "+
				"JOIN object_type as o ON o.id=t.fk_object_type "+DAOHelpers.getCommonInfoTypeSQLForInstitute("t"), true);
				
	}

	public long save(CommonInfoType cit) throws DAOException {
		if(null == cit){
			throw new DAOException("CommonInfoType is mandatory");
		}

		//Check if objectType exist
		if(cit.objectType == null || cit.objectType.id == null ){
			throw new DAOException("CommonInfoType.objectType is mandatory");
		}
		
		//Create new cit
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", cit.name);
		parameters.put("code", cit.code);
		parameters.put("fk_object_type", cit.objectType.id);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		cit.id = newId;
		
		insertState(cit.states, cit.id, false);
		insertResolution(cit.resolutions, cit.id, false);
		insertProperties(cit.propertiesDefinitions, cit.id, false);
		insertInstitutes(cit.institutes, cit.id, false);
		insertValuationCriterias(cit.criterias, cit.id, false);
		
		return cit.id;
	}

	
	public void update(CommonInfoType cit) throws DAOException {
		if(null == cit || cit.id == null){
			throw new DAOException("CommonInfoType is mandatory");
		}
		
		CommonInfoType citDB = findById(cit.id);
		if(null == citDB){
			throw new DAOException("CommonInfoType does not exist");
		}		
		String sql = "UPDATE common_info_type SET name=? WHERE id=?";
		jdbcTemplate.update(sql, cit.name, cit.id);
		insertState(cit.states, cit.id, true);
		insertResolution(cit.resolutions, cit.id, true);
		insertProperties(cit.propertiesDefinitions, cit.id, true);
		insertInstitutes(cit.institutes, cit.id, true);
		insertValuationCriterias(cit.criterias, cit.id, true);
	}

	
	@Override
	public void remove(CommonInfoType commonInfoType) throws DAOException {
		//Delete state common_info_type_state
		removeStates(commonInfoType.id);
		//Delete resolution common_info_type_resolution
		removeResolution(commonInfoType.id);
		//Delete property_definition
		removeProperties(commonInfoType.id);
		//Delete institutes
		removeInstitutes(commonInfoType.id);
		//Delete criteria
		removeValuationCriterias(commonInfoType.id);
		
		super.remove(commonInfoType);
	}

	
	private void removeResolution( Long citId) {
		String sqlResol = "DELETE FROM common_info_type_resolution WHERE fk_common_info_type=?";
		jdbcTemplate.update(sqlResol, citId);
	}
	
	private void removeStates(Long citId) {
		String sqlState = "DELETE FROM common_info_type_state WHERE fk_common_info_type=?";
		jdbcTemplate.update(sqlState, citId);
	}
	
	private void removeInstitutes( Long citId) {
		String sqlInstit = "DELETE FROM common_info_type_institute WHERE fk_common_info_type=?";
		jdbcTemplate.update(sqlInstit, citId);
	}

	private void removeProperties(Long citId)
			throws DAOException {
		String sqlValues = "DELETE FROM value  WHERE fk_property_definition in (select p.id from property_definition p "
	                                              +"where p.fk_common_info_type = ?)";
		jdbcTemplate.update(sqlValues, citId);

		String sqlLevels = "DELETE FROM property_definition_level WHERE fk_property_definition in (select p.id from property_definition p "
	                                              +"where p.fk_common_info_type = ?)";
		jdbcTemplate.update(sqlLevels, citId);
		
		String sqlProps = "DELETE FROM property_definition WHERE fk_common_info_type = ?";
		jdbcTemplate.update(sqlProps, citId);

		//Delete common_info_type
	}
	
	private void removeValuationCriterias(Long citId) {
		String sqlCriteria = "DELETE FROM valuation_criteria_common_info_type WHERE fk_common_info_type=?";
		jdbcTemplate.update(sqlCriteria, citId);
	}
	
	
	private void insertProperties(List<PropertyDefinition> propertyDefinitions, Long citId, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeProperties(citId);
		}
		//Add PropertyDefinition
		if(propertyDefinitions!=null && propertyDefinitions.size()>0){
			PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
			for(PropertyDefinition propertyDefinition : propertyDefinitions){
				propertyDefinitionDAO.save(propertyDefinition, citId);				
			}
		}
	}
	
	private void insertState(List<State> states, Long citId, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeStates(citId);
		}
		//Add states list
		if(states!=null && states.size()>0){
			String sql = "INSERT INTO common_info_type_state (fk_common_info_type,fk_state) VALUES(?,?)";
			for(State state : states){
				if(state == null || state.id == null ){
					throw new DAOException("state is mandatory");
				}
				jdbcTemplate.update(sql, citId,state.id);
			}
		}
	}

	private void insertResolution(List<Resolution> resolutions, Long citId, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeResolution(citId);
		}
		//Add resolutions list		
		if(resolutions!=null && resolutions.size()>0){
			String sql = "INSERT INTO common_info_type_resolution (fk_common_info_type, fk_resolution) VALUES(?,?)";
			for(Resolution resolution:resolutions){
				if(resolution == null || resolution.id == null ){
					throw new DAOException("resolution is mandatory");
				}
				jdbcTemplate.update(sql, citId, resolution.id);
			}
		}
	}
	
	
	private void insertInstitutes(List<Institute> institutes, Long citId, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeInstitutes(citId);
		}
		//Add institutes list		
		if(institutes!=null && institutes.size()>0){
			String sql = "INSERT INTO common_info_type_institute (fk_common_info_type, fk_institute) VALUES(?,?)";
			for(Institute institute : institutes){
				if(institute == null || institute.id == null ){
					throw new DAOException("institute is mandatory");
				}
				jdbcTemplate.update(sql, citId, institute.id);
			}
		}
	}
	
	private void insertValuationCriterias(List<ValuationCriteria> criterias, Long citId, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeValuationCriterias(citId);
		}
		//Add criteria list		
		if(criterias!=null && criterias.size()>0){
			String sql = "INSERT INTO valuation_criteria_common_info_type (fk_common_info_type, fk_valuation_criteria) VALUES(?,?)";
			for(ValuationCriteria criteria : criterias){
				if(criteria == null || criteria.id == null ){
					throw new DAOException("criteria is mandatory");
				}
				jdbcTemplate.update(sql, citId, criteria.id);
			}
		}
	}


/*
	public List<CommonInfoType> findByName(String typeName) {
		String sql = sqlCommon+
				"WHERE t.name like \'%"+typeName+"%\' "+
				"ORDER by t.name asc";
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sql, null);
		return commonInfoTypeMappingQuery.execute();
	}

	public List<CommonInfoType> findByTypeNameAndType(String typeName, long idoObjectType) {
		String sql = sqlCommon+
				"WHERE t.name like \'%"+typeName+"%\' "+
				"AND fk_object_type=? "+
				"ORDER by t.name asc";
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sql, new SqlParameter("fk_object_type",Type.LONG));
		return commonInfoTypeMappingQuery.execute(idoObjectType);
	}
*/	
	
	/**
	 * Particular sql with two code must be implemented
	 */
	public CommonInfoType findByCode(String code) throws DAOException {
		String sql = sqlCommon+
				"WHERE t.code = ? ";
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sql, new SqlParameter("code",Types.VARCHAR));
		return commonInfoTypeMappingQuery.findObject(code);
	}

	public List<CommonInfoType> findByObjectTypeCode(CODE objectTypeCode) {
		String sql = sqlCommon+
				" WHERE o.code=? order by t.name";		
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sql, new SqlParameter("code",Types.VARCHAR));
		return commonInfoTypeMappingQuery.execute(objectTypeCode.name());
	}


}
