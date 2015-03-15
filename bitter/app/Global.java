import models.User;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

	public void onStart(Application app) {
		if (User.find(1) == null) {
			User u = new User("admin@bitter.ba", "123456", "admin", true);
			User.create(u);
		}
	}

}
