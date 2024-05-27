package com.miras.post.unit.repository;

import com.miras.post.model.User;
import com.miras.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PostRepositoryUnitTest {

    @Mock
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
}