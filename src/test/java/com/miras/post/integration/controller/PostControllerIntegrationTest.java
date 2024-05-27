package com.miras.post.integration.controller;

import com.miras.post.TestData;
import com.miras.post.controller.PostController;
import com.miras.post.exception.ResourceNotFoundException;
import com.miras.post.model.Post;
import com.miras.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = PostController.class)
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity()).build();
    }

    @Test
    public void blockUnauthorizedUsers() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/posts");
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());

        requestBuilder = put("/posts/{id}", UUID.randomUUID()).with(csrf());
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());

        requestBuilder = post("/posts").with(csrf());
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());

        requestBuilder = delete("/posts/{id}", UUID.randomUUID()).with(csrf());
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testCreatePostSuccess() throws Exception {
        when(postService.createPost(any(Post.class))).thenReturn(TestData.getTestPost());

        RequestBuilder requestBuilder = post("/posts").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.POST_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestData.POST_JSON));
    }

    @Test
    @WithMockUser
    public void testCreatePostFailure() throws Exception {
        when(postService.createPost(any(Post.class))).thenThrow(new RuntimeException());

        RequestBuilder requestBuilder = post("/posts").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.POST_JSON);

        mockMvc.perform(requestBuilder).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    public void testEditPostSuccess() throws Exception {
        when(postService.editPost(any(UUID.class), any(Post.class))).thenReturn(TestData.getTestPost());

        RequestBuilder requestBuilder = put("/posts/{id}", UUID.randomUUID()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.POST_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestData.POST_JSON));
    }

    @Test
    @WithMockUser
    public void testEditPostFailure() throws Exception {
        when(postService.editPost(any(UUID.class), any(Post.class))).thenThrow(new RuntimeException());

        RequestBuilder requestBuilder = put("/posts/{id}", UUID.randomUUID()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.POST_JSON);

        mockMvc.perform(requestBuilder).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    public void testDeletePostSuccess() throws Exception {
        doNothing().when(postService).deletePost(any(UUID.class));

        RequestBuilder requestBuilder = delete("/posts/{id}", UUID.randomUUID()).with(csrf());

        mockMvc.perform(requestBuilder).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testDeletePostFailure() throws Exception {
        doThrow(new RuntimeException()).when(postService).deletePost(any(UUID.class));

        RequestBuilder requestBuilder = delete("/posts/{id}", UUID.randomUUID()).with(csrf());

        mockMvc.perform(requestBuilder).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    public void getPostSuccess() throws Exception {
        Post post = TestData.getTestPost();
        when(postService.getPost(post.getId())).thenReturn(post);

        RequestBuilder requestBuilder = get("/posts/{id}", post.getId());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestData.POST_JSON));
    }

    @Test
    @WithMockUser
    public void getPostNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(postService.getPost(id)).thenThrow(new ResourceNotFoundException("Post not found"));

        RequestBuilder requestBuilder = get("/posts/{id}", id);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"statusCode\":404,\"message\":\"Post not found\"}"));
    }

    @Test
    @WithMockUser
    public void testGetAllPostsSuccessWithPagination() throws Exception {
        when(postService.getAllPosts(anyInt(), anyInt())).thenReturn(TestData.getAllTestPosts());

        RequestBuilder requestBuilder = get("/posts")
                .param("page", "0")
                .param("size", "50");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestData.EXPECTED_ALL_POST_JSON));
    }

    @Test
    @WithMockUser
    public void testGetAllPostsFailure() throws Exception {
        when(postService.getAllPosts(anyInt(), anyInt())).thenThrow(new RuntimeException());

        RequestBuilder requestBuilder = get("/posts")
                .param("page", "0")
                .param("size", "50");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    public void testGetAllUserPostsSuccessWithPagination() throws Exception {
        when(postService.getAllUserPosts(any(UUID.class), anyInt(), anyInt())).thenReturn(TestData.getAllUserPosts());

        RequestBuilder requestBuilder = get("/posts/user/{userId}", UUID.randomUUID())
                .param("page", "0")
                .param("size", "50");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestData.EXPECTED_ALL_USER_POST_JSON));
    }

    @Test
    @WithMockUser
    public void testGetAllUserPostsFailure() throws Exception {
        when(postService.getAllUserPosts(any(UUID.class), anyInt(), anyInt())).thenThrow(new RuntimeException());

        RequestBuilder requestBuilder = get("/posts/user/{userId}", UUID.randomUUID())
                .param("page", "0")
                .param("size", "50");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    public void testPostNotFoundWhileEditing() throws Exception {
        when(postService.editPost(any(UUID.class), any(Post.class))).thenThrow(new ResourceNotFoundException("Post not found"));

        RequestBuilder requestBuilder = put("/posts/{id}", UUID.randomUUID()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.POST_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"statusCode\":404,\"message\":\"Post not found\"}"));
    }

    @Test
    @WithMockUser
    public void checkIfContentValueIsProperJson() throws Exception {
        String invalidJson = "{test = test}";
        RequestBuilder requestBuilder = put("/posts/{id}", UUID.randomUUID()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void validateContentLength() throws Exception {
        RequestBuilder requestBuilder = post("/posts").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.generatePostJsonWithContentSize(1001));
        String expectedResponse = "{\"statusCode\":400,\"message\":\"[Description should not be more than 1000 characters]\"}";
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
}
