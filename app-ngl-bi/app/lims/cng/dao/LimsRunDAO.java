package lims.cng.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import lims.models.experiment.Experiment;
import lims.models.instrument.Instrument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class LimsRunDAO {
	private SimpleJdbcTemplate jdbcTemplate;
	
    @Autowired
    @Qualifier("lims")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);       
    }
    /**
     * Return the list of active sequencer
     * @return
     */
    
    public List<Instrument> getInstruments(){
    	
    	
    	String sql = "SELECT DISTINCT m.pc_name as code, m.run_path as path, mt.name as categoryCode, 1 as active  " +
    			"FROM t_machine m JOIN t_machine_type mt ON m.type_id=mt.id WHERE mt.type in ('HS','H2') AND (m.status=2 OR m.status=1) " +
    			"ORDER BY m.pc_name";
    	BeanPropertyRowMapper<Instrument> mapper = new BeanPropertyRowMapper<Instrument>(Instrument.class);
    	return this.jdbcTemplate.query(sql, mapper);
    }
    
    public List<LimsExperiment> getExperiments(Experiment experiment){

    	if(null != experiment.date){
	    	String sql = "SELECT m.pc_name as code, min(w.start_date) as date, mt.name as categoryCode, f.nb_cycles" //, f.nb_lanes , mt.type as seq_type, m.model as seq_model
	    				+" FROM t_flowcell f"
	    				+" JOIN t_workflow w on w.flowcell_id=f.id"
	    				//+" JOIN t_stage s on w.stage_id=s.id and ( s.workflow='SEQ' and s.name='Read1')" après passage 2500
	    				+" JOIN t_machine m on w.machine_id=m.id"
	    				+" JOIN t_machine_type mt on ( m.type_id=mt.id AND mt.type IN ('GA','HS','H2','MS', 'NS') )"
	    				+" WHERE f.barcode=? and w.start_date between ? and ?"
	    				+" GROUP BY m.pc_name,  mt.name, f.nb_cycles";
	    	BeanPropertyRowMapper<LimsExperiment> mapper = new BeanPropertyRowMapper<LimsExperiment>(LimsExperiment.class);
	    	return this.jdbcTemplate.query(sql, mapper, experiment.containerSupportCode, minus(experiment.date,5), add(experiment.date,5));    	
	    	
    	}else{
    		String sql = "SELECT m.pc_name as code,  min(w.start_date) as date, mt.name as categoryCode, f.nb_cycles" //, f.nb_lanes , mt.type as seq_type, m.model as seq_model
    				+" FROM t_flowcell f"
    				+" JOIN t_workflow w on w.flowcell_id=f.id"
    				//+" JOIN t_stage s on w.stage_id=s.id and ( s.workflow='SEQ' and s.name='Read1')" après passage 2500
    				+" JOIN t_machine m on w.machine_id=m.id"
    				+" JOIN t_machine_type mt on  ( m.type_id=mt.id AND mt.type IN ('GA','HS','H2','MS', 'NS') )"
    				+" WHERE f.barcode=?"
    				+" GROUP BY m.pc_name,  mt.name, f.nb_cycles";
    		BeanPropertyRowMapper<LimsExperiment> mapper = new BeanPropertyRowMapper<LimsExperiment>(LimsExperiment.class);
        	return this.jdbcTemplate.query(sql, mapper, experiment.containerSupportCode);    	
    	}
    	
    }
    /*
     * 
     *
     * SELECT l.number as lane_number, et.name as exp_name, s.barcode as aliquot_barcode, s.stock_barcode, i.short_name as index_short,
i.sequence as index_sequence , sl.size, fn_getsampleid_project(s.id) as project_names
---,ind.name
FROM t_flowcell f
JOIN t_lane l ON l.flowcell_id=f.id
JOIN t_sample_lane sl ON sl.lane_id=l.id
JOIN t_sample s ON sl.sample_id=s.id
JOIN t_exp_type et on sl.exp_type_id=et.id
JOIN t_individual ind on s.individual_id=ind.id
LEFT OUTER JOIN t_index i ON sl.index=i.cng_name
WHERE f.barcode='$fcbarcode'
ORDER BY l.number
     */
    
    private Date minus(Date date, int nbDay) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());		
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - nbDay);
    	return c.getTime();
	}
    
    private Date add(Date date, int nbDay) {
    	Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());		
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + nbDay);
    	return c.getTime();
	}
    
    
	public List<LimsLibrary> geContainerSupport(String supportCode){
		/*
		 SELECT l.number as lane_number, et.name as exp_name, s.barcode as aliquot_barcode, s.stock_barcode, i.short_name as index_short,
			i.sequence as index_sequence, i.type as index_type
			FROM t_flowcell f
			JOIN t_lane l ON l.flowcell_id=f.id
			JOIN t_sample_lane sl ON sl.lane_id=l.id
			JOIN t_sample s ON sl.sample_id=s.id
			JOIN t_exp_type et on sl.exp_type_id=et.id
			JOIN t_individual ind on s.individual_id=ind.id
			JOIN t_index i ON ((sl.index=i.cng_name OR sl.index=i.short_name) AND i.type !=3 )
			WHERE f.barcode='$fcbarcode'
			ORDER BY l.number"; 

		 */
		
		
    	String sql = "SELECT distinct l.number as laneNumber, et.short_name as experimentTypeCode, s.stock_barcode as sampleBarCode," +
    			" i.short_name as indexName, i.type as indexTypeCode, i.sequence as indexSequence ,ind.name as sampleCode, sl.size as insertLength, fn_getsampleid_project(s.id) as projectCode"
    			+ " FROM t_flowcell f"
    			+ " JOIN t_lane l ON l.flowcell_id=f.id"
    			+ " JOIN t_sample_lane sl ON sl.lane_id=l.id"
    			+ " JOIN t_sample s ON sl.sample_id=s.id"
    			+ " JOIN t_exp_type et on sl.exp_type_id=et.id"
    			+ " JOIN t_individual ind on s.individual_id=ind.id"
    			+ " LEFT OUTER JOIN t_index i ON (sl.index=i.cng_name OR sl.index=i.short_name)"
    			+ " WHERE f.barcode=?"
    			+ " ORDER BY l.number";
    	BeanPropertyRowMapper<LimsLibrary> mapper = new BeanPropertyRowMapper<LimsLibrary>(LimsLibrary.class);
    	return this.jdbcTemplate.query(sql, mapper, supportCode);    	
    	
    }
}
