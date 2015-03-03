package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.container.SolutionStockImportCNS;
import services.instance.container.UpdateSolutionStockCNS;

public class ImportDataCNS{

	public ImportDataCNS(){


		// Import Projects tous les jours à 16h00
		new ProjectImportCNS(Duration.create(ImportDataUtil.nextExecutionInSeconds(16,00),TimeUnit.SECONDS)
				,Duration.create(4,TimeUnit.HOURS));
		new IndexImportCNS(Duration.create(ImportDataUtil.nextExecutionInSeconds(3,00),TimeUnit.SECONDS)
				,Duration.create(7,TimeUnit.DAYS));
		
		//Update/Create Container
		new TubeImportCNS(Duration.create(1,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
		new PrepaflowcellImportCNS(Duration.create(2,TimeUnit.MINUTES),Duration.create(15,TimeUnit.MINUTES));
	    new RunImportCNS(Duration.create(5,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
	    new UpdateReadSetCNS(Duration.create(10,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
	    //Update State and Tara Properties
	    new UpdateTaraPropertiesCNS(Duration.create(ImportDataUtil.nextExecutionInSeconds(2,00),TimeUnit.SECONDS)
				,Duration.create(1,TimeUnit.DAYS));
	    
	    new UpdateSampleCNS(Duration.create(3,TimeUnit.MINUTES)
				,Duration.create(1,TimeUnit.HOURS));
		
		new RunExtImportCNS(Duration.create(10,TimeUnit.MINUTES),Duration.create(12,TimeUnit.HOURS));

	//	new SolutionStockImportCNS(Duration.create(5,TimeUnit.SECONDS),Duration.create(5,TimeUnit.MINUTES));
	//	new UpdateSolutionStockCNS(Duration.create(5,TimeUnit.SECONDS),Duration.create(15,TimeUnit.MINUTES));
	}

}
