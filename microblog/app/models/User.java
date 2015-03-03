package models;

import java.util.List;

import javax.persistence.*;

import play.data.validation.Constraints.*;
import play.db.ebean.Model;
import helpers.*;

@Entity
public class User extends Model {

	@Id
	public long id;

	@Email
	public String email;

	@MinLength(6)
	public String password;
	
	@OneToMany(mappedBy="author")
	public List<Post> posts;

	public User() {
		this.id = -1;
		this.email = null;
		this.password = null;
	}

	public User(String email, String password) {
		this.email = email;
		this.password = HashHelper.createPassword(password);
	}

	static Finder<Long, User> find = new Finder<Long, User>(Long.class,
			User.class);

	public static long createUser(String email, String password) {
		User newUser = new User(email, password);
		newUser.save();
		return newUser.id;
	}
	
	public static User find(long id){
		return find.byId(id);
	}
	
	public static User find(String email){
		return find.where().eq("email", email).findUnique();
	}
	
	public static User authenticate(String email, String password){
		User u = find(email);
		if( u == null)
			return null;
		if( HashHelper.checkPassword(password, u.password)){
			return u;
		}
		return null;
	}

}
