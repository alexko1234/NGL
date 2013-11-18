package models.laboratory.sample.description.dao;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.DescriptionHelper;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class SampleTypeDAO extends AbstractDAOCommonInfoType<SampleType>{

	protected SampleTypeDAO() {
		super("sample_type", SampleType.class, SampleTypeMappingQuery.class, 
				"SELECT distinct c.id, c.fk_common_info_type, c.fk_sample_category ",
				"FROM sample_type as c "+sqlCommonInfoType, false);
	}

	@Override
	public long save(SampleType sampleType) throws DAOException
	{
		
		if(null == sampleType){
			throw new DAOException("sampleType is mandatory");
		}
		//Check if category exist
		if(sampleType.category == null || sampleType.category.id == null){
			throw new DAOException("SampleCategory is not present !!");
		}
		
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		sampleType.id = commonInfoTypeDAO.save(sampleType);
		//Create sampleType 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", sampleType.id);
		parameters.put("fk_common_info_type", sampleType.id);
		parameters.put("fk_sample_category", sampleType.category.id);
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
