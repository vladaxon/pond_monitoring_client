package ua.vladaxon.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ua.vladaxon.xml.ServerProxy;

/**
 * —лушатель закрыти€ окна, предназначен дл€ уведомлени€
 * представител€ сервера о закрытии клиента. ѕредставитель
 * должен корректно завершить св€зь с сервером, после чего
 * приложение закроетс€.
 */
public class WindowListener extends WindowAdapter{
	
	public WindowListener(ServerProxy server){
		this.server = server;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		super.windowClosing(e);
		server.disconnect();
		System.exit(0);
	}
	
	/**ѕредставитель сервера*/
	private ServerProxy server;

}