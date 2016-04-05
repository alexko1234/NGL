package controllers.instruments.io.qpcrlightcycler480ii;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;

/* ce n'est pas un fichier excel....
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
*/



import models.laboratory.parameter.Index;
import play.Logger;
import validation.ContextValidation;
import validation.utils.ValidationHelper;
import controllers.instruments.io.utils.AbstractInput;

public class Input extends AbstractInput {
	
   /* Description du fichier a traiter: TXT TAB délimité généré par LightCycler
	*
	*Experiment: 150929_KAPA-Lib-Quant_PCRFREE_P17  Selected Filter: SYBR Green I / HRM Dye (465-510)
	*Include	Color	Pos		Name		Cp		Concentration	Standard	Status
	*True		255		A1		Sample 1	9.66	4.71E0			0	
	*True		255		A2		Sample 2	9.86	4.12E0			0	
	*True		255		A3		Sample 3	10.01	3.73E0			0	
	*.....    	
	*	 
	*   !!!! si les operateurs cochent/decochent des puits dans le logiciel qui genere le fichier 
	*  l'ordre des puits n'est plus assuré...
	*   Pour les puits en erreur, il n'y a rien dans les colonnes "Cp" et "Concentration"
	*   Ignorer les lignes dont la colonne est > 20, ce sont des controles ( voir aussi remapPosition() )
	*/
	
	@Override
	public Experiment importFile(Experiment experiment,PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
		
		// NOM DE LA PROPRIETE A CHANGER...
		int sector=0;
		if (experiment.instrumentProperties.containsKey("sector96")){
			PropertySingleValue psv = (PropertySingleValue) experiment.instrumentProperties.get("sector96");
			Logger.info( "sector96="+ psv.value.toString() );
			
			if ( psv.value.toString().equals("1-48") ){ 
				sector=0; 
			}else { 
				sector=1;
			}
			Logger.info( "sector="+sector);
		}else {
			contextValidation.addErrors("Erreur","Instrument propertie 'program' non supporté");
			return experiment;
		}
		
		// question pour Julie: actuellement la taille des fragments est de 350...peut changer ???
		// => nouvelle propriété de l'instrument??? ou HARCODED???
		int size=350;
		
		//tableau des facteurs de dilution et leur repetition sur la plaque 384
		double[] fDilution={5000,5000,5000,   50000,50000,50000};
		int nbRep=6; // 2 dilutions avec 3 repetition=> 6; chaque puit 96 initial est traité 6 fois dans la plaque 384
		
		// hashMap  pour stocker les concentrations du fichier
		Map<String,Double> data = new HashMap<String,Double>(0);
		
		InputStream is = new ByteArrayInputStream(pfv.value);
		
		// Ce n'est pas un fichier MS-Excel mais un fichier TXT TAB delimité a lire ligne a ligne. 
		// utiliser un bufferReader...Merci Nicolas	!!
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		int n = 0;
		String line;
		
		while ((line = reader.readLine()) != null) {
			/// attention si le fichier vient d'une machine avec LOCALE = FR les décimaux utilisent la virgule!!!
			String[] cols = line.replace (",", ".").split("\t");
			
			// verifier la premiere ligne d'entete
			if ((n == 0) && ( ! line.matches("Experiment:(.*)") ) ){
				contextValidation.addErrors("Erreurs fichier","experiments.msg.import.header-label.missing","2", "Experiment:");
				return experiment;
			}
			// commencer le traitement en sautant la 2eme ligne d'entete
			if (n >1 ) {
				// description d'une ligne de donnees:
				//  0         1      2       3          4            5             6          7
				//Include	Color	Pos		Name		Cp		Concentration	Standard	Status
				// status est optionnel ????
				//Logger.info ("ligne "+n+": lg="+cols.length );
				if ( cols.length < 7 || cols.length > 8 ) {
					contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.linefields.unexpected",(n+1) );
					continue;
				}
			
				String pos384=cols[2];
				// verifier que c'est une position 384 valide ???
				if  ( !isValidPlatePosition (pos384, 384, contextValidation) ){
					contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.illegal384Position",(n+1), pos384);
				} else {
				  // !!! il faut une position sur 3 caracteres pour pouvoir par la suite trier dans l'ordre reel !!!
				  String pos0384=add02pos(pos384);
				  Logger.info ("ligne "+n+ ": "+pos384+" => "+pos0384);
				
				  int col384 = Integer.parseInt(pos384.substring(1));
				  // ignorer les lignes correspondant aux temoins (colonnes 22,23,24)
				  if (col384 > 20 ){ continue; }
			 
				  // en cas d'erreur de lecture de concentration concentration manquante => forcer a 0 pour les calculs utltérieurs
				  double concentration=0;
				  if ( ! cols[5].equals("") ){ concentration=Double.parseDouble(cols[5]); }
				  //Logger.info ("ligne "+n+"concentration="+concentration);
				  data.put(pos0384, concentration);
				}
		  }
		  ++n;
		}

		reader.close();
		
		if (contextValidation.hasErrors()){ 
			return experiment;
		}
		
		// nouveau HashMap pour les concentrations calculees en nM
		Map<String,Double> results = new HashMap<String,Double>(0);
		
		/* traiter dans l'ordre des positions; 
		 * 6 lignes successives (1block) doivent correspondre au meme echantillon avec 3 repetions de 2 dilutions
		 */
		SortedSet<String> pos0384 = new TreeSet<String>(data.keySet());
		int nbblock=0;
		int rep=0;;
		double[] listConc= new double[nbRep];
		double rocheFactor= (double)( 452 / size); //calculé une seule fois
		
		for (String key : pos0384) { 
			// transformer la concentration du fichier (pM) en nM [ formule donné par Roche ]
			// conc_nM= conc_pM * ( fact_dilution/1000 ) * ( 452/size ) 
			double concentration_nM =  data.get(key) * (double)( fDilution[rep] / 1000 ) * rocheFactor;
			
			// stocker concentration2 pour faire moyenne plus tard...
			listConc[rep]=concentration_nM;
			Logger.info ("pos384="+key+" CONC (pM)="+  data.get(key) +" CONC (nM) ="+concentration_nM );
			
			nbblock++;
			rep++;
			if ((nbblock % nbRep ) == 0 ) {	
				//nbblock est multiple de 6 => fin d'un block de lignes
				
				//calcul de la moyenne des 6 concentrations
				double moyConc_nM= mean(listConc) ;

				// remapper en 96
				String pos96=remapPosition (key, sector, contextValidation );
				
				Logger.info ("FIN DE BLOCK...pos384="+key+" > pos96="+ pos96+"| MOY CONC="+moyConc_nM);
				results.put(pos96,moyConc_nM );
				Logger.info (pos96 + " belong to sector "+sector+" ?? "+ belongToSector96(pos96, sector, contextValidation));

				//reinitialiser le tableau 
				listConc= new double[nbRep];
				// reinitialiser le compteur de repetitions
				rep=0;
			}
		}

		// Verifier que tous les puits du secteur concerné par l'import ont tous une concentration, la mettre a jour
		//  ( verification minimale pour eviter une erreur de choix du fichier initial...)
		if (!contextValidation.hasErrors()) {
			final int sector_arg = sector;
			experiment.atomicTransfertMethods
				.stream()
				.map(atm -> atm.inputContainerUseds.get(0))
				.forEach(icu -> {
					if ( belongToSector96(getCodePosition(icu.code), sector_arg,  contextValidation)) {
						String icupos=getCodePosition(icu.code);
						if (!results.containsKey(icupos) ){
							contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.concentration.missing",icupos);
						} 
						//else {
						//	Logger.info ("set concentration="+results.get(icupos)+" for icu "+ icu.code);
						//	icu.concentration.value=results.get(icupos);
						//}
					}
				});
		}
		
		// ne positionner les valeurs que s'il n'y a pas d'erreur a la verification precedente...
		if (!contextValidation.hasErrors()) {
			final int sector_arg = sector;
			experiment.atomicTransfertMethods
				.stream()
				.map(atm -> atm.inputContainerUseds.get(0))
				.forEach(icu -> {
					if ( belongToSector96(getCodePosition(icu.code), sector_arg,  contextValidation)) {
						String icupos=getCodePosition(icu.code);
						Logger.info ("set concentration="+results.get(icupos)+" for icu "+ icu.code);
						//icu.concentration.value=results.get(icupos);
						
						PropertySingleValue concentration = getPSV(icu, "concentration1");
						concentration.value = results.get(icupos);
						concentration.unit = "nM";
						
						
					}
				});
		}
		
		
		return experiment;
	}
	
	/* description de la tranformation effectué sur le robot:
	 * 
	 *  contient les mesures sur une plaque 384 puits d'une demi plaque 96 ( 6 premieres ou 6 dernieres colonnes)
	 *  il faut donc remaper les resutats trouves dans le fichier vers les puits concernes
	 *  1 parametre necessaire: de quelle demi plaque s'agit-il ? colonne 1-6 ou 7-12 ? 
	 *  
   	 *plaque 96 :
   	 *
   	 *   1.......6 7.......12
  	 *  +-------------------+
	 * A|         |         |
 	 * B|         |         |
 	 * ....
 	 * H|         |         |
  	 * +-------------------+
     *      ||       ||
     *      ||        ==>  sector 1 ( samples 49 a 96 )
     *       ==>  sector 0 ( samples 1 a 48)
     *
	 *
 	 * chaque plaque 384 est composé de 4 zones :
  	 *  3 zones pour les echantillons venant d'une demi plaque 96
  	 *  1 zone  pour les temoins
  	 *
  	 *   1...6.8...13.14...20.22..24
  	 * A|     |      |       |     |
  	 * ....
  	 * P|     |      |       |     |
  	 *  +--------------------------+
  	 *
  	 * les 2 premieres colonnes d'un secteur 96 sont distribuées en colonnes 1 a 6
  	 * les 2 colonnes centrales sont distribuées en colonnes 8 a 13 
  	 * les 2 dernieres colonnes sont distribuées en colonnes 14 a 20
  	 * les colonnes 22 a 24 contiennent des controles (pas des échantillons venant de la plaque 96)=> a ignorer !! 
  	 *
  	 * chaque puit de la plaque initiale 96 est déposé 6 fois sur la plaque 384:
  	 *    - 3 en dilution  1/5000 
  	 *    - 3 en dilution  1/50000 
  	 *    
  	 *    NOTE: pour l'instant au labo ne sont realisées que des demi plaques ( < 48 samples..)=> sector 0 uniqut
	 */
	
	public String remapPosition(String pos384, int sector, ContextValidation contextValidation) {
		int asciiRow96=0;
		int col96=0;
		
		//recuperer le code ASCII du premier caractere de la position
		int asciiRow384=(int)pos384.charAt(0);
		//recuper la colonne
		int col384 = Integer.parseInt(pos384.substring(1));
		
		//ascii A=65, ascii P=80
		if ( asciiRow384 < 65 || asciiRow384 > 80 ) { 
			contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.illegalRowPosition",pos384); 
			return "ERR-ROW384";
		}
		else
		{
			// transformer le code ascii du row384 en rank (A=1 .... P=16)
			// ne garder que la valeur entiere de la division par 2
			int map=  (asciiRow384 - 65 )/2 ;

			// determiner si c'est une ligne paire ou impaire vi l'operateur modulo 2... 
			if ( (asciiRow384 % 2) == 1 ){ 
				//pair		
				asciiRow96=asciiRow384 -map;
				if      ( col384 > 0  && col384 < 7)  { col96= 1 +(sector*6); }
			    else if ( col384 > 7  && col384 < 14) { col96= 3 +(sector*6); }
			    else if ( col384 > 14 && col384 < 21) { col96= 5 +(sector*6); }
			    else { 
			    	contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.illegalColumnPosition",pos384);
			    	return "ERR-COL384";
			    }
			} else  {  
				//impair
				asciiRow96=asciiRow384 -map -1 ;
				if      ( col384 > 0  && col384 < 7)  { col96= 2 +(sector*6); }
			    else if ( col384 > 7  && col384 < 14) { col96= 4 +(sector*6); }
			    else if ( col384 > 14 && col384 < 21) { col96= 6 +(sector*6); }
			    else { 
			    	contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.illegalColumnPosition",pos384);
			    	return "ERR-COL384";
			    }
			}
		}

		//retransformer asciiRow96  et col96 en string
		String pos96=Character.toString((char)asciiRow96) + col96;
		
		return pos96;		
	}
	
	//Calcul de moyenne; source: https://openclassrooms.com
	public static double mean(double[] m) {
	    double sum = 0;
	    for (int i = 0; i < m.length; i++) {
	        sum += m[i];
	    }
	    return sum / m.length;
	}
	
	public Boolean belongToSector96(String pos96, int sector, ContextValidation contextValidation) {
		// verifier si valide ??
		if ( !isValidPlatePosition(pos96, 96, contextValidation) ){
			return false;
		}
		
		// les secteurs sont ici uniquement defini par les colonnes		
		int col96 = Integer.parseInt(pos96.substring(1));
		
		if ( col96 > 0 && col96 < 7  && sector == 0 ){ return true;}
		if ( col96 > 8 && col96 < 13  && sector == 1 ){ return true;}
		
		return false;
	}
	
	//----------------------------------------------------------------====> Helper
	private String add02pos(String pos){
		String row=pos.substring(0,1);
		String col=pos.substring(1);
		if (col.length() == 1){ 
			return row+"0"+col ;
		}else{
			return pos;
		}
	}
	
	//extraire la partie finale du code de l'inputContainer ( dans ce cas pourrait aller dans dans InputHelper???
	//utiliser container.line + container.column serait plus propre ???
	public String getCodePosition(String icuCode) {
		return icuCode.substring(icuCode.indexOf("_")+1);
	}
	
	public Boolean isValidPlatePosition (String pos, int format, ContextValidation contextValidation ) {	
		int asciiRow=(int)pos.charAt(0);              // code ASCII du premier caractere de la position
		int col = Integer.parseInt(pos.substring(1)); // colonne de la position
		
		if ( format == 96 ) {
			//ascii A=65, ascii H=72
			if ( asciiRow < 65 || asciiRow > 72 ) { 
				contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.illegalRowPosition",pos, format); 
				return false;
			}
	
			if (col < 1 || col > 12 ) {
				contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.illegalColumnPosition",pos, format); 
				return false;
			} 
		} else if ( format == 384 ) {
				//ascii A=65, ascii Q=81
				if ( asciiRow < 65 || asciiRow > 81 ) { 
					contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.illegalRowPosition",pos, format); 
					return false;
				}
		
				if (col < 1 || col > 24 ) {
					contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.illegalColumnPosition",pos, format); 
					return false;	
				}
		} else {
			// not supported plate format...
			return false;
		}
		
		return true;
	}	
		

}
