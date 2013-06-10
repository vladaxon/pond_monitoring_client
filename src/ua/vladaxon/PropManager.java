package ua.vladaxon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Класс управляющий свойствами. При запуске пытается загрузить
 * пользовательские свойства из файла. Если загрузка завершилась
 * неудачей - устанавливаются свойства по умолчанию и сохраняются
 * в файл.
 */
public class PropManager {
	
	/**
	 * Конструктор класса. Если свойства из файла
	 * не загрузились - используются свойства по умолчанию.
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
	 * Загружает свойства из файла и проверяется корректность свойств.
	 * @return true - если свойства загружены.
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
	 * Сохраняет свойства в файл.
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
	 * Возвращает объект свойств по умолчанию.
	 * @return Объект свойств.
	 */
	private Properties getDefault(){
		Properties props = new Properties();
		props.setProperty(keylist[0], "127.0.0.1");
		props.setProperty(keylist[1], "2812");
		return props;
	}
	
	/**Объект свойств*/
	private Properties props;
	/**Путь к внешнему файлу свойств по умолчанию*/
	private static final String propdefpath = "properties.txt";
	/**Список ключей свойств*/
	private static final String[] keylist = {"serverurl","serverport"};

}