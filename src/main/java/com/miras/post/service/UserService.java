package com.miras.post.service;

import com.miras.post.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    void saveUser(User user);

    Page<User> getAllUsers(int page);

}
