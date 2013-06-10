package ua.vladaxon.manager;

import java.util.List;

import javax.swing.table.TableModel;

import ua.vladaxon.ItemTableModel;
import ua.vladaxon.objects.BasicItem;
import ua.vladaxon.objects.Monitor;
import ua.vladaxon.objects.Pond;
import ua.vladaxon.ui.ItemTab;
import ua.vladaxon.xml.ServerProxy;

/**
 * ������� ����� ���������� �������.
 * ������������ ������� ������ ������� � �������� ������� ����������
 * @param <T> ��� �������: {@link Pond} ��� {@link Monitor}
 */
public abstract class AbstractManager<T extends BasicItem> {
	
	public AbstractManager(ServerProxy server) {
		this.server = server;
	}
	
	/**
	 * ���������� ������ ������� ��� ����������������� ����������.
	 * @return �������.
	 */
	public ItemTab getItemTab(){
		return tab;
	}

	public abstract int getColumnCount();

	public abstract String getColumnName(int columnIndex);

	/**
	 * ���������� ���������� �����.
	 * @return ���������� �����.
	 */
	public int getRowCount() {
		if(items!=null){
			return items.size();
		} else {
			return 0;
		}
	}
	
	/**
	 * ���������� �������� ����.
	 * @param rowIndex ������ ������
	 * @param columnIndex ������ �������
	 * @return ���� �� ��������� �������� ���� � ������
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(items!=null){
			return items.get(rowIndex).getValue(columnIndex).toString();
		} else {
			return null;
		}
	}
	
	/**
	 * ���������� ������ ������ �������
	 * @return ������ �������
	 */
	public TableModel getTableModel(){
		return tablemodel;
	}
	
	/**
	 * ���������� ������������ ������� ��� ������ �������.
	 */
	public abstract void tabSelected();
	
	/**
	 * ������������� ������ ������� � ��������� �������.
	 * @param list
	 */
	public void setList(List<T> list){
		items = list;
		tab.refreshTab();
	}
	
	/**������ �������*/
	protected List<T> items;
	/**������������� ������� ��� ��������� ������*/
	protected ServerProxy server;
	/**������ �������*/
	protected TableModel tablemodel = new ItemTableModel(this);
	/**������� � �������� ��� ���������� �������*/
	protected ItemTab tab = new ItemTab(this);

}