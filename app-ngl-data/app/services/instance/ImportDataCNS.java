package services.instance;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import fr.cea.ig.play.migration.NGLContext;
import scala.concurrent.duration.Duration;
import services.instance.container.BanqueAmpliImportCNS;
import services.instance.container.SizingImportCNS;
import services.instance.container.TubeImportCNS;
import services.instance.container.UpdateTaraPropertiesCNS;
import services.instance.parameter.IndexImportCNS;
import services.instance.project.ProjectImportCNS;
import services.instance.sample.UpdateReportingData;
import services.instance.sample.UpdateSampleCNS;
import services.instance.sample.UpdateSampleNCBITaxonCNS;
import services.instance.sample.UpdateSamplePropertiesCNS;
import services.ncbi.TaxonomyServices;

public class ImportDataCNS {

	@Inject
	public ImportDataCNS(NGLContext ctx){
		// Import Projects tous les jours à 16h00
		new ProjectImportCNS(ImportDataUtil.getDurationForNextHour(0),Duration.create(1,TimeUnit.HOURS), ctx);
		new TubeImportCNS(ImportDataUtil.getDurationForNextHour(10),Duration.create(1,TimeUnit.HOURS), ctx);
		new UpdateSampleCNS(ImportDataUtil.getDurationForNextHour(20),Duration.create(1,TimeUnit.HOURS), ctx);
		
		new BanqueAmpliImportCNS(Duration.create(5,TimeUnit.SECONDS),Duration.create(5,TimeUnit.MINUTES), ctx);
		new SizingImportCNS(Duration.create(10,TimeUnit.SECONDS),Duration.create(5,TimeUnit.MINUTES), ctx);
		
		
		//Update/Create Container
		//	new PrepaflowcellImportCNS(Duration.create(2,TimeUnit.MINUTES),Duration.create(15,TimeUnit.MINUTES));
	    //new RunImportCNS(Duration.create(5,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
	    //new UpdateReadSetCNS(Duration.create(6,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
	    //Update State and Tara Properties
	    
	    
		
		//new SolutionStockImportCNS(Duration.create(30,TimeUnit.SECONDS),Duration.create(10,TimeUnit.MINUTES));
		//new UpdateSolutionStockCNS(Duration.create(20,TimeUnit.SECONDS),Duration.create(5,TimeUnit.MINUTES));
		
		//Update NCBI scientificName and lineage for Sample
		
			
		
		/*
		new UpdateSizingCNS(Duration.create(1,TimeUnit.MINUTES),Duration.create(10,TimeUnit.MINUTES));
		new UpdateAmpliCNS(Duration.create(1,TimeUnit.MINUTES),Duration.create(10,TimeUnit.MINUTES));
		*/
		new UpdateSampleNCBITaxonCNS(ImportDataUtil.getDurationForNextHour(15),Duration.create(6,TimeUnit.HOURS), ctx, new TaxonomyServices(ctx));
		new UpdateSamplePropertiesCNS(ImportDataUtil.getDurationForNextHour(45),Duration.create(6,TimeUnit.HOURS), ctx);
		
		//new RunExtImportCNS(ImportDataUtil.getDurationInMillinsBefore(12, 30),Duration.create(12,TimeUnit.HOURS), ctx);
		
		new UpdateReportingData(ImportDataUtil.getDurationInMillinsBefore(20, 0),Duration.create(1,TimeUnit.DAYS), ctx);
		new UpdateTaraPropertiesCNS(ImportDataUtil.getDurationInMillinsBefore(4, 0),Duration.create(1,TimeUnit.DAYS), ctx);
	    new IndexImportCNS(ImportDataUtil.getDurationInMillinsBefore(5, 0),Duration.create(1,TimeUnit.DAYS), ctx);
		
	}

	
}
