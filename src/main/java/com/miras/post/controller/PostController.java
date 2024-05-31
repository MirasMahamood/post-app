package com.miras.post.controller;

import com.miras.post.model.Post;
import com.miras.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> editPost(@PathVariable UUID id, @Valid @RequestBody Post post) {
        return ResponseEntity.ok(postService.editPost(id, post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "50") int size) {
        Slice<Post> pagePosts = postService.getAllPosts(page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("posts", pagePosts.getContent());
        response.put("currentPage", pagePosts.getNumber());
        response.put("totalItems", pagePosts.getNumberOfElements());
        response.put("hasNext", pagePosts.hasNext());
        return ResponseEntity.ok(response);
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> getAllUserPosts(@PathVariable UUID userId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "50") int size) {
        Page<Post> pagePosts = postService.getAllUserPosts(userId, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("posts", pagePosts.getContent());
        response.put("currentPage", pagePosts.getNumber());
        response.put("totalItems", pagePosts.getTotalElements());
        response.put("totalPages", pagePosts.getTotalPages());
        return ResponseEntity.ok(response);
    }
}
