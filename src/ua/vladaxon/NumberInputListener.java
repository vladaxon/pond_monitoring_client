package ua.vladaxon;

import ua.vladaxon.CorrectFieldListener;
import ua.vladaxon.ui.ColoredJTextField;

/**
 * Объект слушателя корректности ввода в поле. Слушатель используется
 * при создании новой записи ставка и следит за правильностью ввода числовых значений.
 */
public class NumberInputListener extends CorrectFieldListener{
	
	/**
	 * Конструктор объекта.
	 * @param field Проверяемое поле
	 * @param emptallowed Флаг разрешение пустого поля
	 * @param negallowed Флаг разрешения отрицательного числа
	 */
	public NumberInputListener(ColoredJTextField field, boolean emptallowed, boolean negallowed) {
		this.idfield = field;
		this.emptallowed = emptallowed;
		if(negallowed)
			localregex = negativorpositiv;
		else
			localregex = nonegativ;
		field.getDocument().addDocumentListener(this);
		idfield.setToolTipText(notnumber);
		correctness = emptallowed;
	}

	/**
	 * Проверка на ввод числа.
	 */
	@Override
	protected void testInput() {
		String strid = idfield.getText();
		if(strid.length()==0){
			if(emptallowed){
				idfield.setDefault();
				idfield.setToolTipText(notnumber);
				correctness = true;
			} else {
				idfield.setDenied();
				idfield.setToolTipText(notnumber);
				correctness = false;
			}
		} else {
			if(strid.matches(localregex)){
				idfield.setAccepted();
				idfield.setToolTipText(number);
				correctness = true;
			} else {
				idfield.setDenied();
				idfield.setToolTipText(notnumber);
				correctness = false;
			}
		}
		correctlistener.checkCorrectness();
	}
	
	/**Поле ввода номера*/
	private ColoredJTextField idfield = null; 
	/**Флаг разрешения пустого поля*/
	private boolean emptallowed = false;
	/**Выражение, используемое при проверке*/
	private String localregex = null;

}