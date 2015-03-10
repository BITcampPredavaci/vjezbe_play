package controllers;

import helpers.CurrentUserFilter;
import play.data.Form;
import play.mvc.*;
import models.*;

/**
 * Controller class for the Post model
 * @author benjamin
 *
 */
public class PostController extends Controller {

	@Security.Authenticated(CurrentUserFilter.class)
	public static Result create() {
		Form<Post> submit = Form.form(Post.class).bindFromRequest();
		Post p = submit.get();
		p.author = SessionHelper.currentUser(ctx());
		if (Post.create(p)) {
			return redirect(routes.UserController.show(p.author.username));
		} else {
			return ok(views.html.user.show.render(p.author, submit));
		}

	}
	
	
	/**
	 * Deletes a post from the current user
	 * If an attempt is made to delete someone else's post it will not go through
	 * @param id the id of the post to be deleted
	 * @return the profile page of the user
	 */
	@Security.Authenticated(CurrentUserFilter.class)
	public static Result delete(long id) {
		User currentUser = SessionHelper.currentUser(ctx());
		Post p = Post.find(id);
		if (p == null) {
			return badRequest(views.html.static_pages.loginToComplete
					.render("Post does not exist"));
		}
		if (currentUser.id == p.author.id || SessionHelper.isAdmin(ctx())) {
			Post.delete(id);
			return redirect("/@" + currentUser.username);
		}
		return badRequest(views.html.static_pages.loginToComplete
				.render("Post does not exist"));
	}

}
