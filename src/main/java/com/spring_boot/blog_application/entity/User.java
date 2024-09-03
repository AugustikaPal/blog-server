package com.spring_boot.blog_application.entity;

import lombok.Data;
import java.util.Date;

import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    @NonNull
    private String userName;
    @Indexed(unique = true)
    private String email;
    @NonNull
    private String password;

    private String verificationToken;
    private Date tokenExpirationDate; 
    private boolean enabled=false;
    @DBRef
    private List<BlogEntity>blogEntries=new ArrayList<>();
    private List<String> roles;
    
   

    @DBRef
    private List<BlogEntity> likedBlogs = new ArrayList<>();

    public List<BlogEntity> getLikedBlogs() {
        return likedBlogs;
    }

    public void setLikedBlogs(List<BlogEntity> likedBlogs) {
        this.likedBlogs = likedBlogs;
    }
    public void likeBlog(BlogEntity blog) {
        if (!likedBlogs.contains(blog)) {
            likedBlogs.add(blog);
        }
    }

    public void unlikeBlog(BlogEntity blog) {
        likedBlogs.remove(blog);
    }
   
   
}
