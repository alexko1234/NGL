package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.instance.PropertyValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import play.data.validation.ValidationError;
import static validation.utils.ConstraintsHelper.addErrors;

@Repository
public class TaraDAO {

	private JdbcTemplate jdbcTemplate;


	@Autowired
	@Qualifier("tara")
	public void setDataSourceTara(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}

	//TODO gestion des erreurs
	public Map<String,PropertyValue> findTaraSample(Integer limsCode,Map<String,List<ValidationError>> errors){

		List<Map<String,PropertyValue>> results =  this.jdbcTemplate.query("   SELECT"+
				"  cast(AREA.AREA_CODE as SIGNED INT) as station ,"+
				"  AREA.AREA_NAME,"+
				"  FRACTION.FRACTION_CODE,"+
				"  FRACTION.FRACTION_NAME as filtre,"+
				"  ITERATION.ITERATION_CODE as iteration,"+
				"  ITERATION.ITERATION_NAME,"+
				"  LOCUS.LOCUS_CODE ,"+
				"  LOCUS.LOCUS_NAME as profondeur,"+
				"  MATERIAL.MATERIAL_CODE,"+
				"  MATERIAL.MATERIAL_NAME as materiel,"+
				"  CROSS_REF.REF_ID"+
				" FROM "+
				" AREA INNER JOIN SAMPLE ON (AREA.AREA_ID=SAMPLE.AREA_ID)"+
				"  INNER JOIN CROSS_REF ON (SAMPLE.SAMPLE_ID=CROSS_REF.SAMPLE_ID)"+
				"  INNER JOIN FRACTION ON (SAMPLE.FRACTION_ID=FRACTION.FRACTION_ID)"+
				"  INNER JOIN ITERATION ON (ITERATION.ITERATION_ID=SAMPLE.ITERATION_ID)"+
				"  INNER JOIN LOCUS ON (SAMPLE.LOCUS_ID=LOCUS.LOCUS_ID)"+
				"  INNER JOIN MATERIAL ON (MATERIAL.MATERIAL_ID=SAMPLE.MATERIAL_ID)" +
				"  WHERE CROSS_REF.REF_ID=? ", 
				new Object[]{limsCode},new RowMapper<Map<String,PropertyValue>>() {

			public Map<String,PropertyValue> mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				//TODO manque materiel origine
				Map<String,PropertyValue> properMap=new HashMap<String, PropertyValue>();
				properMap.put("station", new PropertyValue(rs.getInt("station")));
				properMap.put("profondeur", new PropertyValue(rs.getString("profondeur")));
				properMap.put("filtre", new PropertyValue(rs.getString("filtre")));
				properMap.put("iteration", new PropertyValue(rs.getString("iteration")));
				properMap.put("materiel", new PropertyValue(rs.getString("materiel")));
				return properMap;
			}

		});     
		
		if(results.size()==1){
			return results.get(0);
		} else {
			//addErrors(errors,"taraRefId","limsdao.error.code.notexist","Tara Reference Id", limsCode);
			return null;
		}


	}

}
