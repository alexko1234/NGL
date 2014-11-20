package SraValidation;

import java.io.IOException;

import models.sra.utils.SraException;
import models.sra.utils.VariableSRA;

import org.junit.Assert;
import org.junit.Test;

import utils.AbstractTestsSRA;
import validation.ContextValidation;
import validation.sra.SraValidationHelper;

public class SraValidationHelperTest extends AbstractTestsSRA {
	@Test
	public void validationSraValidationHelperRequiredAndConstraintSuccess() throws IOException, SraException {
		ContextValidation contextValidation = new ContextValidation(userContext);
		contextValidation.addKeyToRootKeyName("experiment::");
		String librarySelection = "random";
		SraValidationHelper.requiredAndConstraint(contextValidation, librarySelection, VariableSRA.mapLibrarySelection, "librarySelection");
		contextValidation.removeKeyFromRootKeyName("experiment::");
		Assert.assertTrue(contextValidation.errors.size()==0); // si aucune erreur
	}
	
	@Test
	public void validationSraValidationHelperRequiredAndConstraintEchec() throws IOException, SraException {
		ContextValidation contextValidation = new ContextValidation(userContext);
		contextValidation.addKeyToRootKeyName("experiment::");
		String librarySelection = "farfelue";
		SraValidationHelper.requiredAndConstraint(contextValidation, librarySelection, VariableSRA.mapLibrarySelection, "librarySelection");
		contextValidation.removeKeyFromRootKeyName("experiment::");
		Assert.assertTrue(contextValidation.errors.size()==1); // si une erreur
	}
	
	@Test
	public void validationSraValidationHelperRequiredAndConstraintNull() throws IOException, SraException {
		ContextValidation contextValidation = new ContextValidation(userContext);
		contextValidation.addKeyToRootKeyName("experiment::");
		String librarySelection = null;
		SraValidationHelper.requiredAndConstraint(contextValidation, librarySelection, VariableSRA.mapLibrarySelection, "librarySelection");
		contextValidation.removeKeyFromRootKeyName("experiment::");
		Assert.assertTrue(contextValidation.errors.size()==1); // si une erreur
	}
	
	
}
