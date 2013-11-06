package models.laboratory.run.description.dao;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.run.description.ReadSetType;
import models.utils.DescriptionHelper;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class ReadSetTypeDAO extends AbstractDAOMapping<ReadSetType>{

	protected ReadSetTypeDAO() {
		super("readset_type", ReadSetType.class, ReadSetTypeMappingQuery.class, 
				"SELECT t.id, fk_common_info_type "+ /*, fk_reaset_category */ 
						"FROM readset_type as t "+
						"JOIN common_info_type as c ON c.id=t.fk_common_info_type "+
						"JOIN common_info_type_institute ci ON c.id=ci.fk_common_info_type "+
						"JOIN institute i ON i.id = ci.fk_institute AND i.code=" + DescriptionHelper.getInstitute(), false);
	}

	@Override
	public long save(ReadSetType readSetType) throws DAOException {
		if(null == readSetType){
			throw new DAOException("ReadSetType is mandatory");
		}
		
		//Check if category exist
		//if(readSetType.category == null || readSetType.category.id == null){
		//	throw new DAOException("ReadSetCategory is not present !!");
		//}
		
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		readSetType.id = commonInfoTypeDAO.save(readSetType);
		
		//Create new runType
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", readSetType.id);
		parameters.put("fk_common_info_type", readSetType.id);
		//parameters.put("fk_readset_category", readSetType.category.id);
		jdbcInsert.execute(parameters);
		return readSetType.id;
	}

	@Override
	public void update(ReadSetType readSetType) throws DAOException
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(readSetType);
	}

	@Override
	public void remove(ReadSetType readSetType) throws DAOException {
		super.remove(readSetType);
		//Remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(readSetType);
	}
}

