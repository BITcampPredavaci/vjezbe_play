package controllers;

import helpers.MailHelper;
import models.User;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
//include a specific folder(package) from views

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
		User currentUser = SessionHelper.currentUser(ctx());
		if(currentUser == null)
		return ok(index.render(null));
		else{
			return ok(index.render(currentUser.getFeed()));
		}
			
	}

	public static Result about() {
		return ok(about.render());
	}

	public static Result loginToComplete() {
		return badRequest(loginToComplete
				.render("Login to complete this action"));
	}

	public static Result contact() {
		return ok(contact.render(new Form<Contact>(Contact.class)));
	}

	/**
	 * We return whatever the promise returns, so the return value is changed from Result to Promise<Result>
	 * 
	 * @return the contact page with a message indicating if the email has been sent.
	 */
	public static Promise<Result> sendMail() {
		//need this to get the google recapctha value
		DynamicForm temp = DynamicForm.form().bindFromRequest();
		
		/* send a request to google recaptcha api with the value of our secret code and the value
		 * of the recaptcha submitted by the form */
		Promise<Result> holder = WS
				.url("https://www.google.com/recaptcha/api/siteverify")
				.setContentType("application/x-www-form-urlencoded")
				.post(String.format("secret=%s&response=%s",
						//get the API key from the config file
						Play.application().configuration().getString("recaptchaKey"),
						temp.get("g-recaptcha-response")))
				.map(new Function<WSResponse, Result>() {
					//once we get the response this method is loaded
					public Result apply(WSResponse response) {
						//get the response as JSON
						JsonNode json = response.asJson();
						Form<Contact> submit = Form.form(Contact.class)
								.bindFromRequest();
						
						//check if value of success is true
						if (json.findValue("success").asBoolean() == true
								&& !submit.hasErrors()) {

							Contact newMessage = submit.get();
							String email = newMessage.email;
							String message = newMessage.message;

							flash("success", "Message sent");
							MailHelper.send(email, message);
							return redirect("/contact");
						} else {
							flash("error", "There has been a problem");
							return ok(contact.render(submit));

						}
					}
				});
		//return the promisse
		return holder;
	}

}
