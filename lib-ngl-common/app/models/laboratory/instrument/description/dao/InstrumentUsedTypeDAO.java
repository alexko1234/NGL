package models.laboratory.instrument.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;

import org.springframework.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class InstrumentUsedTypeDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	private DataSource dataSource;
	private String sqlCommon = "SELECT id, fk_common_info_type, fk_instrument_category "+
			"FROM instrument_used_type ";
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("instrument_used_type");
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

	public InstrumentUsedType findById(long id)
	{
		String sql = sqlCommon+
				"WHERE id = ? ";
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return instrumentUsedTypeMappingQuery.findObject(id);
	}

	public InstrumentUsedType findByCommonInfoType(long idCommonInfoType)
	{
		String sql = sqlCommon+
				
				"WHERE fk_common_info_type = ? ";
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return instrumentUsedTypeMappingQuery.findObject(idCommonInfoType);
	}
	
	public InstrumentUsedType findByCode(String code)
	{
		String sql = "SELECT it.id, it.fk_common_info_type, it.fk_instrument_category "+
				"FROM instrument_used_type as it "+
				"JOIN common_info_type as c ON c.id=it.fk_common_info_type "+
				"WHERE code = ? ";
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sql,new SqlParameter("id", Types.VARCHAR));
		return instrumentUsedTypeMappingQuery.findObject(code);
	}

	public List<InstrumentUsedType> findAll()
	{
		InstrumentUsedTypeMappingQuery instrumentUsedTypeMappingQuery = new InstrumentUsedTypeMappingQuery(dataSource, sqlCommon,null);
		return instrumentUsedTypeMappingQuery.execute();
	}

	public void add(List<InstrumentUsedType> instrumentUsedTypes, long idCommonInfoType)
	{
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
				add(instrumentUsedType, idCommonInfoType);
			}
		}
	}

	public void add(InstrumentUsedType instrumentUsedType, long idCommonInfoType)
	{
		String sql = "INSERT INTO common_info_type_instrument_type(fk_common_info_type, fk_instrument_type) VALUES(?,?)";
		if(instrumentUsedType.id==null)
			instrumentUsedType = add(instrumentUsedType);
		jdbcTemplate.update(sql, idCommonInfoType, instrumentUsedType.id);
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
		parameters.put("id", instrumentUsedType.id);
		parameters.put("fk_common_info_type", instrumentUsedType.id);
		parameters.put("fk_instrument_category", instrumentUsedType.instrumentCategory.id);
		jdbcInsert.execute(parameters);

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
