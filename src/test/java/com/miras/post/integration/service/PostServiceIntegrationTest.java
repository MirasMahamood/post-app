package com.miras.post.integration.service;

import com.miras.post.exception.ResourceNotFoundException;
import com.miras.post.model.Post;
import com.miras.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    private Post post;

    @BeforeEach
    public void setUp() {
        post = new Post();
        post.setContent("Hello, Miras!");
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void createPostSuccess() {
        Post createdPost = postService.createPost(post);
        assertNotNull(createdPost);
        assertEquals(post.getContent(), createdPost.getContent());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void editPostSuccess() {
        Post createdPost = postService.createPost(post);
        createdPost.setContent("Edited post");
        Post editedPost = postService.editPost(createdPost.getId(), createdPost);
        assertNotNull(editedPost);
        assertEquals(createdPost.getContent(), editedPost.getContent());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void editPostNotFound() {
        Post createdPost = postService.createPost(post);
        createdPost.setContent("Edited post");
        assertThrows(ResourceNotFoundException.class, () -> postService.editPost(UUID.randomUUID(), createdPost));
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void deletePostSuccess() {
        Post createdPost = postService.createPost(post);
        postService.deletePost(createdPost.getId());
        assertThrows(ResourceNotFoundException.class, () -> postService.getPost(createdPost.getId()));
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void deletePostFailure() {
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(UUID.randomUUID()));
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void getPostSuccess() {
        Post createdPost = postService.createPost(post);
        Post retrievedPost = postService.getPost(createdPost.getId());
        assertNotNull(retrievedPost);
        assertEquals(createdPost.getContent(), retrievedPost.getContent());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void getPostNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> postService.getPost(UUID.randomUUID()));
    }
}