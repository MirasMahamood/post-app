package com.miras.post.service;

import com.miras.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.UUID;

public interface PostService {
    Post createPost(Post post);

    Post editPost(UUID id, Post post);

    void deletePost(UUID id);

    Post getPost(UUID id);

    Slice<Post> getAllPosts(int page);

    Page<Post> getAllUserPosts(UUID userId, int page);

}
