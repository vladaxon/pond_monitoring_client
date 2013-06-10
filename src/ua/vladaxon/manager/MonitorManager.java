package ua.vladaxon.manager;

import ua.vladaxon.objects.Monitor;
import ua.vladaxon.xml.ServerProxy;

/**
 * Менеджер записей мониторинга.
 */
public class MonitorManager extends AbstractManager<Monitor>{
	
	public MonitorManager(ServerProxy server){
		super(server);
	}
	
	@Override
	public int getColumnCount() {
		return Monitor.getColumnCount();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return Monitor.getHeader(columnIndex);
	}
	
	@Override
	public void tabSelected() {
		server.getAllMonitors(this);
	}

}