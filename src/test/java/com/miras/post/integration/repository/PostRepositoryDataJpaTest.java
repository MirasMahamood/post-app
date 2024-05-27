package com.miras.post.integration.repository;

import com.miras.post.TestData;
import com.miras.post.model.Post;
import com.miras.post.model.User;
import com.miras.post.repository.PostRepository;
import com.miras.post.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class PostRepositoryDataJpaTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        user = new User();
        user.setFirstName("Miras");
        user.setLastName("Mahamood");
        user.setPassword("password");
        user.setEmail("miras@gmail.com");
        user.setCreatedDate(now);
        user.setModifiedDate(now);
        user = userRepository.save(user);
        post = new Post();
        post.setUser(user);
        post.setContent("Hello, Miras!");
        post.setCreatedDate(now);
        post.setModifiedDate(now);
        postRepository.save(post);
    }

    @Test
    void shouldFindAllPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Post> posts = postRepository.findAllBy(pageable);
        assertThat(posts).isNotEmpty();
        assertEquals(2, posts.getContent().size());
    }

    @Test
    void shouldFindPostsByUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> posts = postRepository.findByUser(user, pageable);
        assertThat(posts).isNotEmpty();
        assertThat(posts.getContent().get(0).getUser()).isEqualTo(user);
    }

    @Test
    void shouldFindPostById() {
        var foundPost = postRepository.findById(post.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get()).isEqualTo(post);
    }

    @Test
    void shouldNotFindPostByNonExistingId() {
        UUID nonExistingId = UUID.randomUUID();
        var foundPost = postRepository.findById(nonExistingId);
        assertThat(foundPost).isNotPresent();
    }
}