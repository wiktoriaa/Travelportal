package app.TravelGo.Comment;

import app.TravelGo.Post.Post;
import app.TravelGo.Post.PostService;
import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.UserService;
import app.TravelGo.dto.CreateCommentRequest;
import app.TravelGo.dto.GetCommentResponse;
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
public class CommentController {
    private final UserService userService;
    private final CommentService commentService;
    private final PostService postService;
    private final AuthService authService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService, PostService postService,
                             AuthService authService) {
        this.userService = userService;
        this.commentService = commentService;
        this.postService = postService;
        this.authService = authService;
    }

    @GetMapping("/{post_id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getCommentsByPostId(@PathVariable("post_id") Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<GetCommentResponse> commentResponses = new ArrayList<>();

        for (Comment comment : comments) {
            GetCommentResponse commentResponse = GetCommentResponse.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .username(comment.getUsername())
                    .userID(userService.getUserByUsername(comment.getUsername()).get().getId())
                    .post(comment.getPostId())
                    .build();
            commentResponses.add(commentResponse);
        }

        return ResponseEntity.ok(commentResponses);
    }

    @PostMapping("/{post_id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<Void> createComment(@PathVariable("post_id") Long postId, @RequestBody CreateCommentRequest request, UriComponentsBuilder builder) {
        Optional<Post> post = postService.getPost(postId);
        if (post.isPresent()) {
            Comment comment = Comment.builder()
                    .content(request.getContent())
                    .username(this.authService.getCurrentUser().getUsername())
                    .postId(post.get().getId())
                    .createdAt(LocalDateTime.now())
                    .build();

            commentService.createComment(comment);

            return ResponseEntity.created(builder.pathSegment("api", "posts", "{post_id}", "comments", "{comment_id}")
                    .buildAndExpand(postId, comment.getId()).toUri()).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{post_id}/comments/{comment_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getComment(@PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId) {
        Optional<Comment> response = commentService.getComment(commentId);
        if (response.isPresent() && response.get().getPostId().equals(postId)) {
            Comment comment = response.get();
            GetCommentResponse commentResponse = GetCommentResponse.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .username(comment.getUsername())
                    .userID(userService.getUserByUsername(comment.getUsername()).get().getId())
                    .post(comment.getPostId())
                    .build();
            return ResponseEntity.ok(commentResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{post_id}/comments/{comment_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteComment(@PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId) {
        Optional<Comment> commentResponse = commentService.getComment(commentId);
        if (commentResponse.isPresent() && commentResponse.get().getPostId().equals(postId)) {
            String username = this.authService.getCurrentUser().getUsername();
            String commentOwnerUsername = commentResponse.get().getUsername();

            if (username.equals(commentOwnerUsername) || userService.hasRole(this.authService.getCurrentUserId(), "MODERATOR")) {
                boolean success = commentService.deleteComment(commentId);
                if (success) {
                    return ResponseEntity.ok().build();
                }
            }
        }
        return ResponseEntity.notFound().build();
    }
}
