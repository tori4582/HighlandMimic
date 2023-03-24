package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.Tag;
import edu.rmit.highlandmimic.model.request.TagRequestEntity;
import edu.rmit.highlandmimic.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository TagRepository;

    // READ operations

    public List<Tag> getAllTags() {
        return TagRepository.findAll();
    }

    public Tag getTagById(String id) {
        return TagRepository.findById(id).orElse(null);
    }

    public List<Tag> searchTagsByName(String nameQuery) {
        return TagRepository.getTagsByTagNameContains(nameQuery);
    }

    // WRITE operations

    public Tag createNewTag(TagRequestEntity reqEntity) {
        Tag preparedTag = Tag.builder()
                .build();

        return TagRepository.save(preparedTag);
    }

    // MODIFY operations

    public Tag updateExistingTag(String id, TagRequestEntity reqEntity) {

        Tag preparedTag = ofNullable(this.getTagById(id))
                .map(loadedEntity -> {
                    Tag TagObj = Tag.builder()
                            .build();


                    return TagObj;
                }).orElseThrow();

//        return TagRepository.update(preparedTag);
        return null;
    }

    public Tag updateFieldValueOfExistingTag(String id, String fieldName, Object newValue) {
        Tag preparedTag =  ofNullable(this.getTagById(id))
                .map(loadedEntity -> {
                    Tag TagObj = Tag.builder()
                            .build();


                    return TagObj;

                }).orElseThrow();

//        return TagRepository.update(preparedTag);
        return null;
    }


    // DELETE operations

    public Tag removeTagById(String id) {
        return TagRepository.findById(id)
                .map(loadedEntity -> {
                    TagRepository.delete(loadedEntity);
                    return loadedEntity;
                }).orElseThrow();
    }

    public long removeAllTags() {
        long quantity = TagRepository.count();
        TagRepository.deleteAll();
        return quantity;
    }

}