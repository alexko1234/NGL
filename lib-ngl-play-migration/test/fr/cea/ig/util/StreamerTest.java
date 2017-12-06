package fr.cea.ig.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.io.Writer;

public class StreamerTest {
	
	interface TextGenerator {
		void writeTo(Writer w) throws IOException;
	}
	
	class StringTG implements TextGenerator {
		private String txt;
		public StringTG(String txt) {
			this.txt = txt;
		}
		public void writeTo(Writer w) throws IOException {
			w.write(txt);
		}
	}
	public void streamCmp(int bufferSize, TextGenerator g) throws IOException {
		InputStream i = Streamer.stream(bufferSize,new Streamer.IStreamer() {
			public void streamTo(OutputStream o) throws IOException {
				PrintWriter pw = new PrintWriter(o);
				g.writeTo(pw);
				pw.close();
			}
		});
		// Read stream as a single string
		java.util.Scanner s = new java.util.Scanner(i).useDelimiter("\\A");
		String r0 = s.hasNext() ? s.next() : "";
		// Use String writer
		StringWriter sw = new StringWriter();
		g.writeTo(sw);
		String r1 = sw.toString();
		assertEquals(r1,r0);
	}
	
	public void streamCmp(int bufferSize, String text) throws IOException {
		streamCmp(bufferSize,new StringTG(text));
	}
	
	@Test
	public void test0() throws IOException {
		streamCmp(4, "0123456789");
	}
	
}