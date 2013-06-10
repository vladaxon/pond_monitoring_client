package ua.vladaxon.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * ������, ��������������� � ���� ������, ���������� �� �������.
 */
public class ControlData {
	
	/**
	 * ����������� �������. ��������� ���������� ������ �� id.
	 * @param id ����� ������������.
	 * @param ponds �������� ������ �������.
	 * @param monitors �������� ������ �����������.
	 */
	public ControlData(int id, List<Pond> ponds, List<Monitor> monitors){
		this.id = id;
		for(Pond p: ponds){
			if(p.getId()==id){
				pond = p;
				break;
			}
		}
		for(Monitor m: monitors){
			if(m.getPondId()==id){
				list.add(m);
			}
		}
	}
	
	public int getId() {
		return id;
	}
	
	public Pond getPond() {
		return pond;
	}
	
	public List<Monitor> getList() {
		return list;
	}
	
	/**����� ������������*/
	private int id;
	/**������, ���������� �� �������������*/
	private Pond pond;
	/**������ ������� �����������*/
	private List<Monitor> list = new ArrayList<Monitor>();

}