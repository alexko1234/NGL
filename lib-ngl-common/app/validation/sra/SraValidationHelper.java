package validation.sra;

import java.util.Map;

import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.ReadSpec;
import models.sra.submit.util.VariableSRA;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;
import org.apache.commons.lang3.StringUtils;

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
	
	public static void validateFreeText(ContextValidation contextValidation, String nameField, String chaine) {
		String forbidden = "(à, é, è, &)";
		if (!StringUtils.isNotBlank(chaine)){
			if (chaine.contains("&") || chaine.contains("é") || chaine.contains("è") || chaine.contains("à")) {
				contextValidation.addErrors(nameField + " avec valeur '" + chaine + "' qui contient des caractères non autorisés ", forbidden);
			}
		}
	}

	public static void validateReadSpecs(ContextValidation contextValidation, Experiment experiment){
		if ("ILLUMINA".equalsIgnoreCase(experiment.typePlatform)){
			validateReadSpecsILLUMINA(contextValidation, experiment);
		} else if ("LS454".equalsIgnoreCase(experiment.typePlatform)){
			validateReadSpecsLS454(contextValidation, experiment);
		}  else if ("OXFORD_NANOPORE".equalsIgnoreCase(experiment.typePlatform)){
			contextValidation.addKeyToRootKeyName("readSpecsNanopore::");
			if (experiment.readSpecs.size() != 0){
				contextValidation.addErrors("",  "Plateforme OXFORD_NANOPORE incompatible avec presence de readspec ");
				//System.out.println("Pas de validation implementée avec readspec et NANOPORE pour l'experiment " + experiment.code);
				
			}			
			contextValidation.removeKeyFromRootKeyName("readSpecsNanopore::");
		} else {
			contextValidation.addErrors("",  "readSpecs impossibles à evaluer avec platform inconnue " + experiment.typePlatform);
		}
	}
 
	public static void validateReadSpecsILLUMINA(ContextValidation contextValidation, Experiment experiment){
		// Verifier les readSpec :
		contextValidation.addKeyToRootKeyName("readSpecsIllumina::");

		for(ReadSpec readSpec : experiment.readSpecs) {
			readSpec.validate(contextValidation);
		}
		ContextValidation _contextValidation = new ContextValidation(contextValidation.getUser());
		
		if(!SraValidationHelper.requiredAndConstraint(_contextValidation, experiment.libraryLayout, VariableSRA.mapLibraryLayout, "libraryLayout")){
			contextValidation.addErrors("",  "impossibles à evaluer sans libraryLayout valide");
			contextValidation.removeKeyFromRootKeyName("readSpecsIllumina::");
			return;
		}

		if ("SINGLE".equalsIgnoreCase(experiment.libraryLayout) && "ILLUMINA".equalsIgnoreCase(experiment.typePlatform)) {
			if(experiment.readSpecs.size() != 1){
				contextValidation.addErrors("",  " nbre de readSpec " + experiment.readSpecs.size() + "' incompatible avec libraryLayout = SINGLE ");
				contextValidation.removeKeyFromRootKeyName("readSpecsIllumina::");
				return;
			}
			ReadSpec readSpec1 = experiment.readSpecs.get(0);
			if ( ValidationHelper.required(contextValidation, readSpec1.readIndex, "readIndex")
				&& (readSpec1.readIndex != 0)) {
				contextValidation.addErrors("readSpec1.readIndex :",  "readSpec1 avec mauvais readIndex");
			}
			if ( ValidationHelper.required(contextValidation, readSpec1.readClass, "readClass")
				&& (!readSpec1.readClass.equalsIgnoreCase("Application Read"))) {
				contextValidation.addErrors("readSpec1.readClass :",  "readSpec1 avec mauvais readClass (" + readSpec1.readClass + ")");
			}
			if ( ValidationHelper.required(contextValidation, readSpec1.readType, "readType")
				&& (!readSpec1.readType.equalsIgnoreCase("Forward"))) {
				contextValidation.addErrors("readSpec1.readType :",  "readSpec1 avec mauvais readType (" + readSpec1.readType + ")");
			}
			if ( ValidationHelper.required(contextValidation, readSpec1.baseCoord, "baseCoord")
				&& (readSpec1.baseCoord != 1)) {
				contextValidation.addErrors("readSpec1.baseCoord :",  "readSpec1 avec mauvais baseCoord (" + readSpec1.baseCoord + ")");
			}	
		} else if ("PAIRED".equalsIgnoreCase(experiment.libraryLayout) && "ILLUMINA".equalsIgnoreCase(experiment.typePlatform)) {
			if(experiment.readSpecs.size() != 2){
				contextValidation.addErrors("",  " nbre de readSpec " + experiment.readSpecs.size() + "' incompatible avec libraryLayout = PAIRED ");
			}
			if(!SraValidationHelper.requiredAndConstraint(_contextValidation, experiment.libraryLayoutOrientation, VariableSRA.mapLibraryLayoutOrientation, "libraryLayoutOrientation")){
				contextValidation.addErrors("",  "impossible à evaluer sans libraryLayoutOrientation valide");
				contextValidation.removeKeyFromRootKeyName("readSpecsIllumina::");
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
				if ( ValidationHelper.required(contextValidation, readSpec1.baseCoord, "baseCoord")
					&& (readSpec1.baseCoord != 1)) {
					contextValidation.addErrors("readSpec1.baseCoord :",  "readSpec1 avec mauvais baseCoord (" + readSpec1.baseCoord + ")");
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
				if ( ValidationHelper.required(contextValidation, readSpec2.baseCoord, "baseCoord")
					&& (readSpec2.baseCoord == 1)) {
					contextValidation.addErrors("readSpec2.baseCoord :",  "readSpec2 avec mauvais baseCoord (" + readSpec1.baseCoord + ")");
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
				if ( ValidationHelper.required(contextValidation, readSpec1.baseCoord, "baseCoord")
					&& (readSpec1.baseCoord != 1)) {
					contextValidation.addErrors("readSpec1.baseCoord :",  "readSpec1 avec mauvais baseCoord (" + readSpec1.baseCoord + ")");
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
					&& (!readSpec2.readType.equalsIgnoreCase("forward"))) {
					contextValidation.addErrors("readSpec2.readType :",  "readSpec avec mauvais readType (" + readSpec1.readType + ")");
				}
				if ( ValidationHelper.required(contextValidation, readSpec2.baseCoord, "baseCoord")
					&& (readSpec2.baseCoord == 1)) {
					contextValidation.addErrors("readSpec2.baseCoord :",  "readSpec avec mauvais baseCoord (" + readSpec1.baseCoord + ")");
				}	
			} else {
				contextValidation.addErrors("",  "impossible à evaluer avec libraryLayoutOrientation != de 'forward-reverse' ou 'reverse-forward' ");
			}
		}
		contextValidation.removeKeyFromRootKeyName("readSpecsIllumina::");
	}




public static void validateReadSpecsLS454(ContextValidation contextValidation, Experiment experiment){

	// Verifier les readSpec :
	contextValidation.addKeyToRootKeyName("readSpecsLS454::");

	for(ReadSpec readSpec : experiment.readSpecs) {
		readSpec.validate(contextValidation);
	}
	ContextValidation _contextValidation = new ContextValidation(contextValidation.getUser());
	
	if(!SraValidationHelper.requiredAndConstraint(_contextValidation, experiment.libraryLayout, VariableSRA.mapLibraryLayout, "libraryLayout")){
		contextValidation.addErrors("",  "impossibles à evaluer sans libraryLayout valide");
		contextValidation.removeKeyFromRootKeyName("readSpecsLS454::");
		return;
	}

	if ("SINGLE".equalsIgnoreCase(experiment.libraryLayout) && "LS454".equalsIgnoreCase(experiment.typePlatform)) {
		if(experiment.readSpecs.size() != 2){
			contextValidation.addErrors("",  " nbre de readSpec " + experiment.readSpecs.size() + "' incompatible avec libraryLayout = SINGLE ");
			contextValidation.removeKeyFromRootKeyName("readSpecsLS454::");
			return;
		}
		ReadSpec readSpec1 = experiment.readSpecs.get(0);
		if ( ValidationHelper.required(contextValidation, readSpec1.readIndex, "readIndex")
			&& (readSpec1.readIndex != 0)) {
			contextValidation.addErrors("readSpec1.readIndex :",  "readSpec1 avec mauvais readIndex(" + readSpec1.readIndex + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec1.readClass, "readClass")
			&& (! "Technical Read".equalsIgnoreCase(readSpec1.readClass))) {
			contextValidation.addErrors("readSpec1.readClass :",  "readSpec1 avec mauvais readClass (" + readSpec1.readClass + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec1.readType, "readType")
			&& (!"Adapter".equalsIgnoreCase(readSpec1.readType))) {
			contextValidation.addErrors("readSpec1.readType :",  "readSpec1 avec mauvais readType (" + readSpec1.readType + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec1.baseCoord, "baseCoord")
			&& (readSpec1.baseCoord != 1)) {
			contextValidation.addErrors("readSpec1.baseCoord :",  "readSpec1 avec mauvais baseCoord (" + readSpec1.baseCoord + ")");
		}	
		ReadSpec readSpec2 = experiment.readSpecs.get(1);
		if ( ValidationHelper.required(contextValidation, readSpec2.readIndex, "readIndex")
			&& (readSpec2.readIndex != 0)) {
			contextValidation.addErrors("readSpec2.readIndex :",  "readSpec2 avec mauvais readIndex(" + readSpec2.readIndex + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec2.readClass, "readClass")
			&& (! "Application Read".equalsIgnoreCase(readSpec2.readClass))) {
			contextValidation.addErrors("readSpec2.readClass :",  "readSpec2 avec mauvais readClass (" + readSpec2.readClass + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec2.readType, "readType")
			&& (!"Forward".equalsIgnoreCase(readSpec2.readType))) {
			contextValidation.addErrors("readSpec2.readType :",  "readSpec2 avec mauvais readType (" + readSpec2.readType + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec2.baseCoord, "baseCoord")
			&& (readSpec2.baseCoord != 5)) {
			contextValidation.addErrors("readSpec2.baseCoord :",  "readSpec2 avec mauvais baseCoord (" + readSpec2.baseCoord + ")");
		}			
	} else if ("PAIRED".equalsIgnoreCase(experiment.libraryLayout) && "LS454".equalsIgnoreCase(experiment.typePlatform)) {
		if(experiment.readSpecs.size() != 4){
			contextValidation.addErrors("",  " nbre de readSpec " + experiment.readSpecs.size() + "' incompatible avec libraryLayout = PAIRED ");
		}
		ReadSpec readSpec1 = experiment.readSpecs.get(0);
		if ( ValidationHelper.required(contextValidation, readSpec1.readIndex, "readIndex")
			&& (readSpec1.readIndex != 0)) {
			contextValidation.addErrors("readSpec1.readIndex :",  "readSpec1 avec mauvais readIndex(" + readSpec1.readIndex + ")");
		}
			
		if ( ValidationHelper.required(contextValidation, readSpec1.readClass, "readClass")
			&& (! "Technical Read".equalsIgnoreCase(readSpec1.readClass))) {
			contextValidation.addErrors("readSpec1.readClass :",  "readSpec1 avec mauvais readClass (" + readSpec1.readClass + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec1.readType, "readType")
			&& (!"Adapter".equalsIgnoreCase(readSpec1.readType))) {
			contextValidation.addErrors("readSpec1.readType :",  "readSpec1 avec mauvais readType (" + readSpec1.readType + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec1.baseCoord, "baseCoord")
			&& (readSpec1.baseCoord != 1)) {
			contextValidation.addErrors("readSpec1.baseCoord :",  "readSpec1 avec mauvais baseCoord (" + readSpec1.baseCoord + ")");
		}	
			
		ReadSpec readSpec2 = experiment.readSpecs.get(1);
		if ( ValidationHelper.required(contextValidation, readSpec2.readIndex, "readIndex")
			&& (readSpec2.readIndex != 1)) {
			contextValidation.addErrors("readSpec2.readIndex :",  "readSpec2 avec mauvais readIndex(" + readSpec2.readIndex + ")");
		}
			
		if ( ValidationHelper.required(contextValidation, readSpec2.readClass, "readClass")
			&& (!readSpec2.readClass.equalsIgnoreCase("Application Read"))) {
			contextValidation.addErrors("readSpec2.readClass :",  "readSpec2 avec mauvais readClass (" + readSpec2.readClass + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec2.readType, "readType")
			&& (!readSpec2.readType.equalsIgnoreCase("Forward"))) {
			contextValidation.addErrors("readSpec2.readType :",  "readSpec2 avec mauvais readType (" + readSpec2.readType + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec2.baseCoord, "baseCoord")
			&& (readSpec2.baseCoord != 5)) {
			contextValidation.addErrors("readSpec2.baseCoord :",  "readSpec2 avec mauvais baseCoord (" + readSpec2.baseCoord + ")");
		}	
		
		ReadSpec readSpec3 = experiment.readSpecs.get(2);
		if ( ValidationHelper.required(contextValidation, readSpec3.readIndex, "readIndex")
			&& (readSpec3.readIndex != 2)) {
			contextValidation.addErrors("readSpec3.readIndex :",  "readSpec3 avec mauvais readIndex(" + readSpec3.readIndex + ")");
		}
			
		if ( ValidationHelper.required(contextValidation, readSpec3.readClass, "readClass")
			&& (!readSpec3.readClass.equalsIgnoreCase("Technical Read"))) {
			contextValidation.addErrors("readSpec3.readClass :",  "readSpec3 avec mauvais readClass (" + readSpec3.readClass + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec3.readType, "readType")
			&& (!readSpec3.readType.equalsIgnoreCase("Linker"))) {
			contextValidation.addErrors("readSpec3.readType :",  "readSpec3 avec mauvais readType (" + readSpec3.readType + ")");
		}
		// verifier BASECALL
		if ( ValidationHelper.required(contextValidation, readSpec3.expectedBaseCallTable.get(0), "baseCall_1")
				&& (!readSpec3.expectedBaseCallTable.get(0).equalsIgnoreCase("TCGTATAACTTCGTATAATGTATGCTATACGAAGTTATTACG"))) {		
			contextValidation.addErrors("readSpec3.expectedBaseCallTable[0] :",  "readSpec3 avec mauvais baseCall_1 (" + readSpec3.expectedBaseCallTable.get(0) + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec3.expectedBaseCallTable.get(1), "baseCall_2")
				&& (!readSpec3.expectedBaseCallTable.get(1).equalsIgnoreCase("CGTAATAACTTCGTATAGCATACATTATACGAAGTTATACGA"))) {		
			contextValidation.addErrors("readSpec3.expectedBaseCallTable[1] :",  "readSpec3 avec mauvais baseCall_2 (" + readSpec3.expectedBaseCallTable.get(1) + ")");
		}
		
		ReadSpec readSpec4 = experiment.readSpecs.get(3);
		if ( ValidationHelper.required(contextValidation, readSpec4.readIndex, "readIndex")
			&& (readSpec4.readIndex != 3)) {
			contextValidation.addErrors("readSpec4.readIndex :",  "readSpec4 avec mauvais readIndex (" + readSpec4.readIndex + ")");
		}		
		if ( ValidationHelper.required(contextValidation, readSpec4.readClass, "readClass")
			&& (!readSpec4.readClass.equalsIgnoreCase("Application Read"))) {
			contextValidation.addErrors("readSpec4.readClass :",  "readSpec avec mauvais readClass (" + readSpec4.readClass + ")");					}
		if ( ValidationHelper.required(contextValidation, readSpec4.readType, "readType")
			&& (!readSpec4.readType.equalsIgnoreCase("Forward"))) {
			contextValidation.addErrors("readSpec4.readType :",  "readSpec avec mauvais readType (" + readSpec4.readType + ")");
		}
		if ( ValidationHelper.required(contextValidation, readSpec2.baseCoord, "baseCoord")
			&& (readSpec4.baseCoord != 47)) {
			contextValidation.addErrors("readSpec4.baseCoord :",  "readSpec avec mauvais baseCoord (" + readSpec4.baseCoord + ")");
		}	

	
	} else {
			contextValidation.addErrors("",  "impossible à evaluer avec libraryLayoutOrientation != de 'forward-reverse' ou 'reverse-forward' ");
	}
		contextValidation.removeKeyFromRootKeyName("readSpecsLS454::");
	}		
}




