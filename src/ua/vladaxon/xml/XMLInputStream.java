package ua.vladaxon.xml;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XMLInputStream extends ByteArrayInputStream{
	
	public XMLInputStream(InputStream in){
		super(new byte[2]);
		this.in = new DataInputStream(in);
	}
	
	public void receive() throws IOException{
		int lenght = in.readInt();
		byte[] data = new byte[lenght];
		in.read(data, 0, lenght);
		buf = data;
		count = lenght;
		mark = 0;
		pos = 0;
	}
	
	private DataInputStream in;

}
