package models.laboratory.instrument.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;


public class InstrumentUsedTypeMappingQuery extends MappingSqlQuery<InstrumentUsedType>{

	public InstrumentUsedTypeMappingQuery()
	{
		super();
	}
	
	public InstrumentUsedTypeMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();

	}
	@Override
	protected InstrumentUsedType mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
		try {
			InstrumentUsedType instrumentUsedType = new InstrumentUsedType();
			instrumentUsedType.id = rs.getLong("id");
			long idCommonInfoType = rs.getLong("fk_common_info_type");
			long idInstrumentCategory = rs.getLong("fk_instrument_category");
			//Get commonInfoType
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			CommonInfoType commonInfoType=commonInfoTypeDAO.findById(idCommonInfoType);
			if (commonInfoType != null) {
				instrumentUsedType.setCommonInfoType(commonInfoType);
			}
			//Get instrument category
			InstrumentCategoryDAO instrumentCategoryDAO = Spring.getBeanOfType(InstrumentCategoryDAO.class);
			InstrumentCategory instrumentCategory=instrumentCategoryDAO.findById(idInstrumentCategory);
			instrumentUsedType.category = instrumentCategory;
			//Get instrument
			InstrumentDAO instrumentDAO = Spring.getBeanOfType(InstrumentDAO.class);
			List<Instrument> instruments = instrumentDAO.findByInstrumentUsedType(instrumentUsedType.id);
			instrumentUsedType.instruments = instruments;
			
			ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
			//Find inContainerSupportCategories
			instrumentUsedType.inContainerSupportCategories = containerSupportCategoryDAO.findInByInstrumentUsedType(instrumentUsedType.id);
			//Find outContainerSupportCategorie
			instrumentUsedType.outContainerSupportCategories = containerSupportCategoryDAO.findOutByInstrumentUsedType(instrumentUsedType.id);
		
			return instrumentUsedType;
		} catch (DAOException e) {
			throw new SQLException(e);
		}
	}

}
