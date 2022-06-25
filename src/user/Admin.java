package user;

/**
 * A class that extends User and represents a user who is an administrator.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Admin extends User {
	public Admin(String name, String email, String username, String password, int phone) {
		this.setName(name);
		this.setEmail(email);
		this.setType("Admin");
		this.setUsername(username);
		this.setPassword(password);
		this.setPhone(phone);
	}
}

