package models.laboratory.processus.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.processus.description.ProcessCategory;
import models.laboratory.processus.description.ProcessType;
import models.utils.ListObject;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class ProcessTypeDAO extends AbstractDAOMapping<ProcessType>{

	protected ProcessTypeDAO() {
		super("process_type", ProcessType.class, ProcessTypeMappingQuery.class, 
				"SELECT t.id, fk_common_info_type, fk_process_category, fk_void_experiment_type, fk_first_experiment_type, fk_last_experiment_type "+
						"FROM process_type as t  "+
						"JOIN common_info_type as c ON c.id=fk_common_info_type ", false);
	}

	/**
	 * Return a list of ListObject that help populating the <select> input
	 * @return List<ListObject>
	 */
	public List<ListObject> findAllForList(){
		String sql = "SELECT t.id,c.code as code, c.name as name "+
				"FROM process_type as t  "+
				"JOIN common_info_type as c ON c.id=fk_common_info_type";
		
		BeanPropertyRowMapper<ListObject> mapper = new BeanPropertyRowMapper<ListObject>(ListObject.class);
		return this.jdbcTemplate.query(sql, mapper);
	}
	
	@Override
	public long save(ProcessType processType) throws DAOException
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		processType.id = commonInfoTypeDAO.save(processType);
		//Check if category exist
		if(processType.processCategory!=null){
			ProcessCategory processCategoryDB = ProcessCategory.find.findByCode(processType.processCategory.code);
			if(processCategoryDB ==null){
				ProcessCategoryDAO processCategoryDAO = Spring.getBeanOfType(ProcessCategoryDAO.class);
				processType.processCategory.id = processCategoryDAO.save(processType.processCategory);
			}else
				processType.processCategory=processCategoryDB;
		}
		//Create new processType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", processType.id);
		parameters.put("fk_common_info_type", processType.id);
		parameters.put("fk_process_category", processType.processCategory.id);
		
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		
		//Save void experiment type
		if(processType.voidExperimentType!=null){
			ExperimentType voidExpTypeDB = ExperimentType.find.findByCode(processType.voidExperimentType.code);
			if(voidExpTypeDB==null){
				processType.voidExperimentType.id = experimentTypeDAO.save(processType.voidExperimentType);
			}else
				processType.voidExperimentType = voidExpTypeDB;
		}
		parameters.put("fk_void_experiment_type", processType.voidExperimentType.id);
		
		//Save first experiment type
		if(processType.firstExperimentType!=null){
			ExperimentType firstExpTypeDB = ExperimentType.find.findByCode(processType.firstExperimentType.code);
			if(firstExpTypeDB==null){
				processType.firstExperimentType.id = experimentTypeDAO.save(processType.firstExperimentType);
			}else
				processType.firstExperimentType = firstExpTypeDB;
		}
		parameters.put("fk_first_experiment_type", processType.firstExperimentType.id);
		
		if(processType.lastExperimentType!=null){
			ExperimentType lastExpTypeDB = ExperimentType.find.findByCode(processType.lastExperimentType.code);
			if(lastExpTypeDB==null){
				processType.lastExperimentType.id = experimentTypeDAO.save(processType.lastExperimentType);
			}else
				processType.lastExperimentType = lastExpTypeDB;
		}
		parameters.put("fk_last_experiment_type", processType.lastExperimentType.id);
		
		
		jdbcInsert.execute(parameters);

		//Add list experimentType
		List<ExperimentType> experimentTypes = processType.experimentTypes;
		if(experimentTypes!=null && experimentTypes.size()>0){
			String sql = "INSERT INTO process_experiment_type(fk_process_type, fk_experiment_type) VALUES(?,?)";
			for(ExperimentType experimentType : experimentTypes){
				ExperimentType experimentTypeDB = ExperimentType.find.findByCode(experimentType.code);
				if(experimentTypeDB ==null)
					experimentType.id = experimentTypeDAO.save(experimentType);
				else
					experimentType=experimentTypeDB;
				jdbcTemplate.update(sql, processType.id, experimentType.id);
			}
		}
		return processType.id;
	}

	@Override
	public void update(ProcessType processType) throws DAOException
	{
		ProcessType processTypeDB = findById(processType.id);
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(processType);

		//Update InstrumentUsedTypes list
		List<ExperimentType> experimentTypes = processType.experimentTypes;
		if(experimentTypes!=null && experimentTypes.size()>0){
			ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
			String sql = "INSERT INTO process_experiment_type(fk_process_type, fk_experiment_type) VALUES(?,?)";
			for(ExperimentType experimentType : experimentTypes){
				if(processTypeDB.experimentTypes==null || (processTypeDB.experimentTypes!=null && !processTypeDB.experimentTypes.contains(experimentType))){
					ExperimentType experimentTypeDB = ExperimentType.find.findByCode(experimentType.code);
					if(experimentTypeDB ==null)
						experimentType.id = experimentTypeDAO.save(experimentType);
					else
						experimentType=experimentTypeDB;
					jdbcTemplate.update(sql, processType.id, experimentType.id);
				}
			}
		}
	}

	@Override
	public void remove(ProcessType processType) throws DAOException {
		//Remove process_experiment_type
		String sqlExp = "DELETE FROM process_experiment_type WHERE fk_process_type=?";
		jdbcTemplate.update(sqlExp, processType.id);

		//Remove processType
		super.remove(processType);

		//Remove CommonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(processType);
	}
}
