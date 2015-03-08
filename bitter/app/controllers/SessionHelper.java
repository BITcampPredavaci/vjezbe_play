package controllers;

import models.User;
import play.mvc.Http.Context;

public class SessionHelper {
	
	public static User currentUser(Context ctx){
		String username = ctx.session().get("username");
		if(username == null)
			return null;
		return User.find(username);
	}
	
	public static boolean isAdmin(Context ctx){
		return false;
	}

}
