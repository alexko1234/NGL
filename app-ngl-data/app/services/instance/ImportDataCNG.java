package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.container.ContainerImportCNG;
import services.instance.experiment.ExperimentImportCNG;
import services.instance.parameter.IndexImportCNG;
import services.instance.project.ProjectImportCNG;


public class ImportDataCNG {
	
	public ImportDataCNG(){
		new ProjectImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		new ContainerImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		new IndexImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		
		new ExperimentImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		
		
	}

}
