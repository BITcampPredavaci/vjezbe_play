import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import static org.fluentlenium.core.filter.FilterConstructor.*;

public class IntegrationTest extends WithApplication {

   
    @Test
    public void testHome() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                assertThat(browser.pageSource()).contains("Welcome to Bitter");
            }
        });
    }
    
    @Test
    public void testAbout() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/about");
                assertThat(browser.pageSource()).contains("About Us");
                assertThat(browser.title().equals("Bitter | About"));
            }
        });
    }
    
    @Test
    public void testSignup(){
    	running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/register");
                browser.fill("#email").with("test@mail.com");
                browser.fill("#username").with("test");
                browser.fill("#password").with("123456");
                browser.submit("#userform");
            }
        });
    }
    
}
