package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.run.RunImportCNS;


public class ImportDataCNS{

	public ImportDataCNS(){

		//new ProjectImportCNS(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		//new ContainerImportCNS(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		new RunImportCNS(Duration.create(4,TimeUnit.SECONDS),Duration.create(5,TimeUnit.MINUTES));
	}

}
