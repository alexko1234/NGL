package controllers.migration.cng;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// pour test
import java.util.regex.Pattern;

import models.Constants;
import models.LimsCNGDAO;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.sample.instance.Sample;
import models.laboratory.sample.description.SampleType;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;


import play.Logger;
import play.mvc.Result;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

/**
 * correction des samples importés avant 15/09/2015 avec typeCode='default-sample-cng' alors qu'il est connu dans
 * la base source Solexa...
 * + modification des 'IP-samples' en 'IP'
 * @author fdsantos
 * 21/03/2017
 */

public class MigrationSampleType extends  CommonController {
	
	private static final String SAMPLE_COLL_NAME_BCK = InstanceConstants.SAMPLE_COLL_NAME + "_BCK_032017";
	private static final String CONTAINER_COLL_NAME_BCK = InstanceConstants.CONTAINER_COLL_NAME + "_BCK_032017";
	protected static LimsCNGDAO limsServices= Spring.getBeanOfType(LimsCNGDAO.class);
	
	public static Result migration() {	
		
		JacksonDBCollection<Sample, String> samplesCollBck = MongoDBDAO.getCollection(SAMPLE_COLL_NAME_BCK, Sample.class);
		JacksonDBCollection<Sample, String> containersCollBck = MongoDBDAO.getCollection(CONTAINER_COLL_NAME_BCK, Sample.class);
		
		if ( (samplesCollBck.count() == 0) && (containersCollBck.count() == 0) ){
			// collections backup vide (inexistante ??)=> faire les backup
			backUpCollections();
			
			Logger.info("Migration sampleTypeCodes début...");
			try {
				migrationMissingSampleTypeCode();
				migrationUpdateSampleTypeCodeIP();
			}
			catch(Exception e) {
				Logger.error(e.getMessage());
				e.printStackTrace();
				e.getCause().printStackTrace();	
			}
									
		} else {
			Logger.info("Migration sampleTypeCodes déjà effectuée !");
		}		
		Logger.info("Migration sampleTypeCode fin");
		
		// est affiché dans le naviguateur
		return ok("Migration sampleTypesCodes OK");
	}

	private static void backUpCollections() {
		Logger.info("\tCopie "+InstanceConstants.SAMPLE_COLL_NAME+" starts");
		MongoDBDAO.save(SAMPLE_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList());
		Logger.info("\tCopie "+InstanceConstants.SAMPLE_COLL_NAME+" ended");
	
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" starts");
		MongoDBDAO.save(CONTAINER_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" ended");
	}
	
    // OK !!!!
	private static void migrationMissingSampleTypeCode() throws DAOException{
		
		Logger.info("-1- samples avec typeCode n'ayant pas été correctement importé");
		//ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
		
		// lister les samples dont le typeCode ="default-sample-cng"
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,DBQuery.is("typeCode","default-sample-cng")).toList();
		Logger.debug("Nb de containers a corriger : "+samples.size());
		
		// trouver les vrais typeCodes de tous les samples dans Solexa et stocker dans une map
		Map<String, String> results=limsServices.findOldSampleTypes();
		Logger.debug("Nb de old sample: "+results.size());
		
		for ( Sample samp:samples){
			
			 // trouver le sample dans la map
			 if (results.containsKey(samp.code)) {
				 
				String typeCode=results.get(samp.code);
				//Logger.debug("sample barcode "+ samp.code + " trouvé. son type est: "+ typeCode ); 
				SampleType sampleType=null;

				try { 
				  sampleType = SampleType.find.findByCode( typeCode );
				 } catch (DAOException e) {
				 	Logger.debug("OOOOOOOOOOPS...",e);
				}
				
				if ( sampleType==null ) {
					///contextValidation.addErrors("code", "error.codeNotExist", samp.code, typeCode );
					Logger.debug("...typeCode pas trouvé:"+ typeCode +"");
				} else {
					//Logger.debug("...typeCode =>"+ sampleType.code+ " categoryCode=>"+ sampleType.category.code);
					MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",samp.code), 
						                                                                DBUpdate.set("typeCode", sampleType.code ).set("categoryCode", sampleType.category.code ));
				}
			 }
		}
		Logger.info("-1- done");
	}	
		
   private static void migrationUpdateSampleTypeCodeIP() throws DAOException{
	   
	   Logger.info("-2- update 'IP-sample' SampleTypeCode");
	   //ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
	   
	   // NON LES SAMPLES ONT été importés  a default-sample-cng" et corrigés par migrationMissingSampleTypeCode...
	   // les contents correspondants doivent etre corrigés dans Container ou le type IP-sample doit changé a IP
	   List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,DBQuery.is("typeCode","IP")).toList();
	   Logger.debug("Nb de containers a mettre a jour : "+samples.size());
	   int i=1;
	   
	   for ( Sample samp:samples){  
		   Logger.debug(i+"> mettre a jour les container contenant >"+samp.code+"<");
 
	       //-b-lister les containers contenant ce sample et les mettre a jour
		   List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
		                                       DBQuery.is("contents.sampleCode",  samp.code    )).toList();   
   
	       Logger.debug("Nb de containers a mettre a jour : "+containers.size());
	      
	  	   for (Container cont:containers) {
				Logger.debug("...trouvé dans container: "+ cont.code );
			
			    cont.contents.stream().forEach(c->{
						
					if (c.sampleCode.equals(samp.code))
					{
						Logger.debug(" ... contents "+c.sampleCode +" / " + c.sampleTypeCode + " / "+  c.sampleCategoryCode + " ");
						MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.and(DBQuery.is("code",cont.code), DBQuery.is("contents.sampleCode",samp.code)),                                         
                                                                                                  DBUpdate.set("contents.sampleTypeCode", "IP" ));
						
					}
				});
		   }	 
		   
		   i++;
	   }
   }
	
}