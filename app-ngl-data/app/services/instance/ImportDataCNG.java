package services.instance;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import fr.cea.ig.play.migration.NGLContext;
import scala.concurrent.duration.Duration;
import services.instance.container.ContainerImportCNG;
import services.instance.parameter.IndexImportCNG;
import services.instance.project.ProjectImportCNG;
import services.instance.sample.UpdateReportingData;
import services.instance.sample.UpdateSampleNCBITaxonCNG;
import services.instance.sample.UpdateSamplePropertiesCNS;
import services.ncbi.TaxonomyServices;

/**
 * @author dnoisett
 * Import samples and container from CNG's LIMS to NGL 
 */

public class ImportDataCNG {
	
	@Inject
	public ImportDataCNG(NGLContext ctx) {
		// 1er parametre=delai avant 1er declenchement, 2eme parametre=delai pour repetition
		// decaler les demarragesr pour eviter que les logs s'entrecroisent !!!
		
		//vérifier s'il y a des projets a importer 1 fois par heure
		new ProjectImportCNG(ImportDataUtil.getDurationForNextHour(0),Duration.create(60,TimeUnit.MINUTES), ctx);
		
		//vérifier s'il y a des index a importer 1 fois par jour
		new IndexImportCNG(ImportDataUtil.getDurationInMillinsBefore(5, 0),Duration.create(1,TimeUnit.DAYS), ctx);
		
		//FDS: pas fonctionnel ?? ni nécessaire ??
		//new ExperimentImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));	
		
		//vérifier s'il y a des containers a importer toutes les 10 minutes
		new ContainerImportCNG(ImportDataUtil.getDurationForNextHour(30),Duration.create(10,TimeUnit.MINUTES), ctx);
		
		//Mise a jour des info du NCBI pour les samples qui n'en ont pas
		new UpdateSampleNCBITaxonCNG(ImportDataUtil.getDurationForNextHour(30),Duration.create(6,TimeUnit.HOURS), ctx, new TaxonomyServices(ctx));
		 
		//11/04/2017 ajouter la propagation des modifications apportées aux samples...
		new UpdateSamplePropertiesCNS(ImportDataUtil.getDurationForNextHour(45),Duration.create(6,TimeUnit.HOURS), ctx);
		
		new UpdateReportingData(ImportDataUtil.getDurationInMillinsBefore(20, 0),Duration.create(1,TimeUnit.DAYS), ctx);
	}
	
}