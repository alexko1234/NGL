package controllers.dataload;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import validation.utils.ConstraintsHelper;
import views.html.dataload.sampleload;
import au.com.bytecode.opencsv.CSVReader;
import controllers.utils.DataTableForm;
import data.bean.InputLoadData;
import data.utils.LoadDataHelper;
import fr.cea.ig.MongoDBDAO;

public class SampleLoad extends Controller{

	final static Form<InputLoadData> inputLoadData = form(InputLoadData.class);
	final static Form<DataTableForm> datatableForm = form(DataTableForm.class);


	
	public static Result home() {
		return ok(sampleload.render(datatableForm, inputLoadData));
	}

	public static Result createTemplateFile() throws IOException {

		Form<InputLoadData> filledForm = inputLoadData.bindFromRequest();
		InputLoadData inputLoadDatas = filledForm.get();
		String sampleTypeCode=inputLoadDatas.sampleType;
		String experimentTypeCode=inputLoadDatas.importType;
		String fileName=inputLoadDatas.fileName;
		FileWriter fileWriter=null;	

		SampleType sampleType= new HelperObjects<SampleType>().getObject(SampleType.class,sampleTypeCode,filledForm.errors());
		ImportType experimentType= new HelperObjects<ImportType>().getObject(ImportType.class, experimentTypeCode, filledForm.errors());

		try{
			fileWriter=new FileWriter(fileName);
			fileWriter.write(LoadDataHelper.getFirstLine(sampleType, experimentType));
			fileWriter.close();
		}catch(Exception e){
			System.err.println(e.getStackTrace());
			ConstraintsHelper.addErrors(filledForm.errors(), ConstraintsHelper.getKey(null, "error.initfilewriter"), "FILE ",fileName);
			return badRequest(sampleload.render(datatableForm, filledForm));
		}

		return ok("Fichier "+fileName+" créé");

	}


	public static Result uploadDataFromCSVFile() throws Exception {

		Form<InputLoadData> filledForm = inputLoadData.bindFromRequest();
		InputLoadData inputLoadDatas = filledForm.get();		
		String sampleTypeCode=inputLoadDatas.sampleType;
		String importTypeCode=inputLoadDatas.importType;
		String fileName=inputLoadDatas.fileName;

		// TODO a supprimer
		if(fileName==null){
			fileName="/env/cns/home/mhaquell/tmp/data_sampletype.csv";
		}

		Logger.debug("Sample type "+sampleTypeCode);
		Logger.debug("ImportType type "+importTypeCode);

		if(!filledForm.hasErrors()) {

			List<Sample> samples = new ArrayList<Sample>();
			List<Container> containers = new ArrayList<Container>();

			LoadDataHelper.validateFile(fileName,filledForm.errors());
			if(filledForm.hasErrors())
			{
				return badRequest(sampleload.render(datatableForm, filledForm));

			}
			FileReader fileReader=new FileReader(fileName);

			CSVReader reader = new CSVReader(fileReader,LoadDataHelper.SEPARATOR);
			String [] nextLine;

			SampleType sampleType= new HelperObjects<SampleType>().getObject(SampleType.class, sampleTypeCode, filledForm.errors());
			ImportType importType= new HelperObjects<ImportType>().getObject(ImportType.class, importTypeCode, filledForm.errors());

			if(sampleType==null){
				ConstraintsHelper.addErrors(filledForm.errors(), ConstraintsHelper.getKey(null, "SampleTypeCode"), "SAMPLE TYPE NOT EXISTS",sampleTypeCode);
				return badRequest(sampleload.render(datatableForm, filledForm));
			}

			if(importType==null){
				ConstraintsHelper.addErrors(filledForm.errors(), ConstraintsHelper.getKey(null, "ExperimentTypeCOde"), "EXPERIMENT TYPE NOT EXISTS",importTypeCode);
				return badRequest(sampleload.render(datatableForm, filledForm));
			}

			String[] firstLine=reader.readNext();

			LoadDataHelper.validateHeader(firstLine,sampleType,importType,filledForm.errors());
			if(filledForm.hasErrors())
			{
				return badRequest(sampleload.render(datatableForm, filledForm));

			}


			int j=0;
			Boolean ALL=true;

			while ((nextLine = reader.readNext()) != null && ((j<5 && ALL==Boolean.FALSE) || ALL==Boolean.TRUE)) {

				j++;
				Sample sample =LoadDataHelper.sampleFromCSVLine(firstLine,nextLine,sampleType.getMapPropertyDefinition());
				sample.categoryCode=sampleType.sampleCategory.code;
				sample.typeCode=sampleType.code;

				Container container= LoadDataHelper.containerFromCSVLine(firstLine,nextLine,sample,importType.getMapPropertyDefinition());

				ConstraintsHelper.validateProperties(filledForm.errors(), sample.properties, sampleType.propertiesDefinitions,null );

				if(container.contents.get(0).properties!=null)
					ConstraintsHelper.validateProperties(filledForm.errors(), container.contents.get(0).properties, importType.propertiesDefinitions,null );

				samples.add(sample);
				containers.add(container);

			}

			if(filledForm.hasErrors()){
				return badRequest(sampleload.render(datatableForm, filledForm));
			}


			MongoDBDAO.save(containers);
			MongoDBDAO.save(samples);

			if(filledForm.hasErrors()){
				return badRequest(sampleload.render(datatableForm, filledForm));
			}

			return ok(samples.size() +" Samples Containers create");
		}
		//TODO Affiche les messages d'erreurs
		return badRequest(sampleload.render(datatableForm, filledForm));


	}


}
