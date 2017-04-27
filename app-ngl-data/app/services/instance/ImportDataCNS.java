package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.container.BanqueAmpliImportCNS;
import services.instance.container.SizingImportCNS;
import services.instance.container.SolutionStockImportCNS;
import services.instance.container.TubeImportCNS;
import services.instance.container.UpdateAmpliCNS;
import services.instance.container.UpdateSizingCNS;
import services.instance.container.UpdateSolutionStockCNS;
import services.instance.container.UpdateTaraPropertiesCNS;
import services.instance.parameter.IndexImportCNS;
import services.instance.project.ProjectImportCNS;
import services.instance.run.RunExtImportCNS;
import services.instance.run.UpdateReadSetCNS;
import services.instance.sample.UpdateSampleCNS;
import services.instance.sample.UpdateSampleNCBITaxonCNS;
import services.instance.sample.UpdateSamplePropertiesCNS;

public class ImportDataCNS{

	public ImportDataCNS(){


	// Import Projects tous les jours à 16h00
		new ProjectImportCNS(Duration.create(4,TimeUnit.MINUTES),Duration.create(1,TimeUnit.HOURS));
		new IndexImportCNS(Duration.create(40,TimeUnit.SECONDS),Duration.create(1,TimeUnit.DAYS));
		
		//Update/Create Container
		new TubeImportCNS(Duration.create(1,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
	//	new PrepaflowcellImportCNS(Duration.create(2,TimeUnit.MINUTES),Duration.create(15,TimeUnit.MINUTES));
	    //new RunImportCNS(Duration.create(5,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
	    //new UpdateReadSetCNS(Duration.create(6,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
	    //Update State and Tara Properties
	    new UpdateTaraPropertiesCNS(Duration.create(1,TimeUnit.MINUTES),Duration.create(1,TimeUnit.DAYS));
	    
	    new UpdateSampleCNS(Duration.create(3,TimeUnit.MINUTES),Duration.create(1,TimeUnit.HOURS));
	    
		new RunExtImportCNS(Duration.create(10,TimeUnit.MINUTES),Duration.create(12,TimeUnit.HOURS));

		new SolutionStockImportCNS(Duration.create(30,TimeUnit.SECONDS),Duration.create(10,TimeUnit.MINUTES));
		new UpdateSolutionStockCNS(Duration.create(20,TimeUnit.SECONDS),Duration.create(5,TimeUnit.MINUTES));
		
		//Update NCBI scientificName and lineage for Sample
		new UpdateSampleNCBITaxonCNS(Duration.create(5,TimeUnit.MINUTES),Duration.create(6,TimeUnit.HOURS));
	
			
		new BanqueAmpliImportCNS(Duration.create(5,TimeUnit.SECONDS),Duration.create(5,TimeUnit.MINUTES));
		new SizingImportCNS(Duration.create(10,TimeUnit.SECONDS),Duration.create(5,TimeUnit.MINUTES));
		
		/*
		new UpdateSizingCNS(Duration.create(1,TimeUnit.MINUTES),Duration.create(10,TimeUnit.MINUTES));
		new UpdateAmpliCNS(Duration.create(1,TimeUnit.MINUTES),Duration.create(10,TimeUnit.MINUTES));
		*/
		
		new UpdateSamplePropertiesCNS(Duration.create(8,TimeUnit.MINUTES),Duration.create(6,TimeUnit.HOURS));
	}

}
