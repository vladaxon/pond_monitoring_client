package ua.vladaxon.objects;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * �����������, ������� �����, ���������� � ���� ���� ������, ����������� ������ ��� ���������������
 * � ������ ������, ������ ��� ������ �������.
 * @see User
 * @see Pond
 * @see Monitor
 */
public abstract class BasicItem {
	
	/**
	 * ���������� �������� ���������� ����. ���������� ��������� ���� �����
	 * � ����������� �� ������ � �������� ��� ��������.
	 * @param field ����� ����.
	 * @return �������� ����.
	 */
	public abstract Object getValue(int field);
	
	/**
	 * ����� ��������� ���� �������. � ����������� �� ���� �������� ����� ��������� ��� ���.
	 * ���������� ��������� ���� ����� � ����������� �� ������, � �������� ��� ��������.
	 * @param field ����� ����.
	 * @param value ����� ��������.
	 * @return true - ���� ������ ���� ��������. false - ���� ����� �������� ��������� �� ������.
	 */
	public abstract boolean modifyValue(int field, Object value);
	
	/**
	 * ���������� ���� ������.
	 * @return ���� ������.
	 */
	public Flag getFlag() {
		return flag;
	}

	/**
	 * ������������� ���� ������.
	 * @param flag ���� ������.
	 */
	public void setFlag(Flag flag) {
		this.flag = flag;
	}
	
	/**
	 * ���������� �������� ��������.
	 * @param element ������� �� ���������.
	 * @return ��������� �������� ��������.
	 */
	protected static String getString(Element element){
		Text text = (Text) element.getFirstChild();
		return text.getData().trim();
	}
	
	/**���� ������*/
	protected Flag flag = Flag.NORMAL;
	/**��������� ������*/
	public static enum Flag {NORMAL, ADDED, MODIFIED, DELETED};

}