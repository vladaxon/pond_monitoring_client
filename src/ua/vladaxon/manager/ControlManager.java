package ua.vladaxon.manager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import ua.vladaxon.objects.BasicItem.Flag;
import ua.vladaxon.objects.ControlData;
import ua.vladaxon.objects.Monitor;
import ua.vladaxon.objects.Pond;
import ua.vladaxon.objects.UserData;
import ua.vladaxon.ui.AuthorizationDialog;
import ua.vladaxon.ui.ControlTab;
import ua.vladaxon.ui.NewMonitoringData;
import ua.vladaxon.xml.ServerProxy;

/**
 * ����� ��������� ���������� �������.
 * ������������� ��������� ��� �������������� ������, ����������� � ������������
 * � ���������� ������� �����������.
 */
public class ControlManager implements TableModel{
	
	public ControlManager(ServerProxy server){
		this.server = server;
	}

	/**
	 * ���������� ��� ������ �������, �� �������� �������� ��������.
	 * ���������� ��������� ����������� ��� �������� ������� ������������.
	 */
	public void tabSelected() {
		monitors=null;
		tab.clearTab();
		authorization();
	}
	
	/**
	 * ��������� ������ ������������ �� �������.
	 * ������ ��������������� � ������� {@link ControlData} ��� ��������.
	 * @param data ������ ������������.
	 */
	public void setControlData(ControlData data) {
		if(data!=null){
			pond = data.getPond();
			monitors = data.getList();
			userid = data.getId();
		}
		tab.refreshTab();
	}
	
	/**
	 * ��������� �������� �� ������ ������� ����� ���������� ������.
	 * ���������� ����������� ����, � ����������� �� ������ � ������������� ������.
	 * @param checkResponseState true - ���� ������ ������ ���������.
	 */
	public void updateExecuted(boolean checkResponseState) {
		if(checkResponseState){
			JOptionPane.showMessageDialog(owner, "���������!", "������", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(owner, "������!", "������", JOptionPane.WARNING_MESSAGE);
		}
		server.getControlData(this, user);
	}
	
	/**
	 * ���������� ������ ������� ������� ���������.
	 * @return ������� ���������
	 */
	public ControlTab getControlTab() {
		return tab;
	}
	
	/**
	 * ���������� ������ �������.
	 * @return ������ �������.
	 */
	public TableModel getTableModel() {
		return this;
	}
	
	/**
	 * ���������� ������ ������ ��� ���������� ����� � ����������.
	 * @return ������ ������ ������������.
	 */
	public Pond getPond() {
		return pond;
	}
	
	/**
	 * ���������� ����� ������������ ��� �������� ������� �����������.
	 * @return ID ������������.
	 */
	public int getClientID() {
		return userid;
	}
	
	/**
	 * �������� ������ ���� � ������� ����������� �������.
	 * @param owner �����-��������
	 */
	public void setMainFrame(JFrame owner){
		this.owner = owner;
		dialog = new AuthorizationDialog(owner);
		newmonitor = new NewMonitoringData(owner, this);
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
		return Monitor.getColumnCount();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return Monitor.getHeader(columnIndex);
	}

	@Override
	public int getRowCount() {
		if(monitors!=null){
			return monitors.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(monitors!=null){
			return monitors.get(rowIndex).getValue(columnIndex);
		} else {
			return null;
		}
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
	
	/**
	 * ����� �����������. ���������� ������ ����� ������ � ������, ����� ���������� ������
	 * �� ��������� ������ �������.
	 */
	private void authorization(){
		if(dialog.showDialog()){
			user = dialog.getUserData();
			server.getControlData(this, user);
		}
	}
	
	/**
	 * ��������� ������ �������.
	 * �������� ���� ������ � �������� ����������� ������ �����������.
	 * ����� ���������� ������ �� ������.
	 */
	private void save() {
		for(int i=1; i<Pond.getColumnCount()-2; i++){
			pond.modifyValue(i, tab.getFieldText(i));
		}
		List<Monitor> addedmonitor = new ArrayList<Monitor>();
		for(Monitor m: monitors){
			if(m.getFlag()==Flag.ADDED){
				addedmonitor.add(m);
			}
		}
		server.executeUpdate(this, user, pond, addedmonitor);
	}

	/**
	 * ������� ����� ������ �����������.
	 */
	private void addmonitoring() {
		if(newmonitor.showDialog()){
			monitors.add(newmonitor.getItem());
			tab.refreshTab();
		}
	}

	/**
	 * ���������� ��������� ������ ����������.
	 * @return ��������� ������ ����������.
	 */
	public ActionListener getSavelistener() {
		return savelistener;
	}

	/**
	 * ���������� ��������� ������ ���������� ������.
	 * @return ��������� ������ ���������� ������.
	 */
	public ActionListener getAddlistener() {
		return addlistener;
	}

	/**
	 * ������ ��������� ������ ����������. �������� ����� ���������� ���������.
	 */
	private ActionListener savelistener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			save();
		}
	};
	
	/**
	 * ������ ��������� ������ ���������� �������. �������� ����� ���������� �������.
	 */
	private ActionListener addlistener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			addmonitoring();
		}
	};
	
	/**������������� �������*/
	private ServerProxy server;
	/**������ ������ ������������*/
	private Pond pond;
	/**������ ������� ����������� ������������*/
	private List<Monitor> monitors;
	/**������ ������������*/
	private UserData user;
	/**����� ������������*/
	private int userid;
	/**������� ���������*/
	protected ControlTab tab = new ControlTab(this);
	/**������ �����������*/
	private AuthorizationDialog dialog;
	/**������ ����� ������ �����������*/
	private NewMonitoringData newmonitor;
	/**������� ����� ���������*/
	private JFrame owner;
	
}