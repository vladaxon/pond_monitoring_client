package ua.vladaxon;

import java.awt.EventQueue;

import ua.vladaxon.manager.ControlManager;
import ua.vladaxon.manager.MonitorManager;
import ua.vladaxon.manager.PondManager;
import ua.vladaxon.ui.ClientUI;
import ua.vladaxon.xml.ServerProxy;

/**
 * Базовый класс клиентского приложения
 */
public class Client {

	public Client() {
		PropManager props = new PropManager();
		ServerProxy server = new ServerProxy(props);
		PondManager pond = new PondManager(server);
		MonitorManager monitor = new MonitorManager(server);
		ControlManager control = new ControlManager(server);
		new ClientUI(pond, monitor, control, server);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Client();
			}
		});
	}

}