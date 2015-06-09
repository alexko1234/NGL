package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.Logger;
import play.api.modules.spring.Spring;

@Repository
public class PropertyDefinitionDAO extends AbstractDAOMapping<PropertyDefinition>{

	protected PropertyDefinitionDAO() {
		super("property_definition", PropertyDefinition.class, PropertyDefinitionMappingQuery.class,
				"SELECT id,code,name,required,editable,active,type,display_format,display_order,default_value,description,"
						+ "choice_in_list,fk_measure_category, property_value_type,fk_save_measure_unit,fk_display_measure_unit,fk_common_info_type "
				+" FROM property_definition as t",true);
	}

	public List<PropertyDefinition> findByCommonInfoType(long idCommonInfoType)
	{
		String sql = sqlCommon+" WHERE fk_common_info_type = ? ";
		PropertyDefinitionMappingQuery propertyDefinitionMappingQuery=new PropertyDefinitionMappingQuery(dataSource, sql, new SqlParameter("fk_common_info_type",Type.LONG));
		return propertyDefinitionMappingQuery.execute(idCommonInfoType);
	}

	@Override
	public long save(PropertyDefinition value) throws DAOException {
		throw new DAOException("Must be inserted with commonInfoType id");
	}

	/**
	 * Particular sql with two code must be implemented
	 */
	public PropertyDefinition findByCode(String code) throws DAOException
	{
		throw new UnsupportedOperationException("PropertyDefinition does not have a unique code, pass bu type to retrieve PropertyDefinitions");
	}
	
	public PropertyDefinition findUnique(String code, Level.CODE levelCode){
		
		String sql = 
				"select distinct pd.code, pd.type, pd.property_value_type, pd.choice_in_list" 
				+"	from  NGL.property_definition pd"
				+"	inner join NGL.property_definition_level pdf on pdf.fk_property_definition = pd.id"
				+"	inner join NGL.level l on l.id = pdf.fk_level and l.code = ?"
				+"	inner join NGL.common_info_type cit on cit.id = pd.fk_common_info_type "
				+DAOHelpers.getCommonInfoTypeSQLForInstitute("cit")
			    +"	inner join NGL.object_type ot on ot.id = cit.fk_object_type"
			    +" where pd.code = ?";
		
		PropertyDefinitionMappingQuery propertyDefinitionMappingQuery=new PropertyDefinitionMappingQuery(dataSource, sql, true, new SqlParameter("l.code",Types.VARCHAR), new SqlParameter("pd.code",Types.VARCHAR));
		List<PropertyDefinition> l = propertyDefinitionMappingQuery.execute(levelCode.toString(), code);
		
		
		if(l.size() == 1){
			return l.get(0);
		}else{
			Logger.error("PropertyDefinition findUnique query return more than one result: "+sql);
			return null;
		}		
	}
	
	
	
	public PropertyDefinition save(PropertyDefinition propertyDefinition, long idCommonInfoType) throws DAOException
	{
		if(null == propertyDefinition.levels || propertyDefinition.levels.size()==0){
			throw new DAOException("level does not exist or level.id is null) !! - "+propertyDefinition.code);
		}
		//Create propertyDefinition
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", propertyDefinition.code);
		parameters.put("name", propertyDefinition.name);
		parameters.put("description", propertyDefinition.description);
		parameters.put("required", propertyDefinition.required);
		parameters.put("editable", propertyDefinition.editable);
		parameters.put("active", propertyDefinition.active);
		parameters.put("choice_in_list", propertyDefinition.choiceInList);
		parameters.put("type", propertyDefinition.valueType);
		parameters.put("display_format", propertyDefinition.displayFormat);
		parameters.put("display_order", propertyDefinition.displayOrder);
		parameters.put("default_value", propertyDefinition.defaultValue);
		parameters.put("property_value_type", propertyDefinition.propertyValueType);
		parameters.put("fk_common_info_type", idCommonInfoType);


		if(null != propertyDefinition.measureCategory){
			if(null ==  propertyDefinition.measureCategory.id){
				throw new DAOException("measureCategory does not exist (id is null) !!");
			}
			parameters.put("fk_measure_category", propertyDefinition.measureCategory.id);
		}

		if(null != propertyDefinition.saveMeasureValue){
			if(null ==  propertyDefinition.saveMeasureValue.id){
				throw new DAOException("saveMeasureValue does not exist (id is null) !!");
			}
			parameters.put("fk_save_measure_unit", propertyDefinition.saveMeasureValue.id);
		}

		if(null != propertyDefinition.displayMeasureValue){
			if(null ==  propertyDefinition.displayMeasureValue.id){
				throw new DAOException("displayMeasureValue does not exist (id is null) !!");
			}
			parameters.put("fk_display_measure_unit", propertyDefinition.displayMeasureValue.id);
		}

		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		propertyDefinition.id = newId;

		insertPropertyDefinitionLevel(propertyDefinition.levels,propertyDefinition.id,false);
		insertValues(propertyDefinition.possibleValues, propertyDefinition.id, false);
		return propertyDefinition;
	}



	public void update(PropertyDefinition propertyDefinition) throws DAOException
	{
		String sql = "UPDATE property_definition SET name=?, description=?, required=?, editable=?, " +
				"active=?,choice_in_list=?, type=?, display_format=?, " +
				"display_order=?, default_value=?,  property_value_type=?" +
				" WHERE id=?";
		jdbcTemplate.update(sql, propertyDefinition.name, propertyDefinition.description, propertyDefinition.required,propertyDefinition.editable,
				propertyDefinition.active, propertyDefinition.choiceInList, propertyDefinition.valueType, propertyDefinition.displayFormat,
				propertyDefinition.displayOrder, propertyDefinition.defaultValue, propertyDefinition.propertyValueType,
				propertyDefinition.id);

		//Update measure category
		String sqlCategory = "UPDATE property_definition SET fk_measure_category=? WHERE id=?";
		if(propertyDefinition.measureCategory != null  ){
			jdbcTemplate.update(sqlCategory, propertyDefinition.measureCategory.id, propertyDefinition.id);
		}else{
			jdbcTemplate.update(sqlCategory, null, propertyDefinition.id);
		}

		String sqlMeasureValue = "UPDATE property_definition SET fk_save_measure_value=? WHERE id=?";
		if(propertyDefinition.saveMeasureValue != null){
			//Update propertyDefinition
			jdbcTemplate.update(sqlMeasureValue, propertyDefinition.saveMeasureValue.id, propertyDefinition.id);
		}else{
			jdbcTemplate.update(sqlMeasureValue, null, propertyDefinition.id);
		}

		//Update displayMeasureValue
		String sqlValue = "UPDATE property_definition SET fk_display_measure_value=? WHERE id=?";
		if(propertyDefinition.displayMeasureValue!=null){
			//Update propertyDefinition
			jdbcTemplate.update(sqlValue, propertyDefinition.displayMeasureValue.id, propertyDefinition.id);
		}else{
			jdbcTemplate.update(sqlValue, null, propertyDefinition.id);
		}

		insertValues(propertyDefinition.possibleValues, propertyDefinition.id, true);
		insertPropertyDefinitionLevel(propertyDefinition.levels, propertyDefinition.id, true);
	}

	private void insertValues(List<Value> values, Long id, boolean deleteBefore) {
		//Add values list
		if(deleteBefore){
			String sqlState = "DELETE FROM value WHERE property_definition_id=?";
			jdbcTemplate.update(sqlState, id);
		}
		if(values!=null && values.size()>0){
			ValueDAO valueDao = Spring.getBeanOfType(ValueDAO.class);
			for(Value value : values){
				valueDao.save(value, id);
			}
		}
	}


	private void insertPropertyDefinitionLevel(List<Level> levels, Long id, boolean deleteBefore)  throws DAOException {
		if(deleteBefore){
			removePropertyDefinitionLevel(id);
		}
		//Add resolutions list
		if(levels!=null && levels.size()>0){
			String sql = "INSERT INTO property_definition_level (fk_property_definition, fk_level) VALUES(?,?)";
			for(Level level:levels){
				if(level == null || level.id == null ){
					throw new DAOException("level is mandatory");
				}
				jdbcTemplate.update(sql, id,level.id);
			}
		}
	}


	private void removePropertyDefinitionLevel(Long id) {
		String sqlState = "DELETE FROM property_definition_level WHERE fk_property_definition_id=?";
		jdbcTemplate.update(sqlState, id);
	}


	@Override
	public void remove(PropertyDefinition propertyDefinition) throws DAOException {
		//Delete value
		String sqlState = "DELETE FROM value WHERE property_definition_id=?";
		jdbcTemplate.update(sqlState, propertyDefinition.id);
		//Delete levels
		Logger.debug("Delete levels");
		removePropertyDefinitionLevel(propertyDefinition.id);
		//Delete property_definition
		super.remove(propertyDefinition);
	}

}
