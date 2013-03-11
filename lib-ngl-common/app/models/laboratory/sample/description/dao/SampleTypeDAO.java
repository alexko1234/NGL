package models.laboratory.sample.description.dao;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class SampleTypeDAO extends AbstractDAOMapping<SampleType>{

	protected SampleTypeDAO() {
		super("sample_type", SampleType.class, SampleTypeMappingQuery.class, 
				"SELECT t.id, fk_common_info_type, fk_sample_category "+
						"FROM sample_type as t JOIN common_info_type as c ON c.id=fk_common_info_type ", false);
	}

	@Override
	public long save(SampleType sampleType) throws DAOException
	{
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		sampleType.id = commonInfoTypeDAO.save(sampleType);
		//Add sampleCategory
		if(sampleType.sampleCategory!=null){
			SampleCategory sampleCategoryDB = SampleCategory.find.findByCode(sampleType.sampleCategory.code);
			if(sampleCategoryDB ==null){
				SampleCategoryDAO sampleCategoryDAO = Spring.getBeanOfType(SampleCategoryDAO.class);
				sampleType.sampleCategory.id = sampleCategoryDAO.save(sampleType.sampleCategory);
			}else
				sampleType.sampleCategory=sampleCategoryDB;
		}
		//Create sampleType 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", sampleType.id);
		parameters.put("fk_common_info_type", sampleType.id);
		parameters.put("fk_sample_category", sampleType.sampleCategory.id);
		jdbcInsert.execute(parameters);
		return sampleType.id;
	}

	@Override
	public void update(SampleType sampleType) throws DAOException
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(sampleType);
	}

	@Override
	public void remove(SampleType sampleType) throws DAOException {
		//Remove sampleType
		super.remove(sampleType);
		//Remove commonInfotype
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(sampleType);
	}
}
