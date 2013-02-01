package models.laboratory.experiment.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.AbstractExperiment;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import play.modules.spring.Spring;

public abstract class AbstractExperimentDAO<P extends AbstractExperiment> {


	protected SimpleJdbcTemplate jdbcTemplate;
	protected SimpleJdbcInsert jdbcInsert;
	protected DataSource dataSource;
	protected String tableName;

	public AbstractExperimentDAO(String tableName)
	{
		this.tableName=tableName;
	}
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);  
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName);
	}

	public abstract P findById(long id);

	public P add(P experiment)
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType cit = commonInfoTypeDAO.add(experiment);
		experiment.setCommonInfoType(cit);
		//Create purification method 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", cit.id);
		parameters.put("fk_common_info_type", cit.id);
		jdbcInsert.execute(parameters);
		experiment.id = cit.id;
		//Add list protocols
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		protocolDAO.add(experiment.protocols, experiment.id);
		//Add list instruments
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		instrumentUsedTypeDAO.add(experiment.instrumentUsedTypes, experiment.id);
		return experiment;
	}

	public void update(P experiment)
	{
		//Update commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(experiment);
		P experimentDB = findById(experiment.id);

		//Update protocols list (add new)
		List<Protocol> protocols = experiment.protocols;
		if(protocols!=null && protocols.size()>0){
			ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
			for(Protocol protocol : protocols){
				if(experimentDB.protocols==null || (experimentDB.protocols!=null && !experimentDB.protocols.contains(protocol)))
					protocolDAO.add(protocol, experiment.id);
			}
		}
		//Update InstrumentUsedTypes list
		List<InstrumentUsedType> instrumentUsedTypes = experiment.instrumentUsedTypes;
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
			for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
				if(experimentDB.instrumentUsedTypes==null || (experimentDB.instrumentUsedTypes!=null && !experimentDB.instrumentUsedTypes.contains(instrumentUsedType))){
					instrumentUsedTypeDAO.add(instrumentUsedType, experiment.id);
				}
			}
		}
	}
	
}
