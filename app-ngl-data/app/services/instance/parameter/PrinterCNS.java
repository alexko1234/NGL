package services.instance.parameter;

import static services.instance.InstanceFactory.newBBP11;
import static services.instance.InstanceFactory.newBarcodePosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.laboratory.parameter.printer.BBP11;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;

import org.mongojack.DBQuery;

import play.Logger;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class PrinterCNS {
	
	public static void main(ContextValidation ctx) {	

		Logger.info("Start to create Printers collection for CNS");
		Logger.info("Remove Printers");
		removerPrinters(ctx);
		Logger.info("Save Printers ...");
		savePrinters(ctx);
		Logger.info("CNS Printers collection creation is done!");
	}
	
	
	private static void removerPrinters(ContextValidation ctx) {
		MongoDBDAO.delete(InstanceConstants.PARAMETER_COLL_NAME, BBP11.class, DBQuery.is("typeCode", "BBP11"));
	}	
	
	public static void savePrinters(ContextValidation ctx){		
		List<BBP11> lp = new ArrayList<BBP11>();
		
		lp.add(newBBP11("BBPTESTCB", "finishing / tube ","bbp7.tx.local",9100,"2","15","1",true, 
				Arrays.asList(
						newBarcodePosition("1","CB 2D TUBE",298,"A100,30,0,2,1,1,N","b20,30,A,d4",true, true),
						newBarcodePosition("2","CB 1D TUBE",298,"A25,10,0,3,1,1,N","B25,55,0,1B,2,2,30,B",true, false)
						)));
		ctx.addKeyToRootKeyName("printers");
		for(BBP11 printer:lp){
			InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME, printer,ctx);
			
			Logger.debug("printer '"+printer.name + "' saved..." );
		}
	}


	
}
