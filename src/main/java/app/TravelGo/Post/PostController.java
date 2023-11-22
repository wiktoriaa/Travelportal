package app.TravelGo.Post;

import app.TravelGo.Comment.CommentService;
import app.TravelGo.File.FileService;
import app.TravelGo.Post.Like.LikeService;
import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.User;
import app.TravelGo.User.UserService;
import app.TravelGo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("api/posts")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final AuthService authService;
    private final LikeService likeService;
    private final FileService fileService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, UserService userService, AuthService authService,
                          LikeService likeService, FileService fileService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.authService = authService;
        this.likeService = likeService;
        this.fileService = fileService;
        this.commentService = commentService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GetPostResponse>> getAllPosts() {
        List<Post> posts = postService.getPosts();

        Collections.sort(posts, Comparator.comparing(Post::getCreatedAt).reversed());
        List<GetPostResponse> postResponses = new ArrayList<>();

        for (Post post : posts) {
            if (post.getType() != PostType.DISCUSSION) {
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
                    .imagesDir("/api/files/posts/" + post.getId())
                    .numberOfComments(commentService.countCommentsForPost(post.getId()))
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
                    .updatedAt(post.getUpdatedAt())
                    .likes(post.getLikes())
                    .imagesDir("/api/files/posts/" + post.getId())
                    .numberOfComments(commentService.countCommentsForPost(post.getId()))
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

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<Void> createPost(@RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam("about") String about,
                                    @RequestParam(value = "image", required = false) MultipartFile image,
                                    UriComponentsBuilder builder) throws IOException {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .likes(Long.getLong("0"))
                .username(this.authService.getCurrentUser().getUsername())
                .about(about)
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .type(PostType.DISCUSSION)
                .trip(null)
                .build();

        post = postService.createPost(post);

        if (image != null) {
            fileService.uploadFeaturePostImage(image, post.getId());
        }

        return ResponseEntity.created(builder.pathSegment("api", "posts", "{id}")
                .buildAndExpand(post.getId()).toUri()).build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<SimpleStringMessage> likePost(@PathVariable("postId") Long postId) {
        User currentUser = authService.getCurrentUser();
        Post likedPost = postService.getPost(postId).orElse(null);

        if (likeService.isPostLikedByUser(currentUser, likedPost)) {
            return ResponseEntity.ok(new SimpleStringMessage("Post already liked"));
        }
        else if (likedPost != null){
            likeService.likePost(currentUser, likedPost);
            likedPost.setLikes(likeService.getLikesCountForPost(likedPost));
            postService.updatePost(likedPost);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<String> unlikePost(@PathVariable("postId") Long postId) {
        User currentUser = authService.getCurrentUser();
        Post likedPost = postService.getPost(postId).orElse(null);

        likeService.unlikePost(currentUser, likedPost);
        likedPost.setLikes(likeService.getLikesCountForPost(likedPost));
        postService.updatePost(likedPost);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/likes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> getPostLikes(@PathVariable("postId") Long postId) {
        Post post = postService.getPost(postId).orElse(null);
        long likesCount = likeService.getLikesCountForPost(post);
        return ResponseEntity.ok(likesCount);
    }

    @PutMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updatePost(@PathVariable("post_id") Long postId, @RequestBody UpdatePostRequest updateRequest) {
        Optional<Post> postOptional = postService.getPost(postId);

        if (postOptional.isPresent()) {
            Post existingPost = postOptional.get();

            String username = authService.getCurrentUser().getUsername();
            Long userID = authService.getCurrentUser().getId();
            String postOwnerUsername = existingPost.getUsername();

            if (username.equals(postOwnerUsername) || userService.hasRole(userID, "MODERATOR")) {
                if (updateRequest.getTitle() != null) {
                    existingPost.setTitle(updateRequest.getTitle());
                }
                if (updateRequest.getContent() != null) {
                    existingPost.setContent(updateRequest.getContent());
                }
                if (updateRequest.getAbout() != null) {
                    existingPost.setAbout(updateRequest.getAbout());
                }
                existingPost.setUpdatedAt(LocalDateTime.now());

                postService.updatePost(existingPost);

                return ResponseEntity.ok("Post updated successfully");
            }
        }
        return ResponseEntity.notFound().build();
    }

}
