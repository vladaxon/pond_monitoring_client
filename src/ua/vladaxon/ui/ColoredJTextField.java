package ua.vladaxon.ui;

import javax.swing.JTextField;

/**
 * Расширенный класс стандарного однострочного ввода.
 * Позволяет подсвечивать фон в зависимости от события(положительное, отрицательное, по умолчанию)
 */
public class ColoredJTextField extends JTextField implements ColoredTextField{
	
	/**
	 * Метод, устанавливающий положительный цвет фона.
	 */
	public void setAccepted(){
		this.setBackground(accepted);
	}
	
	/**
	 * Метод, устанавливающий отрицательный цвет фона.
	 */
	public void setDenied(){
		this.setBackground(denied);
	}
	
	/**
	 * Метод, устанавливающий цвет фона по умолчанию.
	 */
	public void setDefault(){
		this.setBackground(defcolor);
	}
	
	private static final long serialVersionUID = 1L;

}