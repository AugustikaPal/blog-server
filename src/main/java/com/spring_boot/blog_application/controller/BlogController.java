package com.spring_boot.blog_application.controller;

import com.spring_boot.blog_application.entity.BlogEntity;
import com.spring_boot.blog_application.entity.User;
import com.spring_boot.blog_application.service.BlogService;
import com.spring_boot.blog_application.service.CloudinaryImageService;
import com.spring_boot.blog_application.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = { "https://blog-client-60xvx45w8-augustika1524gmailcoms-projects.vercel.app", "http://localhost:5500" })
@RequestMapping("/post")
@Slf4j
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryImageService cloudinaryImageService;

    @GetMapping("/allblog")
    public ResponseEntity<?> getAllBlogs() {
        List<BlogEntity> allBlogs = blogService.getBlogEntries();
        if (allBlogs != null && !allBlogs.isEmpty()) {
            return new ResponseEntity<>(allBlogs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No blogs found
        }
    }

    // Get blog by user
    @GetMapping("/myblogs")
    public ResponseEntity<List<BlogEntity>> getBlogByUser() {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        // Find the user by username
        User user = userService.findByUserName(userName);

        // Check if the user is found and has blog entries
        if (user != null) {
            List<BlogEntity> userBlogs = user.getBlogEntries();
            if (userBlogs != null && !userBlogs.isEmpty()) {
                return new ResponseEntity<>(userBlogs, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No blogs found
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // User not found or not authenticated
        }
    }
  
    @PostMapping("/create")
public ResponseEntity<BlogEntity> createBlog(@RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestPart("files") MultipartFile[] files) throws IOException {
    try {
           
        BlogEntity blogEntry = new BlogEntity();
        blogEntry.setTitle(title);
        blogEntry.setContent(content);
        

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

              // Find the user by username
              blogEntry.setUserName(userName);     

        if (files != null && files.length > 0) {
            List<String> imageUrls = cloudinaryImageService.uploadImages(files);
            blogEntry.setImageUrls(imageUrls);
        }
      

         blogService.saveEntry(blogEntry,userName);
        return new ResponseEntity<>(blogEntry, HttpStatus.CREATED);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}


    

  

    // // Post by ID
    @GetMapping("/id/{ID}")
    public ResponseEntity<BlogEntity> publicBlogById(@PathVariable String ID) {
        log.info("Fetching blog with ID: {}", ID); // Log the incoming request
        Optional<BlogEntity> blogEntity = blogService.blogById(ID);
        if (blogEntity.isPresent()) {
            return new ResponseEntity<>(blogEntity.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

  

    // blogByIdOwnedUser 
    @GetMapping("/user/pid/{ID}")
    public ResponseEntity<?> blogByIdOwnedUser(@PathVariable String ID) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        // Find the user by username
        User user = userService.findByUserName(userName);

        // Check if the user is found and has blog entries
        if (user != null) {
            // Fetch all blogs created by the user
            List<BlogEntity> userBlogs = user.getBlogEntries();

            // Check if the blog with the given ID exists in the user's blogs
            Optional<BlogEntity> blogEntity = userBlogs.stream()
                    .filter(blog -> blog.getId().equals(ID))
                    .findFirst();

            if (blogEntity.isPresent()) {
                // If the blog exists, return it
                return new ResponseEntity<>(blogEntity.get(), HttpStatus.OK);
            } else {
                // If the blog does not exist in the user's blogs, return an error message
                return new ResponseEntity<>("Invalid User: Blog not found in user's blogs", HttpStatus.NOT_FOUND);
            }
        } else {
            // If the user is not found, return an unauthorized status
            return new ResponseEntity<>("Unauthorized: User not found", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("id/{ID}")
    public ResponseEntity<?> deleteById(@PathVariable String ID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed = blogService.deleteBlogById(ID, userName);
        if (removed)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //update

    @PutMapping("id/{ID}")
public ResponseEntity<?> updateBlogById(@PathVariable String ID,
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "content", required = false) String content,
        @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userName = authentication.getName();
    User user = userService.findByUserName(userName);
    List<BlogEntity> collect = user.getBlogEntries().stream()
            .filter(x -> x.getId().equals(ID))
            .toList();

    if (!collect.isEmpty()) {
        Optional<BlogEntity> blogEntity = blogService.blogById(ID);
        if (blogEntity.isPresent()) {
            BlogEntity oldEntry = blogEntity.get();

            // Update title only if provided
            if (title != null && !title.isEmpty()) {
                oldEntry.setTitle(title);
            }

            // Update content only if provided
            if (content != null && !content.isEmpty()) {
                oldEntry.setContent(content);
            }

            // Handle image updates only if files are provided
            if (files != null && files.length > 0) {
                List<String> newImageUrls = cloudinaryImageService.uploadImages(files);
                oldEntry.setImageUrls(newImageUrls);
            }

            blogService.saveEntry(oldEntry);
            return new ResponseEntity<>(oldEntry, HttpStatus.OK);
        }
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}



    //like
    @PostMapping("/like/{ID}")
    public ResponseEntity<?> likeBlogById(@PathVariable String ID, @RequestBody Map<String, Boolean> body) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean liked = body.getOrDefault("liked", false);
        try {
            blogService.likeBlog(ID, userName, liked);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //comment
    @PostMapping("/comment/{ID}")
    public ResponseEntity<?> addComment(@PathVariable String ID, @RequestBody Map<String, String> body) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        String commentText = body.get("commentText");

        try {
            blogService.addComment(ID, userName, commentText);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    

    @PostMapping("/delete-image")
    public ResponseEntity<?> deleteImage(@RequestBody Map<String, String> request) {
        String imageUrl = request.get("imageUrl");

        Optional<BlogEntity> blogEntityOpt = blogService.findByImageUrl(imageUrl);
        if (blogEntityOpt.isPresent()) {
            BlogEntity blogEntity = blogEntityOpt.get();

            // Remove the image URL from the blog entry
            blogEntity.getImageUrls().remove(imageUrl);

            // Delete the image from Cloudinary
            cloudinaryImageService.deleteImage(imageUrl);

            // Save the updated blog entity
            blogService.saveEntry(blogEntity);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    
   

}
