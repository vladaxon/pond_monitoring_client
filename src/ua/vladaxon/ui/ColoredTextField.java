package ua.vladaxon.ui;

import java.awt.Color;

/**
 * Интерфейс, реализация которого позволяет подсвечивать 
 * фон в зависимости от события(положительное, отрицательное, по умолчанию)
 */
public interface ColoredTextField {
	
	/**
	 * Метод, устанавливающий положительный цвет фона.
	 */
	public void setAccepted();
	
	/**
	 * Метод, устанавливающий отрицательный цвет фона.
	 */
	public void setDenied();
	
	/**
	 * Метод, устанавливающий цвет фона по умолчанию.
	 */
	public void setDefault();
	
	/**Цвет фона соответствующий отрицательному событию*/
	public static final Color denied = new Color(255, 130, 130);
	/**Цвет фона соответствующий положительному событию*/
	public static final Color accepted = new Color(130, 255, 130);
	/**Цвет фона по умолчанию*/
	public static final Color defcolor = Color.WHITE;

}