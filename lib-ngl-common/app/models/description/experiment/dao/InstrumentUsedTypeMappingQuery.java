package models.description.experiment.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.description.common.CommonInfoType;
import models.description.common.dao.CommonInfoTypeDAO;
import models.description.experiment.Instrument;
import models.description.experiment.InstrumentCategory;
import models.description.experiment.InstrumentUsedType;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;


public class InstrumentUsedTypeMappingQuery extends MappingSqlQuery<InstrumentUsedType>{

	public InstrumentUsedTypeMappingQuery(DataSource ds, String sql, boolean all)
	{
		super(ds,sql);
		if(!all)
			super.declareParameter(new SqlParameter("id", Type.LONG));
		compile();
	}
	@Override
	protected InstrumentUsedType mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
		InstrumentUsedType instrumentUsedType = new InstrumentUsedType();
		instrumentUsedType.setId(rs.getLong("id"));
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		long idInstrumentCategory = rs.getLong("fk_instrument_category");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.find(idCommonInfoType);
		instrumentUsedType.setCommonInfoType(commonInfoType);
		//Get instrument category
		InstrumentCategoryDAO instrumentCategoryDAO = Spring.getBeanOfType(InstrumentCategoryDAO.class);
		InstrumentCategory instrumentCategory = instrumentCategoryDAO.findById(idInstrumentCategory);
		instrumentUsedType.setInstrumentCategory(instrumentCategory);
		//Get instrument
		InstrumentDAO instrumentDAO = Spring.getBeanOfType(InstrumentDAO.class);
		List<Instrument> instruments = instrumentDAO.findByInstrumentUsedType(instrumentUsedType.getId());
		instrumentUsedType.setInstruments(instruments);
		return instrumentUsedType;
	}

}
