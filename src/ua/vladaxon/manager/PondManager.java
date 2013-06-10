package ua.vladaxon.manager;

import ua.vladaxon.objects.Pond;
import ua.vladaxon.xml.ServerProxy;

/**
 * Менеджер записей ставков.
 */
public class PondManager extends AbstractManager<Pond>{

	public PondManager(ServerProxy server) {
		super(server);
	}

	@Override
	public int getColumnCount() {
		return Pond.getColumnCount();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return Pond.getHeader(columnIndex);
	}
	
	@Override
	public void tabSelected() {
		server.getAllPonds(this);
	}

}