package models.laboratory.processus.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.processus.description.ProcessType;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class ProcessTypeDAO extends AbstractDAOMapping<ProcessType>{

	protected ProcessTypeDAO() {
		super("process_type", ProcessType.class, ProcessTypeMappingQuery.class, 
				"SELECT t.id, fk_common_info_type, fk_process_category "+
				"FROM process_type as t  "+
				"JOIN common_info_type as c ON c.id=fk_common_info_type ", false);
	}

	public long save(ProcessType processType) throws DAOException
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		processType.id = commonInfoTypeDAO.save(processType);
		//Check if category exist
		if(processType.processCategory!=null && processType.processCategory.id==null)
		{
			ProcessCategoryDAO processCategoryDAO = Spring.getBeanOfType(ProcessCategoryDAO.class);
			processType.processCategory.id = processCategoryDAO.save(processType.processCategory);
		}

		//Create new processType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", processType.id);
		parameters.put("fk_common_info_type", processType.id);
		parameters.put("fk_process_category", processType.processCategory.id);
		jdbcInsert.execute(parameters);

		//Add list experimentType
		List<ExperimentType> experimentTypes = processType.experimentTypes;
		if(experimentTypes!=null && experimentTypes.size()>0){
			ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
			String sql = "INSERT INTO process_experiment_type(fk_process_type, fk_experiment_type) VALUES(?,?)";
			for(ExperimentType experimentType : experimentTypes){
				if(experimentType.id==null)
					experimentType.id = experimentTypeDAO.save(experimentType);
				jdbcTemplate.update(sql, processType.id, experimentType.id);
			}
		}
		return processType.id;
	}

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
					if(experimentType.id==null)
						experimentType.id = experimentTypeDAO.save(experimentType);
					jdbcTemplate.update(sql, processType.id, experimentType.id);
				}
			}
		}
	}

	@Override
	public void remove(ProcessType processType) {
		//Remove CommonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(processType);
		//Remove process_experiment_type
		String sqlExp = "DELETE FROM process_experiment_type WHERE fk_process_type=?";
		jdbcTemplate.update(sqlExp, processType.id);
		//Remove processType
		super.remove(processType);
	}
}
