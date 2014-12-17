package models.laboratory.experiment.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.ListObject;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;


@Repository
public class ExperimentTypeDAO extends AbstractDAOCommonInfoType<ExperimentType>{

	public ExperimentTypeDAO() {
		super("experiment_type", ExperimentType.class,ExperimentTypeMappingQuery.class,
				"SELECT distinct c.id, c.fk_experiment_category, c.fk_common_info_type, c.atomic_transfert_method ",
				"FROM experiment_type as c "+ sqlCommonInfoType, false);
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

	public List<ExperimentType> findByProcessTypeId(long id) throws DAOException
	{
		String sql = sqlCommon + "inner join process_experiment_type as p ON p.fk_experiment_type=c.id "+
				"where p.fk_process_type = ?";
		return initializeMapping(sql, new SqlParameter("p.fk_process_type", Type.LONG)).execute(id);		
	}

	public List<String> findVoidProcessExperimentTypeCode(String processTypeCode){
		String query = "SELECT distinct t.code "+sqlCommonFrom+
				"inner join process_type as p on p.fk_void_experiment_type = t.id " +
				"inner join common_info_type as cp on p.fk_common_info_type=cp.id "+
				" where cp.code=?";

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

		String sql = sqlCommon+" inner join experiment_type_node as n on n.fk_experiment_type = t.id"+
				" inner join previous_nodes as p on p.fk_previous_node = n.id "+
				" inner join experiment_type_node as np on np.id = p.fk_node "+
				" inner join  common_info_type as cp on cp.id = np.fk_experiment_type "+
				" where cp.code=?";
		return initializeMapping(sql, new SqlParameter("cp.code", Types.VARCHAR)).execute(code);
	}
	
	public List<ExperimentType> findNextExperimentTypeForAnExperimentTypeCode(String code) throws DAOException{

		String sql = sqlCommon+" inner join experiment_type_node as n on n.fk_experiment_type = t.id"+
				" inner join previous_nodes as p on p.fk_previous_node = n.id "+
				" inner join experiment_type_node as np on np.id = p.fk_node "+
				" inner join  common_info_type as cp on cp.id = np.fk_experiment_type "+
				" where t.code=?";
		return initializeMapping(sql, new SqlParameter("t.code", Types.VARCHAR)).execute(code);
	}

	public List<ExperimentType> findSatelliteExperimentByNodeId(Long id) throws DAOException {

		String sql = sqlCommon + "inner join satellite_experiment_type as s ON s.fk_experiment_type=t.id "+
				"and s.fk_experiment_type_node = ?";
		return initializeMapping(sql, new SqlParameter("p.fk_process_type", Type.LONG)).execute(id);
	}

	public List<ExperimentType> findByCategoryCode(String categoryCode){
		String sql = "SELECT t.code AS code, t.name AS name "+
				 sqlCommonFrom+
				" JOIN experiment_category as ec  ON c.fk_experiment_category=ec.id "
				+"where ec.code=?";
		BeanPropertyRowMapper<ExperimentType> mapper = new BeanPropertyRowMapper<ExperimentType>(ExperimentType.class);
		return this.jdbcTemplate.query(sql, mapper, categoryCode);
	}
	
	public List<ExperimentType> findByCategoryCodeAndProcessTypeCode(String categoryCode, String processTypeCode){
		String sql = "SELECT t.code AS code, t.name AS name "+
				 sqlCommonFrom+
				" JOIN experiment_category as ec  ON c.fk_experiment_category=ec.id inner join process_experiment_type as p ON p.fk_experiment_type=c.id, process_type as pt " +
				"inner join common_info_type as cp on pt.fk_common_info_type=cp.id"
				+" where ec.code=? and p.fk_process_type = pt.id and cp.code=?";
		BeanPropertyRowMapper<ExperimentType> mapper = new BeanPropertyRowMapper<ExperimentType>(ExperimentType.class);
		return this.jdbcTemplate.query(sql, mapper, categoryCode, processTypeCode);
	}
	
	public List<ExperimentType> findByCategoryCodeWithoutOneToVoid(String categoryCode){
		String sql = "SELECT t.code AS code, t.name AS name, t.display_order AS displayOrder  "
					+sqlCommonFrom
					+" JOIN experiment_category as ec  ON c.fk_experiment_category=ec.id "
					+"where ec.code=? and c.atomic_transfert_method!='OneToVoid' "
					+"ORDER by t.display_order, t.name";
		BeanPropertyRowMapper<ExperimentType> mapper = new BeanPropertyRowMapper<ExperimentType>(ExperimentType.class);
		return this.jdbcTemplate.query(sql, mapper, categoryCode);
	}


}
