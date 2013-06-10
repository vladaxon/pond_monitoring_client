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
 * Базовый класс менеджеров записей.
 * Обрабатывает запросы модели таблицы и содержит вкладку интерфейса
 * @param <T> тип записей: {@link Pond} или {@link Monitor}
 */
public abstract class AbstractManager<T extends BasicItem> {
	
	public AbstractManager(ServerProxy server) {
		this.server = server;
	}
	
	/**
	 * Возвращает объект вкладки для пользовательского интерфейса.
	 * @return Вкладка.
	 */
	public ItemTab getItemTab(){
		return tab;
	}

	public abstract int getColumnCount();

	public abstract String getColumnName(int columnIndex);

	/**
	 * Возвращает количество строк.
	 * @return Количество строк.
	 */
	public int getRowCount() {
		if(items!=null){
			return items.size();
		} else {
			return 0;
		}
	}
	
	/**
	 * Возвращает значение поля.
	 * @param rowIndex Индекс строки
	 * @param columnIndex Индекс столбца
	 * @return Поле по указанным индексам поля и строки
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(items!=null){
			return items.get(rowIndex).getValue(columnIndex).toString();
		} else {
			return null;
		}
	}
	
	/**
	 * Возвращает объект модели таблицы
	 * @return Модель таблицы
	 */
	public TableModel getTableModel(){
		return tablemodel;
	}
	
	/**
	 * Вызывается обработчиком событий при выборе вкладки.
	 */
	public abstract void tabSelected();
	
	/**
	 * Устанавливает список записей и обновляет таблицу.
	 * @param list
	 */
	public void setList(List<T> list){
		items = list;
		tab.refreshTab();
	}
	
	/**Список записей*/
	protected List<T> items;
	/**Представитель сервера для получения данных*/
	protected ServerProxy server;
	/**Модель таблицы*/
	protected TableModel tablemodel = new ItemTableModel(this);
	/**Вкладка с записями для обновления таблицы*/
	protected ItemTab tab = new ItemTab(this);

}