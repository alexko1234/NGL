package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import models.laboratory.common.description.ObjectType;
import models.utils.dao.MappingSqlQueryFactory;
import models.utils.dao.NGLMappingSqlQuery;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

//public class ObjectTypeMappingQuery extends MappingSqlQuery<ObjectType> {
public class ObjectTypeMappingQuery extends NGLMappingSqlQuery<ObjectType> {

	public static final MappingSqlQueryFactory<ObjectType> factory = (d,s) -> new ObjectTypeMappingQuery(d,s);
	
//	public ObjectTypeMappingQuery(){
//		super();
//	}
	
//	public ObjectTypeMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter){
//		super(ds,sql);
//		if (sqlParameter != null)
////			super.declareParameter(sqlParameter);
//			declareParameter(sqlParameter);
//		compile();
//	}
	
	public ObjectTypeMappingQuery(DataSource ds, String sql,SqlParameter... sqlParameters){
		super(ds,sql,sqlParameters);
	}

	@Override
	protected ObjectType mapRow(ResultSet rs, int rowNumber) throws SQLException {
		ObjectType objectType = new ObjectType();
		objectType.id      = rs.getLong("oId");
		objectType.code    = rs.getString("codeObject");
		objectType.generic = rs.getBoolean("generic");
		return objectType;
	}

}