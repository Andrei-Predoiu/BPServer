package server.model;

public class Auth {
	private String username;
	private String password;
	private String hash;
	private String salt;

	public Auth(String username, String password) {

		this.username = username;
		this.password = password;
	}

	public Auth(String username, String salt, String password) {

		this.username = username;
		this.password = password;
		this.salt = salt;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
