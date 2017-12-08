package models.sra.submit.common.instance;

import models.sra.submit.util.VariableSRA;
import validation.ContextValidation;
import validation.IValidation;
import validation.sra.SraValidationHelper;
import fr.cea.ig.DBObject;
//import play.Logger;

public class Readset extends DBObject implements IValidation {
	private static final play.Logger.ALogger logger = play.Logger.of(Readset.class);
	// public String code = null; champs declaré dans DBObject qui ne doit pas etre surchargé ici sinon pas mis dans base
	// au moment du save.
	public String runCode = null;
	public String experimentCode = null;
	public String type = null; // ILLUMINA ou LS454 ou nanopore

	@Override
	public void validate(ContextValidation contextValidation) {
		logger.info("Validate ngl-sub::Readset");
		//Logger.info("ok dans Sample.validate\n");
		contextValidation.addKeyToRootKeyName("ngl-sub::Readset");
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.requiredAndConstraint(contextValidation, this.type, VariableSRA.mapTypeReadset(), "typeReadset");
		contextValidation.removeKeyFromRootKeyName("ngl-sub::Readset");
		//System.out.println("sortie de sample.validate pour " + this.code);
	}




}
