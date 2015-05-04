package models;

import static org.junit.Assert.*;

import org.junit.*;

import play.test.WithApplication;
import models.*;

public class UserModelTest extends WithApplication {

	@Before
	public void setUp(){
		BitUser.create("test@mail.com", "123456", "test");
	}
	
	@Test
	public void uniqueEmail(){
		boolean result = BitUser.create("test@mail.com", "123456", "test");
		assertFalse(result);
	}
	
	@Test
	public void uniqueUsername(){
		boolean result = BitUser.create("test1@mail.com", "123456", "test");
		assertFalse(result);
	}
	
	@Test
	public void minLengthPassword(){
		boolean result = BitUser.create("test1@mail.com", "1236", "test");
		assertFalse(result);
	}
	
	@Test
	public void minLengthUsername(){
		boolean result = BitUser.create("test1@mail.com", "123567", "");
		assertFalse(result);
	}
	
	@Test
	public void maxLengthUsername(){
		boolean result = BitUser.create("test1@mail.com", "123456", "test12345678121212122121");
		assertFalse(result);
	}
	
}
