package SraValidation;

import java.io.IOException;

import models.sra.study.instance.Study;
import models.sra.utils.SraException;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;

import org.junit.Assert;
import org.junit.Test;

import validation.ContextValidation;
import SraValidation.OldSraValidationHelper;
import fr.cea.ig.MongoDBDAO;
import utils.AbstractTestsSRA;


// Pour les tests utilisant MongoDb mettre extents AbstractTests
public class StudyValidationHelperTest extends AbstractTestsSRA {
	//@BeforeClass appelé 1! fois avant tests
	//@Before appelee avant chaque test

	@Test
	public void validationStudySuccess() throws IOException, SraException {
		Study study = new Study();
		// Tests study avec infos obligatoires presentes et bonnes valeurs et mode
		// creation ou update.		
		study.centerName=VariableSRA.centerName;
		study.projectCode = "test";
		study.centerProjectName = "test";
		study.code = "study_test_1";
		study.existingStudyType="Metagenomics";
		ContextValidation contextValidation = new ContextValidation();
		contextValidation.setCreationMode();
		study.validate(contextValidation);
		MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		contextValidation.setUpdateMode();
		study.validate(contextValidation);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.study.instance.Study.class, study.code);

		//contextValidation.getContextObjects().put("stateCode", "F");
		System.out.println(contextValidation.errors.toString());
		Assert.assertTrue(contextValidation.errors.size()==0); // si aucune erreur
		//Assert.assertTrue(contextValidation.errors.size()==1ou > ); // si aucune erreur
	}

	@Test
	public void validationStudyEchec() throws IOException, SraException {
		//this.initConfig();
		// Tests config avec infos obligatoires presentes et bonnes valeurs et mode
		// creation ou update.
		Study study = new Study();
		study.centerName="";
		study.projectCode = "   ";
		study.centerProjectName = "";
		study.code = "study_test_2";
		study.existingStudyType="MetagenomicsFarfelue";
		ContextValidation contextValidation = new ContextValidation();
		contextValidation.setCreationMode();
		study.validate(contextValidation);
		MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.study.instance.Study.class, study.code);

		System.out.println(contextValidation.errors.toString());
		System.out.println("nbre d'erreurs : "+contextValidation.errors.size());
		Assert.assertTrue(contextValidation.errors.size()==4); // On attend 5 erreurs
	}


}
