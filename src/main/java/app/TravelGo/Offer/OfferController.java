package app.TravelGo.Offer;

import app.TravelGo.Post.Post;
import app.TravelGo.Post.PostService;
import app.TravelGo.Post.PostType;
import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.UserService;
import app.TravelGo.dto.CreatePostRequest;

import app.TravelGo.dto.GetPostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("api/offer")
public class OfferController {

    private final PostService postService;
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public OfferController(PostService postService, AuthService authService,
                           UserService userService) {
        this.postService = postService;
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<Void> createPost(@RequestBody CreatePostRequest request, UriComponentsBuilder builder) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .likes(0L)
                .username(this.authService.getCurrentUser().getUsername())
                .about(request.getAbout())
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .type(PostType.OFFER)
                .trip(null)
                .build();

        post = postService.createPost(post);

        return ResponseEntity.created(builder.pathSegment("api", "posts", "{id}")
                .buildAndExpand(post.getId()).toUri()).build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GetPostResponse>> getAllPosts() {
        List<Post> posts = postService.getPosts();

        Collections.sort(posts, Comparator.comparing(Post::getCreatedAt).reversed());
        List<GetPostResponse> postResponses = new ArrayList<>();

        for (Post post : posts) {
            if (post.getType() != PostType.OFFER) {
                continue;
            }

            GetPostResponse postResponse = GetPostResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .username(post.getUsername())
                    .userID(userService.getUserByUsername(post.getUsername()).get().getId())
                    .about(post.getAbout())
                    .createdAt(post.getCreatedAt())
                    .likes(post.getLikes())
                    .build();
            postResponses.add(postResponse);
        }

        return ResponseEntity.ok(postResponses);
    }


    @GetMapping("/{offer_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetPostResponse> getOffer(@PathVariable("offer_id") Long offerID) {
        Optional<Post> response = postService.getPost(offerID);

        if (response.isPresent()) {
            Post post = response.get();
            GetPostResponse postResponse = GetPostResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .username(post.getUsername())
                    .userID(userService.getUserByUsername(post.getUsername()).get().getId())
                    .about(post.getAbout())
                    .createdAt(post.getCreatedAt())
                    .likes(post.getLikes())
                    .build();
            return ResponseEntity.ok(postResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{offer_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteOffer(@PathVariable("offer_id") Long offerID) {
        String username = this.authService.getCurrentUser().getUsername();

        Long userID = this.authService.getCurrentUser().getId();
        String postOwnerUsername = postService.getPost(offerID).orElse(null).getUsername();

        if (username.equals(postOwnerUsername) || userService.hasRole(userID, "MODERATOR")) {
            boolean success = postService.deletePost(offerID);
            if (success) {
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.notFound().build();
    }

}
