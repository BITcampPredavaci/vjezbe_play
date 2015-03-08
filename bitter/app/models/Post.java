package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;

@Entity
public class Post extends Model{
	
	@Id
	public long id;
	
	@Required
	@MinLength(3)
	@MaxLength(144)
	public String content;

	@Column(name="created_at")
	@CreatedTimestamp
	public Date createdAt;

	@Column(name="updated_at")
	@UpdatedTimestamp
	public Date updatedAt;
	
	@ManyToOne
	public User author;
	
	static Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
	
	
	public static boolean create(Post p){
		try{
			p.save();
			return true;
		} catch(Exception e){
			return false;
		}
	}
	
	public static void delete(long id){
		find.byId(id).delete();
	}
	
	public static Post find(long id){
		return find.byId(id);
	}

}
