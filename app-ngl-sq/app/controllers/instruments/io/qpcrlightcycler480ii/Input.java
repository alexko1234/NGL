package controllers.instruments.io.qpcrlightcycler480ii;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
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

	@Override
	public Experiment importFile(Experiment experiment,PropertyFileValue pfv,
			ContextValidation contextValidation) throws Exception {	
		
		InputStream is = new ByteArrayInputStream(pfv.value);
		
		/* ce n'est pas un fichier excel....
		Workbook wb = WorkbookFactory.create(is);
		Sheet sheet = wb.getSheetAt(0);
		*/
		
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
			contextValidation.addErrors("Erreur","Instrument propertie 'sector96' manquant");
			return experiment;
		}
		
		/* utilisation de TreeMap car le fichier peut ne pas etre dans l'ordre !!!
		 * => lire le fichier et stocker dans le TreeMap,, puis lire ceux ci dans l'ordre de a clé...
		 */
		
		Map<String,Double> cpMap = new HashMap<String,Double>(0);
		Map<String,Double> cptreeMap = new TreeMap<String,Double>(cpMap);
		
		Map<String,Double> concMap = new HashMap<String,Double>(0);
		Map<String,Double> conctreeMap = new TreeMap<String,Double>(concMap);
		
		/// Merci  Nicolas	
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		int n = 0;
		//int b = 0;
		String line;
		while ((line = reader.readLine()) != null) {
			String[] cols = line.split("\t");
			// ignorer les 2 premieres lignes d'entete du fichier
			if ((n == 0) && ( ! line.matches("Experiment:(.*)") ) ){
				contextValidation.addErrors("Erreurs fichier","experiments.msg.import.header-label.missing","2", "Experiment:");
				return experiment;
			}
			if (n >1 ) {
				// description d'une ligne de donnees:Include	Color	Pos		Name		Cp		Concentration	Standard	Status
				// status est optionnel ????
				//Logger.info ("ligne "+n+": lg="+cols.length );
				if ( cols.length < 7 || cols.length > 8 ) {
					contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.linefields.unexpected",(n+1) );
				}
			
				String pos384=cols[2];
				// !!! il faut une position sur 3 caracteres pour pouvoir par la suite trier dans l'ordre reel !!!
				String pos0384=add02pos(pos384);
				
				int col384 = Integer.parseInt(pos384.substring(1));
				// ignorer les lignes correspondant aux temoins (colonnes 22,23,24)
				if (col384 > 20 ){ continue; }
			 
				// Cp et concentration sont manquant si qq c'est mal passé=> forcer a 0
				double cp=0;
				if ( ! cols[4].equals("") ){ cp=Double.parseDouble(cols[4]); }
				//store in map
				cpMap.put(pos0384,cp );
				
				double concentration=0;
				if ( ! cols[5].equals("") ){ concentration=Double.parseDouble(cols[5]); }
				//store in map
				concMap.put(pos0384, concentration);
				
				Logger.info ("ligne "+n+": cp="+cp+ " / concentration="+concentration);

				String pos96=remapPosition (pos384, sector );
				Logger.info ("ligne "+n+": pos384="+pos384+" / pos96="+ pos96);
	
				
		  //   if (++b >= 6) {
		  //       ...
		  //       b = 0;
		  //   }
	  
		  }
		  ++n;
		}

		reader.close();
		
		if (contextValidation.hasErrors()){ 
			return experiment;
		}
		
		/* traiter dans l'ordre des positions....*/
		SortedSet<String> pos0384 = new TreeSet<String>(cpMap.keySet());
		for (String key : pos0384) { 
		   //String value = map.get(key);
		   Logger.info ("for pos"+key+ " CP="+ cpMap.get(key)+" CONC="+concMap.get(key));

		}
		
		/* description de l'onglet a traiter:
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
		 *
		 *description du fichier a traiter: TAB delimité..( PAS EXCEL !!!!)
		 *
		 *Experiment: 150929_KAPA-Lib-Quant_PCRFREE_P17  Selected Filter: SYBR Green I / HRM Dye (465-510)
		 *Include	Color	Pos		Name		Cp		Concentration	Standard	Status
		 *True		255		A1		Sample 1	9.66	4.71E0			0	
		 *True		255		A2		Sample 2	9.86	4.12E0			0	
		 *True		255		A3		Sample 3	10.01	3.73E0			0	
		 *.....    		 
		 * !!!! si les operateurs cochent/decochent des puits sans valeur dans le logiciel qui genere le fichier l'ordre n'est plus assuré...
		 */
		
		/*
		lire toutes les lignes du fichier
		- ligne entete=> ignorer
		- lignes dont colonne > 20 ignorer
		*/
		
		return experiment;
		
	}
	
	public String remapPosition(String pos384, int sector) {

		int col96=0;
		int asciiRow96=0;
		
		//recuperer le code ASCII du premier caractere de la position
		int asciiRow384=(int)pos384.charAt(0);
		//recuper la colonne
		int col384 = Integer.parseInt(pos384.substring(1));
		
		//ascii A=65, ascii P=80
		if ( asciiRow384 < 65 || asciiRow384 > 80 ) { return "ERR"; }
		else
		{
			// transformer le code ascii du row384 en rank (A=1 .... P=16)
			// ne garder que la valeur entiere de la division par 2
			int map=  (asciiRow384 - 65 )/2 ;

			// determiner si c'est une ligne paire ou impaire vi l'operateur modulo 2... 
			if ( (asciiRow384 % 2) == 1 ){ 
				//pair		
				asciiRow96=asciiRow384 -map;
				if      ( col384 >0  && col384 < 7)  { col96= 1 +(sector*6); }
			    else if ( col384 >7  && col384 < 14) { col96= 3 +(sector*6); }
			    else if ( col384 >14 && col384 < 21) { col96= 5 +(sector*6); }
			    else { col96=0;} 
			} 
			else  {  
				//impair
				asciiRow96=asciiRow384 -map -1 ;
				if      ( col384 >0  && col384 < 7)  { col96= 2 +(sector*6); }
			    else if ( col384 >7  && col384 < 14) { col96= 4 +(sector*6); }
			    else if ( col384 >14 && col384 < 21) { col96= 6 +(sector*6); }
			    else { col96=0;} 
			}
		}

		//retransformer asciiRow96  et col96 en string
		String pos96=  Character.toString((char)asciiRow96) + col96;
		
		return pos96;
		
	}
	
	private String add02pos(String pos){
		String row=pos.substring(0,1);
		String col=pos.substring(1);
		if (col.length() == 1){ 
			return row+"0"+col ;
		}else{
			return pos;
		}
	}
}
