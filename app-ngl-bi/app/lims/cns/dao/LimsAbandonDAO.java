package lims.cns.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;



import lims.models.runs.EtatTacheHD;
import lims.models.runs.TacheHD;
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
public class LimsAbandonDAO {
        private JdbcTemplate jdbcTemplate;


    @Autowired
    @Qualifier("lims")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<TacheHD> listTacheHD(String readSetCode){
    	Logger.info("pl_TachehdUnLotseq @lseqnom='"+readSetCode);
        List<TacheHD> results = this.jdbcTemplate.query("pl_TachehdUnLotseq @lseqnom=?",
        		new Object[]{readSetCode},new BeanPropertyRowMapper<TacheHD>(TacheHD.class));
        return results;
    }
    
    public List<EtatTacheHD> listEtatTacheHD(){
    	Logger.info("pl_Etachehd");
        List<EtatTacheHD> results = this.jdbcTemplate.query("pl_Etachehd",new BeanPropertyRowMapper<EtatTacheHD>(EtatTacheHD.class));
        return results;
    }
    
    
    public void updateRunAbandon(String runhnom, Integer runhabandon, Integer cptreco){
    	Logger.info("pm_RunhdAbandon @runhnom='"+runhnom+"', @runhabandon="+runhabandon+", @cptreco="+cptreco );
    	this.jdbcTemplate.update("pm_RunhdAbandon @runhnom=?, @runhabandon=?, @cptreco=?", new Object[]{runhnom, runhabandon, cptreco});
    }
    
    public void updatePisteAbandon(String runhnom, Integer pistnum, Integer pistabandon, Integer cptreco){
    	Logger.info("pm_PisteAbandon @runhnom='"+runhnom+"', @pistnum="+pistnum+"', @pistabandon="+pistabandon+", @cptreco="+cptreco );
    	this.jdbcTemplate.update("pm_PisteAbandon @runhnom=?, @pistnum=?, @pistabandon=?, @cptreco=?", new Object[]{runhnom, pistnum, pistabandon, cptreco});
    }
    
    public void updateLotsequenceAbandon(String lseqnom, Integer lseqval, Integer cptreco, Integer tacco, Integer etacco){
    	Logger.info("pm_LotsequenceAbandon @lseqnom='"+lseqnom+"', @lseqval="+lseqval+", @cptreco="+cptreco+", @tacco="+tacco+", @etacco="+etacco );
    	this.jdbcTemplate.update("pm_LotsequenceAbandon @lseqnom=?, @lseqval=?, @cptreco=?, @tacco=?, @etacco=?", new Object[]{lseqnom, lseqval, cptreco, tacco, etacco});
    }
    
    public void updateLotsequenceAbandonBI(String lseqnom, Integer lseqabandonbi){
    	Logger.info("pm_LotsequenceAbandonBI @lseqnom='"+lseqnom+"', @lseqabandonbi="+lseqabandonbi);
    	this.jdbcTemplate.update("pm_LotsequenceAbandonBI @lseqnom=?, @lseqabandonbi=?", new Object[]{lseqnom, lseqabandonbi});
    }
    
    
    
    /*
    public List<Manip> findManips(Integer emnco, Integer ematerielco,String prsco){
    	Logger.info("pl_MaterielmanipChoisi @prsco='"+prsco+"', @emnco="+emnco+", @ematerielco="+ematerielco+", @plaque=1 ");
        List<Manip> results = this.jdbcTemplate.query("pl_MaterielmanipChoisi @prsco=?, @emnco=?, @ematerielco=?, @plaque=?",
        		new Object[]{prsco, emnco, ematerielco, 1},new BeanPropertyRowMapper<Manip>(Manip.class));
        return results;
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

    public List<Plate> findPlates(Integer emnco, String projetValue) {
    	Logger.info("pl_PlaqueSolexa @prsco="+projetValue+", @emnco="+emnco);
		List<Plate> plates = this.jdbcTemplate.query("pl_PlaqueSolexa @prsco=?, @emnco=?", new Object[]{projetValue, emnco}, new RowMapper<Plate>() {
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
	        	
	            return plate;
	        }

		
	    });
		return plates;
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
	*/


}

