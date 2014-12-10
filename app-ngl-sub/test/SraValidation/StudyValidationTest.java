package SraValidation;

import java.io.IOException;

import models.sra.study.instance.Study;
import models.sra.utils.SraException;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;

import org.junit.Assert;
import org.junit.Test;

import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import utils.AbstractTestsSRA;
import play.Logger;


// Pour les tests utilisant MongoDb mettre extents AbstractTests
public class StudyValidationTest extends AbstractTestsSRA {
	//@BeforeClass appelé 1! fois avant tests
	//@Before appelee avant chaque test

	@Test
	public void validationStudySuccess() throws IOException, SraException {
		Study study = new Study();
		// Tests study avec infos obligatoires presentes et bonnes valeurs et mode
		// creation ou update.		
		study.centerName=VariableSRA.centerName;
		study.projectCode = "AWK";
		study.centerProjectName = "AWK";
		study.code = "study_AWK_1";
		study.existingStudyType="Metagenomics";
		ContextValidation contextValidation = new ContextValidation(userContext);
		contextValidation.setCreationMode();
		study.validate(contextValidation);
		MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		contextValidation.setUpdateMode();
		study.validate(contextValidation);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.study.instance.Study.class, study.code);
		System.out.println("contextValidation.errors pour validationStudySuccess :");
		contextValidation.displayErrors(Logger.of("SRA"));
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
		ContextValidation contextValidation = new ContextValidation(userContext);
		contextValidation.setCreationMode();
		study.validate(contextValidation);
		MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.study.instance.Study.class, study.code);
		System.out.println("contextValidation.errors pour validationStudyEchec :");
		contextValidation.displayErrors(Logger.of("SRA"));
		Assert.assertTrue(contextValidation.errors.size()==4); // On attend 5 erreurs
	}


}
