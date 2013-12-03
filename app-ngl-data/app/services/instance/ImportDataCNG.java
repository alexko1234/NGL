package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.cng.container.ContainerImportCNG;
import services.instance.cng.project.ProjectImportCNG;



public class ImportDataCNG extends AbsImportData {

	public ImportDataCNG(){
		new ProjectImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		new ContainerImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
	}

}
