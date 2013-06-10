package ua.vladaxon.xml;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class XMLOutputStream extends ByteArrayOutputStream {
	
	public XMLOutputStream(OutputStream out){
		super();
		this.out = new DataOutputStream(out);
	}
	
	public void send() throws IOException{
	     byte[] data = toByteArray();
	     out.writeInt(data.length);
	     out.write(data);
	     reset();
	}
	
	private DataOutputStream out;

}