package models.laboratory.run.description.dao;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.run.description.RunType;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class RunTypeDAO extends AbstractDAOMapping<RunType>{

	protected RunTypeDAO() {
		super("run_type", RunType.class, RunTypeMappingQuery.class, 
				"SELECT t.id, t.nb_lanes, fk_common_info_type, fk_run_category "+
						"FROM run_type as t "+
						"JOIN common_info_type as c ON c.id=t.fk_common_info_type ", false);
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
		return runType.id;
	}

	@Override
	public void update(RunType runType) throws DAOException
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(runType);
	}

	@Override
	public void remove(RunType runType) throws DAOException {
		//Remove runType
		super.remove(runType);
		//Remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(runType);
	}
}
