package models.laboratory.experiment.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.asm.Type;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

import models.utils.ListObject;


@Repository
public class ExperimentTypeDAO extends AbstractDAOMapping<ExperimentType>{

	public ExperimentTypeDAO() {
		super("experiment_type", ExperimentType.class,ExperimentTypeMappingQuery.class,
				"SELECT t.id, t.fk_experiment_category, t.fk_common_info_type, t.atomic_transfert_method "+
						"FROM experiment_type as t "+
				"JOIN common_info_type as c ON c.id=t.fk_common_info_type ", false);
	}
	
	@Override
	public long save(ExperimentType experimentType) throws DAOException
	{
		
		if(null == experimentType){
			throw new DAOException("ExperimentType is mandatory");
		}
		//Check if category exist
		if(experimentType.category == null || experimentType.category.id == null){
			throw new DAOException("ExperimentCategory is not present !!");
		}
		
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		experimentType.id = commonInfoTypeDAO.save(experimentType);

		//Create experimentType 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", experimentType.id);
		parameters.put("fk_common_info_type", experimentType.id);
		parameters.put("fk_experiment_category", experimentType.category.id);
		parameters.put("atomic_transfert_method", experimentType.atomicTransfertMethod);
		jdbcInsert.execute(parameters);

		//Add list protocols
		insertProtocols(experimentType.protocols, experimentType.id, false);
		//Add list instruments
		insertInstrumentUsedTypes(experimentType.instrumentUsedTypes, experimentType.id, false);
		
		return experimentType.id;
	}

	private void insertInstrumentUsedTypes(
			List<InstrumentUsedType> instrumentUsedTypes, Long id, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeInstrumentUsedTypes(id);
		}
		//Add resolutions list		
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			String sql = "INSERT INTO experiment_type_instrument_type (fk_experiment_type, fk_instrument_used_type) VALUES(?,?)";
			for(InstrumentUsedType instrumentUsedType:instrumentUsedTypes){
				if(instrumentUsedType == null || instrumentUsedType.id == null ){
					throw new DAOException("instrumentUsedType is mandatory");
				}
				jdbcTemplate.update(sql, id,instrumentUsedType.id);
			}
		}		
	}


	private void insertProtocols(List<Protocol> protocols, Long id, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeProtocols(id);
		}
		//Add resolutions list		
		if(protocols!=null && protocols.size()>0){
			String sql = "INSERT INTO experiment_type_protocol (fk_experiment_type, fk_protocol) VALUES(?,?)";
			for(Protocol protocol:protocols){
				if(protocol == null || protocol.id == null ){
					throw new DAOException("protocol is mandatory");
				}
				jdbcTemplate.update(sql, id,protocol.id);
			}
		}
		
	}


	private void removeProtocols(Long id) {
		String sql = "DELETE FROM experiment_type_protocol WHERE fk_experiment_type=?";
		jdbcTemplate.update(sql, id);
		
	}
	
	private void removeInstrumentUsedTypes(Long id) {
		String sql = "DELETE FROM experiment_type_instrument_type WHERE fk_experiment_type=?";
		jdbcTemplate.update(sql, id);
		
	}


	@Override
	public void update(ExperimentType experimentType) throws DAOException
	{
		ExperimentType expTypeDB = findById(experimentType.id);
		//Add list protocols
		insertProtocols(experimentType.protocols, experimentType.id, true);
		//Add list instruments
		insertInstrumentUsedTypes(experimentType.instrumentUsedTypes, experimentType.id, true);
	}

	@Override
	public void remove(ExperimentType experimentType) throws DAOException {
		//Remove protocol common_info_type_protocol
		removeProtocols(experimentType.id);
		//Remove instrument type common_info_type_instrument_type
		removeInstrumentUsedTypes(experimentType.id);
		//Remove experiment
		super.remove(experimentType);
		//Remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(experimentType);
	}


	public List<ListObject> findAllForList(){
		String sql = "SELECT c.code AS code, c.name AS name "+
				"FROM experiment_type as et JOIN common_info_type as c ON c.id=et.fk_common_info_type";
		BeanPropertyRowMapper<ListObject> mapper = new BeanPropertyRowMapper<ListObject>(ListObject.class);
		return this.jdbcTemplate.query(sql, mapper);
	}
	
	
	public List<ExperimentType> findByProcessTypeId(long id) throws DAOException
	{
		String sql = sqlCommon + "inner join process_experiment_type as p ON p.fk_experiment_type=t.id "+
				"WHERE p.fk_process_type = ?";
		return initializeMapping(sql, new SqlParameter("p.fk_process_type", Type.LONG)).execute(id);		
	}

	public List<String> findVoidProcessExperimentTypeCode(String processTypeCode){
		String query = "SELECT c.code FROM experiment_type as t " +
				" inner join common_info_type as c ON c.id=t.fk_common_info_type"+  
				" inner join process_type as p on p.fk_void_experiment_type = t.id " +
				" inner join common_info_type as cp on p.id= p.id where cp.code = ?";
		
		List<String> list = jdbcTemplate.query(
				query,
			    new RowMapper<String>() {

			        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	String listObj = rs.getString("code");
			            return listObj;
			        }
			    }, processTypeCode);
		
		return list;
	}
	
	public List<ExperimentType> findPreviousExperimentTypeForAnExperimentTypeCode(String code) throws DAOException{
		String sql = sqlCommon +" inner join experiment_type_node as n on n.fk_experiment_type = t.id"+
                " inner join previous_nodes as p on p.fk_previous_node = n.id"+
                " inner join experiment_type_node as np on np.id = p.fk_node"+
                " inner join  common_info_type as cp on cp.id = np.fk_experiment_type"+
                " where cp.code = ?";
		return initializeMapping(sql, new SqlParameter("cp.code", Types.VARCHAR)).execute(code);
	}
	
	public List<ExperimentType> findSatelliteExperimentByNodeId(Long id) throws DAOException {
		String sql = sqlCommon + "inner join satellite_experiment_type as s ON s.fk_experiment_type=t.id "+
				"WHERE s.fk_experiment_type_node = ?";
		return initializeMapping(sql, new SqlParameter("p.fk_process_type", Type.LONG)).execute(id);
	}

}
