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
import services.instance.sample.UpdateReportingData;
import services.instance.sample.UpdateSampleCNS;
import services.instance.sample.UpdateSampleNCBITaxonCNS;
import services.instance.sample.UpdateSamplePropertiesCNS;

public class ImportDataCNS{

	public ImportDataCNS(){


		new UpdateReportingData(Duration.create(2,TimeUnit.SECONDS),Duration.create(24,TimeUnit.HOURS));
	}

}
