package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.*;

public class SessionController extends Controller {

	public static class Login {
		public String emailOrUsername;
		public String password;

		public String validate() {
			if (User.authenticate(emailOrUsername, password) == null) {
				return "Email/Password not valid";
			}
			return null;
		}
	}
	
	public static Result login(){
		return ok(login.render(new Form<Login>(Login.class)));
	}

	public static Result loginSubmit() {
		Form<Login> submit = new Form<Login>(Login.class).bindFromRequest();
		if (submit.hasGlobalErrors()) {
			return ok(login.render(submit));
		}
		Login l = submit.get();
		User u = User.authenticate(l.emailOrUsername, l.password);
		if (u == null) {
			return ok(login.render(submit));
		} else {
			loginUser(u.username);
			return redirect("/@"+u.username);
		}
	}
	
	public static Result logout(){
		logoutUser();
		return redirect("/");
	}
	
	public static void loginUser(String username){
		session().clear();
		session("username", username);
	}
	
	public static void logoutUser(){
		session().clear();
	}

}
