package base;
public class User {
	private String userName;
	private String Password;
	
	public User (String userName, String Password) {
		this.userName = userName;
		this.Password = Password;
	}

	public String getUserName() {
		return userName;
	}
	
	public String getPassword() {
		return Password;
	}

	@Override
	public String toString() {
		return "User [userName=" + userName + ", Password=" + Password + "]";
	}

	public void setUserName(String name) {
		this.userName = name;
	}
	
	
}