import models.User;
import play.Application;
import play.GlobalSettings;


public class Global extends GlobalSettings {
	
	public void onStart(Application app){
		User u = new User("admin@bitter.ba", "123456", "admin", true);
		User.create(u);
	}

}
