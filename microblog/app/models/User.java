package models;

import helpers.HashHelper;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;

@Entity
@Table(name="bitter_user") /*to ensure we can work with any sql database*/
public class User extends Model {

	@Id
	public long id;

	@Email
	@Required
	@Column(unique = true)
	public String email;

	@Required
	@Column(unique = true)
	@MinLength(3)
	@MaxLength(10)
	public String username;

	@Required
	@MinLength(6)
	@MaxLength(100)
	public String password;
	
	@Column(name="created_at")
	@CreatedTimestamp
	public Date createdAt;

	@Column(name="updated_at")
	@UpdatedTimestamp
	public Date updatedAt;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="author")
	public List<Post> posts;

	public User(String email, String password, String username) {
		this.email = email;
		this.password = password;
		this.username = username;
		hashPassword();
	}

	static Finder<Long, User> find = new Finder<Long, User>(Long.class,
			User.class);

	/**
	 * Create a new user
	 * @param email users email
	 * @param password users password (to be hashed @see constructor)
	 * @param username the username
	 * @return true if save is successful, false otherwise
	 */
	public static boolean create(String email, String password, String username) {
		try {
			User u = new User(email, password, username);
			if (u.validate() != null)
				return false;
			u.save();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Saves a User object, the password is hashed before save @see hashPassword
	 * @param u the user object we want to save
	 * @return true if save is successful, false otherwise
	 */
	public static boolean create(User u) {
		try {
			if (u.validate() != null)
				return false;
			u.hashPassword();
			u.save();
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	
	public static User find(long id) {
		return find.byId(id);
	}

	/**
	 * Find a user where the email or username are
	 * equal to the passed in parameter
	 * @param usernameOrEmail the username or email we are searching for
	 * @return the user or null
	 */
	public static User find(String usernameOrEmail) {
		return find.where(
				String.format("username = '%s' OR email = '%s'",
						usernameOrEmail, usernameOrEmail)).findUnique();

	}
	
	public static List<User> all(){
		return find.all();
	}

	/**
	 * Rehash the password (the input is from a form so we expect plain text)
	 * @param u User to update
	 * @return true if save is successful 
	 */
	public static boolean update(User u) {
		try {
			u.hashPassword();
			u.update();
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Delete the user with the given username
	 * @param username the username we want to delete
	 */
	public static void deleteUser(String username){
		User.find(username).delete();
	}

	/**
	 * Authenticate a user given a email/username and password
	 * @param usernameOrEmail the email/username of the user
	 * @param password the password
	 * @return the User object if the login is successful or null otherwise
	 */
	public static User authenticate(String usernameOrEmail, String password) {
		User u = find(usernameOrEmail);
		if (u == null)
			return null;
		if (HashHelper.checkPassword(password, u.password)) {
			return u;
		}
		return null;
	}

	/**
	 * Using the @see HashHelper we create a password hash if needed and save it
	 * as the this.password value
	 */
	private void hashPassword() {
		try {
			if (!HashHelper.checkPassword(this.password, this.password)) {
				this.password = HashHelper.createPassword(this.password);
			}
		} catch (Exception e) {
			this.password = HashHelper.createPassword(this.password);
		}
	}

	/**
	 * Helper method for the @see Form class, to validate beyond what
	 * the form can validate for us.
	 * @return null if the validation passed, error string otherwise
	 */
	public String validate() {
		if (find.where().eq("email", email).findRowCount() > 0) {
			User other = find.where().eq("email", email).findUnique();
			System.out
					.println("Other id: " + other.id + " This id: " + this.id);
			if (other.id != this.id && this.id != 0)
				return "Email taken";
		}
		if (find.where().eq("username", username).findRowCount() > 0) {
			User other = find.where().eq("username", username).findUnique();
			if (other.id != this.id && this.id != 0)
				return "Username taken";
		}
		if (username.length() < 3 || username.length() > 10)
			return "Username length not valid";
		if (password.length() < 6)
			return "Password short";

		return null;
	}
}
