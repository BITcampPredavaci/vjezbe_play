package controllers;

import play.*;
import play.mvc.*;
//include a specific folder(package) from views
import views.html.static_pages.*;
/**
 * A controller for our static pages-pages whose content
 * we do not expect to change frequently
 * @author benjamin
 *
 */
public class StaticPagesController extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result about() {
    	return ok(about.render());
    }
    
    public static Result loginToComplete(){
    	return badRequest(loginToComplete.render("Login to complete this action"));
    }

}
