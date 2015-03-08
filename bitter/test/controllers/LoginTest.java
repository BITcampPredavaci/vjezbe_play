package controllers;

import org.junit.*;

import com.google.common.collect.ImmutableMap;

import play.mvc.Result;
import play.test.*;
import static org.junit.Assert.*;
import static play.test.Helpers.*;
import models.*;

public class LoginTest extends WithApplication {

	@Before
	public void setup() {
		fakeApplication(inMemoryDatabase(), fakeGlobal());
		User.create("test@mail.com", "123456", "test");
	}

	@Test
	public void authenticateSuccess() {
		Result result = callAction(
				controllers.routes.ref.SessionController.loginSubmit(),
				fakeRequest().withFormUrlEncodedBody(
						ImmutableMap.of("emailOrUsername", "test@mail.com", "password",
								"123456")));
		assertEquals(303, status(result));
		assertEquals("test", session(result).get("username"));
	}
	
	@Test
	public void authenticateSuccessUsername() {
		Result result = callAction(
				controllers.routes.ref.SessionController.loginSubmit(),
				fakeRequest().withFormUrlEncodedBody(
						ImmutableMap.of("emailOrUsername", "test", "password",
								"123456")));
		assertEquals(303, status(result));
		assertEquals("test", session(result).get("username"));
	}


	@Test
	public void authenticateFail() {
		Result result = callAction(
				controllers.routes.ref.SessionController.loginSubmit(),
				fakeRequest().withFormUrlEncodedBody(
						ImmutableMap.of("emailOrUsername", "test@mail.com", "password",
								"13456")));
		assertEquals(200, status(result));
		assertEquals(null, session(result).get("user_id"));
		assertFalse(session(result).containsKey("user_id"));
	}
	

}
