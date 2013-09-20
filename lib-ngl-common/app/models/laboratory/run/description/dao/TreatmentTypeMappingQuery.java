package models.laboratory.run.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.TreatmentType;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class TreatmentTypeMappingQuery extends MappingSqlQuery<TreatmentType>{

	public TreatmentTypeMappingQuery() {
		super();
	}
	public TreatmentTypeMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter) {
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	
	@Override
	protected TreatmentType mapRow(ResultSet rs, int rowNum) throws SQLException {
			TreatmentType treatmentType = new TreatmentType();
			
			treatmentType.id = rs.getLong("id");
			treatmentType.names = rs.getString("names");  
			
			long idCommonInfoType = rs.getLong("fk_common_info_type");
			long idTreatmentCategory = rs.getLong("fk_treatment_category");
			
			//Get commonInfoType
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			CommonInfoType commonInfoType = null;
			try {
				 commonInfoType = (CommonInfoType) commonInfoTypeDAO.findById(idCommonInfoType);
			} catch (DAOException e) {
				throw new SQLException(e);
			}
			//Set commonInfoType
			treatmentType.setCommonInfoType(commonInfoType);
			//Get category
			TreatmentCategoryDAO treatmentCategoryDAO = Spring.getBeanOfType(TreatmentCategoryDAO.class);
			TreatmentCategory treatmentCategory=null;
			try {
				treatmentCategory = (TreatmentCategory) treatmentCategoryDAO.findById(idTreatmentCategory);
			} catch (DAOException e) {
				throw new SQLException(e);
			}
			//Set category
			treatmentType.category = treatmentCategory;
			return treatmentType;

	}

}

