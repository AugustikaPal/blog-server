package com.spring_boot.blog_application.repo;

import com.spring_boot.blog_application.entity.User;



import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUserName(String userName);
    void deleteByUserName(String name);
    
}
