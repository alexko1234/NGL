package models.laboratory.container.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.container.description.ContainerSupportCategory;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

@Repository
public class ContainerSupportCategoryDAO extends AbstractDAOMapping<ContainerSupportCategory>{

	protected ContainerSupportCategoryDAO() {
		super("container_support_category", ContainerSupportCategory.class,
				ContainerSupportCategoryMappingQuery.class ,
				"select t.id, t.code, t.name, t.nbColumn, t.nbLine, t.nbUsableContainer, t.fk_container_category from container_support_category as t",true);
	}
	
	

	@Override
	public long save(ContainerSupportCategory containerSupportCategory) throws DAOException {
		//Check if category exist
		if(containerSupportCategory.containerCategory == null || containerSupportCategory.containerCategory.id == null){
			throw new IllegalArgumentException("ContainerCategory is not present ");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", containerSupportCategory.code);
		parameters.put("name", containerSupportCategory.name);
		parameters.put("nbColumn", containerSupportCategory.nbColumn);
		parameters.put("nbLine", containerSupportCategory.nbLine);
		parameters.put("nbUsableContainer", containerSupportCategory.nbUsableContainer);
		parameters.put("fk_container_category", containerSupportCategory.containerCategory.id);

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		containerSupportCategory.id = newId;
		return containerSupportCategory.id;
	}

	@Override
	public void update(ContainerSupportCategory containerSupportCategory) throws DAOException {
		if(containerSupportCategory.containerCategory == null || containerSupportCategory.containerCategory.id == null){
			throw new IllegalArgumentException("ContainerCategory is not present ");
		}
		
		String sql = "UPDATE container_support_category SET code=?, name=?, nbColumn=?, nbLine=?, nbUsableContainer=?, fk_container_category=?  WHERE id=?";
		jdbcTemplate.update(sql, containerSupportCategory.code, containerSupportCategory.name, containerSupportCategory.nbColumn,
				containerSupportCategory.nbLine,containerSupportCategory.nbUsableContainer, containerSupportCategory.containerCategory.id);
		
	}
	
	@Override
	public void remove(ContainerSupportCategory containerSupportCategory) throws DAOException
	{
		//Remove inContainerSupport from instrument category
		String sqlContainerIn = "DELETE FROM instrument_ut_in_container_support_cat WHERE fk_container_support_category=?";
		jdbcTemplate.update(sqlContainerIn, containerSupportCategory.id);
		//Remove outContainerSupport from instrument category
		String sqlContainerOut = "DELETE FROM instrument_ut_out_container_support_cat WHERE fk_container_support_category=?";
		jdbcTemplate.update(sqlContainerOut, containerSupportCategory.id);
		super.remove(containerSupportCategory);
	}

	
	public List<ContainerSupportCategory> findInByInstrumentUsedType(Long idInstrumentUsedType) throws DAOException {
		if(null == idInstrumentUsedType){
			throw new DAOException("idInstrumentUsedType is mandatory");
		}
		String sql = sqlCommon+" inner join instrument_ut_in_container_support_cat ON fk_container_support_category=t.id" +
				" WHERE fk_instrument_used_type=? ";
		return initializeMapping(sql, new SqlParameter("fk_instrument_used_type", Types.BIGINT)).execute(idInstrumentUsedType);		
	}
	
	public List<ContainerSupportCategory> findOutByInstrumentUsedType(Long idInstrumentUsedType) throws DAOException {
		if(null == idInstrumentUsedType){
			throw new DAOException("idInstrumentUsedType is mandatory");
		}
		String sql = sqlCommon+" inner join instrument_ut_out_container_support_cat ON fk_container_support_category=t.id" +
				" WHERE fk_instrument_used_type=? ";
		return initializeMapping(sql, new SqlParameter("fk_instrument_used_type", Types.BIGINT)).execute(idInstrumentUsedType);		
	}
	
}
