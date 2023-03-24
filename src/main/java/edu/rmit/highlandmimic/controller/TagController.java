package edu.rmit.highlandmimic.controller;

import edu.rmit.highlandmimic.model.Tag;
import edu.rmit.highlandmimic.model.request.TagRequestEntity;
import edu.rmit.highlandmimic.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.rmit.highlandmimic.common.ControllerUtils.controllerWrapper;

@RestController
@RequestMapping("/Tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService TagService;

    // READ operations

    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(TagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable String id) {
        return ResponseEntity.ok(TagService.getTagById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Tag>> searchTagsByName(@RequestParam String q) {
        return ResponseEntity.ok(TagService.searchTagsByName(q));
    }

    // WRITE operation

    @PostMapping
    public ResponseEntity<?> createNewTag(@RequestBody TagRequestEntity reqEntity) {
        return controllerWrapper(() -> TagService.createNewTag(reqEntity));
    }

    // MODIFY operation

    @PostMapping("/{id}")
    public ResponseEntity<?> updateExistingTag(@PathVariable String id, @RequestBody TagRequestEntity reqEntity) {
        return controllerWrapper(() -> TagService.updateExistingTag(id, reqEntity));
    }

    @PostMapping("/E/{id}/{field}")
    public ResponseEntity<?> updateFieldValueOfExistingTag(@PathVariable String id,
                                                          @PathVariable String fieldName,
                                                          @RequestBody Object newValue) {
        return controllerWrapper(() -> TagService.updateFieldValueOfExistingTag(id, fieldName, newValue));
    }

    // DELETE operation

    @DeleteMapping()
    public ResponseEntity<Long> removeAllTags() {
        return ResponseEntity.ok(TagService.removeAllTags());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeTagById(@PathVariable String id) {
        return controllerWrapper(() -> TagService.removeTagById(id));
    }

}