package models.laboratory.container.description.dao;

import java.util.List;

import models.laboratory.container.description.ContainerSupportCategory;
import models.utils.dao.AbstractDAO;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ContainerSupportCategoryDAO extends AbstractDAO<ContainerSupportCategory>{

	protected ContainerSupportCategoryDAO() {
		super("container_support_category", ContainerSupportCategory.class,true);
	}


	public List<ContainerSupportCategory> findInByInstrumentCategory(long idInstrumentCategory)
	{
		String sql = "SELECT id,name,code,nbUsableContainer,nbLine,nbColumn " +
				"FROM container_support_category "+
				"JOIN instrumentCategory_inContainerSupportCategory ON fk_container_support_category=id "+
				"WHERE fk_instrument_category=?";
		BeanPropertyRowMapper<ContainerSupportCategory> mapper = new BeanPropertyRowMapper<ContainerSupportCategory>(ContainerSupportCategory.class);
		return this.jdbcTemplate.query(sql, mapper, idInstrumentCategory);
	}

	public List<ContainerSupportCategory> findOutByInstrumentCategory(long idInstrumentCategory)
	{
		String sql = "SELECT id, name, code, nbUsableContainer,nbLine,nbColumn "+
				"FROM container_support_category "+
				"JOIN instrumentCategory_outContainerSupportCategory ON fk_container_support_category=id "+
				"WHERE fk_instrument_category=?";
		BeanPropertyRowMapper<ContainerSupportCategory> mapper = new BeanPropertyRowMapper<ContainerSupportCategory>(ContainerSupportCategory.class);
		return this.jdbcTemplate.query(sql, mapper, idInstrumentCategory);
	}

	@Override
	public void remove(ContainerSupportCategory containerSupportCategory)
	{
		//Remove inContainerSupport from instrument category
		String sqlContainerIn = "DELETE FROM instrumentCategory_inContainerSupportCategory WHERE fk_container_support_category=?";
		jdbcTemplate.update(sqlContainerIn, containerSupportCategory.id);
		//Remove outContainerSupport from instrument category
		String sqlContainerOut = "DELETE FROM instrumentCategory_outContainerSupportCategory WHERE fk_container_support_category=?";
		jdbcTemplate.update(sqlContainerOut, containerSupportCategory.id);
		super.remove(containerSupportCategory);
	}
}
