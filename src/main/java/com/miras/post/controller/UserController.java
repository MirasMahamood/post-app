package com.miras.post.controller;

import com.miras.post.model.User;
import com.miras.post.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "users", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @PageableDefault(sort = { "modifiedDate"}, direction = Sort.Direction.DESC, size = 20) Pageable pageable) {
        Page<User> pageUsers = userService.getAllUsers(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("users", pageUsers.getContent());
        response.put("currentPage", pageUsers.getNumber() + 1);
        response.put("totalItems", pageUsers.getTotalElements());
        response.put("totalPages", pageUsers.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> saveUser(@Valid @RequestBody User user) {
        userService.saveUser(user);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }
}
