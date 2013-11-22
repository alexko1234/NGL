package models.laboratory.run.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.ValidationCriteria;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.run.description.ReadSetType;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class ReadSetTypeDAO extends AbstractDAOCommonInfoType<ReadSetType>{

	protected ReadSetTypeDAO() {
		super("readset_type", ReadSetType.class, ReadSetTypeMappingQuery.class, 
				"SELECT distinct c.id, c.fk_common_info_type ", 
						"FROM readset_type as c "+sqlCommonInfoType, false);
	}

	@Override
	public long save(ReadSetType readSetType) throws DAOException {
		if(null == readSetType){
			throw new DAOException("ReadSetType is mandatory");
		}
		
		//Check if category exist
		//if(readSetType.category == null || readSetType.category.id == null){
		//	throw new DAOException("ReadSetCategory is not present !!");
		//}
		
		//Check if criteria exist
		if (null == readSetType.criterias || readSetType.criterias.size()==0) {
			throw new DAOException("Criteria doesn't exist or criteria.id is null) !! - "+readSetType.code);
		}
		
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		readSetType.id = commonInfoTypeDAO.save(readSetType);
		
		//Create new runType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", readSetType.id);
		parameters.put("fk_common_info_type", readSetType.id);
		//parameters.put("fk_readset_category", readSetType.category.id);
		
		jdbcInsert.execute(parameters);
		
		//Add criterias
		insertValidationCriterias(readSetType.criterias, readSetType.id, false);
		
		return readSetType.id;
	}
	
	private void insertValidationCriterias(List<ValidationCriteria> criterias, Long id, boolean deleteBefore) throws DAOException {
		if (deleteBefore) {
			removeValidationCriterias(id);
		}
		//Add resolutions list		
		if (criterias!=null && criterias.size()>0) {

			Map<String, Object> parameters = null;
			
			for (ValidationCriteria criteria : criterias) {
				if (criteria == null) {
					throw new DAOException("criteria is mandatory");
				} else {
					parameters = new HashMap<String, Object>();
					parameters.put("fk_readset_type", id);
					parameters.put("fk_validation_criteria", criteria.id);
					// set the table name to the name of the link table
					 SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
			         .withTableName("validation_criteria_readset");
					jdbcInsert.execute(parameters);
				}				
			}
		} else {
			throw new DAOException("criterias null or empty");
		}
	}
	
	private void removeValidationCriterias(Long id)  throws DAOException {
		String sql = "DELETE FROM validation_criteria_readset WHERE fk_readset_type=?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public void update(ReadSetType readSetType) throws DAOException {
		//Update criterias
		insertValidationCriterias(readSetType.criterias, readSetType.id, true);
		
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(readSetType);
	}

	@Override
	public void remove(ReadSetType readSetType) throws DAOException {
		//Remove criterias for this readSetType
		removeValidationCriterias(readSetType.id);
		//Remove readSetType
		super.remove(readSetType);
		//Remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(readSetType);
	}
}

