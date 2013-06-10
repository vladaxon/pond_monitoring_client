package ua.vladaxon;

import javax.swing.JTextField;

import ua.vladaxon.CorrectFieldListener;
import ua.vladaxon.ui.ColoredJPasswordField;
import ua.vladaxon.ui.ColoredJTextField;
import ua.vladaxon.ui.ColoredTextField;

/**
 * Класс, проверяющий правильность ввода текста в поле.
 * Текст в поле может содержать латинские буквы, цифры, символ подчеркивания.
 * @see CorrectFieldListener
 */
public class UsrAuthDocListener extends CorrectFieldListener{
	
	public UsrAuthDocListener(ColoredTextField field){
		this.field = field;
		((JTextField)field).getDocument().addDocumentListener(this);
	}

	@Override
	protected void testInput() {
		String text = null;
		if(field instanceof ColoredJPasswordField)
			text = new String(((ColoredJPasswordField) field).getPassword());
		else
			text = ((ColoredJTextField) field).getText();
		if(text.length()==0){
			field.setDefault();
			correctness = false;
		} else {
			if (text.matches(wordsymb)) {
				field.setDefault();
				correctness = true;
				((JTextField) field).setToolTipText(null);
			} else {
				field.setDenied();
				correctness = false;
				((JTextField) field).setToolTipText(notwordsymbols);
			}
		}
		correctlistener.checkCorrectness();
	}
	
	/**Текстовое поле, к которому привязан слушатель, необходим для смены цвета*/
	private ColoredTextField field = null;

}