package instruments.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import play.Logger;



public class TestFSWrite {

	public static void main(String[] args) {
		if(args.length == 1){
			File file = new File(args[0]);
			Writer writer = null;
			try {
				
				if(file.canWrite()){
					FileOutputStream fos = new FileOutputStream(file);
					writer = new OutputStreamWriter(fos, "UTF-8");
					writer.write("I'm happy :-)))");
					writer.close();
					fos.close();
				}else{
					System.out.println("Can wrint file : "+file.getAbsolutePath());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.error("IO error: "+e.getMessage(),e);
			}
			
		}
		

	}

}
