package models.laboratory.sample.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

public class ImportTypeMappingQuery extends MappingSqlQuery<ImportType>{

	public ImportTypeMappingQuery()
	{
		super();
	}
	public ImportTypeMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}

	@Override
	protected ImportType mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			ImportType importType = new ImportType();
			importType.id = rs.getLong("id");
			long idCommonInfoType = rs.getLong("fk_common_info_type");
			long idImportCategory = rs.getLong("fk_import_category");
			//Get commonInfoType
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			CommonInfoType commonInfoType = commonInfoTypeDAO.findById(idCommonInfoType);
			importType.setCommonInfoType(commonInfoType);
			//Get sampleCategory
			ImportCategoryDAO importCategoryDAO = Spring.getBeanOfType(ImportCategoryDAO.class);
			ImportCategory importCategory=null;
			try {
				importCategory =  importCategoryDAO.findById(idImportCategory);
			} catch (DAOException e) {
				throw new SQLException(e);
			}
			importType.importCategory = importCategory;
			return importType;
		} catch (DAOException e) {
			throw new SQLException(e);
		}
	}

}
