package com.spring_boot.blog_application.entity;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "blog_entries")
@Data
@NoArgsConstructor
public class BlogEntity {

    @Id
    private String id;
    @NonNull
    private String title;
    @NonNull
    private String content;
    @NonNull
    private String userName; 
    private List<String> imageUrls = new ArrayList<>();
    private LocalDateTime date;

  
    private int likes;
    private Set<String> likedByUsers = new HashSet<>(); 

      // New field for storing comments
      private List<Comment> comments = new ArrayList<>();

      @Data
      @NoArgsConstructor
      public static class Comment {
          private String userName;
          private String commentText;
          private LocalDateTime commentDate;
      }

}
