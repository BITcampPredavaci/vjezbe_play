package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.StaticPagesController.Contact;
import helpers.*;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSAuthScheme;
import play.libs.ws.WSResponse;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import views.html.user.*;
import models.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

/**
 * Controller for our BitUser It supports CRUD operations
 * 
 * @author benjamin
 *
 */
public class UserController extends Controller {

	@Security.Authenticated(AdminFilter.class)
	public static Result index() {
		return ok(index.render(BitUser.all()));
	}

	/**
	 * Action to show the registration page
	 * 
	 * @return registration page
	 */
	public static Result newUser() {
		return ok(newUser.render(new Form<BitUser>(BitUser.class)));
	}

	/**
	 * Handles the Post submit from the registration page Checks that no error
	 * exist in the form and saves the user
	 * 
	 * @return return to the registration page in case of errors or to the user
	 *         profile page in case the registration went well
	 */
	public static Result create() {
		Form<BitUser> submit = Form.form(BitUser.class).bindFromRequest();

		if (submit.hasErrors() || submit.hasGlobalErrors()) {
			return ok(newUser.render(submit));
		}
		MultipartFormData body = request().body().asMultipartFormData();
		BitUser u = submit.get();
		if (body.getFile("avatar") != null) {
			String fileError = u.avatar.upload(body.getFile("avatar"),
					BitUser.avatarSettings);
			if (fileError != null) {
				submit.reject(fileError);
			}
		}

		if (!BitUser.create(u)) {
			return ok(newUser.render(submit));
		}
		SessionController.loginUser(u.username);
		return redirect("/@" + u.username);
	}

	/**
	 * Shows the profile page of the user
	 * 
	 * @param username
	 *            username of the user whose profile we want to show In case
	 *            there is no user with that username a not found response is
	 *            given @See StaticPagesController
	 * @return the users profile page
	 */
	public static Result show(String username) {
		BitUser u = BitUser.find(username);
		if (u == null)
			return notFound(views.html.static_pages.notFound
					.render("BitUser does not exist"));
		else
			return ok(show.render(u, new Form<Post>(Post.class) ));
	}

	/**
	 * Only a logged in user can see this page
	 * 
	 * @param username
	 *            the username of the user we want to edit
	 * @return badRequest if an unauthorized user wants to edit the data, edit
	 *         form otherwise
	 */
	@Security.Authenticated(CurrentUserFilter.class)
	public static Result edit(String username) {
		BitUser currentUser = SessionHelper.currentUser(ctx());
		if (!currentUser.username.equals(username)) {
			return badRequest(views.html.static_pages.loginToComplete
					.render("You can't edit this profile"));
		}
		Form<BitUser> editForm = new Form<BitUser>(BitUser.class).fill(currentUser);
		editForm.get().password = "";
		return ok(edit.render(editForm, currentUser));
	}

	/**
	 * POST handle for the @See edit request
	 * 
	 * @param username
	 *            username of the user whose data we are editing
	 * @return redirect to the form in case of error, user profile otherwise
	 */
	public static Result update(String username) {
		Form<BitUser> submit = Form.form(BitUser.class).bindFromRequest();
		BitUser currentUser = SessionHelper.currentUser(ctx());
		if (submit.hasErrors() || submit.hasGlobalErrors()) {
			return ok(edit.render(submit, currentUser));
		}
		BitUser u = submit.get();
		u.id = currentUser.id;
		if (!BitUser.update(u)) {
			return ok(edit.render(submit, currentUser));
		}
		SessionController.loginUser(u.username);
		return redirect("/@" + u.username);
	}

	/**
	 * Handles the follow relation
	 * 
	 * @param id
	 *            the user the current user is following
	 * @return redirect to the followed users profile page
	 */
	public static Result follow(long id) {
		BitUser.addFollower(id, SessionHelper.currentUser(ctx()));
		return redirect(routes.UserController.show(BitUser.find(id).username));
	}

	public static Result unfollow(long id) {
		BitUser.removeFollower(id, SessionHelper.currentUser(ctx()));
		return redirect(routes.UserController.show(BitUser.find(id).username));
	}

	/**
	 * Delete the user with this username
	 * 
	 * @param username
	 *            username of the user to be deleted
	 * @return user list
	 * 
	 */
	@Security.Authenticated(CurrentUserFilter.class)
	public static Result delete(String username) {
		BitUser currentUser = SessionHelper.currentUser(ctx());
		if (currentUser.username.equals(username)
				|| SessionHelper.isAdmin(ctx())) {
			BitUser.deleteUser(username);
			Logger.info("Deleted user: " + username);
		}
		return redirect("/users");
	}

	// get route for purchase
	public static Result showPurchase() {
		return ok(creditPurchase.render());
	}

	// post
	public static Result purchaseProcessing() {

		try {
			String accessToken = new OAuthTokenCredential(
					"AYOzaewr_1iTD_4lHHPWi4lOklIfA7f76MkBTTmNRnU7MNcaMGtdNpOpiE0zZiBvgVU2TNtWv-46_3NU",
					"EHnDyokc88TzV7pBFKe9p9LM16-1xtbQ8tfMtg0vQ0k-oR_pxlqQTGtJHc5iGLTRp-ET4o2MJH2OQNEy")
					.getAccessToken();

			Map<String, String> sdkConfig = new HashMap<String, String>();
			sdkConfig.put("mode", "sandbox");

			APIContext apiContext = new APIContext(accessToken);
			apiContext.setConfigurationMap(sdkConfig);

			Amount amount = new Amount();
			amount.setTotal("7.47");
			amount.setCurrency("USD");

			Transaction transaction = new Transaction();
			transaction
					.setDescription("So we have a really cool description here");
			transaction.setAmount(amount);

			List<Transaction> transactions = new ArrayList<Transaction>();
			transactions.add(transaction);

			Payer payer = new Payer();
			payer.setPaymentMethod("paypal");

			Payment payment = new Payment();
			payment.setIntent("sale");
			payment.setPayer(payer);
			payment.setTransactions(transactions);
			RedirectUrls redirectUrls = new RedirectUrls();
			redirectUrls.setCancelUrl("http://localhost:9000/creditfail");
			redirectUrls.setReturnUrl("http://localhost:9000/creditsuccess");
			payment.setRedirectUrls(redirectUrls);

			Payment createdPayment = payment.create(apiContext);

			Iterator<Links> itr = createdPayment.getLinks().iterator();
			while (itr.hasNext()) {
				Links link = itr.next();
				if (link.getRel().equals("approval_url"))
					return redirect(link.getHref());
			}

			return TODO;

		} catch (PayPalRESTException e) {
			Logger.warn(e.getMessage());
		}

		return TODO;
	}

	public static Result creditSuccess() {

		DynamicForm paypalReturn = Form.form().bindFromRequest();

		String paymentID = paypalReturn.get("paymentId");
		String payerID = paypalReturn.get("PayerID");
		String token = paypalReturn.get("token");

		try{
		String accessToken = new OAuthTokenCredential(
				"AYOzaewr_1iTD_4lHHPWi4lOklIfA7f76MkBTTmNRnU7MNcaMGtdNpOpiE0zZiBvgVU2TNtWv-46_3NU",
				"EHnDyokc88TzV7pBFKe9p9LM16-1xtbQ8tfMtg0vQ0k-oR_pxlqQTGtJHc5iGLTRp-ET4o2MJH2OQNEy")
				.getAccessToken();

		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", "sandbox");
		APIContext apiContext = new APIContext(accessToken);
		apiContext.setConfigurationMap(sdkConfig);

		Payment payment = Payment.get(accessToken, paymentID);

		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(payerID);

		Payment newPayment = payment.execute(apiContext, paymentExecution);
	
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return ok(creditResult.render("Proslo"));
	}

	public static Result creditFail() {
		return ok(creditResult.render("Nije proslo :("));
	}
	
	
	public static Result addToGallery(){
		BitUser user = SessionHelper.currentUser(ctx());
		if(user != null){
			MultipartFormData body = request().body().asMultipartFormData();
			FileHelper image = new FileHelper();
			image.upload(body.getFile("gallery"), BitUser.avatarSettings);
			user.gallery.add(image);
			user.save();
		}
		return redirect("/@"+user.username);
	}
	
	public static Result removeFromGallery(long id){
		FileHelper f = FileHelper.find.byId(id);
		BitUser user = SessionHelper.currentUser(ctx());
		user.gallery.remove(f);
		user.save();
		f.delete();
		return redirect("/@"+user.username);
	}

}
