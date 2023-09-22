package app.TravelGo.Post;

import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.UserService;
import app.TravelGo.dto.CreatePostRequest;
import app.TravelGo.dto.GetPostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("api/posts")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public PostController(PostService postService, UserService userService, AuthService authService) {
        this.postService = postService;
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Iterable<Post> getAllPosts() {
        return postService.getPosts();
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
                    .userId(post.getUser())
                    .likes(post.getLikes())
                    .build();
            return ResponseEntity.ok(postResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deletePost(@PathVariable("post_id") Long postId) {

        Long userId = this.authService.getCurrentUserId();
        Long postOwnerId = postService.getPost(postId).orElse(null).getUser();

        if (userId.equals(postOwnerId) || userService.hasRole(userId, "MODERATOR")) { // TODO: sprawdzić te "=="
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
                .user(this.authService.getCurrentUserId())
                .status(request.getStatus())
                .build();

        post = postService.createPost(post);

        return ResponseEntity.created(builder.pathSegment("api", "posts", "{id}")
                .buildAndExpand(post.getId()).toUri()).build();
    }

}
