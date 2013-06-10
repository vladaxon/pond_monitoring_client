package ua.vladaxon.objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * ������ ���������� �������� ������ ������ � ��.
 * ������������� ����� ���� ����� ������������ � ��������
 * ��� ����� �������� ����������� ������ � �������.
 */
public class Pond extends BasicItem{
	
	/**
	 * ����������� ������� �� �������� ���������. ��������� ������ ��������� ��� ���������� ������
	 * � ��������. ��� ������ ������� ���� ��������� �����(���, ����� id) �������� �������� �� ���������.
	 * �������� ���� ���� ����� �������� null - ���� ������� ���������������� ��� "" ��� 0. ������
	 * ������������ ������ � ������ ������ �������������� ��������� ���� id.
	 * @param item ������� ���������.
	 * @throws Exception ��� ������ �������������� ��������� ����.
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
	 * ����� ��� �������� ������� � ������ ������.
	 * ����� ��� �������� ������������ ������ ��� �������� ����� ������.
	 * @return ���������� ����� ������.
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
	 * ����� ���������� �������� ��������� �� ������ �������.
	 * ������������� ������ � ������� �������� ��� ���������� ��������.
	 * @param doc ������ ���������, ����� ��� �������� ���������.
	 * @return �������, ���������� ������ �������.
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
	 * �����, ������������ ��������� ������� ��� ����.
	 * @param column ����� �������(����)
	 * @return ��������� ��������� ��� �������.
	 */
	public static String getHeader(int column) {
		return tableheaders[column];
	}

	/**
	 * �����, ������������ ���������� ������� � �������.
	 * ���������� ������� �� ���������� ����������.
	 * @return ���������� ��������.
	 */
	public static int getColumnCount() {
		return tableheaders.length;
	}
	
	/**
	 * �����, ������������ ���� ����������� �������������� ������� ����.
	 * @param column ����� �������.
	 * @return true - ���� ��������� ��������������.
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

	/**���������� �����*/
	private int id = 0;
	/**��� ������*/
	private String name = "";
	/**������� ������ � ������*/
	private int area = 0;
	/**�������� ���������� ����������� ������*/
	private String settlement = "";
	/**���������� ���� ��� �������*/
	private int fishspot = 0;
	/**��������� ������� � ���*/
	private int fishcost = 0;
	/**���� ��� � ������*/
	private String fishes = "";
	/**������ ����������*/
	private String other = "";
	/**����������� ������������*/
	private String uname = "";
	/**������� ������������*/
	private String tel = "";
	/**������ ���������� �������*/
	private static final String[] tableheaders = 
		{"ID","���","�������","���. �����","���� �������",
		"���������/���","���� ���","������","��������","�������"};
	/**������ ���������� ���������*/
	private static final String[] elementheaders = 
		{"pid","pname","parea","psettl","fishspot","fishcost","fishes","other","uname","utel"};
	/**������ ������ ��������������*/
	private static final boolean[] editable = {false,true,true,true,true,true,true,true,false,false};

}