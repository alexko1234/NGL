package models.laboratory.sample.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

public class SampleTypeMappingQuery extends MappingSqlQuery<SampleType>{

	public SampleTypeMappingQuery(DataSource ds)
	{
		super(ds,"SELECT id, fk_common_info_type, fk_sample_category "+
				"FROM sample_type "+
				"WHERE id = ? ");
		super.declareParameter(new SqlParameter("id", Type.LONG));
		compile();
	}

	@Override
	protected SampleType mapRow(ResultSet rs, int rowNum) throws SQLException {
		SampleType sampleType = new SampleType();
		sampleType.id = rs.getLong("id");
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		long idSampleCategory = rs.getLong("fk_sample_category");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.find(idCommonInfoType);
		sampleType.commonInfoType = commonInfoType;
		//Get sampleCategory
		SampleCategoryDAO sampleCategoryDAO = Spring.getBeanOfType(SampleCategoryDAO.class);
		SampleCategory sampleCategory = sampleCategoryDAO.findById(idSampleCategory);
		sampleType.sampleCategory = sampleCategory;
		return sampleType;
	}

}
