package controllers;


import org.apache.commons.codec.binary.Base64;

import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import views.html.*;
/**
 * Handles login and logout functionality
 * @author benjamin
 *
 */
public class SessionController extends Controller {
	/**
	 * Helper class for the login
	 * @author benjamin
	 *
	 */
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
	
	public String getUsername(Context ctx) {

        Logger.debug("Some data: ");
        Logger.debug(ctx.request().getHeader("Authorization"));
        Logger.debug(new String(Base64.decodeBase64(ctx.request().getHeader("Authorization") )));
        Logger.debug(ctx.request().username());
        Logger.debug("=========");

        User user = null;
        String[] authTokenHeaderValues = ctx.request().headers().get(SecurityController.AUTH_TOKEN_HEADER);
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
            user = models.User.findByAuthToken(authTokenHeaderValues[0]);
            if (user != null) {
                ctx.args.put("user", user);
                return user.getEmailAddress();
            }
        }

        return null;
    }

}
