package ua.vladaxon.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ua.vladaxon.xml.ServerProxy;

/**
 * ��������� �������� ����, ������������ ��� �����������
 * ������������� ������� � �������� �������. �������������
 * ������ ��������� ��������� ����� � ��������, ����� ����
 * ���������� ���������.
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
	
	/**������������� �������*/
	private ServerProxy server;

}