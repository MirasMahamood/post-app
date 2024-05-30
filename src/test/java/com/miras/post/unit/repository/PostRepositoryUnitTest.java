package com.miras.post.unit.repository;

import com.miras.post.model.Post;
import com.miras.post.model.User;
import com.miras.post.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PostRepositoryUnitTest {

    @Mock
    private PostRepository postRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    void shouldFindAllPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        postRepository.findAllBy(pageable);
        verify(postRepository, times(1)).findAllBy(pageable);
    }

    @Test
    void shouldFindPostsByUser() {
        User user = new User();
        Pageable pageable = PageRequest.of(0, 10);
        postRepository.findByUser(user, pageable);
        verify(postRepository, times(1)).findByUser(user, pageable);
    }

    @Test
    void shouldFindPostById() {
        UUID id = UUID.randomUUID();
        postRepository.findById(id);
        verify(postRepository, times(1)).findById(id);
    }

    @Test
    void shouldReturnEmptyWhenNoPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        when(postRepository.findAllBy(pageable)).
                thenReturn(new SliceImpl<>(Collections.emptyList(), pageable, false));
        Slice<Post> posts = postRepository.findAllBy(pageable);
        assertTrue(posts.isEmpty());
        verify(postRepository, times(1)).findAllBy(pageable);
    }

    @Test
    void shouldReturnEmptyWhenNoPostsForUser() {
        User user = new User();
        Pageable pageable = PageRequest.of(0, 10);
        when(postRepository.findByUser(user, pageable)).thenReturn(Page.empty());
        Page<Post> posts = postRepository.findByUser(user, pageable);
        assertTrue(posts.isEmpty());
        verify(postRepository, times(1)).findByUser(user, pageable);
    }

    @Test
    void shouldReturnEmptyWhenPostNotFoundById() {
        UUID id = UUID.randomUUID();
        when(postRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Post> post = postRepository.findById(id);
        assertTrue(post.isEmpty());
        verify(postRepository, times(1)).findById(id);
    }

}