package com.miras.post.controller;

import com.miras.post.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: This is added only to generate test data in db for testing.
@RestController
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/createData")
    public ResponseEntity<?> generateData(@RequestParam int userCount, @RequestParam int perUserPostCount, @RequestParam int startIndex) {
        dataService.generateData(userCount, perUserPostCount, startIndex);
        return ResponseEntity.ok(null);
    }
}
