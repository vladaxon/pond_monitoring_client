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
 * Класс менеджера управления ставком.
 * Предоставляет интерфейс для редактирования ставка, привязаного к пользователю
 * и добавления записей мониторинга.
 */
public class ControlManager implements TableModel{
	
	public ControlManager(ServerProxy server){
		this.server = server;
	}

	/**
	 * Вызывается при выборе вкладки, за котороую отвечает менеджер.
	 * Начинается процедура авторизации для загрузки записей пользователя.
	 */
	public void tabSelected() {
		monitors=null;
		tab.clearTab();
		authorization();
	}
	
	/**
	 * Получение данных пользователя от сервера.
	 * Данные инкапсулированы в объекте {@link ControlData} для удобства.
	 * @param data Данные пользователя.
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
	 * Оповещает менеджер об ответе сервера после обновления данных.
	 * Показывает всплывающее окно, в зависимости от ответа и перезагружает данные.
	 * @param checkResponseState true - если сервер принял изменения.
	 */
	public void updateExecuted(boolean checkResponseState) {
		if(checkResponseState){
			JOptionPane.showMessageDialog(owner, "Сохранено!", "Сервер", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(owner, "Ошибка!", "Сервер", JOptionPane.WARNING_MESSAGE);
		}
		server.getControlData(this, user);
	}
	
	/**
	 * Возвращает объект вкладку данного менеджера.
	 * @return Вкладка менеджера
	 */
	public ControlTab getControlTab() {
		return tab;
	}
	
	/**
	 * Возвращает модель таблицы.
	 * @return Модель таблицы.
	 */
	public TableModel getTableModel() {
		return this;
	}
	
	/**
	 * Возвращает объект ставка для заполнения полей в интерфейсе.
	 * @return Объект ставка пользователя.
	 */
	public Pond getPond() {
		return pond;
	}
	
	/**
	 * Возвращает номер пользователя для создания записей мониторинга.
	 * @return ID пользователя.
	 */
	public int getClientID() {
		return userid;
	}
	
	/**
	 * Получает объект окна и создает необходимые диалоги.
	 * @param owner Фрейм-владелец
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
	 * Метод авторизации. Отображает диалог ввода логина и пароля, затем отправляет запрос
	 * на получение данных серверу.
	 */
	private void authorization(){
		if(dialog.showDialog()){
			user = dialog.getUserData();
			server.getControlData(this, user);
		}
	}
	
	/**
	 * Сохраняет данные клиента.
	 * Изменяет поля ставка и отделяет добавленные записи мониторинга.
	 * Затем отправляет данные на сервер.
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
	 * Создает новую запись мониторинга.
	 */
	private void addmonitoring() {
		if(newmonitor.showDialog()){
			monitors.add(newmonitor.getItem());
			tab.refreshTab();
		}
	}

	/**
	 * Возвращает слушатель кнопки сохранения.
	 * @return Слушатель кнопки сохранения.
	 */
	public ActionListener getSavelistener() {
		return savelistener;
	}

	/**
	 * Возвращает слушатель кнопки добавления записи.
	 * @return Слушатель кнопки добавления записи.
	 */
	public ActionListener getAddlistener() {
		return addlistener;
	}

	/**
	 * Объект слушателя кнопки сохранения. Вызывает метод сохранения менеджера.
	 */
	private ActionListener savelistener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			save();
		}
	};
	
	/**
	 * Объект слушателя кнопки добавления записей. Вызывает метод добавления записей.
	 */
	private ActionListener addlistener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			addmonitoring();
		}
	};
	
	/**Представитель сервера*/
	private ServerProxy server;
	/**Объект ставка пользователя*/
	private Pond pond;
	/**Список записей мониторинга пользователя*/
	private List<Monitor> monitors;
	/**Данные пользователя*/
	private UserData user;
	/**Номер пользователя*/
	private int userid;
	/**Вкладка менеджера*/
	protected ControlTab tab = new ControlTab(this);
	/**Диалог авторизации*/
	private AuthorizationDialog dialog;
	/**Диалог новой записи мониторинга*/
	private NewMonitoringData newmonitor;
	/**Главный фрейм программы*/
	private JFrame owner;
	
}