package edu.rmit.highlandmimic.controller;

import edu.rmit.highlandmimic.model.Post;
import edu.rmit.highlandmimic.model.request.PostRequestEntity;
import edu.rmit.highlandmimic.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.rmit.highlandmimic.common.ControllerUtils.controllerWrapper;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPostsByName(@RequestParam String q) {
        return ResponseEntity.ok(postService.searchPostsByName(q));
    }

    // WRITE operation

    @PostMapping
    public ResponseEntity<?> createNewPost(@RequestBody PostRequestEntity reqEntity) {
        return controllerWrapper(() -> postService.createNewPost(reqEntity));
    }

    // MODIFY operation

    @PostMapping("/{id}")
    public ResponseEntity<?> updateExistingPost(@PathVariable String id, @RequestBody PostRequestEntity reqEntity) {
        return controllerWrapper(() -> postService.updateExistingPost(id, reqEntity));
    }

    @PostMapping("/E/{id}/{field}")
    public ResponseEntity<?> updateFieldValueOfExistingPost(@PathVariable String id,
                                                          @PathVariable String fieldName,
                                                          @RequestBody Object newValue) {
        return controllerWrapper(() -> postService.updateFieldValueOfExistingPost(id, fieldName, newValue));
    }

    // DELETE operation

    @DeleteMapping()
    public ResponseEntity<Long> removeAllPosts() {
        return ResponseEntity.ok(postService.removeAllPosts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removePostById(@PathVariable String id) {
        return controllerWrapper(() -> postService.removePostById(id));
    }

}
