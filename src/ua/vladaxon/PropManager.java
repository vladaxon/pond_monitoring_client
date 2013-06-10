package ua.vladaxon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ����� ����������� ����������. ��� ������� �������� ���������
 * ���������������� �������� �� �����. ���� �������� �����������
 * �������� - ��������������� �������� �� ��������� � �����������
 * � ����.
 */
public class PropManager {
	
	/**
	 * ����������� ������. ���� �������� �� �����
	 * �� ����������� - ������������ �������� �� ���������.
	 */
	public PropManager(){
		if(!loadProps()){
			props = getDefault();
			saveProps();
		}
	}
	
	public String getServerURL(){
		return props.getProperty(keylist[0]);
	}
	
	public int getServerPort(){
		return Integer.parseInt(props.getProperty(keylist[1]));
	}
	
	/**
	 * ��������� �������� �� ����� � ����������� ������������ �������.
	 * @return true - ���� �������� ���������.
	 */
	private boolean loadProps(){
		props = new Properties();
		try {
			File propfile = new File(propdefpath);
			FileInputStream in = new FileInputStream(propfile);
			props.load(in);
			if(props.getProperty(keylist[0])==null){
				throw new Exception();
			}
			Integer.parseInt(props.getProperty(keylist[1]));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * ��������� �������� � ����.
	 */
	private void saveProps(){
		try {
			File propfile = new File(propdefpath);
			FileOutputStream out = new FileOutputStream(propfile);
			props.store(out, "Client properties");
		} catch (IOException e) {
		}
	}
	
	/**
	 * ���������� ������ ������� �� ���������.
	 * @return ������ �������.
	 */
	private Properties getDefault(){
		Properties props = new Properties();
		props.setProperty(keylist[0], "127.0.0.1");
		props.setProperty(keylist[1], "2812");
		return props;
	}
	
	/**������ �������*/
	private Properties props;
	/**���� � �������� ����� ������� �� ���������*/
	private static final String propdefpath = "properties.txt";
	/**������ ������ �������*/
	private static final String[] keylist = {"serverurl","serverport"};

}