package com.niit.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.niit.dao.BlogDao;
import com.niit.model.BlogComment;
import com.niit.model.BlogPost;
import com.niit.model.User;
import com.niit.model.Error;

@Controller
public class BlogPostController {
	@Autowired
	private BlogDao blogDao;
	@RequestMapping(value="/saveBlogPost",method=RequestMethod.POST)
	public ResponseEntity<?> saveBlogPost(@RequestBody BlogPost blogPost, HttpSession session ){
		User user=(User)session.getAttribute("user");
		try{
			blogPost.setCreatedBy(user);
			blogPost.setCreatedOn(new Date());
		
		return new ResponseEntity<Void>(HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			Error error=new Error(3,"Couldnt insert user details. Cannot have null/duplicate values " + e.getMessage());
			return new ResponseEntity<Error>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		}
	@RequestMapping(value = "/list/{approved}", method = RequestMethod.GET)
	public ResponseEntity<?> getBlogList(@PathVariable int approved,HttpSession session){
		User user=(User)session.getAttribute("user");
		if(user==null){
			Error error=new Error(1,"Unauthroized user");
			return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
		List<BlogPost> blogPosts = blogDao.getBlogPosts(approved);
	
		/*if(user.getRole().equals("admin")|| user.getRole().equals("Employee"))
			blogPosts=blogDao.getBlogPosts(0);
		else
			blogPosts=blogDao.getBlogPosts(1);
	*/
		return new ResponseEntity<List<BlogPost>>(blogPosts,HttpStatus.OK);
	}
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getBlogPost(@PathVariable(value="id") int id,
			HttpSession session){
		User user=(User)session.getAttribute("user");
		if(user==null){
			Error error=new Error(1,"Unauthroized user");
			return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
		//select * from blogpost where id=33
		BlogPost blogPost=blogDao.getBlogPostById(id);
		return new ResponseEntity<BlogPost>(blogPost,HttpStatus.OK);
	}
	@RequestMapping(value="/addComment",method=RequestMethod.POST)
	public ResponseEntity<?> addBlogComment(@RequestBody BlogComment blogComment,HttpSession session){
		User user=(User)session.getAttribute("user");
		if(user==null){
			Error error=new Error(1,"Unauthroized user");
			return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
		blogComment.setCommentedBy(user);
		blogComment.setCommentedOn(new Date());
		blogDao.addBlogComment(blogComment);
		return new ResponseEntity<BlogComment>(blogComment,HttpStatus.OK);
	}

	@RequestMapping(value="/getBlogComments/{blogPostId}",method=RequestMethod.GET)
	public ResponseEntity<?>blogComments(@PathVariable int blogPostId,HttpSession session){
		User user=(User)session.getAttribute("user");
		if(user==null){
			Error error=new Error(1,"Unauthroized user");
			return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
		List<BlogComment> blogComments=blogDao.getBlogComments(blogPostId);
		return new ResponseEntity<List<BlogComment>>(blogComments,HttpStatus.OK);
	}
	@RequestMapping(value="/updateApproval",method=RequestMethod.PUT)
	public ResponseEntity<?> updateApproval(@RequestBody BlogPost blogPost,HttpSession session){
		User user=(User)session.getAttribute("user");
		if(user==null){
			Error error=new Error(1,"Unauthroized user");
			return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
		if(blogPost.isApproved())
		{
			blogPost.setStatus('A');
		blogDao.update(blogPost);
		}
		else if(blogPost.isApproved())
		{
			blogPost.setStatus('D');
			blogDao.update(blogPost);
		}
	
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
