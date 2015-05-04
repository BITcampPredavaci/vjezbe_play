import models.FileHelper;
import models.Post;
import models.BitUser;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

	public void onStart(Application app) {
		if (BitUser.find(1) == null) {
			BitUser u = new BitUser("admin@bitter.ba", "123456", "admin", true);
			u.avatar = new FileHelper("images/default-avatar.png");
			BitUser.create(u);
			Post p = new Post();
			p.author = u;
			p.content = "The very first post";
			p.save();
		}
	}

}
