package services.instance;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.cea.ig.play.migration.NGLContext;
import models.Constants;
import models.utils.InstanceHelpers;
//import models.utils.play;
import scala.concurrent.duration.Duration;
import services.instance.container.PoolImportGET;
//import services.instance.container.BanqueAmpliImportGET;
//import services.instance.container.PrepaflowcellImportGET;
//import services.instance.container.SizingImportGET;
//import services.instance.container.SolutionStockImportGET;
import services.instance.container.TubeImportGET;
//import services.instance.container.UpdateSolutionStockGET;
//import services.instance.container.UpdateTaraPropertiesGET;
import services.instance.user.UserImportGET;
import services.instance.parameter.IndexImportGET;
import services.instance.project.ProjectImportGET;
import services.instance.resolution.ResolutionServiceGET;
import services.instance.container.SampleImportGET;
import services.instance.container.puitsPlaqueImportGET;
//import services.instance.run.RunExtImportGET;
//import services.instance.run.RunImportGET;
//import services.instance.run.UpdateReadSetGET;
//import services.instance.sample.UpdateSampleGET;
//import services.instance.resolution.ResolutionService;
import play.data.validation.ValidationError;
//import play.Logger;
import validation.ContextValidation;

public class ImportDataGET{

	public static final play.Logger.ALogger logger = play.Logger.of(ImportDataGET.class);
	
	public ImportDataGET(NGLContext ctx){
		logger.error("ImportDataGET");
/*
 * 		ResolutionService
 * 		Créé dans la collection mongo (ngl_common.ResolutionConfiguration) les résolutions à indiquer à la fin d'expérience 
 * 		n'ont pas besoin d'être importés régulièrement
 */
//  	new ResolutionServiceGET();
//		Map<String,List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();	
//		ContextValidation ctx = new ContextValidation(Constants.NGL_DATA_USER);
//		ctx.setCreationMode();
//		try {
//			ResolutionServiceGET.main(ctx); 
//			if (ctx.errors.size() > 0) {
//				Logger.error(ctx.errors.size() + " erreurs : " + errors);
//			} 
//		} catch (Exception e) {
//			Logger.error(e.getMessage(), e);
//		}

//		new ProjectImportGET(Duration.create(1,TimeUnit.SECONDS),Duration.create(24,TimeUnit.HOURS), ctx); 
////		//Import Index
//		new IndexImportGET(Duration.create(1,TimeUnit.SECONDS),Duration.create(24,TimeUnit.HOURS), ctx); 
//		
//		new UserImportGET(Duration.create(1,TimeUnit.SECONDS),Duration.create(24,TimeUnit.HOURS), ctx);
 
		//Update/Create Container
//		new TubeImportGET(Duration.create(5,TimeUnit.SECONDS),Duration.create(15,TimeUnit.MINUTES),ctx); 
////		//new SampleImportGET(Duration.create(10,TimeUnit.SECONDS),Duration.create(15,TimeUnit.MINUTES));
//		new puitsPlaqueImportGET(Duration.create(10,TimeUnit.SECONDS),Duration.create(15,TimeUnit.MINUTES), ctx);
//		new PoolImportGET(Duration.create(15,TimeUnit.SECONDS),Duration.create(15,TimeUnit.MINUTES),ctx);
	}

}
