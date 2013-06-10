package ua.vladaxon.objects;

/**
 * Класс инкапсулирующий в себе данные пользователя.
 */
public class UserData {
	
	public UserData(String username, int password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "UserData [username=" + username + ", password=" + password + "]";
	}

	/**Логин пользователя*/
	private String username;
	/**Хэш пароля(имитация закодированности)*/
	private int password;

}