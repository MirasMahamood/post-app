package com.miras.post.service.impl;

import com.miras.post.model.Post;
import com.miras.post.model.User;
import com.miras.post.repository.PostRepository;
import com.miras.post.repository.UserRepository;
import com.miras.post.service.DataService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// TODO: This is added only to generate test data in db for testing.
@Service
public class DataServiceImpl implements DataService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataServiceImpl(PostRepository postRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void generateData(int userCount, int perUserPostCount, int startIndex) {
        String description = """
                In the depth of the forest, where sunlight filtered through the canopy in shimmering patches, a solitary fox prowled stealthily, its russet fur blending seamlessly with the dappled shadows. In its keen eyes gleamed the wisdom of ages, inherited from ancestors who had roamed these woods since time immemorial. Each step was deliberate, calculated, as it sought sustenance amidst the verdant undergrowth.
                Meanwhile, high above, a lone hawk soared on thermals, its wings spread wide as it rode the currents of the sky with effortless grace. From its vantage point, it surveyed the landscape below, keen eyesight spotting the slightest movement amidst the tapestry of green. With a sudden dive, it plummeted earthward, talons outstretched, aiming for its unsuspecting prey.
                 the heart of the ancient forest, sunlight filtered through the dense canopy, casting a mosaic of light and shadow on the forest floor. Birds chirped melodiously, while a gentle breeze whispered secrets among the towering trees.
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
                post.setDescription(description);
                post.setUser(user);
                postList.add(post);
            }
            postRepository.saveAll(postList);
        }

    }

}
