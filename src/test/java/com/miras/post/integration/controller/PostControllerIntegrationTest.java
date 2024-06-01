package com.miras.post.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.miras.post.integration.config.TestRedisConfiguration;
import com.miras.post.model.Post;
import com.miras.post.model.User;
import com.miras.post.repository.UserRepository;
import com.miras.post.service.PostService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureMockMvc
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostService postService;

    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    public void blockUnauthorizedUsers() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/posts");
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());

        requestBuilder = put("/posts/{id}", UUID.randomUUID());
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());

        requestBuilder = post("/posts");
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());

        requestBuilder = delete("/posts/{id}", UUID.randomUUID());
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    void createPostSuccess() throws Exception {
        Post post = new Post();
        post.setDescription("Hello, Miras!");
        RequestBuilder requestBuilder = post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(post))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk()).andReturn();
        Post newPost = mapper.readValue(result.getResponse().getContentAsString(), Post.class);
        assertNotNull(newPost);
        assertEquals(post.getDescription(), newPost.getDescription());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void testCreatePostFailureWithoutDescription() throws Exception {
        Post post = new Post();
        String expectedResponse = "{\"statusCode\":400,\"message\":\"[Description is required]\"}";
        RequestBuilder requestBuilder = post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(post))
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void getPostSuccess() throws Exception {
        Post post = createPost();
        assertNotNull(post.getId());
        RequestBuilder requestBuilder = get("/posts/{id}", post.getId());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(post)));
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void getPostNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        RequestBuilder requestBuilder = get("/posts/{id}", id);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"statusCode\":404,\"message\":\"Requested post does not exists\"}"));
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void testEditPostSuccess() throws Exception {
        Post post = createPost();
        post.setDescription("Edited post");
        RequestBuilder requestBuilder = put("/posts/{id}", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(post));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk()).andReturn();
        Post editedPost = mapper.readValue(result.getResponse().getContentAsString(), Post.class);
        assertNotNull(editedPost);
        assertEquals(post.getDescription(), editedPost.getDescription());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void testEditPostNotFound() throws Exception {
        Post post = createPost();
        post.setDescription("Edited content");
        RequestBuilder requestBuilder = put("/posts/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(post));

        mockMvc.perform(requestBuilder).andExpect(status().isNotFound())
                .andExpect(content().json("{\"statusCode\":404,\"message\":\"Requested post does not exists\"}"));
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void testEditDescriptionNotFound() throws Exception {
        Post post = createPost();
        post.setDescription(null);
        RequestBuilder requestBuilder = put("/posts/{id}", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(post));

        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
                .andExpect(content().json("{\"statusCode\":400,\"message\":\"[Description is required]\"}"));
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void testDeletePostSuccess() throws Exception {
        Post post = createPost();
        RequestBuilder requestBuilder = delete("/posts/{id}", post.getId());

        mockMvc.perform(requestBuilder).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void testDeletePostNotFound() throws Exception {
        RequestBuilder requestBuilder = delete("/posts/{id}", UUID.randomUUID()).with(csrf());

        mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void getAllPostsSuccess() throws Exception {
        RequestBuilder requestBuilder = get("/posts")
                .param("page", "0")
                .param("size", "50");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void getAllUserPostsSuccess() throws Exception {
        User user = userRepository.findByEmail("miras@gmail.com").orElseThrow();
        RequestBuilder requestBuilder = get("/posts/user/{userId}", user.getId())
                .param("page", "0")
                .param("size", "10");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void getAllUserPostsUserNotFound() throws Exception {
        RequestBuilder requestBuilder = get("/posts/user/{userId}", UUID.randomUUID())
                .param("page", "0")
                .param("size", "10");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"statusCode\":404,\"message\":\"User does not exists\"}"));
    }

    @Test
    @WithMockUser("miras@gmail.com")
    public void checkIfContentValueIsProperJson() throws Exception {
        String invalidJson = "{test = test}";
        RequestBuilder requestBuilder = post("/posts", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    private Post createPost() {
        Post post = new Post();
        post.setDescription("New post");
        return postService.createPost(post);
    }
}
