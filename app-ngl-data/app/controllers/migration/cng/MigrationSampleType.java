package controllers.migration.cng;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * + au passage modification des 'IP-samples' en 'IP'
 * @author fdsantos
 * 21/03/2017
 */

public class MigrationSampleType extends  CommonController {
	
	private static final String SAMPLE_COLL_NAME_BCK = InstanceConstants.SAMPLE_COLL_NAME + "_BCK_032117";
	protected static LimsCNGDAO limsServices= Spring.getBeanOfType(LimsCNGDAO.class);
	
	public static Result migration() {	
		JacksonDBCollection<Sample, String> samplesCollBck = MongoDBDAO.getCollection(SAMPLE_COLL_NAME_BCK, Sample.class);
		if (samplesCollBck.count() == 0) {
			// collection backup vide (inexistante ??)=> faire le backup
			//backUpSampleColl();
			
			Logger.info("Migration sample TypeCode starts");
			try {
				migrationMissingSampleTypeCode();
				migrationUpdateSampleTypeCode();
			}
			catch(Exception e) {
				Logger.error(e.getMessage());
			}
									
		} else {
			Logger.info("Migration sample TypeCode  already executed !");
		}		
		Logger.info("Migration sample TypeCode  ended");
		
		// est affiché dans le naviguateur
		return ok("Finish");
	}

	private static void backUpSampleColl() {
		Logger.info("\tCopie "+InstanceConstants.SAMPLE_COLL_NAME+" starts");
		MongoDBDAO.save(SAMPLE_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class).toList());
		Logger.info("\tCopie "+InstanceConstants.SAMPLE_COLL_NAME+" ended");
	}
	

	private static void migrationMissingSampleTypeCode() throws DAOException{
		
		ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
		
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,DBQuery.is("typeCode","default-sample-cng")).toList();
		Logger.debug("Nb de containers to update : "+samples.size());
		
		Map<String, String> results=limsServices.findOldSampleTypes();
		Logger.debug("Nb de old sample: "+results.size());
		
		
		////////////////////////////////////////////TEST

		final String SAMPLE_USED_TYPE_CODE = "default-sample-cng";	
		String typeCode = SAMPLE_USED_TYPE_CODE;
		SampleType sampleType=null;
		 
		 //String typeCode=results.get(samp.code);
		 //TEST HARCODED
		 ///String typeCode="default-sample-cng";
		 Logger.debug(" trouver les infos sur le sample TypeCode"+ typeCode+ "");
		 
		try { 
		  //sampleType = SampleType.find.findByCode( results.get(samp.code));
		  sampleType = SampleType.find.findByCode( typeCode );
		 } catch (DAOException e) {
		 	Logger.debug("OOOOOOOOOOPS...",e);
		}
		
		if ( sampleType==null ) {
			///contextValidation.addErrors("code", "error.codeNotExist", samp.code, typeCode );
			Logger.debug("...typeCode pas trouvé:"+ typeCode +"");
		} else {
			Logger.debug("...typeCode =>"+ sampleType.code+ " categoryCode=>"+ sampleType.category.code);
			///MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",samp.code), 
			//	                                                            DBUpdate.set("typeCode", sampleType.code ).set("categoryCode", sampleType.category.code ));
		}
		
		
		
		/*
		for ( Sample samp:samples){
			 if (results.containsKey( samp.code )) {
				 Logger.debug("sample barcode "+ samp.code + "trouvé. son type est: "+ results.get(samp.code) +" !");
				  
				/* SampleType sampleType=null;
				 
				 //String typeCode=results.get(samp.code);
				 //TEST HARCODED
				 String typeCode="default-sample-cng";
				 Logger.debug(" trouver les infos sur "+ typeCode+ "");
				 
				try { 
				  //sampleType = SampleType.find.findByCode( results.get(samp.code));
				  sampleType = SampleType.find.findByCode( typeCode );
				 } catch (DAOException e) {
				 	Logger.debug("OOOOOOOOOOPS...",e);
				}
				
				if ( sampleType==null ) {
					///contextValidation.addErrors("code", "error.codeNotExist", samp.code, typeCode );
					Logger.debug("...typeCode pas trouvé:"+ typeCode +"");
				} else {
					Logger.debug("...typeCode =>"+ sampleType.code+ " categoryCode=>"+ sampleType.category.code);
					///MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",samp.code), 
					//	                                                            DBUpdate.set("typeCode", sampleType.code ).set("categoryCode", sampleType.category.code ));
				}
			
			 }
		}
		*/
	}	
		
   private static void migrationUpdateSampleTypeCode() throws DAOException{
		
	   ContextValidation contextValidation=new ContextValidation(Constants.NGL_DATA_USER);
	
	   
	   /*  TODO
	 // au passage modification des 'IP-samples' en 'IP'
	 // ils ont été importés en sampleTypeCode= IP-sample / sampleCategoryCode=IP-sample
	 // => chger  sampleTypeCode= IP et laisser sampleCategoryCode tel quel
	 
				 // lister les containers contenant ce sample
				 List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("contents.sampleCode",samp.code)).toList();
	
				 // parcourir les contents pour trouver
				 for ( Container cont:containers) {
					 //Logger.debug("...trouvé dans container: "+ cont.code + " ");
					 
					 // mettre le type venant de la base Solexa dans la collection Mongo
					 cont.typeCode=results.get(samp.code);
					 
                     // cas particulier pour les IP, dans la collection container ils ont été importés en sampleTypeCode= IP-sample / sampleCategoryCode=IP-sample
					 // => chger  sampleTypeCode= IP et laisser sampleCategoryCode
					 
					 if ( results.get(samp.code).equals( "IP")){
						 // trouver
					 }
					 cont.contents.stream().forEach(c->{
						
						 if (c.sampleCode.equals(samp.code))
						 {
							 //Logger.debug(" ... contents "+c.sampleCode +" / " + c.sampleTypeCode + " / "+  c.sampleCategoryCode + " ");
							 
							 
							 // aucun cas vu...
							 //if ( ! c.sampleTypeCode.equals (results.get(samp.code) )){
							 //	 Logger.debug("=> container a mettre a jour !!!");
							 //}
								 
						 }
					 });
				 }	
	 
			 } else {
				 Logger.debug("sample barcode "+ samp.code + "pas trouvé...");
			 }

		 }
		 
		*/
	}
	
	
	
	/* modifier aussi et les containers qui ont ces samples en contents ?????????
	for(Container container:containers){
		
		for(Content content:container.contents){
		
		/*	content.referenceCollab=sampleRefCollab.get(content.sampleCode);
			
			if(content.sampleTypeCode.equals("default-sample-cns")){
	
   */
}