package com.miras.post.service.impl;

import com.miras.post.exception.ResourceNotFoundException;
import com.miras.post.model.Post;
import com.miras.post.model.User;
import com.miras.post.repository.PostRepository;
import com.miras.post.repository.UserRepository;
import com.miras.post.service.PostService;
import com.miras.post.exception.ErrorMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Post createPost(Post post) {
         return postRepository.save(post);
    }

    @Override
    public Post editPost(UUID id, Post post) {
        Optional<Post> existingPost = postRepository.findById(id);
        if (existingPost.isPresent()) {
            Post _post = existingPost.get();
            _post.setContent(post.getContent());
            return postRepository.save(_post);
        } else {
            logger.error("{}, Post ID: {}", ErrorMessages.ERROR_POST_NOT_FOUND, id);
            throw new ResourceNotFoundException(ErrorMessages.ERROR_POST_NOT_FOUND);
        }
    }

    @Override
    public void deletePost(UUID id) {
        postRepository.deleteById(id);
    }

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
    public Slice<Post> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedDate").descending());
        return postRepository.findAllBy(pageable);
    }

    @Override
    public Page<Post> getAllUserPosts(UUID userId, int page, int size) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("modifiedDate").descending());
            return postRepository.findByUser(user.get(), pageable);
        } else {
            logger.error("{} User ID: {}", ErrorMessages.ERROR_USER_NOT_FOUND, userId);
            throw new ResourceNotFoundException(ErrorMessages.ERROR_USER_NOT_FOUND);
        }
    }

    // TODO: This is added only to generate test data in db. Needs to be removed.
    @Override
    public void generateData(int userCount, int perUserPostCount, int startIndex) {
        // Optional<User> user = userRepository.findById(userId);
        String description = """
                        In the depth of the forest, where sunlight filtered through the canopy in shimmering patches, a solitary fox prowled stealthily, its russet fur blending seamlessly with the dappled shadows. In its keen eyes gleamed the wisdom of ages, inherited from ancestors who had roamed these woods since time immemorial. Each step was deliberate, calculated, as it sought sustenance amidst the verdant undergrowth.
                                
                        Meanwhile, high above, a lone hawk soared on thermals, its wings spread wide as it rode the currents of the sky with effortless grace. From its vantage point, it surveyed the landscape below, keen eyesight spotting the slightest movement amidst the tapestry of green. With a sudden dive, it plummeted earthward, talons outstretched, aiming for its unsuspecting prey.
                """;
        String password = passwordEncoder.encode("test123");
        List<User> userList = new ArrayList<>();
        for (int i = startIndex; i < userCount + startIndex; i++) {
            User user = new User();
            user.setEmail( i + "test@gmail.com");
            user.setFirstName(i + "Miras");
            user.setLastName(i + "Mahamood");
            user.setPassword(password);
            userList.add(user);
        }
        List<User> savedUserList =  userRepository.saveAll(userList);
        for (User user: savedUserList) {
            List<Post> postList = new ArrayList<>();
            for (int i = 0; i < perUserPostCount; i++) {
                Post post = new Post();
                post.setContent(description);
                post.setUser(user);
                postList.add(post);
            }
            postRepository.saveAll(postList);
        }

    }
}
