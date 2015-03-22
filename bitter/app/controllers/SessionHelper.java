package controllers;

import models.User;
import play.mvc.Http.Context;
/**
 * Helper to get the current user and to check if the user is an admin
 * @author benjamin
 *
 */

public class SessionHelper {
	
	public static User currentUser(Context ctx){
		String username = ctx.session().get("username");
		if(username == null)
			return null;
		return User.find(username);
	}
	
	public static boolean isAdmin(Context ctx){
		User u = currentUser(ctx);
		if( u == null)
			return false;
		return u.admin;
	}

}
