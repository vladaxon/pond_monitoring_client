package ua.vladaxon.objects;

/**
 * ����� ��������������� � ���� ������ ������������.
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

	/**����� ������������*/
	private String username;
	/**��� ������(�������� ����������������)*/
	private int password;

}