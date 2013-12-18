package models.util;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;

public class DataMappingCNS {

	public static String getContainerSupportCode(String code) {
		return null;
	}

	

	public static String getImportTypeCode(boolean tara, boolean adapter) {
		
		//Logger.debug("Adaptateur "+adapter);
		//Logger.debug("Tara "+tara);
		if(adapter){
			if(tara){
				return "tara-library";
			}
			else { return "library"; }
		}
		else if(tara){
			return "tara-default";
		}
		else {
			 return "default-import";
		}
	}
	
	public static String getSampleTypeFromLims(String tadnco,String tprco) {

		if(tadnco.equals("15")) return "fosmid";
		else
		if(tadnco.equals("8")) return "plasmid";
		else
		if(tadnco.equals("2")) return "BAC";
		else
		if(tadnco.equals("1") && !tprco.equals("11")) return "gDNA";
		else
		if(tadnco.equals("1") && tprco.equals("11")) return "MeTa-DNA";
		else
		if(tadnco.equals("16")) return "gDNA";
		else
		if(tadnco.equals("19") || tadnco.equals("6")) return "amplicon";
		else
		if(tadnco.equals("12")) return "cDNA";
		else
		if( tadnco.equals("11")) return "total-RNA";
		else 
		if(tadnco.equals("18")) return "sRNA";
		else
		if(tadnco.equals("10")) return "mRNA";
		else
		if(tadnco.equals("17")) return "chIP";
		else
		if(tadnco.equals("20")) return "depletedRNA";
		else
		if(tadnco.equals("9") || tadnco.equals("14")) return "default-sample-cns";
		//Logger.debug("Erreur mapping Type materiel ("+tadnco+")/Type projet ("+tprco+") et Sample Type");
		return null;
	}

	public static String getStateFromLims(String string) {
		

		/*

Lot sequence

| IW-V  | En attente �valuation       |
| A     | Disponible                  |

| UA    | Indisponible                |


   
   Dans le lims : Etat run
  
 A_traiter                      
 En_traitement                  
 Traite                         
 Sans_traitement                
 Non_transfere
 
 Dispatch
       
 Valide 
 
 Etat Lot seq
 
		A_traiter                      
 Non_traite                     
 Sans_sequence                  
 Traite    */
		return "F-V";
	}
	
	public static String getRunTypeCodeMapping(String string) {
		// TODO
		return "RHS2000";
	}



	public static String getStateReadSetFromLims(TBoolean valid) {
		// TODO Auto-generated method stub
		return "F-V";
	}

}
