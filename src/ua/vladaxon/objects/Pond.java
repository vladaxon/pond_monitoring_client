package ua.vladaxon.objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Объект отражающий сущность записи ставка в БД.
 * Дополнительно имеет поля имени пользователя и телефона
 * для более удобного отображения данных в таблице.
 */
public class Pond extends BasicItem{
	
	/**
	 * Конструктор объекта из элемента документа. Разбирает данные документа для дальнейшей работы
	 * с объектом. При ошибке разбора всех неглавных полей(все, кроме id) значения остаются по умолчанию.
	 * Например если поле имеет значение null - поле объекта инициализируется как "" или 0. Ошибка
	 * генерируется только в случае ошибки преобразования ключевого поля id.
	 * @param item Элемент документа.
	 * @throws Exception При ошибке преобразования ключевого поля.
	 */
	public Pond(Element item) throws Exception{
		NodeList pondchilds = item.getChildNodes();
		id = Integer.parseInt(BasicItem.getString((Element) pondchilds.item(0)));
		for (int i = 1; i < pondchilds.getLength(); i++) {
			String value = BasicItem.getString((Element) pondchilds.item(i));
			try {
				modifyValue(i, value);
			} catch (NullPointerException e) {
			}
		}
		flag = Flag.NORMAL;
	}
	
	/**
	 * Метод для быстрого доступа к номеру ставка.
	 * Нужен для проверки уникальности номера при создании новой записи.
	 * @return Уникальный номер ставка.
	 */
	public int getId(){
		return id;
	}
	
	@Override
	public boolean modifyValue(int field, Object value) {
		boolean modified = false;
		String text = value.toString();
		switch (field) {
		case 1: {
			if (!name.equals(text)) {
				name = text;
				modified = true;
			}
			break;
		}
		case 2: {
			try {
				int area = Integer.parseInt(text);
				if (this.area != area) {
					this.area = area;
					modified = true;
				}
				break;
			} catch (NumberFormatException e) {
				break;
			}
		}
		case 3: {
			if (!settlement.equals(text)) {
				settlement = text;
				modified = true;
			}
			break;
		}
		case 4: {
			int fishspot = Integer.parseInt(text);
			if (this.fishspot != fishspot) {
				this.fishspot = fishspot;
				modified = true;
			}
			break;
		}
		case 5: {
			int fishcost = Integer.parseInt(text);
			if (this.fishcost != fishcost) {
				this.fishcost = fishcost;
				modified = true;
			}
			break;
		}
		case 6: {
			if (!fishes.equals(text)) {
				fishes = text;
				modified = true;
			}
			break;
		}
		case 7: {
			if (!other.equals(text)) {
				other = text;
				modified = true;
			}
			break;
		}
		case 8: {
			if (!uname.equals(text)) {
				uname = text;
				modified = true;
			}
			break;
		}
		case 9: {
			if (!tel.equals(text)) {
				tel = text;
				modified = true;
			}
			break;
		}
		}
		if (modified)
			flag = Flag.MODIFIED;
		return modified;
	}

	@Override
	public Object getValue(int field) {
		switch(field){
		case 0:
			return id;
		case 1:
			return name;
		case 2:
			return area;
		case 3:
			return settlement;
		case 4:
			return fishspot;
		case 5:
			return fishcost;
		case 6:
			return fishes;
		case 7:
			return other;
		case 8:
			return uname;
		case 9:
			return tel;
		default:
			return null;
		}
	}
	
	/**
	 * Метод построения элемента документа из данных объекта.
	 * Инкапсулирует данные в объекте элементе для дальнейшей отправки.
	 * @param doc Объект документа, нужен для создания элементов.
	 * @return Элемент, содержащий данные объекта.
	 */
	public Element getElement(Document doc){
		Element pond = doc.createElement("pond");
		for(int i=0; i<elementheaders.length-2; i++){
			Element field = doc.createElement(elementheaders[i]);
			Text textnode = doc.createTextNode(getValue(i).toString());
			field.appendChild(textnode);
			pond.appendChild(field);
		}
		return pond;
	}
	
	/**
	 * Метод, возвращающий заголовок колонки для поля.
	 * @param column Номер колонки(поля)
	 * @return Строковый заголовок для таблицы.
	 */
	public static String getHeader(int column) {
		return tableheaders[column];
	}

	/**
	 * Метод, возвращающий количество колонок в таблице.
	 * Количество зависит от количества заголовков.
	 * @return Количество столбцов.
	 */
	public static int getColumnCount() {
		return tableheaders.length;
	}
	
	/**
	 * Метод, возвращающий флаг возможности редактирования данного поля.
	 * @param column Номер столбца.
	 * @return true - если разрешено редактирование.
	 */
	public static boolean isEditable(int column){
		return editable[column];
	}
	
	@Override
	public String toString() {
		return "Pond [id=" + id + ", name=" + name + ", area=" + area
				+ ", settlement=" + settlement + ", fishspot=" + fishspot
				+ ", fishcost=" + fishcost + ", fishes=" + fishes + ", other="
				+ other + ", uname=" + uname + ", tel=" + tel + "]";
	}

	/**Уникальный номер*/
	private int id = 0;
	/**Имя ставка*/
	private String name = "";
	/**Площадь ставка в метрах*/
	private int area = 0;
	/**Название ближайшего населенного пункта*/
	private String settlement = "";
	/**Количество мест для рыбалки*/
	private int fishspot = 0;
	/**Стоимость рыбалки в час*/
	private int fishcost = 0;
	/**Виды рыб в ставке*/
	private String fishes = "";
	/**Другая информация*/
	private String other = "";
	/**Привязанный пользователь*/
	private String uname = "";
	/**Телефон пользователя*/
	private String tel = "";
	/**Массив заголовков таблицы*/
	private static final String[] tableheaders = 
		{"ID","Имя","Площадь","Нас. Пункт","Мест рыбалки",
		"Стоимость/час","Виды рыб","Прочее","Контакты","Телефон"};
	/**Массив заголовков элементов*/
	private static final String[] elementheaders = 
		{"pid","pname","parea","psettl","fishspot","fishcost","fishes","other","uname","utel"};
	/**Массив флагов редактирования*/
	private static final boolean[] editable = {false,true,true,true,true,true,true,true,false,false};

}