package com.miras.post.unit.service;

import com.miras.post.exception.ResourceNotFoundException;
import com.miras.post.model.Post;
import com.miras.post.model.User;
import com.miras.post.repository.PostRepository;
import com.miras.post.repository.UserRepository;
import com.miras.post.service.impl.PostServiceImpl;
import com.miras.post.unit.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.data.domain.*;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionSystemException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceUnitTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisCacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private PostServiceImpl postService;

    private AutoCloseable closeable;


    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        Authentication auth = new UsernamePasswordAuthenticationToken(TestData.getTestUser().getEmail(),null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        ReflectionTestUtils.setField(postService, "pageSize", 20);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void createPostSuccessfully() {
        Post post = TestData.getTestPost();
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(post.getUser()));
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        Post result = postService.createPost(post);

        assertNotNull(result);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void createPostFailureWithLongDescription() {
        Post post = TestData.getTestPost();
        String longDescription = TestData.getDescriptionWithLength(1001);
        post.setDescription(longDescription);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(post.getUser()));
        when(postRepository.save(any(Post.class))).thenThrow(new TransactionSystemException("Description should not be more than 1000 characters"));

        assertThrows(TransactionSystemException.class, () -> postService.createPost(post));
    }

    @Test
    public void editPostSuccessfully() {
        Post post = TestData.getTestPost();
        UUID id = post.getId();
        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        Post result = postService.editPost(id, post);
        assertNotNull(result);
        verify(postRepository, times(1)).findById(id);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void editPostNotFound() {
        UUID id = UUID.randomUUID();
        Post post = new Post();
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.editPost(id, post));
        verify(postRepository, times(1)).findById(id);
        verify(postRepository, times(0)).save(post);
    }

    @Test
    public void deletePostSuccessfully() {
        UUID id = UUID.randomUUID();
        doNothing().when(postRepository).deleteById(id);
        when(postRepository.findById(id)).thenReturn(Optional.of(TestData.getTestPost()));

        postService.deletePost(id);

        verify(postRepository, times(1)).deleteById(id);
    }

    @Test
    public void getPostSuccessfully() {
        Post post = TestData.getTestPost();
        UUID id = post.getId();
        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        Post result = postService.getPost(id);

        assertNotNull(result);
        verify(postRepository, times(1)).findById(id);
        assertEquals(post, result);
    }

    @Test
    public void getPostNotFound() {
        UUID id = UUID.randomUUID();
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getPost(id));
        verify(postRepository, times(1)).findById(id);
    }

    @Test
    public void getAllPostsSuccessfully() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("modifiedDate").descending());
        when(postRepository.findAllBy(pageable)).thenReturn(TestData.getAllTestPosts());

        Slice<Post> result = postService.getAllPosts(0);

        assertNotNull(result);
        verify(postRepository, times(1)).findAllBy(pageable);
        assertTrue(result.hasContent());
        assertEquals(1, result.getContent().size());
        assertFalse(result.hasNext());
        assertEquals(result.getNumber(), 0);
    }

    @Test
    public void getAllPostsReturnsEmptyWhenNoPosts() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("modifiedDate").descending());
        when(postRepository.findAllBy(pageable)).thenReturn(new SliceImpl<>(new ArrayList<>()));

        Slice<Post> result = postService.getAllPosts(0);

        assertNotNull(result);
        verify(postRepository, times(1)).findAllBy(pageable);
        assertFalse(result.hasContent());
    }

    @Test
    public void getAllUserPostsSuccessfully() {
        int page = 0;
        User user = TestData.getTestUser();
        UUID userId = user.getId();
        Pageable pageable = PageRequest.of(page, 20, Sort.by("modifiedDate").descending());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findByUser(user, pageable)).thenReturn(TestData.getAllUserPosts());

        Page<Post> result = postService.getAllUserPosts(userId, page);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findByUser(user, pageable);
        assertTrue(result.hasContent());
        assertEquals(result.getContent().size(), 1);
        assertEquals(result.getTotalPages(), 1);
        assertEquals(result.getTotalElements(), 1);
        assertEquals(result.getNumber(), page);
    }

    @Test
    public void getAllUserPostsUserNotFound() {
        User user = TestData.getTestUser();
        UUID userId = user.getId();
        int page = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getAllUserPosts(userId, page));
        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(0)).findByUser(user, PageRequest.of(0, 1));
    }
}
