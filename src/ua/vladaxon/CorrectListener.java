package ua.vladaxon;

import javax.swing.JButton;

/**
 * Класс, объект которого следит за общей корректностью ввода в форму.
 * Отключает или включает подтверждающую кнопку, в зависимости от правильности заполнения формы.
 */
public class CorrectListener {
	
	/**
	 * Конструктор объекта, проверяющего общую корректность ввода.
	 * Присваивает параметры приватным переменным и регистрирует 
	 * себя во всех слушателях полей для возможности дальнейшего вызова методов.
	 * @param donebutton Кнопка подтверждения.
	 * @param listeners Массив слушателей полей.
	 */
	public CorrectListener(JButton donebutton, CorrectFieldListener... listeners){
		this.donebutton = donebutton;
		this.listeners = listeners;
		for(CorrectFieldListener cfl: listeners)
			cfl.setCorectListener(this);
	}
	
	/**
	 * Метод проверки общей корректности.
	 * Проверяет флаги корректности всех зарегестрированых слушателей и включает или выключает
	 * кнопку подтверждения. Метод вызывается из слушателей полей.
	 */
	public void checkCorrectness(){
		boolean total = true;
		for(CorrectFieldListener cfl: listeners)
			total = total && cfl.correctness;
		donebutton.setEnabled(total);
	}
	
	/**Кнопка подтверждения ввода*/
	private JButton donebutton = null;
	/**Массив слушателей полей, которые будут вызывать метод проверки*/
	private CorrectFieldListener[] listeners = null;

}