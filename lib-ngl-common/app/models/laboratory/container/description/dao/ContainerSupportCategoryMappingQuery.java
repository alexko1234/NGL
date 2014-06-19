package models.laboratory.container.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.resolutions.description.Resolution;
import models.laboratory.resolutions.description.ResolutionCategory;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

public class ContainerSupportCategoryMappingQuery extends MappingSqlQuery<ContainerSupportCategory>{

	public ContainerSupportCategoryMappingQuery()
	{
		super();
	}
	public ContainerSupportCategoryMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected ContainerSupportCategory mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		ContainerSupportCategory containerSupportCategory = new ContainerSupportCategory();
		containerSupportCategory.id=rs.getLong("id");
		containerSupportCategory.code=rs.getString("code");
		containerSupportCategory.name=rs.getString("name");
		containerSupportCategory.nbColumn = rs.getInt("nbColumn");
		containerSupportCategory.nbLine = rs.getInt("nbLine");
		containerSupportCategory.nbUsableContainer = rs.getInt("nbUsableContainer");
		long idCategory = rs.getLong("fk_container_category");
		ContainerCategory category = null;
		try {
			category = ContainerCategory.find.findById(idCategory);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		containerSupportCategory.containerCategory=category;
		return containerSupportCategory;
	}

}
