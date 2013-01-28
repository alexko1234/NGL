package models.laboratory.experiment.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;

import org.springframework.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;


@Repository
public class ExperimentTypeDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	private DataSource dataSource;
	private NextExperimentTypeMappingQuery nextExperimentTypeMappingQuery;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);  
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("experiment_type").usingGeneratedKeyColumns("id");
		this.nextExperimentTypeMappingQuery=new NextExperimentTypeMappingQuery(dataSource);
	}

	public ExperimentType findByCommonInfoType(long idCommonInfoType)
	{
		String sql = "SELECT id, fk_common_info_type "+
				"FROM experiment_type "+
				"WHERE fk_common_info_type = ? ";
		ExperimentTypeMappingQuery experimentTypeMappingQuery = new ExperimentTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return experimentTypeMappingQuery.findObject(idCommonInfoType);
	}

	public List<ExperimentType> findNextExperiments(long idExperimentType)
	{
		return this.nextExperimentTypeMappingQuery.execute(idExperimentType);
	}

	public ExperimentType findById(long id){
		String sql = "SELECT id, fk_common_info_type "+
				"FROM experiment_type "+
				"WHERE id=?";
		ExperimentTypeMappingQuery experimentTypeMappingQuery = new ExperimentTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return experimentTypeMappingQuery.findObject(id);
	}

	public List<ExperimentType> findAll()
	{
		String sql = "SELECT id, fk_common_info_type "+
				"FROM experiment_type";
		ExperimentTypeMappingQuery experimentTypeMappingQuery = new ExperimentTypeMappingQuery(dataSource, sql, null);
		return experimentTypeMappingQuery.execute();
	}

	public ExperimentType findByCode(String code)
	{
		String sql = "SELECT et.id, fk_common_info_type "+
				"FROM experiment_type as et JOIN common_info_type as c ON c.id=fk_common_info_type "+
				"WHERE code=?";
		ExperimentTypeMappingQuery experimentTypeMappingQuery = new ExperimentTypeMappingQuery(dataSource, sql, new SqlParameter("code",Types.VARCHAR));
		return experimentTypeMappingQuery.findObject(code);
	}

	public ExperimentType add(ExperimentType experimentType)
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType cit = commonInfoTypeDAO.add(experimentType);
		experimentType.setCommonInfoType(cit);
		//Create experimentType 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fk_common_info_type", cit.id);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		experimentType.id = newId;
		//Add nextExperimentTypes list
		List<ExperimentType> nextExpTypes = experimentType.nextExperimentTypes;
		if(nextExpTypes!=null && nextExpTypes.size()>0){
			String sql = "INSERT INTO next_experiment_types(fk_experiment_type,fk_next_experiment_type) VALUES(?,?)";
			for(ExperimentType expType : nextExpTypes){
				jdbcTemplate.update(sql, experimentType.id, expType.id);
			}
		}
		//Add protocols list
		List<Protocol> protocols = experimentType.protocols;
		if(protocols!=null && protocols.size()>0){
			ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
			for(Protocol protocol : protocols){
				protocolDAO.add(protocol, experimentType.id);
			}

		}
		//Add InstrumentUsedTypes list
		List<InstrumentUsedType> instrumentUsedTypes = experimentType.instrumentTypes;
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
			String sql = "INSERT INTO experiment_type_instrument_type(fk_experiment_type,fk_instrument_type) VALUES(?,?)";
			for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
				if(instrumentUsedType.id==null)
					instrumentUsedType = instrumentUsedTypeDAO.add(instrumentUsedType);
				jdbcTemplate.update(sql, experimentType.id, instrumentUsedType.id);
			}
		}
		return experimentType;
	}

	public void update(ExperimentType experimentType)
	{
		ExperimentType expTypeDB = findById(experimentType.id);

		//Update commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(experimentType);

		//Update nexExperiment list (add new)
		List<ExperimentType> nextExpTypes = experimentType.nextExperimentTypes;
		System.out.println("Next experiment db "+expTypeDB.nextExperimentTypes);
		for(ExperimentType nextExpTypeDB : expTypeDB.nextExperimentTypes){
			System.out.println(nextExpTypeDB.id);
		}
		if(nextExpTypes!=null && nextExpTypes.size()>0){
			String sql = "INSERT INTO next_experiment_types(fk_experiment_type,fk_next_experiment_type) VALUES(?,?)";
			for(ExperimentType expType : nextExpTypes){
				if(expTypeDB.nextExperimentTypes==null || (expTypeDB.nextExperimentTypes!=null && !expTypeDB.nextExperimentTypes.contains(expType))){
					play.Logger.debug("Add next experiment types "+expType.id);
					jdbcTemplate.update(sql, experimentType.id, expType.id);
				}
			}
		}
		//Update protocols list (add new)
		List<Protocol> protocols = experimentType.protocols;
		if(protocols!=null && protocols.size()>0){
			ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
			for(Protocol protocol : protocols){
				if(expTypeDB.protocols==null || (expTypeDB.protocols!=null && !expTypeDB.protocols.contains(protocol)))
					protocolDAO.add(protocol, experimentType.id);
			}
		}
		//Update InstrumentUsedTypes list
		List<InstrumentUsedType> instrumentUsedTypes = experimentType.instrumentTypes;
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
			String sql = "INSERT experiment_type_instrument_type(fk_experiment_type, fk_instrument_type) VALUES(?,?)";
			for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
				if(expTypeDB.instrumentTypes==null || (expTypeDB.instrumentTypes!=null && !expTypeDB.instrumentTypes.contains(instrumentUsedType))){
					if(instrumentUsedType.id==null)
						instrumentUsedType=instrumentUsedTypeDAO.add(instrumentUsedType);
					jdbcTemplate.update(sql, experimentType.id, instrumentUsedType.id);
				}
			}
		}
	}
}
