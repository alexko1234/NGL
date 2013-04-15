package ls.dao;


import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import ls.models.Manip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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
}

