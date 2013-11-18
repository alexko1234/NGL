package models.laboratory.run.description.dao;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.run.description.ReadSetType;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class ReadSetTypeDAO extends AbstractDAOCommonInfoType<ReadSetType>{

	protected ReadSetTypeDAO() {
		super("readset_type", ReadSetType.class, ReadSetTypeMappingQuery.class, 
				"SELECT distinct c.id, c.fk_common_info_type ", 
						"FROM readset_type as c "+sqlCommonInfoType, false);
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

