package models.laboratory.instrument.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class InstrumentUsedTypeDAO extends AbstractDAOMapping<InstrumentUsedType>{

	protected InstrumentUsedTypeDAO() {
		super("instrument_used_type", InstrumentUsedType.class, InstrumentUsedTypeMappingQuery.class, 
				"SELECT t.id, fk_common_info_type, fk_instrument_category "+
				"FROM instrument_used_type as t "+
				"JOIN common_info_type as c ON c.id=t.fk_common_info_type ", false);
	}

	public List<InstrumentUsedType> findByCommonExperiment(long idCommonInfoType)
	{
		String sql = "SELECT it.id, it.fk_common_info_type, it.fk_instrument_category "+
				"FROM instrument_used_type as it "+
				"JOIN common_info_type_instrument_type as cit ON fk_instrument_type=id " +
				"WHERE cit.fk_common_info_type = ? ";
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return instrumentUsedTypeMappingQuery.execute(idCommonInfoType);
	}

	public InstrumentUsedType findByCommonInfoType(long idCommonInfoType)
	{
		String sql = sqlCommon+
				"WHERE fk_common_info_type = ? ";
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return instrumentUsedTypeMappingQuery.findObject(idCommonInfoType);
	}

	public void save(List<InstrumentUsedType> instrumentUsedTypes, long idCommonInfoType) throws DAOException
	{
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
				save(instrumentUsedType, idCommonInfoType);
			}
		}
	}

	public void save(InstrumentUsedType instrumentUsedType, long idCommonInfoType) throws DAOException
	{
		String sql = "INSERT INTO common_info_type_instrument_type(fk_common_info_type, fk_instrument_type) VALUES(?,?)";
		if(instrumentUsedType.code!=null && InstrumentUsedType.find.findByCode(instrumentUsedType.code)==null)
			instrumentUsedType.id = save(instrumentUsedType);
		jdbcTemplate.update(sql, idCommonInfoType, instrumentUsedType.id);
	}

	public long save(InstrumentUsedType instrumentUsedType) throws DAOException
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		instrumentUsedType.id =commonInfoTypeDAO.save(instrumentUsedType);
		instrumentUsedType.setCommonInfoType(instrumentUsedType);
		//Check if instrumentCategory exist
		if(instrumentUsedType.instrumentCategory!=null && instrumentUsedType.instrumentCategory.code!=null && InstrumentCategory.find.findByCode(instrumentUsedType.instrumentCategory.code)==null)
		{
			InstrumentCategoryDAO instrumentCategoryDAO = Spring.getBeanOfType(InstrumentCategoryDAO.class);
			instrumentUsedType.instrumentCategory.id = instrumentCategoryDAO.save(instrumentUsedType.instrumentCategory);
		}
		//Create new InstrumentUsedType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", instrumentUsedType.id);
		parameters.put("fk_common_info_type", instrumentUsedType.id);
		parameters.put("fk_instrument_category", instrumentUsedType.instrumentCategory.id);
		jdbcInsert.execute(parameters);

		//Add instruments list
		List<Instrument> instruments = instrumentUsedType.instruments;
		if(instruments!=null && instruments.size()>0){
			InstrumentDAO instrumentDAO = Spring.getBeanOfType(InstrumentDAO.class);
			for(Instrument instrument : instruments){
				instrumentDAO.save(instrument, instrumentUsedType.id);
			}
		}
		return instrumentUsedType.id;
	}

	public void update(InstrumentUsedType instrumentUsedType) throws DAOException 
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
					instrumentDAO.save(instrument, instrumentUsedType.id);
				}
			}
		}

	}

	@Override
	public void remove(InstrumentUsedType instrumentUsedType) {
		//remove from abstractExperiment common_info_type_instrument_type
		String sqlExp = "DELETE FROM common_info_type_instrument_type WHERE fk_instrument_type=?";
		jdbcTemplate.update(sqlExp, instrumentUsedType.id);
		//remove instruments
		String sqlInst = "DELETE FROM instrument WHERE instrument_used_type_id=?";
		jdbcTemplate.update(sqlInst, instrumentUsedType.id);
		//remove common_info_type
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(instrumentUsedType);
		//remove instrument used type
		super.remove(instrumentUsedType);
		
	}


}
