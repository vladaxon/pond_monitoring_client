package ua.vladaxon;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Базовый класс для слушателей корректности ввода.
 * Реализует интерфейс слушателя документа и перенаправляет
 * вызов в 1 метод проверки правильности ввода.
 */
public abstract class CorrectFieldListener implements DocumentListener{

	@Override
	public void changedUpdate(DocumentEvent e) {
		this.testInput();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		this.testInput();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		this.testInput();
	}

	/**
	 * Общий метод проверки корректности.
	 */
	protected abstract void testInput();

	/**
	 * Возвращает текущее состояние корректности ввода.
	 * @return <b>true</b> - если текст в поле соответствует требованиям.
	 */
	public boolean getCorrectness(){
		return correctness;
	}
	
	/**
	 * Метод, регистрирующий объект общей корректности всех полей.
	 * @param correctlistener Слушатель корректности.
	 */
	public void setCorectListener(CorrectListener correctlistener){
		this.correctlistener = correctlistener;
	}
	
	/**Слушатель общей корректности*/
	protected CorrectListener correctlistener = null;
	/**Флаг корректности*/
	protected boolean correctness = false;	
	/**Регулярное выражение символов слова*/
	protected static final String wordsymb = "[a-zA-Z_0-9]*";
	/**Регулярное выражение неотрицательных чисел*/
	protected static final String nonegativ = "[0-9]*";
	/**Регулярное выражение неотрицательных чисел*/
	protected static final String negativorpositiv = "-?[0-9]+";
	/**Сообщение выводимое при вводе недопустимых символов*/
	protected static final String notwordsymbols = 
			"Поле должно содержать латинские буквы, цифры или символ подчеркивания";
	/**Подсказка, выводимая при неправильном вводе числа*/
	protected static final String notnumber = "Число не соответствует требованиям";
	/**Подсказка при правильном вводе числа*/
	protected static final String number = "Число принято";
	
}