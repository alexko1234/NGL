package models.laboratory.instrument.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.ResolutionCategory;
import models.laboratory.instrument.description.Instrument;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class InstrumentMappingQuery extends MappingSqlQuery<Instrument>{

	public InstrumentMappingQuery() {
		super();
	}
	
	public InstrumentMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter) {
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected Instrument mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		Instrument instrument = new Instrument();
		instrument.id=rs.getLong("id");
		instrument.code=rs.getString("code");
		instrument.name=rs.getString("name");
		instrument.path=rs.getString("path");
		instrument.active=rs.getBoolean("active");
		
		
		long idType = rs.getLong("fk_instrument_used_type");
		Map<String, Object> result = Spring.getBeanOfType(InstrumentUsedTypeDAO.class).findTypeCodeAndCatCode(idType);
		
		instrument.typeCode = (String)result.get("typeCode");
		instrument.categoryCode = (String)result.get("catCode");
		
		
		return instrument;
	}

}
