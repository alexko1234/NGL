package models.util;

import play.Logger;
import models.laboratory.common.instance.TBoolean;

public class DataMappingCNS {

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

	public static String getStateRunFromLims(TBoolean state) {

		if(state.equals(TBoolean.UNSET)){
			return "F-RG";
		}
		else return "F";
	}

	public static String getRunTypeCodeMapping(String insCategoryCode) {
		if(insCategoryCode.equals("GA I")){
			return "RGAIIx";
		} else if(insCategoryCode.equals("GA II")){
			return "RGAIIx";
		} else if(insCategoryCode.equals("GA IIx")){
			return "RGAIIx";
		} else if(insCategoryCode.equals("Hi2500")){
			return "RHS2500";
		} else if(insCategoryCode.equals("Hi2500 Fast")){
			return "RHS2500R";
		} else if(insCategoryCode.equals("HiSeq 2000")){
			return "RHS2000";
		} else if(insCategoryCode.equals("MiSeq")){
			return "RMISEQ";
		}
		return null;
	}


	//A revoir avec Julie validation bio et prod
	public static String getStateReadSetFromLims(String state,TBoolean validation) {
	//	Logger.debug("State :"+state+", Validation :"+validation);
		if(state.equals("A_traiter")){
			return "IW-QC";
		} else if(state.equals("En_traitement")){
			return "IP-QC";
		} else if (state.equals("Traite") && validation==TBoolean.UNSET){
			return "IW-QC";
		} else if (state.equals("Traite") && validation==TBoolean.TRUE){
			return "A";
		} else if (state.equals("Traite") && validation==TBoolean.FALSE){
			return "UA";
		} else if(state.equals("Non_traite") || state.equals("Sans_sequence") || state.equals("Indefini")){
			return "UA";
		}
		
		return null;
	}

	public static String getInstrumentTypeCodeMapping(String insCategoryCode) {
		if(insCategoryCode.equals("GA I")){
			return "GAIIx";
		} else if(insCategoryCode.equals("GA II")){
			return "GAIIx";
		} else if(insCategoryCode.equals("GA IIx")){
			return "GAIIx";
		} else if(insCategoryCode.equals("Hi2500")){
			return "HISEQ2500";
		} else if(insCategoryCode.equals("Hi2500 Fast")){
			return "HISEQ2500";
		} else if(insCategoryCode.equals("HiSeq 2000")){
			return "HISEQ2000";
		} else if(insCategoryCode.equals("MiSeq")){
			return "MISEQ";
		}
		return null;
	}

}
