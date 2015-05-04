package controllers;


import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.cloudinary.Cloudinary;

import play.*;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.*;
import models.*;

public class ImageController extends Controller {

	
	
	public static Result create(){
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart filePart = body.getFile("image_url");
		Logger.debug("Content type: " + filePart.getContentType());
		Logger.debug("Key: " + filePart.getKey());
		File image = filePart.getFile();
		
			Image.create(image);
		
		return redirect(routes.Application.index());
	}
	
	
	public static Result delete(int id){
		Image.find.byId(id).delete();
		return redirect(routes.Application.index());
	}
	
	
	
}
