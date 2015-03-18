package validation.sra;

import java.util.Map;

import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.ReadSpec;
import models.sra.submit.util.VariableSRA;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

public class SraValidationHelper extends CommonValidationHelper{

	public static boolean requiredAndConstraint(ContextValidation contextValidation, String value, Map<String, String> mapValues, String nameField) {
		if (ValidationHelper.required(contextValidation, value, nameField)){
			if ( mapValues.containsKey(value.toLowerCase())){
				return true;
			} else {
				contextValidation.addErrors(nameField + " avec valeur '" + value + "' qui n'appartient pas a la liste des valeurs autorisees :" , mapValues.keySet().toString());
				return false;
			}
		} else {
			// contextValidation mis à jour par required.
			return false;
		}
	}
	
	

	public static void validateReadSpecs(ContextValidation contextValidation, Experiment experiment){
		// Verifier les readSpec :
		contextValidation.addKeyToRootKeyName("readSpecs::");

		for(ReadSpec readSpec : experiment.readSpecs) {
			readSpec.validate(contextValidation);
		}
		ContextValidation _contextValidation = new ContextValidation(contextValidation.getUser());
		
		if(!SraValidationHelper.requiredAndConstraint(_contextValidation, experiment.libraryLayout, VariableSRA.mapLibraryLayout, "libraryLayout")){
			contextValidation.addErrors("",  "impossibles à evaluer sans libraryLayout valide");
			contextValidation.removeKeyFromRootKeyName("readSpecs::");
			return;
		}

		if (experiment.libraryLayout.equalsIgnoreCase("SINGLE") ) {
			if(experiment.readSpecs.size() != 1){
				contextValidation.addErrors("",  " nbre de readSpec " + experiment.readSpecs.size() + "' incompatible avec libraryLayout = SINGLE ");
				contextValidation.removeKeyFromRootKeyName("readSpecs::");
				return;
			}
			ReadSpec readSpec1 = experiment.readSpecs.get(0);
			if ( ValidationHelper.required(contextValidation, readSpec1.readIndex, "readIndex")
				&& (readSpec1.readIndex != 0)) {
				contextValidation.addErrors("readSpec1.readIndex :",  "readSpec1 avec mauvais readIndex");
			}
			if ( ValidationHelper.required(contextValidation, readSpec1.readLabel, "readLabel")
				&& (!readSpec1.readLabel.equalsIgnoreCase("F"))) {
				contextValidation.addErrors("readSpec1.readLabel :",  "readSpec1 avec mauvais readLabel (" + readSpec1.readLabel + ")");
			}
			if ( ValidationHelper.required(contextValidation, readSpec1.readClass, "readClass")
				&& (!readSpec1.readClass.equalsIgnoreCase("Application Read"))) {
				contextValidation.addErrors("readSpec1.readClass :",  "readSpec1 avec mauvais readClass (" + readSpec1.readClass + ")");
			}
			if ( ValidationHelper.required(contextValidation, readSpec1.readType, "readType")
				&& (!readSpec1.readType.equalsIgnoreCase("Forward"))) {
				contextValidation.addErrors("readSpec1.readType :",  "readSpec1 avec mauvais readType (" + readSpec1.readType + ")");
			}
			if ( ValidationHelper.required(contextValidation, readSpec1.lastBaseCoord, "lastBaseCoord")
				&& (readSpec1.lastBaseCoord != 1)) {
				contextValidation.addErrors("readSpec1.lastBaseCoord :",  "readSpec1 avec mauvais lastBaseCoord (" + readSpec1.lastBaseCoord + ")");
			}	
		} else {
			if(experiment.readSpecs.size() != 2){
				contextValidation.addErrors("",  " nbre de readSpec " + experiment.readSpecs.size() + "' incompatible avec libraryLayout = PAIRED ");
			}
			if(!SraValidationHelper.requiredAndConstraint(_contextValidation, experiment.libraryLayoutOrientation, VariableSRA.mapLibraryLayoutOrientation, "libraryLayoutOrientation")){
				contextValidation.addErrors("",  "impossible à evaluer sans libraryLayoutOrientation valide");
				contextValidation.removeKeyFromRootKeyName("readSpecs::");
				return;
			}
			if (experiment.libraryLayoutOrientation.equalsIgnoreCase("forward-reverse")) {
				ReadSpec readSpec1 = experiment.readSpecs.get(0);
				if ( ValidationHelper.required(contextValidation, readSpec1.readIndex, "readIndex")
					&& (readSpec1.readIndex != 0)) {
					contextValidation.addErrors("readSpec1.readIndex :",  "readSpec1 avec mauvais readIndex");
				}
				if ( ValidationHelper.required(contextValidation, readSpec1.readLabel, "readLabel")
					&& (!readSpec1.readLabel.equalsIgnoreCase("F"))) {
					contextValidation.addErrors("readSpec1.readLabel :",  "readSpec1 avec mauvais readLabel (" + readSpec1.readLabel + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec1.readClass, "readClass")
					&& (!readSpec1.readClass.equalsIgnoreCase("Application Read"))) {
					contextValidation.addErrors("readSpec1.readClass :",  "readSpec1 avec mauvais readClass (" + readSpec1.readClass + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec1.readType, "readType")
					&& (!readSpec1.readType.equalsIgnoreCase("Forward"))) {
					contextValidation.addErrors("readSpec1.readType :",  "readSpec1 avec mauvais readType (" + readSpec1.readType + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec1.lastBaseCoord, "lastBaseCoord")
					&& (readSpec1.lastBaseCoord != 1)) {
					contextValidation.addErrors("readSpec1.lastBaseCoord :",  "readSpec1 avec mauvais lastBaseCoord (" + readSpec1.lastBaseCoord + ")");
				}	
				
				ReadSpec readSpec2 = experiment.readSpecs.get(1);
				if ( ValidationHelper.required(contextValidation, readSpec2.readIndex, "readIndex")
					&& (readSpec2.readIndex != 1)) {
					contextValidation.addErrors("readSpec2.readIndex :",  "readSpec2 avec mauvais readIndex");
				}
				if ( ValidationHelper.required(contextValidation, readSpec2.readLabel, "readLabel")
					&& (!readSpec2.readLabel.equalsIgnoreCase("R"))) {
					contextValidation.addErrors("readSpec2.readLabel :",  "readSpec2 avec mauvais readLabel (" + readSpec2.readLabel + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec2.readClass, "readClass")
					&& (!readSpec2.readClass.equalsIgnoreCase("Application Read"))) {
					contextValidation.addErrors("readSpec2.readClass :",  "readSpec2 avec mauvais readClass (" + readSpec1.readClass + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec2.readType, "readType")
					&& (!readSpec2.readType.equalsIgnoreCase("Reverse"))) {
					contextValidation.addErrors("readSpec2.readType :",  "readSpec2 avec mauvais readType (" + readSpec1.readType + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec2.lastBaseCoord, "lastBaseCoord")
					&& (readSpec2.lastBaseCoord == 1)) {
					contextValidation.addErrors("readSpec2.lastBaseCoord :",  "readSpec2 avec mauvais lastBaseCoord (" + readSpec1.lastBaseCoord + ")");
				}	
			} else if (experiment.libraryLayoutOrientation.equalsIgnoreCase("reverse-forward")) {
				ReadSpec readSpec1 = experiment.readSpecs.get(0);
				if ( ValidationHelper.required(contextValidation, readSpec1.readIndex, "readIndex")
					&& (readSpec1.readIndex != 0)) {
					contextValidation.addErrors("readSpec1.readIndex :",  "readSpec1 avec mauvais readIndex");
				}
				if ( ValidationHelper.required(contextValidation, readSpec1.readLabel, "readLabel")
					&& (!readSpec1.readLabel.equalsIgnoreCase("R"))) {
					contextValidation.addErrors("readSpec1.readLabel :",  "readSpec1 avec mauvais readLabel (" + readSpec1.readLabel + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec1.readClass, "readClass")
					&& (!readSpec1.readClass.equalsIgnoreCase("Application Read"))) {
					contextValidation.addErrors("readSpec1.readClass :",  "readSpec1 avec mauvais readClass (" + readSpec1.readClass + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec1.readType, "readType")
					&& (!readSpec1.readType.equalsIgnoreCase("Reverse"))) {
					contextValidation.addErrors("readSpec1.readType :",  "readSpec1 avec mauvais readType (" + readSpec1.readType + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec1.lastBaseCoord, "lastBaseCoord")
					&& (readSpec1.lastBaseCoord != 1)) {
					contextValidation.addErrors("readSpec1.lastBaseCoord :",  "readSpec1 avec mauvais lastBaseCoord (" + readSpec1.lastBaseCoord + ")");
				}	
					
				ReadSpec readSpec2 = experiment.readSpecs.get(1);
				if ( ValidationHelper.required(contextValidation, readSpec2.readIndex, "readIndex")
					&& (readSpec2.readIndex != 1)) {
					contextValidation.addErrors("readSpec2.readIndex :",  "readSpec avec mauvais readIndex");
				}
				if ( ValidationHelper.required(contextValidation, readSpec2.readLabel, "readLabel")
					&& (!readSpec2.readLabel.equalsIgnoreCase("F"))) {
					contextValidation.addErrors("readSpec2.readLabel :",  "readSpec avec mauvais readLabel (" + readSpec2.readLabel + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec2.readClass, "readClass")
					&& (!readSpec2.readClass.equalsIgnoreCase("Application Read"))) {
					contextValidation.addErrors("readSpec2.readClass :",  "readSpec avec mauvais readClass (" + readSpec1.readClass + ")");					}
				if ( ValidationHelper.required(contextValidation, readSpec2.readType, "readType")
					&& (!readSpec2.readType.equalsIgnoreCase("Forward"))) {
					contextValidation.addErrors("readSpec2.readType :",  "readSpec avec mauvais readType (" + readSpec1.readType + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec2.lastBaseCoord, "lastBaseCoord")
					&& (readSpec2.lastBaseCoord == 1)) {
					contextValidation.addErrors("readSpec2.lastBaseCoord :",  "readSpec avec mauvais lastBaseCoord (" + readSpec1.lastBaseCoord + ")");
				}	
			} else {
				contextValidation.addErrors("",  "impossible à evaluer avec libraryLayoutOrientation != de 'forward-reverse' ou 'reverse-forward' ");
			}
		}		
		contextValidation.removeKeyFromRootKeyName("readSpecs::");
	}


}


