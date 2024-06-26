package com.miras.post.service.impl;

import com.miras.post.exception.ResourceAlreadyExistsException;
import com.miras.post.model.User;
import com.miras.post.repository.UserRepository;
import com.miras.post.service.UserService;
import com.miras.post.exception.ErrorMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${spring.data.web.pageable.default-page-size}")
    int pageSize;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } else {
            throw new ResourceAlreadyExistsException(ErrorMessages.ERROR_USER_EXISTS);
        }
    }

    @Override
    public Page<User> getAllUsers(int page) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("modifiedDate").descending());
        return userRepository.findAll(pageable);
    }

}
