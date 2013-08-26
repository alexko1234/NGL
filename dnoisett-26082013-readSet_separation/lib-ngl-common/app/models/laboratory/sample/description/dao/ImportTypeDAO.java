package models.laboratory.sample.description.dao;

import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class ImportTypeDAO extends AbstractDAOMapping<ImportType>{


	protected ImportTypeDAO() {
		super("import_type", ImportType.class, ImportTypeMappingQuery.class, 
				"SELECT t.id, fk_common_info_type, fk_import_category "+
						"FROM import_type as t JOIN common_info_type as c ON c.id=fk_common_info_type ",false);
	}

	@Override
	public long save(ImportType importType) throws DAOException {
		
		if(null == importType){
			throw new DAOException("importType is mandatory");
		}
		//Check if category exist
		if(importType.category == null || importType.category.id == null){
			throw new DAOException("ImportCategory is not present !!");
		}
		
		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		importType.id = commonInfoTypeDAO.save(importType);
		//Create sampleType 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", importType.id);
		parameters.put("fk_common_info_type", importType.id);
		parameters.put("fk_import_category", importType.category.id);
		jdbcInsert.execute(parameters);
		return importType.id;
	}

	@Override
	public void update(ImportType importType) throws DAOException {
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(importType);
	}

	@Override
	public void remove(ImportType importType) throws DAOException {
		//Remove importType
		super.remove(importType);
		//Remove commonInfotype
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(importType);
	}


}
