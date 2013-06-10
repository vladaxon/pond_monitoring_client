package ua.vladaxon.objects;

import java.sql.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * ������ ����������� ������. ����� ���� ������ ������, ���� ���������, ����������� ������,
 * ������� ����. ���� ������ ���������� �������������.
 */
public class Monitor extends BasicItem{
	
	/**
	 * ����������� �� ���������.
	 * @param pnum ����� ������
	 * @param date ���� ������
	 * @param temp ����������� ����
	 * @param level ������� ����
	 * @param flag ��������� �������
	 */
	public Monitor(int pnum, Date date, int temp, int level, Flag flag) {
		this.pnum = pnum;
		this.date = date;
		this.temp = temp;
		this.level = level;
		this.flag = flag;
	}
	
	/**
	 * ����������� �� �������� ���������.
	 * @param item ������� ���������, ������������ ���� ������.
	 * @throws Exception - ��� ������ ��������������.
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
	 * ���������� ������ ������� � ���� �������� ���������.
	 * @param doc ������ ���������, ��� �������� ���������.
	 * @return ������� ��������� � ������� �������.
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
	 * ��������� ������� �� ��������� ������.
	 * @param fieldnum ����� ���� ��� ��������� ����� ����
	 * @param data ������
	 * @param doc �������� ��� �������� ��������
	 * @return ������� ���������
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
	 * �����, ������������ ��������� ������� ��� ����.
	 * @param column ����� �������(����)
	 * @return ��������� ��������� ��� �������.
	 */
	public static String getHeader(int column) {
		return headers[column];
	}
	
	/**
	 * �����, ������������ ���������� ������� � �������.
	 * ���������� ������� �� ���������� ����������.
	 * @return ���������� ��������.
	 */
	public static int getColumnCount() {
		return headers.length;
	}
	
	@Override
	public String toString() {
		return "Monitor [pnum=" + pnum + ", date=" + date + ", temp=" + temp
				+ ", level=" + level + ", flag=" + flag + "]";
	}
	
	/**����� ������*/
	private int pnum = 0;
	/**���� ���������*/
	private Date date = null;
	/**����������� ����*/
	private int temp = 0;
	/**������� ����*/
	private int level = 0;
	/**������ ���������� ��� �������*/
	private static final String[] headers = {"������","����","t ����","�������"};
	/**������ ���������� ���������*/
	private static final String[] elementheaders = {"pid","mdate","mtemp","mlevel"};
	/**��� ������� �����������*/
	public static final String monitortablename = "monitor";
	/**��� ���� ������ ������ � �������� ��������� ������ ���������*/
	public static final String mntrpondincolname = "pnum";
	/**��� ���� ���� ����� ���������*/
	public static final String mntrdatecolname = "mdate";
	/**��� ���� ����������� ���� � ������*/
	public static final String mntrtempcolname = "mtemp";
	/**��� ���� ������ ���� ������*/
	public static final String mntrlevelcolname = "wlevel";
	/**��� ���� ������ ������*/
	public static final String monitornum = "mnum";
	
}