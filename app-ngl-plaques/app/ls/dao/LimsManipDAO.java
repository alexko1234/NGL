package ls.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;


import ls.models.Manip;
import ls.models.Plate;
import ls.models.Well;
import models.utils.ListObject;

import org.apache.commons.lang3.StringUtils;
import org.fest.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;


@Repository
public class LimsManipDAO {
        private JdbcTemplate jdbcTemplate;
   

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);              
    }


    public List<Manip> findManips(Integer emnco, Integer ematerielco,String prsco){
        List<Manip> results = this.jdbcTemplate.query("pl_MaterielmanipChoisi @prsco=?, @emnco=?, @ematerielco=?, @plaque=? ", 
        		new Object[]{prsco, emnco, ematerielco, 1},new BeanPropertyRowMapper<Manip>(Manip.class));
        
        return results;
    }
    
    
    public void updatePlateCoordonates(Plate plate){
    	this.jdbcTemplate.update("ps_MaterielmanipPlaque @plaqueId=?", new Object[]{plate.code});
    	for(Well well: plate.wells){
    		this.jdbcTemplate.update("pm_MaterielmanipPlaque @matmaco=?, @plaqueId=?, @plaqueX=?, @plaqueY=?", well.code, plate.code, well.x, well.y);
    	}
    }


	public boolean isPlateCodeExist(String plateCode) {
		List<Well> wells = this.jdbcTemplate.query("pl_MaterielmanipPlaque @plaqueId=?", new Object[]{plateCode}, new RowMapper<Well>() {
	        public Well mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	Well well = new Well();
	        	//well.plateCode = rs.getString("plaqueId");
	        	well.code = rs.getInt("matmaco");
	        	well.x = rs.getString("plaqueX");
	        	well.y = rs.getString("plaqueY");
	            return well;
	        }
	    });
		return (wells.size() > 0);
	}
	
	public List<ListObject> getListObjectFromProcedureLims(String procedure) {
		List<ListObject> listObjects = this.jdbcTemplate.query(procedure,
				new RowMapper<ListObject>() {
					public ListObject mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						ListObject value = new ListObject();
						value.name = rs.getString(1);
						value.code = rs.getString(2);
						return value;
					}
				});
		return listObjects;
	}


	public List<Plate> findPlaques(Integer emnco, String projetValue) {
		List<Plate> plates = this.jdbcTemplate.query("pl_MaterielmanipPlaques @prsco=?, @emnco=?", new Object[]{projetValue, emnco}, new RowMapper<Plate>() {
	        public Plate mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	Plate plate = new Plate();
	        	//well.plateCode = rs.getString("plaqueId");
	        	plate.code = rs.getString("plaqueId");
	        	plate.nbWells = rs.getInt("nombrePuits");
	        	
	            return plate;
	        }
	    });
		return plates;
	}

	/**
	 * Return a plate with coordinate
	 * @param code
	 * @return
	 */
	public Plate findPlate(String code) {
		List<Well> wells = this.jdbcTemplate.query("pl_MaterielmanipPlaque @plaqueId=?", new Object[]{code}, new RowMapper<Well>() {
	        public Well mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	Well well = new Well();
	        	well.name = rs.getString("matmanom");
	        	well.code = rs.getInt("matmaco");
	        	well.x = rs.getString("plaqueX");
	        	well.y = rs.getString("plaqueY");
	            return well;
	        }
	    });
		Plate plate = new Plate();
		plate.code = code;
		plate.wells = wells.toArray(new Well[wells.size()]);
		return plate;
	}
}

