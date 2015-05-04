package controllers;

import play.*;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import models.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render(Form.form(Image.class), Image.all()));
    }

}
