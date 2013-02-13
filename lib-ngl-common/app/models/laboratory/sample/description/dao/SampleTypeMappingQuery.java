package models.laboratory.sample.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

public class SampleTypeMappingQuery extends MappingSqlQuery<SampleType>{

	public SampleTypeMappingQuery()
	{
		super();
	}
	public SampleTypeMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}

	@Override
	protected SampleType mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			SampleType sampleType = new SampleType();
			sampleType.id = rs.getLong("id");
			long idCommonInfoType = rs.getLong("fk_common_info_type");
			long idSampleCategory = rs.getLong("fk_sample_category");
			//Get commonInfoType
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			CommonInfoType commonInfoType = commonInfoTypeDAO.findById(idCommonInfoType);
			sampleType.setCommonInfoType(commonInfoType);
			//Get sampleCategory
			SampleCategoryDAO sampleCategoryDAO = Spring.getBeanOfType(SampleCategoryDAO.class);
			SampleCategory sampleCategory=null;
			try {
				sampleCategory = (SampleCategory) sampleCategoryDAO.findById(idSampleCategory);
			} catch (DAOException e) {
				throw new SQLException(e);
			}
			sampleType.sampleCategory = sampleCategory;
			return sampleType;
		} catch (DAOException e) {
			throw new SQLException(e);
		}
	}

}
