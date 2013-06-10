package ua.vladaxon.ui;

import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ua.vladaxon.manager.AbstractManager;

/**
 * Вкладка таблицы записей
 */
public class ItemTab extends JPanel{

	/**
	 * Конструктор вкладки. Добавляет на панель таблицу и подключает модель.
	 * @param manager Менеджер записей, предоставляющий модель таблицы.
	 */
	public ItemTab(AbstractManager<?> manager){
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		tab = new JTable(manager.getTableModel());
		JScrollPane scroll = new JScrollPane(tab);
		add(scroll);
	}
	
	/**
	 * Обновляет вкладку.
	 */
	public void refreshTab(){
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				tab.revalidate();
				tab.repaint();
			}
		});
	}
	
	private JTable tab;
	private static final long serialVersionUID = 1L;

}