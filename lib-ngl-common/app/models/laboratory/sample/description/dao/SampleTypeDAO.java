package models.laboratory.sample.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;

import org.springframework.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class SampleTypeDAO {

	private DataSource dataSource;
	private String sqlCommon = "SELECT id, fk_common_info_type, fk_sample_category "+
			"FROM sample_type ";
	private SimpleJdbcInsert jdbcInsert;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("sample_type").usingGeneratedKeyColumns("id");
	}

	public SampleType findById(long id)
	{
		String sql = sqlCommon+
				"WHERE id = ? ";
		SampleTypeMappingQuery sampleTypeMappingQuery = new SampleTypeMappingQuery(dataSource,sql, new SqlParameter("id", Type.LONG));
		return sampleTypeMappingQuery.findObject(id);
	}

	public SampleType findByCode(String code)
	{
		String sql = "SELECT st.id, fk_common_info_type, fk_sample_category "+
				"FROM sample_type as st JOIN common_info_type as c ON c.id=fk_common_info_type "+
				"WHERE code=?";
		SampleTypeMappingQuery sampleTypeMappingQuery = new SampleTypeMappingQuery(dataSource,sql, new SqlParameter("code",Types.VARCHAR));
		return sampleTypeMappingQuery.findObject(code);
	}

	public SampleType add(SampleType sampleType)
	{
		//Add commonInfoType
		if(sampleType.commonInfoType!=null && sampleType.commonInfoType.id==null)
		{
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			CommonInfoType cit = commonInfoTypeDAO.add(sampleType.commonInfoType);
			sampleType.commonInfoType = cit;
		}

		//Add sampleCategory
		if(sampleType.sampleCategory!=null && sampleType.sampleCategory.id==null)
		{
			SampleCategoryDAO sampleCategoryDAO = Spring.getBeanOfType(SampleCategoryDAO.class);
			SampleCategory sampleCategory = sampleCategoryDAO.add(sampleType.sampleCategory);
			sampleType.sampleCategory=sampleCategory;
		}

		//Create sampleType 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fk_common_info_type", sampleType.commonInfoType.id);
		parameters.put("fk_sample_category", sampleType.sampleCategory.id);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		sampleType.id = newId;
		return sampleType;
	}
}
