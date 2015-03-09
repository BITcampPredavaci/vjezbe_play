package controllers;

import helpers.MailHelper;
import play.*;
import play.data.Form;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.mvc.*;
//include a specific folder(package) from views
import views.html.static_pages.*;

/**
 * A controller for our static pages-pages whose content we do not expect to
 * change frequently
 * 
 * @author benjamin
 *
 */
public class StaticPagesController extends Controller {

	public static class Contact {
		@Required
		@Email
		public String email;
		@Required
		public String message;
	}

	public static Result index() {
		return ok(index.render());
	}

	public static Result about() {
		return ok(about.render());
	}

	public static Result loginToComplete() {
		return badRequest(loginToComplete
				.render("Login to complete this action"));
	}
	
	public static Result contact(){
		return ok(contact.render(
				new Form<Contact>(Contact.class)
				));
	}
	
	public static Result sendMail(){
		
		Form<Contact> submit = Form.form(Contact.class).bindFromRequest();
		if( submit.hasErrors() ){
			return ok(contact.render(submit));
		}
		
		Contact newMessage = submit.get();
		String email = newMessage.email;
		String message = newMessage.message;
		
		flash("success", "Message sent");
		MailHelper.send(email, message);
		return redirect("/contact");
	}

}
