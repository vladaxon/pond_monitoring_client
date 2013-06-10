package ua.vladaxon;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import ua.vladaxon.manager.AbstractManager;

/**
 * Модель таблицы, связанная с менеджером записей.
 * Делегирует основные методы получения данных.
 */
public class ItemTableModel implements TableModel{
	
	public ItemTableModel(AbstractManager<?> manager){
		this.manager = manager;
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return manager.getColumnCount();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return manager.getColumnName(columnIndex);
	}

	@Override
	public int getRowCount() {
		return manager.getRowCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return manager.getValueAt(rowIndex, columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {		
	}
	
	/**Менеджер записей*/
	private AbstractManager<?> manager;

}