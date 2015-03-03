package controllers;

import helpers.HashHelper;
import models.User;
import play.*;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
	public static class Login {
		public String email;
		public String password;
		
		public String validate(){
			if( User.authenticate(email, password) == null){
				return "Email/Password nije validan";
			}
			return null;
		}	
	}
	
	static Form<Login> loginForm = new Form<Login>(Login.class);

    public static Result index() {
        return ok(index.render(loginForm));
    }
    
    public static Result signin(){
    		Form<Login> submit = loginForm.bindFromRequest();
    		if( submit.hasGlobalErrors() ){
    			return ok(index.render(submit));
    		}
    		Login l = submit.get();
    		User u = User.authenticate(l.email, l.password);
    		if( u == null){
    			return ok(index.render(submit));
    		} else {
    			session().clear();
    			session("user_id", Long.toString(u.id));
    			return redirect("/user/"+u.id);
    		} 
    }
    
    public static Result signout(){
    		session().clear();
    		return redirect("/");
    }

}
