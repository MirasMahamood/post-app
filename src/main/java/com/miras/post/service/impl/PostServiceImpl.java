package com.miras.post.service.impl;

import com.miras.post.exception.ErrorMessages;
import com.miras.post.exception.NotAllowedToEditException;
import com.miras.post.exception.ResourceNotFoundException;
import com.miras.post.model.Post;
import com.miras.post.model.User;
import com.miras.post.repository.PostRepository;
import com.miras.post.repository.UserRepository;
import com.miras.post.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.*;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@EnableCaching
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RedisCacheManager redisCacheManager;

    @Value("${spring.data.web.pageable.default-page-size}")
    private int pageSize;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, RedisCacheManager redisCacheManager) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public Post createPost(Post post) {
        post.setUser(getLoggedInUser());
        post = postRepository.save(post);
        redisCacheManager.getCache("post").put(post.getId(), post);
        return post;
    }

    @CachePut(value = "post", key = "#id")
    @Override
    public Post editPost(UUID id, Post post) {
        Post _post = getPost(id);
        validatePostUserWithLoggedInUser(_post.getUser().getEmail());
        _post.setDescription(post.getDescription());
        return postRepository.save(_post);
    }

    @CacheEvict(value = "post", key = "#id")
    @Override
    public void deletePost(UUID id) {
        Post post = getPost(id);
        validatePostUserWithLoggedInUser(post.getUser().getEmail());
        postRepository.deleteById(id);
    }

    @Cacheable(value = "post", key = "#id")
    @Override
    public Post getPost(UUID id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            logger.error("{}, Post ID: {}", ErrorMessages.ERROR_POST_NOT_FOUND, id);
            throw new ResourceNotFoundException(ErrorMessages.ERROR_POST_NOT_FOUND);
        }
        return post.get();
    }

    @Override
    public Slice<Post> getAllPosts(int page) {
;        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("modifiedDate").descending());
        return postRepository.findAllBy(pageable);
    }

    @Override
    public Page<Post> getAllUserPosts(UUID userId, int page) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()) {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by("modifiedDate").descending());
            return postRepository.findByUser(user.get(), pageable);
        } else {
            logger.error("{} User ID: {}", ErrorMessages.ERROR_USER_NOT_FOUND, userId);
            throw new ResourceNotFoundException(ErrorMessages.ERROR_USER_NOT_FOUND);
        }
    }

    private String getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private User getLoggedInUser() {
        return userRepository.findByEmail(getLoggedInUserEmail()).
                orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ERROR_USER_NOT_FOUND));
    }

    private void validatePostUserWithLoggedInUser(String postUserEmail) {
        if (!postUserEmail.equals(getLoggedInUserEmail())) {
            logger.error("Logged in user is {}, and post user is {}", getLoggedInUserEmail(), postUserEmail);
            throw new NotAllowedToEditException(ErrorMessages.ERROR_POST_MODIFY_NOT_ALLOWED);
        }
    }
}
