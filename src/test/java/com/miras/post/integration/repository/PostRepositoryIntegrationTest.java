package com.miras.post.integration.repository;

import com.miras.post.model.Post;
import com.miras.post.model.User;
import com.miras.post.repository.PostRepository;
import com.miras.post.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PostRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    private Post post;

    @BeforeEach
    void setUp() {
        User user = userRepository.findByEmail("miras@gmail.com").orElseThrow();
        post = new Post();
        post.setContent("Hello, Miras!");
        post.setUser(user);
        post = postRepository.save(post);
    }

    @Test
    void shouldSavePost() {
        Optional<User> user = userRepository.findByEmail("miras@gmail.com");
        assertTrue(user.isPresent());
        Post newPost = new Post();
        newPost.setContent("New post");
        newPost.setUser(user.get());
        newPost = postRepository.save(newPost);
        assertThat(newPost).isNotNull();
        assertEquals(newPost.getContent(), "New post");
    }

    @Test
    void shouldFindPostById() {
        var foundPost = postRepository.findById(post.getId());
        assertThat(foundPost).isPresent();
        assertEquals(post.getContent(), foundPost.get().getContent());
    }

    @Test
    void shouldNotFindPostByNonExistingId() {
        Optional<Post> foundPost = postRepository.findById(UUID.randomUUID());
        assertTrue(foundPost.isEmpty());
    }

    @Test
    void shouldUpdatePost() {
        post.setContent("Updated post");
        post = postRepository.save(post);
        assertEquals(post.getContent(), "Updated post");
    }

    @Test
    void shouldDeletePost() {
        postRepository.deleteById(post.getId());
        Optional<Post> foundPost = postRepository.findById(post.getId());
        assertTrue(foundPost.isEmpty());
    }

    @Test
    void shouldFindAllPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Post> posts = postRepository.findAllBy(pageable);
        assertThat(posts).isNotEmpty();
    }

    @Test
    void shouldFindPostsByUser() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user = userRepository.save(user);

        Post post = new Post();
        post.setContent("Hello, Test User!");
        post.setUser(user);
        post = postRepository.save(post);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> posts = postRepository.findByUser(user, pageable);

        assertThat(posts).isNotEmpty();
        assertEquals(1, posts.getContent().size());
        assertEquals(user.getEmail(), posts.getContent().get(0).getUser().getEmail());
    }




}