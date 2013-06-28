package controllers.dataload;

import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.State;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.PurificationMethodType;
import models.laboratory.experiment.description.QualityControlType;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.description.ProjectType;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.utils.DescriptionHelper;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import data.FirstData;


public class InitialData extends Controller{

	final static DynamicForm listForm = new DynamicForm();
	
	// Firt data type 
	public static Result loadDataType(){

		try {
			
			DescriptionHelper.saveMapType(MeasureCategory.class,FirstData.getMeasureCategoryAll());
			DescriptionHelper.saveMapType(ContainerSupportCategory.class,FirstData.getContainerSupportCategoryAll());
			DescriptionHelper.saveMapType(ContainerCategory.class, FirstData.getContainerCategorieAll());
			DescriptionHelper.saveMapType(State.class,FirstData.getStateAll());
			DescriptionHelper.saveMapType(InstrumentUsedType.class, FirstData.getInstrumentUsedTypeAll());
			DescriptionHelper.saveMapType(ProjectType.class, FirstData.getProjectTypeAll());
			DescriptionHelper.saveMapType(SampleType.class,FirstData.getSampleTypeAll());
			DescriptionHelper.saveMapType(ImportType.class, FirstData.getImportTypeAll());
			DescriptionHelper.saveMapType(PurificationMethodType.class,FirstData.getPurificationMethodtypeAll());
			DescriptionHelper.saveMapType(QualityControlType.class,FirstData.getQualityControlAll());		
			DescriptionHelper.saveMapType(ExperimentType.class, FirstData.getExperimentType());
			DescriptionHelper.saveMapType(ExperimentType.class, FirstData.getExperimentTypeBqMP());
			DescriptionHelper.saveMapType(ProcessType.class,FirstData.getProcessTypeAll());
			
		} catch (Exception e) {
			Logger.error("",e);
			return badRequest(e.getMessage());
		}

		return ok("Data save");
	}


}
