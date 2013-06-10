package ua.vladaxon.ui;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ua.vladaxon.Client;
import ua.vladaxon.manager.ControlManager;
import ua.vladaxon.manager.MonitorManager;
import ua.vladaxon.manager.PondManager;
import ua.vladaxon.xml.ServerProxy;

/**
 * Основной класс интерфейса севрера
 */
public class ClientUI implements ChangeListener{
	
	public ClientUI(PondManager pond, MonitorManager monitor, ControlManager control, ServerProxy server){
		this.pond = pond;
		this.monitor = monitor;
		this.control = control;
		ItemTab pondtab = pond.getItemTab();
		ItemTab montab = monitor.getItemTab();
		ControlTab conttab = control.getControlTab();
		JTabbedPane tabbetpane = new JTabbedPane();
		tabbetpane.addChangeListener(this);
		tabbetpane.addTab(pondtitle, pondicon, pondtab);
		tabbetpane.addTab(montitle, monicon, montab);
		tabbetpane.addTab(controltitle, controlicon, conttab);
		MainFrame main = new MainFrame(tabbetpane, new WindowListener(server));
		control.setMainFrame(main);
		main.setVisible(true);
	}
	
	/**
	 * Уведомляет менеджер о выборе его вкладки.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		int tabindex = ((JTabbedPane)e.getSource()).getSelectedIndex();
		switch (tabindex) {
		case 0:{
			pond.tabSelected();
			break;
		}
		case 1:{
			monitor.tabSelected();
			break;
		}
		case 2:{
			control.tabSelected();
			break;
		}
		default:
			break;
		}
	}
	
	/**Менеджер записей ставков*/
	private PondManager pond;
	/**Менеджер записей мониторинга*/
	private MonitorManager monitor;
	/**Менеджер контроля*/
	private ControlManager control;
	/**Иконка вкладки*/
	private static ImageIcon pondicon = 
			new ImageIcon(Client.class.getResource("/ua/vladaxon/res/lake.png"));
	/**Заголовок вкладки*/
	private static String pondtitle = "Ставки";
	/**Иконка вкладки*/
	public static final ImageIcon monicon = 
			new ImageIcon(Client.class.getResource("/ua/vladaxon/res/monitor.png"));
	/**Заголовок вкладки*/
	private static String montitle = "Мониторинг";
	/**Иконка вкладки*/
	private static ImageIcon controlicon = 
			new ImageIcon(Client.class.getResource("/ua/vladaxon/res/control.png"));
	/**Заголовок вкладки*/
	private static String controltitle = "Управление";
	/**Метка кнопки подтверждения*/
	public static final String oklabel = "Готово";
	/**Иконка подтверждения*/
	public static final ImageIcon okicon = 
			new ImageIcon(Client.class.getResource("/ua/vladaxon/res/ok.png"));
	/**Иконка добавления*/
	public static final ImageIcon addicon = 
			new ImageIcon(Client.class.getResource("/ua/vladaxon/res/add.png"));
	/**Метка кнопки отмены*/
	public static final String cancellabel = "Отмена";
	/**Иконка отмены*/
	public static final ImageIcon cancelicon = 
			new ImageIcon(Client.class.getResource("/ua/vladaxon/res/cancel.png"));

}