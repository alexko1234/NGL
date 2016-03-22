package controllers.instruments.io.covarisandsciclonengsx;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import models.laboratory.parameter.Index;
import validation.ContextValidation;
import validation.utils.ValidationHelper;
import controllers.instruments.io.utils.AbstractInput;
import controllers.instruments.io.utils.InputHelper;
import controllers.instruments.io.utils.TagModel;
import play.Logger;

public abstract class CovarisAndScicloneInput extends AbstractInput {
	
	// 18/03/2016 essai recevoir un argument supplementaire: PlateBarcode
	// public Experiment importFile(Experiment experiment, PropertyFileValue pfv, String plateBarcode, ContextValidation contextValidation)
	@Override
	public Experiment importFile(Experiment experiment, PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {
		
		InputStream is = new ByteArrayInputStream(pfv.value);
		//DEBUG..
		String plateBarcode="DEBUG-ABCDE";
		Logger.info ("checking plate barcode="+plateBarcode);
		
		// plusieurs erreurs possibles...si le fichier n'est pas de l'Excel, ou n'est pas lisible...
		Workbook wb = WorkbookFactory.create(is);
		
		Sheet sheet = wb.getSheet("Indexing");//case sensitive??
		if (sheet == null ){
			contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.sheet.missing","Indexing");
			return experiment;
		}
		/* description de l'onglet a traiter:
		 *  ligne de header en position 3 constituee de 3 champs: 
		 *     colA=Sample Well, colB=Index Well, colC=Index Name
		 *  96 lignes de donnees (max)
		 *     colA=position de plaque : A1-H12 si plaque 96
		 *     colB= un nombre si le puit est indexé
		 *     colC= un nom d'index si la colonne B n'est pas vide !!! c'est une formule
		 *     Modifs 18/03/2016 en D1 label 'Plate barcode' et en D2 le barcode
		 */
		
		//-1- verifier que le fichier correspond a la plaque a traiter:
		
		if (( getStringValue(sheet.getRow(0).getCell(3))==null ) || !(getStringValue(sheet.getRow(0).getCell(3)).equals("Plate Barcodeeeeee"))){
			contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.header.missing","Plate Barcode","1","D");
		}
		//if ( !getStringValue(sheet.getRow(0).getCell(4)).equals(plateBarcode) ) {
		//	contextValidation.addErrors("Erreurs fichier","Barcode '"+ getStringValue(sheet.getRow(0).getCell(4)) +"' ne correspond pas a celui qui est attendu" );
		//}
		//if ( getStringValue(sheet.getRow(0).getCell(4))==null ) {
		//	contextValidation.addErrors("Erreurs fichier","DEBUG..barcode manquant");
		//}		
		
		//-2- verifier qu'on trouve les 3 headers

		if ((getStringValue(sheet.getRow(2).getCell(0))==null ) || !(getStringValue(sheet.getRow(2).getCell(0)).equals("Sample Well"))){
			contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.header.missing","Sample Well","3","A");
		}
		if ((getStringValue(sheet.getRow(2).getCell(1))==null ) || !(getStringValue(sheet.getRow(2).getCell(1)).equals("Index Well"))){
			contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.header.missing","Index Well","3","B");
		}
		if (( getStringValue(sheet.getRow(2).getCell(2))==null ) || !(getStringValue(sheet.getRow(2).getCell(2)).equals("Index Name"))){
			contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.header.missing","Index Name","3","C");
		}

		if (contextValidation.hasErrors()){
			return experiment;
		}
		
		Map<String,Index> results = new HashMap<String,Index>(0);
		
		// traiter les 96 lignes
		for(int i = 3; i < 3+96; i++){
			String platePosition = getStringValue(sheet.getRow(i).getCell(0));
			
			//verifier que c'est une position definie et valide 
			if (ValidationHelper.required(contextValidation, platePosition, "plate Position; line "+(i+1)) &&
					InputHelper.isPlatePosition(contextValidation,platePosition, 96, (i+1))){
				//verifier si la position n'est pas deja connue
				if (results.containsKey(platePosition)) {
					contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.position.duplicate", platePosition, (i+1));
				}
						
				//verifier l'index 
				Double indexNum = getNumericValue(sheet.getRow(i).getCell(1));
				// attention une plaque peut etre partielle donc toutes les positions ne sont pas indexees
				if (indexNum != null ){
					String indexName = getStringValue(sheet.getRow(i).getCell(2)); // !! c'est une formule
					// verifier que cet index existe (type fixe pour l'instant...)
					Index idx = InputHelper.getIndexByName(indexName,"index-illumina-sequencing");
					if ( idx != null) {
						results.put(platePosition,idx );
					} else {
						contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.tag.notexist", indexName, (i+1));
					}
				}
			}	
		}
		
		//validation: verifier que tous les puits recoivent un index...
		if (!contextValidation.hasErrors()){
			experiment.atomicTransfertMethods
				.stream()
				.map(atm -> atm.inputContainerUseds.get(0))
				.forEach(icu -> {
					if(!results.containsKey(getCodePosition(icu.code))){
						contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.tag.missing",getCodePosition(icu.code));
					}
				});
		}
		
		
		//update tag et tag Categorie ...
		Logger.info ("update tag and tagCatgory");
		if(!contextValidation.hasErrors()){
			experiment.atomicTransfertMethods
				.stream()
				.forEach(atm -> {	
					InputContainerUsed icu = atm.inputContainerUseds.get(0);
					OutputContainerUsed ocu = atm.outputContainerUseds.get(0);
					String icupos=getCodePosition(icu.code);
					
					///PropertySingleValue tag = getPSV(ocu, "tag");
					PropertySingleValue tagPsv = new PropertySingleValue();
					tagPsv.value = results.get(icupos).name;
					ocu.experimentProperties.put("tag", tagPsv);
					
					//PropertySingleValue tagCategory = getPSV(ocu, "tagCategory");
					PropertySingleValue tagCategoryPsv = new PropertySingleValue();
					tagCategoryPsv.value = results.get(icupos).categoryCode;
					ocu.experimentProperties.put("tagCategory", tagCategoryPsv);
				});
		}
		
		
		return experiment;
    }
	

	//extraire la partie finale du code de l'inputContainer ( dans ce cas pourrait aller dans dans InputHelper???
	//utiliser container.line + container.column serait plus propre ???
	public String getCodePosition(String icuCode) {
		return icuCode.substring(icuCode.indexOf("_")+1);
	}

}

