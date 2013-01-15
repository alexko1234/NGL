package models.description.experiment.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.description.common.CommonInfoType;
import models.description.common.dao.CommonInfoTypeDAO;
import models.description.experiment.Instrument;
import models.description.experiment.InstrumentUsedType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class InstrumentUsedTypeDAO {

	private SimpleJdbcInsert jdbcInsert;
	private DataSource dataSource;
	private String sqlCommon = "SELECT id, fk_common_info_type, fk_instrument_category "+
			"FROM instrument_used_type ";
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("instrument_used_type").usingGeneratedKeyColumns("id");
	}

	public List<InstrumentUsedType> findByExperimentType(long idExperimentType)
	{
		String sql = sqlCommon+
				"JOIN experiment_type_instrument_type ON fk_instrument_type=id "+
				"WHERE fk_experiment_type = ? ";
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sql,false);
		return instrumentUsedTypeMappingQuery.execute(idExperimentType);
	}

	public InstrumentUsedType findById(long id)
	{
		String sql = sqlCommon+
				"WHERE id = ? ";
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sql,false);
		return instrumentUsedTypeMappingQuery.findObject(id);
	}

	public InstrumentUsedType findByCommonInfoType(long idCommonInfoType)
	{
		String sql = sqlCommon+
				"WHERE fk_common_info_type = ? ";
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sql,false);
		return instrumentUsedTypeMappingQuery.findObject(idCommonInfoType);
	}

	public List<InstrumentUsedType> findAll()
	{
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sqlCommon,true);
		return instrumentUsedTypeMappingQuery.execute();
	}

	public InstrumentUsedType add(InstrumentUsedType instrumentUsedType)
	{
		//Check if commonInfoType exist
		if(instrumentUsedType.getCommonInfoType()!=null && instrumentUsedType.getCommonInfoType().getId()==null)
		{
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			CommonInfoType cit = commonInfoTypeDAO.add(instrumentUsedType.getCommonInfoType());
			instrumentUsedType.setCommonInfoType(cit);
		}
		//Check if instrumentCategory exist
		if(instrumentUsedType.getInstrumentCategory()!=null && instrumentUsedType.getInstrumentCategory().getId()==null)
		{
			
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			CommonInfoType cit = commonInfoTypeDAO.add(instrumentUsedType.getCommonInfoType());
			instrumentUsedType.setCommonInfoType(cit);
		}
		//Create new InstrumentUsedType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fk_common_info_type", instrumentUsedType.getCommonInfoType().getId());
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		instrumentUsedType.setId(newId);

		//Add instruments list
		List<Instrument> instruments = instrumentUsedType.getInstruments();
		if(instruments!=null && instruments.size()>0){
			InstrumentDAO instrumentDAO = Spring.getBeanOfType(InstrumentDAO.class);
			for(Instrument instrument : instruments){
				instrumentDAO.add(instrument, instrumentUsedType.getId());
			}
		}
		return instrumentUsedType;
	}

	public void update(InstrumentUsedType instrumentUsedType) 
	{
		InstrumentUsedType instrumentUsedTypeDB = findById(instrumentUsedType.getId());

		//Update commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(instrumentUsedType.getCommonInfoType());

		//Update instrument list
		List<Instrument> instruments = instrumentUsedType.getInstruments();
		if(instruments!=null && instruments.size()>0){
			InstrumentDAO instrumentDAO = Spring.getBeanOfType(InstrumentDAO.class);
			for(Instrument instrument : instruments){
				if(instrumentUsedTypeDB==null || (instrumentUsedTypeDB!=null && !instrumentUsedTypeDB.getInstruments().contains(instrument))){
					instrumentDAO.add(instrument, instrumentUsedTypeDB.getId());
				}
			}
		}

	}


}
