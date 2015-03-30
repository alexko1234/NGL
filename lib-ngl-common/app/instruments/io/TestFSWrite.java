package instruments.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import play.Logger;
import scala.io.Codec;

public class TestFSWrite {

	public static void main(String[] args) {
		if(args.length == 1){
			File file = new File(args[0]);
			Writer writer = null;
			try {
				
				if(file.canWrite()){
					FileOutputStream fos = new FileOutputStream(file);
					writer = new OutputStreamWriter(fos, Codec.UTF8().name());
					writer.write("I'm happy :-)))");
					writer.close();
					fos.close();
				}else{
					Logger.error("Can wrint file : "+file.getAbsolutePath());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		

	}

}
