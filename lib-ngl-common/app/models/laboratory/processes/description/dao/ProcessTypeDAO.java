package models.laboratory.processes.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessType;
import models.utils.ListObject;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import play.Logger;
import play.api.modules.spring.Spring;

@Repository
public class ProcessTypeDAO extends AbstractDAOCommonInfoType<ProcessType>{

	protected ProcessTypeDAO() {
		super("process_type", ProcessType.class, ProcessTypeMappingQuery.class, 
				"SELECT distinct c.id, c.fk_common_info_type, c.fk_process_category, c.fk_void_experiment_type, c.fk_first_experiment_type, c.fk_last_experiment_type ",
						"FROM process_type as c  "+sqlCommonInfoType, false);
	}
	
	public List<ProcessType> findByProcessCategoryCode(String processCategoryCode){	
		try {
			String sql = sqlCommonSelect + ",t.name, t.code " + sqlCommonFrom + ", process_category as pc WHERE c.fk_process_category=pc.id AND pc.code=?";
			BeanPropertyRowMapper<ProcessType> mapper = new BeanPropertyRowMapper<ProcessType>(entityClass);
			return this.jdbcTemplate.query(sql, mapper, processCategoryCode);
		} catch (DataAccessException e) {
			Logger.warn(e.getMessage());
			return null;
		}
	}
	
	@Override
	public long save(ProcessType processType) throws DAOException
	{
		
		if(null == processType){
			throw new DAOException("ProcessType is mandatory");
		}
		//Check if category exist
		if(processType.category == null || processType.category.id == null){
			throw new DAOException("ProcessCategory is not present !!");
		}
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		processType.id = commonInfoTypeDAO.save(processType);		
		//Create new processType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", processType.id);
		parameters.put("fk_common_info_type", processType.id);
		parameters.put("fk_process_category", processType.category.id);
		
		
		if(processType.voidExperimentType == null || processType.voidExperimentType.id == null ){
			throw new DAOException("VoidExperimentType is not present !!");
		}
				
		parameters.put("fk_void_experiment_type", processType.voidExperimentType.id);
		
		if(processType.firstExperimentType == null || processType.firstExperimentType.id == null ){
			throw new DAOException("FirstExperimentType is not present !!");
		}
		
		parameters.put("fk_first_experiment_type", processType.firstExperimentType.id);
		
		if(processType.lastExperimentType == null || processType.lastExperimentType.id == null ){
			throw new DAOException("LastExperimentType is not present !!");
		}
		
		parameters.put("fk_last_experiment_type", processType.lastExperimentType.id);
		
		jdbcInsert.execute(parameters);

		if(processType.experimentTypes == null || processType.experimentTypes.size() == 0 ){
			throw new DAOException("ExperimentTypes is not present !!");
		}
		
		//Add list experimentType
		insertExperimentTypes(processType.experimentTypes, processType.id, false);
		return processType.id;
	}

	@Override
	public void update(ProcessType processType) throws DAOException
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(processType);
		if(processType.voidExperimentType == null || processType.voidExperimentType.id == null ){
			throw new DAOException("VoidExperimentType is not present !!");
		}
		
		if(processType.firstExperimentType == null || processType.firstExperimentType.id == null ){
			throw new DAOException("FirstExperimentType is not present !!");
		}
		
		if(processType.lastExperimentType == null || processType.lastExperimentType.id == null ){
			throw new DAOException("LastExperimentType is not present !!");
		}
		
		
		if(processType.experimentTypes == null || processType.experimentTypes.size() == 0 ){
			throw new DAOException("ExperimentTypes is not present !!");
		}
		
		String sql = "update process_experiment_type set fk_first_experiment_type = ?, fk_last_experiment_type = ?, fk_void_experiment_type = ? where id = ?";
		jdbcTemplate.update(sql, processType.firstExperimentType.id, processType.lastExperimentType.id, processType.voidExperimentType.id, processType.id);
		insertExperimentTypes(processType.experimentTypes, processType.id, true);
	}

	private void insertExperimentTypes(
			List<ExperimentType> experimentTypes, Long id, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeExperimentTypes(id);
		}
		//Add resolutions list		
		if(experimentTypes!=null && experimentTypes.size()>0){
			String sql = "INSERT INTO process_experiment_type(fk_process_type, fk_experiment_type) VALUES(?,?)";
			for(ExperimentType experimentType:experimentTypes){
				if(experimentType == null || experimentType.id == null ){
					throw new DAOException("experimentType is mandatory");
				}
				jdbcTemplate.update(sql, id, experimentType.id);
			}
		}		
	}
	
	private void removeExperimentTypes(Long id) {
		String sql = "DELETE FROM process_experiment_type WHERE fk_process_type=?";
		jdbcTemplate.update(sql, id);
		
	}
	
	@Override
	public void remove(ProcessType processType) throws DAOException {
		//Remove process_experiment_type
		removeExperimentTypes(processType.id);
		//Remove processType
		super.remove(processType);

		//Remove CommonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(processType);
	}
}
