package com.spring_boot.blog_application.repo;

import com.spring_boot.blog_application.entity.BlogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BlogRepository extends MongoRepository<BlogEntity,
        String> {
    Optional<BlogEntity> findByImageUrlsContaining(String imageUrl);
}

