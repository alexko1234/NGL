package models.laboratory.experiment.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.AbstractExperiment;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

public abstract class AbstractExperimentDAO<P extends AbstractExperiment> extends AbstractDAOMapping<P>{


	public AbstractExperimentDAO(String tableName,Class<P> entityClass, Class<? extends MappingSqlQuery<P>> classMapping, String sqlCommon)
	{
		super(tableName, entityClass, classMapping, sqlCommon,false);

	}

	public long save(P experiment) throws DAOException
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		experiment.id = commonInfoTypeDAO.save(experiment);
		//Create purification method 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", experiment.id);
		parameters.put("fk_common_info_type", experiment.id);
		jdbcInsert.execute(parameters);
		//Add list protocols
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		protocolDAO.save(experiment.protocols, experiment.id);
		//Add list instruments
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		instrumentUsedTypeDAO.save(experiment.instrumentUsedTypes, experiment.id);
		return experiment.id;
	}

	public void update(P experiment) throws DAOException
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
					protocolDAO.save(protocol, experiment.id);
			}
		}
		//Update InstrumentUsedTypes list
		List<InstrumentUsedType> instrumentUsedTypes = experiment.instrumentUsedTypes;
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
			for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
				if(experimentDB.instrumentUsedTypes==null || (experimentDB.instrumentUsedTypes!=null && !experimentDB.instrumentUsedTypes.contains(instrumentUsedType))){
					instrumentUsedTypeDAO.save(instrumentUsedType, experiment.id);
				}
			}
		}
	}

	@Override
	public void remove(P experiment) {
		//Remove protocol common_info_type_protocol
		String sqlProto = "DELETE FROM common_info_type_protocol WHERE fk_common_info_type=?";
		jdbcTemplate.update(sqlProto, experiment.id);
		//Remove instrument type common_info_type_instrument_type
		String sqlInstru = "DELETE FROM common_info_type_instrument_type WHERE fk_common_info_type=?";
		jdbcTemplate.update(sqlInstru, experiment.id);
		//Remove experiment
		super.remove(experiment);
		//Remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(experiment);
	}

}
