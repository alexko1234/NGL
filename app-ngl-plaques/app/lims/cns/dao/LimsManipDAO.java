package lims.cns.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lims.models.Manip;
import lims.models.Plate;
import lims.models.User;
import lims.models.Well;
import models.laboratory.common.instance.TBoolean;
import models.utils.ListObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import play.Logger;


@Repository
public class LimsManipDAO {
        private JdbcTemplate jdbcTemplate;


    @Autowired
    @Qualifier("lims")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<Manip> findManips(Integer emnco, Integer ematerielco,String prsco){
    	Logger.info("pl_MaterielmanipChoisi @prsco='"+prsco+"', @emnco="+emnco+", @ematerielco="+ematerielco+", @plaque=1 ");
        List<Manip> results = this.jdbcTemplate.query("pl_MaterielmanipChoisi @prsco=?, @emnco=?, @ematerielco=?, @plaque=?",
        		new Object[]{prsco, emnco, ematerielco, 1},new BeanPropertyRowMapper<Manip>(Manip.class));
        return results;
    }

    public void createBarcode(String barcode, Integer typeCode, String user){
    	Logger.info("pc_PlaqueSolexa @plaqueId="+barcode+", @emnco="+typeCode);
    	this.jdbcTemplate.update("pc_PlaqueSolexa @plaqueId=?, @emnco=?, @perlog=?", new Object[]{barcode, typeCode, user});
    	
    }
    
    public void createPlate(Plate plate, String user){
    	Logger.info("pc_PlaqueSolexa @plaqueId="+plate.code+", @emnco="+plate.typeCode);
    	this.jdbcTemplate.update("pc_PlaqueSolexa @plaqueId=?, @emnco=?, @valqc=?, @valrun=?, @plaquecom=?, @perlog=?", new Object[]{plate.code, plate.typeCode, getValValue(plate.validQC), getValValue(plate.validRun), plate.comment, user});
    	this.jdbcTemplate.update("ps_MaterielmanipPlaque @plaqueId=?", new Object[]{plate.code});
    	for(Well well: plate.wells){
    		Logger.info("pm_MaterielmanipPlaque @matmaco="+well.code+", @plaqueId="+plate.code+", @plaqueX="+well.x+", @plaqueY="+well.y+"");
    		this.jdbcTemplate.update("pm_MaterielmanipPlaque @matmaco=?, @plaqueId=?, @plaqueX=?, @plaqueY=?", well.code, plate.code, well.x, well.y);
    	}
    }

    public void updatePlate(Plate plate, String user){
	this.jdbcTemplate.update("pm_PlaqueSolexa @plaqueId=?, @valqc=?, @valrun=?, @plaquecom=?, @perlog=?", new Object[]{plate.code, getValValue(plate.validQC), getValValue(plate.validRun), plate.comment, user});    	
    	Logger.info("ps_MaterielmanipPlaque @plaqueId="+plate.code);
    	this.jdbcTemplate.update("ps_MaterielmanipPlaque @plaqueId=?", new Object[]{plate.code});
    	for(Well well: plate.wells){
    		Logger.info("pm_MaterielmanipPlaque @matmaco="+well.code+", @plaqueId="+plate.code+", @plaqueX="+well.x+", @plaqueY="+well.y+"");
    		this.jdbcTemplate.update("pm_MaterielmanipPlaque @matmaco=?, @plaqueId=?, @plaqueX=?, @plaqueY=?", well.code, plate.code, well.x, well.y);
    	}
    }

    public List<Plate> findPlates(Integer emnco, String projetValue, String plaqueId, String matmanom, Integer percodc, String fromDate, String toDate) {
    	Logger.info("pl_PlaqueSolexa @prsco="+projetValue+", @emnco="+emnco);
		List<Plate> plates = this.jdbcTemplate.query("pl_PlaqueSolexa @prsco=?, @emnco=?, @plaqueId=?, @matmanom=?, @percodc=?, @fromDate=?, @toDate=?", new Object[]{projetValue, emnco, plaqueId, matmanom,percodc,fromDate,toDate}, new RowMapper<Plate>() {
	        public Plate mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	Plate plate = new Plate();
	        	//well.plateCode = rs.getString("plaqueId");
	        	plate.code = rs.getString("plaqueId");
	        	plate.typeCode = rs.getInt("emnco");
	        	plate.typeName = rs.getString("emnnom");
	        	plate.nbWells = rs.getInt("nombrePuitUtilises");
	        	plate.validQC = getTBoolean(rs.getInt("valqc"));
	        	plate.validRun = getTBoolean(rs.getInt("valrun"));
	        	plate.comment = rs.getString("plaquecom");
	        	plate.creationDate = rs.getDate("plaquedc");
	        	plate.modificationDate = rs.getDate("plaquedm");
	        	plate.creationUserId = rs.getInt("percodc");
	        	plate.modificationUserId = rs.getInt("percodm");
	        	
	        	
	            return plate;
	        }

		
	    });
		return plates;
	}

    
    public List<String> findUnusedBarCodes(){
    	String query = "pl_PlaqueSolexaUnused";
    	
    	List<String> unusedBarcodes = this.jdbcTemplate.queryForList(query, String.class);
    	return unusedBarcodes;
    	
    }
    
    private TBoolean getTBoolean(int value) {
	TBoolean valid = TBoolean.UNSET;
	if (value == 1) {
	    valid = TBoolean.TRUE;
	} else if (value == 0) {
	    valid = TBoolean.FALSE;
	}
	return valid;
    }
    
    private int getValValue(TBoolean value) {
	int valid = 2;
	if (TBoolean.TRUE.equals(value)) {
	    valid = 1;
	} else if (TBoolean.FALSE.equals(value)) {
	    valid = 0;
	}
	return valid;
    }
    
	/**
	 * Return a plate with coordinate
	 * @param code
	 * @return
	 */
	public Plate getPlate(String code) {
		Logger.info("pl_PlaqueSolexa @plaqueId="+code);
		List<Plate> plates = this.jdbcTemplate.query("pl_PlaqueSolexa @plaqueId=?", new Object[]{code}, new RowMapper<Plate>() {
	        public Plate mapRow(ResultSet rs, int rowNum) throws SQLException {
	            	Plate plate = new Plate();
	        	//well.plateCode = rs.getString("plaqueId");
	        	plate.code = rs.getString("plaqueId");
	        	plate.typeCode = rs.getInt("emnco");
	        	plate.typeName = rs.getString("emnnom");
	        	plate.nbWells = rs.getInt("nombrePuitUtilises");
	        	plate.validQC = getTBoolean(rs.getInt("valqc"));
	        	plate.validRun = getTBoolean(rs.getInt("valrun"));
	        	plate.comment = rs.getString("plaquecom");
	        	plate.creationDate = rs.getDate("plaquedc");
	        	plate.modificationDate = rs.getDate("plaquedm");
	        	plate.creationUserId = rs.getInt("percodc");
	        	plate.modificationUserId = rs.getInt("percodm");
	        	plate.creationUser = getUser(plate.creationUserId);
	        	plate.modificationUser = getUser(plate.modificationUserId);
	        	
	        	
	            return plate;
	        }

			
	    });


		if(plates.size() == 1){
			Plate plate = plates.get(0);
			Logger.info("pl_MaterielmanipPlaque @plaqueId="+plate.code);
			List<Well> wells = this.jdbcTemplate.query("pl_MaterielmanipPlaque @plaqueId=?", new Object[]{code}, new RowMapper<Well>() {
		        public Well mapRow(ResultSet rs, int rowNum) throws SQLException {
			    Well well = new Well();
			    well.name = rs.getString("matmanom");
			    well.code = rs.getInt("matmaco");
			    well.x = rs.getString("plaqueX");
			    well.y = rs.getString("plaqueY");
			    well.typeCode = rs.getInt("emnco");
			    well.typeName = rs.getString("emnnom");
			    well.valid = getTBoolean(rs.getInt("val"));
			    well.typeMaterial = rs.getString("tadnom");
			    return well;
		        }
		    });

			plate.wells = wells.toArray(new Well[wells.size()]);
			return plate;
		}else{
			return null;
		}
	}

	public User getUser(Integer id) {
		Logger.info("pl_PerintUn @perco="+id);
		List<User> users = this.jdbcTemplate.query("pl_PerintUn @perco=?", new Object[]{id}, new RowMapper<User>() {
	        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	User user = new User();
	        	user.perco = rs.getString("perco");
	        	user.perlog = rs.getString("perlog");
	            return user;
	        }
	    });
		
		if(users.size() == 1){
			return users.get(0);
		}else{
			return null;
		}
		
		
	}
	
	public List<User> getUsers() {
		List<User> users = this.jdbcTemplate.query("pl_Perint", new RowMapper<User>() {
	        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	User user = new User();
	        	user.perco = rs.getString("perco");
	        	user.perlog = rs.getString("perlog");
	            return user;
	        }
	    });
		return users;
	}
	
	public boolean isPlateExist(String code) {
		Logger.info("pl_PlaqueSolexa @plaqueId="+code);
		List<Plate> plates = this.jdbcTemplate.query("pl_PlaqueSolexa @plaqueId=?", new Object[]{code}, new RowMapper<Plate>() {
	        public Plate mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	Plate plate = new Plate();
	        	plate.code = rs.getString("plaqueId");
	            return plate;
	        }
	    });
		return (plates.size() > 0);
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


	public void deletePlate(String plateCode) {
		Logger.info("ps_PlaqueSolexa @plaqueId="+plateCode);
		this.jdbcTemplate.update("ps_PlaqueSolexa @plaqueId=?", new Object[]{plateCode});
	}



}

