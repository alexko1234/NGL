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
        private SimpleJdbcCall tracenameCaller;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.tracenameCaller = new SimpleJdbcCall(jdbcTemplate)
                                .withProcedureName("pl_MaterielmanipChoisi ")
                                                .withoutProcedureColumnMetaDataAccess()
                                                .useInParameterNames("@emnco","@prsco","@ematerielco")
                                                .returningResultSet("#result-set-1", new BeanPropertyRowMapper<Manip>(Manip.class))
                                                .declareParameters(
                                                		new SqlParameter("@prsco",Types.CHAR),
                                                		new SqlParameter("@bqanom",Types.CHAR),
                                                		new SqlParameter("@emnco",Types.TINYINT),
                                                		new SqlParameter("@ematerielco",Types.TINYINT),
                                                		new SqlParameter("@val",Types.TINYINT),
                                                		new SqlParameter("@adnco",Types.NUMERIC),
                                                		new SqlParameter("@tinsco",Types.SMALLINT),
                                                		new SqlParameter("@tseqco",Types.TINYINT),
                                                		new SqlParameter("@tbhdco",Types.VARCHAR),
                                                		new SqlParameter("@gmidnom",Types.VARCHAR),
                                                		new SqlParameter("@gmidco",Types.INTEGER),
                                                		new SqlParameter("@dated",Types.VARCHAR),
                                                		new SqlParameter("@datef",Types.VARCHAR),
                                                		new SqlParameter("@perco",Types.SMALLINT),
                                                		new SqlParameter("@proco",Types.INTEGER),
                                                		new SqlParameter("@ttpco",Types.SMALLINT),
                                                		new SqlParameter("@percom",Types.SMALLINT)
                                                );
        
    }


    public List<Manip> getManips(Integer emnco, Integer ematerielco,String prsco){
        MapSqlParameterSource in = new MapSqlParameterSource();
        if(prsco!=null){
        	in.addValue("@prsco",prsco);
        } else in.addValue("@prsco", null);
        in.addValue("@bqanom",null);
        in.addValue("@emnco",emnco);
        in.addValue("@ematerielco",ematerielco);
        in.addValue("@val",null);
        in.addValue("@adnco",null);
        in.addValue("@tinsco",null);
        in.addValue("@tseqco",null);
        in.addValue("@tbhdco",null);
        in.addValue("@gmidnom",null);
        in.addValue("@gmidco",null);
        in.addValue("@dated",null);
        in.addValue("@datef",null);
        in.addValue("@perco",null);
        in.addValue("@proco",null);
        in.addValue("@ttpco",null);
        in.addValue("@percom",null);

        Map<String, Object> out = tracenameCaller.execute(in);
//        System.out.println("getCallString()["+tracenameCaller.getCallString()+"]");

        List<Manip> results = (List<Manip>) out.get("#result-set-1");
        return results;
    }
    
    
    public void updatePlateCoordonates(Plate plate){
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


	public List<Plate> getPlaques(Integer emnco, String projetValue) {
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


	public Plate getPlate(String code) {
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

