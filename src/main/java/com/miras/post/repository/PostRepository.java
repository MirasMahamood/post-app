package com.miras.post.repository;

import com.miras.post.model.Post;
import com.miras.post.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    Slice<Post> findAllBy(Pageable page);

    Page<Post> findByUser(User user, Pageable pageable);

}
