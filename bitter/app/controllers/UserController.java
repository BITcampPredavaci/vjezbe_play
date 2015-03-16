package controllers;

import helpers.*;
import play.data.Form;
import play.mvc.*;
import views.html.user.*;

import models.*;
/**
 * Controller for our User
 * It supports CRUD operations
 * @author benjamin
 *
 */
public class UserController extends Controller {
	
	@Security.Authenticated(AdminFilter.class)
	public static Result index() {
		return ok(index.render(User.all()));
	}
	/**
	 * Action to show the registration page
	 * @return registration page
	 */
	public static Result newUser() {
		return ok(newUser.render(new Form<User>(User.class)));
	}
	/**
	 * Handles the Post submit from the registration page
	 * Checks that no error exist in the form
	 * and saves the user
	 * @return return to the registration page in case of errors or to the
	 * user profile page in case the registration went well
	 */
	public static Result create() {
		Form<User> submit = Form.form(User.class).bindFromRequest();
		if (submit.hasErrors() || submit.hasGlobalErrors()) {
			return ok(newUser.render(submit));
		}
		User u = submit.get();
		if (!User.create(u)) {
			return ok(newUser.render(submit));
		}
		SessionController.loginUser(u.username);
		return redirect("/@" + u.username);
	}
	/**
	 * Shows the profile page of the user
	 * @param username username of the user whose profile we want to show
	 * In case there is no user with that username a not found response is given @See StaticPagesController
	 * @return the users profile page
	 */
	public static Result show(String username) {
		User u = User.find(username);
		if (u == null)
			return notFound(views.html.static_pages.notFound
					.render("User does not exist"));
		else
			return ok(show.render(u, new Form<Post>(Post.class)));
	}
	/**
	 * Only a logged in user can see this page
	 * @param username the username of the user we want to edit
	 * @return badRequest if an unauthorized user wants to edit the data, edit form otherwise
	 */
	@Security.Authenticated(CurrentUserFilter.class)
	public static Result edit(String username) {
		User currentUser = SessionHelper.currentUser(ctx());
		if (!currentUser.username.equals(username)) {
			return badRequest(views.html.static_pages.loginToComplete
					.render("You can't edit this profile"));
		}
		Form<User> editForm = new Form<User>(User.class).fill(currentUser);
		editForm.get().password = "";
		return ok(edit.render(editForm, currentUser));
	}
	/**
	 * POST handle for the @See edit request
	 * @param username username of the user whose data we are editing
	 * @return redirect to the form in case of error, user profile otherwise
	 */
	public static Result update(String username) {
		Form<User> submit = Form.form(User.class).bindFromRequest();
		User currentUser = SessionHelper.currentUser(ctx());
		if (submit.hasErrors() || submit.hasGlobalErrors()) {
			return ok(edit.render(submit, currentUser));
		}
		User u = submit.get();
		u.id = currentUser.id;
		if (!User.update(u)) {
			return ok(edit.render(submit, currentUser));
		}
		SessionController.loginUser(u.username);
		return redirect("/@" + u.username);
	}
	
	/**
	 * Handles the follow relation
	 * @param id the user the current user is following
	 * @return redirect to the followed users profile page
	 */
	public static Result follow(long id){
		User.addFollower(id, SessionHelper.currentUser(ctx()));
		return redirect(routes.UserController.show(User.find(id).username));
	}
	
	public static Result unfollow(long id){
		User.removeFollower(id, SessionHelper.currentUser(ctx()));
		return redirect(routes.UserController.show(User.find(id).username));
	}
	
	/**
	 * Delete the user with this username
	 * @param username username of the user to be deleted
	 * @return user list
	 * 
	 */
	@Security.Authenticated(CurrentUserFilter.class)
	public static Result delete(String username) {
		User currentUser = SessionHelper.currentUser(ctx());
		if (currentUser.username.equals(username)
				|| SessionHelper.isAdmin(ctx()))
			User.deleteUser(username);
		return redirect("/users");
	}

}
