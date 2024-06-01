package com.miras.post.integration.repository;

import com.miras.post.model.Post;
import com.miras.post.model.User;
import com.miras.post.repository.PostRepository;
import com.miras.post.repository.UserRepository;
import com.miras.post.unit.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.TransactionSystemException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    private Post post;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.findByEmail("miras@gmail.com").orElseThrow();
        post = new Post();
        post.setDescription("Hello, Miras!");
        post.setUser(user);
        post = postRepository.save(post);
    }

    @Test
    void shouldSavePost() {
        Post newPost = new Post();
        newPost.setDescription("New post");
        newPost.setUser(user);
        newPost = postRepository.save(newPost);
        assertThat(newPost).isNotNull();
        assertEquals(newPost.getDescription(), "New post");
    }

    @Test
    void shouldSavePostWithLongDescription() {
        Post post = new Post();
        post.setUser(user);
        String longDescription = TestData.getDescriptionWithLength(1000);
        post.setDescription(longDescription);
        Post newPost = postRepository.save(post);
        assertNotNull(newPost);
        assertEquals(longDescription, newPost.getDescription());
    }

    @Test
    void shouldNotSavePostWithLongDescription() {
        Post post = new Post();
        post.setDescription(TestData.getDescriptionWithLength(1001));

        assertThrows(TransactionSystemException.class, () -> postRepository.save(post));
    }

    @Test
    void shouldFindPostById() {
        var foundPost = postRepository.findById(post.getId());
        assertThat(foundPost).isPresent();
        assertEquals(post.getDescription(), foundPost.get().getDescription());
    }

    @Test
    void shouldNotFindPostByNonExistingId() {
        Optional<Post> foundPost = postRepository.findById(UUID.randomUUID());
        assertTrue(foundPost.isEmpty());
    }

    @Test
    void shouldUpdatePost() {
        post.setDescription("Updated post");
        post = postRepository.save(post);
        assertEquals(post.getDescription(), "Updated post");
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
        post.setDescription("Hello, Test User!");
        post.setUser(user);
        postRepository.save(post);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> posts = postRepository.findByUser(user, pageable);

        assertThat(posts).isNotEmpty();
        assertEquals(1, posts.getContent().size());
        assertEquals(user.getEmail(), posts.getContent().get(0).getUser().getEmail());
    }




}