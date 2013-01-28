package models.laboratory.instrument.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;

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
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType cit = commonInfoTypeDAO.add(instrumentUsedType);
		instrumentUsedType.setCommonInfoType(cit);
		//Check if instrumentCategory exist
		if(instrumentUsedType.instrumentCategory!=null && instrumentUsedType.instrumentCategory.id==null)
		{
			InstrumentCategoryDAO instrumentCategoryDAO = Spring.getBeanOfType(InstrumentCategoryDAO.class);
			InstrumentCategory instrumentCategory = instrumentCategoryDAO.add(instrumentUsedType.instrumentCategory);
			instrumentUsedType.instrumentCategory = instrumentCategory;
		}
		//Create new InstrumentUsedType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fk_common_info_type", instrumentUsedType.getIdCommonInfoType());
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		instrumentUsedType.id = newId;

		//Add instruments list
		List<Instrument> instruments = instrumentUsedType.instruments;
		if(instruments!=null && instruments.size()>0){
			InstrumentDAO instrumentDAO = Spring.getBeanOfType(InstrumentDAO.class);
			for(Instrument instrument : instruments){
				instrumentDAO.add(instrument, instrumentUsedType.id);
			}
		}
		return instrumentUsedType;
	}

	public void update(InstrumentUsedType instrumentUsedType) 
	{
		InstrumentUsedType instrumentUsedTypeDB = findById(instrumentUsedType.id);

		//Update commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(instrumentUsedType);

		//Update instrument list
		List<Instrument> instruments = instrumentUsedType.instruments;
		if(instruments!=null && instruments.size()>0){
			InstrumentDAO instrumentDAO = Spring.getBeanOfType(InstrumentDAO.class);
			for(Instrument instrument : instruments){
				if(instrumentUsedTypeDB!=null && !instrumentUsedTypeDB.instruments.contains(instrument)){
					instrumentDAO.add(instrument, instrumentUsedType.id);
				}
			}
		}

	}


}
