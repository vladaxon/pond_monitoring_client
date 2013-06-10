package ua.vladaxon.objects;

import java.sql.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Объект мониторинга ставка. Имеет поля номера ставка, дату показания, температуру ставка,
 * уровень воды. Поля записи невозможно редактировать.
 */
public class Monitor extends BasicItem{
	
	/**
	 * Конструктор по умолчанию.
	 * @param pnum Номер ставка
	 * @param date Дата замера
	 * @param temp Температура воды
	 * @param level Уровень воды
	 * @param flag Состояние объекта
	 */
	public Monitor(int pnum, Date date, int temp, int level, Flag flag) {
		this.pnum = pnum;
		this.date = date;
		this.temp = temp;
		this.level = level;
		this.flag = flag;
	}
	
	/**
	 * Конструктор из элемента документа.
	 * @param item Элемент документа, отображающий этот объект.
	 * @throws Exception - при ошибке преобразования.
	 */
	public Monitor(Element item) throws Exception{
		NodeList monitorchilds = item.getChildNodes();
		pnum = Integer.parseInt(BasicItem.getString((Element) monitorchilds.item(0)));
		date = new Date(Long.parseLong(BasicItem.getString((Element) monitorchilds.item(1))));
		temp = Integer.parseInt(BasicItem.getString((Element) monitorchilds.item(2)));
		level = Integer.parseInt(BasicItem.getString((Element) monitorchilds.item(3)));
		flag = Flag.NORMAL;
	}
	
	/**
	 * Возвращает данные объекта в виде элемента документа.
	 * @param doc Объект документа, для создания элементов.
	 * @return Элемент документа с данными объекта.
	 */
	public Element getElement(Document doc){
		Element monitor = doc.createElement("monitor");
		Element pnumel = formField(0, pnum+"", doc);
		monitor.appendChild(pnumel);
		Element dateel = formField(1, date.getTime()+"", doc);
		monitor.appendChild(dateel);
		Element tempel = formField(2, temp+"", doc);
		monitor.appendChild(tempel);
		Element levelel = formField(3, level+"", doc);
		monitor.appendChild(levelel);
		return monitor;
	}
	
	/**
	 * Формирует элемент из указанных данных.
	 * @param fieldnum Номер поля для получения имени тега
	 * @param data Данные
	 * @param doc Документ для создания элемента
	 * @return Элемент документа
	 */
	private Element formField(int fieldnum, String data, Document doc){
		Element field = doc.createElement(elementheaders[fieldnum]);
		Text textnode = doc.createTextNode(data);
		field.appendChild(textnode);
		return field;
	}
	
	@Override
	public boolean modifyValue(int field, Object value) {
		return false;
	}

	@Override
	public Object getValue(int field) {
		switch(field){
		case 0:
			return pnum;
		case 1:
			return date;
		case 2:
			return temp;
		case 3:
			return level;
		default:
			return null;
		}
	}
	
	public int getPondId(){
		return pnum;
	}
	
	/**
	 * Метод, возвращающий заголовок колонки для поля.
	 * @param column Номер колонки(поля)
	 * @return Строковый заголовок для таблицы.
	 */
	public static String getHeader(int column) {
		return headers[column];
	}
	
	/**
	 * Метод, возвращающий количество колонок в таблице.
	 * Количество зависит от количества заголовков.
	 * @return Количество столбцов.
	 */
	public static int getColumnCount() {
		return headers.length;
	}
	
	@Override
	public String toString() {
		return "Monitor [pnum=" + pnum + ", date=" + date + ", temp=" + temp
				+ ", level=" + level + ", flag=" + flag + "]";
	}
	
	/**Номер ставка*/
	private int pnum = 0;
	/**Дата показания*/
	private Date date = null;
	/**Температура воды*/
	private int temp = 0;
	/**Уровень воды*/
	private int level = 0;
	/**Массив заголовков для таблицы*/
	private static final String[] headers = {"Ставок","Дата","t воды","Уровень"};
	/**Массив заголовков элементов*/
	private static final String[] elementheaders = {"pid","mdate","mtemp","mlevel"};
	/**Имя таблицы мониторинга*/
	public static final String monitortablename = "monitor";
	/**Имя поля номера ставка к которому привязаны данные измерения*/
	public static final String mntrpondincolname = "pnum";
	/**Имя поля дать съема показаний*/
	public static final String mntrdatecolname = "mdate";
	/**Имя поля температуры воды в ставке*/
	public static final String mntrtempcolname = "mtemp";
	/**Имя поля уровня воды ставка*/
	public static final String mntrlevelcolname = "wlevel";
	/**Имя поля номера записи*/
	public static final String monitornum = "mnum";
	
}