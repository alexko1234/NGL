package controllers.dataload;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.utils.HelperObjects;
import models.utils.dao.DAOException;
import play.data.validation.ValidationError;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;
import validation.utils.ConstraintsHelper;
import data.FirstData;
import data.utils.DataTypeHelper;
import fr.cea.ig.MongoDBDAO;

public class InitialData extends Controller{

	
	// Firt data type 
		public static Result loadDataType(){

			try {
				DataTypeHelper.saveMapType(SampleType.class,FirstData.getSampleTypeAll());
				DataTypeHelper.saveMapType(ExperimentType.class, FirstData.getEXperimentType());
				DataTypeHelper.saveMapType(ImportType.class, FirstData.getImportTypeAll());
				DataTypeHelper.saveMapType(ProjectType.class, FirstData.getProjectTypeAll());
			} catch (DAOException e) {

				return badRequest(e.getMessage());
			}

			return ok("Data save");
		}

	
		public static Result createProjectFromLims() throws IOException{

			String projectTypeCode="projectType";
			Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();

			ProjectType projectType= new HelperObjects<ProjectType>().getObject(ProjectType.class,projectTypeCode, errors);

			List<Project> projects=new ArrayList<Project>();


			if(projectType==null){
				ConstraintsHelper.addErrors(errors, ConstraintsHelper.getKey(null, "ProjectTypeCode"), "PROJECT TYPE NOT EXISTS",projectTypeCode);
				return badRequest(errors.toString());
			}

			ResultSet resultSet=null;
			String query=null;

			query="select distinct p.prsco,prsnom=rtrim(p.prsnom),prscom,prsdc from Projet p, Adnmateriel a where p.prsco=a.prsco and esprjco!=25 and eadnco!=170";
			try {
				java.sql.Statement stm=DB.getConnection("lims").createStatement();

				resultSet=stm.executeQuery(query);
				while(resultSet.next()){
					
					Project project = new Project();
					project.code=resultSet.getString("prsco");
					project.name=resultSet.getString("prsnom");
					project.comments=new ArrayList<Comment>();
					project.comments.add(new Comment(resultSet.getString("prscom")));
					project.traceInformation=new TraceInformation();
					project.traceInformation.creationDate=resultSet.getDate("prsdc");
					
					projects.add(project);
				}

			} catch (SQLException e) {
				return badRequest(e.getMessage());
			}

			MongoDBDAO.saveDBOjects(projects);
			
			return ok(projects.size()+" projects save !!");

		}
		
		/*	public static Result createCVSFileFromLims(String sampleTypeCode, String experimentTypeCode,String samplecategoryCode,String fileName) throws IOException{

		FileWriter file=new FileWriter(fileName);
		Form<InputLoadData> filledForm = inputLoadData.bindFromRequest();

		SampleType sampleType= new HelperObjects<SampleType>().getObject(SampleType.class, sampleTypeCode, filledForm.errors());
		ImportType importType= new HelperObjects<ImportType>().getObject(ImportType.class, experimentTypeCode, filledForm.errors());


		if(sampleType==null){
			ConstraintsHelper.addErrors(filledForm.errors(), ConstraintsHelper.getKey(null, "SampleTypeCode"), "SAMPLE TYPE NOT EXISTS",sampleTypeCode);
			return badRequest(sampleload.render(datatableForm, filledForm));
		}

		if(importType==null){
			ConstraintsHelper.addErrors(filledForm.errors(), ConstraintsHelper.getKey(null, "ExperimentTypeCOde"), "EXPERIMENT TYPE NOT EXISTS",experimentTypeCode);
			return badRequest(sampleload.render(datatableForm, filledForm));
		}


		//Write Header
		file.write(LoadDataHelper.getFirstLine(sampleType, importType));
		file.write("\n");

		ResultSet resultSet=null;
		String query=null;

		query="select rtrim(adnnom)+';'+ rtrim(p.prsco)+';'+ rtrim(adnnom)+';'+'BAC'+';'+rtrim(adnclo)" +
				"+';'+convert(varchar,m.orgco)+';comment;' " +
				"+convert(varchar,ceiling(adntai))+';'+convert(varchar,adnfrag)+';'+convert(varchar,adnadpt)+';'"+
				//code;categoryCode;comment;ContainerSupport.categorycode;ContainerSupport.x;ContainerSupport.barecode;Content.INDEX
				"+tubnom+';TUBE;'+str_replace(tubcom,';',',')+';VIDE;1;1;'+tubnom+';IND1'" +
				"from Projet p , Adnmateriel m, Tubeident t, Tubemateriel tm where tm.adnco=m.adnco and t.tubco=tm.tubco and  p.prsco=m.prsco and esprjco!=25 and eadnco!=170";
		try {
			java.sql.Statement stm=DB.getConnection("lims").createStatement();

			resultSet=stm.executeQuery(query);
			while(resultSet.next()){
				file.write(resultSet.getString(1).replace("\n", " --- "));
				file.write("\n");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		file.close();

		return ok();

	}
	 */

	


	
}
