package edu.rmit.highlandmimic.service;

import edu.rmit.highlandmimic.model.Post;
import edu.rmit.highlandmimic.model.request.PostRequestEntity;
import edu.rmit.highlandmimic.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // READ operations

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(String id) {
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> searchPostsByName(String nameQuery) {
        return postRepository.getPostsByTitleContains(nameQuery);
    }

    // WRITE operations

    public Post createNewPost(PostRequestEntity reqEntity) {
        Post preparedPost = Post.builder()
                .build();

        return postRepository.save(preparedPost);
    }

    // MODIFY operations

    public Post updateExistingPost(String id, PostRequestEntity reqEntity) {

        Post preparedPost = ofNullable(this.getPostById(id))
                .map(loadedEntity -> {
                    Post PostObj = Post.builder()
                            .build();


                    return PostObj;
                }).orElseThrow();

//        return PostRepository.update(preparedPost);
        return null;
    }

    public Post updateFieldValueOfExistingPost(String id, String fieldName, Object newValue) {
        Post preparedPost =  ofNullable(this.getPostById(id))
                .map(loadedEntity -> {

                    return Post.builder().build();
                }).orElseThrow();

//        return PostRepository.update(preparedPost);
        return null;
    }

    // DELETE operations

    public Post removePostById(String id) {
        return postRepository.findById(id)
                .map(loadedEntity -> {
                    postRepository.delete(loadedEntity);
                    return loadedEntity;
                }).orElseThrow();
    }

    public long removeAllPosts() {
        long quantity = postRepository.count();
        postRepository.deleteAll();
        return quantity;
    }

}