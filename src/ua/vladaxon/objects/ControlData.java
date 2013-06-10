package ua.vladaxon.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Объект, инкапсулирующий в себе данные, полученные от сервера.
 */
public class ControlData {
	
	/**
	 * Конструктор объекта. Фильтрует полученные записи по id.
	 * @param id Номер пользователя.
	 * @param ponds Исходный список ставков.
	 * @param monitors Исходный список мониторинга.
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
	
	/**Номер пользователя*/
	private int id;
	/**Ставок, привязаный за пользователем*/
	private Pond pond;
	/**Список записей мониторинга*/
	private List<Monitor> list = new ArrayList<Monitor>();

}