package models.laboratory.run.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.ValidationCriteria;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.run.description.RunType;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import play.Logger;
import play.api.modules.spring.Spring;

@Repository
public class RunTypeDAO extends AbstractDAOCommonInfoType<RunType>{

	protected RunTypeDAO() {
		super("run_type", RunType.class, RunTypeMappingQuery.class, 
				"SELECT distinct c.id, c.nb_lanes, c.fk_common_info_type, c.fk_run_category ",
						"FROM run_type as c "+sqlCommonInfoType, false);
	}

	@Override
	public long save(RunType runType) throws DAOException {
		if(null == runType){
			throw new DAOException("RunType is mandatory");
		}
		
		//Check if category exist
		if(runType.category == null || runType.category.id == null){
			throw new DAOException("RunCategory is not present !!");
		}
		//Check if criteria exist
		if (null == runType.criterias || runType.criterias.size()==0) {
			throw new DAOException("Criteria doesn't exist or criteria.id is null) !! - "+runType.code);
		}
		
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		runType.id = commonInfoTypeDAO.save(runType);
		
		//Create new runType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", runType.id);
		parameters.put("nb_lanes", runType.nbLanes);
		parameters.put("fk_common_info_type", runType.id);
		parameters.put("fk_run_category", runType.category.id);
		
		jdbcInsert.execute(parameters);
		
		//Add criterias
		insertValidationCriterias(runType.criterias, runType.id, false);
		
		return runType.id;
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
					parameters.put("fk_run_type", id);
					parameters.put("fk_validation_criteria", criteria.id);
					// set the table name to the name of the link table
					 SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
			         .withTableName("validation_criteria_run");
					jdbcInsert.execute(parameters);
				}				
			}
		} else {
			throw new DAOException("criterias null or empty");
		}
		
	}
	
	private void removeValidationCriterias(Long id)  throws DAOException {
		String sql = "DELETE FROM validation_criteria_run WHERE fk_run_type=?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public void update(RunType runType) throws DAOException {
		//Update criterias
		insertValidationCriterias(runType.criterias, runType.id, true);
		
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(runType);
	}

	@Override
	public void remove(RunType runType) throws DAOException {
		//Remove criterias for this runType
		removeValidationCriterias(runType.id);
		//Remove runType
		super.remove(runType);
		//Remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(runType);
	}
}
