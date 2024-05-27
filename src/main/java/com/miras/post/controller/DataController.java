package com.miras.post.controller;

import com.miras.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: This is added only to generate test data in db. Needs to be removed.
@RestController
public class DataController {

    private final PostService postService;

    public DataController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/createData/{userCount}/{perUserPostCount}")
    public ResponseEntity<?> generateData(@PathVariable int userCount, int perUserPostCount, int startIndex) {
        postService.generateData(userCount, perUserPostCount, startIndex);
        return ResponseEntity.ok(null);
    }
}
