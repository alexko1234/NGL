package models.description.experiment.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.description.common.CommonInfoType;
import models.description.common.dao.CommonInfoTypeDAO;
import models.description.experiment.ExperimentType;
import models.description.experiment.InstrumentUsedType;
import models.description.experiment.Protocol;

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
		if(experimentType.getCommonInfoType()!=null && experimentType.getCommonInfoType().getId()==null)
		{
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			CommonInfoType cit = commonInfoTypeDAO.add(experimentType.getCommonInfoType());
			experimentType.setCommonInfoType(cit);
		}
		//Create experimentType 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fk_common_info_type", experimentType.getCommonInfoType().getId());
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		experimentType.setId(newId);
		//Add nextExperimentTypes list
		List<ExperimentType> nextExpTypes = experimentType.getNextExperimentTypes();
		if(nextExpTypes!=null && nextExpTypes.size()>0){
			String sql = "INSERT INTO next_experiment_types(fk_experiment_type,fk_next_experiment_type) VALUES(?,?)";
			for(ExperimentType expType : nextExpTypes){
				jdbcTemplate.update(sql, experimentType.getId(), expType.getId());
			}
		}
		//Add protocols list
		List<Protocol> protocols = experimentType.getProtocols();
		if(protocols!=null && protocols.size()>0){
			ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
			for(Protocol protocol : protocols){
				protocolDAO.add(protocol, experimentType.getId());
			}
			
		}
		//Add InstrumentUsedTypes list
		List<InstrumentUsedType> instrumentUsedTypes = experimentType.getInstrumentTypes();
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
			String sql = "INSERT INTO experiment_type_instrument_type(fk_experiment_type,fk_instrument_type) VALUES(?,?)";
			for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
				if(instrumentUsedType.getId()==null)
					instrumentUsedType = instrumentUsedTypeDAO.add(instrumentUsedType);
				jdbcTemplate.update(sql, experimentType.getId(), instrumentUsedType.getId());
			}
		}
		return experimentType;
	}
	
	public void update(ExperimentType experimentType)
	{
		ExperimentType expTypeDB = findById(experimentType.getId());
		
		//Update commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(experimentType.getCommonInfoType());
		
		//Update nexExperiment list (add new)
		List<ExperimentType> nextExpTypes = experimentType.getNextExperimentTypes();
		System.out.println("Next experiment db "+expTypeDB.getNextExperimentTypes());
		for(ExperimentType nextExpTypeDB : expTypeDB.getNextExperimentTypes()){
			System.out.println(nextExpTypeDB.getId());
		}
		if(nextExpTypes!=null && nextExpTypes.size()>0){
			String sql = "INSERT INTO next_experiment_types(fk_experiment_type,fk_next_experiment_type) VALUES(?,?)";
			for(ExperimentType expType : nextExpTypes){
				if(expTypeDB.getNextExperimentTypes()==null || (expTypeDB.getNextExperimentTypes()!=null && !expTypeDB.getNextExperimentTypes().contains(expType))){
					System.out.println("Add next experiment types "+expType.getId());
					jdbcTemplate.update(sql, experimentType.getId(), expType.getId());
				}
			}
		}
		//Update protocols list (add new)
		List<Protocol> protocols = experimentType.getProtocols();
		if(protocols!=null && protocols.size()>0){
			ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
			for(Protocol protocol : protocols){
				if(expTypeDB.getProtocols()==null || (expTypeDB.getProtocols()!=null && !expTypeDB.getProtocols().contains(protocol)))
					protocolDAO.add(protocol, experimentType.getId());
			}
		}
		//Update InstrumentUsedTypes list
		List<InstrumentUsedType> instrumentUsedTypes = experimentType.getInstrumentTypes();
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
			String sql = "INSERT experiment_type_instrument_type(fk_experiment_type, fk_instrument_type) VALUES(?,?)";
			for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
				if(expTypeDB.getInstrumentTypes()==null || (expTypeDB.getInstrumentTypes()!=null && !expTypeDB.getInstrumentTypes().contains(instrumentUsedType))){
					if(instrumentUsedType.getId()==null)
						instrumentUsedType=instrumentUsedTypeDAO.add(instrumentUsedType);
					jdbcTemplate.update(sql, experimentType.getId(), instrumentUsedType.getId());
				}
			}
		}
	}
}
