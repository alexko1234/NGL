package models.laboratory.run.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

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
		try {	
			TreatmentType treatmentType = new TreatmentType();
			treatmentType.id = rs.getLong("id");
			treatmentType.names = rs.getString("names");  
						
			//Get commonInfoType
			long idCommonInfoType = rs.getLong("fk_common_info_type");			
			CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
			treatmentType.setCommonInfoType(commonInfoTypeDAO.findById(idCommonInfoType));
			
			//Get category
			long idTreatmentCategory = rs.getLong("fk_treatment_category");			
			TreatmentCategoryDAO treatmentCategoryDAO = Spring.getBeanOfType(TreatmentCategoryDAO.class);
			treatmentType.category = (TreatmentCategory) treatmentCategoryDAO.findById(idTreatmentCategory);
			
			TreatmentTypeContextDAO treatmentContextDAO =  Spring.getBeanOfType(TreatmentTypeContextDAO.class);
			treatmentType.contexts = treatmentContextDAO.findByTreatmentTypeId(treatmentType.id);
			
			return treatmentType;
		} catch (DAOException e) {
			throw new SQLException(e);
		}

	}

}
