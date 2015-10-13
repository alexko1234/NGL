package lims.cns.dao;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import lims.models.LotSeqValuation;
import lims.models.experiment.Experiment;
import lims.models.experiment.illumina.BanqueSolexa;
import lims.models.experiment.illumina.DepotSolexa;
import lims.models.experiment.illumina.LaneSolexa;
import lims.models.experiment.illumina.RunSolexa;
import lims.models.runs.EtatTacheHD;
import lims.models.runs.LimsFile;
import lims.models.runs.ResponProjet;
import lims.models.runs.TacheHD;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TransientState;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import play.Logger;
import fr.cea.ig.MongoDBDAO;


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
    
    public LotSeqValuation getLotsequenceValuation(String lseqnom){
    	Logger.info("pl_LotsequenceValuationToNGL @lseqnom="+lseqnom);
        LotSeqValuation results = this.jdbcTemplate.queryForObject("pl_LotsequenceValuationToNGL @lseqnom=?",
        		new Object[]{lseqnom}, new BeanPropertyRowMapper<LotSeqValuation>(LotSeqValuation.class));
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


	public List<LimsExperiment> getExperiments(Experiment experiment) {
		BeanPropertyRowMapper<LimsExperiment> mapper = new BeanPropertyRowMapper<LimsExperiment>(LimsExperiment.class);
    	
		if(null != experiment.date){
			Logger.info("pl_DepotsolexaUneFCtoNGL @flowcellid='"+experiment.containerSupportCode+"', @daterun="+experiment.date);
			
			return this.jdbcTemplate.query("pl_DepotsolexaUneFCtoNGL @flowcellid=?, @daterun=?", mapper, experiment.containerSupportCode, experiment.date);
		}else{
			Logger.info("pl_DepotsolexaUneFCtoNGL @flowcellid='"+experiment.containerSupportCode+"'");
			
			return this.jdbcTemplate.query("pl_DepotsolexaUneFCtoNGL @flowcellid=?", mapper, experiment.containerSupportCode);
		}
        
	}


	public List<LimsLibrary> geContainerSupport(String supportCode) {
		// TODO Auto-generated method stub
		return null;
	}


	public DepotSolexa getDepotSolexa(String containerSupportCode,	String sequencingStartDate) {
		Logger.info("pl_DepotsolexaUneFC @flowcellid='"+containerSupportCode+"', @daterun="+sequencingStartDate);
		
		//BeanPropertyRowMapper<DepotSolexa> mapper = new BeanPropertyRowMapper<DepotSolexa>(DepotSolexa.class);
		
		
		RowMapper<DepotSolexa> mapper = new RowMapper<DepotSolexa>(){

			@Override
			public DepotSolexa mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				DepotSolexa ds = new DepotSolexa();
				ds.matmaco = rs.getInt("matmaco");
				ds.placo =  rs.getInt("placo");
				ds.num = rs.getInt("num");
				return ds;
			}
			
		};
		try{
			return this.jdbcTemplate.queryForObject("pl_DepotsolexaUneFC @flowcellid=?, @daterun=?", mapper, containerSupportCode, sequencingStartDate);
		}catch(EmptyResultDataAccessException e){
			return null;
		}
	}
    
	public List<BanqueSolexa> getBanqueSolexa(String containerSupportCode){
		Logger.info("pl_BanquesolexaUneFC @flowcellid='"+containerSupportCode);
		
		RowMapper<BanqueSolexa> mapper = new RowMapper<BanqueSolexa>(){

			@Override
			public BanqueSolexa mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				//b.banco,prsco=rtrim(b.prsco),b.bqanom,m.adnnom,s.tagkeyseq,mm.pairedend, r.lanenum , dps.placo, dps.num, r.laneco
				BanqueSolexa bs = new BanqueSolexa();
				bs.banco = rs.getInt("banco");
				bs.prsco =  rs.getString("prsco").trim();
				bs.adnnom = rs.getString("adnnom").trim();
				bs.lanenum = rs.getInt("lanenum");
				bs.laneco = rs.getInt("laneco");
				bs.tagkeyseq = rs.getString("tagkeyseq");
				return bs;
			}
			
		};
		return this.jdbcTemplate.query("pl_BanquesolexaUneFC @flowcellid=?", mapper, containerSupportCode);
	}
	
	
	public List<BanqueSolexa> getBanqueSolexaFlowcellNGL(String containerSupportCode){
		Logger.info("pl_BanquesolexaUneFlowcellNGL @matmanom="+containerSupportCode);
		RowMapper<BanqueSolexa> mapper = new RowMapper<BanqueSolexa>(){

			@Override
			public BanqueSolexa mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				//b.banco,prsco=rtrim(b.prsco),b.bqanom,m.adnnom,s.tagkeyseq,mm.pairedend, r.lanenum , dps.placo, dps.num, r.laneco
				BanqueSolexa bs = new BanqueSolexa();
				bs.banco = rs.getInt("banco");
				bs.prsco =  rs.getString("prsco").trim();
				bs.adnnom = rs.getString("adnnom").trim();
				bs.lanenum = rs.getInt("lanenum");
				bs.laneco = rs.getInt("laneco");
				bs.tagkeyseq = rs.getString("tagkeyseq");
				return bs;
			}
			
		};
		return this.jdbcTemplate.query("pl_BanquesolexaUneFlowcellNGL @matmanom=?", mapper, containerSupportCode);
	}
	
    /**
    * pc_Runsolexa
	*
    * @placo	 int,			//Planning 
    * @num		 smallint,		// Numero ligne 
    * @runslnom varchar(255),       // Nom du un 
	* @runsldc varchar(10), // Date de creation 
	* @runslddt varchar(10), // Date de debut de transfert  
	* @runsldft varchar(10),  // Date de fin de transfert 
	* @runsldispatch bit ,// Run dispatch o/n 
    * @runslnbcluster       numeric(12,0), // nb cluster 
    * @runslnbseq           int,// nb sequences 
    * @rnslnbbasetot        numeric(12,0), // nb bases total 
    * @runslposition varchar(10) =null, // Position 
    * @runslvrta varchar(50), // Version RTA 
    * @runslvflowcell varchar(50)=null, // Version Flowcell 
    * @runslctrlane smallint=null, // Controle lane 0 si toutes les lanes ont été utilisées 
	* @mismatch int =null //donne la possibilité de modifier la valeur de mismatch par défaut
	*
    **/
    public void insertRun(Run run, DepotSolexa ds){
    	RunSolexa rs = convertRunToRunSolexa(run, ds); 		
    	
    	Logger.info("insertRun : "+rs);
    	
    	this.jdbcTemplate.update("pc_Runsolexa @placo=?, @num=?, @runslnom=?, @runsldc=?, @runslddt=?, @runsldft=?, @runsldispatch=?, @runslnbcluster=?, "
    			+ "@runslnbseq=?, @rnslnbbasetot=?, @runslposition=?, @runslctrlane=?, @runslvrta=?, @runslvflowcell=?, @mismatch=?,@matmaco=?", 
    			rs.placo, rs.num, rs.runslnom, rs.runsldc, rs.runslddt, rs.runsldft, rs.runsldispatch, rs.runslnbcluster,
    			rs.runslnbseq, rs.rnslnbbasetot, rs.runslposition, rs.runslctrlane, rs.runslvrta, rs.runslvflowcell, rs.mismatch,ds.matmaco);
    	
    }

    public void insertLanes(List<Lane> lanes, DepotSolexa ds){
    	for(Lane lane : lanes){
    		LaneSolexa ls = convertLaneToLaneSolexa(lane, ds);
    		Logger.info("insertLanes : "+ls);
    		this.jdbcTemplate.update("pc_Lanemetrics @lanenum=?, @matmaco=?, @lmnbseq=?, @lmnbclustfiltr=?, @lmperseqfiltr=?, @lmnbclust=?, "
    				+ "@lmperclustfiltr=?, @lmnbbase=?, @lmnbcycle=?, @pistnbcycle=?, @lmphasing=?, @lmprephasing=?",
    				ls.lanenum, ls.matmaco, ls.lmnbseq, ls.lmnbclustfiltr, ls.lmperseqfiltr, ls.lmnbclust,
    				ls.lmperclustfiltr,ls.lmnbbase,ls.lmnbcycle,ls.pistnbcycle,ls.lmphasing,ls.lmprephasing);
    	}
    }
    
    public void deleteRun(String code){
    	this.jdbcTemplate.update("ps_RunsolexaEtMetricsUnNom ?", code);
    }
    
    public void deleteFlowcellNGL(String code){
    	Logger.debug("Delete flowcellNGL "+code);
    	this.jdbcTemplate.update("ps_FlowcellNGL @matmanom= ?", code);
    }
    
    private LaneSolexa convertLaneToLaneSolexa(Lane lane, DepotSolexa ds) {
    	/*
    	 * pc_Lanemetrics @lanenum=1, @matmaco=115893, @lmnbseq=21696101, @lmnbclustfiltr=21758161, @lmperseqfiltr=99.71, @lmnbclust=23222149, @lmperclustfiltr=93.70, @lmnbbase=13061052802, @lmnbcycle='301,301', @pistnbcycle=602, @lmphasing='NULL', @lmprephasing='NULL'
    	 */
    	LaneSolexa ls = new LaneSolexa();
    	ls.lanenum = lane.number;
    	ls.matmaco = ds.matmaco; 
    	ls.lmnbseq = (Long)getNGSRGProperty(lane.treatments.get("ngsrg"),"nbClusterInternalAndIlluminaFilter"); 
    	ls.lmnbclustfiltr= (Long)getNGSRGProperty(lane.treatments.get("ngsrg"),"nbClusterIlluminaFilter"); 
    	ls.lmperseqfiltr= (Double)getNGSRGProperty(lane.treatments.get("ngsrg"),"percentClusterInternalAndIlluminaFilter"); 
    	ls.lmnbclust= (Long)getNGSRGProperty(lane.treatments.get("ngsrg"),"nbCluster"); 
    	ls.lmperclustfiltr= (Double)getNGSRGProperty(lane.treatments.get("ngsrg"),"percentClusterIlluminaFilter"); 
    	ls.lmnbbase= (Long)getNGSRGProperty(lane.treatments.get("ngsrg"),"nbBaseInternalAndIlluminaFilter");
    	ls.lmnbcycle= (Integer)getNGSRGProperty(lane.treatments.get("ngsrg"),"nbCycleRead1")+","+(Integer)getNGSRGProperty(lane.treatments.get("ngsrg"),"nbCycleRead2"); 
    	ls.pistnbcycle= (Integer)getNGSRGProperty(lane.treatments.get("ngsrg"),"nbCycleRead1")+(Integer)getNGSRGProperty(lane.treatments.get("ngsrg"),"nbCycleRead2");
    	ls.lmphasing= (String)getNGSRGProperty(lane.treatments.get("ngsrg"),"phasing");
    	ls.lmprephasing= (String)getNGSRGProperty(lane.treatments.get("ngsrg"),"prephasing");
		return ls;
	}


	public RunSolexa convertRunToRunSolexa(Run run, DepotSolexa ds) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	
    	RunSolexa rs = new RunSolexa();
    	rs.placo = ds.placo;
    	rs.num = ds.num;
    	rs.runslnom = run.code;
    	rs.runsldc = sdf.format(run.sequencingStartDate);
    	rs.runslddt = sdf.format(getStateDate("IP-RG", run.state)); 		
    	rs.runsldft = sdf.format(getStateDate("F-RG", run.state));  		
    	rs.runsldispatch = run.dispatch; 
    	rs.runslnbcluster= (Long) getNGSRGProperty(run.treatments.get("ngsrg"),"nbClusterTotal");
    	rs.runslnbseq= (Long) getNGSRGProperty(run.treatments.get("ngsrg"),"nbClusterIlluminaFilter");
    	rs.rnslnbbasetot =(Long) getNGSRGProperty(run.treatments.get("ngsrg"),"nbBase");
    	rs.runslposition= (((String) getNGSRGProperty(run.treatments.get("ngsrg"),"flowcellPosition")).equals("-"))?null:(String) getNGSRGProperty(run.treatments.get("ngsrg"),"flowcellPosition"); 
    	rs.runslvrta = (String) getNGSRGProperty(run.treatments.get("ngsrg"),"rtaVersion"); 	
    	rs.runslvflowcell= (String) getNGSRGProperty(run.treatments.get("ngsrg"),"flowcellVersion");
    	rs.runslctrlane= (((Integer)getNGSRGProperty(run.treatments.get("ngsrg"),"controlLane")).equals(Integer.valueOf(0))?null:(Integer)getNGSRGProperty(run.treatments.get("ngsrg"),"controlLane")); 	
    	rs.mismatch= ((Boolean) getNGSRGProperty(run.treatments.get("ngsrg"),"mismatch")?1:0);
    	
    	if(!rs.validate()){
    		throw new RuntimeException("Validation RunSolexa Failed. "+rs);
    	};
    	
		return rs;
	}


	private Object getNGSRGProperty(Treatment treatment, String propertyCode) {
		return treatment.results().get("default").get(propertyCode).value;
	}


	private Date getStateDate(String stateCode, State state) {
		for(TransientState ts : state.historical){
			if(stateCode.equals(ts.code)){
				return ts.date;
			}
		}
		throw new RuntimeException("insertRun : missing state date : "+stateCode);
	}


	public void insertReadSet(ReadSet rs, BanqueSolexa bs) {
		/*
		  	@laneco int, 
			@banco int =null,   
			@tagkeyseq varchar(50), 
			@lseqnbseqval numeric(15,0),
			@lseqnbbase numeric(15,0), 
			@lbscoreQ30 numeric(5,2), 
			@lbscorequal numeric(5,2) 
		 */
		Logger.info("insertReadSet : "+rs.code);
		
		Long lseqnbseqval = (Long) getNGSRGProperty(rs.treatments.get("ngsrg"),"nbCluster");
		Long lseqnbbase = (Long) getNGSRGProperty(rs.treatments.get("ngsrg"),"nbBases");
		Double lbscoreQ30 = (Double) getNGSRGProperty(rs.treatments.get("ngsrg"),"Q30");
		Double lbscorequal =(Double) getNGSRGProperty(rs.treatments.get("ngsrg"),"qualityScore");
		
		Logger.debug("pc_Lanebanquehautdebit @laneco="+bs.laneco+", @banco="+bs.banco+", @tagkeyseq="+bs.tagkeyseq+", "
				+ "@lseqnbseqval="+lseqnbseqval+", @lseqnbbase="+lseqnbbase+", @lbscoreQ30="+lbscoreQ30+", @lbscorequal="+lbscorequal+",@lseqnom="+rs.code);
		
		this.jdbcTemplate.update("pc_Lanebanquehautdebit @laneco=?, @banco=?, @tagkeyseq=?, "
				+ "@lseqnbseqval=?, @lseqnbbase=?, @lbscoreQ30=?, @lbscorequal=?,@lseqnom=?",
				bs.laneco, bs.banco, bs.tagkeyseq, lseqnbseqval, lseqnbbase, lbscoreQ30, lbscorequal,rs.code);
		
	}


	public void insertFiles(ReadSet rs, boolean deleteAllBeforeInsert) {
		/*
		pc_Fichierlotseq

		@lseqco int, 
		@flotseqname varchar(90),
		@flotseqext varchar(9),
		@flotseqascii smallint =null, 
		@tfileco tinyint,
		@flabelco int,
		@futil bit
		*/
		
		if(deleteAllBeforeInsert){
			this.jdbcTemplate.update("ps_FichierlotseqUnlotsequence @lseqco = ?", getLseqco(rs));
		}
		
		for(File file:rs.files){
			
			//Logger.debug("select lseqco from Lotsequence l inner join Runhd r on r.runhco = l.runhco where lseqnom = '"+rs.code+"' and runhnom = '"+rs.runCode+"'");
			
			Integer lseqco = getLseqco(rs);
			Integer tfileco = convertTypeCode(file.typeCode);  //=
			Integer flabelco = convertLabel((String)file.properties.get("label").value);
			String flotseqname = file.fullname.replace("."+file.extension, "");
			
			
			this.jdbcTemplate.update("pc_Fichierlotseq @lseqco=?, @flotseqname=?, @flotseqext=?, "
					+ "@flotseqascii=?, @tfileco=?, @flabelco=?, @futil=?",
					lseqco, flotseqname, file.extension, Integer.valueOf((String)file.properties.get("asciiEncoding").value.toString()), tfileco, flabelco, file.usable);
					
		}
	}


	private Integer getLseqco(ReadSet rs) {
		return this.jdbcTemplate.queryForObject("select lseqco from Lotsequence l inner join Runhd r on r.runhco = l.runhco "
				+ "where lseqnom = ? and runhnom = ?",Integer.class, rs.code, rs.runCode);
	}

	public Boolean isLseqco(ReadSet rs){
		return (this.jdbcTemplate.queryForObject("select count(lseqco) from Lotsequence l inner join Runhd r on r.runhco = l.runhco "
				+ "where lseqnom = ? and runhnom = ?",Integer.class, rs.code, rs.runCode) > 0);
	}
	
	private Integer convertLabel(String value) {
		if("READ1".equalsIgnoreCase(value)){
			return 1;
		}else if("READ2".equalsIgnoreCase(value)){
			return 2;
		}else if("SINGLETON".equalsIgnoreCase(value)){
			return 3;
		}else{
			Logger.error("No label !!!"+value);
		}
		return null;
	}


	private Integer convertTypeCode(String typeCode) {
		if("RAW".equalsIgnoreCase(typeCode)){
			return 7;
		}else if("TRIM".equalsIgnoreCase(typeCode)){
			return 9;
		}else if("CLEAN".equalsIgnoreCase(typeCode)){
			return 10;
		}else if("NO_RIBO_CLEAN".equalsIgnoreCase(typeCode)){
			return 11;
		}else if("RIBO_CLEAN".equalsIgnoreCase(typeCode)){
			return 12;
		}else{
			Logger.error("No typeCode !!!"+typeCode);
		}
		return null;
	}


	public void dispatchRun(Run run) {
		/*
			pm_RunsolexaDispatch
			@runslnom varchar(255),	
	    	@runsldft	char(20) 
	    */
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
	    this.jdbcTemplate.update("pm_RunsolexaDispatch @runslnom=?, @runsldft=?", run.code, sdf.format(getStateDate("F-RG", run.state)));
	}


	public void updateRunInNGL(Run run) {
		this.jdbcTemplate.update("pm_RunhdInNGL  @runhnom=?", run.code);
		
	}


	public void updateRunEtat(Run run, int etat) {
		this.jdbcTemplate.update("pm_RunhdEtape @runhnom=?, @erunhco= ?", run.code, etat);
		
	}


	public void updateReadSetEtat(ReadSet readset, int etat) {
		this.jdbcTemplate.update("pm_LotsequenceEtape @lseqnom=?, @elseqco= ?", readset.code, etat);
		
	}

	public void updateReferenceFlowcell(String currentReference,String newReference){
		this.jdbcTemplate.update("pm_LotreactifReference @lotrearef=?, @lotrearefnew= ?", currentReference,newReference);
	}

	public void updateReadSetBaseUtil(ReadSet readset) {
		
		this.jdbcTemplate.update("pm_LotsequenceClean @lseqco=?, @lseqnbsequtil=?, @lseqnbbaseutil=?", getLseqco(readset), 
				getNGSRGProperty(readset.treatments.get("global"), "usefulSequences"), getNGSRGProperty(readset.treatments.get("global"), "usefulBases"));
	}

	public void updateReadSetArchive(ReadSet readset){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(null != readset.archiveId && null != readset.archiveDate){
			this.jdbcTemplate.update("pm_LotsequenceArchiveUneListe @lseqlist=?,@lseqssid=?,@lseqdarch=?", getLseqco(readset)+"", Long.valueOf(readset.archiveId), sdf.format(readset.archiveDate));
		}
	}
	
	public void linkRunWithMaterielManip(){
		this.jdbcTemplate.update("pc_Runmaterielmanip");
	}
	
	public List<RunSolexa> findRunMismatch(){
		RowMapper<RunSolexa> mapper = new RowMapper<RunSolexa>(){

			@Override
			public RunSolexa mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				RunSolexa ds = new RunSolexa();
				ds.runslnom = rs.getString("runslnom");
				ds.mismatch =  rs.getInt("mismatch");
				return ds;
			}
			
		};
		return this.jdbcTemplate.query("select runhnom as runslnom, mismatch from Runhd r inner join Runsolexa rs on rs.runhco = r.runhco", mapper);		
	}


	public DepotSolexa insertFlowcellNGL(models.laboratory.experiment.instance.Experiment expPrepaflowcell,models.laboratory.experiment.instance.Experiment expDepotIllumina, Run run) {
		
		//Nb Cycle total
		int nbCycles=Integer.valueOf(expDepotIllumina.instrumentProperties.get("nbCyclesRead1").value.toString())+
				Integer.valueOf(expDepotIllumina.instrumentProperties.get("nbCyclesReadIndex1").value.toString())+
				Integer.valueOf(expDepotIllumina.instrumentProperties.get("nbCyclesRead2").value.toString())+
				Integer.valueOf(expDepotIllumina.instrumentProperties.get("nbCyclesReadIndex2").value.toString());
				
		DepotSolexa ds = this.jdbcTemplate.query("pc_FlowcellNGL @matmanom=?, @sequenceur=?,@nbcycle=?,@nbpiste=?,@instrumentType=?"
												,new Object[]{expPrepaflowcell.outputContainerSupportCodes.toArray(new String[0])[0]
																,expDepotIllumina.instrument.code
																,nbCycles
																,expPrepaflowcell.atomicTransfertMethods.size()
																,run.typeCode},
				new RowMapper<DepotSolexa>() {
			public DepotSolexa mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				DepotSolexa value = new DepotSolexa();
				value.matmaco = rs.getInt("matmaco");
				return value;
			}
		}).get(0);
		
		 Logger.debug("Matmaco for new flowcellNGL "+ds.matmaco);
		 List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code",expPrepaflowcell.inputContainerSupportCodes)).toList();
		 if(CollectionUtils.isEmpty(containers)){
			 throw new RuntimeException("Container vide for "+expPrepaflowcell.inputContainerSupportCodes.toArray(new String[0])[0]);
		 }
		 
		 for(AtomicTransfertMethod atomicTransfertMethods: expPrepaflowcell.atomicTransfertMethods)
		 {
			 int laneNum=Integer.valueOf(atomicTransfertMethods.line);
			 for(ContainerUsed containerUsed : atomicTransfertMethods.inputContainerUseds){
				 Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerUsed.code);
				 int matmacos=Double.valueOf(container.properties.get("limsCode").value.toString()).intValue();
				 Logger.debug("Matmaco solution stock "+matmacos+" percentage "+containerUsed.percentage+", laneNum ="+laneNum);
				 Logger.debug("pc_DepotsolutionstockNGL @matmaco="+ds.matmaco+",@matmacos = "+matmacos+",@lanenum="+laneNum+",@rmatperpiste="+containerUsed.percentage);
				 this.jdbcTemplate.update("pc_DepotsolutionstockNGL @matmaco=?,@matmacos = ?,@lanenum=?,@rmatperpiste=?",ds.matmaco,matmacos,laneNum,containerUsed.percentage);

			 }
		 }
		return ds;
		
	}
	
	public List<LimsFile> getFiles(String readSetCode){
		RowMapper<LimsFile> mapper = new RowMapper<LimsFile>(){

			@Override
			public LimsFile mapRow(ResultSet rs, int rowNum)
					throws SQLException {
					LimsFile ds = new LimsFile();
					ds.fullname = rs.getString("fullname");
					ds.extension = rs.getString("extension");
					ds.asciiEncoding = rs.getString("asciiEncoding");
					ds.typeCode = rs.getString("typeCode");
					ds.label = rs.getString("label");
					ds.usable = rs.getBoolean("usable");
					return ds;					
			}
			
		};
		return this.jdbcTemplate.query("pl_FileUnReadSetToNGL @readSetCode = ?", mapper, readSetCode);		
	
	}
	
	
	public ResponProjet getResponProjet(String projectCode){
		RowMapper<ResponProjet> mapper = new RowMapper<ResponProjet>(){

			@Override
			public ResponProjet mapRow(ResultSet rs, int rowNum)
					throws SQLException {
					ResponProjet rp = new ResponProjet();
					rp.code = rs.getString("code_projet");
					rp.name = rs.getString("nom_projet");
					rp.biomanager = rs.getString("nom_bio").toUpperCase()+" "+rs.getString("pren_bio");
					rp.infomanager = rs.getString("nom_info").toUpperCase()+" "+rs.getString("pren_info");;
					
					return rp;					
			}
			
		};
		return this.jdbcTemplate.queryForObject("pl_ResponDuProjet @prsco = ?", mapper, projectCode);	
		
	}
	
}

