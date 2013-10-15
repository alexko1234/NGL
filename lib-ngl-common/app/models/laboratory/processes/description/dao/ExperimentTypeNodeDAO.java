package models.laboratory.processes.description.dao;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;


import org.springframework.stereotype.Repository;
@Repository
public class ExperimentTypeNodeDAO  extends AbstractDAOMapping<ExperimentTypeNode>{

	public ExperimentTypeNodeDAO() {
		super("experiment_type_node", ExperimentTypeNode.class, ExperimentTypeNodeMappingQuery.class,
				"SELECT t.id, t.code, t.doPurification, t.mandatoryPurification, t.doQualityControl, t.mandatoryQualityControl," +
				"t.fk_experiment_type FROM experiment_type_node as t", true);
	}

	@Override
	public long save(ExperimentTypeNode value) throws DAOException {
		if(null == value){
			throw new DAOException("ExperimentTypeNode is mandatory");
		}
		if(null == value.experimentType || null == value.experimentType.id){
			throw new DAOException("ExperimentType is mandatory");
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", value.code);
		parameters.put("doPurification", value.doPurification);
		parameters.put("mandatoryPurification", value.mandatoryPurification);
		parameters.put("doQualityControl", value.doQualityControl);
		parameters.put("mandatoryQualityControl", value.mandatoryQualityControl);
		parameters.put("fk_experiment_type", value.experimentType.id);
		value.id = (Long) jdbcInsert.executeAndReturnKey(parameters);

		List<ExperimentType> experimentTypes = new ArrayList<ExperimentType>();
		if(null != value.possibleQualityControlTypes){
			experimentTypes.addAll(value.possibleQualityControlTypes);

		}
		if(null != value.possiblePurificationTypes){
			experimentTypes.addAll(value.possiblePurificationTypes);
		}
		if(experimentTypes.size() > 0){
			insertSatellites(experimentTypes, value.id, false);
		}

		if(value.previousExperimentType != null && value.previousExperimentType.size() > 0){
			insertPrevious(value.previousExperimentType, value.id, false);
		}
		return value.id;
	}

	@Override
	public List<ExperimentTypeNode> findAll() throws DAOException {
		return initializeMapping(sqlCommon + " order by id DESC").execute();
	}

	private void insertPrevious(
			List<ExperimentTypeNode> previousExperimentType, Long id, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removePrevious(id);
		}
		//Add resolutions list
		if(previousExperimentType!=null && previousExperimentType.size()>0){
			String sql = "INSERT INTO previous_nodes(fk_node, fk_previous_node) VALUES(?,?)";
			for(ExperimentTypeNode experimentTypeNode:previousExperimentType){
				if(experimentTypeNode == null || experimentTypeNode.id == null ){
					throw new DAOException("experimentTypeNode is mandatory");
				}
				jdbcTemplate.update(sql, id, experimentTypeNode.id);
			}
		}

	}

	private void insertSatellites(
			List<ExperimentType> experimentTypes, Long id, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeSatellites(id);
		}
		//Add resolutions list
		if(experimentTypes!=null && experimentTypes.size()>0){
			String sql = "INSERT INTO satellite_experiment_type(fk_experiment_type_node, fk_experiment_type) VALUES(?,?)";
			for(ExperimentType experimentType:experimentTypes){
				if(experimentType == null || experimentType.id == null ){
					throw new DAOException("experimentType is mandatory");
				}
				jdbcTemplate.update(sql, id, experimentType.id);
			}
		}

	}

	private void removeSatellites(Long id) {
		String sql = "DELETE FROM satellite_experiment_type WHERE fk_experiment_type_node=?";
		jdbcTemplate.update(sql, id);

	}

	private void removePrevious(Long id) {
		String sql = "DELETE FROM previous_nodes WHERE fk_node=?";
		jdbcTemplate.update(sql, id);

	}

	@Override
	public void update(ExperimentTypeNode value) throws DAOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(ExperimentTypeNode value) throws DAOException {
		removeSatellites(value.id);
		removePrevious(value.id);
		super.remove(value);
	}

}
