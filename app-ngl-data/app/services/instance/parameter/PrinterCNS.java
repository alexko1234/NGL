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

import com.typesafe.config.ConfigFactory;

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
		if(ConfigFactory.load().getString("ngl.env").equals("UAT") 
				|| ConfigFactory.load().getString("ngl.env").equals("DEV")){
			
		lp.add(newBBP11("BBPTESTCB", "pour test","bbp7.tx.local",9100,"2","15","1",true, 
				Arrays.asList(
						newBarcodePosition("1","CB 2D TUBE",298,"A100,30,0,2,1,1,N","b20,30,A,d4",true, true),
						newBarcodePosition("2","CB 1D TUBE",298,"A25,10,0,3,1,1,N","B25,55,0,1B,2,2,30,B",true, false)
						)));
		}
		lp.add(newBBP11("BBP1", "prod / tube","bbp1.tx.local",9100,"2","15","1",true, 
				Arrays.asList(
						newBarcodePosition("1","CB 2D TUBE",298,"A100,30,0,2,1,1,N","b20,30,A,d4",true, true)
						)));
		lp.add(newBBP11("BBP2", "prod / tube","bbp2.tx.local",9100,"2","15","1",true, 
				Arrays.asList(
						newBarcodePosition("1","CB 2D TUBE",298,"A100,30,0,2,1,1,N","b20,30,A,d4",true, true)
						)));
		lp.add(newBBP11("BBP3", "flx / tube","bbp3.tx.local",9100,"2","15","1",true, 
				Arrays.asList(
						newBarcodePosition("1","CB 2D TUBE",298,"A100,30,0,2,1,1,N","b20,30,A,d4",true, true)
						)));
		lp.add(newBBP11("BBP4", "sequence / plaque","bbp4.tx.local",9100,"2","15","1",true, 
				Arrays.asList(
						newBarcodePosition("1","6cm code barre en dessous",712,"A25,0,0,2,2,1,N","B75,28,0,1B,2,2,39,N",true, false),
						newBarcodePosition("2","6cm code barre en dessus",712,"A25,42,0,2,2,1,N","B75,0,0,1B,2,2,39,N",false, false)						
						)));
		lp.add(newBBP11("BBP5", "finishing / tube","bbp5.tx.local",9100,"2","15","1",true, 
				Arrays.asList(
						newBarcodePosition("1","CB 2D TUBE",298,"A100,30,0,2,1,1,N","b20,30,A,d4",true, true)
						)));
		
		lp.add(newBBP11("BBP7", "prod / tube","bbp7.tx.local",9100,"2","15","1",true, 
				Arrays.asList(
						newBarcodePosition("1","CB 2D TUBE",298,"A100,30,0,2,1,1,N","b20,30,A,d4",true, true)
						)));
		lp.add(newBBP11("BBP8", "bureau Nanopore","bbp8.tx.local",9100,"2","15","1",true, 
				Arrays.asList(
						newBarcodePosition("1","CB 2D TUBE",298,"A100,30,0,2,1,1,N","b20,30,A,d4",true, true)
						)));
		lp.add(newBBP11("BBP9", "Labo Solexa / plaque","bbp9.tx.local",9100,"2","15","1",true, 
				Arrays.asList(
						newBarcodePosition("1","6cm code barre en dessous",712,"A25,0,0,2,2,1,N","B75,28,0,1B,2,2,39,N",true, false),
						newBarcodePosition("2","6cm code barre en dessus",712,"A25,42,0,2,2,1,N","B75,0,0,1B,2,2,39,N",false, false)						
						)));
		
		
		ctx.addKeyToRootKeyName("printers");
		for(BBP11 printer:lp){
			InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME, printer,ctx);
			
			Logger.debug("printer '"+printer.name + "' saved..." );
		}
	}

/*
  <BBP11 name="BBP1"  location="prod / tube" ip="bbp1.tx.local" port="9100" speed="2" density="15" print-configuration-id="7" inverse-list="true">
                        <barcode-position id="7" name="2.5cm date en dessous" label-width="298" label-command="A60,5,0,3,1,1,N" barcode-command="A60,65,0,3,1,1,N" barcode-bottom="false"/>
                </BBP11>
                <BBP11 name="BBP2"  location="prod / tube" ip="bbp2.tx.local" port="9100" speed="2" density="15" print-configuration-id="7" inverse-list="true">
                        <barcode-position id="7" name="2.5cm date en dessous" label-width="298" label-command="A60,5,0,3,1,1,N" barcode-command="A60,65,0,3,1,1,N" barcode-bottom="false"/>
                </BBP11>
                <BBP11 name="BBP3"  location="flx / tube" ip="bbp3.tx.local" port="9100" speed="2" density="15" print-configuration-id="7" inverse-list="true">
                        <barcode-position id="7" name="2.5cm date en dessous" label-width="298" label-command="A20,5,0,3,1,1,N" barcode-command="A20,65,0,3,1,1,N" barcode-bottom="false"/>
                </BBP11>
                <BBP11 name="BBP4"  location="sequence / plaque" ip="bbp4.tx.local" port="9100" speed="2" density="15" print-configuration-id="1" inverse-list="true">
                        <barcode-position id="1" name="6cm code barre en dessous" label-width="712" label-command="A25,0,0,2,2,1,N" barcode-command="B75,28,0,1B,2,2,39,N" barcode-bottom="true"/>
                        <barcode-position id="2" name="6cm code barre au dessus" label-width="712" label-command="A25,42,0,2,2,1,N" barcode-command="B75,0,0,1B,2,2,39,N" barcode-bottom="false"/>
                </BBP11>

                <BBP11 name="BBP5"  location="finishing / tube " ip="bbp5.tx.local" port="9100" speed="2" density="15" print-configuration-id="7" inverse-list="true">
                        <barcode-position id="7" name="2.5cm date en dessous" label-width="298" label-command="A60,5,0,3,1,1,N" barcode-command="A60,65,0,3,1,1,N" barcode-bottom="false"/>
                </BBP11> 

 */
	
}
