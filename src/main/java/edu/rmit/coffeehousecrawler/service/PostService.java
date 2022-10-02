package edu.rmit.coffeehousecrawler.service;

import edu.rmit.coffeehousecrawler.model.Post;
import edu.rmit.coffeehousecrawler.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final String BASE_URL = "https://thecoffeehouse.com/blogs";
    private static final List<String> collectionNames = Arrays.asList(
        "teaholic", "blog", "coffeeholic"
    );

    private final PostRepository postRepository;

    public List<Post> getPosts() {
        return collectionNames.stream()
                .map(e -> postRepository.getPostsOfCollection(BASE_URL + "/" + e))
                .reduce(new ArrayList<>(), (acc, list) -> { acc.addAll(list); return acc; });
    }

    public List<Post> getPostsByCollectionName(String collectionName, Integer page) {
        if (!collectionNames.contains(collectionName)) {
            throw new IllegalArgumentException("Unavailable collection name");
        }

        final String URL = BASE_URL + "/" + collectionName;

        return (page == null || page == 0)
                ? postRepository.getPostsOfCollection(BASE_URL + "/" + collectionName)
                : postRepository.getPostsOnPage(BASE_URL + "/" + collectionName, page);
    }
}
