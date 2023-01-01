package com.bd.socialnetwork.Repository;

import com.bd.socialnetwork.Entity.PostEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<PostEntity, Long> {
    List<PostEntity> findAllByUser(String idUser);
}
