package app.TravelGo.Post;

import app.TravelGo.Post.Like.LikeService;
import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.User;
import app.TravelGo.User.UserService;
import app.TravelGo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/posts")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final AuthService authService;
    private final LikeService likeService;

    @Autowired
    public PostController(PostService postService, UserService userService, AuthService authService,
                          LikeService likeService) {
        this.postService = postService;
        this.userService = userService;
        this.authService = authService;
        this.likeService = likeService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GetPostResponse>> getAllPosts() {
        List<Post> posts = postService.getPosts();
        List<GetPostResponse> postResponses = new ArrayList<>();

        for (Post post : posts) {
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



    @GetMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getPost(@PathVariable("post_id") Long postId) {
        Optional<Post> response = postService.getPost(postId);
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

    @DeleteMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deletePost(@PathVariable("post_id") Long postId) {

        String username = this.authService.getCurrentUser().getUsername();
        Long userID = this.authService.getCurrentUser().getId();
        String postOwnerUsername = postService.getPost(postId).orElse(null).getUsername();

        if (username.equals(postOwnerUsername) || userService.hasRole(userID, "MODERATOR")) {
            boolean success = postService.deletePost(postId);
            if (success) {
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<Void> createPost(@RequestBody CreatePostRequest request, UriComponentsBuilder builder) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .likes(0)
                .username(this.authService.getCurrentUser().getUsername())
                .about(request.getAbout())
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .status(request.getStatus())
                .build();

        post = postService.createPost(post);

        return ResponseEntity.created(builder.pathSegment("api", "posts", "{id}")
                .buildAndExpand(post.getId()).toUri()).build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<SimpleStringMessage> likePost(@RequestBody LikeRequest request) {
        User currentUser = authService.getCurrentUser();
        Post likedPost = postService.getPost(request.getPostId()).orElse(null);

        if (likeService.isPostLikedByUser(currentUser, likedPost)) {
            return ResponseEntity.ok(new SimpleStringMessage("Post already liked"));
        }
        else if (likedPost != null){
            likeService.likePost(currentUser, likedPost);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<String> unlikePost(@RequestBody LikeRequest request) {
        User currentUser = authService.getCurrentUser();
        Post likedPost = postService.getPost(request.getPostId()).orElse(null);

        likeService.unlikePost(currentUser, likedPost);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/likes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> getPostLikes(@PathVariable("postId") Long postId) {
        Post post = postService.getPost(postId).orElse(null);
        long likesCount = likeService.getLikesCountForPost(post);
        return ResponseEntity.ok(likesCount);
    }
}
