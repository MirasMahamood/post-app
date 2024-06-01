package com.miras.post.unit.controller;

import com.miras.post.unit.TestData;
import com.miras.post.controller.PostController;
import com.miras.post.exception.ResourceAlreadyExistsException;
import com.miras.post.model.Post;
import com.miras.post.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostControllerUnitTest {
     @Mock
     private PostService postService;

     @InjectMocks
     private PostController postController;

    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

     @Test
     public void testCreatePostSuccess() {
        Post post = TestData.getTestPost();
         when(postService.createPost(post)).thenReturn(post);

         ResponseEntity<Post> response = postController.createPost(post);

         assertEquals(HttpStatus.OK, response.getStatusCode());
         assertEquals(post, response.getBody());
     }

     @Test
     public void testCreatePostAlreadyExists() {
         when(postService.createPost(any(Post.class))).
                 thenThrow(new ResourceAlreadyExistsException("Post already exists"));
         try {
             postController.createPost(TestData.getTestPost());
         } catch (ResourceAlreadyExistsException ex) {
             assertEquals("Post already exists", ex.getMessage());
         }
     }

    @Test
    public void editPostSuccess() {
        Post post = TestData.getTestPost();
        UUID postId = post.getId();
        post.setDescription("New edited content");
        when(postService.editPost(postId, post)).thenReturn(post);

        ResponseEntity<Post> response = postController.editPost(postId, post);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(post, response.getBody());
    }

    @Test
    public void editPostFailure() {
        Post post = TestData.getTestPost();
        UUID id = UUID.randomUUID();
        when(postService.editPost(id, post)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> postController.editPost(id, post));
    }

    @Test
    public void deletePostSuccess() {
        UUID id = UUID.randomUUID();
        doNothing().when(postService).deletePost(id);

        ResponseEntity<Void> response = postController.deletePost(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deletePostFailure() {
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException()).when(postService).deletePost(id);

        assertThrows(RuntimeException.class, () -> postController.deletePost(id));
    }

    @Test
    public void getPostSuccess() {
        Post post = TestData.getTestPost();
        UUID id = UUID.randomUUID();
        when(postService.getPost(id)).thenReturn(post);

        ResponseEntity<Post> response = postController.getPost(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(post, response.getBody());
    }

    @Test
    public void getPostFailure() {
        UUID id = UUID.randomUUID();
        when(postService.getPost(id)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> postController.getPost(id));
    }

    @Test
    public void getAllPostsSuccess() {
        when(postService.getAllPosts(0, 50)).thenReturn(TestData.getAllTestPosts());

        ResponseEntity<?> response = postController.getAllPosts(0, 50);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertTrue(responseBody.containsKey("posts"));
        assertTrue(responseBody.containsKey("currentPage"));
        assertTrue(responseBody.containsKey("totalItems"));
        assertTrue(responseBody.containsKey("hasNext"));
    }

    @Test
    public void getAllPostsFailure() {
        when(postService.getAllPosts(0, 50)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> postController.getAllPosts(0, 50));
    }

    @Test
    public void getAllUserPostsSuccess() {
        UUID userId = UUID.randomUUID();
        when(postService.getAllUserPosts(userId, 0, 50)).thenReturn(TestData.getAllUserPosts());

        ResponseEntity<?> response = postController.getAllUserPosts(userId, 0, 50);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertTrue(responseBody.containsKey("posts"));
        assertTrue(responseBody.containsKey("currentPage"));
        assertTrue(responseBody.containsKey("totalItems"));
        assertTrue(responseBody.containsKey("totalPages"));
    }

    @Test
    public void getAllUserPostsFailure() {
        UUID userId = UUID.randomUUID();
        when(postService.getAllUserPosts(userId, 0, 50)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> postController.getAllUserPosts(userId, 0, 50));
    }

}
