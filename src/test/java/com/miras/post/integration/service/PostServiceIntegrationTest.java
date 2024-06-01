package com.miras.post.integration.service;

import com.miras.post.exception.ResourceNotFoundException;
import com.miras.post.model.Post;
import com.miras.post.service.PostService;
import com.miras.post.unit.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;

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
        post.setDescription("Hello, Miras!");
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void createPostSuccess() {
        Post createdPost = postService.createPost(post);
        assertNotNull(createdPost);
        assertEquals(post.getDescription(), createdPost.getDescription());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void createPostSuccessWithLongDescription() {
        Post post = new Post();
        String longDescription = TestData.getDescriptionWithLength(1000);
        post.setDescription(longDescription);
        Post newPost = postService.createPost(post);
        assertNotNull(newPost);
        assertEquals(longDescription, newPost.getDescription());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void createPostFailureWithLongDescription() {
        Post post = new Post();
        post.setDescription(TestData.getDescriptionWithLength(1001));
        assertThrows(TransactionSystemException.class, () -> postService.createPost(post));
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void editPostSuccess() {
        Post createdPost = postService.createPost(post);
        createdPost.setDescription("Edited post");
        Post editedPost = postService.editPost(createdPost.getId(), createdPost);
        assertNotNull(editedPost);
        assertEquals(createdPost.getDescription(), editedPost.getDescription());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void editPostNotFound() {
        Post createdPost = postService.createPost(post);
        createdPost.setDescription("Edited post");
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
        assertEquals(createdPost.getDescription(), retrievedPost.getDescription());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void getPostNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> postService.getPost(UUID.randomUUID()));
    }
}