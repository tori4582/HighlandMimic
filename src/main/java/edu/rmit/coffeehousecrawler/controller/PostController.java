package edu.rmit.coffeehousecrawler.controller;

import edu.rmit.coffeehousecrawler.model.Post;
import edu.rmit.coffeehousecrawler.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/")
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok(postService.getPosts());
    }

    @GetMapping("/{collectionName}/{page}")
    public ResponseEntity<?> getPostByCollectionName(@PathVariable String collectionName, @PathVariable Integer page) {
        try {
            return ResponseEntity.ok(postService.getPostsByCollectionName(collectionName, page));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{collection}")
    public ResponseEntity<?> getPostByCollectionName(@PathVariable String collection) {
        return this.getPostByCollectionName(collection, null);
    }

}
